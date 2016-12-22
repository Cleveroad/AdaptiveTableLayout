package com.cleveroad.library;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * Custom matrix realisation to hold Objects
 *
 * @param <TObj> Object
 */
class MapMatrix<TObj> {
    private final HashMap<Integer, HashMap<Integer, TObj>> mData = new HashMap<>();

    /**
     * Put item to the matrix in row, column position.
     *
     * @param row    item row position
     * @param column item column  position
     * @param item   Object
     */
    void put(int row, int column, @NonNull TObj item) {
        HashMap<Integer, TObj> map = mData.get(row);
        if (map == null) {
            map = new HashMap<>();
            map.put(column, item);
            mData.put(row, map);
        } else {
            map.put(column, item);
        }
    }

    /**
     * Get Object from matrix by row and column.
     *
     * @param row    item row position
     * @param column item column position
     * @return Object in row, column position in the matrix
     */
    @Nullable
    TObj get(int row, int column) {
        HashMap<Integer, TObj> map = mData.get(row);
        if (map == null) {
            return null;
        } else {
            return map.get(column);
        }
    }

    /**
     * Get all row's items
     *
     * @param row row index
     * @return Collection with row's Objects
     */
    @NonNull
    Collection<TObj> getRowItems(int row) {
        Collection<TObj> result = new LinkedList<>();
        HashMap<Integer, TObj> map = mData.get(row);
        if (map != null) {
            result.addAll(map.values());
        }
        return result;
    }

    /**
     * Get all column's items
     *
     * @param column column index
     * @return Collection with column's Objects
     */
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

    /**
     * Get all matrix's items
     *
     * @return Collection with column's Objects
     */
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

    /**
     * Remove item in row, column position int the matrix
     *
     * @param row    item row position
     * @param column item column position
     */
    void remove(int row, int column) {
        HashMap<Integer, TObj> map = mData.get(row);
        if (map != null) {
            map.remove(column);
        }
    }


}
