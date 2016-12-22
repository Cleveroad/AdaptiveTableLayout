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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TableLayout extends ViewGroup implements ScrollHelper.ScrollHelperListener, TableDataSetObserver {
    private static final int SHIFT_VIEWS_THRESHOLD = 25; // TODO SIMPLE FILTER. CHANGE TO MORE SPECIFIC...

    private final MapMatrix<ViewHolder> mViewHolders = new MapMatrix<>();
    private final HashMap<Integer, ViewHolder> mHeaderColumnViewHolders = new HashMap<>();
    private final HashMap<Integer, ViewHolder> mHeaderRowViewHolders = new HashMap<>();
    private final DragAndDropPoints mDragAndDropPoints = new DragAndDropPoints();
    private final TableState mState = new TableState();
    private final TableManager mManager = new TableManager();
    // need to fix columns bounce when dragging column
    private final Point mLastSwitchColumnsPoint = new Point();
    private final Rect mVisibleArea = new Rect();
    /**
     * View holder in the left top corner.
     */
    @Nullable
    private ViewHolder mLeftTopViewHolder;
    private DataTableLayoutAdapter<ViewHolder> mAdapter;
    private Recycler mRecycler;
    private TableLayoutSettings mSettings;
    private ScrollHelper mScrollHelper;
    private SmoothScrollRunnable mScrollerRunnable;
    private DragAndDropScrollRunnable mScrollerDragAndDropRunnable;


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
            mSettings.setLayoutWidth(r - l);
            mSettings.setLayoutHeight(b - t);
            initItems();
        }
    }

    private void init(Context context) {
        mScrollerRunnable = new SmoothScrollRunnable(this);
        mScrollerDragAndDropRunnable = new DragAndDropScrollRunnable(this);
        mRecycler = new Recycler();

        final ViewConfiguration configuration = ViewConfiguration.get(context);

        mSettings = new TableLayoutSettings();
        mSettings
                .setMinVelocity(configuration.getScaledMinimumFlingVelocity())
                .setMaxVelocity(configuration.getScaledMaximumFlingVelocity());

        mScrollHelper = new ScrollHelper(context);
        mScrollHelper.setListener(this);

    }

    private void initItems() {
        if (mAdapter == null) {
            return;
        }

        for (int count = mManager.getColumnCount(), i = 0; i < count; i++) {
            int item = mAdapter.getColumnWidth(i);
            mManager.putColumnWidth(i, item);
        }

        for (int count = mManager.getRowCount(), i = 0; i < count; i++) {
            int item = mAdapter.getRowHeight(i);
            mManager.putRowHeight(i, item);
        }

        mManager.setHeaderColumnHeight(Math.max(0, mAdapter.getHeaderColumnHeight()));
        mManager.setHeaderRowWidth(Math.max(0, mAdapter.getHeaderRowWidth()));

        mManager.invalidate();
        mVisibleArea.set(mState.getScrollX(), mState.getScrollY(),
                mState.getScrollX() + mSettings.getLayoutWidth(),
                mState.getScrollY() + mSettings.getLayoutHeight());
        addViews(mVisibleArea);
    }

    public void setAdapter(@Nullable TableAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(this);
        }
        if (adapter != null) {
            mAdapter = new DataTableAdapterImpl<>(adapter);
        } else {
            mAdapter = null;
        }
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(this);
            mManager.init(mAdapter.getRowCount(), mAdapter.getColumnCount());
            if (mSettings.getLayoutHeight() != 0 && mSettings.getLayoutWidth() != 0) {
                initItems();
            }
        }
    }

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

        scrollView(diffX, diffY);
    }

    private void scrollView(int x, int y) {
        if (x == 0 && y == 0) {
            return;
        }

        if (mAdapter != null) {
            recycleViews();
            mVisibleArea.set(mState.getScrollX(), mState.getScrollY(),
                    mState.getScrollX() + mSettings.getLayoutWidth(),
                    mState.getScrollY() + mSettings.getLayoutHeight());
            addViews(mVisibleArea);
            refreshLayouts();
        }
    }

    private void refreshLayouts() {
        if (mAdapter != null) {
            for (ViewHolder holder : mHeaderColumnViewHolders.values()) {
                if (holder != null) {
                    refreshHeaderColumn(holder.getColumnIndex(), holder);
                }
            }

            for (ViewHolder holder : mHeaderRowViewHolders.values()) {
                if (holder != null) {
                    refreshHeaderRow(holder.getRowIndex(), holder);
                }
            }

            for (ViewHolder holder : mViewHolders.getAll()) {
                if (holder != null) {
                    refreshLayout(holder.getRowIndex(), holder.getColumnIndex(), holder, mState.isRowDragging(), mState.isColumnDragging());
                }
            }
        }
    }

    private void refreshLayout(int row, int column, ViewHolder holder,
                               boolean isRowDragging, boolean isColumnDragging) {
        int left = mManager.getColumnsWidth(0, Math.max(0, column));
        int top = mManager.getRowsHeight(0, Math.max(0, row));
        View view = holder.getItemView();
        if (isColumnDragging && holder.isDragging() && mDragAndDropPoints.getOffset().x > 0) {
            left = mState.getScrollX() + mDragAndDropPoints.getOffset().x - view.getWidth() / 2 - mManager.getHeaderRowWidth();
            view.bringToFront();
        } else if (isRowDragging && holder.isDragging() && mDragAndDropPoints.getOffset().y > 0) {
            top = mState.getScrollY() + mDragAndDropPoints.getOffset().y - view.getHeight() / 2 - mManager.getHeaderColumnHeight();
            view.bringToFront();
        }
        view.layout(left - mState.getScrollX() + mManager.getHeaderRowWidth(),
                top - mState.getScrollY() + mManager.getHeaderColumnHeight(),
                left + mManager.getColumnWidth(column) - mState.getScrollX() + mManager.getHeaderRowWidth(),
                top + mManager.getRowHeight(row) - mState.getScrollY() + mManager.getHeaderColumnHeight());
    }

    private void refreshLayout(int row, int column, ViewHolder holder) {
        refreshLayout(row, column, holder, false, false);
    }

    private void refreshHeaderColumn(int column, ViewHolder holder) {
        int left = mManager.getColumnsWidth(0, Math.max(0, column)) + mManager.getHeaderRowWidth();
        View view = holder.getItemView();

        if (holder.isDragging() && mDragAndDropPoints.getOffset().x > 0) {
            left = mState.getScrollX() + mDragAndDropPoints.getOffset().x - view.getWidth() / 2;
            view.bringToFront();
        }

        view.layout(left - mState.getScrollX(),
                0,
                left + mManager.getColumnWidth(column) - mState.getScrollX(),
                mManager.getHeaderColumnHeight());
    }

    private void refreshHeaderRow(int row, ViewHolder holder) {
        int top = mManager.getRowsHeight(0, Math.max(0, row)) + mManager.getHeaderColumnHeight();
        View view = holder.getItemView();
        if (holder.isDragging() && mDragAndDropPoints.getOffset().y > 0) {
            top = mState.getScrollY() + mDragAndDropPoints.getOffset().y - view.getHeight() / 2;
            view.bringToFront();
        }
        view.layout(0,
                top - mState.getScrollY(),
                mManager.getHeaderRowWidth(),
                top + mManager.getHeaderColumnHeight() - mState.getScrollY());
    }

    private void recycleViews() {
        recycleViews(false);
    }

    private void recycleViews(boolean isRecycleAll) {

        if (mAdapter == null) {
            return;
        }

        for (ViewHolder holder : mViewHolders.getAll()) {
            if (holder != null && !holder.isDragging()) {
                View view = holder.getItemView();
                if (isRecycleAll || (view.getRight() < 0 || view.getLeft() > mSettings.getLayoutWidth() ||
                        view.getBottom() < 0 || view.getTop() > mSettings.getLayoutHeight())) {
                    mViewHolders.remove(holder.getRowIndex(), holder.getColumnIndex());
                    recycleViewHolder(holder, ItemType.ITEM);
                }
            }
        }

        for (Iterator<Map.Entry<Integer, ViewHolder>> it = mHeaderColumnViewHolders.entrySet().iterator(); it.hasNext(); ) {
            ViewHolder holder = it.next().getValue();
            if (holder != null && !holder.isDragging()) {
                View view = holder.getItemView();
                if (isRecycleAll || (view.getRight() < 0 || view.getLeft() > mSettings.getLayoutWidth())) {
                    it.remove();
                    recycleViewHolder(holder, ItemType.COLUMN_HEADER);
                }
            }
        }

        for (Iterator<Map.Entry<Integer, ViewHolder>> it = mHeaderRowViewHolders.entrySet().iterator(); it.hasNext(); ) {
            ViewHolder holder = it.next().getValue();
            if (holder != null && !holder.isDragging()) {
                View view = holder.getItemView();
                if (isRecycleAll || (view.getBottom() < 0 || view.getTop() > mSettings.getLayoutHeight())) {
                    it.remove();
                    recycleViewHolder(holder, ItemType.ROW_HEADER);
                }
            }
        }
    }

    private void recycleViewHolder(ViewHolder holder, int type) {
        mRecycler.pushRecycledView(holder, type);
        removeView(holder.getItemView());
        mAdapter.onViewHolderRecycled(holder); // TODO FIX THIS!!
    }

    private void addViews(Rect filledArea) {
        //search indexes for columns and rows which NEED TO BE showed in this area
        int leftColumn = mManager.getColumnByX(filledArea.left);
        int rightColumn = mManager.getColumnByX(filledArea.right);
        int topRow = mManager.getRowByY(filledArea.top);
        int bottomRow = mManager.getRowByY(filledArea.bottom);

        for (int i = topRow; i <= bottomRow; i++) {
            for (int j = leftColumn; j <= rightColumn; j++) {
                // data holders
                ViewHolder viewHolder = mViewHolders.get(i, j);
                if (viewHolder == null && mAdapter != null) {
                    addViews(i, j, ItemType.ITEM);
                }
            }
            // row headers
            ViewHolder viewHolder = mHeaderRowViewHolders.get(i);
            if (viewHolder == null && mAdapter != null) {
                addViews(i, 0, ItemType.ROW_HEADER);
            }
        }
        for (int i = leftColumn; i <= rightColumn; i++) {
            // column header holders
            ViewHolder viewHolder = mHeaderColumnViewHolders.get(i);
            if (viewHolder == null && mAdapter != null) {
                addViews(0, i, ItemType.COLUMN_HEADER);
            }
        }

        // add view left top view.
        if (mLeftTopViewHolder == null && mAdapter != null) {
            mLeftTopViewHolder = mAdapter.onCreateLeftTopHeaderViewHolder(TableLayout.this);
            mLeftTopViewHolder.setItemType(ItemType.FIRST_HEADER);
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

    private void addViews(int row, int column, int itemType) {

        // need to add new one
        ViewHolder viewHolder = mRecycler.popRecycledViewHolder(itemType);

        if (viewHolder == null) {
            viewHolder = createViewHolder(TableLayout.this, itemType);
        }

        if (viewHolder == null) {
            return;
        }

        viewHolder.setRowIndex(row);
        viewHolder.setColumnIndex(column);
        viewHolder.setItemType(itemType);
        View view = viewHolder.getItemView();
        view.setTag(R.id.tag_view_holder, viewHolder);

        addView(view, 0);

        if (itemType == ItemType.ITEM) {
            mViewHolders.put(row, column, viewHolder);
            mAdapter.onBindViewHolder(viewHolder, row, column);
            view.measure(
                    MeasureSpec.makeMeasureSpec(mManager.getColumnWidth(column), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mManager.getRowHeight(row), MeasureSpec.EXACTLY));
            refreshLayout(row, column, viewHolder);
        } else if (itemType == ItemType.ROW_HEADER) {
            mHeaderRowViewHolders.put(row, viewHolder);
            mAdapter.onBindHeaderRowViewHolder(viewHolder, row);
            view.measure(
                    MeasureSpec.makeMeasureSpec(mManager.getHeaderRowWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mManager.getRowHeight(row), MeasureSpec.EXACTLY));
            refreshHeaderRow(row, viewHolder);
        } else if (itemType == ItemType.COLUMN_HEADER) {
            mHeaderColumnViewHolders.put(column, viewHolder);
            mAdapter.onBindHeaderColumnViewHolder(viewHolder, column);
            view.measure(
                    MeasureSpec.makeMeasureSpec(mManager.getColumnWidth(column), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mManager.getHeaderColumnHeight(), MeasureSpec.EXACTLY));
            refreshHeaderColumn(column, viewHolder);
        }
    }

    @Nullable
    private ViewHolder createViewHolder(@NonNull ViewGroup parent, int itemType) {
        if (itemType == ItemType.ITEM) {
            return mAdapter.onCreateViewHolder(parent);
        } else if (itemType == ItemType.ROW_HEADER) {
            return mAdapter.onCreateRowHeaderViewHolder(TableLayout.this);
        } else if (itemType == ItemType.COLUMN_HEADER) {
            return mAdapter.onCreateColumnHeaderViewHolder(TableLayout.this);
        }
        return null;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mScrollHelper.onTouch(ev);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mScrollHelper.isDragging()) {
            if (event.getAction() == MotionEvent.ACTION_UP) {

                mDragAndDropPoints.setEnd((int) (mState.getScrollX() + event.getX()),
                        (int) (mState.getScrollY() + event.getY()));

                return mScrollHelper.onTouch(event);
            }

            int absoluteX = (int) (mState.getScrollX() + event.getX());
            int absoluteY = (int) (mState.getScrollY() + event.getY());
            if (mState.isColumnDragging() && Math.abs(absoluteX - mLastSwitchColumnsPoint.x) > SHIFT_VIEWS_THRESHOLD) {
                int toColumn = 0;
                int fromColumn = 0;
                for (ViewHolder header : mHeaderColumnViewHolders.values()) {
                    if (header.isDragging()) {
                        fromColumn = header.getColumnIndex();
                        toColumn = mManager.getColumnByX(absoluteX);
                        break;
                    }
                }

                if (fromColumn != toColumn) {
                    mLastSwitchColumnsPoint.x = absoluteX;
                    if (fromColumn < toColumn) {
                        for (int i = fromColumn; i < toColumn; i++) {
                            shiftColumnsViews(i, i + 1);
                        }
                    } else {
                        for (int i = fromColumn; i > toColumn; i--) {
                            shiftColumnsViews(i - 1, i);
                        }
                    }
                }
            } else if (mState.isRowDragging() && Math.abs(absoluteY - mLastSwitchColumnsPoint.y) > SHIFT_VIEWS_THRESHOLD) {
                int toRow = 0;
                int fromRow = 0;
                for (ViewHolder header : mHeaderRowViewHolders.values()) {
                    if (header.isDragging()) {
                        fromRow = header.getRowIndex();
                        toRow = mManager.getRowByY(absoluteY);
                        break;
                    }
                }

                if (fromRow != toRow) {
                    mLastSwitchColumnsPoint.y = absoluteY;
                    if (fromRow < toRow) {
                        for (int i = fromRow; i < toRow; i++) {
                            shiftRowsViews(i, i + 1);
                        }
                    } else {
                        for (int i = fromRow; i > toRow; i--) {
                            shiftRowsViews(i - 1, i);
                        }
                    }
                }
            }

            mDragAndDropPoints.setOffset((int) (event.getX()), (int) (event.getY()));

            mScrollerDragAndDropRunnable.touch((int) event.getX(), (int) event.getY(),
                    mState.isColumnDragging() ? ScrollType.SCROLL_HORIZONTAL : ScrollType.SCROLL_VERTICAL);

            refreshLayouts();

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

            mAdapter.changeColumns(fromColumn, toColumn);
            switchHeaders(mHeaderColumnViewHolders, fromColumn, toColumn, ItemType.COLUMN_HEADER);
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

            mAdapter.changeRows(fromRow, toRow);
            switchHeaders(mHeaderRowViewHolders, fromRow, toRow, ItemType.ROW_HEADER);
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
    private void switchHeaders(HashMap<Integer, ViewHolder> map, int fromIndex, int toIndex, int type) {
        ViewHolder fromVh = map.get(fromIndex);

        if (fromVh != null) {
            map.remove(fromIndex);
            if (type == ItemType.COLUMN_HEADER) {
                fromVh.setColumnIndex(toIndex);
            } else {
                fromVh.setRowIndex(toIndex);
            }
        }

        ViewHolder toVh = map.get(toIndex);
        if (toVh != null) {
            map.remove(toIndex);
            if (type == ItemType.COLUMN_HEADER) {
                toVh.setColumnIndex(fromIndex);
            } else {
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
        } else if (viewHolder.getItemType() == ItemType.COLUMN_HEADER) {
            // prepare canvas rect area for draw column header
            canvas.clipRect(
                    mManager.getHeaderRowWidth(),
                    0,
                    mSettings.getLayoutWidth(),
                    mManager.getHeaderColumnHeight());
        } else if (viewHolder.getItemType() == ItemType.ROW_HEADER) {
            // prepare canvas rect area for draw row header
            canvas.clipRect(
                    0,
                    mManager.getHeaderColumnHeight(),
                    mManager.getHeaderRowWidth(),
                    mSettings.getLayoutHeight());
        } else if (viewHolder.getItemType() == ItemType.ITEM) {
            // prepare canvas rect area for draw item (cell in table)
            canvas.clipRect(
                    mManager.getHeaderRowWidth(),
                    mManager.getHeaderColumnHeight(),
                    mSettings.getLayoutWidth(),
                    mSettings.getLayoutHeight());
        } else if (viewHolder.getItemType() == ItemType.FIRST_HEADER) {
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
                if (viewHolder.getItemType() == ItemType.ITEM) {
                    onItemClickListener.onItemClick(viewHolder.getRowIndex(), viewHolder.getColumnIndex());
                } else if (viewHolder.getItemType() == ItemType.ROW_HEADER) {
                    onItemClickListener.onRowHeaderClick(viewHolder.getRowIndex());
                } else if (viewHolder.getItemType() == ItemType.COLUMN_HEADER) {
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
            if (viewHolder.getItemType() == ItemType.COLUMN_HEADER) {
                // dragging column header
                mState.setRowDragging(false);
                mState.setColumnDragging(true);

                // set dragging flags to column's view holder
                setDraggingToColumn(viewHolder.getColumnIndex(), true);

                // update view
                refreshLayouts();
                return true;
            } else if (viewHolder.getItemType() == ItemType.ROW_HEADER) {
                // dragging column header
                mState.setRowDragging(true);
                mState.setColumnDragging(false);

                // set dragging flags to row's view holder
                setDraggingToRow(viewHolder.getRowIndex(), true);

                // update view
                refreshLayouts();
                return true;
            } else {
                OnItemLongClickListener onItemClickListener = mAdapter.getOnItemLongClickListener();
                if (onItemClickListener != null) {
                    if (viewHolder.getItemType() == ItemType.ITEM) {
                        onItemClickListener.onItemLongClick(viewHolder.getRowIndex(), viewHolder.getColumnIndex());
                    } else if (viewHolder.getItemType() == ItemType.FIRST_HEADER) {
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
        refreshLayouts();
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
        recycleViews(true);
        mVisibleArea.set(mState.getScrollX(), mState.getScrollY(),
                mState.getScrollX() + mSettings.getLayoutWidth(),
                mState.getScrollY() + mSettings.getLayoutHeight());
        addViews(mVisibleArea);
    }

    @Override
    public void notifyLayoutChanged() {
        recycleViews(true);
        invalidate();
        mVisibleArea.set(mState.getScrollX(), mState.getScrollY(),
                mState.getScrollX() + mSettings.getLayoutWidth(),
                mState.getScrollY() + mSettings.getLayoutHeight());
        addViews(mVisibleArea);
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
        recycleViewHolder(holder, holder.getItemType());
        addViews(holder.getRowIndex(), holder.getColumnIndex(), holder.getItemType());
    }

}
