package com.cleveroad.library;

/**
 * Settings keeper class.
 */
class TableLayoutSettings {
    /**
     * Minimum velocity var
     */
    private int mMinVelocity;
    /**
     * Maximum velocity var
     */
    private int mMaxVelocity;

    private int mLayoutWidth;
    private int mLayoutHeight;


    TableLayoutSettings() {
    }

    public int getMinVelocity() {
        return mMinVelocity;
    }

    TableLayoutSettings setMinVelocity(int minVelocity) {
        mMinVelocity = minVelocity;
        return this;
    }

    int getMaxVelocity() {
        return mMaxVelocity;
    }

    TableLayoutSettings setMaxVelocity(int maxVelocity) {
        mMaxVelocity = maxVelocity;
        return this;
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
