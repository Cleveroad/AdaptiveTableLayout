package com.cleveroad.tablelayout.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cleveroad.library.adapter.BaseTableAdapter;
import com.cleveroad.library.ItemType;
import com.cleveroad.library.TableLayout;
import com.cleveroad.tablelayout.R;
import com.cleveroad.tablelayout.model.MockCategory;
import com.cleveroad.tablelayout.model.MockItem;
import com.cleveroad.tablelayout.utils.ValueInterpolator;

public class TableAdapter extends BaseTableAdapter<TableAdapter.ViewHolder>{
    private static final int COL_COUNT = 100;
    private static final int ROW_COUNT = 5_000;
    private static final boolean IS_DEBUG = true;
    private static final boolean IS_FIXED_ROW_ENABLED = true;
    private static final boolean IS_FIXED_COLUMN_ENABLED = true;
    private static final boolean IS_CATEGORY_ROW_ENABLED = false;
    private static final MockCategory MOCK_CATEGORIES[] = {
            new MockCategory("Mock1", ROW_COUNT)
    };

    private final float mDensity;
    private final LayoutInflater mLayoutInflater;
    private final ValueInterpolator interpolatorR, interpolatorG, interpolatorB;

    public TableAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mDensity = context.getResources().getDisplayMetrics().density;

        int startColor = ContextCompat.getColor(context, R.color.colorTable1);
        int endColor = ContextCompat.getColor(context, R.color.colorTable2);
        interpolatorR = new ValueInterpolator(0, 5, Color.red(endColor), Color.red(startColor));
        interpolatorG = new ValueInterpolator(0, 7, Color.green(endColor), Color.green(startColor));
        interpolatorB = new ValueInterpolator(0, 11, Color.blue(endColor), Color.blue(startColor));
    }

    @Override
    public void changeColumns(int rowIndex, int rowToIndex) {

    }

    @Override
    public int getRowCount() {
        return MOCK_CATEGORIES.length + ROW_COUNT * MOCK_CATEGORIES.length;
    }

    @Override
    public int getColumnCount() {
        return COL_COUNT - 1;
    }

    @Override
    public int getItemWidth(int column) {
        /*final*/ int width;
        if (column == TableLayout.FIXED_COLUMN_INDEX) {
            width = IS_FIXED_COLUMN_ENABLED ? 50 : 0;
        } else {
            width = 80;
        }
        if(IS_DEBUG && column == 5) width = 250;
        if(IS_DEBUG && column == 3) width = 30;
        return Math.round(width * mDensity);
    }

    @Override
    public int getItemHeight(int row) {
        /*final */int height;
        if (row == TableLayout.FIXED_ROW_INDEX) {
            height = IS_FIXED_ROW_ENABLED ? 50 : 0;
        } else if (isCategoryHeader(row)) {
            height = IS_FIXED_COLUMN_ENABLED && IS_FIXED_ROW_ENABLED && IS_CATEGORY_ROW_ENABLED ? 25 : 0;
        } else {
            height = 80;
        }
        if(IS_DEBUG && row == 5) height = 250;
        if(IS_DEBUG && row == 3) height = 30;
        return Math.round(height * mDensity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(getLayoutResource(viewType), parent, false));
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int row, int column) {

        int color = Color.rgb(
                (int) interpolatorR.map(row + column),
                (int) interpolatorG.map(row + column),
                (int) interpolatorB.map(row + column));


        switch (getItemType(row, column)) {
            case ItemType.BODY:
                viewHolder.mTextView.setBackgroundColor(color);
                viewHolder.mTextView.setText(getItem(row).getProp(column));
                break;
            case ItemType.CATEGORY:
                viewHolder.getItemView().setBackgroundColor(color);
                if(column == TableLayout.FIXED_COLUMN_INDEX) {
                    viewHolder.mTextView.setText(getCategory(row).getName());
                } else {
                    viewHolder.mTextView.setText(null);
                }
                break;
            default:
                viewHolder.mTextView.setText(viewHolder.getRowIndex() + ";" + viewHolder.getColumnIndex());
                viewHolder.getItemView().setBackgroundColor(color);
        }
    }

    @ItemType
    @Override
    public int getItemType(int row, int column) {
        if (row == TableLayout.FIXED_ROW_INDEX && column == TableLayout.FIXED_COLUMN_INDEX) {
            return ItemType.FIRST_HEADER;
        } else if (row == TableLayout.FIXED_ROW_INDEX) {
            return ItemType.FIXED_ROW;
        } else if (isCategoryHeader(row)) {
            return ItemType.CATEGORY;
        } else if (column == TableLayout.FIXED_COLUMN_INDEX) {
            return ItemType.FIXED_COLUMN;
        } else {
            return ItemType.BODY;
        }
    }

    private int getLayoutResource(@ItemType int itemType) {
        switch (itemType) {
            case ItemType.FIRST_HEADER:
                return R.layout.item_table_header_first;
            case ItemType.FIXED_ROW:
                return R.layout.item_table_fixed_row;
            case ItemType.FIXED_COLUMN:
                return R.layout.item_table_fixed_column;
            case ItemType.BODY:
                return R.layout.item_body;
            case ItemType.CATEGORY:
                return R.layout.item_table_category;
            case ItemType.IGNORE:
            default:
                throw new RuntimeException("Unsupported item type " + itemType);
        }
    }

    private boolean isCategoryHeader(int row) {
        int categoryIndex = 0;
        while (row > 0) {
            row -= MOCK_CATEGORIES[categoryIndex].size() + 1;
            categoryIndex++;
        }
        return row == 0;
    }

    @SuppressWarnings("unused")
    private MockCategory getCategory(int row) {
        int categoryIndex = 0;
        while (row >= 0) {
            row -= MOCK_CATEGORIES[categoryIndex].size() + 1;
            categoryIndex++;
        }
        return MOCK_CATEGORIES[categoryIndex - 1];
    }

    @SuppressWarnings("unused")
    private MockItem getItem(int row) {
        int categoryIndex = 0;
        while (row >= 0) {
            row -= MOCK_CATEGORIES[categoryIndex].size() + 1;
            categoryIndex++;
        }
        categoryIndex--;
        return MOCK_CATEGORIES[categoryIndex].get(row + MOCK_CATEGORIES[categoryIndex].size());
    }

    static class ViewHolder extends BaseTableAdapter.ViewHolderImpl {
        TextView mTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mTextView = ((TextView) itemView.findViewById(android.R.id.text1));
        }
    }
}