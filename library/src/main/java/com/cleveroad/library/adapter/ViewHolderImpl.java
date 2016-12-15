package com.cleveroad.library.adapter;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * {@inheritDoc}
 */
public abstract class ViewHolderImpl implements ViewHolder {
    private final View mItemView;
    private int mRowIndex;
    private int mColIndex;
    private int mItemType;
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
