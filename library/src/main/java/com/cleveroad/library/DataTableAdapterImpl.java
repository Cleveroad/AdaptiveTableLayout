package com.cleveroad.library;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.cleveroad.library.adapter.DataTableLayoutAdapter;
import com.cleveroad.library.adapter.LinkedTableAdapter;
import com.cleveroad.library.adapter.TableAdapter;
import com.cleveroad.library.adapter.ViewHolder;

class DataTableAdapterImpl<VH extends ViewHolder> extends LinkedTableAdapter<VH> implements DataTableLayoutAdapter<VH> {
    private final TableAdapter<VH> mInner;
    private final int[] mColumnIds;
    private final int[] mRowIds;

    public DataTableAdapterImpl(@NonNull TableAdapter<VH> inner) {
        mInner = inner;
        mColumnIds = new int[getColumnCount()];
        mRowIds = new int[getRowCount()];
        fill(mColumnIds);
        fill(mRowIds);
    }

    private void fill(int[] array) {
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
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
        return mInner.onCreateViewHolder(parent, itemType);
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

    void switchTwoItems(int[] array, int columnIndex, int columnToIndex) {
        int cellData = array[columnToIndex];
        array[columnToIndex] = array[columnIndex];
        array[columnIndex] = cellData;
    }

}
