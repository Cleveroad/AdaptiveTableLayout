//package com.cleveroad.library;
//
//import android.widget.Scroller;
//
///**
// *  {@see http://stackoverflow.com/a/6219382/842697 }
// */
//class SmoothScrollRunnableOld implements Runnable {
//    private final TableLayoutOld mTableLayout;
//    private final Scroller mScroller;
//
//    private int mLastX, mLastY;
//
//    SmoothScrollRunnableOld(TableLayoutOld tableLayout) {
//        mTableLayout = tableLayout;
//        mScroller = new Scroller(tableLayout.getContext());
//    }
//
//    void start(int initX, int initY, int initialVelocityX, int initialVelocityY, int maxX, int maxY) {
//        mScroller.fling(initX, initY, initialVelocityX, initialVelocityY, 0, maxX, 0, maxY);
//
//        mLastX = initX;
//        mLastY = initY;
//        mTableLayout.post(this);
//    }
//
//    public void run() {
//        if (mScroller.isFinished()) {
//            return;
//        }
//
//        boolean more = mScroller.computeScrollOffset();
//        int x = mScroller.getCurrX();
//        int y = mScroller.getCurrY();
//        int diffX = mLastX - x;
//        int diffY = mLastY - y;
//        if (diffX != 0 || diffY != 0) {
//            mTableLayout.scrollBy(diffX, diffY);
//            mLastX = x;
//            mLastY = y;
//        }
//
//        if (more) {
//            mTableLayout.post(this);
//        }
//    }
//
//    boolean isFinished() {
//        return mScroller.isFinished();
//    }
//
//    void forceFinished() {
//        if (!mScroller.isFinished()) {
//            mScroller.forceFinished(true);
//        }
//    }
//}
