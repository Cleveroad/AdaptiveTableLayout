package com.cleveroad.sample.ui.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;

import com.cleveroad.sample.R;

import java.util.Objects;

import static com.cleveroad.sample.datasource.Constants.REQUEST_CODE_SETTINGS;

public class SettingsDialog extends DialogFragment implements View.OnClickListener {

    public static final String EXTRA_VALUE_SOLID_HEADER = "EXTRA_VALUE_SOLID_HEADER";
    public static final String EXTRA_VALUE_HEADER_FIXED = "EXTRA_VALUE_HEADER_FIXED";
    public static final String EXTRA_VALUE_RTL_DIRECTION = "EXTRA_VALUE_RTL_DIRECTION";
    public static final String EXTRA_VALUE_DRAG_AND_DROP_ENABLED = "EXTRA_VALUE_DRAG_AND_DROP_ENABLED";

    /**
     * if true - value of row header fixed to the row. Fixed to the data
     * if false - fixed to the number of row. Fixed to the row' number from 0 to n.
     */
    private boolean mSolidRowHeader;

    private boolean mIsHeaderFixed;

    private boolean mIsRtlDirection;

    private boolean mIsDragAndDropEnabled;

    private SwitchCompat swSolidRow;

    private SwitchCompat swFixedHeaders;

    private SwitchCompat swRtlDirection;

    private SwitchCompat swDragAndDropEnabled;


    public static SettingsDialog newInstance(boolean isHeaderFixed, boolean solidRowHeader,
                                             boolean isRtlDirection, boolean isDragAndDropEnabled) {
        SettingsDialog dialog = new SettingsDialog();
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_VALUE_HEADER_FIXED, isHeaderFixed);
        args.putBoolean(EXTRA_VALUE_SOLID_HEADER, solidRowHeader);
        args.putBoolean(EXTRA_VALUE_RTL_DIRECTION, isRtlDirection);
        args.putBoolean(EXTRA_VALUE_DRAG_AND_DROP_ENABLED, isDragAndDropEnabled);
        dialog.setArguments(args);
        return dialog;
    }

    public static SettingsDialog newInstance(boolean isHeaderFixed, boolean solidRowHeader, boolean isDragAndDropEnabled) {
        SettingsDialog dialog = new SettingsDialog();
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_VALUE_HEADER_FIXED, isHeaderFixed);
        args.putBoolean(EXTRA_VALUE_SOLID_HEADER, solidRowHeader);
        args.putBoolean(EXTRA_VALUE_DRAG_AND_DROP_ENABLED, isDragAndDropEnabled);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mSolidRowHeader = args.getBoolean(EXTRA_VALUE_SOLID_HEADER);
            mIsHeaderFixed = args.getBoolean(EXTRA_VALUE_HEADER_FIXED);
            mIsRtlDirection = args.getBoolean(EXTRA_VALUE_RTL_DIRECTION);
            mIsDragAndDropEnabled = args.getBoolean(EXTRA_VALUE_DRAG_AND_DROP_ENABLED);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //noinspection ConstantConditions
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_settings, container, false);
        swSolidRow = view.findViewById(R.id.swSolidRow);
        swFixedHeaders = view.findViewById(R.id.swFixedHeaders);
        swRtlDirection = view.findViewById(R.id.swRtlDirection);
        swDragAndDropEnabled = view.findViewById(R.id.swDragAndDropEnabled);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.bPositive).setOnClickListener(this);
        view.findViewById(R.id.bNegative).setOnClickListener(this);

        swFixedHeaders.setChecked(mIsHeaderFixed);
        swSolidRow.setChecked(mSolidRowHeader);
        swRtlDirection.setChecked(mIsRtlDirection);
        swDragAndDropEnabled.setChecked(mIsDragAndDropEnabled);

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
        swDragAndDropEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mIsDragAndDropEnabled = isChecked;
            }
        });

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPositive:
                sendResult();
                break;
            case R.id.bNegative:
                Fragment fragment = getParentFragment();
                if (fragment != null) {
                    fragment.onActivityResult(REQUEST_CODE_SETTINGS, Activity.RESULT_CANCELED, null);
                }
                break;
            default:
                //do nothing
        }
        dismiss();
    }

    private void sendResult() {
        Fragment fragment = getParentFragment();
        if (fragment != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_VALUE_SOLID_HEADER, mSolidRowHeader);
            intent.putExtra(EXTRA_VALUE_HEADER_FIXED, mIsHeaderFixed);
            intent.putExtra(EXTRA_VALUE_RTL_DIRECTION, mIsRtlDirection);
            intent.putExtra(EXTRA_VALUE_DRAG_AND_DROP_ENABLED, mIsDragAndDropEnabled);
            fragment.onActivityResult(REQUEST_CODE_SETTINGS, Activity.RESULT_OK, intent);
        }
    }
}
