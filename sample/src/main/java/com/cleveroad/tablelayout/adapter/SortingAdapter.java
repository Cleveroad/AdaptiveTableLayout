package com.cleveroad.tablelayout.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleveroad.library.BaseTableAdapter;
import com.cleveroad.library.ItemType;
import com.cleveroad.library.TableLayout;
import com.cleveroad.tablelayout.R;
import com.cleveroad.tablelayout.utils.ValueInterpolator;

public class SortingAdapter extends BaseTableAdapter<SortingAdapter.ViewHolder> {
    private static final String mColumns[] = {
            "C1", "C2", "C3", "C4", "C5", "C6", "C7",
    };
    private static final boolean mColumnOrder[] = new boolean[mColumns.length];
    private static final String mData[][] = {
            {"R1C1", "R1C2", "R1C3", "R1C4", "R1C5", "R1C6", "R1C7"},
            {"R2C1", "R2C2", "R2C3", "R2C4", "R2C5", "R2C6", "R2C7"},
            {"R3C1", "R3C2", "R3C3", "R3C4", "R3C5", "R3C6", "R3C7"},
            {"R4C1", "R4C2", "R4C3", "R4C4", "R4C5", "R4C6", "R4C7"},
            {"R5C1", "R5C2", "R5C3", "R5C4", "R5C5", "R5C6", "R5C7"},
            {"R6C1", "R6C2", "R6C3", "R6C4", "R6C5", "R6C6", "R6C7"},
            {"R7C1", "R7C2", "R7C3", "R7C4", "R7C5", "R7C6", "R7C7"},
            {"R8C1", "R8C2", "R8C3", "R8C4", "R8C5", "R8C6", "R8C7"},
            {"R9C1", "R9C2", "R9C3", "R9C4", "R9C5", "R9C6", "R9C7"},
            {"R10C1","R10C2","R10C3","R10C4","R10C5","R10C6","R10C7"},
    };

    private final float mDensity;
    private final LayoutInflater mLayoutInflater;
    private final ValueInterpolator interpolatorR, interpolatorG, interpolatorB;

    public SortingAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mDensity = context.getResources().getDisplayMetrics().density;
        int startColor = ContextCompat.getColor(context, R.color.colorTable1);
        int endColor = ContextCompat.getColor(context, R.color.colorTable2);
        interpolatorR = new ValueInterpolator(0, mColumns.length + mData.length, Color.red(endColor), Color.red(startColor));
        interpolatorG = new ValueInterpolator(0, mColumns.length + mData.length, Color.green(endColor), Color.green(startColor));
        interpolatorB = new ValueInterpolator(0, mColumns.length + mData.length, Color.blue(endColor), Color.blue(startColor));
    }

    @Override
    public int getRowCount() {
        return mData.length;
    }

    @Override
    public int getColumnCount() {
        return mColumns.length;
    }

    @NonNull
    @Override
    public SortingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
        return new SortingAdapter.ViewHolder(mLayoutInflater.inflate(getLayoutResource(itemType), parent, false));
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onBindViewHolder(@NonNull final SortingAdapter.ViewHolder viewHolder, final int row, final int column) {
        int color = Color.rgb(
                (int) interpolatorR.map(row + column),
                (int) interpolatorG.map(row + column),
                (int) interpolatorB.map(row + column));

        switch (getItemType(row, column)) {
            case ItemType.BODY:
                viewHolder.mTvData.setText(mData[row][column]);
                viewHolder.mTvData.setBackgroundColor(color);
                break;
            case ItemType.FIXED_ROW:
                viewHolder.mTvData.setText(mColumns[column]);
                viewHolder.mIvOrder.setRotation(mColumnOrder[column] ? 180 : 0);

                viewHolder.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sortData(column, mColumnOrder[column]);
                        mColumnOrder[column] = !mColumnOrder[column];
                        viewHolder.mIvOrder.setRotation(mColumnOrder[column] ? 180 : 0);
                        notifyDataSetChanged();
                    }
                });
            default:
                viewHolder.getItemView().setBackgroundColor(color);
        }
    }

    @Override
    public int getItemWidth(int column) {
        final int width;
        if (column == TableLayout.FIXED_COLUMN_INDEX) {
            width = 0;
        } else {
            width = 80;
        }
        return Math.round(width * mDensity);
    }

    @Override
    public int getItemHeight(int row) {
        final int height;
        if (row == TableLayout.FIXED_ROW_INDEX) {
            height = 50;
        } else {
            height = 80;
        }
        return Math.round(height * mDensity);
    }

    @Override
    public int getItemType(int row, int column) {
        if (row == TableLayout.FIXED_ROW_INDEX && column == TableLayout.FIXED_COLUMN_INDEX) {
            return ItemType.FIRST_HEADER;
        } else if (row == TableLayout.FIXED_ROW_INDEX) {
            return ItemType.FIXED_ROW;
        } else if (column == TableLayout.FIXED_COLUMN_INDEX) {
            return ItemType.FIXED_COLUMN;
        } else {
            return ItemType.BODY;
        }
    }

    @SuppressLint("SwitchIntDef")
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
            default:
                throw new RuntimeException("Unsupported item type " + itemType);
        }
    }

    private void sortData(int column, boolean order) {
        for (int i = 0; i < mData.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < mData.length - i - 1; j++) {
                if ((mData[j][column].compareTo(mData[j + 1][column]) > 0) == order) {
                    String[] tmp = mData[j];
                    mData[j] = mData[j + 1];
                    mData[j + 1] = tmp;
                    swapped = true;
                }
            }

            if(!swapped) {
                break;
            }
        }
    }

    static class ViewHolder extends BaseTableAdapter.ViewHolderImpl {
        TextView mTvData;
        ImageView mIvOrder;

        ViewHolder(View itemView) {
            super(itemView);
            mTvData = ((TextView) itemView.findViewById(android.R.id.text1));
            mIvOrder = ((ImageView) itemView.findViewById(android.R.id.icon1));
        }
    }
}
