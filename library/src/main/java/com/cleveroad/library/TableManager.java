package com.cleveroad.library;

class TableManager {
    private long mFullWidth;
    private long mFullHeight;
    private int[] mColumnWidths;
    private int[] mRowHeights;

    private int mHeaderColumnHeight;
    private int mHeaderRowWidth;

    void init(int rowCount, int columnCount) {
        mRowHeights = new int[rowCount];
        mColumnWidths = new int[columnCount];
    }

    void invalidate() {
        mFullWidth = 0;
        for (int itemWidth : mColumnWidths) {
            mFullWidth += itemWidth;
        }

        mFullHeight = 0;
        for (int itemWidth : mRowHeights) {
            mFullHeight += itemWidth;
        }
    }

    int getColumnWidth(int column) {
        return mColumnWidths[column];
    }

    void putColumnWidth(int column, int width) {
        mColumnWidths[column] = width;
    }

    int getColumnsWidth(int from, int to) {
        int width = 0;
        for (int i = from; i < to && mColumnWidths != null; i++) {
            width += mColumnWidths[i];
        }
        return width;
    }

    int getColumnCount() {
        if (mColumnWidths != null) {
            return mColumnWidths.length;
        }
        return 0;
    }

    int getRowHeight(int row) {
        return mRowHeights[row];
    }

    void putRowHeight(int row, int height) {
        mRowHeights[row] = height;

    }

    int getRowsHeight(int from, int to) {
        int height = 0;
        for (int i = from; i < to && mRowHeights != null; i++) {
            height += mRowHeights[i];
        }
        return height;
    }

    int getRowCount() {
        if (mRowHeights != null) {
            return mRowHeights.length;
        }
        return 0;
    }

    long getFullWidth() {
        return mFullWidth + mHeaderRowWidth;
    }

    long getFullHeight() {
        return mFullHeight + mHeaderColumnHeight;
    }

    /**
     * Return column number which bounds contains X
     *
     * @param x coordinate
     * @return column number
     */
    int getColumnByX(int x) {
        int sum = 0;
        x -= mHeaderRowWidth;
        if (x <= sum) {
            return 0;
        }
        for (int count = mColumnWidths.length, i = 0; i < count; i++) {
            int nextSum = sum + mColumnWidths[i];
            if (x > sum && x < nextSum) {
                return i;
            } else if (x < nextSum) {
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
    int getRowByY(int y) {
        int sum = 0;
        y -= mHeaderColumnHeight;
        if (y <= sum) {
            return 0;
        }
        for (int count = mRowHeights.length, i = 0; i < count; i++) {
            int nextSum = sum + mRowHeights[i];

            if (y > sum && y < nextSum) {
                return i;
            } else if (y < nextSum) {
                return i - 1;
            }
            sum = nextSum;
        }
        return mRowHeights.length - 1;
    }

    int getHeaderColumnHeight() {
        return mHeaderColumnHeight;
    }

    void setHeaderColumnHeight(int headerColumnHeight) {
        mHeaderColumnHeight = headerColumnHeight;
    }

    int getHeaderRowWidth() {
        return mHeaderRowWidth;
    }

    void setHeaderRowWidth(int headerRowWidth) {
        mHeaderRowWidth = headerRowWidth;
    }

    void switchTwoColumns(int columnIndex, int columnToIndex) {
        int cellData = mColumnWidths[columnToIndex];
        mColumnWidths[columnToIndex] = mColumnWidths[columnIndex];
        mColumnWidths[columnIndex] = cellData;
    }

    void switchTwoRows(int rowIndex, int rowToIndex) {
        int cellData = mRowHeights[rowToIndex];
        mRowHeights[rowToIndex] = mRowHeights[rowIndex];
        mRowHeights[rowIndex] = cellData;
    }
}
