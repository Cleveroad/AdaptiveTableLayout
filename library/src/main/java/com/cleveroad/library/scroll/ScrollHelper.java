package com.cleveroad.library.scroll;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.cleveroad.library.TableLayoutSettings;

import java.util.Timer;
import java.util.TimerTask;

class ScrollHelper {
    private VelocityTracker mVelocityTracker;
    private SmoothScrollRunnable mSmoothScrollRunnable;
    private int mCurrentX;
    private int mCurrentY;
    private Timer mTimer;
    private ScrollableView mScrollableView;
    private ScrollMediatorListener mScrollMediatorListener;

    // flag show us if user moved screen in this tap "session"
    private boolean wasMoved = false;

    @NonNull
    private ScrollHelperListener mListener;
    private int mActualScrollX;
    private int mActualScrollY;

    @SuppressWarnings("all")
    ScrollHelper(@NonNull ScrollMediatorListener scrollMediatorListener,
                 @NonNull ScrollableView scrollableView,
                 @NonNull ScrollHelperListener listener) {
        if (scrollMediatorListener == null) {
            throw new IllegalStateException("ScrollMediatorListener is null!!");
        }

        if (scrollableView == null) {
            throw new IllegalStateException("ScrollableView is null!!");
        }

        if (listener == null) {
            throw new IllegalStateException("ScrollHelperListener is null!!");
        }
        mScrollMediatorListener = scrollMediatorListener;
        mScrollableView = scrollableView;
        mListener = listener;
        mSmoothScrollRunnable = new SmoothScrollRunnable(mScrollMediatorListener.getContext(), scrollableView);
    }

    boolean onTouchEvent(MotionEvent event, int actualScrollX, int actualScrollY) {
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
            mActualScrollX = actualScrollX;
            mActualScrollY = actualScrollY;
            actionUp(event);
            resetLongClickTimer();
        } else {
            resetLongClickTimer();
        }
        return true;
    }

    private void actionDown(MotionEvent event) {
        resetLongClickTimer();
        mCurrentX = (int) event.getRawX();
        mCurrentY = (int) event.getRawY();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO LONG CLICK!!!
                Log.e("ScrollHelper", "mCurrentX = " + mCurrentX + " mCurrentY = " + mCurrentY);
                mScrollableView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onLongTouch(mCurrentX, mCurrentY);
                    }
                });

            }
        }, TableLayoutSettings.LONG_PRESS_DELAY);

        if (!mSmoothScrollRunnable.isFinished()) {
            mSmoothScrollRunnable.forceFinished();
        }
    }

    private void actionMove(MotionEvent event) {
        int x2 = Math.abs(mCurrentX - (int) event.getRawX());
        int y2 = Math.abs(mCurrentY - (int) event.getRawY());
        int touchSlop = mScrollMediatorListener.getTouchSlop();
        if (!wasMoved && (x2 < touchSlop && y2 < touchSlop)) {
            // long click detecting. Need to wait...
        } else {
            resetLongClickTimer();
            wasMoved = true;
            x2 = (int) event.getRawX();
            y2 = (int) event.getRawY();
            final int diffX = mCurrentX - x2;
            final int diffY = mCurrentY - y2;
            mCurrentX = x2;
            mCurrentY = y2;
            mScrollableView.scrollBy(diffX, diffY);
        }
    }

    private void actionUp(MotionEvent event) {
        resetLongClickTimer();
        mVelocityTracker.computeCurrentVelocity(1000, mScrollMediatorListener.getMaxVelocity());
        int velocityX = (int) mVelocityTracker.getXVelocity();
        int velocityY = (int) mVelocityTracker.getYVelocity();

        if (Math.abs(velocityX) > mScrollMediatorListener.getMinVelocity() ||
                Math.abs(velocityY) > mScrollMediatorListener.getMinVelocity()) {
            // need to smooth scroll end.
            mSmoothScrollRunnable.start(mActualScrollX, mActualScrollY,
                    velocityX, velocityY, mScrollMediatorListener.getMaxScrollX(), mScrollMediatorListener.getMaxScrollY());
        } else {
            if (mVelocityTracker != null) { // If the velocity less than threshold
                mVelocityTracker.recycle(); // recycle the tracker
                mVelocityTracker = null; // DO NOT REMOVE THIS. Fix issue with IllegalStateException: Already in the pool!
            }
        }
        wasMoved = false;
    }


    void resetLongClickTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
    }

    interface ScrollHelperListener {
        void onLongTouch(int x, int y);
    }

}
