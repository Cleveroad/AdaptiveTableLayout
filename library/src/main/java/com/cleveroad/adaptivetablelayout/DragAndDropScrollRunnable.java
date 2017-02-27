package com.cleveroad.adaptivetablelayout;

import android.view.View;

/**
 * Move table layout logic in dragging mode
 */
class DragAndDropScrollRunnable implements Runnable {
    private View mView;
    private boolean isFinished;

    private int mDiffX;
    private int mDiffY;

    DragAndDropScrollRunnable(View view) {
        mView = view;
        isFinished = true;
    }

    synchronized void touch(int touchX, int touchY, @ScrollType int orientation) {

        int partOfWidth = mView.getWidth() / 4;
        // vertical scroll area (top, bottom)
        int partOfHeight = mView.getHeight() / 4;


        if (orientation == ScrollType.SCROLL_HORIZONTAL) {
            if (touchX < partOfWidth) {
                // if touch in left horizontal area -> scroll to left
                start(touchX - partOfWidth, 0);
            } else if (touchX > mView.getWidth() - partOfWidth) {
                // if touch in right horizontal area -> scroll to right
                start(touchX - mView.getWidth() + partOfWidth, 0);
            } else {
                // touch between scroll left and right areas.
                mDiffX = 0;
                mDiffY = 0;
            }
        } else if (orientation == ScrollType.SCROLL_VERTICAL) {
            if (touchY < partOfHeight) {
                // if touch in top vertical area -> scroll to top
                start(0, touchY - partOfHeight);
            } else if (touchY > mView.getHeight() - partOfHeight) {
                // if touch in bottom vertical area -> scroll to bottom
                start(0, touchY - mView.getHeight() + partOfHeight);
            } else {
                // touch between scroll top and bottom areas.
                mDiffX = 0;
                mDiffY = 0;
            }
        }
    }

    private synchronized void start(int diffX, int diffY) {
        // save start data
        mDiffX = diffX;
        mDiffY = diffY;
        // check if scrolling in the process
        if (isFinished) {
            // start scroll
            isFinished = false;
            mView.post(this);
        }
    }

    @Override
    public void run() {
        // scroll speed. Calculated by hand.
        int shiftDistanceX = mDiffX / 5;
        int shiftDistanceY = mDiffY / 5;

        if ((shiftDistanceX != 0 || shiftDistanceY != 0) && !isFinished) {
            // change state
            isFinished = false;
            // scroll view
            mView.scrollBy(shiftDistanceX, shiftDistanceY);
            // start self again
            mView.post(this);
        } else {
            // have no shift distance or need to finish.
            stop();
        }
    }

    synchronized void stop() {
        // vars to default
        mDiffX = 0;
        mDiffY = 0;

        // change state
        isFinished = true;

        // remove callbacks
        mView.removeCallbacks(this);
    }

    boolean isFinished() {
        return isFinished;
    }
}
