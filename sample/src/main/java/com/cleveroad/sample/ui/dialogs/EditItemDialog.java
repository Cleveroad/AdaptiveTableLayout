package com.cleveroad.sample.ui.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.cleveroad.sample.R;

import java.util.Objects;

import static com.cleveroad.sample.datasource.Constants.EXTRA_COLUMN_NUMBER;
import static com.cleveroad.sample.datasource.Constants.EXTRA_ROW_NUMBER;
import static com.cleveroad.sample.datasource.Constants.EXTRA_TITLE;
import static com.cleveroad.sample.datasource.Constants.EXTRA_VALUE;
import static com.cleveroad.sample.datasource.Constants.REQUEST_CODE_ADD_COLUMN;
import static com.cleveroad.sample.datasource.Constants.REQUEST_CODE_ADD_ROW;
import static com.cleveroad.sample.datasource.Constants.REQUEST_CODE_DELETE_COLUMN;
import static com.cleveroad.sample.datasource.Constants.REQUEST_CODE_DELETE_ROW;
import static com.cleveroad.sample.datasource.Constants.REQUEST_CODE_EDIT_SONG;

public class EditItemDialog extends DialogFragment implements View.OnClickListener {
    private static final int ZERO_COLUMN_OR_ROW = 0;
    private String mTitle;
    private String mValue;
    private int mColumn;
    private int mRow;

    private TextInputLayout mTilValue;
    private TextInputEditText mEtValue;
    private TextView mTvDelete;
    private TextView mTvAdd;

    public static EditItemDialog newInstance(int row, int column, String title, String value) {
        EditItemDialog dialog = new EditItemDialog();
        Bundle args = new Bundle();
        args.putInt(EXTRA_COLUMN_NUMBER, column);
        args.putInt(EXTRA_ROW_NUMBER, row);
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_VALUE, value);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mColumn = args.getInt(EXTRA_COLUMN_NUMBER);
            mRow = args.getInt(EXTRA_ROW_NUMBER);
            mTitle = args.getString(EXTRA_TITLE);
            mValue = args.getString(EXTRA_VALUE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //noinspection ConstantConditions
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_edit_item, container, false);

        mTilValue = view.findViewById(R.id.tilValue);
        mEtValue = view.findViewById(R.id.etValue);
        mTvDelete = view.findViewById(R.id.tvDelete);
        mTvAdd = view.findViewById(R.id.tvAdd);

        mTvDelete.setOnClickListener(this);
        mTvAdd.setOnClickListener(this);

        view.findViewById(R.id.bPositive).setOnClickListener(this);
        view.findViewById(R.id.bNegative).setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if (window != null) {
            DisplayMetrics dm = new DisplayMetrics();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(dm);
            }
            int height = dm.heightPixels;
            int width = dm.widthPixels;
            window.setLayout(width, height);
            window.setLayout((int) (width * 0.8), (int) (height * 0.9));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUiAccordingToModel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPositive:
                sendResult();
                break;
            case R.id.bNegative:
                Fragment fragment = getParentFragment();
                if (fragment != null) {
                    fragment.onActivityResult(REQUEST_CODE_EDIT_SONG, Activity.RESULT_CANCELED, null);
                }
                break;
            case R.id.tvDelete:
                delete();
                break;
            case R.id.tvAdd:
                add();
                break;
            default:
                //do nothing
        }

        dismiss();
    }

    private void updateUiAccordingToModel() {
        mTilValue.setHint(mTitle);
        mEtValue.setText(mValue);

        if (mColumn == ZERO_COLUMN_OR_ROW) {
            mTvDelete.setText(getString(R.string.delete_row));
            mTvAdd.setText(getString(R.string.add_row));
        } else if (mRow == ZERO_COLUMN_OR_ROW) {
            mTvDelete.setText(getString(R.string.delete_column));
            mTvAdd.setText(getString(R.string.add_column));
        } else {
            mTvDelete.setVisibility(View.GONE);
            mTvAdd.setVisibility(View.GONE);
        }
    }

    private void delete() {
        if (mColumn == ZERO_COLUMN_OR_ROW) {
            sendResult(EXTRA_ROW_NUMBER, mRow, REQUEST_CODE_DELETE_ROW);
        } else if (mRow == ZERO_COLUMN_OR_ROW) {
            sendResult(EXTRA_COLUMN_NUMBER, mColumn, REQUEST_CODE_DELETE_COLUMN);
        }
    }

    private void add() {
        if (mColumn == ZERO_COLUMN_OR_ROW) {
            sendResult(EXTRA_ROW_NUMBER, mRow, REQUEST_CODE_ADD_ROW);
        } else if (mRow == ZERO_COLUMN_OR_ROW) {
            sendResult(EXTRA_COLUMN_NUMBER, mColumn, REQUEST_CODE_ADD_COLUMN);
        }
    }

    private void sendResult() {
        Fragment fragment = getParentFragment();
        if (fragment != null) {
            Intent intent = new Intent();
            String str = mEtValue.getText().toString().trim();
            intent.putExtra(EXTRA_VALUE, str.isEmpty() ? " " : str);
            intent.putExtra(EXTRA_COLUMN_NUMBER, mColumn);
            intent.putExtra(EXTRA_ROW_NUMBER, mRow);
            fragment.onActivityResult(REQUEST_CODE_EDIT_SONG, Activity.RESULT_OK, intent);
        }
    }

    private void sendResult(String extra, int data, int requestCode) {
        Fragment fragment = getParentFragment();
        if (fragment != null) {
            Intent intent = new Intent();
            intent.putExtra(extra, data);
            fragment.onActivityResult(requestCode, Activity.RESULT_OK, intent);
        }
    }
}
