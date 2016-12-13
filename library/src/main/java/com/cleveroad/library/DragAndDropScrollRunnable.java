package com.cleveroad.library;

import android.view.View;

/**
 * {@see http://stackoverflow.com/a/6219382/842697 }
 */
class DragAndDropScrollRunnable implements Runnable {
    static final int ORIENTATION_VERTICAL = 0;
    static final int ORIENTATION_HORIZONTAL = 1;
    private View mView;
    private boolean isFinished = true;

    private int mDiffX;
    private int mDiffY;

    DragAndDropScrollRunnable(View view) {
        mView = view;
    }

    synchronized void touch(int touchX, int touchY, int orientation) {

        int partOfWidth = mView.getWidth() / 4;
        int partOfHeight = mView.getHeight() / 4;


        if (orientation == ORIENTATION_HORIZONTAL) {
            if (touchX < partOfWidth) {
                start(touchX - partOfWidth, 0);
            } else if (touchX > mView.getWidth() - partOfWidth) {
                start(touchX - mView.getWidth() + partOfWidth, 0);
            } else {
                mDiffX = 0;
                mDiffY = 0;
            }
        } else if (orientation == ORIENTATION_VERTICAL) {
            if (touchY < partOfHeight) {
                start(0, touchY - partOfHeight);
            } else if (touchY > mView.getHeight() - partOfHeight) {
                start(0, touchY - mView.getHeight() + partOfHeight);
            } else {
                mDiffX = 0;
                mDiffY = 0;
            }
        }


    }

    private synchronized void start(int diffX, int diffY) {
        mDiffX = diffX;
        mDiffY = diffY;
        if (isFinished) {
            isFinished = false;
            mView.post(this);
        }
    }

    @Override
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

    boolean isFinished() {
        return isFinished;
    }
}
