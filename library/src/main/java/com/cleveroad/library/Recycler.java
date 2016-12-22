package com.cleveroad.library;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The Recycler facilitates reuse of mViewHolders across layouts.
 */
class Recycler {

    private SparseArray<Deque<ViewHolder>> mViewHolders;

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
        Deque<ViewHolder> deque = mViewHolders.get(type);
        if (deque == null) {
            deque = new ArrayDeque<>();
            mViewHolders.put(type, deque);
        }
        deque.push(viewHolder);
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
        Deque<ViewHolder> deque = mViewHolders.get(itemType);
        return deque == null || deque.isEmpty() ? null : deque.pop();
    }
}
