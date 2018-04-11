package com.cleveroad.adaptivetablelayout;

import android.support.annotation.NonNull;
import android.util.SparseIntArray;

public interface ModificationsHolder {

    /**
     * Method calls when need to need to switch 2 columns with each other in the data matrix
     *
     * @param columnIndex   column from
     * @param columnToIndex column to
     */
    void changeColumns(int columnIndex, int columnToIndex);

    /**
     * Method calls when need to need to switch 2 rows with each other in the data matrix
     *
     * @param rowIndex       row from
     * @param rowToIndex     row to
     * @param solidRowHeader fixed to the data or to the row number.
     */
    void changeRows(int rowIndex, int rowToIndex, boolean solidRowHeader);

    @NonNull
    SparseIntArray getRowsIndexToIdModifications();

    @NonNull
    SparseIntArray getColumnsIndexToIdModifications();

    void setRowsModifications(@NonNull SparseIntArray mod);

    void setColumnsModifications(@NonNull SparseIntArray mod);
}
