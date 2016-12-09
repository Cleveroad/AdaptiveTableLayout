package com.cleveroad.tablelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cleveroad.library.tlib.TTableLayout;
import com.cleveroad.tablelayout.adapter.TSampleTableAdapter;

//import com.cleveroad.library.TableLayoutOld;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

//        TableLayoutOld tableLayout = (TableLayoutOld) findViewById(R.id.tableOld);
//        TableLayout tableLayout = (TableLayout) findViewById(R.id.table);
        TTableLayout tableLayout = (TTableLayout) findViewById(R.id.ttable);

//        tableLayout.setTableAdapter(new SortingAdapter(this));
//        tableLayout.setTableAdapter(new SortingAdapter(this));
        tableLayout.setAdapter(new TSampleTableAdapter(this));
//        tableLayout.setFixedRowScrollEnabled(true);
    }
}
