package com.ivanandevs;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Utilities {

    public enum PatternLockMode {
        LOGIN(1, "Draw unlock pattern"),
        CHANGE_OLD(2, "Draw your old pattern"),
        CHANGE_NEW1(3, "Draw your new pattern"),
        CHANGE_NEW2(4, "Draw your new pattern again");

        public int id;
        public String message;

        PatternLockMode(int id, String message) {
            this.id = id;
            this.message = message;
        }
    }

    public enum Mod {
        All(1),
        Hide(2);

        public int id;

        Mod(int id) {
            this.id = id;
        }
    }

    public enum Sort {
        Name(1),
        Date(2),
        Size(2);

        public int id;

        Sort(int id) {
            this.id = id;
        }
    }

    public enum Order {
        DESC(1, "DESC"),
        ASC(2, "ASC");

        public int id;
        public String name;

        Order(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public enum Tab {
        Folder(1, "All Files", "Hidden Files"),
        Image(2, "All Images", "Hidden Images"),
        Video(3, "All Videos", "Hidden Videos"),
        Music(4, "All Musics", "Hidden Musics"),
        Document(5, "All Documents", "Hidden Documents");

        public int id;
        public String title;
        public String hideTitle;

        Tab(int id, String title, String hideTitle) {
            this.id = id;
            this.title = title;
            this.hideTitle = hideTitle;
        }
    }

    private static Point size;
    private static float density = 1;

    public static void init(Activity activity) {
        density = activity.getResources().getDisplayMetrics().density;
        Display display = activity.getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    public static int getScreenX() {
        return size.x;
    }

    public static int getScreenY() {
        return size.y;
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    public static float dpf2(float value) {
        if (value == 0) {
            return 0;
        }
        return density * value;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean makeFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            Utilities.broadCastPaths.add(file.getPath());
            return file.mkdir();
        } else {
            return true;
        }
    }

    public static boolean renameFile(File from, String name) {
        File to = new File(from.getParent(), name);
        if (from.exists()) {
            Utilities.broadCastPaths.add(to.getPath());
            Utilities.broadCastPaths.add(from.getPath());
            return from.renameTo(to);
        }
        return false;
    }

    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            File[] list = new File(file.getPath()).listFiles();
            if (list != null) {
                for (File item : list) {
                    boolean success = deleteFile(item);
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        boolean success = file.delete();
        Utilities.broadCastPaths.add(file.getPath());
        return success;
    }

    public static TypedFiles typedFiles = new TypedFiles();

    public interface LoadFileListener {
        void onLoad();
    }

    public static void loadFileList(FileManagerActivity activity, LoadFileListener listener) {
        if (activity.currentMod != Mod.Hide || !Utilities.typedFiles.needUpdate) {
            listener.onLoad();
        } else {
            Utilities.typedFiles.images.clear();
            Utilities.typedFiles.videos.clear();
            Utilities.typedFiles.musics.clear();
            Utilities.typedFiles.documents.clear();
            SuperThread task = new SuperThread(activity);
            task.setListener(new SuperThread.ESLThreadListener() {
                @Override
                public HashMap<String, Object> onProgress() {
                    updateTypedFiles(Utilities.getHidePath());
                    return new HashMap<>();
                }

                @Override
                public void onEnd(HashMap<String, Object> data) {
                    Utilities.typedFiles.needUpdate = false;
                    listener.onLoad();
                }

                private void updateTypedFiles(String path) {
                    File dir = new File(path);
                    File[] list = dir.listFiles();
                    if (list != null && list.length > 0) {
                        for (File file : list) {
                            if (file.isDirectory()) {
                                updateTypedFiles(file.getPath());
                            } else {
                                if (Utilities.isExtensionPhoto(file.getName())) {
                                    Utilities.typedFiles.images.add(Folder.getInstance(file, Folder.Type.File));
                                } else if (Utilities.isExtensionVideo(file.getName())) {
                                    Utilities.typedFiles.videos.add(Folder.getInstance(file, Folder.Type.File));
                                } else if (Utilities.isExtensionMusic(file.getName())) {
                                    Utilities.typedFiles.musics.add(Folder.getInstance(file, Folder.Type.File));
                                } else if (Utilities.isExtensionDocument(file.getName())) {
                                    Utilities.typedFiles.documents.add(Folder.getInstance(file, Folder.Type.File));
                                }
                            }
                        }
                    }
                }
            });
            task.execute();
        }
    }

    public static ArrayList<String> broadCastPaths = new ArrayList<>();

    public interface BroadCastListener {
        void onStart();
        void onDone();
    }

    public static void callBroadCast(BroadCastListener listener) {
        String[] stringArray = broadCastPaths.toArray(new String[0]);
        listener.onStart();
        if (stringArray.length > 0) {
            Utilities.broadCastPaths.clear();
            Utilities.typedFiles.needUpdate = true;
            MediaScannerConnection.scanFile(FileManagerActivity.context, stringArray, null, (MediaScannerConnection.OnScanCompletedListener) (path, uri) -> {
                listener.onDone();
            });
        } else {
            listener.onDone();
        }
    }

    public static void deleteEmptyFolders(File dir) {
        if (!dir.isDirectory()) {
            dir = dir.getParentFile();
            if (dir == null) {
                return;
            }
        }
        while (getFileCount(dir) == 0) {
            boolean success = deleteFile(dir);
            if (!success) {
                return;
            }
            dir = dir.getParentFile();
            if (dir == null || !dir.isDirectory()) {
                return;
            }
        }
    }

    public static int getFileCount(File dir) {
        if (dir.isDirectory()) {
            String[] list = dir.list();
            if (list != null) {
                return list.length;
            }
            return 0;
        }
        return -1;
    }

    public static boolean copyFile(String srcDir, String dstDir, String name, Crypto crypto) {
        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, name);
            if (src.isDirectory()) {
                String[] list = src.list();
                if (list != null) {
                    for (String file : list) {
                        File src1 = new File(src, file);
                        boolean success = copyFile(src1.getPath(), dst.getPath(), src1.getName(), crypto);
                        if (!success) {
                            Loge("wwwwww", "w1");
                            return false;
                        }
                    }
                }
            } else {
                if (dst.getParentFile() != null) {
                    if (!dst.getParentFile().exists()) {
                        boolean success = dst.getParentFile().mkdirs();
                        if (!success) {
                            Loge("wwwwww", "w2");
                            return false;
                        }
                    }
                } else {
                    Loge("wwwwww", "w3");
                    return false;
                }
                if (!dst.exists()) {
                    boolean success = dst.createNewFile();
                    if (!success) {
                        Loge("wwwwww", "w4");
                        return false;
                    }
                }
                FileChannel source = null;
                FileChannel destination = null;
                try {
                    source = new FileInputStream(src).getChannel();
                    destination = new FileOutputStream(dst).getChannel();
                    destination.transferFrom(source, 0, source.size());
                } catch (Exception e) {
                    Loge("wwwwww", "w5 " + e.getMessage());
                    return false;
                } finally {
                    if (source != null) {
                        source.close();
                    }
                    if (destination != null) {
                        destination.close();
                    }
                }
            }
        } catch (Exception e) {
            Loge("wwwwww", "w6 " + e.getMessage());
            return false;
        }
        File file = new File(dstDir, name);
        if (file.isDirectory()) {
            Loge("wwwwww", "Directory " + file.getPath());
        } else {
            if (crypto == Crypto.Encrypt) {
                Loge("wwwwww", "Encrypt " + file.getPath());
                return encrypt(file);
            } else if (crypto == Crypto.Decrypt) {
                Loge("wwwwww", "Decrypt " + file.getPath());
                return decrypt(file);
            } else {
                Loge("wwwwww", "None " + file.getPath());
            }
        }
        return true;
    }

    public enum Crypto {
        None,
        Encrypt,
        Decrypt;
    }

    public static boolean encrypt(File file) {
        try {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                for (int i = 0; i < 1024 && i < raf.length(); i = i + 128) {
                    raf.seek(i);
                    int value = raf.read();
                    value = 255 - value;
                    raf.seek(i);
                    raf.write(value);
                }

                byte[] bytes = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(bytes);
                fis.close();

                for (int i = 0; i < bytes.length / 2; i++) {
                    byte temp = bytes[i];
                    bytes[i] = (byte) - bytes[bytes.length - i - 1];
                    bytes[bytes.length - i - 1] = (byte) - temp;
                }

                FileOutputStream fos = new FileOutputStream(file.getPath());
                fos.write(bytes);
                fos.close();

                return true;
            }
        } catch (IOException e) {
            Utilities.Loge("zipzipzipzip5", e.getMessage());
            return false;
        }
    }

    public static boolean decrypt(File file) {
        try {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                byte[] bytes = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(bytes);
                fis.close();

                for (int i = 0; i < bytes.length / 2; i++) {
                    byte temp = bytes[i];
                    bytes[i] = (byte) - bytes[bytes.length - i - 1];
                    bytes[bytes.length - i - 1] = (byte) - temp;
                }

                FileOutputStream fos = new FileOutputStream(file.getPath());
                fos.write(bytes);
                fos.close();

                for (int i = 0; i < 1024 && i < raf.length(); i = i + 128) {
                    raf.seek(i);
                    int value = raf.read();
                    value = 255 - value;
                    raf.seek(i);
                    raf.write(value);
                }
                return true;
            }
        } catch (IOException e) {
            Utilities.Loge("zipzipzipzip6", e.getMessage());
            return false;
        }
    }

    public static String getAllPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getHidePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/.HIDEit";
    }

    public static String getTempPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/.HIDEit/.temp";
        //return "/data/data/" + AppLoader.getContext().getPackageName() + "/temp";
    }

    public static Spannable formatPath(String path) {
        String format = path;
        format = format.replace(Utilities.getHidePath(), "Hidden");
        format = format.replace(Utilities.getAllPath(), "All");
        if (format.endsWith("/")) {
            format = format.substring(0, format.length() - 1);
        }
        if (format.equals("All")) {
            format = "All Files";
        }
        if (format.equals("Hidden")) {
            format = "Hidden Files";
        }
        format = format.replaceAll("/", " » ");
        Spannable span = new SpannableString(format);
        for (int i = 0; i < format.length(); i++) {
            if (format.charAt(i) == '»') {
                int color = Utilities.getColor(R.color.colorGrayLight);
                span.setSpan(new ForegroundColorSpan(color), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return span;
    }

    public static String formatFileSize(long value) {
        if (value < 1024) {
            return getString(R.string.SizeB, Utilities.toB(value));
        } else if (value < 1024 * 1024) {
            return getString(R.string.SizeKB, Utilities.toKB(value));
        } else if (value < 1024 * 1024 * 1024) {
            return getString(R.string.SizeMB, Utilities.toMB(value));
        } else {
            return getString(R.string.SizeGB, Utilities.toGB(value));
        }
    }

    public static String toB(long value) {
        DecimalFormat dec = new DecimalFormat("0", new DecimalFormatSymbols(Locale.US));
        return dec.format((float) value);
    }

    public static String toKB(long value) {
        DecimalFormat dec = new DecimalFormat("0", new DecimalFormatSymbols(Locale.US));
        return dec.format((float) value/1024);
    }

    public static String toMB(long value) {
        DecimalFormat dec = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US));
        return dec.format((float) value/1024/1024);
    }

    public static String toGB(long value) {
        DecimalFormat dec = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US));
        return dec.format((float) value/1024/1024/1024);
    }

    public static String getString(@StringRes int res, Object... objects) {
        return String.format(Locale.US, FileManagerActivity.context.getResources().getString(res), objects);
    }

    public static Drawable getDrawable(@DrawableRes int res) {
        return FileManagerActivity.context.getResources().getDrawable(res);
    }

    public static int getColor(@ColorRes int res) {
        return FileManagerActivity.context.getResources().getColor(res);
    }

    public static boolean isNightTheme() {
        return Utilities.getColor(R.color.colorBlack) != Color.BLACK;
    }

    public static Typeface getTypeface(Context context, FontType fontType) {
        return Typeface.createFromAsset(context.getAssets(), fontType.path);
    }

    public static void Loge(String title, String message) {
        Log.e("xxxxxxx-" + title, message);
    }

    public enum FontType {
        Light("fonts/normal.ttf"),
        Normal("fonts/normal.ttf"),
        Bold("fonts/bold.ttf");

        public String path;

        FontType(String path) {
            this.path = path;
        }
    }

    private static ConcurrentHashMap<String, Bitmap> bitmaps = new ConcurrentHashMap<>();

    public static Bitmap getThumbnailPhoto(Context context, File file) {
        Bitmap bitmap = bitmaps.get(file.getPath());
        if (bitmap != null) {
            return bitmap;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            bitmap = ThumbnailUtils.createImageThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            if (bitmap != null) {
                bitmaps.put(file.getPath(), bitmap);
                return bitmap;
            }
        }
        Bitmap source = BitmapFactory.decodeFile(file.getPath());
        bitmap = ThumbnailUtils.extractThumbnail(source, 150, 150);
        return bitmap;
    }

    public static Bitmap getThumbnailVideo(File file) {
        Bitmap bitmap = bitmaps.get(file.getPath());
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        if (bitmap != null) {
            bitmaps.put(file.getPath(), bitmap);
        }
        return bitmap;
    }

    public static Bitmap getThumbnailApp(Context context, File file) {
        Bitmap bitmap = bitmaps.get(file.getPath());
        if (bitmap != null) {
            return bitmap;
        }
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(file.getPath(), 0);
        if (packageInfo != null && packageInfo.applicationInfo != null) {
            packageInfo.applicationInfo.sourceDir = file.getPath();
            packageInfo.applicationInfo.publicSourceDir = file.getPath();
            Drawable drawable = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
            bitmap = getBitmap(drawable);
            if (bitmap != null) {
                bitmaps.put(file.getPath(), bitmap);
            }
            return bitmap;
        }
        return null;
    }

    public static Bitmap getBitmap(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static boolean isExtensionDocument(String name) {
        String[] array = new String[]{".pdf", ".txt", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(array));
        for (int i = 0; i < arrayList.size(); i++) {
            if (name.toLowerCase().endsWith(arrayList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExtensionVideo(String name) {
        String[] array = new String[]{".mkv", ".mp4", ".flv", ".avi", ".mov", ".qt", ".ts", ".wmv", ".m4v", ".vob"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(array));
        for (int i = 0; i < arrayList.size(); i++) {
            if (name.toLowerCase().endsWith(arrayList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExtensionMusic(String name) {
        String[] array = new String[]{".mp3", ".ogg", ".wav"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(array));
        for (int i = 0; i < arrayList.size(); i++) {
            if (name.toLowerCase().endsWith(arrayList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExtensionPhoto(String name) {
        String[] array = new String[]{".jpg", ".jpeg", ".png", ".webp"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(array));
        for (int i = 0; i < arrayList.size(); i++) {
            if (name.toLowerCase().endsWith(arrayList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExtensionWord(String name) {
        String[] array = new String[]{".doc", ".docx"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(array));
        for (int i = 0; i < arrayList.size(); i++) {
            if (name.toLowerCase().endsWith(arrayList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExtensionPowerpoint(String name) {
        String[] array = new String[]{".ppt", ".pptx"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(array));
        for (int i = 0; i < arrayList.size(); i++) {
            if (name.toLowerCase().endsWith(arrayList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExtensionExcel(String name) {
        String[] array = new String[]{".xls", ".xlsx"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(array));
        for (int i = 0; i < arrayList.size(); i++) {
            if (name.toLowerCase().endsWith(arrayList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExtensionZip(String name) {
        String[] array = new String[]{".zip", ".rar"};
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(array));
        for (int i = 0; i < arrayList.size(); i++) {
            if (name.toLowerCase().endsWith(arrayList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2f;
        } else {
            r = bitmap.getWidth() / 2f;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static void runOnUi(Activity activity, Runnable runnable) {
        activity.runOnUiThread(runnable);
    }

    public static void runOnUi(Activity activity, Runnable runnable, long delay) {
        new Handler().postDelayed(() -> runOnUi(activity, runnable), delay);
    }

    public static long getFolderSize(File dir) {
        long length = 0;
        if (dir.isDirectory()) {
            File[] list = dir.listFiles();
            if (list != null) {
                for (File file : list) {
                    length += getFolderSize(file);
                }
            }
        } else {
            length += dir.length();
        }
        return length;
    }

    public static int getRandomNumber(int min, int max) {
        return new Random().nextInt((max- min) + 1) + min;
    }

    public static void setPatternLock(Context context, ArrayList<Integer> list) {
        SharedPreferences settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        String patternStr = list.toString();
        patternStr = patternStr.replaceAll("[\\[\\]]", "");
        patternStr = patternStr.replaceAll(" ", "");
        settings.edit().putString("PatternLock", patternStr).apply();
    }

    public static ArrayList<Integer> getPatternLock(Context context) {
        SharedPreferences settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
        String patternStr = settings.getString("PatternLock", "");
        if (patternStr.equals("")) {
            return new ArrayList<>();
        }
        ArrayList<Integer> pattern = new ArrayList<>();
        patternStr = patternStr.replaceAll("[\\[\\]]", "");
        patternStr = patternStr.replaceAll(" ", "");
        String[] corrects = patternStr.split(",");
        for (String correct : corrects) {
            pattern.add(Integer.parseInt(correct));
        }
        return pattern;
    }

    public static boolean checkPattern(ArrayList<Integer> pattern1, ArrayList<Integer> pattern2) {
        if (pattern1.size() != pattern2.size()) {
            return false;
        }
        for (int i = 0; i < pattern1.size() - 1; i++) {
            if (pattern1.get(i) != pattern2.get(i)) {
                return false;
            }
        }
        return true;
    }
}
