package com.cleveroad.library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

/**
 * This is TableAdapter decorator (wrapper).
 * It makes it possible to change the rows and columns without data mutations.
 *
 * @param <VH> Adapter's ViewHolder class
 */
class DataTableAdapterImpl<VH extends ViewHolder> extends LinkedTableAdapter<VH> implements DataTableLayoutAdapter<VH> {
    private static final String EXTRA_SAVE_STATE_COLUMNS = "EXTRA_SAVE_STATE_COLUMNS";
    private static final String EXTRA_SAVE_STATE_ROWS = "EXTRA_SAVE_STATE_ROWS";
    /**
     * Decorated TableAdapter
     */
    private final TableAdapter<VH> mInner;

    /**
     * Redirect column's ids
     */
    private int[] mColumnIds;

    /**
     * Redirect row's ids
     */
    private int[] mRowIds;

    DataTableAdapterImpl(@NonNull TableAdapter<VH> inner) {
        mInner = inner;

        // init data
        mColumnIds = new int[getColumnCount()];
        mRowIds = new int[getRowCount()];

        // fill data
        fill(mColumnIds);
        fill(mRowIds);
    }

    private void fill(int[] array) {
        // filling its array indices
        for (int count = array.length, i = 0; i < count; i++) {
            array[i] = i;
        }
    }

    @Override
    public void changeColumns(int columnIndex, int columnToIndex) {
        switchTwoItems(mColumnIds, columnIndex, columnToIndex);
    }

    public void changeRows(int rowIndex, int rowToIndex) {
        switchTwoItems(mRowIds, rowIndex, rowToIndex);
    }

    @Override
    public int getRowCount() {
        return mInner.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return mInner.getColumnCount();
    }

    @NonNull
    @Override
    public VH onCreateItemViewHolder(@NonNull ViewGroup parent) {
        return mInner.onCreateItemViewHolder(parent);
    }

    @NonNull
    @Override
    public VH onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent) {
        return mInner.onCreateColumnHeaderViewHolder(parent);
    }

    @NonNull
    @Override
    public VH onCreateRowHeaderViewHolder(@NonNull ViewGroup parent) {
        return mInner.onCreateRowHeaderViewHolder(parent);
    }

    @NonNull
    @Override
    public VH onCreateLeftTopHeaderViewHolder(@NonNull ViewGroup parent) {
        return mInner.onCreateLeftTopHeaderViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull VH viewHolder, int row, int column) {
        mInner.onBindViewHolder(viewHolder, mRowIds[row], mColumnIds[column]);
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull VH viewHolder, int column) {
        mInner.onBindHeaderColumnViewHolder(viewHolder, mColumnIds[column]);
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull VH viewHolder, int row) {
        mInner.onBindHeaderRowViewHolder(viewHolder, mRowIds[row]);
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull VH viewHolder) {
        mInner.onBindLeftTopHeaderViewHolder(viewHolder);
    }

    @Override
    public int getColumnWidth(int column) {
        return mInner.getColumnWidth(column);
    }

    @Override
    public int getHeaderColumnHeight() {
        return mInner.getHeaderColumnHeight();
    }

    @Override
    public int getRowHeight(int row) {
        return mInner.getRowHeight(mRowIds[row]);
    }

    @Override
    public int getHeaderRowWidth() {
        return mInner.getHeaderRowWidth();
    }

    @Override
    @Nullable
    public OnItemClickListener getOnItemClickListener() {
        return mInner.getOnItemClickListener();
    }

    @Override
    @Nullable
    public OnItemLongClickListener getOnItemLongClickListener() {
        return mInner.getOnItemLongClickListener();
    }

    @Override
    public void onViewHolderRecycled(@NonNull VH viewHolder) {
        mInner.onViewHolderRecycled(viewHolder);
    }

    /**
     * Switched 2 values in the array
     *
     * @param array     array with values
     * @param fromIndex first index
     * @param toIndex   second index
     */
    void switchTwoItems(int[] array, int fromIndex, int toIndex) {
        int cellData = array[toIndex];
        array[toIndex] = array[fromIndex];
        array[fromIndex] = cellData;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        bundle.putIntArray(EXTRA_SAVE_STATE_COLUMNS, mColumnIds);
        bundle.putIntArray(EXTRA_SAVE_STATE_ROWS, mRowIds);

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle bundle) {
        restoreColumns(bundle.getIntArray(EXTRA_SAVE_STATE_COLUMNS));
        restoreRows(bundle.getIntArray(EXTRA_SAVE_STATE_ROWS));
    }

    private void restoreColumns(@Nullable int[] array) {
        if (array != null) {
            for (int count = array.length, i = 0; i < count; i++) {
                if (mColumnIds.length > i) {
                    mColumnIds[i] = array[i];
                }
            }
        }
    }

    private void restoreRows(@Nullable int[] array) {
        if (array != null) {
            for (int count = array.length, i = 0; i < count; i++) {
                if (mRowIds.length > i) {
                    mRowIds[i] = array[i];
                }
            }
        }
    }
}
