package com.cleveroad.library;

import android.support.annotation.Nullable;
import android.util.SparseArray;

public class SparseMatrix<TObj> {
    private final SparseArray<SparseArray<TObj>> mData = new SparseArray<>();

    public void put(int row, int column, TObj item) {
        SparseArray<TObj> map = mData.get(row);
        if (map == null) {
            map = new SparseArray<>();
            map.put(column, item);
            mData.put(row, map);
        } else {
            map.put(column, item);
        }
    }

    @Nullable
    public TObj get(int row, int column) {
        SparseArray<TObj> map = mData.get(row);
        if (map == null) {
            return null;
        } else {
            return map.get(column);
        }
    }

    public void remove(int row, int column) {
        SparseArray<TObj> map = mData.get(row);
        if (map == null) {
            return;
        } else {
            map.remove(column);
        }

    }


}
