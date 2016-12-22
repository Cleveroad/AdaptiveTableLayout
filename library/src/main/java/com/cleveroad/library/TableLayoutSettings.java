package com.cleveroad.library;

/**
 * Settings keeper class.
 */
class TableLayoutSettings {

    /**
     * Layout width
     */
    private int mLayoutWidth;
    /**
     * Layout height
     */
    private int mLayoutHeight;


    TableLayoutSettings() {
    }

    int getLayoutWidth() {
        return mLayoutWidth;
    }

    TableLayoutSettings setLayoutWidth(int layoutWidth) {
        mLayoutWidth = layoutWidth;
        return this;
    }

    int getLayoutHeight() {
        return mLayoutHeight;
    }

    TableLayoutSettings setLayoutHeight(int layoutHeight) {
        mLayoutHeight = layoutHeight;
        return this;
    }
}
