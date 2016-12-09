package com.cleveroad.tablelayout.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cleveroad.library.ItemType;
import com.cleveroad.library.TableLayout;
import com.cleveroad.library.adapter.BaseTableAdapter;
import com.cleveroad.tablelayout.R;

public class SortingAdapter extends BaseTableAdapter<BaseTableAdapter.ViewHolderImpl> {
    private static final int ROWS = 50;
    private static final int COLUMNS = 50;
    private final String mData[][] = new String[ROWS][COLUMNS];
    private final float mDensity;
    private final LayoutInflater mLayoutInflater;
    private Context mContext;

    private String mColumns[] = new String[COLUMNS];
    private final boolean mColumnOrder[] = new boolean[mColumns.length];

    public SortingAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mDensity = context.getResources().getDisplayMetrics().density;
        mColumns = new String[COLUMNS];

        for (int columns = COLUMNS, i = 0; i < columns; i++) {
            String columnText = "C" + (i + 1);
            mColumns[i] = columnText;
            for (int rows = ROWS, j = 0; j < rows; j++) {
                String rowText = "R" + (j + 1) + columnText;
                mData[j][i] = rowText;
            }
        }
    }

    @Override
    public void changeColumns(int columnIndex, int columnToIndex) {
        // columnIndex = 2, columnToIndex = 5
        // 0,1,2,3,4,5,6,7,8,9 -> 0,1,3,4,5,2,6,7,8,9
        //change 2 -> 5
        //1: switch "from" column with "to" column
        // 0,1,(2),3,4,(5),6,7,8,9 - > 0,1,(5),3,4,(2),6,7,8,9
        switchTwoColumns(columnIndex, columnToIndex);
        switchTwoColumnHeaders(columnIndex, columnToIndex);
        // first step - change in what way we shall do loop
        if (columnIndex < columnToIndex) {

            for (int i = columnIndex; i < columnToIndex - 1; i++) {
                // from left to right
                // 0,1,(5),(3),4,2,6,7,8,9 - > 0,1,(3),(5),4,2,6,7,8,9
                // 0,1,3,(5),(4),2,6,7,8,9 - > 0,1,3,(4),(5),2,6,7,8,9
                switchTwoColumns(i, i + 1);
                switchTwoColumnHeaders(i, i + 1);
            }

        } else if (columnIndex > columnToIndex) {
            // from right to left

            for (int i = columnIndex; i > columnToIndex + 1; i--) {
                switchTwoColumns(i, i - 1);
                switchTwoColumnHeaders(i, i - 1);
            }
        }
    }

    void switchTwoColumns(int columnIndex, int columnToIndex) {
        for (int i = 0; i < mData.length; i++) {
            String cellData = mData[i][columnToIndex];
            mData[i][columnToIndex] = mData[i][columnIndex];
            mData[i][columnIndex] = cellData;
        }
    }

    void switchTwoColumnHeaders(int columnIndex, int columnToIndex) {
        String cellData = mColumns[columnToIndex];
        mColumns[columnToIndex] = mColumns[columnIndex];
        mColumns[columnIndex] = cellData;
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
    public BaseTableAdapter.ViewHolderImpl onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
        if (itemType == ItemType.BODY) {
            return new SortingAdapter.CardViewHolder(mLayoutInflater.inflate(getLayoutResource(itemType), parent, false));
        } else {
            return new SortingAdapter.ViewHolder(mLayoutInflater.inflate(getLayoutResource(itemType), parent, false));
        }

    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onBindViewHolder(@NonNull final BaseTableAdapter.ViewHolderImpl pViewHolder, final int row, final int column) {
        if (pViewHolder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) pViewHolder;
            if (column >= 0) {
                viewHolder.mTvData.setText(mColumns[column]);
            }
        } else if (pViewHolder instanceof CardViewHolder) {
            CardViewHolder viewHolder = (CardViewHolder) pViewHolder;

            Glide
                    .with(mContext)
                    .load("http://lorempixel.com/400/400/")
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher)
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                            Log.e("Adapter", model, e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            Log.e("Adapter", "onResourceReady");
                            return false;
                        }
                    })
                    .into(viewHolder.ivImage);

        }
    }

    @Override
    public int getItemWidth(int column) {
        final int width;
        if (column == TableLayout.FIXED_COLUMN_INDEX) {
            return 0;
        }
//        else if (column % 3 == 0) {
//            width = 60;
//        } else {
//            width = 120;
//        }
        String s = mColumns[column];
        if (s.startsWith("C3")) {
            width = 120;
        } else if (s.startsWith("C2")) {
            width = 80;
        } else {
            width = 60;
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
                return R.layout.item_card;
            default:
                throw new RuntimeException("Unsupported item type " + itemType);
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

    static class CardViewHolder extends BaseTableAdapter.ViewHolderImpl {
        ImageView ivImage;

        CardViewHolder(View itemView) {
            super(itemView);
            ivImage = ((ImageView) itemView.findViewById(R.id.ivImage));
        }
    }
}
