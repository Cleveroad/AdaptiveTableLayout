package com.cleveroad.tablelayout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.tablelayout.utils.PermissionHelper;

public class CsvPickerFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_PICK_CSV = 2;

    public static CsvPickerFragment newInstance() {
        Bundle args = new Bundle();
        CsvPickerFragment fragment = new CsvPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_csv_picker, container, false);
        view.findViewById(R.id.bPickFile).setOnClickListener(this);
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickCsvFile();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CSV && data != null) {
            Activity activity = getActivity();
            if (activity instanceof OnCsvFileSelectedListener) {
                Log.e("Result", data.getData().toString());
                Log.e("Result", UriHelper.getPath(getContext(), data.getData()));
                ((OnCsvFileSelectedListener) activity).onCsvFileSelected(
                        UriHelper.getPath(getContext(), data.getData()));
            }
        }
    }

//    public String getRealPathFromURI(Context context, Uri contentUri) {
//        Cursor cursor = null;
//        try {
//            String[] proj = {MediaStore.Images.Media.DATA};
//            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }

    @Override
    public void onClick(View v) {
        if (PermissionHelper.checkOrRequest(
                this,
                REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            pickCsvFile();
        }
    }

    private void pickCsvFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/comma-separated-values");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.pick_file)), REQUEST_CODE_PICK_CSV);
    }

    interface OnCsvFileSelectedListener {
        void onCsvFileSelected(String file);
    }
}
