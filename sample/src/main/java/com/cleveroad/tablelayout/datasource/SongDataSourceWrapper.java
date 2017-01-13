package com.cleveroad.tablelayout.datasource;

import android.support.annotation.NonNull;

import com.cleveroad.tablelayout.model.SongModel;

import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

public class SongDataSourceWrapper implements SongDataSource {
    private static final String SONG_GENRES_DELIMITER = ";";
    private final TableDataSource<String, String, String, String> mDataSource;
    private Map<Integer, SongModel> mItems = new WeakHashMap<>();

    public SongDataSourceWrapper(TableDataSource<String, String, String, String> dataSource) {
        mDataSource = dataSource;
    }

    @Override
    public String getColumnHeader(int index) {
        return mDataSource.getColumnHeaderData(index);
    }

    @Override
    public SongModel getSong(int index) {
        SongModel artistModel = mItems.get(index);
        if (artistModel == null) {
            int columnNumber = 0;
            artistModel = new SongModel(
                    mDataSource.getItemData(index, columnNumber++),
                    mDataSource.getItemData(index, columnNumber++),
                    mDataSource.getItemData(index, columnNumber++),
                    mDataSource.getItemData(index, columnNumber++),
                    mDataSource.getItemData(index, columnNumber++),
                    Arrays.asList(mDataSource.getItemData(index, columnNumber++).split(SONG_GENRES_DELIMITER)),
                    Integer.parseInt(mDataSource.getItemData(index, columnNumber++)),
                    Long.parseLong(mDataSource.getItemData(index, columnNumber))
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
    public void updateRow(int rowIndex, @NonNull SongModel songModel) {
        mItems.put(rowIndex, songModel);
    }
}
