package com.cleveroad.library;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * This is TableAdapter decorator (wrapper).
 * It makes it possible to change the rows and columns without data mutations.
 *
 * @param <VH> Adapter's ViewHolder class
 */
class LinkedTableAdapterImpl<VH extends ViewHolder> extends LinkedTableAdapter<VH> implements DataTableLayoutAdapter<VH> {
    private static final String EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID = "EXTRA_SAVE_STATE_COLUMN_INDEX_TO_ID";
    private static final String EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX = "EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX";
    private static final String EXTRA_SAVE_STATE_ROW_INDEX_TO_ID = "EXTRA_SAVE_STATE_ROW_INDEX_TO_ID";
    private static final String EXTRA_SAVE_STATE_ROW_ID_TO_INDEX = "EXTRA_SAVE_STATE_ROW_ID_TO_INDEX";
    /**
     * Decorated TableAdapter
     */
    private final TableAdapter<VH> mInner;

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

    /**
     * OnItemLongClickListener wrapper
     */
    private final OnItemLongClickListener mOnItemLongClickListenerWrapper = new OnItemLongClickListener() {
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
    };

    /**
     * OnItemClickListener wrapper
     */
    private final OnItemClickListener mOnItemClickListenerWrapper = new OnItemClickListener() {
        @Override
        public void onItemClick(int row, int column) {
            OnItemClickListener innerListener = mInner.getOnItemClickListener();
            if (innerListener != null) {
                innerListener.onItemClick(rowIndexToId(row), columnIndexToId(column));
            }
        }

        @Override
        public void onRowHeaderClick(int row) {
            OnItemClickListener innerListener = mInner.getOnItemClickListener();
            if (innerListener != null) {
                innerListener.onRowHeaderClick(rowIndexToId(row));
            }
        }

        @Override
        public void onColumnHeaderClick(int column) {
            OnItemClickListener innerListener = mInner.getOnItemClickListener();
            if (innerListener != null) {
                innerListener.onColumnHeaderClick(columnIndexToId(column));
            }
        }

        @Override
        public void onLeftTopHeaderClick() {
            OnItemClickListener innerListener = mInner.getOnItemClickListener();
            if (innerListener != null) {
                innerListener.onLeftTopHeaderClick();
            }
        }
    };

    @SuppressLint("UseSparseArrays")
    LinkedTableAdapterImpl(@NonNull TableAdapter<VH> inner, boolean isSolidRowHeader) {
        mInner = inner;
        mIsSolidRowHeader = isSolidRowHeader;

        // init data
        mColumnIndexToId = new HashMap<>();
        mColumnIdToIndex = new HashMap<>();
        mRowIndexToId = new HashMap<>();
        mRowIdToIndex = new HashMap<>();
    }

    @Override
    public void changeColumns(int columnIndex, int columnToIndex) {
        int fromId = columnIndexToId(columnIndex);
        int toId = columnIndexToId(columnToIndex);
        if(columnIndex != toId) {
            mColumnIndexToId.put(columnIndex, toId);
            mColumnIdToIndex.put(toId, columnIndex);
        } else {
            //remove excess modifications
            mColumnIndexToId.remove(columnIndex);
            mColumnIdToIndex.remove(toId);
        }

        if(columnToIndex != fromId) {
            mColumnIndexToId.put(columnToIndex, fromId);
            mColumnIdToIndex.put(fromId, columnToIndex);
        } else {
            //remove excess modifications
            mColumnIndexToId.remove(columnToIndex);
            mColumnIdToIndex.remove(fromId);
        }
    }

    @Override
    public void changeRows(int rowIndex, int rowToIndex, boolean solidRowHeader) {
        mIsSolidRowHeader = solidRowHeader;
        int fromId = rowIndexToId(rowIndex);
        int toId = rowIndexToId(rowToIndex);
        if(rowIndex != toId) {
            mRowIndexToId.put(rowIndex, toId);
            mRowIdToIndex.put(toId, rowIndex);
        } else {
            mRowIndexToId.remove(rowIndex);
            mRowIdToIndex.remove(toId);
        }

        if(rowToIndex != fromId) {
            mRowIndexToId.put(rowToIndex, fromId);
            mRowIdToIndex.put(fromId, rowToIndex);
        } else {
            mRowIndexToId.remove(rowToIndex);
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
        mInner.onBindViewHolder(viewHolder, rowIndexToId(row), columnIndexToId(column));
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull VH viewHolder, int column) {
        mInner.onBindHeaderColumnViewHolder(viewHolder, columnIndexToId(column));
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull VH viewHolder, int row) {
        mInner.onBindHeaderRowViewHolder(viewHolder, mIsSolidRowHeader ? rowIndexToId(row) : row);
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull VH viewHolder) {
        mInner.onBindLeftTopHeaderViewHolder(viewHolder);
    }

    @Override
    public int getColumnWidth(int column) {
        return mInner.getColumnWidth(columnIndexToId(column));
    }

    @Override
    public int getHeaderColumnHeight() {
        return mInner.getHeaderColumnHeight();
    }

    @Override
    public int getRowHeight(int row) {
        return mInner.getRowHeight(rowIndexToId(row));
    }

    @Override
    public int getHeaderRowWidth() {
        return mInner.getHeaderRowWidth();
    }

    @Override
    @Nullable
    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListenerWrapper;
    }

    @Override
    @Nullable
    public OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListenerWrapper;
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
        mColumnIdToIndex = ((HashMap<Integer, Integer>) bundle.getSerializable(EXTRA_SAVE_STATE_COLUMN_ID_TO_INDEX));

        mRowIndexToId = ((HashMap<Integer, Integer>) bundle.getSerializable(EXTRA_SAVE_STATE_ROW_INDEX_TO_ID));
        mRowIdToIndex = ((HashMap<Integer, Integer>) bundle.getSerializable(EXTRA_SAVE_STATE_ROW_ID_TO_INDEX));
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
