package com.cleveroad.tablelayout.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cleveroad.library.BaseTableAdapter;
import com.cleveroad.tablelayout.R;

public class SampleTableAdapter extends BaseTableAdapter<BaseTableAdapter.ViewHolderImpl> {
    public static final int ROWS = 2_000_000;
    public static final int COLUMNS = 2_000_000;
    private final LayoutInflater mLayoutInflater;


    public SampleTableAdapter(Context context) {
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

    public static class TestViewHolder extends BaseTableAdapter.ViewHolderImpl implements View.OnClickListener {
        TextView tvText;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.e("ViewHolder", "onClick " + tvText.getText().toString());
        }
    }

    public static class TestHeaderColumnViewHolder extends BaseTableAdapter.ViewHolderImpl implements View.OnClickListener {
        TextView tvText;

        public TestHeaderColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.e("ViewHolder", "Header onClick " + tvText.getText().toString());
        }
    }

    public static class TestHeaderRowViewHolder extends BaseTableAdapter.ViewHolderImpl implements View.OnClickListener {
        TextView tvText;

        public TestHeaderRowViewHolder(@NonNull View itemView) {
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
