package com.cleveroad.library.tlib;

import android.util.Log;
import android.view.View;

/**
 * {@see http://stackoverflow.com/a/6219382/842697 }
 */
class TDragAndDropScrollRunnable implements Runnable {
    private View mView;
    private boolean isFinished = true;

    private int mTouchX;
    private int mTouchY; // for future!!

    private int mDiffX;

    TDragAndDropScrollRunnable(View view) {
        mView = view;
    }

    synchronized void touch(int touchX, int touchY) {
        mTouchX = touchX;
        mTouchY = touchY;
        int partOfWidth = mView.getWidth() / 4;
        if (mTouchX < partOfWidth) {
            start(touchX - partOfWidth);
        } else if (mTouchX > mView.getWidth() - partOfWidth) {
            start(touchX - mView.getWidth() + partOfWidth);
        } else {
            mDiffX = 0;
        }
    }

    synchronized void start(int diffX) {
        mDiffX = diffX;
        if (isFinished) {
            isFinished = false;
            mView.post(this);
        }
    }

    public void run() {
        int shiftDistance = mDiffX / 5;
        if (shiftDistance != 0 && !isFinished) {
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

    public boolean isFinished() {
        return isFinished;
    }
}
