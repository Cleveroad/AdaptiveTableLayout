package com.cleveroad.tablelayout.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cleveroad.library.tlib.TBaseTableAdapter;
import com.cleveroad.tablelayout.R;

public class TSampleTableAdapter extends TBaseTableAdapter<TBaseTableAdapter.ViewHolderImpl> {
    public static final int ROWS = 20;
    public static final int COLUMNS = 20;
    private final LayoutInflater mLayoutInflater;

    private final String mData[][] = new String[ROWS][COLUMNS];
    private final String mColumns[] = new String[COLUMNS];
    private final String mRows[] = new String[ROWS];

    public TSampleTableAdapter(Context context) {
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

    @Override
    public void changeColumns(int columnIndex, int columnToIndex) {
        switchTwoColumns(columnIndex, columnToIndex);
        switchTwoColumnHeaders(columnIndex, columnToIndex);
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
    public void changeRows(int rowIndex, int rowToIndex) {
        switchTwoRows(rowIndex, rowToIndex);
        switchTwoRowHeaders(rowIndex, rowToIndex);
    }

    void switchTwoRows(int rowIndex, int rowToIndex) {
        for (int i = 0; i < mData.length; i++) {
            String cellData = mData[rowToIndex][i];
            mData[rowToIndex][i] = mData[rowIndex][i];
            mData[rowIndex][i] = cellData;
        }
    }

    void switchTwoRowHeaders(int rowIndex, int rowToIndex) {
        String cellData = mRows[rowToIndex];
        mRows[rowToIndex] = mRows[rowIndex];
        mRows[rowIndex] = cellData;
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
        return new TTestViewHolder(mLayoutInflater.inflate(R.layout.item_card, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TTestHeaderColumnViewHolder(mLayoutInflater.inflate(R.layout.item_header_card, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateRowHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TTestHeaderRowViewHolder(mLayoutInflater.inflate(R.layout.item_header_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderImpl viewHolder, int row, int column) {
        if (viewHolder instanceof TTestViewHolder) {
            TTestViewHolder vh = (TTestViewHolder) viewHolder;
            vh.tvText.setText(mData[row][column]);
        }
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        if (viewHolder instanceof TTestHeaderColumnViewHolder) {
            TTestHeaderColumnViewHolder vh = (TTestHeaderColumnViewHolder) viewHolder;
            vh.tvText.setText(mColumns[column]);
        }
    }

    public void onBindHeaderRowViewHolder(@NonNull ViewHolderImpl viewHolder, int row) {
        if (viewHolder instanceof TTestHeaderRowViewHolder) {
            TTestHeaderRowViewHolder vh = (TTestHeaderRowViewHolder) viewHolder;
            vh.tvText.setText(mRows[row]);
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

    public static class TTestViewHolder extends TBaseTableAdapter.ViewHolderImpl implements View.OnClickListener {
        TextView tvText;

        public TTestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.e("ViewHolder", "onClick " + tvText.getText().toString());
        }
    }

    public static class TTestHeaderColumnViewHolder extends TBaseTableAdapter.ViewHolderImpl implements View.OnClickListener {
        TextView tvText;

        public TTestHeaderColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.e("ViewHolder", "Header onClick " + tvText.getText().toString());
        }
    }

    public static class TTestHeaderRowViewHolder extends TBaseTableAdapter.ViewHolderImpl implements View.OnClickListener {
        TextView tvText;

        public TTestHeaderRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.e("ViewHolder", "Header Row onClick " + tvText.getText().toString());
        }
    }
}
