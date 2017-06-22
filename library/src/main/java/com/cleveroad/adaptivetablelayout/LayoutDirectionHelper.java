package com.cleveroad.adaptivetablelayout;

import android.os.Build;
import android.view.View;

/**
 * Helper for convenient work with layout direction with the version API below 17
 */
class LayoutDirectionHelper {

    static int getLayoutDirection(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return view.getLayoutDirection();
        } else {
            return LayoutDirection.LTR;
        }
    }

    static boolean isRTL(View view) {
        return getLayoutDirection(view) == LayoutDirection.RTL;
    }

}
