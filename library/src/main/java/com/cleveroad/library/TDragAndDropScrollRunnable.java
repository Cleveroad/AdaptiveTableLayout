package com.cleveroad.library;

import android.view.View;

/**
 * {@see http://stackoverflow.com/a/6219382/842697 }
 */
class TDragAndDropScrollRunnable implements Runnable {
    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;
    private View mView;
    private boolean isFinished = true;

    private int mTouchX;
    private int mTouchY; // for future!!

    private int mDiffX;
    private int mDiffY;

    TDragAndDropScrollRunnable(View view) {
        mView = view;
    }

    synchronized void touch(int touchX, int touchY, int orientation) {
        mTouchX = touchX;
        mTouchY = touchY;

        int partOfWidth = mView.getWidth() / 4;
        int partOfHeight = mView.getHeight() / 4;


        if (orientation == ORIENTATION_HORIZONTAL) {
            if (mTouchX < partOfWidth) {
                start(touchX - partOfWidth, 0);
            } else if (mTouchX > mView.getWidth() - partOfWidth) {
                start(touchX - mView.getWidth() + partOfWidth, 0);
            } else {
                mDiffX = 0;
                mDiffY = 0;
            }
        } else if (orientation == ORIENTATION_VERTICAL) {
            if (mTouchY < partOfHeight) {
                start(0, touchY - partOfHeight);
            } else if (mTouchY > mView.getHeight() - partOfHeight) {
                start(0, touchY - mView.getHeight() + partOfHeight);
            } else {
                mDiffX = 0;
                mDiffY = 0;
            }
        }


    }

    synchronized void start(int diffX, int diffY) {
        mDiffX = diffX;
        mDiffY = diffY;
        if (isFinished) {
            isFinished = false;
            mView.post(this);
        }
    }

    public void run() {
        int shiftDistanceX = mDiffX / 5;
        int shiftDistanceY = mDiffY / 5;
        if ((shiftDistanceX != 0 || shiftDistanceY != 0) && !isFinished) {
            isFinished = false;
            mView.scrollBy(shiftDistanceX, shiftDistanceY);
            mView.post(this);
        } else {
            stop();
        }
    }

    synchronized void stop() {
        mDiffX = 0;
        mDiffY = 0;
        mView.removeCallbacks(this);
        isFinished = true;
    }

    public boolean isFinished() {
        return isFinished;
    }
}
