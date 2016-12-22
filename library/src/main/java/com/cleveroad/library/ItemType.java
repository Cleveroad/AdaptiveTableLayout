package com.cleveroad.library;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        ItemType.FIRST_HEADER,
        ItemType.ROW_HEADER,
        ItemType.COLUMN_HEADER,
        ItemType.ITEM
})
@Retention(RetentionPolicy.SOURCE)
public @interface ItemType {
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
