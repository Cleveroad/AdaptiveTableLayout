package com.cleveroad.adaptivetablelayout;

public abstract class BaseDataAdaptiveTableLayoutAdapter<VH extends ViewHolder> extends LinkedAdaptiveTableAdapter<VH> implements
        DataAdaptiveTableLayoutAdapter<VH> {

    /**
     * @return data matrix
     */
    protected abstract Object[][] getItems();

    /**
     * @return row's headers array
     */
    protected abstract Object[] getRowHeaders();

    /**
     * @return column's headers array
     */
    protected abstract Object[] getColumnHeaders();

    @Override
    public void changeColumns(int columnIndex, int columnToIndex) {
        // switch data
        switchTwoColumns(columnIndex, columnToIndex);
        // switch headers
        switchTwoColumnHeaders(columnIndex, columnToIndex);
    }

    /**
     * Switch 2 columns with data
     *
     * @param columnIndex   column from
     * @param columnToIndex column to
     */
    void switchTwoColumns(int columnIndex, int columnToIndex) {
        for (int i = 0; i < getRowCount(); i++) {
            Object cellData = getItems()[i][columnToIndex];
            getItems()[i][columnToIndex] = getItems()[i][columnIndex];
            getItems()[i][columnIndex] = cellData;
        }
    }

    /**
     * Switch 2 columns headers with data
     *
     * @param columnIndex   column header from
     * @param columnToIndex column header to
     */
    void switchTwoColumnHeaders(int columnIndex, int columnToIndex) {
        Object cellData = getColumnHeaders()[columnToIndex];
        getColumnHeaders()[columnToIndex] = getColumnHeaders()[columnIndex];
        getColumnHeaders()[columnIndex] = cellData;
    }

    @Override
    public void changeRows(int rowIndex, int rowToIndex, boolean solidRowHeader) {
        // switch data
        switchTwoRows(rowIndex, rowToIndex);
        if (solidRowHeader) {
            // switch headers
            switchTwoRowHeaders(rowIndex, rowToIndex);
        }
    }

    /**
     * Switch 2 rows with data
     *
     * @param rowIndex   row from
     * @param rowToIndex row to
     */
    void switchTwoRows(int rowIndex, int rowToIndex) {
        for (int i = 0; i < getItems().length; i++) {
            Object cellData = getItems()[rowToIndex][i];
            getItems()[rowToIndex][i] = getItems()[rowIndex][i];
            getItems()[rowIndex][i] = cellData;
        }
    }

    /**
     * Switch 2 rows headers with data
     *
     * @param rowIndex   row header from
     * @param rowToIndex row header to
     */
    void switchTwoRowHeaders(int rowIndex, int rowToIndex) {
        Object cellData = getRowHeaders()[rowToIndex];
        getRowHeaders()[rowToIndex] = getRowHeaders()[rowIndex];
        getRowHeaders()[rowIndex] = cellData;
    }
}
