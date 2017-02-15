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

public class EditFileNameDialog extends DialogFragment implements View.OnClickListener {
    public static final int REQUEST_CODE_EDIT_FILE_NAME = 673;
    public static final String EXTRA_VALUE_FILE_NAME = "EXTRA_VALUE_FILE_NAME";

    private String mFileName;

    private TextInputEditText etFileName;

    public static EditFileNameDialog newInstance(String fileName) {
        EditFileNameDialog dialog = new EditFileNameDialog();
        Bundle args = new Bundle();
        args.putString(EXTRA_VALUE_FILE_NAME, fileName);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileName = getArguments().getString(EXTRA_VALUE_FILE_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //noinspection ConstantConditions
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_edit_file_name, container, false);

        etFileName = (TextInputEditText) view.findViewById(R.id.etFileName);

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
            window.setLayout(Double.valueOf(width * 0.8D).intValue(), Double.valueOf(height * 0.6D).intValue());
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
                intent.putExtra(EXTRA_VALUE_FILE_NAME, etFileName.getText().toString().trim());
                getParentFragment().onActivityResult(REQUEST_CODE_EDIT_FILE_NAME, Activity.RESULT_OK, intent);
                break;
            case R.id.bNegative:
                getParentFragment().onActivityResult(REQUEST_CODE_EDIT_FILE_NAME, Activity.RESULT_CANCELED, null);
                break;
            default:
                //do nothing
        }

        dismiss();
    }

    private void updateUiAccordingToModel() {
        etFileName.setHint(mFileName);
    }
}
