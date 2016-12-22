package com.cleveroad.library;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.cleveroad.library.adapter.ViewHolder;

import java.util.Stack;

/**
 * The Recycler facilitates reuse of mViewHolders across layouts.
 */
class Recycler {

    private SparseArray<Stack<ViewHolder>> mViewHolders;

    /**
     * Constructor
     */
    @SuppressWarnings("unchecked")
    Recycler() {
        mViewHolders = new SparseArray<>(3);
    }

    /**
     * Add a view to the Recycler. This view may be reused in the function
     * {@link #popRecycledViewHolder(int)}
     *
     * @param viewHolder A viewHolder to add to the Recycler. It can no longer be used.
     * @param type       the type of the view.
     */
    void pushRecycledView(@NonNull ViewHolder viewHolder, int type) {
        Stack<ViewHolder> stack = mViewHolders.get(type);
        if (stack == null) {
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
    ViewHolder popRecycledViewHolder(int itemType) {
        Stack<ViewHolder> stack = mViewHolders.get(itemType);
        return stack == null || stack.isEmpty() ? null : stack.pop();
    }
}
