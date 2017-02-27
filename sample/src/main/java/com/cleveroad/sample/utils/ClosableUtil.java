package com.cleveroad.sample.utils;

import android.util.Log;

import java.io.Closeable;

public class ClosableUtil {
    private static final String TAG = ClosableUtil.class.getSimpleName();

    private ClosableUtil() {

    }

    public static void closeWithoutException(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
