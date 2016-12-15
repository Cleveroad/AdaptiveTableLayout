package com.cleveroad.tablelayout.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cleveroad.library.adapter.LinkedTableAdapter;
import com.cleveroad.library.adapter.ViewHolderImpl;
import com.cleveroad.tablelayout.R;

public class SampleLinkedTableAdapter extends LinkedTableAdapter<ViewHolderImpl> {
    public static final int ROWS = 2_000_000;
    public static final int COLUMNS = 2_000_000;
    private final LayoutInflater mLayoutInflater;


    public SampleLinkedTableAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getRowCount() {
        return ROWS;
    }

    @Override
    public int getColumnCount() {
        return COLUMNS;
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
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
            vh.tvText.setText("R" + row + "C" + column);
        }
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        if (viewHolder instanceof TestHeaderColumnViewHolder) {
            TestHeaderColumnViewHolder vh = (TestHeaderColumnViewHolder) viewHolder;
            vh.tvText.setText("C" + column);
        }
    }

    public void onBindHeaderRowViewHolder(@NonNull ViewHolderImpl viewHolder, int row) {
        if (viewHolder instanceof TestHeaderRowViewHolder) {
            TestHeaderRowViewHolder vh = (TestHeaderRowViewHolder) viewHolder;
            vh.tvText.setText("R" + row);
        }
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull ViewHolderImpl viewHolder) {
        if (viewHolder instanceof TestHeaderLeftTopViewHolder) {
            TestHeaderLeftTopViewHolder vh = (TestHeaderLeftTopViewHolder) viewHolder;
            vh.tvText.setText("LeftTop");
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

    static class TestViewHolder extends ViewHolderImpl {
        TextView tvText;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }
    }

    static class TestHeaderColumnViewHolder extends ViewHolderImpl {
        TextView tvText;

        public TestHeaderColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }

    }

    static class TestHeaderRowViewHolder extends ViewHolderImpl {
        TextView tvText;

        public TestHeaderRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }

    }

    static class TestHeaderLeftTopViewHolder extends ViewHolderImpl {
        TextView tvText;

        public TestHeaderLeftTopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }

    }
}
