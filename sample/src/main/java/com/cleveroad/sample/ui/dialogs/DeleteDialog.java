package com.cleveroad.sample.ui.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cleveroad.sample.R;

import static com.cleveroad.sample.datasource.Constants.EXTRA_COLUMN_NUMBER;
import static com.cleveroad.sample.datasource.Constants.EXTRA_ROW_NUMBER;
import static com.cleveroad.sample.datasource.Constants.REQUEST_CODE_DELETE_COLUMN_CONFIRMED;
import static com.cleveroad.sample.datasource.Constants.REQUEST_CODE_DELETE_ROW_CONFIRMED;

public class DeleteDialog extends DialogFragment implements View.OnClickListener {
    private static final int ZERO_COLUMN_OR_ROW = 0;
    private TextView tvTitle;
    private int mRow;
    private int mColumn;

    public static DeleteDialog newInstance(int row, int column) {
        DeleteDialog fragment = new DeleteDialog();
        Bundle args = new Bundle();
        args.putInt(EXTRA_ROW_NUMBER, row);
        args.putInt(EXTRA_COLUMN_NUMBER, column);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
                mRow = args.getInt(EXTRA_ROW_NUMBER);
                mColumn = args.getInt(EXTRA_COLUMN_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //noinspection ConstantConditions
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_delete, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.bPositive).setOnClickListener(this);
        view.findViewById(R.id.bNegative).setOnClickListener(this);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        updateUi();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPositive:
                // positive
                delete();
                break;
            case R.id.bNegative:
                // negative
                Fragment fragment = getParentFragment();
                if (fragment != null) {
                    fragment.onActivityResult(REQUEST_CODE_DELETE_ROW_CONFIRMED, Activity.RESULT_CANCELED, null);
                }
                break;
            default:
                //do nothing
        }

        dismiss();
    }

    private void updateUi() {

        if (mColumn == ZERO_COLUMN_OR_ROW) {
            tvTitle.setText(getString(R.string.delete_row));
        } else if (mRow == ZERO_COLUMN_OR_ROW) {
            tvTitle.setText(getString(R.string.delete_column));
        }
    }

    private void delete(){
        if (mColumn == ZERO_COLUMN_OR_ROW) {
            sendResult(EXTRA_ROW_NUMBER, mRow, REQUEST_CODE_DELETE_ROW_CONFIRMED);
        } else if (mRow == ZERO_COLUMN_OR_ROW) {
            sendResult(EXTRA_COLUMN_NUMBER, mColumn, REQUEST_CODE_DELETE_COLUMN_CONFIRMED);
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