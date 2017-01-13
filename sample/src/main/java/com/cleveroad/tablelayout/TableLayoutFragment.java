package com.cleveroad.tablelayout;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.cleveroad.tablelayout.adapter.FifaLinkedTableAdapter;
import com.cleveroad.tablelayout.datasource.CsvFileDataSourceImpl;

import java.io.FileReader;
import java.io.InputStreamReader;

public class TableLayoutFragment
        extends Fragment
        implements OnItemClickListener, OnItemLongClickListener {
    private static final String TAG = TableLayoutFragment.class.getSimpleName();
    private static final String EXTRA_CSV_FILE = "EXTRA_CSV_FILE";
    private static final String EXTRA_ASSETS_FILE = "EXTRA_ASSETS_FILE";
    @Nullable
    private Uri mCsvFile;
    @Nullable
    private String mAssetsFileName;
    private CsvFileDataSourceImpl mCsvFileDataSource;
    private TableLayout mTableLayout;

    public static TableLayoutFragment newInstance(@NonNull String filename) {

        Bundle args = new Bundle();
        args.putString(EXTRA_CSV_FILE, filename);
        TableLayoutFragment fragment = new TableLayoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mCsvFile = Uri.parse(getArguments().getString(EXTRA_CSV_FILE));
        mAssetsFileName = getArguments().getString(EXTRA_ASSETS_FILE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_layout, container, false);

        mTableLayout = (TableLayout) view.findViewById(R.id.tableLayout);

        mCsvFileDataSource = new CsvFileDataSourceImpl() {
            @Override
            protected InputStreamReader getInputStreamReader() throws Exception {
                if (mCsvFile != null) {
                    return new FileReader(mCsvFile.getEncodedPath());
                } else {
                    return new InputStreamReader(getContext().getAssets().open(mAssetsFileName));
                }
            }
        };
        final LinkedTableAdapter adapter = new FifaLinkedTableAdapter(getContext(), mCsvFileDataSource);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);

        mTableLayout.setAdapter(adapter);

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
