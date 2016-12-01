//package com.cleveroad.library;
//
//import android.animation.ValueAnimator;
//import android.annotation.SuppressLint;
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.graphics.Canvas;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Parcelable;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.VelocityTracker;
//import android.view.View;
//import android.view.ViewConfiguration;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import com.cleveroad.library.adapter.TableAdapter;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import static com.cleveroad.library.adapter.TableAdapter.ViewHolder;
//
///**
// * This view shows a table which can scroll in both directions. Also still
// * leaves the headers fixed.
// */
//public class TableLayoutOld extends ViewGroup {
//    public static final int FIXED_COLUMN_INDEX = -1;
//    public static final int FIXED_ROW_INDEX = -1;
//
//    protected static final String DEFAULT_STRATEGY = "lsq2";
//
//    protected static final String EXTRA_SUPER_STATE = "EXTRA_SUPER_STATE";
//    protected static final String EXTRA_SCROLL_X = "EXTRA_SCROLL_X";
//    protected static final String EXTRA_SCROLL_Y = "EXTRA_SCROLL_Y";
//
//    private static final long LONG_PRESS_DELAY = 1500;
//    //    private final ValueAnimator mCollapseFixedRowAnimator, mExpandFixedRowAnimator;
////    private final ValueAnimator mCollapseFixedColumnAnimator, mExpandFixedColumnAnimator;
////    private final ValueAnimator mCollapseDragColumnAnimator, mExpandDragColumnAnimator;
//    private final LazyIntArrayCalc mWidthCalc, mHeightCalc;
//    private final ImageView[] mShadows;
//    private final TableLayoutSettings mSettings;
//
//    private final SmoothScrollRunnableOld mScrollHelper;
//    private final Recycler mRecycler;
//    private final List<ViewHolder> mDragViewHolders = new ArrayList<>();
//    private int mCurrentX, mCurrentY;
//    private TableAdapter mTableAdapter;
//    private int mScrollX, mScrollY;
//    private int mFirstRow, mFirstColumn;
//    @SuppressWarnings("unused")
//    private ViewHolder mHeadViewHolder;
//    private List<ViewHolder> mFixedRowViewHolderList;
//    private List<ViewHolder> mFixedColumnViewHolderList;
//    private List<List<ViewHolder>> mBodyViewHolderTable; //rows<columns>
//    private int mRowCount, mColumnCount;
//    private int mWidth, mHeight;
//    private TableDataSetObserver mTableAdapterDataSetObserver;
//    private boolean mNeedRelayout = true;
//    private VelocityTracker mVelocityTracker;
//    private int mTouchSlop;
//    private int mFixedRowTop, mFixedColumnLeft;
//    private final ValueAnimator.AnimatorUpdateListener mExpandCollapseRowUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
//        @Override
//        public void onAnimationUpdate(ValueAnimator animation) {
//            final Integer value = (Integer) animation.getAnimatedValue();
//
//            mHeadViewHolder.getItemView().setTop(value);
//            for (ViewHolder viewHolder : mFixedRowViewHolderList) {
//                viewHolder.getItemView().setTop(value);
//            }
//
//            mShadows[1].setTop(mHeightCalc.getItem(0) + value);
//            scrollBy(0, value - mFixedRowTop);
//            mFixedRowTop = value;
//
//            invalidate();
//        }
//    };
//    private final ValueAnimator.AnimatorUpdateListener mExpandCollapseColumnUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
//        @Override
//        public void onAnimationUpdate(ValueAnimator animation) {
//            final Integer value = (Integer) animation.getAnimatedValue();
//
//            mHeadViewHolder.getItemView().setLeft(value);
//            for (ViewHolder viewHolder : mFixedColumnViewHolderList) {
//                viewHolder.getItemView().setLeft(value);
//            }
//
//            mShadows[0].setLeft(mWidthCalc.getItem(0) + value);
//            scrollBy(value - mFixedColumnLeft, 0);
//            mFixedColumnLeft = value;
//            invalidate();
//        }
//    };
//    private boolean mIsFixedRowScrollEnabled, mIsFixedColumnScrollEnabled;
//    private boolean mIsDragTitleEnabled;
//    private Timer mTimer;
//
//    /**
//     * Simple constructor to use when creating a view from code.
//     *
//     * @param context The Context the view is running in, through which it can
//     *                access the current theme, resources, etc.
//     */
//    public TableLayoutOld(Context context) {
//        this(context, null);
//    }
//
//    /**
//     * Constructor that is called when inflating a view from XML. This is called
//     * when a view is being constructed from an XML file, supplying attributes
//     * that were specified in the XML file. This version uses a default style of
//     * 0, so the only attribute values applied are those in the Context's Theme
//     * and the given AttributeSet.
//     * <p>
//     * The method onFinishInflate() will be called after all children have been
//     * added.
//     *
//     * @param context The Context the view is running in, through which it can
//     *                access the current theme, resources, etc.
//     * @param attrs   The attributes of the XML tag that is inflating the view.
//     */
//    public TableLayoutOld(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//        mWidthCalc = new LazyIntArrayCalc();
//        mHeightCalc = new LazyIntArrayCalc();
//        mHeadViewHolder = null;
//        mFixedRowViewHolderList = new LinkedList<>();
//        mFixedColumnViewHolderList = new LinkedList<>();
//        mBodyViewHolderTable = new LinkedList<>();
//        mRecycler = new Recycler();
//
////        mCollapseFixedRowAnimator = new ValueAnimator();
////        mExpandFixedRowAnimator = new ValueAnimator();
////        mCollapseFixedColumnAnimator = new ValueAnimator();
////        mExpandFixedColumnAnimator = new ValueAnimator();
////        mCollapseDragColumnAnimator = new ValueAnimator();
////        mExpandDragColumnAnimator = new ValueAnimator();
//
//        mNeedRelayout = true;
//
//        mShadows = new ImageView[4];
//        mShadows[0] = new ImageView(context);
//        mShadows[0].setImageResource(R.drawable.shadow_left);
//        mShadows[1] = new ImageView(context);
//        mShadows[1].setImageResource(R.drawable.shadow_top);
//        mShadows[2] = new ImageView(context);
//        mShadows[2].setImageResource(R.drawable.shadow_right);
//        mShadows[3] = new ImageView(context);
//        mShadows[3].setImageResource(R.drawable.shadow_bottom);
//
//        mSettings = new TableLayoutSettings();
//        mSettings.setShadowSize(getResources().getDimensionPixelSize(R.dimen.shadow_size));
//
//        mScrollHelper = new SmoothScrollRunnableOld(this);
//        final ViewConfiguration configuration = ViewConfiguration.get(context);
//        mTouchSlop = configuration.getScaledTouchSlop();
//        mSettings
//                .setMinimumVelocity(configuration.getScaledMinimumFlingVelocity())
//                .setMaximumVelocity(configuration.getScaledMaximumFlingVelocity());
//
//        setWillNotDraw(false);
//    }
//
//    /**
//     * Returns the mTableAdapter currently associated with this widget.
//     *
//     * @return The mTableAdapter used to provide this view's content.
//     */
//    public TableAdapter getTableAdapter() {
//        return mTableAdapter;
//    }
//
//    /**
//     * Sets the data behind this {@link TableLayoutOld}.
//     *
//     * @param tableAdapter The {@link TableAdapter} which is responsible for maintaining the data
//     *                     backing this list and for producing a view to represent an
//     *                     item in that data set.
//     */
//    public void setTableAdapter(TableAdapter tableAdapter) {
//        if (this.mTableAdapter != null) {
//            this.mTableAdapter.unregisterDataSetObserver(mTableAdapterDataSetObserver);
//        }
//
//        this.mTableAdapter = tableAdapter;
//        mTableAdapterDataSetObserver = new TableAdapterDataSetObserver();
//        this.mTableAdapter.registerDataSetObserver(mTableAdapterDataSetObserver);
//
//        mScrollX = 0;
//        mScrollY = 0;
//        mFirstColumn = 0;
//        mFirstRow = 0;
//
//        mNeedRelayout = true;
//        requestLayout();
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        boolean intercept = false;
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN: {
//                resetLongClickTimer();
//                mCurrentX = (int) event.getRawX();
//                mCurrentY = (int) event.getRawY();
//
//                final ViewHolder clickedViewHolder = findFixedRowViewHolder((int) event.getX(), (int) event.getY());
//                if (clickedViewHolder != null) {
//                    mTimer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            // TODO LONG CLICK!!!
////                            setDragViewHoldersColumnByHeader(clickedViewHolder);
////                            mIsDragTitleEnabled = true;
////                            TableLayout.this.post(new Runnable() {
////                                @Override
////                                public void run() {
////                                    collapseDragColumn();
////                                }
////                            });
//                        }
//                    }, LONG_PRESS_DELAY);
//                    Log.e("TL", "Click on row= " + clickedViewHolder.getRowIndex() + "; col=" + clickedViewHolder.getColumnIndex());
//                }
//                break;
//            }
//            case MotionEvent.ACTION_MOVE: {
//                int x2 = Math.abs(mCurrentX - (int) event.getRawX());
//                int y2 = Math.abs(mCurrentY - (int) event.getRawY());
//                if (x2 > mTouchSlop || y2 > mTouchSlop) {
//                    intercept = true;
//                }
//                break;
//            }
//            default: {
//                resetLongClickTimer();
//            }
//        }
//        return intercept;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (mVelocityTracker == null) {
//
//            mVelocityTracker = VelocityTracker.obtain();
//        }
//        mVelocityTracker.addMovement(event);
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN: {
//                if (!mScrollHelper.isFinished()) {
//                    mScrollHelper.forceFinished();
//                }
//                mCurrentX = (int) event.getRawX();
//                mCurrentY = (int) event.getRawY();
//
//                break;
//            }
//            case MotionEvent.ACTION_MOVE: {
//                if (mIsDragTitleEnabled) {
//
//                } else {
//                    final int x2 = (int) event.getRawX();
//                    final int y2 = (int) event.getRawY();
//                    final int diffX = mCurrentX - x2;
//                    final int diffY = mCurrentY - y2;
//                    mCurrentX = x2;
//                    mCurrentY = y2;
//
//                    scrollBy(diffX, diffY);
//                }
//
//                break;
//            }
//            case MotionEvent.ACTION_UP: {
//                resetLongClickTimer();
//                mVelocityTracker.computeCurrentVelocity(1000, mSettings.getMaximumVelocity());
//                int velocityX = (int) mVelocityTracker.getXVelocity();
//                int velocityY = (int) mVelocityTracker.getYVelocity();
//
//                if (Math.abs(velocityX) > mSettings.getMinimumVelocity() || Math.abs(velocityY) > mSettings.getMinimumVelocity()) {
//                    // need to smooth scroll end.
//                    mScrollHelper.start(getActualScrollX(), getActualScrollY(), velocityX, velocityY, getMaxScrollX(), getMaxScrollY());
//                } else {
//                    if (mVelocityTracker != null) { // If the velocity less than threshold
//                        mVelocityTracker.recycle(); // recycle the tracker
//                        mVelocityTracker = null; // DO NOT REMOVE THIS. Fix issue with IllegalStateException: Already in the pool!
//                    }
//                }
//                break;
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public void scrollTo(int x, int y) {
//        if (mNeedRelayout) {
//            mScrollX = x;
//            mFirstColumn = 0;
//
//            mScrollY = y;
//            mFirstRow = 0;
//        } else {
//            scrollBy(x - mWidthCalc.getArraySum(1, mFirstColumn) - mScrollX, y - mHeightCalc.getArraySum(1, mFirstRow) - mScrollY);
//        }
//    }
//
//    @Override
//    public void scrollBy(int x, int y) {
//        mScrollX += x;
//        mScrollY += y;
//
//        if (mNeedRelayout) {
//            return;
//        }
//
//        scrollBounds();
//
//        //noinspection StatementWithEmptyBody
//        if (mScrollX == 0) {
//            // no op
//        } else if (mScrollX > 0) {
//            while (mWidthCalc.getItem(mFirstColumn + 1) < mScrollX) {
//                if (!mFixedRowViewHolderList.isEmpty()) {
//                    removeLeft();
//                }
//                mScrollX -= mWidthCalc.getItem(mFirstColumn + 1);
//                mFirstColumn++;
//            }
//            while (getFilledWidth() < mWidth) {
//                addRight();
//            }
//        } else {
//            while (!mFixedRowViewHolderList.isEmpty() &&
//                    getFilledWidth() - mWidthCalc.getItem(mFirstColumn + mFixedRowViewHolderList.size()) >= mWidth) {
//                removeRight();
//            }
//            if (mFixedRowViewHolderList.isEmpty()) {
//                while (mScrollX < 0) {
//                    mFirstColumn--;
//                    mScrollX += mWidthCalc.getItem(mFirstColumn + 1);
//                }
//                while (getFilledWidth() < mWidth) {
//                    addRight();
//                }
//            } else {
//                while (0 > mScrollX) {
//                    addLeft();
//                    mFirstColumn--;
//                    mScrollX += mWidthCalc.getItem(mFirstColumn + 1);
//                }
//            }
//        }
//
//        //noinspection StatementWithEmptyBody
//        if (mScrollY == 0) {
//            // no op
//        } else if (mScrollY > 0) {
//            while (mHeightCalc.getItem(mFirstRow + 1) < mScrollY) {
//                if (!mFixedColumnViewHolderList.isEmpty()) {
//                    removeTop();
//                }
//                mScrollY -= mHeightCalc.getItem(mFirstRow + 1);
//                mFirstRow++;
//            }
//            while (getFilledHeight() < mHeight) {
//                addBottom();
//            }
//        } else {
//            while (!mFixedColumnViewHolderList.isEmpty() &&
//                    getFilledHeight() - mHeightCalc.getItem(mFirstRow + mFixedColumnViewHolderList.size()) >= mHeight) {
//                removeBottom();
//            }
//            if (mFixedColumnViewHolderList.isEmpty()) {
//                while (mScrollY < 0) {
//                    mFirstRow--;
//                    mScrollY += mHeightCalc.getItem(mFirstRow + 1);
//                }
//                while (getFilledHeight() < mHeight) {
//                    addBottom();
//                }
//            } else {
//                while (0 > mScrollY) {
//                    addTop();
//                    mFirstRow--;
//                    mScrollY += mHeightCalc.getItem(mFirstRow + 1);
//                }
//            }
//        }
//
////        if (y > 5) {
////            collapseFixedRow();
////        } else if (y < -5) {
////            expandFixedRow();
////        }
////
////        if (x > 5) {
////            collapseFixedColumn();
////        } else if (x < -5) {
////            expandFixedColumn();
////        }
//
//        repositionViews();
//
//        shadowsVisibility();
//
//        awakenScrollBars();
//    }
//
//    @Override
//    protected int computeHorizontalScrollExtent() {
//        final float tableSize = mWidth - mWidthCalc.getItem(0) + mFixedColumnLeft;
//        final float contentSize = mWidthCalc.getArraySum() - mWidthCalc.getItem(0) + mFixedColumnLeft;
//        final float percentageOfVisibleView = tableSize / contentSize;
//
//        return Math.round(percentageOfVisibleView * tableSize);
//    }
//
//    @Override
//    protected int computeHorizontalScrollOffset() {
//        final float maxScrollX = mWidthCalc.getArraySum() - mWidth;
//        final float percentageOfViewScrolled = getActualScrollX() / maxScrollX;
//        final int maxHorizontalScrollOffset = mWidth - mWidthCalc.getItem(0) + mFixedColumnLeft - computeHorizontalScrollExtent();
//
//        return mFixedColumnLeft + mWidthCalc.getItem(0) + Math.round(percentageOfViewScrolled * maxHorizontalScrollOffset);
//    }
//
//    @Override
//    protected int computeHorizontalScrollRange() {
//        return mWidth;
//    }
//
//    @Override
//    protected int computeVerticalScrollExtent() {
//        final float tableSize = mHeight - mHeightCalc.getItem(0);
//        final float contentSize = mHeightCalc.getArraySum() - mHeightCalc.getItem(0);
//        final float percentageOfVisibleView = tableSize / contentSize;
//
//        return Math.round(percentageOfVisibleView * tableSize);
//    }
//
//    @Override
//    protected int computeVerticalScrollOffset() {
//        final float maxScrollY = mHeightCalc.getArraySum() - mHeight;
//        final float percentageOfViewScrolled = getActualScrollY() / maxScrollY;
//        final int maxVerticalScrollOffset = mHeight - mFixedRowTop - computeVerticalScrollExtent();
//
//        return mFixedRowTop + mHeightCalc.getItem(0) + Math.round(percentageOfViewScrolled * maxVerticalScrollOffset);
//    }
//
//    @Override
//    protected int computeVerticalScrollRange() {
//        return mHeight;
//    }
//
//    public int getActualScrollX() {
//        return mScrollX + mWidthCalc.getArraySum(1, mFirstColumn);
//    }
//
//    public int getActualScrollY() {
//        return mScrollY + mHeightCalc.getArraySum(1, mFirstRow);
//    }
//
//    private int getMaxScrollX() {
//        return Math.max(0, mWidthCalc.getArraySum() - mWidth);
//    }
//
//    private int getMaxScrollY() {
//        return Math.max(0, mHeightCalc.getArraySum() - mHeight);
//    }
//
//    private int getFilledWidth() {
//        return mFixedColumnLeft + mWidthCalc.getItem(0)
//                + mWidthCalc.getArraySum(mFirstColumn + 1, mFixedRowViewHolderList.size())
//                - mScrollX;
//    }
//
//    private int getFilledHeight() {
//        return mFixedRowTop + mHeightCalc.getItem(0)
//                + mHeightCalc.getArraySum(mFirstRow + 1, mFixedColumnViewHolderList.size())
//                - mScrollY;
//    }
//
//    private void addLeft() {
//        addLeftOrRight(mFirstColumn - 1, 0);
//    }
//
//    private void addTop() {
//        addTopAndBottom(mFirstRow - 1, 0);
//    }
//
//    private void addRight() {
//        final int size = mFixedRowViewHolderList.size();
//        addLeftOrRight(mFirstColumn + size, size);
//    }
//
//    private void addBottom() {
//        final int size = mFixedColumnViewHolderList.size();
//        addTopAndBottom(mFirstRow + size, size);
//    }
//
//    private void addLeftOrRight(int column, int index) {
//        ViewHolder viewHolder = makeViewHolder(FIXED_ROW_INDEX, column, mWidthCalc.getItem(column + 1), mHeightCalc.getItem(0));
//        mFixedRowViewHolderList.add(index, viewHolder);
//
//        int i = mFirstRow;
//        for (List<ViewHolder> list : mBodyViewHolderTable) {
//            viewHolder = makeViewHolder(i, column, mWidthCalc.getItem(column + 1), mHeightCalc.getItem(i + 1));
//            list.add(index, viewHolder);
//            i++;
//        }
//    }
//
//    private void addTopAndBottom(int row, int index) {
//        ViewHolder viewHolder = makeViewHolder(row, FIXED_COLUMN_INDEX, mWidthCalc.getItem(0), mHeightCalc.getItem(row + 1));
//        mFixedColumnViewHolderList.add(index, viewHolder);
//
//        List<ViewHolder> list = new ArrayList<>();
//        final int size = mFixedRowViewHolderList.size() + mFirstColumn;
//        for (int i = mFirstColumn; i < size; i++) {
//            viewHolder = makeViewHolder(row, i, mWidthCalc.getItem(i + 1), mHeightCalc.getItem(row + 1));
//            list.add(viewHolder);
//        }
//        mBodyViewHolderTable.add(index, list);
//    }
//
//    private void removeLeft() {
//        removeLeftOrRight(0);
//    }
//
//    private void removeTop() {
//        removeTopOrBottom(0);
//    }
//
//    private void removeRight() {
//        removeLeftOrRight(mFixedRowViewHolderList.size() - 1);
//    }
//
//    private void removeBottom() {
//        removeTopOrBottom(mFixedColumnViewHolderList.size() - 1);
//    }
//
//    private void removeLeftOrRight(int position) {
//        removeView(mFixedRowViewHolderList.remove(position));
//        for (List<ViewHolder> list : mBodyViewHolderTable) {
//            removeView(list.remove(position));
//        }
//    }
//
//    private void removeTopOrBottom(int position) {
//        removeView(mFixedColumnViewHolderList.remove(position));
//        List<ViewHolder> remove = mBodyViewHolderTable.remove(position);
//        for (ViewHolder view : remove) {
//            removeView(view);
//        }
//    }
//
//    @Override
//    public void removeView(View view) {
//        ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.tag_view_holder);
//        if (viewHolder != null) {
//            removeView(viewHolder);
//        } else {
//            super.removeView(view);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    public void removeView(@NonNull ViewHolder viewHolder) {
//        super.removeView(viewHolder.getItemView());
//
//        final int typeView = viewHolder.getItemType();
//        if (typeView != ItemType.IGNORE) {
//            mTableAdapter.onViewHolderRecycled(viewHolder);
//            mRecycler.pushRecycledView(viewHolder, typeView);
//        }
//    }
//
//    private void repositionViews() {
//        int left, top, right, bottom, i;
//
//        left = mWidthCalc.getItem(0) + mFixedColumnLeft - mScrollX;
//        i = mFirstColumn;
//        for (ViewHolder viewHolder : mFixedRowViewHolderList) {
//            right = left + mWidthCalc.getItem(++i);
//            viewHolder.getItemView().layout(left, mFixedRowTop, right, mFixedRowTop + mHeightCalc.getItem(0));
//            left = right;
//        }
//
//        top = mHeightCalc.getItem(0) + mFixedRowTop - mScrollY;
//        i = mFirstRow;
//        for (ViewHolder viewHolder : mFixedColumnViewHolderList) {
//            bottom = top + mHeightCalc.getItem(++i);
//            viewHolder.getItemView().layout(0, top, mWidthCalc.getItem(0), bottom);
//            top = bottom;
//        }
//
//        top = mHeightCalc.getItem(0) + mFixedRowTop - mScrollY;
//        i = mFirstRow;
//        for (List<ViewHolder> list : mBodyViewHolderTable) {
//            bottom = top + mHeightCalc.getItem(++i);
//            left = mWidthCalc.getItem(0) + mFixedColumnLeft - mScrollX;
//            int j = mFirstColumn;
//            for (ViewHolder viewHolder : list) {
//                right = left + mWidthCalc.getItem(++j);
//                viewHolder.getItemView().layout(left, top, right, bottom);
//                left = right;
//            }
//            top = bottom;
//        }
//        invalidate();
//    }
//
//    @SuppressLint("DrawAllocation")
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        final int measuredWidth;
//        final int measuredHeight;
//
//        if (mTableAdapter != null) {
//            if (mNeedRelayout) {
//                this.mRowCount = mTableAdapter.getRowCount();
//                this.mColumnCount = mTableAdapter.getColumnCount();
//
//                int widths[] = new int[mColumnCount + 1];
//                for (int i = -1; i < mColumnCount; i++) {
//                    widths[i + 1] += mTableAdapter.getItemWidth(i);
//                }
//                mWidthCalc.setArray(widths);
//
//                int heights[] = new int[mRowCount + 1];
//                for (int i = -1; i < mRowCount; i++) {
//                    heights[i + 1] += mTableAdapter.getItemHeight(i);
//                }
//                mHeightCalc.setArray(heights);
//            }
//
//            if (widthMode == MeasureSpec.AT_MOST) {
//                measuredWidth = Math.min(widthSize, mWidthCalc.getArraySum());
//            } else if (widthMode == MeasureSpec.UNSPECIFIED) {
//                measuredWidth = mWidthCalc.getArraySum();
//            } else {
//                measuredWidth = widthSize;
//                if (mNeedRelayout) {
//                    int sumArray = mWidthCalc.getArraySum();
//                    if (sumArray < widthSize) {
//                        final float factor = widthSize / (float) sumArray;
//                        int firstColumnWidth = widthSize;
//                        for (int i = 1; i < mWidthCalc.getSize(); i++) {
//                            int itemWidth = Math.round(mWidthCalc.getItem(i) * factor);
//                            mWidthCalc.setItem(i, itemWidth);
//                            firstColumnWidth -= itemWidth;
//                        }
//                        mWidthCalc.invalidate();
//                        mWidthCalc.setItem(0, firstColumnWidth);
//                        mWidthCalc.invalidate();
//                    }
//                }
//            }
//
//            if (heightMode == MeasureSpec.AT_MOST) {
//                measuredHeight = Math.min(heightSize, mHeightCalc.getArraySum());
//            } else if (heightMode == MeasureSpec.UNSPECIFIED) {
//                measuredHeight = mHeightCalc.getArraySum();
//            } else {
//                measuredHeight = heightSize;
//            }
//        } else {
//            if (heightMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
//                measuredWidth = 0;
//                measuredHeight = 0;
//            } else {
//                measuredWidth = widthSize;
//                measuredHeight = heightSize;
//            }
//        }
//
//        if (mFirstRow >= mRowCount || getMaxScrollY() - getActualScrollY() < 0) {
//            mFirstRow = 0;
//            mScrollY = Integer.MAX_VALUE;
//        }
//        if (mFirstColumn >= mColumnCount || getMaxScrollX() - getActualScrollX() < 0) {
//            mFirstColumn = 0;
//            mScrollX = Integer.MAX_VALUE;
//        }
//
//        setMeasuredDimension(measuredWidth, measuredHeight);
//    }
//
//    @SuppressLint("DrawAllocation")
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        if (mNeedRelayout || changed) {
//            mNeedRelayout = false;
//            resetTable();
//
//            if (mTableAdapter != null) {
//                mWidth = r - l;
//                mHeight = b - t;
//
//                int left, top, right, bottom;
//
//                // Shadows for header in when scroll
////                right = Math.min(mWidth, mWidthCalc.getArraySum());
////                bottom = Math.min(mHeight, mHeightCalc.getArraySum());
////                addShadow(mShadows[0], mWidthCalc.getItem(0), 0, mWidthCalc.getItem(0) + mShadowSize, bottom);
////                addShadow(mShadows[1], 0, mHeightCalc.getItem(0), right, mHeightCalc.getItem(0) + mShadowSize);
////                addShadow(mShadows[2], right - mShadowSize, 0, right, bottom);
////                addShadow(mShadows[3], 0, bottom - mShadowSize, right, bottom);
//
//                mHeadViewHolder = makeAndSetup(FIXED_ROW_INDEX, FIXED_COLUMN_INDEX, 0, 0, mWidthCalc.getItem(0), mHeightCalc.getItem(0));
//
//                scrollBounds();
//                adjustFirstCellsAndScroll();
//
//                left = mWidthCalc.getItem(0) + mFixedColumnLeft - mScrollX;
//                for (int i = mFirstColumn; i < mColumnCount && left < mWidth; i++) {
//                    right = left + mWidthCalc.getItem(i + 1);
//                    final ViewHolder viewHolder = makeAndSetup(FIXED_ROW_INDEX, i, left, 0, right, mHeightCalc.getItem(0));
//                    mFixedRowViewHolderList.add(viewHolder);
//                    left = right;
//                }
//
//                top = mHeightCalc.getItem(0) + mFixedRowTop - mScrollY;
//                for (int i = mFirstRow; i < mRowCount && top < mHeight; i++) {
//                    bottom = top + mHeightCalc.getItem(i + 1);
//                    final ViewHolder viewHolder = makeAndSetup(i, FIXED_COLUMN_INDEX, 0, top, mWidthCalc.getItem(0), bottom);
//                    mFixedColumnViewHolderList.add(viewHolder);
//                    top = bottom;
//                }
//
//                top = mHeightCalc.getItem(0) + mFixedRowTop - mScrollY;
//                for (int i = mFirstRow; i < mRowCount && top < mHeight; i++) {
//                    bottom = top + mHeightCalc.getItem(i + 1);
//                    left = mWidthCalc.getItem(0) + mFixedColumnLeft - mScrollX;
//                    List<ViewHolder> list = new LinkedList<>();
//                    for (int j = mFirstColumn; j < mColumnCount && left < mWidth; j++) {
//                        right = left + mWidthCalc.getItem(j + 1);
//                        final ViewHolder viewHolder = makeAndSetup(i, j, left, top, right, bottom);
//                        list.add(viewHolder);
//                        left = right;
//                    }
//                    mBodyViewHolderTable.add(list);
//                    top = bottom;
//                }
//
//                shadowsVisibility();
//            }
//        }
//    }
//
//    private void scrollBounds() {
//        mScrollX = scrollBounds(mScrollX, mFirstColumn, mWidthCalc, mWidth - mFixedColumnLeft);
//        mScrollY = scrollBounds(mScrollY, mFirstRow, mHeightCalc, mHeight - mFixedRowTop);
//    }
//
//    private int scrollBounds(int desiredScroll, int firstCell, LazyIntArrayCalc sizes, int viewSize) {
//        //noinspection StatementWithEmptyBody
//        if (desiredScroll == 0) {
//            // no op
//        } else if (desiredScroll < 0) {
//            desiredScroll = Math.max(desiredScroll, -sizes.getArraySum(1, firstCell));
//        } else {
//            desiredScroll = Math.min(desiredScroll, Math.max(0, sizes.getArraySum(firstCell + 1, sizes.getSize() - 1 - firstCell) + sizes.getItem(0) - viewSize));
//        }
//        return desiredScroll;
//    }
//
//    private void adjustFirstCellsAndScroll() {
//        int values[];
//
//        values = adjustFirstCellsAndScroll(mScrollX, mFirstColumn, mWidthCalc);
//        mScrollX = values[0];
//        mFirstColumn = values[1];
//
//        values = adjustFirstCellsAndScroll(mScrollY, mFirstRow, mHeightCalc);
//        mScrollY = values[0];
//        mFirstRow = values[1];
//    }
//
//    private int[] adjustFirstCellsAndScroll(int scroll, int firstCell, LazyIntArrayCalc calc) {
//        if (scroll == 0) {
//            // no op
//        } else if (scroll > 0) {
//            while (calc.getItem(firstCell + 1) < scroll) {
//                firstCell++;
//                scroll -= calc.getItem(firstCell);
//            }
//        } else {
//            while (scroll < 0) {
//                scroll += calc.getItem(firstCell);
//                firstCell--;
//            }
//        }
//        return new int[]{scroll, firstCell};
//    }
//
//    private void shadowsVisibility() {
//        final int actualScrollX = getActualScrollX();
//        final int actualScrollY = getActualScrollY();
//        final int[] remainPixels = {
//                actualScrollX,
//                actualScrollY,
//                getMaxScrollX() - actualScrollX,
//                getMaxScrollY() - actualScrollY,
//        };
//
//        for (int i = 0; i < mShadows.length; i++) {
//            setAlpha(mShadows[i], Math.min(remainPixels[i] / (float) mSettings.getShadowSize(), 1));
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    @SuppressWarnings("deprecation")
//    private void setAlpha(ImageView imageView, float alpha) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            imageView.setAlpha(alpha);
//        } else {
//            imageView.setAlpha(Math.round(alpha * 255));
//        }
//    }
//
//    private void addShadow(ImageView imageView, int l, int t, int r, int b) {
//        imageView.layout(l, t, r, b);
//        addView(imageView);
//    }
//
//    private void resetTable() {
//        mHeadViewHolder = null;
//        mFixedRowViewHolderList.clear();
//        mFixedColumnViewHolderList.clear();
//        mBodyViewHolderTable.clear();
//
//        removeAllViews();
//    }
//
//    private ViewHolder makeAndSetup(int row, int column, int left, int top, int right, int bottom) {
//        final ViewHolder viewHolder = makeViewHolder(row, column, right - left, bottom - top);
//        viewHolder.getItemView().layout(left, top, right, bottom);
//        return viewHolder;
//    }
//
//    @Override
//    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
//        final boolean result;
//
//        final ViewHolder viewHolder = (ViewHolder) child.getTag(R.id.tag_view_holder);
//
//        canvas.save();
//
//        //noinspection StatementWithEmptyBody
//        if (viewHolder == null) {
//            //ignore
//        } else if (viewHolder.getRowIndex() == FIXED_ROW_INDEX && viewHolder.getColumnIndex() == FIXED_COLUMN_INDEX) {
//            canvas.clipRect(
//                    mFixedColumnLeft,
//                    mFixedRowTop,
//                    mFixedColumnLeft + mWidthCalc.getItem(0),
//                    mFixedRowTop + mHeightCalc.getItem(0));
//        } else {
//            if (viewHolder.getRowIndex() == FIXED_ROW_INDEX) {
//                canvas.clipRect(
//                        mWidthCalc.getItem(0) + mFixedColumnLeft,
//                        mFixedRowTop,
//                        canvas.getWidth(),
//                        mFixedRowTop + canvas.getHeight());
//            } else if (viewHolder.getColumnIndex() == FIXED_COLUMN_INDEX) {
//                canvas.clipRect(
//                        mFixedColumnLeft,
//                        mHeightCalc.getItem(0) + mFixedRowTop,
//                        canvas.getWidth(),
//                        canvas.getHeight());
//            } else {
//                canvas.clipRect(
//                        mWidthCalc.getItem(0) + mFixedColumnLeft,
//                        mHeightCalc.getItem(0) + mFixedRowTop,
//                        canvas.getWidth(),
//                        canvas.getHeight());
//            }
//        }
//
//        result = super.drawChild(canvas, child, drawingTime);
//        canvas.restore();
//        return result;
//    }
//
//    @Override
//    protected Parcelable onSaveInstanceState() {
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(EXTRA_SUPER_STATE, super.onSaveInstanceState());
//
//        if (!mBodyViewHolderTable.isEmpty() && !mBodyViewHolderTable.get(0).isEmpty()) {
//            ViewHolder firstVisibleVH = mBodyViewHolderTable.get(0).get(0);
//
//            int scX = mWidthCalc.getArraySum(0, firstVisibleVH.getColumnIndex() + 1);
//            int scY = mHeightCalc.getArraySum(2, firstVisibleVH.getRowIndex() - 1);
//            bundle.putInt(EXTRA_SCROLL_X, scX + 5);
//            bundle.putInt(EXTRA_SCROLL_Y, scY + 5);
//        }
//        return bundle;
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Parcelable state) {
//        if (state instanceof Bundle) {
//            Bundle bundle = ((Bundle) state);
//            super.onRestoreInstanceState(bundle.getParcelable(EXTRA_SUPER_STATE));
//            scrollTo(bundle.getInt(EXTRA_SCROLL_X), bundle.getInt(EXTRA_SCROLL_Y));
//        } else {
//            super.onRestoreInstanceState(state);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    private ViewHolder makeViewHolder(int row, int column, int w, int h) {
//        final int itemType = mTableAdapter.getItemType(row, column);
//        ViewHolder viewHolder = null;
//        if (itemType != ItemType.IGNORE) {
//            viewHolder = mRecycler.popRecycledViewHolder(itemType);
//        }
//        if (viewHolder == null) {
//            viewHolder = mTableAdapter.onCreateViewHolder(TableLayoutOld.this, itemType);
//        }
//        viewHolder.setItemType(itemType);
//        viewHolder.setRowIndex(row);
//        viewHolder.setColumnIndex(column);
//        viewHolder.getItemView().setTag(R.id.tag_view_holder, viewHolder);
//        viewHolder.getItemView().measure(
//                MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
//                MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
//
//        addTableView(viewHolder.getItemView(), row, column);
//
//        mTableAdapter.onBindViewHolder(viewHolder, row, column);
//
//        return viewHolder;
//    }
//
//    private void addTableView(View view, int row, int column) {
//        if (row == FIXED_ROW_INDEX && column == FIXED_COLUMN_INDEX) {
//            addView(view, getChildCount() - 4);
//        } else if (row == FIXED_ROW_INDEX || column == FIXED_COLUMN_INDEX) {
//            addView(view, getChildCount() - 5);
//        } else {
//            addView(view, 0);
//        }
//    }
//
////    private void collapseFixedRow() {
////        final int fixedRowHeight = mTableAdapter.getItemHeight(FIXED_ROW_INDEX);
////        if (!mIsFixedRowScrollEnabled ||
////                mCollapseFixedRowAnimator.isRunning() ||
////                mExpandFixedRowAnimator.isRunning() ||
////                mFixedRowTop == -fixedRowHeight ||
////                fixedRowHeight == 0) {
////            return;
////        }
////
////        mCollapseFixedRowAnimator.setIntValues(0, -fixedRowHeight);
////        mCollapseFixedRowAnimator.setDuration(500);
////        mCollapseFixedRowAnimator.removeAllUpdateListeners();
////        mCollapseFixedRowAnimator.removeAllListeners();
////        mCollapseFixedRowAnimator.addUpdateListener(mExpandCollapseRowUpdateListener);
////        mCollapseFixedRowAnimator.start();
////    }
//
////    private void expandFixedRow() {
////        final int fixedRowHeight = mTableAdapter.getItemHeight(FIXED_ROW_INDEX);
////        if (!mIsFixedRowScrollEnabled ||
////                mExpandFixedRowAnimator.isRunning() ||
////                mCollapseFixedRowAnimator.isRunning() ||
////                mFixedRowTop == 0 ||
////                fixedRowHeight == 0) {
////            return;
////        }
////        mExpandFixedRowAnimator.setIntValues(-fixedRowHeight, 0);
////        mExpandFixedRowAnimator.setDuration(500);
////        mExpandFixedRowAnimator.removeAllUpdateListeners();
////        mExpandFixedRowAnimator.removeAllListeners();
////        mExpandFixedRowAnimator.addUpdateListener(mExpandCollapseRowUpdateListener);
////
////        mExpandFixedRowAnimator.start();
////    }
//
////    private void collapseFixedColumn() {
////        final int fixedColumnWidth = mTableAdapter.getItemWidth(FIXED_COLUMN_INDEX);
////        if (!mIsFixedColumnScrollEnabled ||
////                mExpandFixedColumnAnimator.isRunning() ||
////                mCollapseFixedColumnAnimator.isRunning() ||
////                mFixedColumnLeft == -fixedColumnWidth ||
////                fixedColumnWidth == 0) {
////            return;
////        }
////        mCollapseFixedColumnAnimator.setIntValues(0, -fixedColumnWidth);
////        mCollapseFixedColumnAnimator.setDuration(500);
////        mCollapseFixedColumnAnimator.removeAllUpdateListeners();
////        mCollapseFixedColumnAnimator.removeAllListeners();
////        mCollapseFixedColumnAnimator.addUpdateListener(mExpandCollapseColumnUpdateListener);
////
////        mCollapseFixedColumnAnimator.start();
////    }
//
////    private void expandFixedColumn() {
////        final int fixedColumnWidth = mTableAdapter.getItemWidth(FIXED_COLUMN_INDEX);
////        if (!mIsFixedColumnScrollEnabled ||
////                mExpandFixedColumnAnimator.isRunning() ||
////                mCollapseFixedColumnAnimator.isRunning() ||
////                mFixedColumnLeft == 0 ||
////                fixedColumnWidth == 0) {
////            return;
////        }
////        mExpandFixedColumnAnimator.setIntValues(-fixedColumnWidth, 0);
////        mExpandFixedColumnAnimator.setDuration(500);
////        mExpandFixedColumnAnimator.removeAllUpdateListeners();
////        mExpandFixedColumnAnimator.removeAllListeners();
////        mExpandFixedColumnAnimator.addUpdateListener(mExpandCollapseColumnUpdateListener);
////
////        mExpandFixedColumnAnimator.start();
////    }
//
////    private void collapseDragColumn() {
////        if (mDragViewHolders.isEmpty()) {
////            return;
////        }
////        if (mCollapseDragColumnAnimator.isRunning()) {
////            mCollapseDragColumnAnimator.cancel();
////        }
////        final int columnIndex = mDragViewHolders.get(0).getColumnIndex();
////        final int from = mTableAdapter.getItemWidth(columnIndex);
////        mCollapseDragColumnAnimator.setIntValues(from, 0);
////        mCollapseDragColumnAnimator.setDuration(5000);
////        mCollapseDragColumnAnimator.removeAllUpdateListeners();
////        mCollapseDragColumnAnimator.removeAllListeners();
////        mCollapseDragColumnAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
////            @Override
////            public void onAnimationUpdate(ValueAnimator animation) {
////                int width = (Integer) animation.getAnimatedValue();
////                mWidthCalc.setItem(columnIndex + 1, width);
//////                for (ViewHolder vh: mDragViewHolders) {
//////                    View v = vh.getItemView();
//////                    int centerX = (int) (v.getLeft() + (v.getRight() - v.getLeft()) / 2f);
//////                    v.layout(centerX - width / 2, v.getTop(), centerX + width / 2, v.getBottom());
//////                    v.invalidate();
//////                }
////                mWidthCalc.invalidate();
////                requestLayout();
////
////                scrollBy(0, 0);
////            }
////        });
////
////        mCollapseDragColumnAnimator.start();
////    }
//
//    @Nullable
//    private ViewHolder findFixedRowViewHolder(int x, int y) {
//        for (ViewHolder vh : mFixedRowViewHolderList) {
//            View view = vh.getItemView();
//            float viewX = view.getX();
//            float viewY = view.getY();
//
//            if (y >= viewY && y <= viewY + view.getHeight()) {
//                if (x >= viewX && x <= viewX + view.getWidth()) {
//                    return vh;
//                }
//            }
//        }
//        return null;
//    }
//
//    private void setDragViewHoldersColumnByHeader(ViewHolder headViewHolder) {
//        final int columnIndex = headViewHolder.getColumnIndex();
//        mDragViewHolders.clear();
//        mDragViewHolders.add(headViewHolder);
//
//        for (List<ViewHolder> row : mBodyViewHolderTable) {
//            for (ViewHolder col : row) {
//                if (col.getColumnIndex() == columnIndex) {
//                    mDragViewHolders.add(col);
//                }
//            }
//        }
//    }
//
//    private void resetLongClickTimer() {
//        if (mTimer != null) {
//            mTimer.cancel();
//        }
//        mTimer = new Timer();
//        mIsDragTitleEnabled = false;
//    }
//
//    public void setFixedRowScrollEnabled(boolean isEnabled) {
//        mIsFixedRowScrollEnabled = isEnabled;
//    }
//
//    public void setFixedColumnScrollEnabled(boolean isEnabled) {
//        mIsFixedColumnScrollEnabled = isEnabled;
//    }
//
//    private class TableAdapterDataSetObserver implements TableDataSetObserver {
//
//        @SuppressWarnings("unchecked")
//        @Override
//        public void notifyDataSetChanged() {
//            notifyHeadViewChanged();
//            for (ViewHolder vh : mFixedRowViewHolderList) {
//                mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
//            }
//            for (ViewHolder vh : mFixedColumnViewHolderList) {
//                mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
//            }
//            for (List<ViewHolder> list : mBodyViewHolderTable) {
//                for (ViewHolder vh : list) {
//                    mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
//                }
//            }
//        }
//
//        @Override
//        public void notifyLayoutChanged() {
//            mNeedRelayout = true;
//            requestLayout();
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        public void notifyItemChanged(int rowIndex, int columnIndex) {
//            if (rowIndex == FIXED_ROW_INDEX && columnIndex == FIXED_COLUMN_INDEX) {
//                notifyHeadViewChanged();
//            } else if (rowIndex == FIXED_ROW_INDEX) {
//                for (ViewHolder vh : mFixedRowViewHolderList) {
//                    if (vh.getColumnIndex() == columnIndex && vh.getRowIndex() == rowIndex) {
//                        mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
//                    }
//                }
//            } else if (columnIndex == FIXED_COLUMN_INDEX) {
//                for (ViewHolder vh : mFixedColumnViewHolderList) {
//                    if (vh.getColumnIndex() == columnIndex && vh.getRowIndex() == rowIndex) {
//                        mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
//                    }
//                }
//            } else {
//                for (List<ViewHolder> list : mBodyViewHolderTable) {
//                    for (ViewHolder vh : list) {
//                        if (vh.getColumnIndex() == columnIndex && vh.getRowIndex() == rowIndex) {
//                            mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
//                        }
//                    }
//                }
//            }
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        public void notifyRowChanged(int rowIndex) {
//            for (ViewHolder vh : mFixedColumnViewHolderList) {
//                if (vh.getRowIndex() == rowIndex) {
//                    mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
//                }
//            }
//            if (rowIndex == FIXED_ROW_INDEX) {
//                for (ViewHolder vh : mFixedRowViewHolderList) {
//                    mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
//                }
//            } else {
//                for (List<ViewHolder> list : mBodyViewHolderTable) {
//                    for (ViewHolder vh : list) {
//                        if (vh.getRowIndex() == rowIndex) {
//                            mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
//                        }
//                    }
//                }
//            }
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        public void notifyColumnChanged(int columnIndex) {
//            for (ViewHolder vh : mFixedRowViewHolderList) {
//                if (vh.getColumnIndex() == columnIndex) {
//                    mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
//                }
//            }
//            if (columnIndex == FIXED_COLUMN_INDEX) {
//                for (ViewHolder vh : mFixedColumnViewHolderList) {
//                    mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
//                }
//            } else {
//                for (List<ViewHolder> list : mBodyViewHolderTable) {
//                    for (ViewHolder vh : list) {
//                        if (vh.getColumnIndex() == columnIndex) {
//                            mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
//                        }
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void notifyHeadViewChanged() {
//            mHeadViewHolder = makeAndSetup(FIXED_ROW_INDEX, FIXED_COLUMN_INDEX, 0, 0, mWidthCalc.getItem(0), mHeightCalc.getItem(0));
//        }
//    }
//}