package com.cleveroad.library;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        ItemType.IGNORE,
        ItemType.FIRST_HEADER,
        ItemType.FIXED_ROW,
        ItemType.FIXED_COLUMN,
        ItemType.BODY,
        ItemType.CATEGORY
})
@Retention(RetentionPolicy.SOURCE)
public @interface ItemType {
    /**
     * This item type will not be recycled
     */
    int IGNORE = -1;
    /**
     * Top left header
     */
    int FIRST_HEADER = 0;
    /**
     * Vertical header
     */
    int FIXED_ROW = 1;
    /**
     * Horizontal header
     */
    int FIXED_COLUMN = 2;
    /**
     * Normal scrollable item
     */
    int BODY = 3;
    /**
     * Category divider
     */
    int CATEGORY = 4;

    /**
     * Count of item types
     */
    int TYPES_COUNT = 5;
}
