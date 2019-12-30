package com.cleveroad.adaptivetablelayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * This is AdaptiveTableAdapter decorator (wrapper).
 * It makes it possible to change the rows and columns without data mutations.
 *
 * @param <VH> Adapter's ViewHolder class
 */
class LinkedAdaptiveTableAdapterImpl<VH extends ViewHolder> extends LinkedAdaptiveTableAdapter<VH> implements
        DataAdaptiveTableLayoutAdapter<VH>,
        OnItemClickListener,
        OnItemLongClickListener {
    private static final String EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID = "EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID";
    private static final String EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX = "EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX";
    private static final String EXTRA_SAVE_STATE_ROW_INDEX_TO_ID = "EXTRA_SAVE_STATE_ROW_INDEX_TO_ID";
    private static final String EXTRA_SAVE_STATE_ROW_ID_TO_INDEX = "EXTRA_SAVE_STATE_ROW_ID_TO_INDEX";
    /**
     * Decorated AdaptiveTableAdapter
     */
    private final AdaptiveTableAdapter<VH> mInner;

    /**
     * need to fix row header to data or to row' number
     * true - fixed to the row data.
     * false - fixed to row number.
     */
    private boolean mIsSolidRowHeader;
    /**
     * Redirect column's ids
     */
    private HashMap<Integer, Integer> mColumnIndexToId;
    private HashMap<Integer, Integer> mColumnIdToIndex;

    /**
     * Redirect row's ids
     */
    private HashMap<Integer, Integer> mRowIndexToId;
    private HashMap<Integer, Integer> mRowIdToIndex;

    @SuppressLint("UseSparseArrays")
    LinkedAdaptiveTableAdapterImpl(@NonNull AdaptiveTableAdapter<VH> inner, boolean isSolidRowHeader) {
        mInner = inner;
        mIsSolidRowHeader = isSolidRowHeader;

        // init data
        mColumnIndexToId = new HashMap<>();
        mColumnIdToIndex = new HashMap<>();
        mRowIndexToId = new HashMap<>();
        mRowIdToIndex = new HashMap<>();
    }

    @Override
    public void onItemLongClick(int row, int column) {
        OnItemLongClickListener innerListener = mInner.getOnItemLongClickListener();
        if (innerListener != null) {
            innerListener.onItemLongClick(rowIndexToId(row), columnIndexToId(column));
        }
    }

    @Override
    public void onLeftTopHeaderLongClick() {
        OnItemLongClickListener innerListener = mInner.getOnItemLongClickListener();
        if (innerListener != null) {
            innerListener.onLeftTopHeaderLongClick();
        }
    }

    @Override
    public void onItemClick(int row, int column) {
        OnItemClickListener innerListener = mInner.getOnItemClickListener();
        if (innerListener != null) {
            int tempRow = row + 1; // need to merge matrix with table headers and without.
            int tempColumn = column + 1; // need to merge matrix with table headers and without.
            innerListener.onItemClick(rowIndexToId(tempRow), columnIndexToId(tempColumn));
        }
    }

    @Override
    public void onRowHeaderClick(int row) {
        OnItemClickListener innerListener = mInner.getOnItemClickListener();
        if (innerListener != null) {
            int tempRow = row + 1; // need to merge matrix with table headers and without.
            innerListener.onRowHeaderClick(mIsSolidRowHeader ? rowIndexToId(tempRow) : tempRow);
        }
    }

    @Override
    public void onColumnHeaderClick(int column) {
        OnItemClickListener innerListener = mInner.getOnItemClickListener();
        if (innerListener != null) {
            int tempColumn = column + 1; // need to merge matrix with table headers and without.
            innerListener.onColumnHeaderClick(columnIndexToId(tempColumn));
        }
    }

    @Override
    public void onLeftTopHeaderClick() {
        OnItemClickListener innerListener = mInner.getOnItemClickListener();
        if (innerListener != null) {
            innerListener.onLeftTopHeaderClick();
        }
    }

    @Override
    public void changeColumns(int columnIndex, int columnToIndex) {

        int tempColumnIndex = columnIndex + 1; // need to merge matrix with table headers and without.
        int tempColumnToIndex = columnToIndex + 1; // need to merge matrix with table headers and without.

        int fromId = columnIndexToId(tempColumnIndex);
        int toId = columnIndexToId(tempColumnToIndex);
        if (tempColumnIndex != toId) {
            mColumnIndexToId.put(tempColumnIndex, toId);
            mColumnIdToIndex.put(toId, tempColumnIndex);
        } else {
            //remove excess modifications
            mColumnIndexToId.remove(tempColumnIndex);
            mColumnIdToIndex.remove(toId);
        }

        if (tempColumnToIndex != fromId) {
            mColumnIndexToId.put(tempColumnToIndex, fromId);
            mColumnIdToIndex.put(fromId, tempColumnToIndex);
        } else {
            //remove excess modifications
            mColumnIndexToId.remove(tempColumnToIndex);
            mColumnIdToIndex.remove(fromId);
        }
    }

    @Override
    public void changeRows(int rowIndex, int rowToIndex, boolean solidRowHeader) {

        int tempRowIndex = rowIndex + 1; // need to merge matrix with table headers and without.
        int tempRowToIndex = rowToIndex + 1; // need to merge matrix with table headers and without.

        mIsSolidRowHeader = solidRowHeader;
        int fromId = rowIndexToId(tempRowIndex);
        int toId = rowIndexToId(tempRowToIndex);
        if (tempRowIndex != toId) {
            mRowIndexToId.put(tempRowIndex, toId);
            mRowIdToIndex.put(toId, tempRowIndex);
        } else {
            mRowIndexToId.remove(tempRowIndex);
            mRowIdToIndex.remove(toId);
        }

        if (tempRowToIndex != fromId) {
            mRowIndexToId.put(tempRowToIndex, fromId);
            mRowIdToIndex.put(fromId, tempRowToIndex);
        } else {
            mRowIndexToId.remove(tempRowToIndex);
            mRowIdToIndex.remove(fromId);
        }
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
        int tempRow = row + 1; // need to merge matrix with table headers and without.
        int tempColumn = column + 1; // need to merge matrix with table headers and without.
        mInner.onBindViewHolder(viewHolder, rowIndexToId(tempRow), columnIndexToId(tempColumn));
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull VH viewHolder, int column) {
        int tempColumn = column + 1; // need to merge matrix with table headers and without.
        mInner.onBindHeaderColumnViewHolder(viewHolder, columnIndexToId(tempColumn));
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull VH viewHolder, int row) {
        int tempRow = row + 1; // need to merge matrix with table headers and without.
        mInner.onBindHeaderRowViewHolder(viewHolder, mIsSolidRowHeader ? rowIndexToId(tempRow) : tempRow);
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull VH viewHolder) {
        mInner.onBindLeftTopHeaderViewHolder(viewHolder);
    }

    @Override
    public int getColumnWidth(int column) {
        int tempColumn = column + 1; // need to merge matrix with table headers and without.
        return mInner.getColumnWidth(columnIndexToId(tempColumn));
    }

    @Override
    public int getHeaderColumnHeight() {
        return mInner.getHeaderColumnHeight();
    }

    @Override
    public int getRowHeight(int row) {
        int tempRow = row + 1; // need to merge matrix with table headers and without.
        return mInner.getRowHeight(rowIndexToId(tempRow));
    }

    @Override
    public int getHeaderRowWidth() {
        return mInner.getHeaderRowWidth();
    }

    @Override
    @Nullable
    public OnItemClickListener getOnItemClickListener() {
        return this;
    }

    @Override
    @Nullable
    public OnItemLongClickListener getOnItemLongClickListener() {
        return this;
    }

    @Override
    public void onViewHolderRecycled(@NonNull VH viewHolder) {
        mInner.onViewHolderRecycled(viewHolder);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        bundle.putSerializable(EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID, mColumnIndexToId);
        bundle.putSerializable(EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX, mColumnIdToIndex);

        bundle.putSerializable(EXTRA_SAVE_STATE_ROW_INDEX_TO_ID, mRowIndexToId);
        bundle.putSerializable(EXTRA_SAVE_STATE_ROW_ID_TO_INDEX, mRowIdToIndex);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRestoreInstanceState(@NonNull Bundle bundle) {
        mColumnIndexToId = (HashMap<Integer, Integer>) bundle.getSerializable(EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID);
        mColumnIdToIndex = (HashMap<Integer, Integer>) bundle.getSerializable(EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX);

        mRowIndexToId = (HashMap<Integer, Integer>) bundle.getSerializable(EXTRA_SAVE_STATE_ROW_INDEX_TO_ID);
        mRowIdToIndex = (HashMap<Integer, Integer>) bundle.getSerializable(EXTRA_SAVE_STATE_ROW_ID_TO_INDEX);
    }

    @Override
    public void notifyItemChanged(int rowIndex, int columnIndex) {
        super.notifyItemChanged(rowIdToIndex(rowIndex), columnIdToIndex(columnIndex));
    }

    @Override
    public void notifyRowChanged(int rowIndex) {
        super.notifyRowChanged(rowIdToIndex(rowIndex));
    }

    @Override
    public void notifyColumnChanged(int columnIndex) {
        super.notifyColumnChanged(columnIdToIndex(columnIndex));
    }

    public Map<Integer, Integer> getRowsModifications() {
        return mRowIndexToId;
    }

    public Map<Integer, Integer> getColumnsModifications() {
        return mColumnIndexToId;
    }

    private int columnIndexToId(int columnIndex) {
        Integer id = mColumnIndexToId.get(columnIndex);
        return id != null ? id : columnIndex;
    }

    private int columnIdToIndex(int columnId) {
        Integer index = mColumnIdToIndex.get(columnId);
        return index != null ? index : columnId;
    }

    private int rowIndexToId(int rowIndex) {
        Integer id = mRowIndexToId.get(rowIndex);
        return id != null ? id : rowIndex;
    }

    private int rowIdToIndex(int rowId) {
        Integer index = mRowIdToIndex.get(rowId);
        return index != null ? index : rowId;
    }
}
