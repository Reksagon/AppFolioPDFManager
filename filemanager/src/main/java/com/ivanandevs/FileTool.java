package com.ivanandevs;

import android.app.ActivityManager;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.StrictMode;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import com.ivanandevs.adapter.AppsBean;
import com.ivanandevs.adapter.CleanFilesBean;
import com.ivanandevs.adapter.SystemMode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileTool {
    private static final String[][] MATCH_ARRAY = {new String[]{".3gp", "video/3gpp"}, new String[]{".apk", "application/vnd.android.package-archive"}, new String[]{".asf", "video/x-ms-asf"}, new String[]{".avi", "video/x-msvideo"}, new String[]{".bin", "application/octet-stream"}, new String[]{".bmp", "image/bmp"}, new String[]{".c", "text/plain"}, new String[]{".class", "application/octet-stream"}, new String[]{".conf", "text/plain"}, new String[]{".cpp", "text/plain"}, new String[]{".doc", "application/msword"}, new String[]{".docx", "application/msword"}, new String[]{".xls", "application/msword"}, new String[]{".xlsx", "application/msword"}, new String[]{".exe", "application/octet-stream"}, new String[]{".gif", "image/gif"}, new String[]{".gtar", "application/x-gtar"}, new String[]{".gz", "application/x-gzip"}, new String[]{".h", "text/plain"}, new String[]{".htm", "text/html"}, new String[]{".html", "text/html"}, new String[]{".jar", "application/java-archive"}, new String[]{".java", "text/plain"}, new String[]{".jpeg", "image/jpeg"}, new String[]{".jpg", "image/jpeg"}, new String[]{".js", "application/x-javascript"}, new String[]{".log", "text/plain"}, new String[]{".m3u", "audio/x-mpegurl"}, new String[]{".m4a", "audio/mp4a-latm"}, new String[]{".m4b", "audio/mp4a-latm"}, new String[]{".m4p", "audio/mp4a-latm"}, new String[]{".m4u", "video/vnd.mpegurl"}, new String[]{".m4v", "video/x-m4v"}, new String[]{".mov", "video/quicktime"}, new String[]{".mp2", "audio/x-mpeg"}, new String[]{".mp3", "audio/x-mpeg"}, new String[]{".mp4", "video/mp4"}, new String[]{".mpc", "application/vnd.mpohun.certificate"}, new String[]{".mpe", "video/mpeg"}, new String[]{".mpeg", "video/mpeg"}, new String[]{".mpg", "video/mpeg"}, new String[]{".mpg4", "video/mp4"}, new String[]{".mpga", "audio/mpeg"}, new String[]{".msg", "application/vnd.ms-outlook"}, new String[]{".ogg", "audio/ogg"}, new String[]{".pdf", "application/pdf"}, new String[]{".png", "image/png"}, new String[]{".pps", "application/vnd.ms-powerpoint"}, new String[]{".ppt", "application/vnd.ms-powerpoint"}, new String[]{".prop", "text/plain"}, new String[]{".rar", "application/x-rar-compressed"}, new String[]{".rc", "text/plain"}, new String[]{".rmvb", "audio/x-pn-realaudio"}, new String[]{".rtf", "application/rtf"}, new String[]{".sh", "text/plain"}, new String[]{".tar", "application/x-tar"}, new String[]{".tgz", "application/x-compressed"}, new String[]{".txt", "text/plain"}, new String[]{".wav", "audio/x-wav"}, new String[]{".wma", "audio/x-ms-wma"}, new String[]{".wmv", "audio/x-ms-wmv"}, new String[]{".wps", "application/vnd.ms-works"}, new String[]{".xml", "text/plain"}, new String[]{".z", "application/x-compress"}, new String[]{".zip", "application/zip"}, new String[]{"", "*/*"}};
    private static final String TAG = "OKFILE_StorageUtil";

    public static SystemMode getSpace() {
        SystemMode systemMode = new SystemMode();
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (Build.VERSION.SDK_INT >= 18) {
            systemMode.setRomTotalSize(statFs.getTotalBytes());
            systemMode.setRomUsableSize(statFs.getFreeBytes());
        }
        return systemMode;
    }

    public static SystemMode getSystemStorageUseage(Context context) {
        char c;
        long j;
        long j2;
        long j3;
        Context context2 = context;
        SystemMode systemMode = new SystemMode();
        StorageManager storageManager = (StorageManager) context2.getSystemService("storage");
        int i = 1;
        if (Build.VERSION.SDK_INT < 23) {
            try {
                StorageVolume[] storageVolumeArr = (StorageVolume[]) StorageManager.class.getDeclaredMethod("getVolumeList", new Class[0]).invoke(storageManager, new Object[0]);
                if (storageVolumeArr != null) {
                    Method method = null;
                    j3 = 0;
                    j2 = 0;
                    for (StorageVolume storageVolume : storageVolumeArr) {
                        if (method == null) {
                            method = storageVolume.getClass().getDeclaredMethod("getPathFile", new Class[0]);
                        }
                        File file = (File) method.invoke(storageVolume, new Object[0]);
                        j3 += file.getTotalSpace();
                        j2 += file.getUsableSpace();
                    }
                } else {
                    j3 = 0;
                    j2 = 0;
                }
                systemMode.setRomTotalSize(j3);
                systemMode.setRomUsableSize(j2);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            try {
                long j4 = 0;
                long j5 = 0;
                for (Object next : (List) StorageManager.class.getDeclaredMethod("getVolumes", new Class[0]).invoke(storageManager, new Object[0])) {
                    int i2 = next.getClass().getField("type").getInt(next);
                    if (i2 == i) {
                        if (Build.VERSION.SDK_INT >= 26) {
                            j = getTotalSize(context2, (String) next.getClass().getDeclaredMethod("getFsUuid", new Class[0]).invoke(next, new Object[0]));
                        } else {
                            j = Build.VERSION.SDK_INT >= 25 ? ((Long) StorageManager.class.getMethod("getPrimaryStorageSize", new Class[0]).invoke(storageManager, new Object[0])).longValue() : 0;
                        }
                        if (((Boolean) next.getClass().getDeclaredMethod("isMountedReadable", new Class[0]).invoke(next, new Object[0])).booleanValue()) {
                            File file2 = (File) next.getClass().getDeclaredMethod("getPath", new Class[0]).invoke(next, new Object[0]);
                            if (j == 0) {
                                j = file2.getTotalSpace();
                            }
                            file2.getTotalSpace();
                            j5 += j - file2.getFreeSpace();
                            j4 += j;
                        }
                        systemMode.setRomTotalSize(j);
                        systemMode.setRomUsableSize(j - j5);
                    } else if (i2 == 0 && ((Boolean) next.getClass().getDeclaredMethod("isMountedReadable", new Class[0]).invoke(next, new Object[0])).booleanValue()) {
                        File file3 = (File) next.getClass().getDeclaredMethod("getPath", new Class[0]).invoke(next, new Object[0]);
                        j5 += file3.getTotalSpace() - file3.getFreeSpace();
                        j4 += file3.getTotalSpace();
                    }
                    i = 1;
                }
                systemMode.setRomTotalSize(j4);
                systemMode.setRomUsableSize(j4 - j5);
            } catch (SecurityException unused) {
                c = 1;
                Log.e(TAG, "less permission：permission.PACKAGE_USAGE_STATS");
            } catch (Exception e2) {
                e2.printStackTrace();
                long[] queryWidthStatFs = queryWidthStatFs();
                systemMode.setRomTotalSize(queryWidthStatFs[0]);
                c = 1;
                systemMode.setRomUsableSize(queryWidthStatFs[1]);
            }
        }
        c = 1;
        long[] ramMemory = getRamMemory(context);
        systemMode.setRamTotalSize(ramMemory[0]);
        systemMode.setRamUsableSize(ramMemory[c]);
        return systemMode;
    }

    private static long[] queryWidthStatFs() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize = (long) statFs.getBlockSize();
        return new long[]{(((long) statFs.getBlockCount()) * blockSize) + (((long) statFs.getFreeBlocks()) * blockSize), blockSize * ((long) statFs.getAvailableBlocks())};
    }


    private static long getTotalSize(Context context, String str) throws IOException {
        UUID uuid;
        if (str == null) {
            try {
                uuid = StorageManager.UUID_DEFAULT;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        } else {
            uuid = UUID.fromString(str);
        }
        return ((StorageStatsManager) context.getSystemService(StorageStatsManager.class)).getTotalBytes(uuid);


    }


    private static long[] getRamMemory(Context r16) {


        ActivityManager activityManager = (ActivityManager) r16.getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        activityManager.getMemoryInfo(memoryInfo);

        long totalMemory = memoryInfo.totalMem;
        long uasable = memoryInfo.availMem;
        long[] tttt = new long[2];

        tttt[0] = totalMemory;
        tttt[1] = uasable;

        return tttt;

    }

    public static List<CleanFilesBean> getFiles(String str) {
        File[] listFiles = new File(str).listFiles();
        ArrayList arrayList = new ArrayList();
        if (listFiles != null) {
            for (File path : listFiles) {
                arrayList.add(new CleanFilesBean(path.getPath(), 0));
            }
        }
        return arrayList;
    }

    public static boolean checkIsImageFile(String str) {
        String lowerCase = str.substring(str.lastIndexOf(".") + 1, str.length()).toLowerCase();
        if (lowerCase.equals("jpg") || lowerCase.equals("png") || lowerCase.equals("gif") || lowerCase.equals("jpeg") || lowerCase.equals("bmp")) {
            return true;
        }
        return false;
    }

    public static Drawable getAppIcon(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 0).applicationInfo.loadIcon(context.getPackageManager());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getFileType(String str) {
        String[] split = str.split("\\.");
        if (split[split.length - 1].equals("txt") || split[split.length - 1].equals("xls") || split[split.length - 1].equals("docx") || split[split.length - 1].equals("xlsx")) {
            return 1;
        }
        if (split[split.length - 1].equals("zip") || split[split.length - 1].equals("rar")) {
            return 2;
        }
        if (split[split.length - 1].equals("apk")) {
            return 3;
        }
        if (split[split.length - 1].equals("mp3") || split[split.length - 1].equals("mp4") || split[split.length - 1].equals("ogg")) {
            return 4;
        }
        return (split[split.length - 1].equals("png") || split[split.length - 1].equals("jpg") || split[split.length - 1].equals("gif") || split[split.length - 1].equals("jpeg") || split[split.length - 1].equals("bmp")) ? 5 : 6;
    }

    public static void openFileByPath(Context context, String str) {
        String str2;
        Uri uri;
        if (context != null && str != null) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(268435456);
            intent.addCategory("android.intent.category.DEFAULT");
            int i = 0;
            while (true) {
                String[][] strArr = MATCH_ARRAY;
                if (i >= strArr.length) {
                    str2 = "";
                    break;
                } else if (str.contains(strArr[i][0])) {
                    str2 = strArr[i][1];
                    break;
                } else {
                    i++;
                }
            }
            try {
                File file = new File(str);
                if (Build.VERSION.SDK_INT >= 24) {
                    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
                    uri = Uri.fromFile(file);
                } else {
                    uri = Uri.fromFile(file);
                }
                intent.setDataAndType(uri, str2);
                if (context.getPackageManager().resolveActivity(intent, 65536) != null) {
                    context.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<AppsBean> getAppList(Context context) {

        try {
            String packageName = context.getPackageName();
//            List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
            List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);

            ArrayList arrayList = new ArrayList();

            Log.e("size", "" + installedPackages.size());
            for (PackageInfo next : installedPackages) {
                if (!next.packageName.equals(packageName)) {
                    arrayList.add(new AppsBean(next.packageName, isSystemApp(next), next.lastUpdateTime, next.applicationInfo));
                }
            }
            return arrayList;


        } catch (Exception e) {

            Log.e("ffff", "" + e.getMessage());

        }
        return null;

    }

    public static Drawable getAppIcon(ApplicationInfo applicationInfo, Context context) {
        return context.getPackageManager().getApplicationIcon(applicationInfo);
    }

    public static String getAppName(ApplicationInfo applicationInfo, Context context) {
        return context.getPackageManager().getApplicationLabel(applicationInfo).toString();
    }

    private static boolean isSystemApp(PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & 1) != 0;
    }
}
