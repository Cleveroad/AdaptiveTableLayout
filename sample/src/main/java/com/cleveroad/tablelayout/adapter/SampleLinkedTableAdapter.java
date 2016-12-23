package com.cleveroad.tablelayout.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cleveroad.library.LinkedTableAdapter;
import com.cleveroad.library.ViewHolderImpl;
import com.cleveroad.tablelayout.R;
import com.cleveroad.tablelayout.datasource.TableDataSource;

import java.io.File;

public class SampleLinkedTableAdapter extends LinkedTableAdapter<ViewHolderImpl> {
    private final LayoutInflater mLayoutInflater;
    private final TableDataSource<String, String , String, String> mTableDataSource;

    public SampleLinkedTableAdapter(Context context, TableDataSource<String, String, String, String> tableDataSource) {
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
            vh.tvText.setText(mTableDataSource.getItemData(row, column));
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
        if (column % 2 == 0) {
            return 240;
        } else {
            return 160;
        }
    }

    @Override
    public int getHeaderColumnHeight() {
        return 160;
    }

    @Override
    public int getRowHeight(int row) {
        return 160;
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

        private TestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
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
