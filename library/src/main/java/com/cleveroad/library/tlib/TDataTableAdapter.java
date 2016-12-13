package com.cleveroad.library.tlib;

public interface TDataTableAdapter<VH extends TTableAdapter.TViewHolder> extends TTableAdapter<VH> {

    void changeColumns(int columnIndex, int columnToIndex);

    void changeRows(int rowIndex, int rowToIndex);
}
