package com.cleveroad.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.util.SparseArrayCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TableLayout extends ViewGroup implements ScrollHelper.ScrollHelperListener, TableDataSetObserver {

    private static final String EXTRA_STATE_SUPER = "EXTRA_STATE_SUPER";
    private static final String EXTRA_STATE_VIEW_GROUP = "EXTRA_STATE_VIEW_GROUP";

    private static final int SHADOW_THICK = 25;

    /**
     * Matrix with item view holders
     */
    private SparseMatrix<ViewHolder> mViewHolders;
    /**
     * Map with column's headers view holders
     */
    private SparseArrayCompat<ViewHolder> mHeaderColumnViewHolders;
    /**
     * Map with row's headers view holders
     */
    private SparseArrayCompat<ViewHolder> mHeaderRowViewHolders;
    /**
     * Contained with drag and drop points
     */
    private DragAndDropPoints mDragAndDropPoints;
    /**
     * Container with layout state
     */
    private TableState mState;
    /**
     * Item's widths and heights manager.
     */
    private TableManager mManager;

    /**
     * Need to fix columns bounce when dragging header.
     * Saved absolute point when header switched in drag and drop mode.
     */

    private Point mLastSwitchHeaderPoint;
    /**
     * Contains visible area rect. Left top point and right bottom
     */
    private Rect mVisibleArea;
    /**
     * View holder in the left top corner.
     */
    @Nullable
    private ViewHolder mLeftTopViewHolder;
    /**
     * Table layout adapter
     */
    private DataTableLayoutAdapter<ViewHolder> mAdapter;
    /**
     * Recycle ViewHolders
     */
    private Recycler mRecycler;
    /**
     * Keep layout settings
     */
    private TableLayoutSettings mSettings;

    /**
     * Detect all gestures on layout.
     */
    private ScrollHelper mScrollHelper;
    /**
     * Runnable helps with fling events
     */
    private SmoothScrollRunnable mScrollerRunnable;
    /**
     * Runnable helps with scroll in drag and drop mode
     */
    private DragAndDropScrollRunnable mScrollerDragAndDropRunnable;

    /**
     * Helps work with row' or column' shadows.
     */
    private ShadowHelper mShadowHelper;

    /**
     * Instant state
     */
    @Nullable
    private TableInstanceSaver mSaver;

    public TableLayout(Context context) {
        super(context);
        init(context);
    }

    public TableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttrs(context, attrs);
    }

    public TableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttrs(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        initAttrs(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            // calculate layout width and height
            mSettings.setLayoutWidth(r - l);
            mSettings.setLayoutHeight(b - t);
            // init data
            initItems();
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TableLayout,
                0, 0);

        try {
            mSettings.setHeaderFixed(a.getBoolean(R.styleable.TableLayout_fixedHeaders, true));
            mSettings.setCellMargin(a.getDimensionPixelSize(R.styleable.TableLayout_cellMargin, 0));
            mSettings.setSolidRowHeader(a.getBoolean(R.styleable.TableLayout_solidRowHeaders, true));
        } finally {
            a.recycle();
        }

    }

    private void init(Context context) {
        mViewHolders = new SparseMatrix<>();
        mHeaderColumnViewHolders = new SparseArrayCompat<>();
        mHeaderRowViewHolders = new SparseArrayCompat<>();
        mDragAndDropPoints = new DragAndDropPoints();
        mState = new TableState();
        mManager = new TableManager();
        mLastSwitchHeaderPoint = new Point();
        mVisibleArea = new Rect();
        // init scroll and fling helpers
        mScrollerRunnable = new SmoothScrollRunnable(this);
        mScrollerDragAndDropRunnable = new DragAndDropScrollRunnable(this);
        mRecycler = new Recycler();
        mSettings = new TableLayoutSettings();
        mScrollHelper = new ScrollHelper(context);
        mScrollHelper.setListener(this);
        mShadowHelper = new ShadowHelper();
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_STATE_SUPER, super.onSaveInstanceState());

        mSaver = new TableInstanceSaver();
        mSaver.mScrollX = mState.getScrollX();
        mSaver.mScrollY = mState.getScrollY();
        if (mAdapter != null) {
            mAdapter.onSaveInstanceState(bundle);
        }
        bundle.putParcelable(EXTRA_STATE_VIEW_GROUP, mSaver);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            Parcelable parcelable = bundle.getParcelable(EXTRA_STATE_VIEW_GROUP);
            if (parcelable != null && parcelable instanceof TableInstanceSaver) {
                mSaver = (TableInstanceSaver) parcelable;
            }
            if (mAdapter != null) {
                mAdapter.onRestoreInstanceState(bundle);
            }
            state = bundle.getParcelable(EXTRA_STATE_SUPER);
        }
        super.onRestoreInstanceState(state);
    }

    private void initItems() {
        if (mAdapter == null) {
            // clear
            mManager.clear();
            recycleViewHolders(true);
            return;
        }

        // init manager
        mManager.init(mAdapter.getRowCount(), mAdapter.getColumnCount());

        // calculate widths
        for (int count = mManager.getColumnCount(), i = 0; i < count; i++) {
            int item = mAdapter.getColumnWidth(i);
            mManager.putColumnWidth(i, item);
        }

        // calculate heights
        for (int count = mManager.getRowCount(), i = 0; i < count; i++) {
            int item = mAdapter.getRowHeight(i);
            mManager.putRowHeight(i, item);
        }

        // set header's width and height. Set 0 in case < 0
        mManager.setHeaderColumnHeight(Math.max(0, mAdapter.getHeaderColumnHeight()));
        mManager.setHeaderRowWidth(Math.max(0, mAdapter.getHeaderRowWidth()));

        // start calculating full width and full height
        mManager.invalidate();

        // show items in this area
        mVisibleArea.set(mState.getScrollX(),
                mState.getScrollY(),
                mState.getScrollX() + mSettings.getLayoutWidth(),
                mState.getScrollY() + mSettings.getLayoutHeight());
        addViewHolders(mVisibleArea);
        if (mSaver != null) {
            scrollTo(mSaver.mScrollX, mSaver.mScrollY);
            mSaver = null;
        }
    }

    /**
     * Set adapter with IMMUTABLE data.
     * Create wrapper with links between layout rows, columns and data rows, columns.
     * On drag and drop event just change links but not change data in adapter.
     *
     * @param adapter TableLayout adapter
     */
    @SuppressWarnings("unchecked")
    public void setAdapter(@Nullable TableAdapter adapter) {
        if (mAdapter != null) {
            // remove observers from old adapter
            mAdapter.unregisterDataSetObserver(this);
        }

        if (adapter != null) {
            // wrap adapter
            mAdapter = new LinkedTableAdapterImpl<>(adapter, mSettings.isSolidRowHeader());
            // register notify callbacks
            mAdapter.registerDataSetObserver(this);
            adapter.registerDataSetObserver(new DataSetObserverProxy(mAdapter));
        } else {
            // remove adapter
            mAdapter = null;
        }

        if (mSettings.getLayoutHeight() != 0 && mSettings.getLayoutWidth() != 0) {
            // if layout has width and height
            initItems();
        }
    }

    /**
     * Set adapter with MUTABLE data.
     * You need to implement switch rows and columns methods.
     * On drag and drop event calls {@link DataTableLayoutAdapter#changeColumns(int, int)} and
     * {@link DataTableLayoutAdapter#changeRows(int, int, boolean)}
     * <p>
     * DO NOT USE WITH BIG DATA!!
     *
     * @param adapter DataTableLayoutAdapter adapter
     */
    @SuppressWarnings("unchecked")
    public void setAdapter(@Nullable DataTableLayoutAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(this);
        }
        mAdapter = adapter;

        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(this);
        }

        if (mSettings.getLayoutHeight() != 0 && mSettings.getLayoutWidth() != 0) {
            initItems();
        }
    }

    /**
     * When used adapter with IMMUTABLE data, returns rows position modifications
     * (old position -> new position)
     *
     * @return row position modification map. Includes only modified row numbers
     */
    @SuppressWarnings("unchecked")
    public Map<Integer, Integer> getLinkedAdapterRowsModifications() {
        return mAdapter instanceof LinkedTableAdapterImpl ?
                ((LinkedTableAdapterImpl) mAdapter).getRowsModifications() :
                Collections.<Integer, Integer>emptyMap();
    }

    /**
     * When used adapter with IMMUTABLE data, returns columns position modifications
     * (old position -> new position)
     *
     * @return row position modification map. Includes only modified column numbers
     */
    @SuppressWarnings("unchecked")
    public Map<Integer, Integer> getLinkedAdapterColumnsModifications() {
        return mAdapter instanceof LinkedTableAdapterImpl ?
                ((LinkedTableAdapterImpl) mAdapter).getColumnsModifications() :
                Collections.<Integer, Integer>emptyMap();
    }

    @Override
    public void scrollTo(int x, int y) {
        int absoluteX = x;
        int absoluteY = y;

        int shadowShiftX = mManager.getColumnCount() * mSettings.getCellMargin();
        int shadowShiftY = mManager.getRowCount() * mSettings.getCellMargin();

        long maxX = mManager.getFullWidth() + shadowShiftX;
        long maxY = mManager.getFullHeight() + shadowShiftY;

        if (x < 0) {
            // scroll over view to the left
            absoluteX = 0;
        } else if (mSettings.getLayoutWidth() + x > maxX) {
            // scroll over view to the right
            absoluteX = (int) (maxX - mSettings.getLayoutWidth());
        }

        mState.setScrollX(absoluteX);

        if (y < 0) {
            // scroll over view to the top
            absoluteY = 0;
        } else if (mSettings.getLayoutHeight() + absoluteY > maxY) {
            // scroll over view to the bottom
            absoluteY = (int) (maxY - mSettings.getLayoutHeight());
        }
        mState.setScrollY(absoluteY);

        if (mAdapter != null) {
            // refresh views
            recycleViewHolders();
            mVisibleArea.set(mState.getScrollX(),
                    mState.getScrollY(),
                    mState.getScrollX() + mSettings.getLayoutWidth(),
                    mState.getScrollY() + mSettings.getLayoutHeight());
            addViewHolders(mVisibleArea);
            refreshViewHolders();
        }
    }

    @Override
    public void scrollBy(int x, int y) {
        // block scroll one axle
        x = mState.isRowDragging() ? 0 : x;
        y = mState.isColumnDragging() ? 0 : y;

        int diffX = x;
        int diffY = y;

        int shadowShiftX = mManager.getColumnCount() * mSettings.getCellMargin();
        int shadowShiftY = mManager.getRowCount() * mSettings.getCellMargin();

        long maxX = mManager.getFullWidth() + shadowShiftX;
        long maxY = mManager.getFullHeight() + shadowShiftY;

        if (mState.getScrollX() + x < 0) {
            // scroll over view to the left
            diffX = mState.getScrollX();
            mState.setScrollX(0);
        } else if (mSettings.getLayoutWidth() > maxX) {
            // few items and we have free space.
            diffX = 0;
            mState.setScrollX(0);
        } else if (mState.getScrollX() + mSettings.getLayoutWidth() + x > maxX) {
            // scroll over view to the right
            diffX = (int) (maxX - mState.getScrollX() - mSettings.getLayoutWidth());

            mState.setScrollX(mState.getScrollX() + diffX);
        } else {
            mState.setScrollX(mState.getScrollX() + x);
        }

        if (mState.getScrollY() + y < 0) {
            // scroll over view to the top
            diffY = mState.getScrollY();
            mState.setScrollY(0);
        } else if (mState.getScrollY() > maxY) {
            // few items and we have free space.
            diffY = 0;
            mState.setScrollY(0);
        } else if (mState.getScrollY() + mSettings.getLayoutHeight() + y > maxY) {
            // scroll over view to the bottom
            diffY = (int) (maxY - mState.getScrollY() - mSettings.getLayoutHeight());
            mState.setScrollY(mState.getScrollY() + diffY);
        } else {
            mState.setScrollY(mState.getScrollY() + y);
        }

        if (diffX == 0 && diffY == 0) {
            return;
        }

        if (mAdapter != null) {
            // refresh views
            recycleViewHolders();
            mVisibleArea.set(mState.getScrollX(),
                    mState.getScrollY(),
                    mState.getScrollX() + mSettings.getLayoutWidth(),
                    mState.getScrollY() + mSettings.getLayoutHeight());
            addViewHolders(mVisibleArea);
            refreshViewHolders();
        }
    }

    /**
     * Refresh all view holders
     */
    private void refreshViewHolders() {
        if (mAdapter != null) {
            for (int count = mHeaderColumnViewHolders.size(), i = 0; i < count; i++) {
                int key = mHeaderColumnViewHolders.keyAt(i);
                // get the object by the key.
                ViewHolder holder = mHeaderColumnViewHolders.get(key);
                if (holder != null) {
                    // column header
                    refreshHeaderColumnViewHolder(holder);
                }

            }

            for (int count = mHeaderRowViewHolders.size(), i = 0; i < count; i++) {
                int key = mHeaderRowViewHolders.keyAt(i);
                // get the object by the key.
                ViewHolder holder = mHeaderRowViewHolders.get(key);
                if (holder != null) {
                    // column header
                    refreshHeaderRowViewHolder(holder);
                }

            }

            if (mLeftTopViewHolder != null) {
                refreshLeftTopHeaderViewHolder(mLeftTopViewHolder);
            }

            for (ViewHolder holder : mViewHolders.getAll()) {
                if (holder != null) {
                    // cell item
                    refreshItemViewHolder(holder, mState.isRowDragging(), mState.isColumnDragging());
                }
            }
        }
    }

    /**
     * Refresh current item view holder.
     *
     * @param holder           current view holder
     * @param isRowDragging    row dragging state
     * @param isColumnDragging column dragging state
     */
    private void refreshItemViewHolder(@NonNull ViewHolder holder,
                                       boolean isRowDragging, boolean isColumnDragging) {

        int left = mManager.getColumnsWidth(0, Math.max(0, holder.getColumnIndex()));
        int top = mManager.getRowsHeight(0, Math.max(0, holder.getRowIndex()));
        View view = holder.getItemView();
        if (isColumnDragging && holder.isDragging() && mDragAndDropPoints.getOffset().x > 0) {
            // visible dragging column. Calculate left offset using drag and drop points.
            left = mState.getScrollX() + mDragAndDropPoints.getOffset().x - view.getWidth() / 2 - mManager.getHeaderRowWidth();
            view.bringToFront();
        } else if (isRowDragging && holder.isDragging() && mDragAndDropPoints.getOffset().y > 0) {
            // visible dragging row. Calculate top offset using drag and drop points.
            top = mState.getScrollY() + mDragAndDropPoints.getOffset().y - view.getHeight() / 2 - mManager.getHeaderColumnHeight();
            view.bringToFront();
        }
        int leftMargin = holder.getColumnIndex() * mSettings.getCellMargin() + mSettings.getCellMargin();
        int topMargin = holder.getRowIndex() * mSettings.getCellMargin() + mSettings.getCellMargin();
        // update layout position
        view.layout(left - mState.getScrollX() + mManager.getHeaderRowWidth() + leftMargin,
                top - mState.getScrollY() + mManager.getHeaderColumnHeight() + topMargin,
                left + mManager.getColumnWidth(holder.getColumnIndex()) - mState.getScrollX() + mManager.getHeaderRowWidth() + leftMargin,
                top + mManager.getRowHeight(holder.getRowIndex()) - mState.getScrollY() + mManager.getHeaderColumnHeight() + topMargin);
    }

    /**
     * Refresh current item view holder with default parameters.
     *
     * @param holder current view holder
     */

    private void refreshItemViewHolder(ViewHolder holder) {
        refreshItemViewHolder(holder, false, false);
    }

    /**
     * Refresh current column header view holder.
     *
     * @param holder current view holder
     */
    private void refreshHeaderColumnViewHolder(ViewHolder holder) {

        int left = mManager.getColumnsWidth(0, Math.max(0, holder.getColumnIndex())) + mManager.getHeaderRowWidth();
        int top = mSettings.isHeaderFixed() ? 0 : -mState.getScrollY();
        View view = holder.getItemView();

        int leftMargin = holder.getColumnIndex() * mSettings.getCellMargin() + mSettings.getCellMargin();
        int topMargin = holder.getRowIndex() * mSettings.getCellMargin() + mSettings.getCellMargin();

        if (holder.isDragging() && mDragAndDropPoints.getOffset().x > 0) {
            left = mState.getScrollX() + mDragAndDropPoints.getOffset().x - view.getWidth() / 2;
            view.bringToFront();
        }

        if (holder.isDragging()) {
            View leftShadow = mShadowHelper.getLeftShadow();
            View rightShadow = mShadowHelper.getRightShadow();

            if (leftShadow != null) {
                int shadowLeft = left - mState.getScrollX();
                leftShadow.layout(
                        Math.max(mManager.getHeaderRowWidth() - mState.getScrollX(), shadowLeft - SHADOW_THICK) + leftMargin,
                        0,
                        shadowLeft + leftMargin,
                        mSettings.getLayoutHeight());
                leftShadow.bringToFront();
            }

            if (rightShadow != null) {
                int shadowLeft = left + mManager.getColumnWidth(holder.getColumnIndex()) - mState.getScrollX();
                rightShadow.layout(
                        Math.max(mManager.getHeaderRowWidth() - mState.getScrollX(), shadowLeft) + leftMargin,
                        0,
                        shadowLeft + SHADOW_THICK + leftMargin,
                        mSettings.getLayoutHeight());
                rightShadow.bringToFront();
            }
        }


        //noinspection ResourceType
        view.layout(left - mState.getScrollX() + leftMargin,
                top + topMargin,
                left + mManager.getColumnWidth(holder.getColumnIndex()) - mState.getScrollX() + leftMargin,
                mManager.getHeaderColumnHeight() + topMargin);
    }


    /**
     * Refresh current row header view holder.
     *
     * @param holder current view holder
     */
    private void refreshHeaderRowViewHolder(ViewHolder holder) {
        int top = mManager.getRowsHeight(0, Math.max(0, holder.getRowIndex())) + mManager.getHeaderColumnHeight();
        int left = mSettings.isHeaderFixed() ? 0 : -mState.getScrollX();
        View view = holder.getItemView();

        int leftMargin = holder.getColumnIndex() * mSettings.getCellMargin() + mSettings.getCellMargin();
        int topMargin = holder.getRowIndex() * mSettings.getCellMargin() + mSettings.getCellMargin();

        if (holder.isDragging() && mDragAndDropPoints.getOffset().y > 0) {
            top = mState.getScrollY() + mDragAndDropPoints.getOffset().y - view.getHeight() / 2;
            view.bringToFront();
        }
        if (holder.isDragging()) {
            View topShadow = mShadowHelper.getTopShadow();
            View bottomShadow = mShadowHelper.getBottomShadow();
            if (topShadow != null) {
                int shadowTop = top - mState.getScrollY();
                topShadow.layout(0,
                        Math.max(mManager.getHeaderColumnHeight() - mState.getScrollY(), shadowTop - SHADOW_THICK) + topMargin,
                        mSettings.getLayoutWidth(),
                        shadowTop + topMargin);
                topShadow.bringToFront();
            }

            if (bottomShadow != null) {
                int shadowBottom = top - mState.getScrollY() + mManager.getRowHeight(holder.getRowIndex());
                bottomShadow.layout(
                        0,
                        Math.max(mManager.getHeaderColumnHeight() - mState.getScrollY(), shadowBottom) + topMargin,
                        mSettings.getLayoutWidth(),
                        shadowBottom + SHADOW_THICK + topMargin);

                bottomShadow.bringToFront();
            }
        }

        //noinspection ResourceType
        view.layout(left + leftMargin,
                top - mState.getScrollY() + topMargin,
                mManager.getHeaderRowWidth() + leftMargin,
                top + mManager.getRowHeight(holder.getRowIndex()) - mState.getScrollY() + topMargin);
    }

    /**
     * Refresh current row header view holder.
     *
     * @param holder current view holder
     */
    private void refreshLeftTopHeaderViewHolder(ViewHolder holder) {
        if (mSettings.isHeaderFixed()) {
            return;
        }

        int left = -mState.getScrollX();
        int top = -mState.getScrollY();
        View view = holder.getItemView();

        int leftMargin = mSettings.getCellMargin();
        int topMargin = mSettings.getCellMargin();

        view.layout(left + leftMargin,
                top + topMargin,
                left + mManager.getHeaderRowWidth() + leftMargin,
                top + mManager.getHeaderColumnHeight() + topMargin);

    }

    /**
     * Recycle all views
     */
    private void recycleViewHolders() {
        recycleViewHolders(false);
    }

    /**
     * Recycle view holders outside screen
     *
     * @param isRecycleAll recycle all view holders if true
     */
    private void recycleViewHolders(boolean isRecycleAll) {

        if (mAdapter == null) {
            return;
        }

        // item view holders
        for (ViewHolder holder : mViewHolders.getAll()) {
            if (holder != null && !holder.isDragging()) {
                View view = holder.getItemView();

                if (isRecycleAll
                        || (view.getRight() < 0
                        || view.getLeft() > mSettings.getLayoutWidth()
                        || view.getBottom() < 0
                        || view.getTop() > mSettings.getLayoutHeight())) {
                    // recycle view holder
                    mViewHolders.remove(holder.getRowIndex(), holder.getColumnIndex());
                    recycleViewHolder(holder);
                }
            }
        }
        // column header view holders
        for (int count = mHeaderColumnViewHolders.size(), i = 0; i < count; i++) {
            int key = mHeaderColumnViewHolders.keyAt(i);
            // get the object by the key.
            ViewHolder holder = mHeaderColumnViewHolders.get(key);
            if (holder != null) {
                View view = holder.getItemView();
                // recycle view holder
                if (isRecycleAll || (view.getRight() < 0 || view.getLeft() > mSettings.getLayoutWidth())) {
                    mHeaderColumnViewHolders.remove(key);
                    recycleViewHolder(holder);
                }
            }

        }

        // row header view holders

        for (int count = mHeaderRowViewHolders.size(), i = 0; i < count; i++) {
            int key = mHeaderRowViewHolders.keyAt(i);
            // get the object by the key.
            ViewHolder holder = mHeaderRowViewHolders.get(key);
            if (holder != null && !holder.isDragging()) {
                View view = holder.getItemView();
                // recycle view holder
                if (isRecycleAll || (view.getBottom() < 0 || view.getTop() > mSettings.getLayoutHeight())) {
                    mHeaderRowViewHolders.remove(key);
                    recycleViewHolder(holder);
                }
            }

        }
    }

    /**
     * Recycle view holder and remove view from layout.
     *
     * @param holder view holder to recycle
     */
    private void recycleViewHolder(ViewHolder holder) {
        mRecycler.pushRecycledView(holder);
        removeView(holder.getItemView());
        mAdapter.onViewHolderRecycled(holder);
    }

    /**
     * Create and add view holders with views to the layout.
     *
     * @param filledArea visible rect
     */
    private void addViewHolders(Rect filledArea) {

//        int shadowShiftX = mManager.getColumnCount() * mSettings.getCellMargin();
//        int shadowShiftY = mManager.getRowCount() * mSettings.getCellMargin();

        //search indexes for columns and rows which NEED TO BE showed in this area
        int leftColumn = mManager.getColumnByXWithShift(filledArea.left, mSettings.getCellMargin());
        int rightColumn = mManager.getColumnByXWithShift(filledArea.right, mSettings.getCellMargin());
        int topRow = mManager.getRowByYWithShift(filledArea.top, mSettings.getCellMargin());
        int bottomRow = mManager.getRowByYWithShift(filledArea.bottom, mSettings.getCellMargin());

        for (int i = topRow; i <= bottomRow; i++) {
            for (int j = leftColumn; j <= rightColumn; j++) {
                // item view holders
                ViewHolder viewHolder = mViewHolders.get(i, j);
                if (viewHolder == null && mAdapter != null) {
                    addViewHolder(i, j, ViewHolderType.ITEM);
                }
            }
            // row view headers holders
            ViewHolder viewHolder = mHeaderRowViewHolders.get(i);
            if (viewHolder == null && mAdapter != null) {
                addViewHolder(i, 0, ViewHolderType.ROW_HEADER);
            }
        }
        for (int i = leftColumn; i <= rightColumn; i++) {
            // column view header holders
            ViewHolder viewHolder = mHeaderColumnViewHolders.get(i);
            if (viewHolder == null && mAdapter != null) {
                addViewHolder(0, i, ViewHolderType.COLUMN_HEADER);
            }
        }

        // add view left top view.
        if (mLeftTopViewHolder == null && mAdapter != null) {
            mLeftTopViewHolder = mAdapter.onCreateLeftTopHeaderViewHolder(TableLayout.this);
            mLeftTopViewHolder.setItemType(ViewHolderType.FIRST_HEADER);
            View view = mLeftTopViewHolder.getItemView();
            view.setTag(R.id.tag_view_holder, mLeftTopViewHolder);
            addView(view, 0);
            mAdapter.onBindLeftTopHeaderViewHolder(mLeftTopViewHolder);
            view.measure(
                    MeasureSpec.makeMeasureSpec(mManager.getHeaderRowWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mManager.getHeaderColumnHeight(), MeasureSpec.EXACTLY));
            view.layout(mSettings.getCellMargin(),
                    mSettings.getCellMargin(),
                    mManager.getHeaderRowWidth() + mSettings.getCellMargin(),
                    mManager.getHeaderColumnHeight() + mSettings.getCellMargin());
        }
    }

    @SuppressWarnings("unused")
    private void addViewHolder(int row, int column, int itemType) {
        boolean createdNewView = false;
        // need to add new one
        ViewHolder viewHolder = mRecycler.popRecycledViewHolder(itemType);

        if (createdNewView = (viewHolder == null)) {
            viewHolder = createViewHolder(itemType);
        }

        if (viewHolder == null) {
            return;
        }

        // prepare view holder
        viewHolder.setRowIndex(row);
        viewHolder.setColumnIndex(column);
        viewHolder.setItemType(itemType);
        View view = viewHolder.getItemView();

        view.setTag(R.id.tag_view_holder, viewHolder);
        // add view to the layout
        addView(view, 0);

        // save and measure view holder
        if (itemType == ViewHolderType.ITEM) {
            mViewHolders.put(row, column, viewHolder);
            if (createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                mAdapter.onBindViewHolder(viewHolder, row, column);
            }
            view.measure(
                    MeasureSpec.makeMeasureSpec(mManager.getColumnWidth(column), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mManager.getRowHeight(row), MeasureSpec.EXACTLY));
            refreshItemViewHolder(viewHolder);
            if (!createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                mAdapter.onBindViewHolder(viewHolder, row, column);
            }


        } else if (itemType == ViewHolderType.ROW_HEADER) {
            mHeaderRowViewHolders.put(row, viewHolder);
            if (createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                mAdapter.onBindHeaderRowViewHolder(viewHolder, row);
            }
            view.measure(
                    MeasureSpec.makeMeasureSpec(mManager.getHeaderRowWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mManager.getRowHeight(row), MeasureSpec.EXACTLY));

            refreshHeaderRowViewHolder(viewHolder);
            if (!createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                mAdapter.onBindHeaderRowViewHolder(viewHolder, row);
            }

        } else if (itemType == ViewHolderType.COLUMN_HEADER) {
            mHeaderColumnViewHolders.put(column, viewHolder);
            if (createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                mAdapter.onBindHeaderColumnViewHolder(viewHolder, column);
            }
            view.measure(
                    MeasureSpec.makeMeasureSpec(mManager.getColumnWidth(column), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mManager.getHeaderColumnHeight(), MeasureSpec.EXACTLY));

            refreshHeaderColumnViewHolder(viewHolder);

            if (!createdNewView) {
                // DO NOT REMOVE THIS!! Fix bug with request layout "requestLayout() improperly called"
                mAdapter.onBindHeaderColumnViewHolder(viewHolder, column);
            }
//            mAdapter.onBindHeaderColumnViewHolder(viewHolder, column);
        }
    }

    /**
     * Create view holder by type
     *
     * @param itemType view holder type
     * @return Created view holder
     */
    @Nullable
    private ViewHolder createViewHolder(int itemType) {
        if (itemType == ViewHolderType.ITEM) {
            return mAdapter.onCreateItemViewHolder(TableLayout.this);
        } else if (itemType == ViewHolderType.ROW_HEADER) {
            return mAdapter.onCreateRowHeaderViewHolder(TableLayout.this);
        } else if (itemType == ViewHolderType.COLUMN_HEADER) {
            return mAdapter.onCreateColumnHeaderViewHolder(TableLayout.this);
        }
        return null;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // intercept event before OnClickListener on item view.
        mScrollHelper.onTouch(ev);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mState.isDragging()) {
            // Drag and drop logic

            if (event.getAction() == MotionEvent.ACTION_UP) {
                // end drag and drop event
                mDragAndDropPoints.setEnd((int) (mState.getScrollX() + event.getX()),
                        (int) (mState.getScrollY() + event.getY()));
                mLastSwitchHeaderPoint.set(0, 0);
                return mScrollHelper.onTouch(event);
            }
            // calculate absolute x, y
            int absoluteX = (int) (mState.getScrollX() + event.getX());
            int absoluteY = (int) (mState.getScrollY() + event.getY());

//            if (mState.isColumnDragging() && Math.abs(absoluteX - mLastSwitchHeaderPoint.x) > SHIFT_VIEWS_THRESHOLD) {
            // if column drag and drop mode and column offset > SHIFT_VIEWS_THRESHOLD

            if (mState.isColumnDragging()) {
                ViewHolder dragAndDropHolder = mHeaderColumnViewHolders.get(mState.getColumnDraggingIndex());
                if (dragAndDropHolder != null) {
                    int fromColumn = dragAndDropHolder.getColumnIndex();
                    int toColumn = mManager.getColumnByXWithShift(absoluteX, mSettings.getCellMargin());
                    if (fromColumn != toColumn) {
                        int columnWidth = mManager.getColumnWidth(toColumn);
                        int absoluteColumnX = mManager.getColumnsWidth(0, toColumn) + mManager.getHeaderRowWidth();

                        if (fromColumn < toColumn) {
                            // left column is dragging one
                            int deltaX = (int) (absoluteColumnX + columnWidth * 0.6f);
                            if (absoluteX > deltaX) {
                                // move column from left to right
                                for (int i = fromColumn; i < toColumn; i++) {
                                    shiftColumnsViews(i, i + 1);
                                }
                                mState.setColumnDragging(true, toColumn);
                            }
                        } else {
                            // right column is dragging one
                            int deltaX = (int) (absoluteColumnX + columnWidth * 0.4f);
                            if (absoluteX < deltaX) {
                                // move column from right to left
                                for (int i = fromColumn; i > toColumn; i--) {
                                    shiftColumnsViews(i - 1, i);
                                }
                                mState.setColumnDragging(true, toColumn);
                            }
                        }
                    }
                }
            } else if (mState.isRowDragging()) {
                ViewHolder dragAndDropHolder = mHeaderRowViewHolders.get(mState.getRowDraggingIndex());
                if (dragAndDropHolder != null) {
                    int fromRow = dragAndDropHolder.getRowIndex();
                    int toRow = mManager.getRowByYWithShift(absoluteY, mSettings.getCellMargin());
                    if (fromRow != toRow) {

                        int rowHeight = mManager.getRowHeight(toRow);
                        int absoluteColumnY = mManager.getRowsHeight(0, toRow) + mManager.getHeaderColumnHeight();
                        if (fromRow < toRow) {
                            // left column is dragging one
                            int deltaY = (int) (absoluteColumnY + rowHeight * 0.6f);
                            if (absoluteY > deltaY) {
                                // move column from left to right
                                for (int i = fromRow; i < toRow; i++) {
                                    shiftRowsViews(i, i + 1);
                                }
                                mState.setRowDragging(true, toRow);
                            }
                        } else {
                            // right column is dragging one
                            int deltaY = (int) (absoluteColumnY + rowHeight * 0.4f);
                            if (absoluteY < deltaY) {
                                // move column from right to left
                                for (int i = fromRow; i > toRow; i--) {
                                    shiftRowsViews(i - 1, i);
                                }
                                mState.setRowDragging(true, toRow);
                            }
                        }
                    }
                }
            }

            // set drag and drop offset
            mDragAndDropPoints.setOffset((int) (event.getX()), (int) (event.getY()));

            // intercept touch for scroll in drag and drop mode
            mScrollerDragAndDropRunnable.touch((int) event.getX(), (int) event.getY(),
                    mState.isColumnDragging() ? ScrollType.SCROLL_HORIZONTAL : ScrollType.SCROLL_VERTICAL);

            // update positions
            refreshViewHolders();
            return true;
        }
        return mScrollHelper.onTouch(event);
    }

    /**
     * Method change columns. Change view holders indexes, kay in map, init changing items in adapter.
     *
     * @param fromColumn from column index which need to shift
     * @param toColumn   to column index which need to shift
     */
    private void shiftColumnsViews(final int fromColumn, final int toColumn) {
        if (mAdapter != null) {

            // change data
            mAdapter.changeColumns(fromColumn, toColumn);

            // change view holders
            switchHeaders(mHeaderColumnViewHolders, fromColumn, toColumn, ViewHolderType.COLUMN_HEADER);

            // change indexes in array with widths
            mManager.switchTwoColumns(fromColumn, toColumn);

            Collection<ViewHolder> fromHolders = mViewHolders.getColumnItems(fromColumn);
            Collection<ViewHolder> toHolders = mViewHolders.getColumnItems(toColumn);

            removeViewHolders(fromHolders);
            removeViewHolders(toHolders);

            for (ViewHolder holder : fromHolders) {
                holder.setColumnIndex(toColumn);
                mViewHolders.put(holder.getRowIndex(), holder.getColumnIndex(), holder);
            }

            for (ViewHolder holder : toHolders) {
                holder.setColumnIndex(fromColumn);
                mViewHolders.put(holder.getRowIndex(), holder.getColumnIndex(), holder);
            }
        }
    }

    /**
     * Method change rows. Change view holders indexes, kay in map, init changing items in adapter.
     *
     * @param fromRow from row index which need to shift
     * @param toRow   to row index which need to shift
     */
    private void shiftRowsViews(final int fromRow, final int toRow) {
        if (mAdapter != null) {
            // change data
            mAdapter.changeRows(fromRow, toRow, mSettings.isSolidRowHeader());

            // change view holders
            switchHeaders(mHeaderRowViewHolders, fromRow, toRow, ViewHolderType.ROW_HEADER);

            // change indexes in array with heights
            mManager.switchTwoRows(fromRow, toRow);

            Collection<ViewHolder> fromHolders = mViewHolders.getRowItems(fromRow);
            Collection<ViewHolder> toHolders = mViewHolders.getRowItems(toRow);

            removeViewHolders(fromHolders);
            removeViewHolders(toHolders);

            for (ViewHolder holder : fromHolders) {
                holder.setRowIndex(toRow);
                mViewHolders.put(holder.getRowIndex(), holder.getColumnIndex(), holder);
            }

            for (ViewHolder holder : toHolders) {
                holder.setRowIndex(fromRow);
                mViewHolders.put(holder.getRowIndex(), holder.getColumnIndex(), holder);
            }

            // update row headers
            if (!mSettings.isSolidRowHeader()) {
                ViewHolder fromViewHolder = mHeaderRowViewHolders.get(fromRow);
                ViewHolder toViewHolder = mHeaderRowViewHolders.get(toRow);
                if (fromViewHolder != null) {
                    mAdapter.onBindHeaderRowViewHolder(fromViewHolder, fromRow);
                }
                if (toViewHolder != null) {
                    mAdapter.onBindHeaderRowViewHolder(toViewHolder, toRow);
                }
            }
        }
    }

    /**
     * Method switch view holders in map (map with headers view holders).
     *
     * @param map       header view holder's map
     * @param fromIndex index from view holder
     * @param toIndex   index to view holder
     * @param type      type of items (column header or row header)
     */
    @SuppressWarnings("unused")
    private void switchHeaders(HashMap<Integer, ViewHolder> map, int fromIndex, int toIndex, int type) {
        ViewHolder fromVh = map.get(fromIndex);

        if (fromVh != null) {
            map.remove(fromIndex);
            if (type == ViewHolderType.COLUMN_HEADER) {
                fromVh.setColumnIndex(toIndex);
            } else if (type == ViewHolderType.ROW_HEADER) {
                fromVh.setRowIndex(toIndex);
            }
        }

        ViewHolder toVh = map.get(toIndex);
        if (toVh != null) {
            map.remove(toIndex);
            if (type == ViewHolderType.COLUMN_HEADER) {
                toVh.setColumnIndex(fromIndex);
            } else if (type == ViewHolderType.ROW_HEADER) {
                toVh.setRowIndex(fromIndex);
            }
        }

        if (fromVh != null) {
            map.put(toIndex, fromVh);
        }

        if (toVh != null) {
            map.put(fromIndex, toVh);
        }
    }

    /**
     * Method switch view holders in map (map with headers view holders).
     *
     * @param map       header view holder's map
     * @param fromIndex index from view holder
     * @param toIndex   index to view holder
     * @param type      type of items (column header or row header)
     */
    @SuppressWarnings("unused")
    private void switchHeaders(SparseArrayCompat<ViewHolder> map, int fromIndex, int toIndex, int type) {
        ViewHolder fromVh = map.get(fromIndex);

        if (fromVh != null) {
            map.remove(fromIndex);
            if (type == ViewHolderType.COLUMN_HEADER) {
                fromVh.setColumnIndex(toIndex);
            } else if (type == ViewHolderType.ROW_HEADER) {
                fromVh.setRowIndex(toIndex);
            }
        }

        ViewHolder toVh = map.get(toIndex);
        if (toVh != null) {
            map.remove(toIndex);
            if (type == ViewHolderType.COLUMN_HEADER) {
                toVh.setColumnIndex(fromIndex);
            } else if (type == ViewHolderType.ROW_HEADER) {
                toVh.setRowIndex(fromIndex);
            }
        }

        if (fromVh != null) {
            map.put(toIndex, fromVh);
        }

        if (toVh != null) {
            map.put(fromIndex, toVh);
        }
    }

    /**
     * Remove item view holders from base collection
     *
     * @param toRemove Collection with view holders which need to remove
     */
    private void removeViewHolders(@Nullable Collection<ViewHolder> toRemove) {
        if (toRemove != null) {
            for (ViewHolder holder : toRemove) {
                mViewHolders.remove(holder.getRowIndex(), holder.getColumnIndex());
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean result;
        final ViewHolder viewHolder = (ViewHolder) child.getTag(R.id.tag_view_holder);
        canvas.save();
        int headerFixedX = (mSettings.isHeaderFixed() ? 0 : mState.getScrollX());
        int headerFixedY = (mSettings.isHeaderFixed() ? 0 : mState.getScrollY());
        //noinspection StatementWithEmptyBody
        if (viewHolder == null) {
            //ignore
        } else if (viewHolder.getItemType() == ViewHolderType.COLUMN_HEADER) {
            // prepare canvas rect area for draw column header
            canvas.clipRect(
                    Math.max(0, mManager.getHeaderRowWidth() - headerFixedX),
                    0,
                    mSettings.getLayoutWidth(),
                    Math.max(0, mManager.getHeaderColumnHeight() - headerFixedY));
        } else if (viewHolder.getItemType() == ViewHolderType.ROW_HEADER) {
            // prepare canvas rect area for draw row header
            canvas.clipRect(
                    0,
                    Math.max(0, mManager.getHeaderColumnHeight() - headerFixedY),
                    Math.max(0, mManager.getHeaderRowWidth() - headerFixedX),
                    mSettings.getLayoutHeight());
        } else if (viewHolder.getItemType() == ViewHolderType.ITEM) {
            // prepare canvas rect area for draw item (cell in table)
            canvas.clipRect(
                    Math.max(0, mManager.getHeaderRowWidth() - headerFixedX),
                    Math.max(0, mManager.getHeaderColumnHeight() - headerFixedY),
                    mSettings.getLayoutWidth(),
                    mSettings.getLayoutHeight());
        } else if (viewHolder.getItemType() == ViewHolderType.FIRST_HEADER) {
            // prepare canvas rect area for draw item (cell in table)
            canvas.clipRect(
                    0,
                    0,
                    Math.max(0, mManager.getHeaderRowWidth() - headerFixedX),
                    Math.max(0, mManager.getHeaderColumnHeight() - headerFixedY));
        }
        result = super.drawChild(canvas, child, drawingTime);
        canvas.restore(); // need to restore here.
        return result;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // stop smooth scrolling
        if (!mScrollerRunnable.isFinished()) {
            mScrollerRunnable.forceFinished();
        }
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // simple click event
        ViewHolder viewHolder = getViewHolderByPosition((int) e.getX(), (int) e.getY());
        if (viewHolder != null) {
            OnItemClickListener onItemClickListener = mAdapter.getOnItemClickListener();
            if (onItemClickListener != null) {
                if (viewHolder.getItemType() == ViewHolderType.ITEM) {
                    onItemClickListener.onItemClick(viewHolder.getRowIndex(), viewHolder.getColumnIndex());
                } else if (viewHolder.getItemType() == ViewHolderType.ROW_HEADER) {
                    onItemClickListener.onRowHeaderClick(viewHolder.getRowIndex());
                } else if (viewHolder.getItemType() == ViewHolderType.COLUMN_HEADER) {
                    onItemClickListener.onColumnHeaderClick(viewHolder.getColumnIndex());
                } else {
                    onItemClickListener.onLeftTopHeaderClick();
                }
            }
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // prepare drag and drop
        // search view holder by x, y
        ViewHolder viewHolder = getViewHolderByPosition((int) e.getX(), (int) e.getY());
        if (viewHolder != null) {
            // save start dragging touch position
            mDragAndDropPoints.setStart((int) (mState.getScrollX() + e.getX()), (int) (mState.getScrollY() + e.getY()));
            if (viewHolder.getItemType() == ViewHolderType.COLUMN_HEADER) {
                // dragging column header
                mState.setRowDragging(false, viewHolder.getRowIndex());
                mState.setColumnDragging(true, viewHolder.getColumnIndex());

                // set dragging flags to column's view holder
                setDraggingToColumn(viewHolder.getColumnIndex(), true);

                // update view
                refreshViewHolders();
                mShadowHelper.addLeftShadow(this);
                mShadowHelper.addRightShadow(this);

            } else if (viewHolder.getItemType() == ViewHolderType.ROW_HEADER) {
                // dragging column header
                mState.setRowDragging(true, viewHolder.getRowIndex());
                mState.setColumnDragging(false, viewHolder.getColumnIndex());

                // set dragging flags to row's view holder
                setDraggingToRow(viewHolder.getRowIndex(), true);

                // update view
                refreshViewHolders();

                mShadowHelper.addTopShadow(this);
                mShadowHelper.addBottomShadow(this);

            } else {
                OnItemLongClickListener onItemClickListener = mAdapter.getOnItemLongClickListener();
                if (onItemClickListener != null) {
                    if (viewHolder.getItemType() == ViewHolderType.ITEM) {
                        onItemClickListener.onItemLongClick(viewHolder.getRowIndex(), viewHolder.getColumnIndex());
                    } else if (viewHolder.getItemType() == ViewHolderType.FIRST_HEADER) {
                        onItemClickListener.onLeftTopHeaderLongClick();
                    }
                }
            }
        }
    }

    /**
     * Method set dragging flag to all view holders in the specific column
     *
     * @param column     specific column
     * @param isDragging flag to set
     */
    @SuppressWarnings("unused")
    private void setDraggingToColumn(int column, boolean isDragging) {
        Collection<ViewHolder> holders = mViewHolders.getColumnItems(column);
        for (ViewHolder holder : holders) {
            holder.setIsDragging(isDragging);
        }

        ViewHolder holder = mHeaderColumnViewHolders.get(column);
        if (holder != null) {
            holder.setIsDragging(isDragging);
        }
    }

    /**
     * Method set dragging flag to all view holders in the specific row
     *
     * @param row        specific row
     * @param isDragging flag to set
     */
    @SuppressWarnings("unused")
    private void setDraggingToRow(int row, boolean isDragging) {
        Collection<ViewHolder> holders = mViewHolders.getRowItems(row);
        for (ViewHolder holder : holders) {
            holder.setIsDragging(isDragging);
        }

        ViewHolder holder = mHeaderRowViewHolders.get(row);
        if (holder != null) {
            holder.setIsDragging(isDragging);
        }
    }

    @Override
    public boolean onActionUp(MotionEvent e) {

        // remove shadows from dragging views
        mShadowHelper.removeAll(this);

        // stop smooth scrolling
        if (!mScrollerDragAndDropRunnable.isFinished()) {
            mScrollerDragAndDropRunnable.stop();
        }

        // remove dragging flag from all item view holders
        Collection<ViewHolder> holders = mViewHolders.getAll();
        for (ViewHolder holder : holders) {
            holder.setIsDragging(false);
        }

        // remove dragging flag from all column header view holders

        for (int count = mHeaderColumnViewHolders.size(), i = 0; i < count; i++) {
            int key = mHeaderColumnViewHolders.keyAt(i);
            // get the object by the key.
            ViewHolder holder = mHeaderColumnViewHolders.get(key);
            if (holder != null) {
                holder.setIsDragging(false);
            }
        }

        // remove dragging flag from all row header view holders
        for (int count = mHeaderRowViewHolders.size(), i = 0; i < count; i++) {
            int key = mHeaderRowViewHolders.keyAt(i);
            // get the object by the key.
            ViewHolder holder = mHeaderRowViewHolders.get(key);
            if (holder != null) {
                holder.setIsDragging(false);
            }
        }

        // remove dragging flags from state
        mState.setRowDragging(false, TableState.NO_DRAGGING_POSITION);
        mState.setColumnDragging(false, TableState.NO_DRAGGING_POSITION);

        // clear dragging point positions
        mDragAndDropPoints.setStart(0, 0);
        mDragAndDropPoints.setOffset(0, 0);
        mDragAndDropPoints.setEnd(0, 0);

        // update main layout
        refreshViewHolders();
        return true;
    }

    @Nullable
    private ViewHolder getViewHolderByPosition(int x, int y) {
        ViewHolder viewHolder;

        int absX = x + mState.getScrollX();
        int absY = y + mState.getScrollY();

        if (!mSettings.isHeaderFixed()) {
            x = absX;
            y = absY;
        }

        if (y < mManager.getHeaderColumnHeight() && x < mManager.getHeaderRowWidth()) {
            // left top view was clicked
            viewHolder = mLeftTopViewHolder;
        } else if (mSettings.isHeaderFixed()) {
            if (y < mManager.getHeaderColumnHeight()) {
                // coordinate x, y in the column header's area
                int column = mManager.getColumnByXWithShift(absX, mSettings.getCellMargin());
                viewHolder = mHeaderColumnViewHolders.get(column);
            } else if (x < mManager.getHeaderRowWidth()) {
                // coordinate x, y in the row header's area
                int row = mManager.getRowByYWithShift(absY, mSettings.getCellMargin());
                viewHolder = mHeaderRowViewHolders.get(row);
            } else {
                // coordinate x, y in the items area
                int column = mManager.getColumnByXWithShift(absX, mSettings.getCellMargin());
                int row = mManager.getRowByYWithShift(absY, mSettings.getCellMargin());
                viewHolder = mViewHolders.get(row, column);
            }
        } else {
            // TODO Refactor
            if (absY < mManager.getHeaderColumnHeight()) {
                // coordinate x, y in the column header's area
                int column = mManager.getColumnByXWithShift(absX, mSettings.getCellMargin());
                viewHolder = mHeaderColumnViewHolders.get(column);
            } else if (absX < mManager.getHeaderRowWidth()) {
                // coordinate x, y in the row header's area
                int row = mManager.getRowByYWithShift(absY, mSettings.getCellMargin());
                viewHolder = mHeaderRowViewHolders.get(row);
            } else {
                // coordinate x, y in the items area
                int column = mManager.getColumnByXWithShift(absX, mSettings.getCellMargin());
                int row = mManager.getRowByYWithShift(absY, mSettings.getCellMargin());
                viewHolder = mViewHolders.get(row, column);
            }
        }
        return viewHolder;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!mState.isDragging()) {
            // simple scroll....
            if (!mScrollerRunnable.isFinished()) {
                mScrollerRunnable.forceFinished();
            }
            scrollBy((int) distanceX, (int) distanceY);
        }
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (!mState.isDragging()) {
            // simple fling
            mScrollerRunnable.start(
                    mState.getScrollX(), mState.getScrollY(),
                    (int) velocityX / 2, (int) velocityY / 2,
                    (int) (mManager.getFullWidth() - mSettings.getLayoutWidth() + mManager.getColumnCount() * mSettings.getCellMargin()),
                    (int) (mManager.getFullHeight() - mSettings.getLayoutHeight() + mManager.getRowCount() * mSettings.getCellMargin())
            );
        }
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        recycleViewHolders(true);
        mVisibleArea.set(mState.getScrollX(),
                mState.getScrollY(),
                mState.getScrollX() + mSettings.getLayoutWidth(),
                mState.getScrollY() + mSettings.getLayoutHeight());
        addViewHolders(mVisibleArea);
    }

    @Override
    public void notifyLayoutChanged() {
        recycleViewHolders(true);
        invalidate();
        mVisibleArea.set(mState.getScrollX(),
                mState.getScrollY(),
                mState.getScrollX() + mSettings.getLayoutWidth(),
                mState.getScrollY() + mSettings.getLayoutHeight());
        addViewHolders(mVisibleArea);
    }

    @Override
    public void notifyItemChanged(int rowIndex, int columnIndex) {
        Log.e("UpdateItem", "notifyItemChanged: rowIndex = " + rowIndex + " | columnIndex = " + columnIndex);
        ViewHolder holder;
        if (rowIndex == 0 && columnIndex == 0) {
            holder = mLeftTopViewHolder;
        } else if (rowIndex == 0) {
            holder = mHeaderColumnViewHolders.get(columnIndex - 1);
        } else if (columnIndex == 0) {
            holder = mHeaderRowViewHolders.get(rowIndex - 1);
        } else {
            holder = mViewHolders.get(rowIndex - 1, columnIndex - 1);
        }
        if (holder != null) {
            viewHolderChanged(holder);
        }
    }

    @Override
    public void notifyRowChanged(int rowIndex) {
        Collection<ViewHolder> rowHolders = mViewHolders.getRowItems(rowIndex);
        for (ViewHolder holder : rowHolders) {
            viewHolderChanged(holder);
        }
    }

    @Override
    public void notifyColumnChanged(int columnIndex) {
        Collection<ViewHolder> columnHolders = mViewHolders.getColumnItems(columnIndex);
        for (ViewHolder holder : columnHolders) {
            viewHolderChanged(holder);
        }
    }

    private void viewHolderChanged(@NonNull ViewHolder holder) {
        if (holder.getItemType() == ViewHolderType.FIRST_HEADER) {
            mLeftTopViewHolder = holder;
            mAdapter.onBindLeftTopHeaderViewHolder(mLeftTopViewHolder);
        } else if (holder.getItemType() == ViewHolderType.COLUMN_HEADER) {
            mHeaderColumnViewHolders.remove(holder.getColumnIndex());
            recycleViewHolder(holder);
            addViewHolder(holder.getRowIndex(), holder.getColumnIndex(), holder.getItemType());
        } else if (holder.getItemType() == ViewHolderType.ROW_HEADER) {
            mHeaderRowViewHolders.remove(holder.getRowIndex());
            recycleViewHolder(holder);
            addViewHolder(holder.getRowIndex(), holder.getColumnIndex(), holder.getItemType());
        } else {
            mViewHolders.remove(holder.getRowIndex(), holder.getColumnIndex());
            recycleViewHolder(holder);
            addViewHolder(holder.getRowIndex(), holder.getColumnIndex(), holder.getItemType());
        }
    }

    public boolean isHeaderFixed() {
        return mSettings.isHeaderFixed();
    }

    public void setHeaderFixed(boolean headerFixed) {
        mSettings.setHeaderFixed(headerFixed);
    }

    public boolean isSolidRowHeader() {
        return mSettings.isSolidRowHeader();
    }

    public void setSolidRowHeader(boolean solidRowHeader) {
        mSettings.setSolidRowHeader(solidRowHeader);
    }

    private static class TableInstanceSaver implements Parcelable {
        public static final Creator<TableInstanceSaver> CREATOR = new Creator<TableInstanceSaver>() {
            @Override
            public TableInstanceSaver createFromParcel(Parcel source) {
                return new TableInstanceSaver(source);
            }

            @Override
            public TableInstanceSaver[] newArray(int size) {
                return new TableInstanceSaver[size];
            }
        };
        private int mScrollX;
        private int mScrollY;

        public TableInstanceSaver() {
        }

        protected TableInstanceSaver(Parcel in) {
            this.mScrollX = in.readInt();
            this.mScrollY = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mScrollX);
            dest.writeInt(this.mScrollY);
        }
    }

}
