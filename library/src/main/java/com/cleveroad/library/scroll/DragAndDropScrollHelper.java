package com.cleveroad.library.scroll;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;

class DragAndDropScrollHelper {
    private final ScrollMediatorListener mScrollMediatorListener;
    private int mCurrentX;
    private int mCurrentY;
    private DraggableView mDraggableView;
    private DragAndDropScrollRunnable mDragAndDropScrollRunnable;
    @NonNull
    private DragAndDropScrollHelperListener mListener;

    @SuppressWarnings("all")
    DragAndDropScrollHelper(@NonNull ScrollMediatorListener scrollMediatorListener,
                            @NonNull DraggableView draggableView,
                            @NonNull DragAndDropScrollHelperListener listener) {
        if (scrollMediatorListener == null) {
            throw new IllegalStateException("ScrollMediatorListener is null!!");
        }

        if (draggableView == null) {
            throw new IllegalStateException("ScrollableView is null!!");
        }

        if (listener == null) {
            throw new IllegalStateException("ScrollHelperListener is null!!");
        }
        mScrollMediatorListener = scrollMediatorListener;
        mDraggableView = draggableView;
        mListener = listener;
        mDragAndDropScrollRunnable = new DragAndDropScrollRunnable(draggableView, mScrollMediatorListener);
    }

    boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();

//        if (action == MotionEvent.ACTION_DOWN) {
//        } else if (action == MotionEvent.ACTION_MOVE) {
//
//        } else

        if (action == MotionEvent.ACTION_UP) {
            actionUp(event);
        } else {
            actionMove(event);
        }
        return true;
    }

    private void actionMove(MotionEvent event) {

        if (mCurrentX == -100) {
            mCurrentX = (int) event.getRawX();
        }

        if (mCurrentY == -100) {
            mCurrentY = (int) event.getRawY();
        }
        int diffX = (int) (mCurrentX - event.getRawX());
        int diffY = (int) (mCurrentY - event.getRawY());

        if (mCurrentX == 0) {
            diffX = 0;
        }
        if (mCurrentY == 0) {
            diffY = 0;
        }
        mCurrentX = (int) event.getRawX();
        mCurrentY = (int) event.getRawY();


        Log.e("DragAndDrop", "actionMove " + mCurrentX + " | " + mCurrentY);


        mDragAndDropScrollRunnable.touch(mCurrentX, mCurrentY);
        mListener.onDragAndDropScroll(mCurrentX, mCurrentY);
    }

    private void actionUp(MotionEvent event) {
        mDragAndDropScrollRunnable.stop();
        mListener.onDragAndDropEnd(mCurrentX, mCurrentY);
    }


    interface DragAndDropScrollHelperListener {

        void onDragAndDropEnd(int x, int y);

        void onDragAndDropScroll(int x, int y);
    }

}
