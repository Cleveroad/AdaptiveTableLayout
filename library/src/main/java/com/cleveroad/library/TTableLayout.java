package com.cleveroad.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
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

public class TTableLayout extends ViewGroup implements TScrollHelper.ScrollHelperListener {
    public static final int HOLDER_TYPE = 0;
    public static final int HOLDER_HEADER_COLUMN_TYPE = 1;
    public static final int HOLDER_HEADER_ROW_TYPE = 2;
    public static final String TAG = "TTableLayout";
    private static final int SHIFT_VIEWS_THRESHOLD = 25; // TODO SIMPLE FILTER. CHANGE TO MORE SPECIFIC...

    private final TSparseMatrix<TBaseTableAdapter.TViewHolder> mViewHolders = new TSparseMatrix<>();
    private final HashMap<Integer, TBaseTableAdapter.TViewHolder> mHeaderColumnViewHolders = new HashMap<>();
    private final HashMap<Integer, TBaseTableAdapter.TViewHolder> mHeaderRowViewHolders = new HashMap<>();

    private final TDragAndDropPoints mTDragAndDropPoints = new TDragAndDropPoints();

    private final TTableState mState = new TTableState();
    private final TTableManager mManager = new TTableManager();

    // need to fix columns bounce when dragging column
    private final Point mLastSwitchColumnsPoint = new Point();

    private TDataTableAdapter<TTableAdapter.TViewHolder> mAdapter;
    private TRecycler mRecycler;
    private TTableLayoutSettings mSettings;
    private TScrollHelper mScrollHelper;
    private TSmoothScrollRunnable mScrollerRunnable;
    private TDragAndDropScrollRunnable mScrollerDragAndDropRunnable;


    public TTableLayout(Context context) {
        super(context);
        init(context);
    }

    public TTableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TTableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TTableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        mScrollerRunnable = new TSmoothScrollRunnable(this);
        mScrollerDragAndDropRunnable = new TDragAndDropScrollRunnable(this);
        mRecycler = new TRecycler();

        final ViewConfiguration configuration = ViewConfiguration.get(context);

        mSettings = new TTableLayoutSettings();
        mSettings
                .setMinimumVelocity(configuration.getScaledMinimumFlingVelocity())
                .setMaximumVelocity(configuration.getScaledMaximumFlingVelocity());

        mScrollHelper = new TScrollHelper(context);
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

