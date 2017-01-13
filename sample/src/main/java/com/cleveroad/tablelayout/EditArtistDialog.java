package com.cleveroad.tablelayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.cleveroad.tablelayout.model.ArtistModel;

import java.util.ArrayList;
import java.util.List;

public class EditArtistDialog extends DialogFragment implements View.OnClickListener {
    public static final String EXTRA_ARTIST = "EXTRA_ARTIST";
    public static final String EXTRA_ROW_NUMBER = "EXTRA_ROW_NUMBER";

    private EditText mEtName;
    private EditText mEtPhotoUrl;
    private EditText mEtSong;
    private CheckBox mCbRock;
    private CheckBox mCbRandB;
    private CheckBox mCbPop;
    private CheckBox mCbJazz;
    private CheckBox mCbHipHop;
    private CheckBox mCbElectronic;
    private CheckBox mCbBlues;
    private CheckBox mCbCountry;

    private ArtistModel mArtistModel;
    private int mRowNumber;

    public static EditArtistDialog newInstance(@NonNull ArtistModel artist, int rowNumber) {
        EditArtistDialog fragment = new EditArtistDialog();
        fragment.setArguments(createArgs(artist, rowNumber));
        return fragment;
    }

    public static Bundle createArgs(@NonNull ArtistModel artist, int rowNumber) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ARTIST, artist);
        args.putInt(EXTRA_ROW_NUMBER, rowNumber);
        return args;
    }

    private static boolean containsIgnoreCase(List<String> list, String item) {
        for (String s : list) {
            if (item.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtistModel = (ArtistModel) getArguments().getSerializable(EXTRA_ARTIST);
        mRowNumber = getArguments().getInt(EXTRA_ROW_NUMBER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_artist, container, false);

        mEtName = (EditText) view.findViewById(R.id.etName);
        mEtPhotoUrl = (EditText) view.findViewById(R.id.etPhotoUrl);
        mEtSong = (EditText) view.findViewById(R.id.etSong);
        mCbRock = (CheckBox) view.findViewById(R.id.cbRock);
        mCbRandB = (CheckBox) view.findViewById(R.id.cbRandB);
        mCbPop = (CheckBox) view.findViewById(R.id.cbPop);
        mCbJazz = (CheckBox) view.findViewById(R.id.cbJazz);
        mCbHipHop = (CheckBox) view.findViewById(R.id.cbHipHop);
        mCbElectronic = (CheckBox) view.findViewById(R.id.cbElectronic);
        mCbBlues = (CheckBox) view.findViewById(R.id.cbBlues);
        mCbCountry = (CheckBox) view.findViewById(R.id.cbCountry);

        view.findViewById(R.id.bPositive).setOnClickListener(this);
        view.findViewById(R.id.bNegative).setOnClickListener(this);

        updateUiAccordingToModel();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPositive:
                Intent intent = new Intent();
                updateModelAccordingToUi();
                intent.putExtra(EXTRA_ARTIST, mArtistModel);
                intent.putExtra(EXTRA_ROW_NUMBER, mRowNumber);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                break;
            case R.id.bNegative:
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                break;
            default:
                //do nothing
        }

        dismiss();
    }

    private void updateUiAccordingToModel() {
        mEtName.setText(mArtistModel.getName());
        mEtPhotoUrl.setText(mArtistModel.getPhotoUrl());
        mEtSong.setText(mArtistModel.getSong());

        List<String> genres = mArtistModel.getGenres();
        mCbRock.setChecked(containsIgnoreCase(genres, mCbRock.getText().toString()));
        mCbRandB.setChecked(containsIgnoreCase(genres, mCbRandB.getText().toString()));
        mCbPop.setChecked(containsIgnoreCase(genres, mCbPop.getText().toString()));
        mCbJazz.setChecked(containsIgnoreCase(genres, mCbJazz.getText().toString()));
        mCbHipHop.setChecked(containsIgnoreCase(genres, mCbHipHop.getText().toString()));
        mCbElectronic.setChecked(containsIgnoreCase(genres, mCbElectronic.getText().toString()));
        mCbBlues.setChecked(containsIgnoreCase(genres, mCbBlues.getText().toString()));
        mCbCountry.setChecked(containsIgnoreCase(genres, mCbCountry.getText().toString()));
    }

    private void updateModelAccordingToUi() {
        mArtistModel.setName(mEtName.getText().toString());
        mArtistModel.setPhotoUrl(mEtPhotoUrl.getText().toString());
        mArtistModel.setSong(mEtSong.getText().toString());

        List<String> genres = new ArrayList<>();

        if (mCbRock.isChecked()) {
            genres.add(mCbRock.getText().toString());
        }

        if (mCbRandB.isChecked()) {
            genres.add(mCbRandB.getText().toString());
        }

        if (mCbPop.isChecked()) {
            genres.add(mCbPop.getText().toString());
        }

        if (mCbJazz.isChecked()) {
            genres.add(mCbJazz.getText().toString());
        }

        if (mCbHipHop.isChecked()) {
            genres.add(mCbHipHop.getText().toString());
        }

        if (mCbElectronic.isChecked()) {
            genres.add(mCbElectronic.getText().toString());
        }

        if (mCbBlues.isChecked()) {
            genres.add(mCbBlues.getText().toString());
        }

        if (mCbCountry.isChecked()) {
            genres.add(mCbCountry.getText().toString());
        }


        mArtistModel.setGenres(genres);

        getArguments().putSerializable(EXTRA_ARTIST, mArtistModel);
    }
}
