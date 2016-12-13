package com.cleveroad.library;

public class TTableState {

    private int mLeftColumn;
    private int mRightColumn;
    private int mTopRow;
    private int mBottomRow;
    private int mScrollX;
    private int mScrollY;

    private boolean mIsRowDragging;
    private boolean mIsColumnDragging;

    public int getLeftColumn() {
        return mLeftColumn;
    }

    public void setLeftColumn(int leftColumn) {
        mLeftColumn = leftColumn;
    }

    public int getRightColumn() {
        return mRightColumn;
    }

    public void setRightColumn(int rightColumn) {
        mRightColumn = rightColumn;
    }

    public int getTopRow() {
        return mTopRow;
    }

    public void setTopRow(int topRow) {
        mTopRow = topRow;
    }

    public int getBottomRow() {
        return mBottomRow;
    }

    public void setBottomRow(int bottomRow) {
        mBottomRow = bottomRow;
    }

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
