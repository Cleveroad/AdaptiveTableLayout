package com.cleveroad.library;

/**
 * Layout state holder.
 */
class TableState {
    /**
     * Current scroll position.
     */
    private int mScrollX;
    private int mScrollY;

    /**
     * Dragging row flag
     */
    private boolean mIsRowDragging;

    /**
     * Dragging column flag
     */
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

    boolean isDragging() {
        return mIsColumnDragging || mIsRowDragging;
    }
}
