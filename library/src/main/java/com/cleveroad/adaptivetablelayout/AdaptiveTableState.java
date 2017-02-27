package com.cleveroad.adaptivetablelayout;

/**
 * Layout state holder.
 */
class AdaptiveTableState {

    static final int NO_DRAGGING_POSITION = -1;
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

    /**
     * Dragging column index
     */
    private int mColumnDraggingIndex;
    /**
     * Dragging row index
     */
    private int mRowDraggingIndex;


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

    void setRowDragging(boolean rowDragging, int rowIndex) {
        mIsRowDragging = rowDragging;
        mRowDraggingIndex = rowIndex;
    }

    boolean isColumnDragging() {
        return mIsColumnDragging;
    }

    void setColumnDragging(boolean columnDragging, int columnIndex) {
        mIsColumnDragging = columnDragging;
        mColumnDraggingIndex = columnIndex;
    }

    boolean isDragging() {
        return mIsColumnDragging || mIsRowDragging;
    }

    int getColumnDraggingIndex() {
        return mColumnDraggingIndex;
    }

    int getRowDraggingIndex() {
        return mRowDraggingIndex;
    }
}
