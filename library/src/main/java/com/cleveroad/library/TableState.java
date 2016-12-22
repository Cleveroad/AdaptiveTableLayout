package com.cleveroad.library;

class TableState {

    private int mScrollX;
    private int mScrollY;

    private boolean mIsRowDragging;
    private boolean mIsColumnDragging;

     int getScrollX() {
        return mScrollX;
    }

     void setScrollX(int scrollX) {
        mScrollX = scrollX;
    }

     int getScrollY() {
        return mScrollY;
    }

     void setScrollY(int scrollY) {
        mScrollY = scrollY;
    }

     boolean isRowDragging() {
        return mIsRowDragging;
    }

     void setRowDragging(boolean rowDragging) {
        mIsRowDragging = rowDragging;
    }

     boolean isColumnDragging() {
        return mIsColumnDragging;
    }

     void setColumnDragging(boolean columnDragging) {
        mIsColumnDragging = columnDragging;
    }
}
