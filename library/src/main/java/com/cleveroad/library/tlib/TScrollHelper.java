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
    @Nullable
    private ScrollHelperListener mListener;

    public TScrollHelper(Context context) {
        mContext = context;
        mGestureDetectorCompat = new GestureDetectorCompat(mContext, this);
    }

    public void setListener(@Nullable ScrollHelperListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
//        Log.e(TAG, "onDown");
//        if (mListener != null) {
//            return mListener.onDown(e);
//        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
//        Log.e(TAG, "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
//        Log.e(TAG, "onSingleTapUp");
        if (mListener != null) {
            return mListener.onSingleTapUp(e);
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        Log.e(TAG, "onScroll");
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
        return mGestureDetectorCompat.onTouchEvent(event);
    }

    interface ScrollHelperListener {

//        boolean onDown(MotionEvent e);

        boolean onSingleTapUp(MotionEvent e);

        void onLongPress(MotionEvent e);

        boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
    }
}
