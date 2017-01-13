package com.cleveroad.tablelayout;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import com.cleveroad.library.LinkedTableAdapter;
import com.cleveroad.library.OnItemClickListener;
import com.cleveroad.library.OnItemLongClickListener;
import com.cleveroad.library.TableLayout;
import com.cleveroad.tablelayout.adapter.FifaLinkedTableAdapter;
import com.cleveroad.tablelayout.datasource.CsvFileDataSourceImpl;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TableLayoutFragment
        extends Fragment
        implements OnItemClickListener, OnItemLongClickListener {
    private static final String TAG = TableLayoutFragment.class.getSimpleName();
    private static final String EXTRA_CSV_FILE = "EXTRA_CSV_FILE";
    private static final String EXTRA_ASSETS_FILE = "EXTRA_ASSETS_FILE";
    private static final int EDIT_ARTIST_REQUEST_CODE = 121;
    @Nullable
    private Uri mCsvFile;
    @Nullable
    private String mAssetsFileName;
    private LinkedTableAdapter mTableAdapter;
    private CsvFileDataSourceImpl mCsvFileDataSource;
    private ArtistDataSource mArtistDataSource;
    private TableLayout mTableLayout;
    private Toolbar mToolbar;

    public static TableLayoutFragment newInstance(@NonNull String filename) {

        Bundle args = new Bundle();
        args.putString(EXTRA_CSV_FILE, filename);
        args.putSerializable(EXTRA_CSV_FILE, csvFile);

        TableLayoutFragment fragment = new TableLayoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TableLayoutFragment newInstance(@NonNull String assetsFileName) {
        Bundle args = new Bundle();
        args.putString(EXTRA_ASSETS_FILE, assetsFileName);

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
            protected InputStreamReader getInputStreamReader() throws IOException {
                if (mCsvFile != null) {
                    return new FileReader(mCsvFile.getEncodedPath());
                } else {
                    return new InputStreamReader(getContext().getAssets().open(mAssetsFileName));
                }
            }
        };
        mArtistDataSource = new ArtistDataSourceWrapper(mCsvFileDataSource);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_layout, container, false);

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

                }
                return true;
            }
        });


        mTableLayout = (TableLayout) view.findViewById(R.id.tableLayout);

        mTableAdapter = new ArtistLinkedTableAdapter(getContext(), mArtistDataSource);
        mTableAdapter.setOnItemClickListener(this);
        mTableAdapter.setOnItemLongClickListener(this);

        mTableLayout.setAdapter(mTableAdapter);

        //rotation fix
        Fragment fragment = getChildFragmentManager().findFragmentByTag(EditArtistDialog.class.getSimpleName());
        if (fragment != null) {
            fragment.setTargetFragment(this, EDIT_ARTIST_REQUEST_CODE);
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_ARTIST_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            int rowIndex = data.getIntExtra(EditArtistDialog.EXTRA_ROW_NUMBER, 0);
            ArtistModel model = (ArtistModel) data.getSerializableExtra(EditArtistDialog.EXTRA_ARTIST);
            mArtistDataSource.updateRow(rowIndex, model);
            mTableAdapter.notifyRowChanged(rowIndex);
        }
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
        DialogFragment dialog = EditArtistDialog.newInstance(mArtistDataSource.getArtist(row), row);
        dialog.setTargetFragment(this, EDIT_ARTIST_REQUEST_CODE);
        dialog.show(getChildFragmentManager(), EditArtistDialog.class.getSimpleName());
    }

    @Override
    public void onLeftTopHeaderLongClick() {
        Log.e(TAG, "onLeftTopHeaderLongClick ");
    }
}
