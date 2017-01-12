package com.cleveroad.tablelayout.datasource;

import android.support.annotation.NonNull;

import com.cleveroad.tablelayout.model.ArtistModel;

import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

public class ArtistDataSourceWrapper implements ArtistDataSource {
    private final TableDataSource<String, String, String, String> mDataSource;
    private Map<Integer, ArtistModel> mItems = new WeakHashMap<>();

    public ArtistDataSourceWrapper(TableDataSource<String, String, String, String> dataSource) {
        mDataSource = dataSource;
    }

    @Override
    public String getColumnHeader(int index) {
        return mDataSource.getColumnHeaderData(index);
    }

    @Override
    public ArtistModel getArtist(int index) {
        ArtistModel artistModel = mItems.get(index);
        if (artistModel == null) {
            artistModel = new ArtistModel(
                    mDataSource.getItemData(index, 0),
                    mDataSource.getItemData(index, 1),
                    mDataSource.getItemData(index, 2),
                    Arrays.asList(mDataSource.getItemData(index, 3).split(";"))
            );
            mItems.put(index, artistModel);
        }

        return artistModel;
    }

    @Override
    public int getRowsCount() {
        return mDataSource.getRowsCount();
    }

    @Override
    public int getColumnsCount() {
        return mDataSource.getColumnsCount();
    }

    @Override
    public void updateRow(int rowIndex, @NonNull ArtistModel artistModel) {
        mItems.put(rowIndex, artistModel);
    }
}
