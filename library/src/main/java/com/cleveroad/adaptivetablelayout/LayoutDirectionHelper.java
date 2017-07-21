package com.cleveroad.adaptivetablelayout;

/**
 * Helper for convenient work with layout direction with the version API below 17
 */
class LayoutDirectionHelper {
    private static int mLayoutDirection;

    LayoutDirectionHelper(int direction) {
        mLayoutDirection = direction;
    }

    static int getLayoutDirection() {
            return mLayoutDirection;
    }

    static void setLayoutDirection(int direction) {
        mLayoutDirection = direction;
    }

    static boolean isRTL() {
        return getLayoutDirection() == LayoutDirection.RTL;
    }

}
