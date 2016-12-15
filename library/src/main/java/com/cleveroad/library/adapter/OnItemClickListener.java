package com.cleveroad.library.adapter;

public interface OnItemClickListener {
    void onItemClick(int row, int column);

    void onRowHeaderClick(int row);

    void onColumnHeaderClick(int column);

    void onLeftTopHeaderClick();
}
