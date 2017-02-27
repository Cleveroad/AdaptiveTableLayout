package com.cleveroad.sample.datasource;

public interface TableDataSource<TFirstHeaderDataType, TRowHeaderDataType, TColumnHeaderDataType, TItemDataType> {

    int getRowsCount();

    int getColumnsCount();

    TFirstHeaderDataType getFirstHeaderData();

    TRowHeaderDataType getRowHeaderData(int index);

    TColumnHeaderDataType getColumnHeaderData(int index);

    TItemDataType getItemData(int rowIndex, int columnIndex);

}
