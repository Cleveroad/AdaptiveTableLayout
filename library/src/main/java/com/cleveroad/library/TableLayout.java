package com.cleveroad.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TableLayout extends ViewGroup implements ScrollHelper.ScrollHelperListener, TableDataSetObserver {
    private static final int SHADOW_THICK = 25;
    private static final int SHIFT_VIEWS_THRESHOLD = 25;
    /**
     * Matrix with item view holders
     */
    private final MapMatrix<ViewHolder> mViewHolders = new MapMatrix<>();
    /**
     * Map with column's headers view holders
     */
    private final HashMap<Integer, ViewHolder> mHeaderColumnViewHolders = new HashMap<>();
    /**
     * Map with row's headers view holders
     */
    private final HashMap<Integer, ViewHolder> mHeaderRowViewHolders = new HashMap<>();
    /**
     * Contained with drag and drop points
     */
    private final DragAndDropPoints mDragAndDropPoints = new DragAndDropPoints();
    /**
     * Container with layout state
     */
    private final TableState mState = new TableState();
    /**
     * Item's widths and heights manager.
     */
    private final TableManager mManager = new TableManager();
    /**
     * Need to fix columns bounce when dragging header.
     * Saved absolute point when header switched in drag and drop mode.
     */

    private final Point mLastSwitchHeaderPoint = new Point();
    /**
     * Contains visible area rect. Left top point and right bottom
     */
    private final Rect mVisibleArea = new Rect();


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


    public TableLayout(Context context) {
        super(context);
        init(context);
    }

    public TableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
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

    private void init(Context context) {
        // init scroll and fling helpers
        mScrollerRunnable = new SmoothScrollRunnable(this);
        mScrollerDragAndDropRunnable = new DragAndDropScrollRunnable(this);
        mRecycler = new Recycler();
        mSettings = new TableLayoutSettings();
        mScrollHelper = new ScrollHelper(context);
        mScrollHelper.setListener(this);
        mShadowHelper = new ShadowHelper();
    }

    private void initItems() {
        if (mAdapter == null) {
            return;
        }

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
        mVisibleArea.set(mState.getScrollX(), mState.getScrollY(),
                mState.getScrollX() + mSettings.getLayoutWidth(),
                mState.getScrollY() + mSettings.getLayoutHeight());
        addViewHolders(mVisibleArea);
    }

    /**
     * Set adapter with IMMUTABLE data.
     * Create wrapper with links between layout rows, columns and data rows, columns.
     * On drag and drop event just change links but not change data in adapter.
     *
     * @param adapter TableLayout adapter
     */
    public void setAdapter(@Nullable TableAdapter adapter) {
        if (mAdapter != null) {
            // remove observers from old adapter
            mAdapter.unregisterDataSetObserver(this);
        }
        if (adapter != null) {
            // wrap adapter
            mAdapter = new DataTableAdapterImpl<>(adapter);
        } else {
            // remove adapter
            mAdapter = null;
        }

        if (mAdapter != null) {
            // register notify callbacks
            mAdapter.registerDataSetObserver(this);

            // init manager
            mManager.init(mAdapter.getRowCount(), mAdapter.getColumnCount());

            if (mSettings.getLayoutHeight() != 0 && mSettings.getLayoutWidth() != 0) {
                // if layout has width and height
                initItems();
            }
        }
    }

    /**
     * Set adapter with MUTABLE data.
     * You need to implement switch rows and columns methods.
     * On drag and drop event calls {@link DataTableLayoutAdapter#changeColumns(int, int)} and
     * {@link DataTableLayoutAdapter#changeRows(int, int)}
     * <p>
     * DO NOT USE WITH BIG DATA!!
     *
     * @param adapter DataTableLayoutAdapter adapter
     */
    public void setAdapter(@Nullable DataTableLayoutAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(this);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(this);
            mManager.init(mAdapter.getRowCount(), mAdapter.getColumnCount());
            if (mSettings.getLayoutHeight() != 0 && mSettings.getLayoutWidth() != 0) {
                initItems();
            }
        }
    }

    @Override
    public void scrollBy(int x, int y) {
        // block scroll one axle
        x = mState.isRowDragging() ? 0 : x;
        y = mState.isColumnDragging() ? 0 : y;

        int diffX = x;
        int diffY = y;
        if (mState.getScrollX() + x < 0) {
            // scroll over view to the left
            diffX = mState.getScrollX();
            mState.setScrollX(0);
        } else if (mState.getScrollX() + mSettings.getLayoutWidth() + x > mManager.getFullWidth()) {
            // scroll over view to the right
            diffX = (int) (mManager.getFullWidth() - mState.getScrollX() - mSettings.getLayoutWidth());
            mState.setScrollX(mState.getScrollX() + diffX);
        } else {
            mState.setScrollX(mState.getScrollX() + x);
        }

        if (mState.getScrollY() + y < 0) {
            // scroll over view to the top
            diffY = mState.getScrollY();
            mState.setScrollY(0);
        } else if (mState.getScrollY() + mSettings.getLayoutHeight() + y > mManager.getFullHeight()) {
            // scroll over view to the bottom
            diffY = (int) (mManager.getFullHeight() - mState.getScrollY() - mSettings.getLayoutHeight());
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
            mVisibleArea.set(mState.getScrollX(), mState.getScrollY(),
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
            for (ViewHolder holder : mHeaderColumnViewHolders.values()) {
                if (holder != null) {
                    // column header
                    refreshHeaderColumnViewHolder(holder);
                }
            }

            for (ViewHolder holder : mHeaderRowViewHolders.values()) {
                if (holder != null) {
                    // row header
                    refreshHeaderRowViewHolder(holder);
                }
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

        // update layout position
        view.layout(left - mState.getScrollX() + mManager.getHeaderRowWidth(),
                top - mState.getScrollY() + mManager.getHeaderColumnHeight(),
                left + mManager.getColumnWidth(holder.getColumnIndex()) - mState.getScrollX() + mManager.getHeaderRowWidth(),
                top + mManager.getRowHeight(holder.getRowIndex()) - mState.getScrollY() + mManager.getHeaderColumnHeight());
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
        View view = holder.getItemView();

        if (holder.isDragging() && mDragAndDropPoints.getOffset().x > 0) {
            left = mState.getScrollX() + mDragAndDropPoints.getOffset().x - view.getWidth() / 2;
            view.bringToFront();
        }

        if (holder.isDragging()) {
            View leftShadow = mShadowHelper.getLeftShadow();
            View rightShadow = mShadowHelper.getRightShadow();

            if (leftShadow != null) {
                int shadowRight = left - mState.getScrollX();
                leftShadow.layout(Math.max(mManager.getHeaderRowWidth(), shadowRight - SHADOW_THICK),
                        0, shadowRight, mSettings.getLayoutHeight());
                leftShadow.bringToFront();
            }

            if (rightShadow != null) {
                int shadowLeft = left + mManager.getColumnWidth(holder.getColumnIndex()) - mState.getScrollX();
                rightShadow.layout(Math.max(mManager.getHeaderRowWidth(), shadowLeft),
                        0, shadowLeft + SHADOW_THICK, mSettings.getLayoutHeight());
                rightShadow.bringToFront();
            }


        }

        view.layout(left - mState.getScrollX(),
                0,
                left + mManager.getColumnWidth(holder.getColumnIndex()) - mState.getScrollX(),
                mManager.getHeaderColumnHeight());
    }


    /**
     * Refresh current row header view holder.
     *
     * @param holder current view holder
     */
    private void refreshHeaderRowViewHolder(ViewHolder holder) {
        int top = mManager.getRowsHeight(0, Math.max(0, holder.getRowIndex())) + mManager.getHeaderColumnHeight();
        View view = holder.getItemView();
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
                        Math.max(mManager.getHeaderColumnHeight(), shadowTop - SHADOW_THICK),
                        mSettings.getLayoutWidth(),
                        shadowTop);
                topShadow.bringToFront();
            }

            if (bottomShadow != null) {
                int shadowBottom = top - mState.getScrollY() + mManager.getRowHeight(holder.getRowIndex());
                bottomShadow.layout(
                        0,
                        Math.max(mManager.getHeaderColumnHeight(), shadowBottom),
                        mSettings.getLayoutWidth(),
                        shadowBottom + SHADOW_THICK);

                bottomShadow.bringToFront();
            }
        }
        view.layout(0,
                top - mState.getScrollY(),
                mManager.getHeaderRowWidth(),
                top + mManager.getRowHeight(holder.getRowIndex()) - mState.getScrollY());
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
        for (Iterator<Map.Entry<Integer, ViewHolder>> it = mHeaderColumnViewHolders.entrySet().iterator(); it.hasNext(); ) {
            ViewHolder holder = it.next().getValue();
            if (holder != null && !holder.isDragging()) {
                View view = holder.getItemView();
                // recycle view holder
                if (isRecycleAll || (view.getRight() < 0 || view.getLeft() > mSettings.getLayoutWidth())) {
                    it.remove();
                    recycleViewHolder(holder);
                }
            }
        }

        // row header view holders
        for (Iterator<Map.Entry<Integer, ViewHolder>> it = mHeaderRowViewHolders.entrySet().iterator(); it.hasNext(); ) {
            ViewHolder holder = it.next().getValue();
            if (holder != null && !holder.isDragging()) {
                View view = holder.getItemView();
                // recycle view holder
                if (isRecycleAll || (view.getBottom() < 0 || view.getTop() > mSettings.getLayoutHeight())) {
                    it.remove();
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
        //search indexes for columns and rows which NEED TO BE showed in this area
        int leftColumn = mManager.getColumnByX(filledArea.left);
        int rightColumn = mManager.getColumnByX(filledArea.right);
        int topRow = mManager.getRowByY(filledArea.top);
        int bottomRow = mManager.getRowByY(filledArea.bottom);

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
            view.layout(0,
                    0,
                    mManager.getHeaderRowWidth(),
                    mManager.getHeaderColumnHeight());
        }
    }

    @SuppressWarnings("unused")
    private void addViewHolder(int row, int column, int itemType) {

        // need to add new one
        ViewHolder viewHolder = mRecycler.popRecycledViewHolder(itemType);

        if (viewHolder == null) {
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
            mAdapter.onBindViewHolder(viewHolder, row, column);
            view.measure(
                    MeasureSpec.makeMeasureSpec(mManager.getColumnWidth(column), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mManager.getRowHeight(row), MeasureSpec.EXACTLY));
            refreshItemViewHolder(viewHolder);
        } else if (itemType == ViewHolderType.ROW_HEADER) {
            mHeaderRowViewHolders.put(row, viewHolder);
            mAdapter.onBindHeaderRowViewHolder(viewHolder, row);
            Log.e("ttst", "mManager.getHeaderRowWidth()=" + mManager.getHeaderRowWidth() + "; getRowHeight(row)=" + mManager.getRowHeight(row) + "; row=" + row);
            view.measure(
                    MeasureSpec.makeMeasureSpec(mManager.getHeaderRowWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mManager.getRowHeight(row), MeasureSpec.EXACTLY));
            refreshHeaderRowViewHolder(viewHolder);
        } else if (itemType == ViewHolderType.COLUMN_HEADER) {
            mHeaderColumnViewHolders.put(column, viewHolder);
            mAdapter.onBindHeaderColumnViewHolder(viewHolder, column);
            view.measure(
                    MeasureSpec.makeMeasureSpec(mManager.getColumnWidth(column), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mManager.getHeaderColumnHeight(), MeasureSpec.EXACTLY));
            refreshHeaderColumnViewHolder(viewHolder);
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
        if (mScrollHelper.isDragging()) {
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

            if (mState.isColumnDragging() && Math.abs(absoluteX - mLastSwitchHeaderPoint.x) > SHIFT_VIEWS_THRESHOLD) {
                // if column drag and drop mode and column offset > SHIFT_VIEWS_THRESHOLD
                int toColumn = 0;
                int fromColumn = 0;

                // search dragging column and under column
                for (ViewHolder header : mHeaderColumnViewHolders.values()) {
                    if (header.isDragging()) {
                        fromColumn = header.getColumnIndex();
                        toColumn = mManager.getColumnByX(absoluteX);
                        break;
                    }
                }

                if (fromColumn != toColumn) {
                    // need to switch columns
                    mLastSwitchHeaderPoint.x = absoluteX;
                    if (fromColumn < toColumn) {
                        // move column from left to right
                        for (int i = fromColumn; i < toColumn; i++) {
                            shiftColumnsViews(i, i + 1);
                        }
                    } else {
                        // move column from right to left
                        for (int i = fromColumn; i > toColumn; i--) {
                            shiftColumnsViews(i - 1, i);
                        }
                    }
                }
            } else if (mState.isRowDragging() && Math.abs(absoluteY - mLastSwitchHeaderPoint.y) > SHIFT_VIEWS_THRESHOLD) {
                int toRow = 0;
                int fromRow = 0;
                // search dragging row and under row
                for (ViewHolder header : mHeaderRowViewHolders.values()) {
                    if (header.isDragging()) {
                        fromRow = header.getRowIndex();
                        toRow = mManager.getRowByY(absoluteY);
                        break;
                    }
                }

                if (fromRow != toRow) {
                    // need to switch rows
                    mLastSwitchHeaderPoint.y = absoluteY;
                    if (fromRow < toRow) {
                        // move row from top to bottom
                        for (int i = fromRow; i < toRow; i++) {
                            shiftRowsViews(i, i + 1);
                        }
                    } else {
                        // move row from bottom to top
                        for (int i = fromRow; i > toRow; i--) {
                            shiftRowsViews(i - 1, i);
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
            mAdapter.changeRows(fromRow, toRow);

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
        //noinspection StatementWithEmptyBody
        if (viewHolder == null) {
            //ignore
        } else if (viewHolder.getItemType() == ViewHolderType.COLUMN_HEADER) {
            // prepare canvas rect area for draw column header
            canvas.clipRect(
                    mManager.getHeaderRowWidth(),
                    0,
                    mSettings.getLayoutWidth(),
                    mManager.getHeaderColumnHeight());
        } else if (viewHolder.getItemType() == ViewHolderType.ROW_HEADER) {
            // prepare canvas rect area for draw row header
            canvas.clipRect(
                    0,
                    mManager.getHeaderColumnHeight(),
                    mManager.getHeaderRowWidth(),
                    mSettings.getLayoutHeight());
        } else if (viewHolder.getItemType() == ViewHolderType.ITEM) {
            // prepare canvas rect area for draw item (cell in table)
            canvas.clipRect(
                    mManager.getHeaderRowWidth(),
                    mManager.getHeaderColumnHeight(),
                    mSettings.getLayoutWidth(),
                    mSettings.getLayoutHeight());
        } else if (viewHolder.getItemType() == ViewHolderType.FIRST_HEADER) {
            // prepare canvas rect area for draw item (cell in table)
            canvas.clipRect(
                    0,
                    0,
                    mManager.getHeaderRowWidth(),
                    mManager.getHeaderColumnHeight());
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
    public boolean onLongPress(MotionEvent e) {
        // prepare drag and drop
        // search view holder by x, y
        ViewHolder viewHolder = getViewHolderByPosition((int) e.getX(), (int) e.getY());
        if (viewHolder != null) {
            // save start dragging touch position
            mDragAndDropPoints.setStart((int) (mState.getScrollX() + e.getX()), (int) (mState.getScrollY() + e.getY()));
            if (viewHolder.getItemType() == ViewHolderType.COLUMN_HEADER) {
                // dragging column header
                mState.setRowDragging(false);
                mState.setColumnDragging(true);

                // set dragging flags to column's view holder
                setDraggingToColumn(viewHolder.getColumnIndex(), true);

                // update view
                refreshViewHolders();
                mShadowHelper.addLeftShadow(this);
                mShadowHelper.addRightShadow(this);

                return true;
            } else if (viewHolder.getItemType() == ViewHolderType.ROW_HEADER) {
                // dragging column header
                mState.setRowDragging(true);
                mState.setColumnDragging(false);

                // set dragging flags to row's view holder
                setDraggingToRow(viewHolder.getRowIndex(), true);

                // update view
                refreshViewHolders();

                mShadowHelper.addTopShadow(this);
                mShadowHelper.addBottomShadow(this);

                return true;
            } else {
                OnItemLongClickListener onItemClickListener = mAdapter.getOnItemLongClickListener();
                if (onItemClickListener != null) {
                    if (viewHolder.getItemType() == ViewHolderType.ITEM) {
                        onItemClickListener.onItemLongClick(viewHolder.getRowIndex(), viewHolder.getColumnIndex());
                    } else if (viewHolder.getItemType() == ViewHolderType.FIRST_HEADER) {
                        onItemClickListener.onLeftTopHeaderLongClick();
                    }
                }
                return false;
            }

        }
        return false;
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
        for (ViewHolder holder : mHeaderColumnViewHolders.values()) {
            holder.setIsDragging(false);
        }

        // remove dragging flag from all row header view holders
        for (ViewHolder holder : mHeaderRowViewHolders.values()) {
            holder.setIsDragging(false);
        }

        // remove dragging flags from state
        mState.setRowDragging(false);
        mState.setColumnDragging(false);

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
        if (y < mManager.getHeaderColumnHeight() && x < mManager.getHeaderRowWidth()) {
            // left top view was clicked
            viewHolder = mLeftTopViewHolder;
        } else if (y < mManager.getHeaderColumnHeight()) {
            // coordinate x, y in the column header's area
            int column = mManager.getColumnByX(x + mState.getScrollX());
            viewHolder = mHeaderColumnViewHolders.get(column);
        } else if (x < mManager.getHeaderRowWidth()) {
            // coordinate x, y in the row header's area
            int row = mManager.getRowByY(y + mState.getScrollY());
            viewHolder = mHeaderRowViewHolders.get(row);
        } else {
            // coordinate x, y in the items area
            int column = mManager.getColumnByX(x + mState.getScrollX());
            int row = mManager.getRowByY(y + mState.getScrollY());
            viewHolder = mViewHolders.get(row, column);
        }
        return viewHolder;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!mScrollHelper.isDragging()) {
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
        if (!mScrollHelper.isDragging()) {
            // simple fling
            mScrollerRunnable.start(
                    mState.getScrollX(), mState.getScrollY(),
                    (int) velocityX / 2, (int) velocityY / 2,
                    (int) (mManager.getFullWidth() - mSettings.getLayoutWidth()),
                    (int) (mManager.getFullHeight() - mSettings.getLayoutHeight())
            );
        }
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        recycleViewHolders(true);
        mVisibleArea.set(mState.getScrollX(), mState.getScrollY(),
                mState.getScrollX() + mSettings.getLayoutWidth(),
                mState.getScrollY() + mSettings.getLayoutHeight());
        addViewHolders(mVisibleArea);
    }

    @Override
    public void notifyLayoutChanged() {
        recycleViewHolders(true);
        invalidate();
        mVisibleArea.set(mState.getScrollX(), mState.getScrollY(),
                mState.getScrollX() + mSettings.getLayoutWidth(),
                mState.getScrollY() + mSettings.getLayoutHeight());
        addViewHolders(mVisibleArea);
    }

    @Override
    public void notifyItemChanged(int rowIndex, int columnIndex) {
        ViewHolder holder = mViewHolders.get(rowIndex, columnIndex);
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
        mViewHolders.remove(holder.getRowIndex(), holder.getColumnIndex());
        recycleViewHolder(holder);
        addViewHolder(holder.getRowIndex(), holder.getColumnIndex(), holder.getItemType());
    }

}
