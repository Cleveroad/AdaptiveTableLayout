package com.cleveroad.library;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cleveroad.library.adapter.TableAdapter;
import com.cleveroad.library.scroll.DraggableView;
import com.cleveroad.library.scroll.ScrollMediator;
import com.cleveroad.library.scroll.ScrollMediatorListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.cleveroad.library.adapter.TableAdapter.ViewHolder;

/**
 * This view shows a table which can scroll in both directions. Also still
 * leaves the headers fixed.
 */
public class TableLayout extends ViewGroup implements DraggableView, ScrollMediatorListener {
    public static final int FIXED_COLUMN_INDEX = -1;
    public static final int FIXED_ROW_INDEX = -1;

    protected static final String EXTRA_SUPER_STATE = "EXTRA_SUPER_STATE";
    protected static final String EXTRA_SCROLL_X = "EXTRA_SCROLL_X";
    protected static final String EXTRA_SCROLL_Y = "EXTRA_SCROLL_Y";
    static final String[] MIMETYPES_TEXT_PLAIN = new String[]{
            ClipDescription.MIMETYPE_TEXT_PLAIN};
    private final LazyIntArrayCalc mWidthCalc;
    private final LazyIntArrayCalc mHeightCalc;
    private final TableLayoutSettings mSettings;
    private final Recycler mRecycler;
    //    private final ScrollHelper mScrollHelper;
//    private final DragAndDropScrollHelper mDragAndDropScrollHelper;
    @Nullable
    private ScrollMediator mScrollMediator;
    private TableAdapter mTableAdapter;
    private int mScrollX, mScrollY;
    private int mFirstRow, mFirstColumn;
    //    @SuppressWarnings("unused")
//    private ViewHolder mHeadViewHolder;
    private ViewHolder mDragAndDropHolder;

    private List<ViewHolder> mFixedRowViewHolderList;
    private List<ViewHolder> mFixedColumnViewHolderList;
    private List<List<ViewHolder>> mBodyViewHolderTable; //rows<columns>
    private int mRowCount, mColumnCount;
    //    private int mWidth, mHeight;
    private TableDataSetObserver mTableAdapterDataSetObserver;
    private boolean mNeedRelayout = true;
    private int mFixedRowTop = 0;
    private int mFixedColumnLeft = 0;
    /**
     * Coordinates for drag and drop column which user select by long tap
     */
    private Rect mSelectedRect;
    private int mSelectedColumn = -1;
    /**
     * Coordinates for drag and drop column which user select by drag and drop scroll
     */
    private Rect mSelectedToRect;
    private int mSelectedToColumn = -1;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public TableLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public TableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mWidthCalc = new LazyIntArrayCalc();
        mHeightCalc = new LazyIntArrayCalc();

//        mHeadViewHolder = null;
        mFixedRowViewHolderList = new LinkedList<>();
        mFixedColumnViewHolderList = new LinkedList<>();
        mBodyViewHolderTable = new LinkedList<>();
        mRecycler = new Recycler();

        mNeedRelayout = true;

        final ViewConfiguration configuration = ViewConfiguration.get(context);

        mSettings = new TableLayoutSettings();
        mSettings
                .setMinimumVelocity(configuration.getScaledMinimumFlingVelocity())
                .setMaximumVelocity(configuration.getScaledMaximumFlingVelocity())
                .setShadowSize(getResources().getDimensionPixelSize(R.dimen.shadow_size))
                .setTouchSlop(configuration.getScaledTouchSlop());
        mScrollMediator =
                ScrollMediator.newBuilder(getContext())
                        .withDraggableView(this)
                        .withScrollableView(this)
                        .withScrollMediatorListener(this)
                        .build();


