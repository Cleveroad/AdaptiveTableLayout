package com.cleveroad.library.tlib;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class TSparseMatrix<TObj> {
    private final HashMap<Integer, HashMap<Integer, TObj>> mData = new HashMap<>();

    public void put(int row, int column, TObj item) {
        HashMap<Integer, TObj> map = mData.get(row);
        if (map == null) {
            map = new HashMap<>();
            map.put(column, item);
            mData.put(row, map);
        } else {
            map.put(column, item);
        }
    }

    @Nullable
    public TObj get(int row, int column) {
        HashMap<Integer, TObj> map = mData.get(row);
        if (map == null) {
            return null;
        } else {
            return map.get(column);
        }
    }

    @Nullable
    public Collection<Integer> getRowKeys() {
        return mData.keySet();
    }

    @NonNull
    public Collection<TObj> getRowItems(int row) {
        Collection<TObj> result = new LinkedList<>();
        HashMap<Integer, TObj> map = mData.get(row);
        if (map != null) {
            result.addAll(map.values());
        }
        return result;
    }

    @NonNull
    public Collection<TObj> getColumnItems(int column) {
        Collection<TObj> result = new LinkedList<>();
        Set<Integer> keys = mData.keySet();
        for (int key : keys) {
            TObj columnObj = mData.get(key).get(column);
            if (columnObj != null) {
                result.add(columnObj);
            }
        }
        return result;
    }

    @NonNull
    public Collection<TObj> getAll() {
        Collection<TObj> result = new LinkedList<>();
        Set<Integer> keys = mData.keySet();
        for (int key : keys) {
            Collection<TObj> collection = mData.get(key).values();
            result.addAll(collection);
        }
        return result;
    }

    public void remove(int row, int column) {
        HashMap<Integer, TObj> map = mData.get(row);
        if (map == null) {
            return;
        } else {
            map.remove(column);
        }
    }


}
