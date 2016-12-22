package com.cleveroad.library;

public interface OnItemLongClickListener {
    /**
     * Long click item callback.
     *
     * @param row    clicked item row
     * @param column clicked item column
     */
    void onItemLongClick(int row, int column);

    /**
     * Long click left top item callback
     */
    void onLeftTopHeaderLongClick();
}
