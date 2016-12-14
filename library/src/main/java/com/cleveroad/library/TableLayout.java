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

public class TableLayout extends ViewGroup implements ScrollHelper.ScrollHelperListener {
    public static final int HOLDER_TYPE = 0;
    public static final int HOLDER_HEADER_COLUMN_TYPE = 1;
    public static final int HOLDER_HEADER_ROW_TYPE = 2;
    public static final String TAG = "TTableLayout";
    private static final int SHIFT_VIEWS_THRESHOLD = 25; // TODO SIMPLE FILTER. CHANGE TO MORE SPECIFIC...

    private final SparseMatrix<TableAdapter.ViewHolder> mViewHolders = new SparseMatrix<>();
    private final HashMap<Integer, TableAdapter.ViewHolder> mHeaderColumnViewHolders = new HashMap<>();
    private final HashMap<Integer, TableAdapter.ViewHolder> mHeaderRowViewHolders = new HashMap<>();

    private final DragAndDropPoints mDragAndDropPoints = new DragAndDropPoints();

    private final TableState mState = new TableState();
    private final TableManager mManager = new TableManager();

    // need to fix columns bounce when dragging column
    private final Point mLastSwitchColumnsPoint = new Point();
    private final Rect mVisibleArea = new Rect();
    private DataTableAdapter<TableAdapter.ViewHolder> mAdapter;
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
                .setMinimumVelocity(configuration.getScaledMinimumFlingVelocity())
                .setMaximumVelocity(configuration.getScaledMaximumFlingVelocity());

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

        mManager.setHeaderColumnHeight(mAdapter.getHeaderColumnHeight());
        mManager.setHeaderRowWidth(mAdapter.getHeaderRowWidth());

