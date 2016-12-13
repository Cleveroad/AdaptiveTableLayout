package com.cleveroad.library;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

class SparseMatrix<TObj> {
    private final HashMap<Integer, HashMap<Integer, TObj>> mData = new HashMap<>();

    void put(int row, int column, TObj item) {
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
    TObj get(int row, int column) {
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
    Collection<TObj> getRowItems(int row) {
        Collection<TObj> result = new LinkedList<>();
        HashMap<Integer, TObj> map = mData.get(row);
        if (map != null) {
            result.addAll(map.values());
        }
        return result;
    }

    @NonNull
    Collection<TObj> getColumnItems(int column) {
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
    Collection<TObj> getAll() {
        Collection<TObj> result = new LinkedList<>();
        Set<Integer> keys = mData.keySet();
        for (int key : keys) {
            Collection<TObj> collection = mData.get(key).values();
            result.addAll(collection);
        }
        return result;
    }

    void remove(int row, int column) {
        HashMap<Integer, TObj> map = mData.get(row);
        if (map != null) {
            map.remove(column);
        }
    }


}
