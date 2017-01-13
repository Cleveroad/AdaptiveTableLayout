package com.cleveroad.tablelayout.datasource;

import android.support.annotation.NonNull;

import com.cleveroad.tablelayout.model.SongModel;

public interface SongDataSource {
    String getColumnHeader(int index);

    SongModel getSong(int index);

    int getRowsCount();

    int getColumnsCount();

    void updateRow(int rowIndex, @NonNull SongModel songModel);
}
