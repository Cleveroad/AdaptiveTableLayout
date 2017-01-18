package com.cleveroad.tablelayout.ui;

import com.cleveroad.library.LinkedTableAdapter;
import com.cleveroad.library.OnItemClickListener;
import com.cleveroad.library.OnItemLongClickListener;
import com.cleveroad.library.TableLayout;
import com.cleveroad.tablelayout.R;
import com.cleveroad.tablelayout.adapter.SampleLinkedTableAdapter;
import com.cleveroad.tablelayout.datasource.CsvFileDataSourceImpl;
import com.cleveroad.tablelayout.datasource.UpdateFileCallback;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class TableLayoutFragment
        extends Fragment
        implements OnItemClickListener, OnItemLongClickListener, UpdateFileCallback {
    private static final String TAG = TableLayoutFragment.class.getSimpleName();
    private static final String EXTRA_CSV_FILE = "EXTRA_CSV_FILE";
    private Uri mCsvFile;
    private LinkedTableAdapter mTableAdapter;
    private CsvFileDataSourceImpl mCsvFileDataSource;
    private TableLayout mTableLayout;
    private Toolbar mToolbar;

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
        mCsvFileDataSource = new CsvFileDataSourceImpl(getContext(), mCsvFile);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tab_layout, container, false);

        mTableLayout = (TableLayout) view.findViewById(R.id.tableLayout);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        mToolbar.inflateMenu(R.menu.table_layout);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.actionSave) {
                    mCsvFileDataSource.applyChanges(
                            getLoaderManager(),
                            mTableLayout.getLinkedAdapterRowsModifications(),
                            mTableLayout.getLinkedAdapterColumnsModifications(),
                            TableLayoutFragment.this);
                }
                return true;
            }
        });

       initAdapter();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EditRowDialog.REQUEST_CODE_EDIT_SONG && resultCode == Activity.RESULT_OK && data != null) {
            int rowIndex = data.getIntExtra(EditRowDialog.EXTRA_ROW_NUMBER, 0);
            List<String> values = data.getStringArrayListExtra(EditRowDialog.EXTRA_VALUES);
            mCsvFileDataSource.updateRow(rowIndex, values);
            mTableAdapter.notifyRowChanged(rowIndex);
        }
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

        EditRowDialog.newInstance(
                mCsvFileDataSource.getColumnHeaders(),
                mCsvFileDataSource.getRowValues(row),
                row).show(getChildFragmentManager(), EditRowDialog.class.getSimpleName());
    }


    @Override
    public void onLeftTopHeaderLongClick() {
        Log.e(TAG, "onLeftTopHeaderLongClick ");
    }

    @Override
    public void onFileUpdated(String fileName, boolean isSuccess) {
        View view = getView();
        if(view == null) {
            return;
        }

        if (isSuccess) { //if data source have been changed
            initAdapter();
            mTableAdapter.notifyDataSetChanged();
            Snackbar.make(view, R.string.changes_saved, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(view, R.string.unexpected_error, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void initAdapter() {

        mTableAdapter = new SampleLinkedTableAdapter(getContext(), mCsvFileDataSource);
        mTableAdapter.setOnItemClickListener(this);
        mTableAdapter.setOnItemLongClickListener(this);

        mTableLayout.setAdapter(mTableAdapter);
    }
}
