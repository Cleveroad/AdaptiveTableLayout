package com.cleveroad.tablelayout.datasource;

import android.support.annotation.NonNull;

import com.cleveroad.tablelayout.model.ArtistModel;

public interface ArtistDataSource {
    String getColumnHeader(int index);

    ArtistModel getArtist(int index);

    int getRowsCount();

    int getColumnsCount();

    void updateRow(int rowIndex, @NonNull ArtistModel artistModel);
}
