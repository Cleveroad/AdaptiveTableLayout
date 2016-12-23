package com.cleveroad.tablelayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.library.LinkedTableAdapter;
import com.cleveroad.library.OnItemClickListener;
import com.cleveroad.library.OnItemLongClickListener;
import com.cleveroad.library.TableLayout;
import com.cleveroad.tablelayout.adapter.SampleLinkedTableAdapter;
import com.cleveroad.tablelayout.datasource.CsvFileDataSourceImpl;

import java.io.File;

public class TableLayoutFragment
        extends Fragment
        implements OnItemClickListener, OnItemLongClickListener {
    private static final String TAG = TableLayoutFragment.class.getSimpleName();
    private static final String EXTRA_CSV_FILE = "EXTRA_CSV_FILE";
    private File mCsvFile;
    private CsvFileDataSourceImpl mCsvFileDataSource;

    public static TableLayoutFragment newInstance(File csvFile) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CSV_FILE, csvFile);

        TableLayoutFragment fragment = new TableLayoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mCsvFile = (File) getArguments().getSerializable(EXTRA_CSV_FILE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_layout, container, false);

        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.tableLayout);


        mCsvFileDataSource = new CsvFileDataSourceImpl(mCsvFile);
        final LinkedTableAdapter adapter = new SampleLinkedTableAdapter(getContext(), mCsvFileDataSource);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        tableLayout.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        mCsvFileDataSource.destroy();
        super.onDestroyView();
    }

    //------------------------------------- adapter callbacks --------------------------------------
    @Override
    public void onItemClick(int row, int column) {
        Log.e(TAG, "onItemClick = " + row + " | " + column);
    }

    @Override
    public void onRowHeaderClick(int row) {
        Log.e(TAG, "onRowHeaderClick = " + row);
    }

    @Override
    public void onColumnHeaderClick(int column) {
        Log.e(TAG, "onColumnHeaderClick = " + column);
    }

    @Override
    public void onLeftTopHeaderClick() {
        Log.e(TAG, "onLeftTopHeaderClick ");
    }

    @Override
    public void onItemLongClick(int row, int column) {
        Log.e(TAG, "onItemLongClick = " + row + " | " + column);
    }

    @Override
    public void onLeftTopHeaderLongClick() {
        Log.e(TAG, "onLeftTopHeaderLongClick ");
    }
}
