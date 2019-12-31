package com.cleveroad.adaptivetablelayout;

/**
 * Manage row's heights and column's widths.
 * Work flow:
 * 1) Create object AdaptiveTableManager
 * 2) Call {@link #init(int, int)}
 * 3) Put data using {@link #putRowHeight(int, int)} and {@link #putColumnWidth(int, int)}
 * 4) Call invalidate
 * <p>
 * In case changing full width or count of rows or columns, you need to re-init manager.(steps 2 - 4)
 */
class AdaptiveTableManager {
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

    private boolean mIsInited = false;

    void clear() {
        // clear objects
        mRowHeights = new int[0];
        mColumnWidths = new int[0];

        mFullWidth = 0;
        mFullHeight = 0;

        mHeaderColumnHeight = 0;
        mHeaderRowWidth = 0;

        mIsInited = false;
    }

    void init(int rowCount, int columnCount) {
        if (rowCount < 0) {
            rowCount = 0;
        }
        if (columnCount < 0) {
            columnCount = 0;
        }
        // create objects
        mRowHeights = new int[rowCount];
        mColumnWidths = new int[columnCount];
        mIsInited = true;
    }

    void checkForInit() {
        if (!mIsInited) {
            throw new IllegalStateException("You need to init matrix before work!");
        }
    }

    void invalidate() {
        checkForInit();
        // calculate widths
        mFullWidth = 0;
        for (int itemWidth : mColumnWidths) {
            mFullWidth += itemWidth;
        }

        // calculate heights
        mFullHeight = 0;
        for (int itemHeight : mRowHeights) {
            mFullHeight += itemHeight;
        }
    }

    /**
     * @param column column index
     * @return column's width
     */
    int getColumnWidth(int column) {
        checkForInit();
        return mColumnWidths[column];
    }

    /**
     * Put column width to the array. Call {@link #invalidate()} after all changes.
     *
     * @param column column index
     * @param width  column's width
     */
    void putColumnWidth(int column, int width) {
        checkForInit();
        mColumnWidths[column] = width;
    }

    /**
     * @param from from column index
     * @param to   to column index
     * @return columns width
     */
    int getColumnsWidth(int from, int to) {
        checkForInit();
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
        checkForInit();
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
        checkForInit();
        return mRowHeights[row];
    }


    /**
     * Put row height to the array. Call {@link #invalidate()} after all changes.
     *
     * @param row    row index
     * @param height row's height
     */
    void putRowHeight(int row, int height) {
        checkForInit();
        mRowHeights[row] = height;

    }

    /**
     * @param from from row index
     * @param to   to row index
     * @return rows height
     */
    int getRowsHeight(int from, int to) {
        checkForInit();
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
        checkForInit();
        if (mRowHeights != null) {
            return mRowHeights.length;
        }
        return 0;
    }

    /**
     * @return columns width with row's header width
     */
    long getFullWidth() {
        checkForInit();
        return mFullWidth + mHeaderRowWidth;
    }

    /**
     * @return rows height with column's header height
     */
    long getFullHeight() {
        checkForInit();
        return mFullHeight + mHeaderColumnHeight;
    }

    /**
     * Return column number which bounds contains X
     *
     * @param x coordinate
     * @return column number
     */
    int getColumnByX(int x) {
        checkForInit();
        int sum = 0;
        // header offset
        int tempX = x - mHeaderRowWidth;
        if (tempX <= sum) {
            return 0;
        }
        for (int count = mColumnWidths.length, i = 0; i < count; i++) {
            int nextSum = sum + mColumnWidths[i];
            if (tempX > sum && tempX < nextSum) {
                return i;
            } else if (tempX < nextSum) {
                return i - 1;
            }
            sum = nextSum;
        }
        return mColumnWidths.length - 1;
    }

    /**
     * Return column number which bounds contains X
     *
     * @param x coordinate
     * @return column number
     */
    int getColumnByXWithShift(int x, int shiftEveryStep) {
        checkForInit();
        int sum = 0;
        // header offset
        int tempX = x - mHeaderRowWidth;
        if (tempX <= sum) {
            return 0;
        }
        for (int count = mColumnWidths.length, i = 0; i < count; i++) {
            int nextSum = sum + mColumnWidths[i] + shiftEveryStep;
            if (tempX > sum && tempX < nextSum) {
                return i;
            } else if (tempX < nextSum) {
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
        checkForInit();
        int sum = 0;
        // header offset
        int tempY = y - mHeaderColumnHeight;
        if (tempY <= sum) {
            return 0;
        }
        for (int count = mRowHeights.length, i = 0; i < count; i++) {
            int nextSum = sum + mRowHeights[i];

            if (tempY > sum && tempY < nextSum) {
                return i;
            } else if (tempY < nextSum) {
                return i - 1;
            }
            sum = nextSum;
        }
        return mRowHeights.length - 1;
    }

    /**
     * Return row number which bounds contains Y
     *
     * @param y coordinate
     * @return row number
     */
    int getRowByYWithShift(int y, int shiftEveryStep) {
        checkForInit();
        int sum = 0;
        // header offset
        int tempY = y - mHeaderColumnHeight;
        if (tempY <= sum) {
            return 0;
        }
        for (int count = mRowHeights.length, i = 0; i < count; i++) {
            int nextSum = sum + mRowHeights[i] + shiftEveryStep;

            if (tempY > sum && tempY < nextSum) {
                return i;
            } else if (tempY < nextSum) {
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
        checkForInit();
        return mHeaderColumnHeight;
    }

    /**
     * Set column's header height.
     *
     * @param headerColumnHeight column's header height.
     */
    void setHeaderColumnHeight(int headerColumnHeight) {
        checkForInit();
        mHeaderColumnHeight = headerColumnHeight;
    }

    /**
     * @return row's header width
     */
    int getHeaderRowWidth() {
        checkForInit();
        return mHeaderRowWidth;
    }


    /**
     * Set row's header width.
     *
     * @param headerRowWidth row's header width.
     */
    void setHeaderRowWidth(int headerRowWidth) {
        checkForInit();
        mHeaderRowWidth = headerRowWidth;
    }

    /**
     * Switch 2 items in array with columns data
     *
     * @param columnIndex   from column index
     * @param columnToIndex to column index
     */
    void switchTwoColumns(int columnIndex, int columnToIndex) {
        checkForInit();
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
        checkForInit();
        int cellData = mRowHeights[rowToIndex];
        mRowHeights[rowToIndex] = mRowHeights[rowIndex];
        mRowHeights[rowIndex] = cellData;
    }

    public int[] getColumnWidths() {
        return mColumnWidths;
    }

    public int[] getRowHeights() {
        return mRowHeights;
    }
}
