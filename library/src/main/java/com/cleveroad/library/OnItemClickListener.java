package com.cleveroad.library;

public interface OnItemClickListener {
    /**
     * Click item callback.
     *
     * @param row    clicked item row
     * @param column clicked item column
     */
    void onItemClick(int row, int column);

    /**
     * Click row header item callback
     *
     * @param row clicked row header
     */
    void onRowHeaderClick(int row);

    /**
     * Click column header item callback
     *
     * @param column clicked column header
     */
    void onColumnHeaderClick(int column);


    /**
     * Click left top item callback
     */
    void onLeftTopHeaderClick();
}
