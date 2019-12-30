package com.cleveroad.adaptivetablelayout;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        ViewHolderType.FIRST_HEADER,
        ViewHolderType.ROW_HEADER,
        ViewHolderType.COLUMN_HEADER,
        ViewHolderType.ITEM
})
/**
 * Type of adapter's ViewHolders
 */
@Retention(RetentionPolicy.SOURCE)
@interface ViewHolderType {
    /**
     * Top left header
     */
    int FIRST_HEADER = 0;
    /**
     * Vertical header
     */
    int ROW_HEADER = 1;
    /**
     * Horizontal header
     */
    int COLUMN_HEADER = 2;
    /**
     * Normal scrollable item
     */
    int ITEM = 3;
}
