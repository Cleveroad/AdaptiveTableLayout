package com.cleveroad.adaptivetablelayout;

/**
 * Helper for convenient work with layout direction
 */
class LayoutDirectionHelper {
    private int mLayoutDirection;

    LayoutDirectionHelper(int direction) {
        mLayoutDirection = direction;
    }

    private int getLayoutDirection() {
            return mLayoutDirection;
    }

    void setLayoutDirection(int direction) {
        mLayoutDirection = direction;
    }

    boolean isRTL() {
        return getLayoutDirection() == LayoutDirection.RTL;
    }

}
