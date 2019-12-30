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

import com.cleveroad.sample.R;

import static com.cleveroad.sample.datasource.Constants.EXTRA_BEFORE_OR_AFTER;
import static com.cleveroad.sample.datasource.Constants.EXTRA_ROW_NUMBER;
import static com.cleveroad.sample.datasource.Constants.REQUEST_CODE_ADD_ROW_CONFIRMED;

public class AddRowDialog extends DialogFragment implements View.OnClickListener {

    private int mRow;

    public static AddRowDialog newInstance(int row) {
        AddRowDialog fragment = new AddRowDialog();
        Bundle args = new Bundle();
        args.putInt(EXTRA_ROW_NUMBER, row);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mRow = args.getInt(EXTRA_ROW_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //noinspection ConstantConditions
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_add_row, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.bAbove).setOnClickListener(this);
        view.findViewById(R.id.bBelow).setOnClickListener(this);
        view.findViewById(R.id.bNegative).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bAbove:
                sendResult(mRow, true);
                break;
            case R.id.bBelow:
                sendResult(mRow, false);
                break;
            case R.id.bNegative:
                // negative
                Fragment fragment = getParentFragment();
                if (fragment != null) {
                    fragment.onActivityResult(REQUEST_CODE_ADD_ROW_CONFIRMED, Activity.RESULT_CANCELED, null);
                }
                break;
            default:
                //do nothing
        }

        dismiss();
    }

    private void sendResult(int row, boolean beforeORAfter) {
        Fragment fragment = getParentFragment();
        if (fragment != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_ROW_NUMBER, row);
            intent.putExtra(EXTRA_BEFORE_OR_AFTER, beforeORAfter);
            fragment.onActivityResult(REQUEST_CODE_ADD_ROW_CONFIRMED, Activity.RESULT_OK, intent);
        }
    }
}