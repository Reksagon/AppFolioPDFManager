package com.ivanandevs.appfoliopdf.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

/**
 * !! IMPORTANT !!
 * permission arrays are defined in Constants.java file. we have two types of permissions:
 * READ_WRITE_PERMISSIONS and READ_WRITE_CAMERA_PERMISSIONS 
 * use these constants in project whenever required.
 */
public class PermissionsUtils {

    private static class SingletonHolder {
        static final PermissionsUtils INSTANCE = new PermissionsUtils();
    }

    public static PermissionsUtils getInstance() {
        return PermissionsUtils.SingletonHolder.INSTANCE;
    }

    public boolean checkRuntimePermissions(Object context, String[] permissions) {
        for (String permission : permissions) {
            if ((ContextCompat.checkSelfPermission(retrieveContext(context),
                    permission)
                    != PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }


    public void requestRuntimePermissions(Object context, String[] permissions,
                                                 int requestCode) {
        if (context instanceof Activity) {
            ActivityCompat.requestPermissions((AppCompatActivity) context,
                    permissions, requestCode);
        } else if (context instanceof Fragment) {
            ((Fragment) context).requestPermissions(permissions, requestCode);
        }
    }


    private Context retrieveContext(@NonNull Object context) {
        if (context instanceof AppCompatActivity) {
            return ((AppCompatActivity) context).getApplicationContext();
        } else {
            return ((Fragment) context).requireActivity();
        }
    }
}
