package com.cleveroad.library;

import android.graphics.Point;

/**
 * Helps implement dragging feature.
 * Contains start, offset and end point in drag and drop mode.
 */
class DragAndDropPoints {
    /**
     * Start dragging touch point
     */
    private final Point mStart;
    /**
     * Screen offset (touch position)
     */
    private final Point mOffset;

    /**
     * End dragging touch point
     */
    private final Point mEnd;

    DragAndDropPoints() {
        mStart = new Point();
        mOffset = new Point();
        mEnd = new Point();
    }

    Point getStart() {
        return mStart;
    }

    Point getOffset() {
        return mOffset;
    }

    Point getEnd() {
        return mEnd;
    }

    void setStart(int x, int y) {
        mStart.set(x, y);
    }

    void setOffset(int x, int y) {
        mOffset.set(x, y);
    }

    void setEnd(int x, int y) {
        mEnd.set(x, y);
    }

}
