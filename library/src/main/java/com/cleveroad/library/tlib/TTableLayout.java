package com.cleveroad.library.tlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.cleveroad.library.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TTableLayout extends ViewGroup implements TScrollHelper.ScrollHelperListener {
    public static final int HOLDER_TYPE = 0;
    public static final int HOLDER_HEADER_COLUMN_TYPE = 1;
    public static final int HOLDER_HEADER_ROW_TYPE = 2;
    public static final String TAG = "TTableLayout";
    private final TSparseMatrix<TBaseTableAdapter.ViewHolder> mViewHolders = new TSparseMatrix<>();
    private final HashMap<Integer, TBaseTableAdapter.ViewHolder> mHeaderColumnViewHolders = new HashMap<>();
    private final HashMap<Integer, TBaseTableAdapter.ViewHolder> mHeaderRowViewHolders = new HashMap<>();

    private final Point mTouchPoint = new Point();


    private final TTableState mState = new TTableState();
    private final TTableManager mManager = new TTableManager();
    @Nullable
    private TTableAdapter<TTableAdapter.ViewHolder> mAdapter;
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
            Log.e(TAG, " onLayout");
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

        addViews(new Rect(0, 0, mSettings.getLayoutWidth(), mSettings.getLayoutHeight()));
//        invalidate();
    }

    public void setAdapter(@Nullable TTableAdapter adapter) {
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
            addViews(new Rect(mState.getScrollX(), mState.getScrollY(), mState.getScrollX() + mSettings.getLayoutWidth(), mState.getScrollY() + mSettings.getLayoutHeight()));
            refreshLayouts();
        }
    }

    private void refreshLayouts() {
        if (mAdapter != null) {
            for (TTableAdapter.ViewHolder holder : mViewHolders.getAll()) {
                if (holder != null) {
                    refreshLayout(holder.getRowIndex(), holder.getColumnIndex(), holder);
                }
            }

            for (TTableAdapter.ViewHolder holder : mHeaderColumnViewHolders.values()) {
                if (holder != null) {
                    View view = holder.getItemView();
                    refreshHeaderColumn(holder.getColumnIndex(), view);
                }
            }

            for (TTableAdapter.ViewHolder holder : mHeaderRowViewHolders.values()) {
                if (holder != null) {
                    View view = holder.getItemView();
                    refreshHeaderRow(holder.getRowIndex(), view);
                }
            }
        }
    }

    private void refreshLayout(int row, int column, TTableAdapter.ViewHolder holder) {
        int left = mManager.getColumnsWidth(0, Math.max(0, column));
        int top = mManager.getRowsHeight(0, Math.max(0, row));
        if (holder.isDragging()) {
            left += mTouchPoint.x;
            top += mTouchPoint.y;
        }
        View view = holder.getItemView();
        view.layout(left - mState.getScrollX() + mManager.getHeaderRowWidth(),
                top - mState.getScrollY() + mManager.getHeaderColumnHeight(),
                left + mManager.getColumnWidth(column) - mState.getScrollX() + mManager.getHeaderRowWidth(),
                top + mManager.getRowHeight(row) - mState.getScrollY() + mManager.getHeaderColumnHeight());
    }

    private void refreshHeaderColumn(int column, View view) {
        int left = mManager.getColumnsWidth(0, Math.max(0, column)) + mManager.getHeaderRowWidth();
        view.layout(left - mState.getScrollX(),
                0,
                left + mManager.getColumnWidth(column) - mState.getScrollX(),
                mManager.getHeaderColumnHeight());
    }

    private void refreshHeaderRow(int row, View view) {
        int top = mManager.getRowsHeight(0, Math.max(0, row)) + mManager.getHeaderColumnHeight();
        view.layout(0,
                top - mState.getScrollY(),
                mManager.getHeaderRowWidth(),
                top + mManager.getHeaderColumnHeight() - mState.getScrollY());
    }

    private void recycleViews() {

        if (mAdapter == null) {
            return;
        }

        for (TTableAdapter.ViewHolder holder : mViewHolders.getAll()) {
            if (holder != null) {
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

        for (Iterator<Map.Entry<Integer, TTableAdapter.ViewHolder>> it = mHeaderColumnViewHolders.entrySet().iterator(); it.hasNext(); ) {
            TTableAdapter.ViewHolder holder = it.next().getValue();
            if (holder != null) {
                View view = holder.getItemView();
                if (view.getRight() < 0 || view.getLeft() > mSettings.getLayoutWidth()) {
                    mRecycler.pushRecycledView(holder, HOLDER_HEADER_COLUMN_TYPE);
                    it.remove();
                    removeView(view);
                    mAdapter.onViewHolderRecycled(holder); // TODO FIX THIS!!
                }
            }
        }

        for (Iterator<Map.Entry<Integer, TTableAdapter.ViewHolder>> it = mHeaderRowViewHolders.entrySet().iterator(); it.hasNext(); ) {
            TTableAdapter.ViewHolder holder = it.next().getValue();
            if (holder != null) {
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
                TTableAdapter.ViewHolder viewHolder = mViewHolders.get(i, j);
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
            TTableAdapter.ViewHolder viewHolder = mHeaderRowViewHolders.get(i);
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

                refreshHeaderRow(i, view);
                //row header holders
//                TTableAdapter.ViewHolder header = mHeaderColumnViewHolders.get(i);
            }
        }
        for (int i = leftColumn; i <= rightColumn; i++) {
            // column header holders
            TTableAdapter.ViewHolder viewHolder = mHeaderColumnViewHolders.get(i);
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

                refreshHeaderColumn(i, view);
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
        mTouchPoint.offset((int) event.getX(), (int) event.getY());
        if (mScrollHelper.isDragging() && event.getAction() != MotionEvent.ACTION_UP) {
            mScrollerDragAndDropRunnable.touch((int) event.getX(), (int) event.getY());
//            scrollBy((int) event.getX(), (int) event.getY());
            return true;
        }
        return mScrollHelper.onTouch(event);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean result;

        final TTableAdapter.ViewHolder viewHolder = (TTableAdapter.ViewHolder) child.getTag(R.id.tag_view_holder);

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
        TTableAdapter.ViewHolder viewHolder = getViewHolderByPosition((int) e.getX(), (int) e.getY());
        if (viewHolder != null) {
            viewHolder.getItemView().callOnClick();
        }
        return true;
    }

    @Override
    public boolean onLongPress(MotionEvent e) {
        TTableAdapter.ViewHolder viewHolder = getViewHolderByPosition((int) e.getX(), (int) e.getY());
        if (viewHolder != null) {
            if (viewHolder.getItemType() == HOLDER_HEADER_COLUMN_TYPE) {
                mTouchPoint.set(0, 0);
                setDraggingToColumn(viewHolder.getColumnIndex(), true);
            }
        }
        return viewHolder != null;
    }

    private void setDraggingToColumn(int column, boolean isDragging) {
        Collection<TTableAdapter.ViewHolder> holders = mViewHolders.getColumnItems(column);
        if (holders != null) {
            for (TTableAdapter.ViewHolder holder : holders) {
                holder.setIsDragging(isDragging);
            }
        }
    }

    @Override
    public boolean onActionUp(MotionEvent e) {
        if (!mScrollerDragAndDropRunnable.isFinished()) {
            mScrollerDragAndDropRunnable.stop();
        }
        Collection<TTableAdapter.ViewHolder> holders = mViewHolders.getAll();
        for (TTableAdapter.ViewHolder holder : holders) {
            holder.setIsDragging(false);
        }
        return true;
    }

    @Nullable
    private TTableAdapter.ViewHolder getViewHolderByPosition(int x, int y) {
        TTableAdapter.ViewHolder viewHolder;
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
