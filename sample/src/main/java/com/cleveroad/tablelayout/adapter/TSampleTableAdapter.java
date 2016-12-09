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


    public TSampleTableAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void changeColumns(int columnIndex, int columnToIndex) {

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
        return new TTestHeaderColumnViewHolder(mLayoutInflater.inflate(R.layout.item_header_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderImpl viewHolder, int row, int column) {
        if (viewHolder instanceof TTestViewHolder) {
            TTestViewHolder vh = (TTestViewHolder) viewHolder;
            vh.tvText.setText("R" + row + "C" + column);
        }
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        if (viewHolder instanceof TTestHeaderColumnViewHolder) {
            TTestHeaderColumnViewHolder vh = (TTestHeaderColumnViewHolder) viewHolder;
            vh.tvText.setText("HC " + column);
        }
    }

    public void onBindHeaderRowViewHolder(@NonNull ViewHolderImpl viewHolder, int row) {

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
        return 0;
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
}
