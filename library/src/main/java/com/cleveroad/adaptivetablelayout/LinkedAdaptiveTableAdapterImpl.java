package com.cleveroad.adaptivetablelayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseIntArray;
import android.view.ViewGroup;

/**
 * This is AdaptiveTableAdapter decorator (wrapper).
 * It makes it possible to change the rows and columns without data mutations.
 *
 * @param <VH> Adapter's ViewHolder class
 */
public class LinkedAdaptiveTableAdapterImpl<VH extends ViewHolder> extends LinkedAdaptiveTableAdapter<VH> implements
        DataAdaptiveTableLayoutAdapter<VH>,
        OnItemClickListener,
        OnItemLongClickListener {
    /**
     * Decorated AdaptiveTableAdapter
     */
    private final AdaptiveTableAdapter<VH> mInner;

    private final ModificationsHolder mModsHolder;

    /**
     * need to fix row header to data or to row' number
     * true - fixed to the row data.
     * false - fixed to row number.
     */
    private boolean mIsSolidRowHeader;
    /**
     * Redirect column's ids
     */
    private final SparseIntArray mColumnIndexToId;
    private SparseIntArray mColumnIdToIndex;

    /**
     * Redirect row's ids
     */
    private final SparseIntArray mRowIndexToId;
    private SparseIntArray mRowIdToIndex;

    @SuppressLint("UseSparseArrays")
    LinkedAdaptiveTableAdapterImpl(@NonNull AdaptiveTableAdapter<VH> inner, boolean isSolidRowHeader) {
        mInner = inner;
        mModsHolder = inner instanceof ModificationsHolder ? ((ModificationsHolder) inner) : null;
        mIsSolidRowHeader = isSolidRowHeader;
        // init data
        mColumnIndexToId = new SparseIntArray();
        mColumnIdToIndex = new SparseIntArray();
        mRowIndexToId = new SparseIntArray();
        mRowIdToIndex = new SparseIntArray();
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
            getColumnsIndexToIdModifications().put(tempColumnIndex, toId);
            mColumnIdToIndex.put(toId, tempColumnIndex);
        } else {
            //remove excess modifications
            getColumnsIndexToIdModifications().delete(tempColumnIndex);
            mColumnIdToIndex.delete(toId);
        }

        if (tempColumnToIndex != fromId) {
            getColumnsIndexToIdModifications().put(tempColumnToIndex, fromId);
            mColumnIdToIndex.put(fromId, tempColumnToIndex);
        } else {
            //remove excess modifications
            getColumnsIndexToIdModifications().delete(tempColumnToIndex);
            mColumnIdToIndex.delete(fromId);
        }
        if (mModsHolder != null) {
            mModsHolder.changeColumns(columnIndex, columnToIndex);
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
            getRowsIndexToIdModifications().put(tempRowIndex, toId);
            mRowIdToIndex.put(toId, tempRowIndex);
        } else {
            getRowsIndexToIdModifications().delete(tempRowIndex);
            mRowIdToIndex.delete(toId);
        }

        if (tempRowToIndex != fromId) {
            getRowsIndexToIdModifications().put(tempRowToIndex, fromId);
            mRowIdToIndex.put(fromId, tempRowToIndex);
        } else {
            getRowsIndexToIdModifications().delete(tempRowToIndex);
            mRowIdToIndex.delete(fromId);
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
        /*bundle.putSerializable(EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID, (Serializable) getColumnsIndexToIdModifications());
        bundle.putSerializable(EXTRA_SAVE_STATE_ROW_INDEX_TO_ID, (Serializable) getRowsIndexToIdModifications());*/
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRestoreInstanceState(@NonNull Bundle bundle) {
        /*HashMap<Integer, Integer> columnsMods = (HashMap<Integer, Integer>) bundle.getSerializable(EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID);
        if (columnsMods != null) {
            setColumnsModifications(columnsMods);
        }

        HashMap<Integer, Integer> rowsMods = (HashMap<Integer, Integer>) bundle.getSerializable(EXTRA_SAVE_STATE_ROW_INDEX_TO_ID);
        if (rowsMods != null) {
            setRowsModifications(rowsMods);
        }*/
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

    @NonNull
    @Override
    public SparseIntArray getRowsIndexToIdModifications() {
        return mModsHolder == null ? mRowIndexToId : mModsHolder.getRowsIndexToIdModifications();
    }

    @NonNull
    @Override
    public SparseIntArray getColumnsIndexToIdModifications() {
        return mModsHolder == null ? mColumnIndexToId : mModsHolder.getColumnsIndexToIdModifications();
    }

    @Override
    public void setRowsModifications(@NonNull SparseIntArray mod) {
        mRowIndexToId.clear();
        mRowIdToIndex.clear();
        for (int i = 0; i < mod.size(); i++) {
            mRowIndexToId.put(mod.keyAt(i), mod.valueAt(i));
            mRowIdToIndex.put(mod.valueAt(i), mod.keyAt(i));
        }
        if (mModsHolder != null) {
            mModsHolder.setRowsModifications(mod);
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    public void setColumnsModifications(@NonNull SparseIntArray mod) {
        mColumnIndexToId.clear();
        mColumnIdToIndex.clear();
        for (int i = 0; i < mod.size(); i++) {
            mColumnIndexToId.put(mod.keyAt(i), mod.valueAt(i));
            mColumnIdToIndex.put(mod.valueAt(i), mod.keyAt(i));
        }
        if (mModsHolder != null) {
            mModsHolder.setColumnsModifications(mod);
        } else {
            notifyDataSetChanged();
        }
    }

    private int columnIndexToId(int columnIndex) {
        int id = getColumnsIndexToIdModifications().get(columnIndex, -1);
        return id >= 0 ? id : columnIndex;
    }

    private int columnIdToIndex(int columnId) {
        int index = mColumnIdToIndex.get(columnId, -1);
        return index >= 0 ? index : columnId;
    }

    private int rowIndexToId(int rowIndex) {
        int id = getRowsIndexToIdModifications().get(rowIndex, -1);
        return id >= 0 ? id : rowIndex;
    }

    private int rowIdToIndex(int rowId) {
        int index = mRowIdToIndex.get(rowId, -1);
        return index >= 0 ? index : rowId;
    }
}
