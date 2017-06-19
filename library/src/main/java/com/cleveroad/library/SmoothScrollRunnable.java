package com.cleveroad.library;

import android.view.View;
import android.widget.Scroller;

/**
 * Fling table layout logic
 * {@see http://stackoverflow.com/a/6219382/842697 }
 */
class SmoothScrollRunnable implements Runnable {
    /**
     * Scrollable view
     */
    private final View mView;
    /**
     * Need to calculate offset.
     */
    private Scroller mScroller;

    private int mLastX;
    private int mLastY;

    SmoothScrollRunnable(View view) {
        mView = view;
        mScroller = new Scroller(view.getContext());
    }

    void start(int initX, int initY, int initialVelocityX, int initialVelocityY, int maxX, int maxY) {
        // start smooth scrolling
        mScroller.fling(initX, initY, initialVelocityX, initialVelocityY, 0, maxX, 0, maxY);

        // save new data
        mLastX = initX;
        mLastY = initY;

        // run self
        mView.post(this);
    }

    @Override
    public void run() {
        if (mScroller.isFinished()) {
            return;
        }
        // calculate offset
        boolean more = mScroller.computeScrollOffset();
        int x = mScroller.getCurrX();
        int y = mScroller.getCurrY();
        int diffX = mLastX - x;
        int diffY = mLastY - y;
        if (diffX != 0 || diffY != 0) {
            mView.scrollBy(diffX, diffY);
            mLastX = x;
            mLastY = y;
        }

        if (more) {
            // run self
            mView.post(this);
        }
    }

    boolean isFinished() {
        return mScroller.isFinished();
    }

    void forceFinished() {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
    }
}
