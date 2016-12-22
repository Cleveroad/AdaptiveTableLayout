package com.cleveroad.library;

/**
 * Manage row's heights and column's widths.
 * Work flow:
 * 1) Create object TableManager
 * 2) Call {@link #init(int, int)}
 * 3) Put data using {@link #putRowHeight(int, int)} and {@link #putColumnWidth(int, int)}
 * 4) Call invalidate
 * <p>
 * In case changing full width or count of rows or columns, you need to re-init manager.(steps 2 - 4)
 */
class TableManager {
    /**
     * Contains full width (columns widths)
     */
    private long mFullWidth;
    /**
     * Contains full height (rows heights)
     */
    private long mFullHeight;
    /**
     * Array with column's widths
     */
    private int[] mColumnWidths;
    /**
     * Array with row's heights
     */
    private int[] mRowHeights;

    /**
     * Column's header height
     */
    private int mHeaderColumnHeight;
    /**
     * Column's row width
     */
    private int mHeaderRowWidth;

    void init(int rowCount, int columnCount) {
        // create objects
        mRowHeights = new int[rowCount];
        mColumnWidths = new int[columnCount];
    }

    void invalidate() {
        // calculate widths
        mFullWidth = 0;
        for (int itemWidth : mColumnWidths) {
            mFullWidth += itemWidth;
        }

        // calculate heights
        for (int itemWidth : mRowHeights) {
            mFullHeight += itemWidth;
        }
    }

    /**
     * @param column column index
     * @return column's width
     */
    int getColumnWidth(int column) {
        return mColumnWidths[column];
    }

    /**
     * Put column width to the array. Call {@link #invalidate()} after all changes.
     *
     * @param column column index
     * @param width  column's width
     */
    void putColumnWidth(int column, int width) {
        mColumnWidths[column] = width;
    }

    /**
     * @param from from column index
     * @param to   to column index
     * @return columns width
     */
    int getColumnsWidth(int from, int to) {
        int width = 0;
        for (int i = from; i < to && mColumnWidths != null; i++) {
            width += mColumnWidths[i];
        }
        return width;
    }

    /**
     * @return columns count
     */
    int getColumnCount() {
        if (mColumnWidths != null) {
            return mColumnWidths.length;
        }
        return 0;
    }

    /**
     * @param row row index
     * @return row's height
     */
    int getRowHeight(int row) {
        return mRowHeights[row];
    }


    /**
     * Put row height to the array. Call {@link #invalidate()} after all changes.
     *
     * @param row    row index
     * @param height row's height
     */
    void putRowHeight(int row, int height) {
        mRowHeights[row] = height;

    }

    /**
     * @param from from row index
     * @param to   to row index
     * @return rows height
     */
    int getRowsHeight(int from, int to) {
        int height = 0;
        for (int i = from; i < to && mRowHeights != null; i++) {
            height += mRowHeights[i];
        }
        return height;
    }

    /**
     * @return rows count
     */
    int getRowCount() {
        if (mRowHeights != null) {
            return mRowHeights.length;
        }
        return 0;
    }

    /**
     * @return columns width with row's header width
     */
    long getFullWidth() {
        return mFullWidth + mHeaderRowWidth;
    }

    /**
     * @return rows height with column's header height
     */
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
        // header offset
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
        // header offset
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

    /**
     * @return column's header height
     */
    int getHeaderColumnHeight() {
        return mHeaderColumnHeight;
    }

    /**
     * Set column's header height.
     *
     * @param headerColumnHeight column's header height.
     */
    void setHeaderColumnHeight(int headerColumnHeight) {
        mHeaderColumnHeight = headerColumnHeight;
    }

    /**
     * @return row's header width
     */
    int getHeaderRowWidth() {
        return mHeaderRowWidth;
    }


    /**
     * Set row's header width.
     *
     * @param headerRowWidth row's header width.
     */
    void setHeaderRowWidth(int headerRowWidth) {
        mHeaderRowWidth = headerRowWidth;
    }

    /**
     * Switch 2 items in array with columns data
     *
     * @param columnIndex   from column index
     * @param columnToIndex to column index
     */
    void switchTwoColumns(int columnIndex, int columnToIndex) {
        int cellData = mColumnWidths[columnToIndex];
        mColumnWidths[columnToIndex] = mColumnWidths[columnIndex];
        mColumnWidths[columnIndex] = cellData;
    }

    /**
     * Switch 2 items in array with rows data
     *
     * @param rowIndex   from row index
     * @param rowToIndex to row index
     */
    void switchTwoRows(int rowIndex, int rowToIndex) {
        int cellData = mRowHeights[rowToIndex];
        mRowHeights[rowToIndex] = mRowHeights[rowIndex];
        mRowHeights[rowIndex] = cellData;
    }
}
