package com.cleveroad.adaptivetablelayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Custom matrix realisation to hold Objects
 *
 * @param <TObj> Object
 */
class SparseMatrix<TObj> {
    private final SparseArrayCompat<SparseArrayCompat<TObj>> mData;

    SparseMatrix() {
        mData = new SparseArrayCompat<>();
    }

    /**
     * Put item to the matrix in row, column position.
     *
     * @param row    item row position
     * @param column item column  position
     * @param item   Object
     */
    void put(int row, int column, @NonNull TObj item) {
        SparseArrayCompat<TObj> array = mData.get(row);
        if (array == null) {
            array = new SparseArrayCompat<>();
            array.put(column, item);
            mData.put(row, array);
        } else {
            array.put(column, item);
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
        SparseArrayCompat<TObj> array = mData.get(row);
        return array == null ? null : array.get(column);
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
        SparseArrayCompat<TObj> array = mData.get(row);
        for (int count = array.size(), i = 0; i < count; i++) {
            int key = array.keyAt(i);
            TObj columnObj = array.get(key);
            if (columnObj != null) {
                result.add(columnObj);
            }

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
        for (int count = mData.size(), i = 0; i < count; i++) {
            int key = mData.keyAt(i);
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
        for (int countR = mData.size(), i = 0; i < countR; i++) {
            int rowKey = mData.keyAt(i);
            SparseArrayCompat<TObj> columns = mData.get(rowKey);
            for (int countC = columns.size(), j = 0; j < countC; j++) {
                int key = columns.keyAt(j);
                result.add(columns.get(key));
            }
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
        SparseArrayCompat<TObj> array = mData.get(row);
        if (array != null) {
            array.remove(column);
        }
    }
}
