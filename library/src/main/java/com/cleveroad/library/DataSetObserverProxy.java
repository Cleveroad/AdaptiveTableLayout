package com.cleveroad.library;

class DataSetObserverProxy implements TableDataSetObserver {
    private final TableAdapter mTableAdapter;

    DataSetObserverProxy(TableAdapter tableAdapter) {
        mTableAdapter = tableAdapter;
    }

    @Override
    public void notifyDataSetChanged() {
        mTableAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyLayoutChanged() {
        mTableAdapter.notifyLayoutChanged();
    }

    @Override
    public void notifyItemChanged(int rowIndex, int columnIndex) {
        mTableAdapter.notifyItemChanged(rowIndex, columnIndex);
    }

    @Override
    public void notifyRowChanged(int rowIndex) {
        mTableAdapter.notifyRowChanged(rowIndex);
    }

    @Override
    public void notifyColumnChanged(int columnIndex) {
        mTableAdapter.notifyColumnChanged(columnIndex);
    }
}
