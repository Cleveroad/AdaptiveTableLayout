package com.cleveroad.tablelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cleveroad.library.TableLayout;
import com.cleveroad.tablelayout.adapter.SampleTableAdapter;

//import com.cleveroad.library.TableLayoutOld;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        TableLayout tableLayout = (TableLayout) findViewById(R.id.ttable);

        tableLayout.setAdapter(new SampleTableAdapter(this));
    }
}