        mManager.invalidate();
        mVisibleArea.set(mState.getScrollX(), mState.getScrollY(),
                mState.getScrollX() + mSettings.getLayoutWidth(),
                mState.getScrollY() + mSettings.getLayoutHeight());
        addViews(mVisibleArea);
    }

    public void setAdapter(@Nullable TableAdapter adapter) {
        if (adapter != null) {
            mAdapter = new DataTableAdapterImpl<>(adapter);
        } else {
            mAdapter = null;
        }
        if (mAdapter != null) {
            mManager.init(mAdapter.getRowCount(), mAdapter.getColumnCount());
            if (mSettings.getLayoutHeight() != 0 && mSettings.getLayoutWidth() != 0) {
                initItems();
            }
        }
    }

    public void setAdapter(@Nullable DataTableAdapter adapter) {
        mAdapter = adapter;
        if (mAdapter != null) {
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
//        Log.e("DragAndDrop", "isRow = " + mIsRowDragging + " | isCol = " + mIsColumnDragging + " x = " + diffX + " | y = " + diffY);
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
            for (TableAdapter.ViewHolder holder : mHeaderColumnViewHolders.values()) {
                if (holder != null) {
                    refreshHeaderColumn(holder.getColumnIndex(), holder);
                }
            }

            for (TableAdapter.ViewHolder holder : mHeaderRowViewHolders.values()) {
                if (holder != null) {
                    refreshHeaderRow(holder.getRowIndex(), holder);
                }
            }

            for (TableAdapter.ViewHolder holder : mViewHolders.getAll()) {
                if (holder != null) {
                    refreshLayout(holder.getRowIndex(), holder.getColumnIndex(), holder, mState.isRowDragging(), mState.isColumnDragging());
                }
            }
        }
    }

    private void refreshLayout(int row, int column, TableAdapter.ViewHolder holder,
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

    private void refreshLayout(int row, int column, TableAdapter.ViewHolder holder) {
        refreshLayout(row, column, holder, false, false);
    }

    private void refreshHeaderColumn(int column, TableAdapter.ViewHolder holder) {
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

    private void refreshHeaderRow(int row, TableAdapter.ViewHolder holder) {
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

        if (mAdapter == null) {
            return;
        }

        for (TableAdapter.ViewHolder holder : mViewHolders.getAll()) {
            if (holder != null && !holder.isDragging()) {
                View view = holder.getItemView();
                if (view.getRight() < 0 || view.getLeft() > mSettings.getLayoutWidth() ||
                        view.getBottom() < 0 || view.getTop() > mSettings.getLayoutHeight()) {
                    mViewHolders.remove(holder.getRowIndex(), holder.getColumnIndex());
                    recycleViewHolder(holder, HOLDER_TYPE);
                }
            }
        }

        for (Iterator<Map.Entry<Integer, TableAdapter.ViewHolder>> it = mHeaderColumnViewHolders.entrySet().iterator(); it.hasNext(); ) {
            TableAdapter.ViewHolder holder = it.next().getValue();
            if (holder != null && !holder.isDragging()) {
                View view = holder.getItemView();
                if (view.getRight() < 0 || view.getLeft() > mSettings.getLayoutWidth()) {
                    it.remove();
                    recycleViewHolder(holder, HOLDER_HEADER_COLUMN_TYPE);
                }
            }
        }

        for (Iterator<Map.Entry<Integer, TableAdapter.ViewHolder>> it = mHeaderRowViewHolders.entrySet().iterator(); it.hasNext(); ) {
            TableAdapter.ViewHolder holder = it.next().getValue();
            if (holder != null && !holder.isDragging()) {
                View view = holder.getItemView();
                if (view.getBottom() < 0 || view.getTop() > mSettings.getLayoutHeight()) {
                    it.remove();
                    recycleViewHolder(holder, HOLDER_HEADER_ROW_TYPE);

                }
            }
        }
    }

    private void recycleViewHolder(TableAdapter.ViewHolder holder, int type) {
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
                TableAdapter.ViewHolder viewHolder = mViewHolders.get(i, j);
                if (viewHolder == null && mAdapter != null) {
                    addViews(i, j, HOLDER_TYPE);
                }
            }
            // row headers
            TableAdapter.ViewHolder viewHolder = mHeaderRowViewHolders.get(i);
            if (viewHolder == null && mAdapter != null) {
                addViews(i, 0, HOLDER_HEADER_ROW_TYPE);
            }
        }
        for (int i = leftColumn; i <= rightColumn; i++) {
            // column header holders
            TableAdapter.ViewHolder viewHolder = mHeaderColumnViewHolders.get(i);
            if (viewHolder == null && mAdapter != null) {
                addViews(0, i, HOLDER_HEADER_COLUMN_TYPE);
            }
        }
    }

    private void addViews(int row, int column, int itemType) {

        // need to add new one
        TableAdapter.ViewHolder viewHolder = mRecycler.popRecycledViewHolder(itemType);

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

        if (itemType == HOLDER_TYPE) {
            mViewHolders.put(row, column, viewHolder);
            mAdapter.onBindViewHolder(viewHolder, row, column);
            view.measure(
                    MeasureSpec.makeMeasureSpec(mManager.getColumnWidth(column), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mManager.getRowHeight(row), MeasureSpec.EXACTLY));
            refreshLayout(row, column, viewHolder);
        } else if (itemType == HOLDER_HEADER_ROW_TYPE) {
            mHeaderRowViewHolders.put(row, viewHolder);
            mAdapter.onBindHeaderRowViewHolder(viewHolder, row);
            view.measure(
                    MeasureSpec.makeMeasureSpec(mManager.getHeaderRowWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mManager.getRowHeight(row), MeasureSpec.EXACTLY));
            refreshHeaderRow(row, viewHolder);
        } else if (itemType == HOLDER_HEADER_COLUMN_TYPE) {
            mHeaderColumnViewHolders.put(column, viewHolder);
            mAdapter.onBindHeaderColumnViewHolder(viewHolder, column);
            view.measure(
                    MeasureSpec.makeMeasureSpec(mManager.getColumnWidth(column), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mManager.getHeaderColumnHeight(), MeasureSpec.EXACTLY));
            refreshHeaderColumn(column, viewHolder);
        }

    }

    @Nullable
    private TableAdapter.ViewHolder createViewHolder(@NonNull ViewGroup parent, int itemType) {
        if (itemType == HOLDER_TYPE) {
            return mAdapter.onCreateViewHolder(parent, itemType);
        } else if (itemType == HOLDER_HEADER_ROW_TYPE) {
            return mAdapter.onCreateRowHeaderViewHolder(TableLayout.this);
        } else if (itemType == HOLDER_HEADER_COLUMN_TYPE) {
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
                for (TableAdapter.ViewHolder header : mHeaderColumnViewHolders.values()) {
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
                for (TableAdapter.ViewHolder header : mHeaderRowViewHolders.values()) {
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
                    mState.isColumnDragging() ? DragAndDropScrollRunnable.ORIENTATION_HORIZONTAL : DragAndDropScrollRunnable.ORIENTATION_VERTICAL);

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
            switchHeaders(mHeaderColumnViewHolders, fromColumn, toColumn, HOLDER_HEADER_COLUMN_TYPE);
            mManager.switchTwoColumns(fromColumn, toColumn);

            Collection<TableAdapter.ViewHolder> fromHolders = mViewHolders.getColumnItems(fromColumn);
            Collection<TableAdapter.ViewHolder> toHolders = mViewHolders.getColumnItems(toColumn);

            removeViewHolders(fromHolders);
            removeViewHolders(toHolders);

            for (TableAdapter.ViewHolder holder : fromHolders) {
                holder.setColumnIndex(toColumn);
                mViewHolders.put(holder.getRowIndex(), holder.getColumnIndex(), holder);
            }

            for (TableAdapter.ViewHolder holder : toHolders) {
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
            switchHeaders(mHeaderRowViewHolders, fromRow, toRow, HOLDER_HEADER_ROW_TYPE);
            mManager.switchTwoRows(fromRow, toRow);

            Collection<TableAdapter.ViewHolder> fromHolders = mViewHolders.getRowItems(fromRow);
            Collection<TableAdapter.ViewHolder> toHolders = mViewHolders.getRowItems(toRow);

            removeViewHolders(fromHolders);
            removeViewHolders(toHolders);

            for (TableAdapter.ViewHolder holder : fromHolders) {
                holder.setRowIndex(toRow);
                mViewHolders.put(holder.getRowIndex(), holder.getColumnIndex(), holder);
            }

            for (TableAdapter.ViewHolder holder : toHolders) {
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
    private void switchHeaders(HashMap<Integer, TableAdapter.ViewHolder> map, int fromIndex, int toIndex, int type) {
        TableAdapter.ViewHolder fromVh = map.get(fromIndex);

        if (fromVh != null) {
            map.remove(fromIndex);
            if (type == HOLDER_HEADER_COLUMN_TYPE) {
                fromVh.setColumnIndex(toIndex);
            } else {
                fromVh.setRowIndex(toIndex);
            }
        }

        TableAdapter.ViewHolder toVh = map.get(toIndex);
        if (toVh != null) {
            map.remove(toIndex);
            if (type == HOLDER_HEADER_COLUMN_TYPE) {
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
    private void removeViewHolders(@Nullable Collection<TableAdapter.ViewHolder> toRemove) {
        if (toRemove != null) {
            for (TableAdapter.ViewHolder holder : toRemove) {
                mViewHolders.remove(holder.getRowIndex(), holder.getColumnIndex());
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean result;
        final TableAdapter.ViewHolder viewHolder = (TableAdapter.ViewHolder) child.getTag(R.id.tag_view_holder);
        canvas.save();
        //noinspection StatementWithEmptyBody
        if (viewHolder == null) {
            //ignore
        } else if (viewHolder.getItemType() == HOLDER_HEADER_COLUMN_TYPE) {
            // prepare canvas rect area for draw column header
            canvas.clipRect(
                    mManager.getHeaderRowWidth(),
                    0,
                    mSettings.getLayoutWidth(),
                    mManager.getHeaderColumnHeight());
        } else if (viewHolder.getItemType() == HOLDER_HEADER_ROW_TYPE) {
            // prepare canvas rect area for draw row header
            canvas.clipRect(
                    0,
                    mManager.getHeaderColumnHeight(),
                    mManager.getHeaderRowWidth(),
                    mSettings.getLayoutHeight());
        } else {
            // prepare canvas rect area for draw item (cell tin table)
            canvas.clipRect(
                    mManager.getHeaderRowWidth(),
                    mManager.getHeaderColumnHeight(),
                    mSettings.getLayoutWidth(),
                    mSettings.getLayoutHeight());
        }
        result = super.drawChild(canvas, child, drawingTime);
        canvas.restore(); // need to restore here.
        return result;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // simple click event
        TableAdapter.ViewHolder viewHolder = getViewHolderByPosition((int) e.getX(), (int) e.getY());
        if (viewHolder != null) {
            viewHolder.getItemView().callOnClick();
        }
        return true;
    }

    @Override
    public boolean onLongPress(MotionEvent e) {
        // prepare drag and drop
        // search view holder by x, y
        TableAdapter.ViewHolder viewHolder = getViewHolderByPosition((int) e.getX(), (int) e.getY());
        if (viewHolder != null) {
            // save start dragging touch position
            mDragAndDropPoints.setStart((int) (mState.getScrollX() + e.getX()), (int) (mState.getScrollY() + e.getY()));
            if (viewHolder.getItemType() == HOLDER_HEADER_COLUMN_TYPE) {
                // dragging column header
                mState.setRowDragging(false);
                mState.setColumnDragging(true);

                // set dragging flags to column's view holder
                setDraggingToColumn(viewHolder.getColumnIndex(), true);

                // update view
                refreshLayouts();
            } else if (viewHolder.getItemType() == HOLDER_HEADER_ROW_TYPE) {
                // dragging column header
                mState.setRowDragging(true);
                mState.setColumnDragging(false);

                // set dragging flags to row's view holder
                setDraggingToRow(viewHolder.getRowIndex(), true);

                // update view
                refreshLayouts();
            }
        }
        return viewHolder != null;
    }

    /**
     * Method set dragging flag to all view holders in the specific column
     *
     * @param column     specific column
     * @param isDragging flag to set
     */
    private void setDraggingToColumn(int column, boolean isDragging) {
        Collection<TableAdapter.ViewHolder> holders = mViewHolders.getColumnItems(column);
        for (TableAdapter.ViewHolder holder : holders) {
            holder.setIsDragging(isDragging);
        }

        TableAdapter.ViewHolder holder = mHeaderColumnViewHolders.get(column);
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
        Collection<TableAdapter.ViewHolder> holders = mViewHolders.getRowItems(row);
        for (TableAdapter.ViewHolder holder : holders) {
            holder.setIsDragging(isDragging);
        }

        TableAdapter.ViewHolder holder = mHeaderRowViewHolders.get(row);
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
        Collection<TableAdapter.ViewHolder> holders = mViewHolders.getAll();
        for (TableAdapter.ViewHolder holder : holders) {
            holder.setIsDragging(false);
        }

        // remove dragging flag from all column header view holders
        for (TableAdapter.ViewHolder holder : mHeaderColumnViewHolders.values()) {
            holder.setIsDragging(false);
        }

        // remove dragging flag from all row header view holders
        for (TableAdapter.ViewHolder holder : mHeaderRowViewHolders.values()) {
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
    private TableAdapter.ViewHolder getViewHolderByPosition(int x, int y) {
        TableAdapter.ViewHolder viewHolder;
        if (y < mManager.getHeaderColumnHeight()) {
            // coordinate x, y in the column header's area
            int column = mManager.getColumnByX(x + mState.getScrollX());
            viewHolder = mHeaderColumnViewHolders.get(column);
        } else if (x < mManager.getHeaderRowWidth()) {
            // coordinate x, y in the row header's area
            int row = mManager.getRowByY(y + mState.getScrollY());
            viewHolder = mHeaderRowViewHolders.get(row);
        } else {
            // coordinate x, y in the items area
            int column = mManager.getColumnByX(x + mState.getScrollX() - mManager.getHeaderRowWidth());
            int row = mManager.getRowByY(y + mState.getScrollY() - mManager.getHeaderColumnHeight());
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
                    (int) velocityX / 4, (int) velocityY / 4,
                    (int) (mManager.getFullWidth() - mSettings.getLayoutWidth()),
                    (int) (mManager.getFullHeight() - mSettings.getLayoutHeight())
            );
        }
        return true;
    }

}
