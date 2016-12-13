package com.cleveroad.library;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * Common base class of common implementation for an {@link TableAdapter} that
 * can be used in {@link TableLayout}.
 */
public abstract class BaseTableAdapter<VH extends TableAdapter.ViewHolder> implements TableAdapter<VH> {
    private final List<TableDataSetObserver> mTableDataSetObservers = new ArrayList<>();

    /**
     * {@inheritDoc}
     *
     * @param observer the object that gets notified when the data set changes.
     */
    @Override
    public void registerDataSetObserver(@NonNull TableDataSetObserver observer) {
        mTableDataSetObservers.add(observer);
    }

    /**
     * {@inheritDoc}
     *
     * @param observer the object to unregister.
     */
    @Override
    public void unregisterDataSetObserver(@NonNull TableDataSetObserver observer) {
        mTableDataSetObservers.remove(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyDataSetChanged() {
        for (TableDataSetObserver observer : mTableDataSetObservers) {
            observer.notifyDataSetChanged();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param rowIndex    the row index
     * @param columnIndex the column index
     */
    @Override
    public void notifyItemChanged(int rowIndex, int columnIndex) {
        for (TableDataSetObserver observer : mTableDataSetObservers) {
            observer.notifyItemChanged(rowIndex, columnIndex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param rowIndex the row index
     */
    @Override
    public void notifyRowChanged(int rowIndex) {
        for (TableDataSetObserver observer : mTableDataSetObservers) {
            observer.notifyRowChanged(rowIndex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param columnIndex the column index
     */
    @Override
    public void notifyColumnChanged(int columnIndex) {
        for (TableDataSetObserver observer : mTableDataSetObservers) {
            observer.notifyColumnChanged(columnIndex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyHeadViewChanged() {
        for (TableDataSetObserver observer : mTableDataSetObservers) {
            observer.notifyHeadViewChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyLayoutChanged() {
        for (TableDataSetObserver observer : mTableDataSetObservers) {
            observer.notifyLayoutChanged();
        }
    }


    /**
     * {@inheritDoc}
     *
     * @param viewHolder The {@link ViewHolder} for the view being recycled
     */
    @Override
    public void onViewHolderRecycled(@NonNull VH viewHolder) {
        //do nothing
    }

    /**
     * {@inheritDoc}
     */
    public static abstract class ViewHolderImpl implements ViewHolder {
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
            ViewHolder vh = ((ViewHolder) obj);
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


}
