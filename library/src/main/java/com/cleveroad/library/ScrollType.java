package com.cleveroad.library;

import android.support.annotation.IntDef;

import static com.cleveroad.library.ScrollType.SCROLL_HORIZONTAL;
import static com.cleveroad.library.ScrollType.SCROLL_VERTICAL;

@IntDef({
        SCROLL_HORIZONTAL,
        SCROLL_VERTICAL
})
/**
 * Scroll type for drag and drop mode.
 */
@interface ScrollType {
    int SCROLL_HORIZONTAL = 0;
    int SCROLL_VERTICAL = 1;
}
