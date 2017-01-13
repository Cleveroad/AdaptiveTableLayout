package com.cleveroad.tablelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SampleActivity extends AppCompatActivity implements
        CsvPickerFragment.OnCsvFileSelectedListener {

    private static final String ASSETS_FIFA_PLAYERS_FILE = "fifa100.csv";
    private static final String ASSETS_ARTISTS_FILE = "artists.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, CsvPickerFragment.newInstance(), CsvPickerFragment.class.getSimpleName())
//                    .replace(R.id.container,
//                            TableLayoutFragment.newInstance(ASSETS_FIFA_PLAYERS_FILE),
//                            CsvPickerFragment.class.getSimpleName())
                    .add(R.id.container, CsvPickerFragment.newInstance(), CsvPickerFragment.class.getSimpleName())
//                    .replace(R.id.container,
//                            TableLayoutFragment.newInstance(ASSETS_ARTISTS_FILE),
//                            CsvPickerFragment.class.getSimpleName())
                    .commit();
        }
    }


    @Override
    public void onCsvFileSelected(String file) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, TableLayoutFragment.newInstance(file), CsvPickerFragment.class.getSimpleName())
                .commit();
    }
}
