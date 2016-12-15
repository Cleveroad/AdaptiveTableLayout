package com.cleveroad.library.adapter;

public interface DataTableLayoutAdapter<VH extends ViewHolder> extends TableAdapter<VH> {

    void changeColumns(int columnIndex, int columnToIndex);

    void changeRows(int rowIndex, int rowToIndex);
}
