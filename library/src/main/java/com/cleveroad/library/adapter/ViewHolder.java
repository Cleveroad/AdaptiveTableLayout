package com.cleveroad.library.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import com.cleveroad.library.TableLayout;

/**
 * A {@link ViewHolder} describes an item view and metadata about its place within the {@link TableLayout}.
 */
public interface ViewHolder {
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

    boolean isDragging();

    void setIsDragging(boolean isDragging);
}
