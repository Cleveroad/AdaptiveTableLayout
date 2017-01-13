package com.cleveroad.tablelayout.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cleveroad.tablelayout.R;

import java.util.ArrayList;
import java.util.List;

public class EditRowDialog extends DialogFragment implements View.OnClickListener {
    public static final int REQUEST_CODE_EDIT_SONG = 777;
    public static final String EXTRA_TITLES = "EXTRA_TITLES";
    public static final String EXTRA_VALUES = "EXTRA_VALUES";
    public static final String EXTRA_ROW_NUMBER = "EXTRA_ROW_NUMBER";

    private ArrayList<String> mTitles;
    private ArrayList<String> mValues;
    private int mRowNumber;

    private ViewGroup mColumnsContainer;

    public static EditRowDialog newInstance(
            @NonNull List<String> titles,
            @NonNull List<String> values,
            int rowNumber) {
        EditRowDialog dialog = new EditRowDialog();
        Bundle args = new Bundle();
        args.putInt(EXTRA_ROW_NUMBER, rowNumber);
        args.putStringArrayList(EXTRA_TITLES, new ArrayList<>(titles));
        args.putStringArrayList(EXTRA_VALUES, new ArrayList<>(values));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRowNumber = getArguments().getInt(EXTRA_ROW_NUMBER);
        mTitles = getArguments().getStringArrayList(EXTRA_TITLES);
        mValues = getArguments().getStringArrayList(EXTRA_VALUES);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        //noinspection ConstantConditions
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_edit_row, container, false);

        mColumnsContainer = (ViewGroup) view.findViewById(R.id.llColumnsContainer);

        view.findViewById(R.id.bPositive).setOnClickListener(this);
        view.findViewById(R.id.bNegative).setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUiAccordingToModel();
    }

        @Override
    public void onSaveInstanceState(Bundle outState) {
        updateModelAccordingToUi();
        mColumnsContainer.removeAllViews();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPositive:
                Intent intent = new Intent();
                updateModelAccordingToUi();
                intent.putStringArrayListExtra(EXTRA_VALUES, mValues);
                intent.putExtra(EXTRA_ROW_NUMBER, mRowNumber);
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
        mColumnsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0, size = mTitles.size(); i < size; i++) {
            View view = inflater.inflate(R.layout.item_edit_column, mColumnsContainer, false);
            mColumnsContainer.addView(view);

            TextInputLayout inputLayout = ((TextInputLayout) view.findViewById(R.id.textInputLayout));
            inputLayout.setHint(mTitles.get(i));

            EditText editText = (EditText) view.findViewById(R.id.etColumnValue);
            editText.setText(mValues.get(i));
        }
    }


    private void updateModelAccordingToUi() {
        mValues.clear();
        for (int i = 0, size = mColumnsContainer.getChildCount(); i < size; i++) {
            View view = mColumnsContainer.getChildAt(i);
            mValues.add(((TextInputEditText) view.findViewById(R.id.etColumnValue)).getText().toString());
        }

        getArguments().putStringArrayList(EXTRA_VALUES, mValues);
    }
}
