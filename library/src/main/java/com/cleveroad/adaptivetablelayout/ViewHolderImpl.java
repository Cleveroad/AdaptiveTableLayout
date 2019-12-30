package com.cleveroad.adaptivetablelayout;

import androidx.annotation.NonNull;
import android.view.View;

/**
 * {@inheritDoc}
 */
public abstract class ViewHolderImpl implements ViewHolder {
    /**
     * Holder view
     */
    private final View mItemView;
    /**
     * ViewHolder's table row index
     */
    private int mRowIndex;
    /**
     * ViewHolder's table column index
     */
    private int mColIndex;
    /**
     * ViewHolder's table item type param
     */
    private int mItemType;
    /**
     * ViewHolder's dragging flag
     */
    private boolean mIsDragging;

    public ViewHolderImpl(@NonNull View itemView) {
        mItemView = itemView;
    }

    @NonNull
    @Override
    public View getItemView() {
        return mItemView;
    }

    @Override
    public int getRowIndex() {
        return mRowIndex;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        mRowIndex = rowIndex;
    }

    @Override
    public int getColumnIndex() {
        return mColIndex;
    }

    @Override
    public void setColumnIndex(int columnIndex) {
        mColIndex = columnIndex;
    }

    @Override
    public int getItemType() {
        return mItemType;
    }

    @Override
    public void setItemType(int itemType) {
        mItemType = itemType;
    }

    @Override
    public int hashCode() {
        int result = mItemView.hashCode();
        result = 31 * result + mRowIndex;
        result = 31 * result + mColIndex;
        result = 31 * result + mItemType;
        result = 31 * result + (mIsDragging ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ViewHolder)) {
            return false;
        }
        ViewHolder vh = (ViewHolder) obj;
        return vh.getColumnIndex() == this.getColumnIndex() && vh.getRowIndex() == this.getRowIndex();
    }

    @Override
    public boolean isDragging() {
        return mIsDragging;
    }

    @Override
    public void setIsDragging(boolean isDragging) {
        mIsDragging = isDragging;
    }
}
