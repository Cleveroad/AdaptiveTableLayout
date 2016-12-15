package com.cleveroad.library;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;


class ScrollHelper implements GestureDetector.OnGestureListener {
    static final String TAG = "ScrollHelper";
    private final GestureDetectorCompat mGestureDetectorCompat;
    private final GestureDetectorCompat mGestureDetectorLongPressCompat;
    private boolean isDragging = false;
    @Nullable
    private ScrollHelperListener mListener;

    ScrollHelper(Context context) {
        mGestureDetectorCompat = new GestureDetectorCompat(context, this);
        mGestureDetectorLongPressCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                if (mListener != null) {
                    isDragging = mListener.onLongPress(e);
                }
            }
        });
        mGestureDetectorCompat.setIsLongpressEnabled(false);
        mGestureDetectorLongPressCompat.setIsLongpressEnabled(true);
    }

    void setListener(@Nullable ScrollHelperListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return mListener == null || mListener.onDown(e);
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return mListener != null && mListener.onSingleTapUp(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return mListener != null && mListener.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.e(TAG, "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return mListener == null || mListener.onFling(e1, e2, velocityX, velocityY);
    }

    boolean onTouch(MotionEvent event) {
        mGestureDetectorLongPressCompat.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            isDragging = false;
            if (mListener != null) {
                mListener.onActionUp(event);
            }
        }

        return mGestureDetectorCompat.onTouchEvent(event);
    }


    boolean isDragging() {
        return isDragging;
    }

    interface ScrollHelperListener {

        boolean onDown(MotionEvent e);

        boolean onSingleTapUp(MotionEvent e);

        boolean onLongPress(MotionEvent e);

        boolean onActionUp(MotionEvent e);

        boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
    }
}