        addViews(new Rect(mState.getScrollX(), mState.getScrollY(),
                mState.getScrollX() + mSettings.getLayoutWidth(),
                mState.getScrollY() + mSettings.getLayoutHeight()));
    }

    public void setAdapter(@Nullable TTableAdapter adapter) {
        if (adapter != null) {
            mAdapter = new TDataTableAdapterImpl<>(adapter);
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
            addViews(new Rect(mState.getScrollX(), mState.getScrollY(), mState.getScrollX() + mSettings.getLayoutWidth(), mState.getScrollY() + mSettings.getLayoutHeight()));
            refreshLayouts();
        }
    }

    private void refreshLayouts() {
        if (mAdapter != null) {
            for (TTableAdapter.TViewHolder holder : mHeaderColumnViewHolders.values()) {
                if (holder != null) {
                    refreshHeaderColumn(holder.getColumnIndex(), holder);
                }
            }

            for (TTableAdapter.TViewHolder holder : mHeaderRowViewHolders.values()) {
                if (holder != null) {
                    refreshHeaderRow(holder.getRowIndex(), holder);
                }
            }

            for (TTableAdapter.TViewHolder holder : mViewHolders.getAll()) {
                if (holder != null) {
                    refreshLayout(holder.getRowIndex(), holder.getColumnIndex(), holder, mState.isRowDragging(), mState.isColumnDragging());
                }
            }
        }
    }

    private void refreshLayout(int row, int column, TTableAdapter.TViewHolder holder,
                               boolean isRowDragging, boolean isColumnDragging) {
        int left = mManager.getColumnsWidth(0, Math.max(0, column));
        int top = mManager.getRowsHeight(0, Math.max(0, row));
        View view = holder.getItemView();
        if (isColumnDragging && holder.isDragging() && mTDragAndDropPoints.getOffset().x > 0) {
            left = mState.getScrollX() + mTDragAndDropPoints.getOffset().x - view.getWidth() / 2 - mManager.getHeaderRowWidth();
            view.bringToFront();
        } else if (isRowDragging && holder.isDragging() && mTDragAndDropPoints.getOffset().y > 0) {
            top = mState.getScrollY() + mTDragAndDropPoints.getOffset().y - view.getHeight() / 2 - mManager.getHeaderColumnHeight();
            view.bringToFront();
        }
        view.layout(left - mState.getScrollX() + mManager.getHeaderRowWidth(),
                top - mState.getScrollY() + mManager.getHeaderColumnHeight(),
                left + mManager.getColumnWidth(column) - mState.getScrollX() + mManager.getHeaderRowWidth(),
                top + mManager.getRowHeight(row) - mState.getScrollY() + mManager.getHeaderColumnHeight());
    }

    private void refreshLayout(int row, int column, TTableAdapter.TViewHolder holder) {
        refreshLayout(row, column, holder, false, false);
    }

    private void refreshHeaderColumn(int column, TTableAdapter.TViewHolder holder) {
        int left = mManager.getColumnsWidth(0, Math.max(0, column)) + mManager.getHeaderRowWidth();
        View view = holder.getItemView();

        if (holder.isDragging() && mTDragAndDropPoints.getOffset().x > 0) {
            left = mState.getScrollX() + mTDragAndDropPoints.getOffset().x - view.getWidth() / 2;
            view.bringToFront();
        }

        view.layout(left - mState.getScrollX(),
                0,
                left + mManager.getColumnWidth(column) - mState.getScrollX(),
                mManager.getHeaderColumnHeight());
    }

    private void refreshHeaderRow(int row, TTableAdapter.TViewHolder holder) {
        int top = mManager.getRowsHeight(0, Math.max(0, row)) + mManager.getHeaderColumnHeight();
        View view = holder.getItemView();
        if (holder.isDragging() && mTDragAndDropPoints.getOffset().y > 0) {
            top = mState.getScrollY() + mTDragAndDropPoints.getOffset().y - view.getHeight() / 2;
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

        for (TTableAdapter.TViewHolder holder : mViewHolders.getAll()) {
            if (holder != null && !holder.isDragging()) {
                View view = holder.getItemView();
                if (view.getRight() < 0 || view.getLeft() > mSettings.getLayoutWidth() ||
                        view.getBottom() < 0 || view.getTop() > mSettings.getLayoutHeight()) {
                    mRecycler.pushRecycledView(holder, HOLDER_TYPE);
                    mViewHolders.remove(holder.getRowIndex(), holder.getColumnIndex());
                    removeView(view);
                    mAdapter.onViewHolderRecycled(holder);
                }
            }
        }

        for (Iterator<Map.Entry<Integer, TTableAdapter.TViewHolder>> it = mHeaderColumnViewHolders.entrySet().iterator(); it.hasNext(); ) {
            TTableAdapter.TViewHolder holder = it.next().getValue();
            if (holder != null && !holder.isDragging()) {
                View view = holder.getItemView();
                if (view.getRight() < 0 || view.getLeft() > mSettings.getLayoutWidth()) {
                    mRecycler.pushRecycledView(holder, HOLDER_HEADER_COLUMN_TYPE);
                    it.remove();
                    removeView(view);
                    mAdapter.onViewHolderRecycled(holder); // TODO FIX THIS!!
                }
            }
        }

        for (Iterator<Map.Entry<Integer, TTableAdapter.TViewHolder>> it = mHeaderRowViewHolders.entrySet().iterator(); it.hasNext(); ) {
            TTableAdapter.TViewHolder holder = it.next().getValue();
            if (holder != null && !holder.isDragging()) {
                View view = holder.getItemView();
                if (view.getBottom() < 0 || view.getTop() > mSettings.getLayoutHeight()) {
                    mRecycler.pushRecycledView(holder, HOLDER_HEADER_ROW_TYPE);
                    it.remove();
                    removeView(view);
                    mAdapter.onViewHolderRecycled(holder); // TODO FIX THIS!!
                }
            }
        }
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
                TTableAdapter.TViewHolder viewHolder = mViewHolders.get(i, j);
                if (viewHolder == null && mAdapter != null) {
                    // need to add new one
                    viewHolder = mRecycler.popRecycledViewHolder(HOLDER_TYPE);
                    if (viewHolder == null) {
                        viewHolder = mAdapter.onCreateViewHolder(TTableLayout.this, HOLDER_TYPE);
                    }
                    viewHolder.setRowIndex(i);
                    viewHolder.setColumnIndex(j);
                    viewHolder.setItemType(HOLDER_TYPE);
                    View view = viewHolder.getItemView();
                    view.setTag(R.id.tag_view_holder, viewHolder);

                    addView(view, HOLDER_TYPE);
                    mViewHolders.put(i, j, viewHolder);
                    mAdapter.onBindViewHolder(viewHolder, i, j);

                    view.measure(
                            MeasureSpec.makeMeasureSpec(mManager.getColumnWidth(j), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(mManager.getRowHeight(i), MeasureSpec.EXACTLY));

                    refreshLayout(i, j, viewHolder);

                }
            }
            TTableAdapter.TViewHolder viewHolder = mHeaderRowViewHolders.get(i);
            if (viewHolder == null && mAdapter != null) {
                // need to add new one
                viewHolder = mRecycler.popRecycledViewHolder(HOLDER_HEADER_ROW_TYPE);

                if (viewHolder == null) {
                    viewHolder = mAdapter.onCreateRowHeaderViewHolder(TTableLayout.this);
                }

                viewHolder.setRowIndex(i);
                viewHolder.setColumnIndex(0);
                viewHolder.setItemType(HOLDER_HEADER_ROW_TYPE);

                View view = viewHolder.getItemView();
                view.setTag(R.id.tag_view_holder, viewHolder);

                addView(view, HOLDER_HEADER_ROW_TYPE);
                mHeaderRowViewHolders.put(i, viewHolder);
                mAdapter.onBindHeaderRowViewHolder(viewHolder, i);

                view.measure(
                        MeasureSpec.makeMeasureSpec(mManager.getHeaderRowWidth(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(mManager.getRowHeight(i), MeasureSpec.EXACTLY));
                refreshHeaderRow(i, viewHolder);
            }
        }
        for (int i = leftColumn; i <= rightColumn; i++) {
            // column header holders
            TTableAdapter.TViewHolder viewHolder = mHeaderColumnViewHolders.get(i);
            if (viewHolder == null && mAdapter != null) {
                // need to add new one
                viewHolder = mRecycler.popRecycledViewHolder(HOLDER_HEADER_COLUMN_TYPE);

                if (viewHolder == null) {
                    viewHolder = mAdapter.onCreateColumnHeaderViewHolder(TTableLayout.this);
                }
                viewHolder.setRowIndex(0);
                viewHolder.setColumnIndex(i);
                viewHolder.setItemType(HOLDER_HEADER_COLUMN_TYPE);

                View view = viewHolder.getItemView();
                view.setTag(R.id.tag_view_holder, viewHolder);

                addView(view, HOLDER_HEADER_COLUMN_TYPE);
                mHeaderColumnViewHolders.put(i, viewHolder);
                mAdapter.onBindHeaderColumnViewHolder(viewHolder, i);

                view.measure(
                        MeasureSpec.makeMeasureSpec(mManager.getColumnWidth(i), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(mManager.getHeaderColumnHeight(), MeasureSpec.EXACTLY));

                refreshHeaderColumn(i, viewHolder);
            }
        }
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

                mTDragAndDropPoints.setEnd((int) (mState.getScrollX() + event.getX()),
                        (int) (mState.getScrollY() + event.getY()));

                return mScrollHelper.onTouch(event);
            }


            int absoluteX = (int) (mState.getScrollX() + event.getX());
            int absoluteY = (int) (mState.getScrollY() + event.getY());
            if (mState.isColumnDragging() && Math.abs(absoluteX - mLastSwitchColumnsPoint.x) > SHIFT_VIEWS_THRESHOLD) {
                // TODO optimize this block
                int toColumn = 0;
                int fromColumn = 0;
                for (TTableAdapter.TViewHolder header : mHeaderColumnViewHolders.values()) {
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
                // TODO optimize this block too
                int toRow = 0;
                int fromRow = 0;
                for (TTableAdapter.TViewHolder header : mHeaderRowViewHolders.values()) {
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

            mTDragAndDropPoints.setOffset((int) (event.getX()), (int) (event.getY()));

            mScrollerDragAndDropRunnable.touch((int) event.getX(), (int) event.getY(),
                    mState.isColumnDragging() ? TDragAndDropScrollRunnable.ORIENTATION_HORIZONTAL : TDragAndDropScrollRunnable.ORIENTATION_VERTICAL);

            refreshLayouts();

            return true;
        }
        return mScrollHelper.onTouch(event);
    }

    private void shiftColumnsViews(final int fromColumn, final int toColumn) {
        if (mAdapter != null) {
            mAdapter.changeColumns(fromColumn, toColumn);
            TTableAdapter.TViewHolder fromVh = mHeaderColumnViewHolders.get(fromColumn);

            if (fromVh != null) {
                mHeaderColumnViewHolders.remove(fromVh.getColumnIndex());
                fromVh.setColumnIndex(toColumn);
            }

            TTableAdapter.TViewHolder toVh = mHeaderColumnViewHolders.get(toColumn);
            if (toVh != null) {
                mHeaderColumnViewHolders.remove(toVh.getColumnIndex());
                toVh.setColumnIndex(fromColumn);
            }

            if (fromVh != null) {
                mHeaderColumnViewHolders.put(toColumn, fromVh);
            }

            if (toVh != null) {
                mHeaderColumnViewHolders.put(fromColumn, toVh);
            }

            mManager.switchTwoColumns(fromColumn, toColumn);

            Collection<TTableAdapter.TViewHolder> fromHolders = mViewHolders.getColumnItems(fromColumn);
            Collection<TTableAdapter.TViewHolder> toHolders = mViewHolders.getColumnItems(toColumn);

            removeViewHolders(fromHolders);
            removeViewHolders(toHolders);

            for (TTableAdapter.TViewHolder holder : fromHolders) {
                holder.setColumnIndex(toColumn);
                mViewHolders.put(holder.getRowIndex(), holder.getColumnIndex(), holder);
            }

            for (TTableAdapter.TViewHolder holder : toHolders) {
                holder.setColumnIndex(fromColumn);
                mViewHolders.put(holder.getRowIndex(), holder.getColumnIndex(), holder);
            }
        }
    }

    private void shiftRowsViews(final int fromRow, final int toRow) {
        if (mAdapter != null) {
            mAdapter.changeRows(fromRow, toRow);
            TTableAdapter.TViewHolder fromVh = mHeaderRowViewHolders.get(fromRow);

            if (fromVh != null) {
                mHeaderRowViewHolders.remove(fromVh.getRowIndex());
                fromVh.setRowIndex(toRow);
            }

            TTableAdapter.TViewHolder toVh = mHeaderRowViewHolders.get(toRow);
            if (toVh != null) {
                mHeaderRowViewHolders.remove(toVh.getRowIndex());
                toVh.setRowIndex(fromRow);
            }

            if (fromVh != null) {
                mHeaderRowViewHolders.put(toRow, fromVh);
            }

            if (toVh != null) {
                mHeaderRowViewHolders.put(fromRow, toVh);
            }

            mManager.switchTwoRows(fromRow, toRow);

            Collection<TTableAdapter.TViewHolder> fromHolders = mViewHolders.getRowItems(fromRow);
            Collection<TTableAdapter.TViewHolder> toHolders = mViewHolders.getRowItems(toRow);

            removeViewHolders(fromHolders);
            removeViewHolders(toHolders);

            for (TTableAdapter.TViewHolder holder : fromHolders) {
                holder.setRowIndex(toRow);
                mViewHolders.put(holder.getRowIndex(), holder.getColumnIndex(), holder);
            }

            for (TTableAdapter.TViewHolder holder : toHolders) {
                holder.setRowIndex(fromRow);
                mViewHolders.put(holder.getRowIndex(), holder.getColumnIndex(), holder);
            }
        }
    }

    private void removeViewHolders(@Nullable Collection<TTableAdapter.TViewHolder> toRemove) {
        if (toRemove != null) {
            for (TTableAdapter.TViewHolder holder : toRemove) {
                mViewHolders.remove(holder.getRowIndex(), holder.getColumnIndex());
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean result;

        final TTableAdapter.TViewHolder viewHolder = (TTableAdapter.TViewHolder) child.getTag(R.id.tag_view_holder);

        canvas.save();

        //noinspection StatementWithEmptyBody
        if (viewHolder == null) {
            //ignore
        } else if (viewHolder.getItemType() == HOLDER_HEADER_COLUMN_TYPE) {
            canvas.clipRect(
                    mManager.getHeaderRowWidth(),
                    0,
                    mSettings.getLayoutWidth(),
                    mManager.getHeaderColumnHeight());
        } else if (viewHolder.getItemType() == HOLDER_HEADER_ROW_TYPE) {
            canvas.clipRect(
                    0,
                    mManager.getHeaderColumnHeight(),
                    mManager.getHeaderRowWidth(),
                    mSettings.getLayoutHeight());
        } else {
            canvas.clipRect(
                    mManager.getHeaderRowWidth(),
                    mManager.getHeaderColumnHeight(),
                    mSettings.getLayoutWidth(),
                    mSettings.getLayoutHeight());
        }
        result = super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        return result;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        TTableAdapter.TViewHolder viewHolder = getViewHolderByPosition((int) e.getX(), (int) e.getY());
        if (viewHolder != null) {
            viewHolder.getItemView().callOnClick();
        }
        return true;
    }

    @Override
    public boolean onLongPress(MotionEvent e) {
        TTableAdapter.TViewHolder viewHolder = getViewHolderByPosition((int) e.getX(), (int) e.getY());
        if (viewHolder != null) {
            mTDragAndDropPoints.setStart((int) (mState.getScrollX() + e.getX()), (int) (mState.getScrollY() + e.getY()));
            if (viewHolder.getItemType() == HOLDER_HEADER_COLUMN_TYPE) {

                mState.setRowDragging(false);
                mState.setColumnDragging(true);

                setDraggingToColumn(viewHolder.getColumnIndex(), true);
                refreshLayouts();
            } else if (viewHolder.getItemType() == HOLDER_HEADER_ROW_TYPE) {

                mState.setRowDragging(true);
                mState.setColumnDragging(false);

                setDraggingToRow(viewHolder.getRowIndex(), true);
                refreshLayouts();
            }
        }
        return viewHolder != null;
    }

    private void setDraggingToColumn(int column, boolean isDragging) {
        Collection<TTableAdapter.TViewHolder> holders = mViewHolders.getColumnItems(column);
        for (TTableAdapter.TViewHolder holder : holders) {
            holder.setIsDragging(isDragging);
        }

        TTableAdapter.TViewHolder holder = mHeaderColumnViewHolders.get(column);
        if (holder != null) {
            holder.setIsDragging(isDragging);
        }
    }

    private void setDraggingToRow(int row, boolean isDragging) {
        Collection<TTableAdapter.TViewHolder> holders = mViewHolders.getRowItems(row);
        for (TTableAdapter.TViewHolder holder : holders) {
            holder.setIsDragging(isDragging);
        }

        TTableAdapter.TViewHolder holder = mHeaderRowViewHolders.get(row);
        if (holder != null) {
            holder.setIsDragging(isDragging);
        }
    }

    @Override
    public boolean onActionUp(MotionEvent e) {
        if (!mScrollerDragAndDropRunnable.isFinished()) {
            mScrollerDragAndDropRunnable.stop();
        }

        Collection<TTableAdapter.TViewHolder> holders = mViewHolders.getAll();
        for (TTableAdapter.TViewHolder holder : holders) {
            holder.setIsDragging(false);
        }

        for (TTableAdapter.TViewHolder holder : mHeaderColumnViewHolders.values()) {
            holder.setIsDragging(false);
        }

        for (TTableAdapter.TViewHolder holder : mHeaderRowViewHolders.values()) {
            holder.setIsDragging(false);
        }

        mState.setRowDragging(false);
        mState.setColumnDragging(false);

        mTDragAndDropPoints.setStart(0, 0);
        mTDragAndDropPoints.setOffset(0, 0);
        mTDragAndDropPoints.setEnd(0, 0);
        refreshLayouts();
        return true;
    }

    @Nullable
    private TTableAdapter.TViewHolder getViewHolderByPosition(int x, int y) {
        TTableAdapter.TViewHolder viewHolder;
        if (y < mManager.getHeaderColumnHeight()) {
            // header column click
            int column = mManager.getColumnByX(x + mState.getScrollX());
            viewHolder = mHeaderColumnViewHolders.get(column);
        } else if (x < mManager.getHeaderRowWidth()) {
            // header row click
            int row = mManager.getRowByY(y + mState.getScrollY());
            viewHolder = mHeaderRowViewHolders.get(row);
        } else {
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
