package com.cleveroad.library;

public class TableLayoutSettings {

    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private int mLayoutWidth;
    private int mLayoutHeight;


    public TableLayoutSettings() {
    }

    public int getMinimumVelocity() {
        return mMinimumVelocity;
    }

    public TableLayoutSettings setMinimumVelocity(int minimumVelocity) {
        mMinimumVelocity = minimumVelocity;
        return this;
    }

    public int getMaximumVelocity() {
        return mMaximumVelocity;
    }

    public TableLayoutSettings setMaximumVelocity(int maximumVelocity) {
        mMaximumVelocity = maximumVelocity;
        return this;
    }

    public int getLayoutWidth() {
        return mLayoutWidth;
    }

    public TableLayoutSettings setLayoutWidth(int layoutWidth) {
        mLayoutWidth = layoutWidth;
        return this;
    }

    public int getLayoutHeight() {
        return mLayoutHeight;
    }

    public TableLayoutSettings setLayoutHeight(int layoutHeight) {
        mLayoutHeight = layoutHeight;
        return this;
    }
}
