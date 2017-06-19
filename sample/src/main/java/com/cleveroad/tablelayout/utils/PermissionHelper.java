package com.cleveroad.tablelayout.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

public class PermissionHelper {
    private PermissionHelper() {

    }

    /**
     * @return true if all permissions is granted
     */
    public static boolean check(@NonNull Context context, String... permissions) {
        boolean result = true;
        for (String permission : permissions) {
            result = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
            if (!result) {
                break;
            }
        }
        return result;
    }

    /**
     * @return true if all permissions is granted
     */
    public static boolean checkOrRequest(@NonNull Activity activity, int requestCode, String... permissions) {
        if (!check(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
            return false;
        } else return true;
    }

    /**
     * @return true if all permissions is granted
     */
    public static boolean checkOrRequest(@NonNull Fragment fragment, int requestCode, String... permissions) {
        if (!check(fragment.getContext(), permissions)) {
            fragment.requestPermissions(permissions, requestCode);
            return false;
        } else return true;
    }
}
