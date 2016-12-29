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

    private boolean mIsHeaderFixed;

    private int mCellMargin;


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

    public boolean isHeaderFixed() {
        return mIsHeaderFixed;
    }

    public TableLayoutSettings setHeaderFixed(boolean headerFixed) {
        mIsHeaderFixed = headerFixed;
        return this;
    }

    public int getCellMargin() {
        return mCellMargin;
    }

    public TableLayoutSettings setCellMargin(int cellMargin) {
        mCellMargin = cellMargin;
        return this;
    }
}
