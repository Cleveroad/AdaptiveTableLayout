package com.cleveroad.sample.ui.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.sample.R;

import static com.cleveroad.sample.datasource.Constants.EXTRA_BEFORE_OR_AFTER;
import static com.cleveroad.sample.datasource.Constants.EXTRA_COLUMN_NUMBER;
import static com.cleveroad.sample.datasource.Constants.REQUEST_CODE_ADD_COLUMN_CONFIRMED;

public class AddColumnDialog extends DialogFragment implements View.OnClickListener {
    private int mColumn;

    public static AddColumnDialog newInstance(int column) {
        AddColumnDialog fragment = new AddColumnDialog();
        Bundle args = new Bundle();
        args.putInt(EXTRA_COLUMN_NUMBER, column);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mColumn = args.getInt(EXTRA_COLUMN_NUMBER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //noinspection ConstantConditions
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_add_column, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.bColumnRight).setOnClickListener(this);
        view.findViewById(R.id.bColumnLeft).setOnClickListener(this);
        view.findViewById(R.id.bNegative).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bColumnRight:
                sendResult(mColumn, false);
                break;
            case R.id.bColumnLeft:
                sendResult(mColumn, true);
                break;
            case R.id.bNegative:
                // negative
                Fragment fragment = getParentFragment();
                if (fragment != null) {
                    fragment.onActivityResult(REQUEST_CODE_ADD_COLUMN_CONFIRMED, Activity.RESULT_CANCELED, null);
                }
                break;
            default:
                //do nothing
        }
        dismiss();
    }

    private void sendResult(int column, boolean beforeORAfter) {
        Fragment fragment = getParentFragment();
        if (fragment != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_COLUMN_NUMBER, column);
            intent.putExtra(EXTRA_BEFORE_OR_AFTER, beforeORAfter);
            fragment.onActivityResult(REQUEST_CODE_ADD_COLUMN_CONFIRMED, Activity.RESULT_OK, intent);
        }
    }
}