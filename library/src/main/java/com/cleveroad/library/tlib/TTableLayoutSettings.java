package com.cleveroad.library.tlib;

public class TTableLayoutSettings {

    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private int mLayoutWidth;
    private int mLayoutHeight;


    public TTableLayoutSettings() {
    }

    public int getMinimumVelocity() {
        return mMinimumVelocity;
    }

    public TTableLayoutSettings setMinimumVelocity(int minimumVelocity) {
        mMinimumVelocity = minimumVelocity;
        return this;
    }

    public int getMaximumVelocity() {
        return mMaximumVelocity;
    }

    public TTableLayoutSettings setMaximumVelocity(int maximumVelocity) {
        mMaximumVelocity = maximumVelocity;
        return this;
    }

    public int getLayoutWidth() {
        return mLayoutWidth;
    }

    public TTableLayoutSettings setLayoutWidth(int layoutWidth) {
        mLayoutWidth = layoutWidth;
        return this;
    }

    public int getLayoutHeight() {
        return mLayoutHeight;
    }

    public TTableLayoutSettings setLayoutHeight(int layoutHeight) {
        mLayoutHeight = layoutHeight;
        return this;
    }
}
