package com.cleveroad.library;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import java.util.Timer;
import java.util.TimerTask;

public class ScrollHelper {


    private final TableLayout mTableLayout;
    private VelocityTracker mVelocityTracker;
    private SmoothScrollRunnable mSmoothScrollRunnable;
    private int mCurrentX;
    private int mCurrentY;
    private Timer mTimer;

    // flag show us if user moved screen in this tap "session"
    private boolean wasMoved = false;

    // flag show us if user started dragging
    private boolean isDragging = false;

    @Nullable
    private ScrollHelperListener mListener;

    ScrollHelper(TableLayout tableLayout) {
        mTableLayout = tableLayout;
        mSmoothScrollRunnable = new SmoothScrollRunnable(mTableLayout);
    }

    public ScrollHelperListener getListener() {
        return mListener;
    }

    public void setListener(ScrollHelperListener listener) {
        mListener = listener;
    }

    boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            actionDown(event);
        } else if (action == MotionEvent.ACTION_MOVE) {
            actionMove(event);
        } else if (action == MotionEvent.ACTION_UP) {
            actionUp(event);
            resetLongClickTimer();
        } else {
            resetLongClickTimer();
        }
        return true;
    }


    private void actionDown(MotionEvent event) {
        resetLongClickTimer();
        Log.e("ScrollHelper", "isDragging = false");
        isDragging = false;
        mCurrentX = (int) event.getRawX();
        mCurrentY = (int) event.getRawY();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("ScrollHelper", "isDragging = true");
                isDragging = true;
                // TODO LONG CLICK!!!
                Log.e("ScrollHelper", "mCurrentX = " + mCurrentX + " mCurrentY = " + mCurrentY);
                if (mListener != null) {
                    mTableLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onDragAndDropStart(mCurrentX, mCurrentY);
                        }
                    });

                }
            }
        }, TableLayoutSettings.LONG_PRESS_DELAY);

        if (!mSmoothScrollRunnable.isFinished()) {
            mSmoothScrollRunnable.forceFinished();
        }

    }

    private void actionMove(MotionEvent event) {
        int x2 = Math.abs(mCurrentX - (int) event.getRawX());
        int y2 = Math.abs(mCurrentY - (int) event.getRawY());
        int touchSlop = mTableLayout.getSettings().getTouchSlop();
        if (!wasMoved && (x2 < touchSlop && y2 < touchSlop)) {
            // long click detecting
        } else {
            if (isDragging) {
                //TODO Implement dragging here!!
                Log.e("ScrollHelper", "x2 = " + x2 + " y2 = " + y2);
                if (mListener != null) {
                    mListener.onDragAndDropScroll(x2, y2);
                }
            } else {
                resetLongClickTimer();
            }
            Log.e("ScrollHelper", "wasMoved = true");
            wasMoved = true;
            x2 = (int) event.getRawX();
            y2 = (int) event.getRawY();
            final int diffX = mCurrentX - x2;
            final int diffY = mCurrentY - y2;
            mCurrentX = x2;
            mCurrentY = y2;
            mTableLayout.scrollBy(diffX, diffY);
        }
    }

    private void actionUp(MotionEvent event) {
        if (isDragging) {
            if (mListener != null) {
                mListener.onDragAndDropEnd(mCurrentX, mCurrentY);
            }
        }
        resetLongClickTimer();
        Log.e("ScrollHelper", "isDragging = false");
        isDragging = false;
        mVelocityTracker.computeCurrentVelocity(1000, mTableLayout.getSettings().getMaximumVelocity());
        int velocityX = (int) mVelocityTracker.getXVelocity();
        int velocityY = (int) mVelocityTracker.getYVelocity();

        if (Math.abs(velocityX) > mTableLayout.getSettings().getMinimumVelocity() || Math.abs(velocityY) > mTableLayout.getSettings().getMinimumVelocity()) {
            // need to smooth scroll end.
            mSmoothScrollRunnable.start(mTableLayout.getActualScrollX(), mTableLayout.getActualScrollY(), velocityX, velocityY, mTableLayout.getMaxScrollX(), mTableLayout.getMaxScrollY());
        } else {
            if (mVelocityTracker != null) { // If the velocity less than threshold
                mVelocityTracker.recycle(); // recycle the tracker
                mVelocityTracker = null; // DO NOT REMOVE THIS. Fix issue with IllegalStateException: Already in the pool!
            }
        }
        Log.e("ScrollHelper", "wasMoved = false");
        wasMoved = false;
    }


    void resetLongClickTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
    }

    public boolean isDragging() {
        return isDragging;
    }

    interface ScrollHelperListener {
        void onDragAndDropStart(int x, int y);

        void onDragAndDropScroll(int x, int y);

        void onDragAndDropEnd(int x, int y);
    }

}
