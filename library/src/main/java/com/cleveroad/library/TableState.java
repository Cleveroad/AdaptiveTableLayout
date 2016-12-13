package com.cleveroad.library;

public class TableState {

    private int mScrollX;
    private int mScrollY;

    private boolean mIsRowDragging;
    private boolean mIsColumnDragging;

    public int getScrollX() {
        return mScrollX;
    }

    public void setScrollX(int scrollX) {
        mScrollX = scrollX;
    }

    public int getScrollY() {
        return mScrollY;
    }

    public void setScrollY(int scrollY) {
        mScrollY = scrollY;
    }

    public boolean isRowDragging() {
        return mIsRowDragging;
    }

    public void setRowDragging(boolean rowDragging) {
        mIsRowDragging = rowDragging;
    }

    public boolean isColumnDragging() {
        return mIsColumnDragging;
    }

    public void setColumnDragging(boolean columnDragging) {
        mIsColumnDragging = columnDragging;
    }
}
