package com.cleveroad.library.tlib;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;


public class TScrollHelper implements GestureDetector.OnGestureListener {
    static final String TAG = "TScrollHelper";
    private final Context mContext;
    private final GestureDetectorCompat mGestureDetectorCompat;
    private final GestureDetectorCompat mGestureDetectorLongPressCompat;
    private boolean isDragging = false;
    @Nullable
    private ScrollHelperListener mListener;

    public TScrollHelper(Context context) {
        mContext = context;
        mGestureDetectorCompat = new GestureDetectorCompat(mContext, this);
        mGestureDetectorLongPressCompat = new GestureDetectorCompat(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                Log.e(TAG, "onLongPress");
                if (mListener != null) {
                    isDragging = mListener.onLongPress(e);
                }
            }
        });
        mGestureDetectorCompat.setIsLongpressEnabled(false);
        mGestureDetectorLongPressCompat.setIsLongpressEnabled(true);
    }

    public void setListener(@Nullable ScrollHelperListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (mListener != null) {
            return mListener.onSingleTapUp(e);
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mListener != null) {
            return mListener.onScroll(e1, e2, distanceX, distanceY);
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.e(TAG, "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.e(TAG, "onFling");
        if (mListener != null) {
            return mListener.onFling(e1, e2, velocityX, velocityY);
        }
        return true;
    }

    public boolean onTouch(MotionEvent event) {
        mGestureDetectorLongPressCompat.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            isDragging = false;
            if (mListener != null) {
                mListener.onActionUp(event);
            }
        }

        return mGestureDetectorCompat.onTouchEvent(event);
    }


    public boolean isDragging() {
        return isDragging;
    }

    interface ScrollHelperListener {

//        boolean onDown(MotionEvent e);

        boolean onSingleTapUp(MotionEvent e);

        boolean onLongPress(MotionEvent e);

        boolean onActionUp(MotionEvent e);

        boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
    }
}
