package com.cleveroad.tablelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class SampleActivity
        extends AppCompatActivity
        implements CsvPickerFragment.OnCsvFileSelectedListener {

    private static final String ASSETS_FIFA_PLAYERS_FILE = "fifa100.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
//                    .add(R.id.container, CsvPickerFragment.newInstance(), CsvPickerFragment.class.getSimpleName())
                    .replace(R.id.container,
                            TableLayoutFragment.newInstance(ASSETS_FIFA_PLAYERS_FILE),
                            CsvPickerFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public void onCsvFileSelected(File file) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, TableLayoutFragment.newInstance(file), CsvPickerFragment.class.getSimpleName())
                .commit();
    }
}
