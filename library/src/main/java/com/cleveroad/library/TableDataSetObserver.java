package com.cleveroad.library;

interface TableDataSetObserver {
    /**
     * Notify any registered observers that the data set has changed.
     * If you change the size of the data set, you must call {@link TableDataSetObserver#notifyLayoutChanged()} instead
     */
    void notifyDataSetChanged();

    /**
     * You must call this when something has changed which has invalidated the layout of this view
     * or the size of the data set was changed
     */
    void notifyLayoutChanged();

    /**
     * Notify any registered observers that the item at
     * (<code>rowIndex</code>;<code>columnIndex</code>) has changed.
     *
     * @param rowIndex    the row index
     * @param columnIndex the column index
     */
    void notifyItemChanged(int rowIndex, int columnIndex);

    /**
     * Notify any registered observers that the row with rowIndex has changed.
     *
     * @param rowIndex the row index
     */
    void notifyRowChanged(int rowIndex);

    /**
     * Notify any registered observers that the column with columnIndex has changed.
     *
     * @param columnIndex the column index
     */
    void notifyColumnChanged(int columnIndex);


}