        setWillNotDraw(false);
    }

    /**
     * Returns the mTableAdapter currently associated with this widget.
     *
     * @return The mTableAdapter used to provide this view's content.
     */
    public TableAdapter getTableAdapter() {
        return mTableAdapter;
    }

    /**
     * Sets the data behind this {@link TableLayout}.
     *
     * @param tableAdapter The {@link TableAdapter} which is responsible for maintaining the data
     *                     backing this list and for producing a view to represent an
     *                     item in that data set.
     */
    public void setTableAdapter(TableAdapter tableAdapter) {
        if (this.mTableAdapter != null) {
            this.mTableAdapter.unregisterDataSetObserver(mTableAdapterDataSetObserver);
        }

        mTableAdapter = tableAdapter;
        mTableAdapterDataSetObserver = new TableAdapterDataSetObserver();
        mTableAdapter.registerDataSetObserver(mTableAdapterDataSetObserver);

        mScrollX = 0;
        mScrollY = 0;
        mFirstColumn = 0;
        mFirstRow = 0;

        mNeedRelayout = true;
        requestLayout();
    }

    public TableLayoutSettings getSettings() {
        return mSettings;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mScrollMediator != null) {
            mScrollMediator.onTouchEvent(ev, getActualScrollX(), getActualScrollY());
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mScrollMediator != null) {
            return mScrollMediator.onTouchEvent(event, getActualScrollX(), getActualScrollY());
        } else {
            return false;
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (mNeedRelayout) {
            mScrollX = x;
            mFirstColumn = 0;

            mScrollY = y;
            mFirstRow = 0;
        } else {
            scrollBy(x - mWidthCalc.getArraySum(1, mFirstColumn) - mScrollX, y - mHeightCalc.getArraySum(1, mFirstRow) - mScrollY);
        }
    }

    @Override
    public void scrollBy(int x, int y) {
        mScrollX += x;
        mScrollY += y;

        if (mNeedRelayout) {
            return;
        }

        scrollBounds();

        //noinspection StatementWithEmptyBody
        if (mScrollX == 0) {
            // no op
        } else if (mScrollX > 0) {
            // loop for all elements in left and are needed to destroy
            while (mWidthCalc.getItem(mFirstColumn + 1) < mScrollX) {

                if (!mFixedRowViewHolderList.isEmpty()) {
                    removeLeft();
                }
                mScrollX -= mWidthCalc.getItem(mFirstColumn + 1);
                mFirstColumn++;
            }
            while (getFilledWidth() < mSettings.getLayoutWidth()) {
                addRight();
            }
        } else {
            while (!mFixedRowViewHolderList.isEmpty() &&
                    getFilledWidth() - mWidthCalc.getItem(mFirstColumn + mFixedRowViewHolderList.size()) >= mSettings.getLayoutWidth()) {
                removeRight();
            }
            if (mFixedRowViewHolderList.isEmpty()) {
                while (mScrollX < 0) {
                    mFirstColumn--;
                    mScrollX += mWidthCalc.getItem(mFirstColumn + 1);
                }
                while (getFilledWidth() < mSettings.getLayoutWidth()) {
                    addRight();
                }
            } else {
                while (0 > mScrollX) {
                    addLeft();
                    mFirstColumn--;
                    mScrollX += mWidthCalc.getItem(mFirstColumn + 1);
                }
            }
        }

        //noinspection StatementWithEmptyBody
        if (mScrollY == 0) {
            // no op
        } else if (mScrollY > 0) {
            while (mHeightCalc.getItem(mFirstRow + 1) < mScrollY) {
                if (!mFixedColumnViewHolderList.isEmpty()) {
                    removeTop();
                }
                mScrollY -= mHeightCalc.getItem(mFirstRow + 1);
                mFirstRow++;
            }
            while (getFilledHeight() < mSettings.getLayoutHeight()) {
                addBottom();
            }
        } else {
            while (!mFixedColumnViewHolderList.isEmpty() &&
                    getFilledHeight() - mHeightCalc.getItem(mFirstRow + mFixedColumnViewHolderList.size()) >= mSettings.getLayoutHeight()) {
                removeBottom();
            }
            if (mFixedColumnViewHolderList.isEmpty()) {
                while (mScrollY < 0) {
                    mFirstRow--;
                    mScrollY += mHeightCalc.getItem(mFirstRow + 1);
                }
                while (getFilledHeight() < mSettings.getLayoutHeight()) {
                    addBottom();
                }
            } else {
                while (0 > mScrollY) {
                    addTop();
                    mFirstRow--;
                    mScrollY += mHeightCalc.getItem(mFirstRow + 1);
                }
            }
        }

        repositionViews();
        awakenScrollBars();
    }

    @Override
    protected int computeHorizontalScrollExtent() {
        final float tableSize = mSettings.getLayoutWidth() - mWidthCalc.getItem(0) + mFixedColumnLeft;
        final float contentSize = mWidthCalc.getArraySum() - mWidthCalc.getItem(0) + mFixedColumnLeft;
        final float percentageOfVisibleView = tableSize / contentSize;

        return Math.round(percentageOfVisibleView * tableSize);
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        final float maxScrollX = mWidthCalc.getArraySum() - mSettings.getLayoutWidth();
        final float percentageOfViewScrolled = getActualScrollX() / maxScrollX;
        final int maxHorizontalScrollOffset = mSettings.getLayoutWidth() - mWidthCalc.getItem(0) + mFixedColumnLeft - computeHorizontalScrollExtent();

        return mFixedColumnLeft + mWidthCalc.getItem(0) + Math.round(percentageOfViewScrolled * maxHorizontalScrollOffset);
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return mSettings.getLayoutWidth();
    }

    @Override
    protected int computeVerticalScrollExtent() {
        final float tableSize = mSettings.getLayoutHeight() - mHeightCalc.getItem(0);
        final float contentSize = mHeightCalc.getArraySum() - mHeightCalc.getItem(0);
        final float percentageOfVisibleView = tableSize / contentSize;

        return Math.round(percentageOfVisibleView * tableSize);
    }

    @Override
    protected int computeVerticalScrollOffset() {
        final float maxScrollY = mHeightCalc.getArraySum() - mSettings.getLayoutHeight();
        final float percentageOfViewScrolled = getActualScrollY() / maxScrollY;
        final int maxVerticalScrollOffset = mSettings.getLayoutHeight() - mFixedRowTop - computeVerticalScrollExtent();

        return mFixedRowTop + mHeightCalc.getItem(0) + Math.round(percentageOfViewScrolled * maxVerticalScrollOffset);
    }

    @Override
    protected int computeVerticalScrollRange() {
        return mSettings.getLayoutHeight();
    }

    public int getActualScrollX() {
        return mScrollX + mWidthCalc.getArraySum(1, mFirstColumn);
    }

    public int getActualScrollY() {
        return mScrollY + mHeightCalc.getArraySum(1, mFirstRow);
    }

    @Override
    public boolean canStartDragging(int x, int y) {
        if (mDragAndDropHolder == null) {
            mDragAndDropHolder = findColumnHeaderViewHolder(x, y);
        }
        return mDragAndDropHolder != null;
    }

    @Override
    public int getMaxScrollX() {
        return Math.max(0, mWidthCalc.getArraySum() - mSettings.getLayoutWidth());
    }

    @Override
    public int getMaxScrollY() {
        return Math.max(0, mHeightCalc.getArraySum() - mSettings.getLayoutHeight());
    }

    @Override
    public int getTouchSlop() {
        return mSettings.getTouchSlop();
    }

    @Override
    public int getMaxVelocity() {
        return mSettings.getMaximumVelocity();
    }

    @Override
    public int getMinVelocity() {
        return mSettings.getMinimumVelocity();
    }

    @Override
    public int getLayoutWidth() {
        return mSettings.getLayoutWidth();
    }

    int getFilledWidth() {
        return mFixedColumnLeft + mWidthCalc.getItem(0)
                + mWidthCalc.getArraySum(mFirstColumn + 1, mFixedRowViewHolderList.size())
                - mScrollX;
    }

    int getFilledHeight() {
        return mFixedRowTop + mHeightCalc.getItem(0)
                + mHeightCalc.getArraySum(mFirstRow + 1, mFixedColumnViewHolderList.size())
                - mScrollY;
    }

    private void addLeft() {
        addLeftOrRight(mFirstColumn - 1, 0);
    }

    private void addTop() {
        addTopAndBottom(mFirstRow - 1, 0);
    }

    private void addRight() {
        final int size = mFixedRowViewHolderList.size();
        addLeftOrRight(mFirstColumn + size, size);
    }

    private void addBottom() {
        final int size = mFixedColumnViewHolderList.size();
        addTopAndBottom(mFirstRow + size, size);
    }

    private void addLeftOrRight(int column, int index) {
        ViewHolder viewHolder = makeViewHolder(FIXED_ROW_INDEX, column, mWidthCalc.getItem(column + 1), mHeightCalc.getItem(0));
        mFixedRowViewHolderList.add(index, viewHolder);

        int i = mFirstRow;
        for (List<ViewHolder> list : mBodyViewHolderTable) {
            viewHolder = makeViewHolder(i, column, mWidthCalc.getItem(column + 1), mHeightCalc.getItem(i + 1));
            list.add(index, viewHolder);
            i++;
        }
    }

    private void addTopAndBottom(int row, int index) {
        ViewHolder viewHolder = makeViewHolder(row, FIXED_COLUMN_INDEX, mWidthCalc.getItem(0), mHeightCalc.getItem(row + 1));
        mFixedColumnViewHolderList.add(index, viewHolder);

        List<ViewHolder> list = new ArrayList<>();
        final int size = mFixedRowViewHolderList.size() + mFirstColumn;
        for (int i = mFirstColumn; i < size; i++) {
            viewHolder = makeViewHolder(row, i, mWidthCalc.getItem(i + 1), mHeightCalc.getItem(row + 1));
            list.add(viewHolder);
        }
        mBodyViewHolderTable.add(index, list);
    }

    private void removeLeft() {
        removeLeftOrRight(0);
    }

    private void removeTop() {
        removeTopOrBottom(0);
    }

    private void removeRight() {
        removeLeftOrRight(mFixedRowViewHolderList.size() - 1);
    }

    private void removeBottom() {
        removeTopOrBottom(mFixedColumnViewHolderList.size() - 1);
    }

    private void removeLeftOrRight(int position) {
        removeView(mFixedRowViewHolderList.remove(position));
        for (List<ViewHolder> list : mBodyViewHolderTable) {
            removeView(list.remove(position));
        }
    }

    private void removeTopOrBottom(int position) {
        removeView(mFixedColumnViewHolderList.remove(position));
        List<ViewHolder> remove = mBodyViewHolderTable.remove(position);
        for (ViewHolder view : remove) {
            removeView(view);
        }
    }

    @Override
    public void removeView(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.tag_view_holder);
        if (viewHolder != null) {
            removeView(viewHolder);
        } else {
            super.removeView(view);
        }
    }

    @SuppressWarnings("unchecked")
    public void removeView(@NonNull ViewHolder viewHolder) {
        super.removeView(viewHolder.getItemView());

        final int typeView = viewHolder.getItemType();
        if (typeView != ItemType.IGNORE) {
            mTableAdapter.onViewHolderRecycled(viewHolder);
            mRecycler.pushRecycledView(viewHolder, typeView);
        }
    }

    private void repositionViews() {
        int left, top, right, bottom, i;

        left = mWidthCalc.getItem(0) + mFixedColumnLeft - mScrollX;
        i = mFirstColumn;
        for (ViewHolder viewHolder : mFixedRowViewHolderList) {
            right = left + mWidthCalc.getItem(++i);
            viewHolder.getItemView().layout(left, mFixedRowTop, right, mFixedRowTop + mHeightCalc.getItem(0));
            left = right;
        }

        top = mHeightCalc.getItem(0) + mFixedRowTop - mScrollY;
        i = mFirstRow;
        for (ViewHolder viewHolder : mFixedColumnViewHolderList) {
            bottom = top + mHeightCalc.getItem(++i);
            viewHolder.getItemView().layout(0, top, mWidthCalc.getItem(0), bottom);
            top = bottom;
        }

        top = mHeightCalc.getItem(0) + mFixedRowTop - mScrollY;
        i = mFirstRow;
        for (List<ViewHolder> list : mBodyViewHolderTable) {
            bottom = top + mHeightCalc.getItem(++i);
            left = mWidthCalc.getItem(0) + mFixedColumnLeft - mScrollX;
            int j = mFirstColumn;
            for (ViewHolder viewHolder : list) {
                right = left + mWidthCalc.getItem(++j);
                viewHolder.getItemView().layout(left, top, right, bottom);
                left = right;
            }
            top = bottom;
        }
        invalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("TableLay", "onMeasure");
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        final int measuredWidth;
        final int measuredHeight;

        if (mTableAdapter != null) {
            if (mNeedRelayout) {
                // get row and columns count
                mRowCount = mTableAdapter.getRowCount();
                mColumnCount = mTableAdapter.getColumnCount();

                // prepare width array
                int widths[] = new int[mColumnCount + 1];
                // -1 header index in the adapter // TODO Need To fix this!!
                for (int i = -1; i < mColumnCount; i++) {
                    // calculate and save width for each column item
                    widths[i + 1] += mTableAdapter.getItemWidth(i);
                }
                mWidthCalc.setArray(widths);

                // prepare width array
                int heights[] = new int[mRowCount + 1];
                // -1 header index in the adapter // TODO Need To fix this!!
                for (int i = -1; i < mRowCount; i++) {
                    // calculate and save width for each row item
                    heights[i + 1] += mTableAdapter.getItemHeight(i);
                }
                mHeightCalc.setArray(heights);
            }

            if (widthMode == MeasureSpec.AT_MOST) {
                measuredWidth = Math.min(widthSize, mWidthCalc.getArraySum());
            } else if (widthMode == MeasureSpec.UNSPECIFIED) {
                measuredWidth = mWidthCalc.getArraySum();
            } else {
                measuredWidth = widthSize;
                if (mNeedRelayout) {
                    int sumArray = mWidthCalc.getArraySum();
                    if (sumArray < widthSize) {
                        // multiply width by scaleFactor.
                        final float factor = widthSize / (float) sumArray;
                        int firstColumnWidth = widthSize;
                        for (int i = 1; i < mWidthCalc.getSize(); i++) {
                            int itemWidth = Math.round(mWidthCalc.getItem(i) * factor);
                            mWidthCalc.setItem(i, itemWidth);
                            firstColumnWidth -= itemWidth;
                        }
                        mWidthCalc.invalidate();
                        mWidthCalc.setItem(0, firstColumnWidth);
                        mWidthCalc.invalidate();
                    }
                }
            }

            if (heightMode == MeasureSpec.AT_MOST) {
                measuredHeight = Math.min(heightSize, mHeightCalc.getArraySum());
            } else if (heightMode == MeasureSpec.UNSPECIFIED) {
                measuredHeight = mHeightCalc.getArraySum();
            } else {
                measuredHeight = heightSize;
            }
        } else {
            if (heightMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
                measuredWidth = 0;
                measuredHeight = 0;
            } else {
                measuredWidth = widthSize;
                measuredHeight = heightSize;
            }
        }

        if (mFirstRow >= mRowCount || getMaxScrollY() - getActualScrollY() < 0) {
            mFirstRow = 0;
            mScrollY = Integer.MAX_VALUE;
        }
        if (mFirstColumn >= mColumnCount || getMaxScrollX() - getActualScrollX() < 0) {
            mFirstColumn = 0;
            mScrollX = Integer.MAX_VALUE;
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("TableLay", "onLayout");
        if (mNeedRelayout || changed) {
            mNeedRelayout = false;
            // clear all data
            resetTable();

            if (mTableAdapter != null) {
                mSettings.setLayoutWidth(r - l);
                mSettings.setLayoutHeight(b - t);

                int left, top, right, bottom;

//                mHeadViewHolder = makeAndSetup(FIXED_ROW_INDEX, FIXED_COLUMN_INDEX, 0, 0, mWidthCalc.getItem(0), mHeightCalc.getItem(0));

                scrollBounds();
                adjustFirstCellsAndScroll();

                left = mWidthCalc.getItem(0) + mFixedColumnLeft - mScrollX;
                for (int i = mFirstColumn; i < mColumnCount && left < mSettings.getLayoutWidth(); i++) {
                    right = left + mWidthCalc.getItem(i + 1);
                    final ViewHolder viewHolder = makeAndSetup(FIXED_ROW_INDEX, i, left, 0, right, mHeightCalc.getItem(0));
                    mFixedRowViewHolderList.add(viewHolder);
                    left = right;
                }

                top = mHeightCalc.getItem(0) + mFixedRowTop - mScrollY;
                for (int i = mFirstRow; i < mRowCount && top < mSettings.getLayoutHeight(); i++) {
                    bottom = top + mHeightCalc.getItem(i + 1);
                    final ViewHolder viewHolder = makeAndSetup(i, FIXED_COLUMN_INDEX, 0, top, mWidthCalc.getItem(0), bottom);
                    mFixedColumnViewHolderList.add(viewHolder);
                    top = bottom;
                }

                top = mHeightCalc.getItem(0) + mFixedRowTop - mScrollY;
                for (int i = mFirstRow; i < mRowCount && top < mSettings.getLayoutHeight(); i++) {
                    bottom = top + mHeightCalc.getItem(i + 1);
                    left = mWidthCalc.getItem(0) + mFixedColumnLeft - mScrollX;
                    List<ViewHolder> list = new LinkedList<>();
                    for (int j = mFirstColumn; j < mColumnCount && left < mSettings.getLayoutWidth(); j++) {
                        right = left + mWidthCalc.getItem(j + 1);
                        final ViewHolder viewHolder = makeAndSetup(i, j, left, top, right, bottom);
                        list.add(viewHolder);
                        left = right;
                    }
                    mBodyViewHolderTable.add(list);
                    top = bottom;
                }
            }
        }
    }

    private void scrollBounds() {
        mScrollX = scrollBounds(mScrollX, mFirstColumn, mWidthCalc, mSettings.getLayoutWidth() - mFixedColumnLeft);
        mScrollY = scrollBounds(mScrollY, mFirstRow, mHeightCalc, mSettings.getLayoutHeight() - mFixedRowTop);
    }

    private int scrollBounds(int desiredScroll, int firstCell, LazyIntArrayCalc sizes, int viewSize) {
        //noinspection StatementWithEmptyBody
        if (desiredScroll == 0) {
            // no op
        } else if (desiredScroll < 0) {
            desiredScroll = Math.max(desiredScroll, -sizes.getArraySum(1, firstCell));
        } else {
            desiredScroll = Math.min(desiredScroll, Math.max(0, sizes.getArraySum(firstCell + 1, sizes.getSize() - 1 - firstCell) + sizes.getItem(0) - viewSize));
        }
        return desiredScroll;
    }

    private void adjustFirstCellsAndScroll() {
        int values[];

        values = adjustFirstCellsAndScroll(mScrollX, mFirstColumn, mWidthCalc);
        mScrollX = values[0];
        mFirstColumn = values[1];

        values = adjustFirstCellsAndScroll(mScrollY, mFirstRow, mHeightCalc);
        mScrollY = values[0];
        mFirstRow = values[1];
    }

    private int[] adjustFirstCellsAndScroll(int scroll, int firstCell, LazyIntArrayCalc calc) {
        if (scroll == 0) {
            // no op
        } else if (scroll > 0) {
            while (calc.getItem(firstCell + 1) < scroll) {
                firstCell++;
                scroll -= calc.getItem(firstCell);
            }
        } else {
            while (scroll < 0) {
                scroll += calc.getItem(firstCell);
                firstCell--;
            }
        }
        return new int[]{scroll, firstCell};
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    private void setAlpha(ImageView imageView, float alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            imageView.setAlpha(alpha);
        } else {
            imageView.setAlpha(Math.round(alpha * 255));
        }
    }

    /**
     * Clear view holders, remove all views
     */
    private void resetTable() {
//        mHeadViewHolder = null;
        mFixedRowViewHolderList.clear();
        mFixedColumnViewHolderList.clear();
        mBodyViewHolderTable.clear();

        removeAllViews();
    }

    private ViewHolder makeAndSetup(int row, int column, int left, int top, int right, int bottom) {
        final ViewHolder viewHolder = makeViewHolder(row, column, right - left, bottom - top);
        viewHolder.getItemView().layout(left, top, right, bottom);
        return viewHolder;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean result;

        final ViewHolder viewHolder = (ViewHolder) child.getTag(R.id.tag_view_holder);

        canvas.save();

        //noinspection StatementWithEmptyBody
        if (viewHolder == null) {
            //ignore
        } else if (viewHolder.getRowIndex() == FIXED_ROW_INDEX && viewHolder.getColumnIndex() == FIXED_COLUMN_INDEX) {
            canvas.clipRect(
                    mFixedColumnLeft,
                    mFixedRowTop,
                    mFixedColumnLeft + mWidthCalc.getItem(0),
                    mFixedRowTop + mHeightCalc.getItem(0));
        } else {
            if (viewHolder.getRowIndex() == FIXED_ROW_INDEX) {
                canvas.clipRect(
                        mWidthCalc.getItem(0) + mFixedColumnLeft,
                        mFixedRowTop,
                        canvas.getWidth(),
                        mFixedRowTop + canvas.getHeight());
            } else if (viewHolder.getColumnIndex() == FIXED_COLUMN_INDEX) {
                canvas.clipRect(
                        mFixedColumnLeft,
                        mHeightCalc.getItem(0) + mFixedRowTop,
                        canvas.getWidth(),
                        canvas.getHeight());
            } else {
                canvas.clipRect(
                        mWidthCalc.getItem(0) + mFixedColumnLeft,
                        mHeightCalc.getItem(0) + mFixedRowTop,
                        canvas.getWidth(),
                        canvas.getHeight());
            }
        }
        result = super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        return result;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mScrollMediator != null && mScrollMediator.isDragging()) {
            if (mDragAndDropHolder != null) {
                View view = mDragAndDropHolder.getItemView();
                if (mSelectedRect == null) {
                    mSelectedRect = new Rect(view.getLeft(), view.getTop(),
                            view.getRight(), mSettings.getLayoutHeight());
                }
                Paint myPaint = new Paint();
                myPaint.setColor(Color.argb(95, 100, 100, 100));
                myPaint.setStyle(Paint.Style.FILL);

                canvas.drawRect(mSelectedRect.left, mSelectedRect.top,
                        mSelectedRect.right, mSelectedRect.bottom, myPaint);


                if (mSelectedToRect != null) {
                    Paint myPaintTo = new Paint();
                    myPaintTo.setColor(Color.GREEN);
                    myPaintTo.setStrokeWidth(10);
                    myPaintTo.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(mSelectedToRect.left, mSelectedToRect.top,
                            mSelectedToRect.right, mSelectedToRect.bottom, myPaintTo);
                }
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_SUPER_STATE, super.onSaveInstanceState());

        if (!mBodyViewHolderTable.isEmpty() && !mBodyViewHolderTable.get(0).isEmpty()) {
            ViewHolder firstVisibleVH = mBodyViewHolderTable.get(0).get(0);

            int scX = mWidthCalc.getArraySum(0, firstVisibleVH.getColumnIndex() + 1);
            int scY = mHeightCalc.getArraySum(2, firstVisibleVH.getRowIndex() - 1);
            bundle.putInt(EXTRA_SCROLL_X, scX + 5);
            bundle.putInt(EXTRA_SCROLL_Y, scY + 5);
        }
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = ((Bundle) state);
            super.onRestoreInstanceState(bundle.getParcelable(EXTRA_SUPER_STATE));
            scrollTo(bundle.getInt(EXTRA_SCROLL_X), bundle.getInt(EXTRA_SCROLL_Y));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @SuppressWarnings("unchecked")
    private ViewHolder makeViewHolder(int row, int column, int w, int h) {
        final int itemType = mTableAdapter.getItemType(row, column);
        ViewHolder viewHolder = null;
        if (itemType != ItemType.IGNORE) {
            viewHolder = mRecycler.popRecycledViewHolder(itemType);
        }
        if (viewHolder == null) {
            viewHolder = mTableAdapter.onCreateViewHolder(TableLayout.this, itemType);
        }
        viewHolder.setItemType(itemType);
        viewHolder.setRowIndex(row);
        viewHolder.setColumnIndex(column);
        viewHolder.getItemView().setTag(R.id.tag_view_holder, viewHolder);
        viewHolder.getItemView().measure(
                MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));

        addTableView(viewHolder.getItemView(), row, column);

        mTableAdapter.onBindViewHolder(viewHolder, row, column);
        return viewHolder;
    }

    /**
     * find header view holder
     *
     * @param x touch x
     * @param y touch y
     * @return header view holder. Can be null.
     */
    @Nullable
    private ViewHolder findColumnHeaderViewHolder(int x, int y) {
        for (ViewHolder vh : mFixedRowViewHolderList) {
            View view = vh.getItemView();
            float viewX = view.getX();
            float viewY = getTop();
            if (y >= viewY && y <= viewY + view.getHeight()) {
                if (x >= viewX && x <= viewX + view.getWidth()) {
                    return vh;
                }
            }
        }
        return null;
    }

    /**
     * find column by x,y which entered into column area.
     *
     * @param x touch x
     * @param y ouch y
     * @return Column rec or null
     */
    @Nullable
    private Rect getColumnRect(int x, int y) {
        for (ViewHolder vh : mFixedRowViewHolderList) {
            View view = vh.getItemView();
            float viewX = view.getX();
            if (x >= viewX && x <= viewX + view.getWidth()) {
                return new Rect(view.getLeft(), view.getTop(),
                        view.getRight(), mSettings.getLayoutHeight());
            }
        }
        return null;
    }

    /**
     * find column by x,y which entered into column area.
     *
     * @param x touch x
     * @param y ouch y
     * @return Column rec or null
     */
    @Nullable
    private int getColumnIndex(int x, int y) {
        for (ViewHolder vh : mFixedRowViewHolderList) {
            View view = vh.getItemView();
            float viewX = view.getX();
            if (x >= viewX && x <= viewX + view.getWidth()) {
                return vh.getColumnIndex();
            }
        }
        return -1;
    }

    private void addTableView(View view, int row, int column) {
        if (row == FIXED_ROW_INDEX && column == FIXED_COLUMN_INDEX) {
            addView(view, getChildCount() - 4);
        } else if (row == FIXED_ROW_INDEX || column == FIXED_COLUMN_INDEX) {
            addView(view, getChildCount() - 5);
        } else {
            addView(view, 0);
        }
    }

    @Override
    public void onDragAndDropStart(int x, int y) {
        if (mDragAndDropHolder == null) {
            mDragAndDropHolder = findColumnHeaderViewHolder(x, y);
        }
        if (mDragAndDropHolder != null) {
            mSelectedColumn = mDragAndDropHolder.getColumnIndex();
        }
        invalidate();
    }

    @Override
    public void onDragAndDropScroll(int x, int y) {
        Log.e("DragAndDrop", "x = " + x + " | y = " + y);
        if (mSelectedRect != null) {
            int halfWidth = mSelectedRect.width() / 2;
            mSelectedRect.left = x - halfWidth;
            mSelectedRect.right = x + halfWidth;
            mSelectedToRect = getColumnRect(x, y);
        }
        invalidate();
    }

    @Override
    public void onDragAndDropEnd(int x, int y) {

        if (mSelectedToRect != null) {
            mSelectedToColumn = getColumnIndex(x, y);
        }

        if (mSelectedColumn != -1 && mSelectedToColumn != -1) {
            int[] locationOnScreen = new int[2];
            int[] locationToOnScreen = new int[2];
            mBodyViewHolderTable.get(0).get(mSelectedColumn).getItemView().getLocationOnScreen(locationOnScreen);
            mBodyViewHolderTable.get(0).get(mSelectedToColumn).getItemView().getLocationInWindow(locationToOnScreen);

            for (int i = 0; i < mBodyViewHolderTable.size(); i++) {
                View view = mBodyViewHolderTable.get(i).get(mSelectedColumn).getItemView();
                view.bringToFront();
            }


            ValueAnimator animator = new ValueAnimator();
            animator.setIntValues(0, locationToOnScreen[0] - locationOnScreen[0]);
            animator.setDuration(500);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    for (int i = 0; i < mBodyViewHolderTable.size(); i++) {
                        View view = mBodyViewHolderTable.get(i).get(mSelectedColumn).getItemView();
                        view.setTranslationX(value);
                    }
                    mTableAdapter.notifyDataSetChanged();
                }
            });

            ValueAnimator animatorTo = new ValueAnimator();
            animatorTo.setIntValues(0, locationOnScreen[0] - locationToOnScreen[0]);
            animatorTo.setDuration(500);
            animatorTo.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    for (int i = 0; i < mBodyViewHolderTable.size(); i++) {
                        View view = mBodyViewHolderTable.get(i).get(mSelectedToColumn).getItemView();
                        view.setTranslationX(value);
                    }
                    mTableAdapter.notifyDataSetChanged();
                }
            });

            AnimatorSet set = new AnimatorSet();
            set.playTogether(animator, animatorTo);
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mTableAdapter.changeColumns(mSelectedColumn, mSelectedToColumn);
                    for (int i = 0; i < mBodyViewHolderTable.size(); i++) {
                        View view = mBodyViewHolderTable.get(i).get(mSelectedColumn).getItemView();
                        view.setTranslationX(0);
                    }
                    for (int i = 0; i < mBodyViewHolderTable.size(); i++) {
                        View view = mBodyViewHolderTable.get(i).get(mSelectedToColumn).getItemView();
                        view.setTranslationX(0);
                    }
                    mTableAdapter.notifyDataSetChanged();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            set.start();
        }
        mDragAndDropHolder = null;
        mSelectedRect = null;
        mSelectedToRect = null;
        invalidate();
    }


    private class TableAdapterDataSetObserver implements TableDataSetObserver {

        @SuppressWarnings("unchecked")
        @Override
        public void notifyDataSetChanged() {
            notifyHeadViewChanged();
            for (ViewHolder vh : mFixedRowViewHolderList) {
                mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
            }
            for (ViewHolder vh : mFixedColumnViewHolderList) {
                mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
            }
            for (List<ViewHolder> list : mBodyViewHolderTable) {
                for (ViewHolder vh : list) {
                    mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
                }
            }
        }

        @Override
        public void notifyLayoutChanged() {
            mNeedRelayout = true;
            requestLayout();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void notifyItemChanged(int rowIndex, int columnIndex) {
            if (rowIndex == FIXED_ROW_INDEX && columnIndex == FIXED_COLUMN_INDEX) {
                notifyHeadViewChanged();
            } else if (rowIndex == FIXED_ROW_INDEX) {
                for (ViewHolder vh : mFixedRowViewHolderList) {
                    if (vh.getColumnIndex() == columnIndex && vh.getRowIndex() == rowIndex) {
                        mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
                    }
                }
            } else if (columnIndex == FIXED_COLUMN_INDEX) {
                for (ViewHolder vh : mFixedColumnViewHolderList) {
                    if (vh.getColumnIndex() == columnIndex && vh.getRowIndex() == rowIndex) {
                        mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
                    }
                }
            } else {
                for (List<ViewHolder> list : mBodyViewHolderTable) {
                    for (ViewHolder vh : list) {
                        if (vh.getColumnIndex() == columnIndex && vh.getRowIndex() == rowIndex) {
                            mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
                        }
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void notifyRowChanged(int rowIndex) {
            for (ViewHolder vh : mFixedColumnViewHolderList) {
                if (vh.getRowIndex() == rowIndex) {
                    mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
                }
            }
            if (rowIndex == FIXED_ROW_INDEX) {
                for (ViewHolder vh : mFixedRowViewHolderList) {
                    mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
                }
            } else {
                for (List<ViewHolder> list : mBodyViewHolderTable) {
                    for (ViewHolder vh : list) {
                        if (vh.getRowIndex() == rowIndex) {
                            mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
                        }
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void notifyColumnChanged(int columnIndex) {
            for (ViewHolder vh : mFixedRowViewHolderList) {
                if (vh.getColumnIndex() == columnIndex) {
                    mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
                }
            }
            if (columnIndex == FIXED_COLUMN_INDEX) {
                for (ViewHolder vh : mFixedColumnViewHolderList) {
                    mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
                }
            } else {
                for (List<ViewHolder> list : mBodyViewHolderTable) {
                    for (ViewHolder vh : list) {
                        if (vh.getColumnIndex() == columnIndex) {
                            mTableAdapter.onBindViewHolder(vh, vh.getRowIndex(), vh.getColumnIndex());
                        }
                    }
                }
            }
        }

        @Override
        public void notifyHeadViewChanged() {
//            mHeadViewHolder = makeAndSetup(FIXED_ROW_INDEX, FIXED_COLUMN_INDEX, 0, 0, mWidthCalc.getItem(0), mHeightCalc.getItem(0));
        }
    }
}