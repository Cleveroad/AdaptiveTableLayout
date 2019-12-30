package com.cleveroad.adaptivetablelayout;

import android.os.Bundle;
import androidx.annotation.NonNull;

interface DataAdaptiveTableLayoutAdapter<VH extends ViewHolder> extends AdaptiveTableAdapter<VH> {
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

    void onSaveInstanceState(@NonNull Bundle bundle);

    void onRestoreInstanceState(@NonNull Bundle bundle);
}
