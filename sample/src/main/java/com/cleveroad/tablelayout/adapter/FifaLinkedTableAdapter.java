package com.cleveroad.tablelayout.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cleveroad.library.LinkedTableAdapter;
import com.cleveroad.library.ViewHolderImpl;
import com.cleveroad.tablelayout.R;
import com.cleveroad.tablelayout.datasource.TableDataSource;

public class FifaLinkedTableAdapter extends LinkedTableAdapter<ViewHolderImpl> {
    private static final int COLUMN_PHOTO = 0;
    private static final int COLUMN_NAME = 1;
    private static final int COLUMN_POSITION = 2;
    private static final int COLUMN_DATE_OF_BIRTH = 3;
    private static final int COLUMN_FOOTBALL_TEAM = 4;
    private final LayoutInflater mLayoutInflater;
    private final TableDataSource<String, String, String, String> mTableDataSource;

    public FifaLinkedTableAdapter(Context context, TableDataSource<String, String, String, String> tableDataSource) {
        mLayoutInflater = LayoutInflater.from(context);
        mTableDataSource = tableDataSource;
    }

    @Override
    public int getRowCount() {
        return mTableDataSource.getRowsCount();
    }

    @Override
    public int getColumnCount() {
        return mTableDataSource.getColumnsCount();
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateViewHolder(@NonNull ViewGroup parent) {
        return new TestViewHolder(mLayoutInflater.inflate(R.layout.item_card, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderColumnViewHolder(mLayoutInflater.inflate(R.layout.item_header_card, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateRowHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderRowViewHolder(mLayoutInflater.inflate(R.layout.item_header_card, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateLeftTopHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderLeftTopViewHolder(mLayoutInflater.inflate(R.layout.item_header_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderImpl viewHolder, int row, int column) {
        if (viewHolder instanceof TestViewHolder) {
            TestViewHolder vh = (TestViewHolder) viewHolder;
            String itemData = mTableDataSource.getItemData(row, column).trim();

            switch (column) {
                case COLUMN_FOOTBALL_TEAM:
                case COLUMN_PHOTO: {
                    vh.tvText.setVisibility(View.GONE);
                    vh.ivImage.setVisibility(View.VISIBLE);
                    Glide.with(vh.ivImage.getContext())
                            .load(itemData)
                            .centerCrop()
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(vh.ivImage);
                    break;
                }
                case COLUMN_NAME:
                case COLUMN_POSITION:
                case COLUMN_DATE_OF_BIRTH: {
                    vh.tvText.setVisibility(View.VISIBLE);
                    vh.ivImage.setVisibility(View.GONE);
                    vh.tvText.setText(itemData);
                    break;
                }
            }

        }
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        if (viewHolder instanceof TestHeaderColumnViewHolder) {
            TestHeaderColumnViewHolder vh = (TestHeaderColumnViewHolder) viewHolder;
            vh.tvText.setText(mTableDataSource.getColumnHeaderData(column));
        }
    }

    public void onBindHeaderRowViewHolder(@NonNull ViewHolderImpl viewHolder, int row) {
        if (viewHolder instanceof TestHeaderRowViewHolder) {
            TestHeaderRowViewHolder vh = (TestHeaderRowViewHolder) viewHolder;
            vh.tvText.setText(mTableDataSource.getRowHeaderData(row));
        }
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull ViewHolderImpl viewHolder) {
        if (viewHolder instanceof TestHeaderLeftTopViewHolder) {
            TestHeaderLeftTopViewHolder vh = (TestHeaderLeftTopViewHolder) viewHolder;
            vh.tvText.setText(mTableDataSource.getFirstHeaderData());
        }
    }

    @Override
    public int getColumnWidth(int column) {
        switch (column) {
            case COLUMN_FOOTBALL_TEAM:
                return 300;
            case COLUMN_NAME:
                return 400;
            case COLUMN_POSITION:
                return 200;
            case COLUMN_DATE_OF_BIRTH:
                return 300;
            case COLUMN_PHOTO:
                return 300;
            default:
                return 100;

        }
    }

    @Override
    public int getHeaderColumnHeight() {
        return 100/*360*/;
    }

    @Override
    public int getRowHeight(int row) {
        return 360;
    }

    @Override
    public int getHeaderRowWidth() {
        return 160;
    }

    @Override
    public void onViewHolderRecycled(@NonNull ViewHolderImpl viewHolder) {
        //do nothing
    }

    //------------------------------------- view holders ------------------------------------------

    private static class TestViewHolder extends ViewHolderImpl {
        TextView tvText;
        ImageView ivImage;

        private TestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }

    private static class TestHeaderColumnViewHolder extends ViewHolderImpl {
        TextView tvText;

        private TestHeaderColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }

    }

    private static class TestHeaderRowViewHolder extends ViewHolderImpl {
        TextView tvText;

        TestHeaderRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            itemView.setBackgroundColor(Color.DKGRAY);
        }

    }

    private static class TestHeaderLeftTopViewHolder extends ViewHolderImpl {
        TextView tvText;

        private TestHeaderLeftTopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }
    }

}
