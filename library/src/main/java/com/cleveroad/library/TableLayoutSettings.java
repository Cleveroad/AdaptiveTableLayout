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

    /**
     * if true - value of row header fixed to the row. Fixed to the data
     * if false - fixed to the number of row. Fixed to the row' number from 0 to n.
     */
    private boolean mSolidRowHeader;


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

    public boolean isSolidRowHeader() {
        return mSolidRowHeader;
    }

    public TableLayoutSettings setSolidRowHeader(boolean solidRowHeader) {
        mSolidRowHeader = solidRowHeader;
        return this;
    }
}
