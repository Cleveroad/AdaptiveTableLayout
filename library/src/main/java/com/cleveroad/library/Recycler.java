package com.cleveroad.library;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.Stack;

/**
 * The Recycler facilitates reuse of mViewHolders across layouts.
 */
class Recycler {

    private SparseArray<Stack<TableAdapter.ViewHolder>> mViewHolders;

    /**
     * Constructor
     *
     */
    @SuppressWarnings("unchecked")
    Recycler() {
        mViewHolders = new SparseArray<>(ItemType.TYPES_COUNT);
    }

    /**
     * Add a view to the Recycler. This view may be reused in the function
     * {@link #popRecycledViewHolder(int)}
     *
     * @param viewHolder A viewHolder to add to the Recycler. It can no longer be used.
     * @param type       the type of the view.
     */
    void pushRecycledView(@NonNull TableAdapter.ViewHolder viewHolder, int type) {
        Stack<TableAdapter.ViewHolder> stack = mViewHolders.get(type);
        if(stack == null) {
            stack = new Stack<>();
            mViewHolders.put(type, stack);
        }
        stack.push(viewHolder);
    }

    /**
     * Returns, if exists, a view of the type <code>typeView</code>.
     *
     * @param itemType the type of view that you want.
     * @return a viewHolder of the type <code>typeView</code>. <code>null</code> if
     * not found.
     */
    @Nullable
    TableAdapter.ViewHolder popRecycledViewHolder(@ItemType int itemType) {
        Stack<TableAdapter.ViewHolder> stack = mViewHolders.get(itemType);
        return stack == null || stack.isEmpty() ? null : stack.pop();
    }
}
