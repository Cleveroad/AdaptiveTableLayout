package com.cleveroad.library;

import android.graphics.Point;

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

    public Point getStart() {
        return mStart;
    }

    Point getOffset() {
        return mOffset;
    }

    public Point getEnd() {
        return mEnd;
    }

    public void setStart(int x, int y) {
        mStart.set(x, y);
    }

    public void setOffset(int x, int y) {
        mOffset.set(x, y);
    }

    public void setEnd(int x, int y) {
        mEnd.set(x, y);
    }

}
