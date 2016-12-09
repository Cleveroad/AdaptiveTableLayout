package com.cleveroad.library.tlib;

public class TTableManager {
    private long mFullWidth;
    private long mFullHeight;
    private int[] mColumnWidths;
    private int[] mRowHeights;

    private int mHeaderColumnHeight;
    private int mHeaderRowWidth;

    public void init(int rowCount, int columnCount) {
        mRowHeights = new int[rowCount];
        mColumnWidths = new int[columnCount];
    }

    public void invalidate() {
        mFullWidth = 0;
        for (int itemWidth : mColumnWidths) {
            mFullWidth += itemWidth;
        }

        mFullHeight = 0;
        for (int itemWidth : mRowHeights) {
            mFullHeight += itemWidth;
        }
    }

    public int getColumnWidth(int column) {
        return mColumnWidths[column];
    }

    public void putColumnWidth(int column, int width) {
        mColumnWidths[column] = width;
    }

    public int getColumnsWidth(int from, int to) {
        int width = 0;
        for (int i = from; i < to && mColumnWidths != null; i++) {
            width += mColumnWidths[i];
        }
        return width;
    }

    public int getColumnCount() {
        if (mColumnWidths != null) {
            return mColumnWidths.length;
        }
        return 0;
    }

    public int getRowHeight(int row) {
        return mRowHeights[row];
    }

    public void putRowHeight(int row, int height) {
        mRowHeights[row] = height;

    }

    public int getRowsHeight(int from, int to) {
        int height = 0;
        for (int i = from; i < to && mRowHeights != null; i++) {
            height += mRowHeights[i];
        }
        return height;
    }

    public int getRowCount() {
        if (mRowHeights != null) {
            return mRowHeights.length;
        }
        return 0;
    }

    public long getFullWidth() {
        return mFullWidth;
    }

    public long getFullHeight() {
        return mFullHeight;
    }

    /**
     * Return column number which bounds contains X
     *
     * @param x coordinate
     * @return column number
     */
    public int getColumnByX(int x) {
        int sum = 0;
        if (x <= sum) {
            return 0;
        }
        for (int count = mColumnWidths.length, i = 0; i < count; i++) {
            int nextSum = sum + mColumnWidths[i];
            if (x > sum && x < nextSum) {
                return i;
            } else if (x < sum && x < nextSum) {
                return i - 1;
            }
            sum = nextSum;
        }
        return mColumnWidths.length - 1;
    }

    /**
     * Return row number which bounds contains Y
     *
     * @param y coordinate
     * @return row number
     */
    public int getRowByY(int y) {
        int sum = 0;
        if (y <= sum) {
            return 0;
        }
        for (int count = mRowHeights.length, i = 0; i < count; i++) {
            int nextSum = sum + mRowHeights[i];

            if (y > sum && y < nextSum) {
                return i;
            } else if (y < sum && y < nextSum) {
                return i - 1;
            }
            sum = nextSum;
        }
        return mRowHeights.length - 1;
    }

    public int getHeaderColumnHeight() {
        return mHeaderColumnHeight;
    }

    public void setHeaderColumnHeight(int headerColumnHeight) {
        mHeaderColumnHeight = headerColumnHeight;
    }

    public int getHeaderRowWidth() {
        return mHeaderRowWidth;
    }

    public void setHeaderRowWidth(int headerRowWidth) {
        mHeaderRowWidth = headerRowWidth;
    }
}
