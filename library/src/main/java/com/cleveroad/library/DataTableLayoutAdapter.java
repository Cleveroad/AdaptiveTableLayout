package com.cleveroad.library;

import android.os.Bundle;
import android.support.annotation.NonNull;

interface DataTableLayoutAdapter<VH extends ViewHolder> extends TableAdapter<VH> {
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
     * @param rowIndex   row from
     * @param rowToIndex row to
     */
    void changeRows(int rowIndex, int rowToIndex);

    void onSaveInstanceState(@NonNull Bundle bundle);

    void onRestoreInstanceState(@NonNull Bundle bundle);
}
