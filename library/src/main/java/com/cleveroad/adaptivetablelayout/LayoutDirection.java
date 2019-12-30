package com.cleveroad.adaptivetablelayout;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        LayoutDirection.LTR,
        LayoutDirection.RTL
})
/**
 * Type of layout directions. Same values as in android.util.LayoutDirection
 * This interface needed because project min API is 16
 */
@Retention(RetentionPolicy.SOURCE)
@interface LayoutDirection {
    /**
     * Top left header
     */
    int LTR = 0;
    /**
     * Vertical header
     */
    int RTL = 1;
}
