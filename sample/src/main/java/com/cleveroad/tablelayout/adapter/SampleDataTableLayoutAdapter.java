package com.cleveroad.tablelayout.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cleveroad.library.adapter.BaseDataTableLayoutAdapter;
import com.cleveroad.library.adapter.ViewHolderImpl;
import com.cleveroad.tablelayout.R;

public class SampleDataTableLayoutAdapter extends BaseDataTableLayoutAdapter<ViewHolderImpl> {
    static final int ROWS = 200;
    static final int COLUMNS = 200;
    private final LayoutInflater mLayoutInflater;

    private final String mData[][] = new String[ROWS][COLUMNS];
    private final String mColumns[] = new String[COLUMNS];
    private final String mRows[] = new String[ROWS];

    public SampleDataTableLayoutAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);

        for (int columns = COLUMNS, i = 0; i < columns; i++) {
            String columnText = "C" + (i + 1);
            mColumns[i] = columnText;
            for (int rows = ROWS, j = 0; j < rows; j++) {
                String rowText = "R" + (j + 1);
                mRows[j] = "R" + (j + 1);
                mData[j][i] = rowText + columnText;
            }
        }
    }

    public void putItem(int row, int column, String item) {
        mData[row][column] = item;
        notifyItemChanged(row, column);
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
            vh.tvText.setText(mData[row][column]);
        }
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        if (viewHolder instanceof TestHeaderColumnViewHolder) {
            TestHeaderColumnViewHolder vh = (TestHeaderColumnViewHolder) viewHolder;
            vh.tvText.setText(mColumns[column]);
        }
    }

    public void onBindHeaderRowViewHolder(@NonNull ViewHolderImpl viewHolder, int row) {
        if (viewHolder instanceof TestHeaderRowViewHolder) {
            TestHeaderRowViewHolder vh = (TestHeaderRowViewHolder) viewHolder;
            vh.tvText.setText(mRows[row]);
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

    @Override
    public void onViewHolderRecycled(@NonNull ViewHolderImpl viewHolder) {

    }

    @Override
    protected Object[][] getItems() {
        return mData;
    }

    @Override
    protected Object[] getRowHeaders() {
        return mRows;
    }

    @Override
    protected Object[] getColumnHeaders() {
        return mColumns;
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
