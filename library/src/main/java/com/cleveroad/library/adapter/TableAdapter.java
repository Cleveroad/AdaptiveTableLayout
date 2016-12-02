package com.cleveroad.library.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.library.ItemType;
import com.cleveroad.library.TableDataSetObserver;
import com.cleveroad.library.TableLayout;

/**
 * The TableAdapter object acts as a bridge between an {@link TableLayout} and the
 * underlying data for that view. The Adapter provides access to the data items.
 * The Adapter is also responsible for making a View for each item in the data
 * set.
 *
 * @see TableLayout
 */
public interface TableAdapter<VH extends TableAdapter.ViewHolder> extends TableDataSetObserver {

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


    void changeColumns(int columnIndex, int columnToIndex);

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
     * Called when {@link TableLayout} needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param itemType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemType(int, int)
     * @see #onBindViewHolder(ViewHolder, int, int)
     */
    @NonNull
    VH onCreateViewHolder(@NonNull ViewGroup parent, int itemType);

    /**
     * Called by {@link TableLayout} to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#getItemView()} to reflect the item at the given
     * position.
     *
     * @param viewHolder The {@link ViewHolder} which should be updated to represent the contents of the
     *                   item at the given position in the data set.
     * @param row        The row index of the item within the adapter's data set.
     * @param column     The column index of the item within the adapter's data set.
     */
    void onBindViewHolder(@NonNull VH viewHolder, int row, int column);

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
     * @param viewHolder The {@link ViewHolder} for the view being recycled
     */
    void onViewHolderRecycled(@NonNull VH viewHolder);

    /**
     * Return the width of the column.
     *
     * @param column the column index. If the column is {@link TableLayout#FIXED_COLUMN_INDEX} it is the header.
     * @return The width of the column, in pixels.
     */
    int getItemWidth(int column);

    /**
     * Return the height of the row.
     *
     * @param row the row index. If the row is {@link TableLayout#FIXED_ROW_INDEX} it is the header.
     * @return The height of the row, in pixels.
     */
    int getItemHeight(int row);

    /**
     * Returns the View type defined by the adapter.
     *
     * @param row    the row index. If the row is {@link TableLayout#FIXED_ROW_INDEX} it is the header.
     * @param column the column index. If the column is {@link TableLayout#FIXED_COLUMN_INDEX} it is the header.
     * @return The type of the view assigned by the adapter.
     * @see ItemType
     */
    @ItemType
    int getItemType(int row, int column);

    /**
     * A {@link ViewHolder} describes an item view and metadata about its place within the {@link TableLayout}.
     */
    interface ViewHolder {
        /**
         * @return item represents the item of the {@link TableLayout}
         */
        @NonNull
        View getItemView();

        /**
         * @return the type of the View
         */
        @ItemType
        int getItemType();

        /**
         * @param itemType is the type of the View
         */
        void setItemType(@ItemType int itemType);

        /**
         * @return the row index. If the row is {@link TableLayout#FIXED_ROW_INDEX} it is the header.
         */
        int getRowIndex();

        /**
         * @param rowIndex the row index. If the row is {@link TableLayout#FIXED_ROW_INDEX} it is the header.
         */
        void setRowIndex(int rowIndex);

        /**
         * @return the column index. If the column is {@link TableLayout#FIXED_COLUMN_INDEX} it is the header.
         */
        int getColumnIndex();

        /**
         * @param columnIndex the column index. If the column is {@link TableLayout#FIXED_COLUMN_INDEX} it is the header.
         */
        void setColumnIndex(int columnIndex);
    }
}
