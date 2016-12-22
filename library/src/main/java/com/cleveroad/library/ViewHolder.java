package com.cleveroad.library;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * A {@link ViewHolder} describes an item view and metadata about its place within the {@link TableLayout}.
 */
interface ViewHolder {
    /**
     * @return item represents the item of the {@link TableLayout}
     */
    @NonNull
    View getItemView();

    /**
     * @return the type of the View
     */
    int getItemType();

    /**
     * @param itemType is the type of the View
     */
    void setItemType(int itemType);

    /**
     * @return the row index.
     */
    int getRowIndex();

    /**
     * @param rowIndex the row index.
     */
    void setRowIndex(int rowIndex);

    /**
     * @return the column index.
     */
    int getColumnIndex();

    /**
     * @param columnIndex the column index.
     */
    void setColumnIndex(int columnIndex);


    /**
     * @return dragging flag
     */
    boolean isDragging();

    /**
     * @param isDragging dragging param
     */
    void setIsDragging(boolean isDragging);
}
