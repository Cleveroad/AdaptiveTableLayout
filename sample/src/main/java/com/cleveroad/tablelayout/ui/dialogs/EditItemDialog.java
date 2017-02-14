package com.cleveroad.tablelayout.ui.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.cleveroad.tablelayout.R;

public class EditItemDialog extends DialogFragment implements View.OnClickListener {
    public static final int REQUEST_CODE_EDIT_SONG = 663;
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_VALUE = "EXTRA_VALUE";
    public static final String EXTRA_COLUMN_NUMBER = "EXTRA_COLUMN_NUMBER";
    public static final String EXTRA_ROW_NUMBER = "EXTRA_ROW_NUMBER";

    private String mTitle;
    private String mValue;
    private int mColumn;
    private int mRow;

    private TextInputLayout mTilValue;
    private TextInputEditText mEtValue;

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
        mColumn = getArguments().getInt(EXTRA_COLUMN_NUMBER);
        mRow = getArguments().getInt(EXTRA_ROW_NUMBER);
        mTitle = getArguments().getString(EXTRA_TITLE);
        mValue = getArguments().getString(EXTRA_VALUE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //noinspection ConstantConditions
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_edit_item, container, false);

        mTilValue = (TextInputLayout) view.findViewById(R.id.tilValue);
        mEtValue = (TextInputEditText) view.findViewById(R.id.etValue);

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
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int height = dm.heightPixels;
            int width = dm.widthPixels;
            window.setLayout(width, height);
            window.setLayout(Double.valueOf(width * 0.6D).intValue(), Double.valueOf(height * 0.4D).intValue());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUiAccordingToModel();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPositive:
                Intent intent = new Intent();
                intent.putExtra(EXTRA_VALUE, mEtValue.getText().toString().trim());
                intent.putExtra(EXTRA_COLUMN_NUMBER, mColumn);
                intent.putExtra(EXTRA_ROW_NUMBER, mRow);

                getParentFragment().onActivityResult(REQUEST_CODE_EDIT_SONG, Activity.RESULT_OK, intent);
                break;
            case R.id.bNegative:
                getParentFragment().onActivityResult(REQUEST_CODE_EDIT_SONG, Activity.RESULT_CANCELED, null);
                break;
            default:
                //do nothing
        }

        dismiss();
    }

    private void updateUiAccordingToModel() {
        mTilValue.setHint(mTitle);
        mEtValue.setText(mValue);
    }
}
