package com.cleveroad.tablelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cleveroad.library.TableLayout;
import com.cleveroad.tablelayout.adapter.SortingAdapter;

//import com.cleveroad.library.TableLayoutOld;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

//        TableLayoutOld tableLayout = (TableLayoutOld) findViewById(R.id.tableOld);
        TableLayout tableLayout = (TableLayout) findViewById(R.id.table);

//        tableLayout.setTableAdapter(new SortingAdapter(this));
        tableLayout.setTableAdapter(new SortingAdapter(this));
//        tableLayout.setFixedRowScrollEnabled(true);
    }
}
