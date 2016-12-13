package com.cleveroad.library;

import android.graphics.Point;

public class DragAndDropPoints {
    private final Point mStart = new Point();
    private final Point mOffset = new Point();
    private final Point mEnd = new Point();

    public Point getStart() {
        return mStart;
    }

    public Point getOffset() {
        return mOffset;
    }

    public Point getEnd() {
        return mEnd;
    }

    public void setStart(int x, int y) {
        mStart.set(x, y);
    }

    public void setOffset(int x, int y) {
        mOffset.x = x;
        mOffset.y = y;
    }

    public void setEnd(int x, int y) {
        mEnd.x = x;
        mEnd.y = y;
    }

}
