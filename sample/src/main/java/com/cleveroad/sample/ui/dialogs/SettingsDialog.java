package com.cleveroad.sample.ui.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;

import com.cleveroad.sample.R;

public class SettingsDialog extends DialogFragment implements View.OnClickListener {
    public static final int REQUEST_CODE_SETTINGS = 673;

    public static final String EXTRA_VALUE_SOLID_HEADER = "EXTRA_VALUE_SOLID_HEADER";
    public static final String EXTRA_VALUE_HEADER_FIXED = "EXTRA_VALUE_HEADER_FIXED";
    public static final String EXTRA_VALUE_RTL_DIRECTION = "EXTRA_VALUE_RTL_DIRECTION";
    public static final String EXTRA_VALUE_ROW_DRAG_AND_DROP_ENABLED = "EXTRA_VALUE_ROW_DRAG_AND_DROP_ENABLED";
    public static final String EXTRA_VALUE_COLUMN_DRAG_AND_DROP_ENABLED = "EXTRA_VALUE_COLUMN_DRAG_AND_DROP_ENABLED";

    /**
     * if true - value of row header fixed to the row. Fixed to the data
     * if false - fixed to the number of row. Fixed to the row' number from 0 to n.
     */
    private boolean mSolidRowHeader;

    private boolean mIsHeaderFixed;

    private boolean mIsRtlDirection;

    private boolean mIsRowDragAndDropEnabled;

    private boolean mIsColumnDragAndDropEnabled;

    private SwitchCompat swSolidRow;

    private SwitchCompat swFixedHeaders;

    private SwitchCompat swRtlDirection;

    private SwitchCompat swRowDragAndDropEnabled;

    private SwitchCompat swColumnDragAndDropEnabled;


    public static SettingsDialog newInstance(boolean isHeaderFixed, boolean solidRowHeader,
                                             boolean isRtlDirection, boolean isRowDragAndDropEnabled,
                                             boolean isColumnDragAndDropEnabled) {
        SettingsDialog dialog = new SettingsDialog();
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_VALUE_HEADER_FIXED, isHeaderFixed);
        args.putBoolean(EXTRA_VALUE_SOLID_HEADER, solidRowHeader);
        args.putBoolean(EXTRA_VALUE_RTL_DIRECTION, isRtlDirection);
        args.putBoolean(EXTRA_VALUE_ROW_DRAG_AND_DROP_ENABLED, isRowDragAndDropEnabled);
        args.putBoolean(EXTRA_VALUE_COLUMN_DRAG_AND_DROP_ENABLED, isColumnDragAndDropEnabled);
        dialog.setArguments(args);
        return dialog;
    }

    public static SettingsDialog newInstance(boolean isHeaderFixed, boolean solidRowHeader,
                                             boolean isRowDragAndDropEnabled, boolean isColumnDragAndDropEnabled) {
        SettingsDialog dialog = new SettingsDialog();
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_VALUE_HEADER_FIXED, isHeaderFixed);
        args.putBoolean(EXTRA_VALUE_SOLID_HEADER, solidRowHeader);
        args.putBoolean(EXTRA_VALUE_ROW_DRAG_AND_DROP_ENABLED, isRowDragAndDropEnabled);
        args.putBoolean(EXTRA_VALUE_COLUMN_DRAG_AND_DROP_ENABLED, isColumnDragAndDropEnabled);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSolidRowHeader = getArguments().getBoolean(EXTRA_VALUE_SOLID_HEADER);
        mIsHeaderFixed = getArguments().getBoolean(EXTRA_VALUE_HEADER_FIXED);
        mIsRtlDirection = getArguments().getBoolean(EXTRA_VALUE_RTL_DIRECTION);
        mIsRowDragAndDropEnabled = getArguments().getBoolean(EXTRA_VALUE_ROW_DRAG_AND_DROP_ENABLED);
        mIsColumnDragAndDropEnabled = getArguments().getBoolean(EXTRA_VALUE_COLUMN_DRAG_AND_DROP_ENABLED);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //noinspection ConstantConditions
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_settings, container, false);
        swSolidRow = (SwitchCompat) view.findViewById(R.id.swSolidRow);
        swFixedHeaders = (SwitchCompat) view.findViewById(R.id.swFixedHeaders);
        swRtlDirection = (SwitchCompat) view.findViewById(R.id.swRtlDirection);
        swRowDragAndDropEnabled = (SwitchCompat) view.findViewById(R.id.swRowDragAndDropEnabled);
        swColumnDragAndDropEnabled = (SwitchCompat) view.findViewById(R.id.swColumnDragAndDropEnabled);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.bPositive).setOnClickListener(this);
        view.findViewById(R.id.bNegative).setOnClickListener(this);

        swFixedHeaders.setChecked(mIsHeaderFixed);
        swSolidRow.setChecked(mSolidRowHeader);
        swRtlDirection.setChecked(mIsRtlDirection);
        swRowDragAndDropEnabled.setChecked(mIsRowDragAndDropEnabled);
        swColumnDragAndDropEnabled.setChecked(mIsColumnDragAndDropEnabled);

        swFixedHeaders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsHeaderFixed = isChecked;
            }
        });
        swSolidRow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSolidRowHeader = isChecked;
            }
        });
        swRtlDirection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mIsRtlDirection = isChecked;
            }
        });
        swRowDragAndDropEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mIsRowDragAndDropEnabled = isChecked;
            }
        });
        swColumnDragAndDropEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mIsColumnDragAndDropEnabled = isChecked;
            }
        });


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
            window.setLayout((int) (width * 0.8), (int) (height * 0.9));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPositive:
                Intent intent = new Intent();
                intent.putExtra(EXTRA_VALUE_SOLID_HEADER, mSolidRowHeader);
                intent.putExtra(EXTRA_VALUE_HEADER_FIXED, mIsHeaderFixed);
                intent.putExtra(EXTRA_VALUE_RTL_DIRECTION, mIsRtlDirection);
                intent.putExtra(EXTRA_VALUE_ROW_DRAG_AND_DROP_ENABLED, mIsRowDragAndDropEnabled);
                intent.putExtra(EXTRA_VALUE_COLUMN_DRAG_AND_DROP_ENABLED, mIsColumnDragAndDropEnabled);
                getParentFragment().onActivityResult(REQUEST_CODE_SETTINGS, Activity.RESULT_OK, intent);
                break;
            case R.id.bNegative:
                getParentFragment().onActivityResult(REQUEST_CODE_SETTINGS, Activity.RESULT_CANCELED, null);
                break;
            default:
                //do nothing
        }

        dismiss();
    }
}
