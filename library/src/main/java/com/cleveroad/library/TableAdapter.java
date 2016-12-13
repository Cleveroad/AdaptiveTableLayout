package com.cleveroad.library;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;


public interface TableAdapter<VH extends TableAdapter.TViewHolder> extends TableDataSetObserver {
    /**
     * Register an observer that is called when changes happen to the data used
     * by this adapter.
     *
     * @param observer the object that gets notified when the data set changes.
     */
    void registerDataSetObserver(@NonNull TableDataSetObserver observer);

    /**
     * Unregister an observer that has previously been registered with this
     * adapter via {@link #registerDataSetObserver}.
     *
     * @param observer the object to unregister.
     */
    void unregisterDataSetObserver(@NonNull TableDataSetObserver observer);


    /**
     * How many rows are in the data table represented by this Adapter.
     *
     * @return count of rows.
     */
    int getRowCount();

    /**
     * How many columns are in the data table represented by this Adapter.
     *
     * @return count of columns.
     */
    int getColumnCount();

    /**
     * Called when {@link TableLayout} needs a new {@link TableAdapter.TViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(TableAdapter.TViewHolder, int, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param itemType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #onBindViewHolder(TableAdapter.TViewHolder, int, int)
     */
    @NonNull
    VH onCreateViewHolder(@NonNull ViewGroup parent, int itemType);


    @NonNull
    VH onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent);

    @NonNull
    VH onCreateRowHeaderViewHolder(@NonNull ViewGroup parent);

    /**
     * Called by {@link TableLayout} to display the data at the specified position. This method should
     * update the contents of the {@link TableAdapter.TViewHolder#getItemView()} to reflect the item at the given
     * position.
     *
     * @param viewHolder The {@link TableAdapter.TViewHolder} which should be updated to represent the contents of the
     *                   item at the given position in the data set.
     * @param row        The row index of the item within the adapter's data set.
     * @param column     The column index of the item within the adapter's data set.
     */
    void onBindViewHolder(@NonNull VH viewHolder, int row, int column);

    void onBindHeaderColumnViewHolder(@NonNull VH viewHolder, int column);

    void onBindHeaderRowViewHolder(@NonNull VH viewHolder, int row);

    /**
     * Called when a view created by this adapter has been recycled.
     * <p>
     * <p>A view is recycled when a {@link TableLayout} decides that it no longer
     * needs to be attached. This can be because it has
     * fallen out of visibility or a set of cached views represented by views still
     * attached to the parent RecyclerView. If an item view has large or expensive data
     * bound to it such as large bitmaps, this may be a good place to release those
     * resources.</p>
     * <p>
     * {@link TableLayout} calls this method right before clearing ViewHolder's internal data and
     * sending it to Recycler.
     *
     * @param viewHolder The {@link TableAdapter.TViewHolder} for the view being recycled
     */
    void onViewHolderRecycled(@NonNull VH viewHolder);

    /**
     * Return the width of the column.
     *
     * @param column the column index.
     * @return The width of the column, in pixels.
     */
    int getColumnWidth(int column);

    int getHeaderColumnHeight();

    /**
     * Return the height of the row.
     *
     * @param row the row index.
     * @return The height of the row, in pixels.
     */
    int getRowHeight(int row);

    int getHeaderRowWidth();

    /**
     * A {@link TableAdapter.TViewHolder} describes an item view and metadata about its place within the {@link TableLayout}.
     */
    interface TViewHolder {
        /**
         * @return item represents the item of the {@link TableLayout}
         */
        @NonNull
        View getItemView();

        /**
         * @return the type of the View
         */
        int getItemType();

        /**
         * @param itemType is the type of the View
         */
        void setItemType(int itemType);

        /**
         * @return the row index.
         */
        int getRowIndex();

        /**
         * @param rowIndex the row index.
         * @return the row index.
         * int getRowIndex();
         * <p>
         * /**
         */
        void setRowIndex(int rowIndex);

        /**
         * @return the column index.
         */
        int getColumnIndex();

        /**
         * @param columnIndex the column index.
         */
        void setColumnIndex(int columnIndex);

        boolean isDragging();

        void setIsDragging(boolean isDragging);
    }
}
