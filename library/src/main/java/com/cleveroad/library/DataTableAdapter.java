package com.cleveroad.library;

interface DataTableAdapter<VH extends TableAdapter.ViewHolder> extends TableAdapter<VH> {

    void changeColumns(int columnIndex, int columnToIndex);

    void changeRows(int rowIndex, int rowToIndex);
}
