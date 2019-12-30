package com.cleveroad.adaptivetablelayout;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;


class ScrollHelper implements GestureDetector.OnGestureListener {
    /**
     * Gesture detector -> Scroll, Fling, Tap, LongPress, ...
     * Using when user need to scroll table
     */
    private final GestureDetectorCompat mGestureDetectorCompat;

    @Nullable
    private ScrollHelperListener mListener;

    ScrollHelper(Context context) {
        mGestureDetectorCompat = new GestureDetectorCompat(context, this);
        mGestureDetectorCompat.setIsLongpressEnabled(true);
    }

    void setListener(@Nullable ScrollHelperListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // catch down action
        return mListener == null || mListener.onDown(e);
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // nothing to do
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // catch click action
        return mListener != null && mListener.onSingleTapUp(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // catch scroll action
        return mListener != null && mListener.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // catch long click action
        if (mListener != null) {
            mListener.onLongPress(e);
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // catch fling action
        return mListener == null || mListener.onFling(e1, e2, velocityX, velocityY);
    }

    boolean onTouch(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP && mListener != null) {
            // stop drag and drop mode
            mListener.onActionUp(event);
        }
        // connect GestureDetector with our touch events
        return mGestureDetectorCompat.onTouchEvent(event);
    }


    interface ScrollHelperListener {

        boolean onDown(MotionEvent e);

        boolean onSingleTapUp(MotionEvent e);

        void onLongPress(MotionEvent e);

        boolean onActionUp(MotionEvent e);

        boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
    }
}
