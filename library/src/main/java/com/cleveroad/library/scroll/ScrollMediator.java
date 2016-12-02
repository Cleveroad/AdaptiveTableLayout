package com.cleveroad.library.scroll;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

public class ScrollMediator implements
        ScrollHelper.ScrollHelperListener,
        DragAndDropScrollHelper.DragAndDropScrollHelperListener {
    private final ScrollHelper mScrollHelper;
    private final DragAndDropScrollHelper mDragAndDropScrollHelper;
    private final Point dragAndDropStart = new Point(-100, -100);
    private final Point dragAndDropEnd = new Point(-100, -100);
    private DraggableView mDraggableView;
    private ScrollableView mScrollableView;
    private ScrollMediatorListener mScrollMediatorListener;
    private Context mContext; //TODO Remove later
    // flag show us if user started dragging
    private boolean isDragging = false;

    private ScrollMediator(Builder builder) {
        mDraggableView = builder.mDraggableView;
        mScrollableView = builder.mScrollableView;
        mContext = builder.mContext;
        mScrollMediatorListener = builder.mScrollMediatorListener;

        mScrollHelper = new ScrollHelper(mScrollMediatorListener, mScrollableView, this);
        mDragAndDropScrollHelper = new DragAndDropScrollHelper(mScrollMediatorListener, mDraggableView, this);
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    public boolean onTouchEvent(MotionEvent event, int actualScrollX, int actualScrollY) {
        if (isDragging) {
            return mDragAndDropScrollHelper.onTouchEvent(event);
        } else {
            return mScrollHelper.onTouchEvent(event, actualScrollX, actualScrollY);
        }
    }

    @Override
    public void onLongTouch(int x, int y) {
        if (mScrollMediatorListener.canStartDragging(x, y)) {
            isDragging = true;
            dragAndDropStart.set(x, y);
            dragAndDropEnd.set(-100, -100);
            if (mDraggableView != null) {
                mDraggableView.onDragAndDropStart(x, y);
            }
        }
    }

    @Override
    public void onDragAndDropEnd(int x, int y) {
        dragAndDropEnd.set(x, y);
        if (mDraggableView != null) {
            mDraggableView.onDragAndDropEnd(x, y);
        }
        isDragging = false;
        dragAndDropEnd.set(-100, -100);
        dragAndDropStart.set(-100, -100);
    }

    @Override
    public void onDragAndDropScroll(int x, int y) {

        if (mDraggableView != null) {
            mDraggableView.onDragAndDropScroll(x, y);
        }
    }


    public boolean isDragging() {
        return isDragging;
    }

    public Point getDragAndDropStart() {
        return dragAndDropStart;
    }

    public Point getDragAndDropEnd() {
        return dragAndDropEnd;
    }

    /**
     * {@code ScrollMediator} builder static inner class.
     */
    public static final class Builder {
        private DraggableView mDraggableView;
        private ScrollableView mScrollableView;
        private ScrollMediatorListener mScrollMediatorListener;
        @NonNull
        private Context mContext;

        private Builder(@NonNull Context context) {
            mContext = context;
        }

        /**
         * Sets the {@code mDraggableView} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param draggableView the {@code mDraggableView} to set
         * @return a reference to this Builder
         */
        @NonNull
        public Builder withDraggableView(@NonNull DraggableView draggableView) {
            mDraggableView = draggableView;
            return this;
        }

        /**
         * Sets the {@code mScrollableView} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param scrollableView the {@code mScrollableView} to set
         * @return a reference to this Builder
         */
        @NonNull
        public Builder withScrollableView(@NonNull ScrollableView scrollableView) {
            mScrollableView = scrollableView;
            return this;
        }

        /**
         * Sets the {@code mScrollableView} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param scrollMediatorListener the {@code mScrollMediatorListener} to set
         * @return a reference to this Builder
         */
        @NonNull
        public Builder withScrollMediatorListener(@NonNull ScrollMediatorListener scrollMediatorListener) {
            mScrollMediatorListener = scrollMediatorListener;
            return this;
        }

        /**
         * Returns a {@code ScrollMediator} built from the parameters previously set.
         *
         * @return a {@code ScrollMediator} built with parameters of this {@code ScrollMediator.Builder}
         */
        @NonNull
        public ScrollMediator build() {
            return new ScrollMediator(this);
        }
    }
}
