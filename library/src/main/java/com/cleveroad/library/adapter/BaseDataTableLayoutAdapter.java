package com.cleveroad.library.adapter;

public abstract class BaseDataTableLayoutAdapter<VH extends ViewHolder> extends LinkedTableAdapter<VH> implements DataTableLayoutAdapter<VH> {

    protected abstract Object[][] getItems();

    protected abstract Object[] getRowHeaders();

    protected abstract Object[] getColumnHeaders();

    @Override
    public void changeColumns(int columnIndex, int columnToIndex) {
        switchTwoColumns(columnIndex, columnToIndex);
        switchTwoColumnHeaders(columnIndex, columnToIndex);
    }

    void switchTwoColumns(int columnIndex, int columnToIndex) {
        for (int i = 0; i < getRowCount(); i++) {
            Object cellData = getItems()[i][columnToIndex];
            getItems()[i][columnToIndex] = getItems()[i][columnIndex];
            getItems()[i][columnIndex] = cellData;
        }
    }

    void switchTwoColumnHeaders(int columnIndex, int columnToIndex) {
        Object cellData = getColumnHeaders()[columnToIndex];
        getColumnHeaders()[columnToIndex] = getColumnHeaders()[columnIndex];
        getColumnHeaders()[columnIndex] = cellData;
    }

    @Override
    public void changeRows(int rowIndex, int rowToIndex) {
        switchTwoRows(rowIndex, rowToIndex);
        switchTwoRowHeaders(rowIndex, rowToIndex);
    }

    void switchTwoRows(int rowIndex, int rowToIndex) {
        for (int i = 0; i < getItems().length; i++) {
            Object cellData = getItems()[rowToIndex][i];
            getItems()[rowToIndex][i] = getItems()[rowIndex][i];
            getItems()[rowIndex][i] = cellData;
        }
    }

    void switchTwoRowHeaders(int rowIndex, int rowToIndex) {
        Object cellData = getRowHeaders()[rowToIndex];
        getRowHeaders()[rowToIndex] = getRowHeaders()[rowIndex];
        getRowHeaders()[rowIndex] = cellData;
    }
}
