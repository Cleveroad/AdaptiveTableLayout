package com.cleveroad.library.scroll;

import android.util.Log;

/**
 * {@see http://stackoverflow.com/a/6219382/842697 }
 */
class DragAndDropScrollRunnable implements Runnable {
    private DraggableView mView;
    private ScrollMediatorListener mScrollMediatorListener;
    private boolean isFinished = true;

    private int mTouchX;
    private int mTouchY; // for future!!

    private int mDiffX;

    DragAndDropScrollRunnable(DraggableView view, ScrollMediatorListener scrollMediatorListener) {
        mView = view;
        mScrollMediatorListener = scrollMediatorListener;
    }

    synchronized void touch(int touchX, int touchY) {
        mTouchX = touchX;
        mTouchY = touchY;
        int partOfWidth = mScrollMediatorListener.getLayoutWidth() / 4;
        if (mTouchX < partOfWidth) {
            start(touchX - partOfWidth);
        } else if (mTouchX > mScrollMediatorListener.getLayoutWidth() - partOfWidth) {
            start(touchX - mScrollMediatorListener.getLayoutWidth() + partOfWidth);
        } else {
            mDiffX = 0;
        }
    }

    synchronized void start(int diffX) {
        mDiffX = diffX;
        if (isFinished) {
            isFinished = false;
            Log.e("Runnable", "start " + diffX);
            mView.post(this);
        }
    }

    public void run() {
        int shiftDistance = mDiffX / 5;
        if (shiftDistance != 0) {
            isFinished = false;
            mView.scrollBy(shiftDistance, 0);
            mView.post(this);
        } else {
            stop();
        }
    }

    synchronized void stop() {
        mDiffX = 0;
        mView.removeCallbacks(this);
        isFinished = true;
    }
}
