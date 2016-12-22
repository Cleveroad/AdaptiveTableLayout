package com.cleveroad.library;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * Common base class of common implementation for an {@link TableAdapter} that
 * can be used in {@link TableLayout}.
 */
public abstract class LinkedTableAdapter<VH extends ViewHolder> implements TableAdapter<VH> {
    /**
     * Set with observers
     */
    @NonNull
    private final List<TableDataSetObserver> mTableDataSetObservers = new ArrayList<>();

    /**
     * Need to throw item click action
     */
    @Nullable
    private OnItemClickListener mOnItemClickListener;

    /**
     * Need to throw long item click action
     */
    @Nullable
    private OnItemLongClickListener mOnItemLongClickListener;

    @Override
    @Nullable
    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    @Override
    public void setOnItemClickListener(@Nullable OnItemClickListener onItemLongClickListener) {
        mOnItemClickListener = onItemLongClickListener;
    }

    @Override
    @Nullable
    public OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    @Override
    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    public List<TableDataSetObserver> getTableDataSetObservers() {
        return mTableDataSetObservers;
    }

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
    public void notifyLayoutChanged() {
        for (TableDataSetObserver observer : mTableDataSetObservers) {
            observer.notifyLayoutChanged();
        }
    }

}
