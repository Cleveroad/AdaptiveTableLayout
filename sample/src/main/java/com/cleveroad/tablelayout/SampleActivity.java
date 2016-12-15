package com.cleveroad.tablelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cleveroad.library.TableLayout;
import com.cleveroad.library.adapter.OnItemClickListener;
import com.cleveroad.library.adapter.OnItemLongClickListener;
import com.cleveroad.tablelayout.adapter.SampleDataTableLayoutAdapter;

//import com.cleveroad.library.TableLayoutOld;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        TableLayout tableLayout = (TableLayout) findViewById(R.id.ttable);
        final SampleDataTableLayoutAdapter adapter = new SampleDataTableLayoutAdapter(this);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int row, int column) {
                adapter.putItem(row, column, "" + System.currentTimeMillis());
                Log.e("SampleActivity", "onItemClick = " + row + " | " + column);
            }

            @Override
            public void onRowHeaderClick(int row) {
                Log.e("SampleActivity", "onRowHeaderClick = " + row);
            }

            @Override
            public void onColumnHeaderClick(int column) {
                Log.e("SampleActivity", "onColumnHeaderClick = " + column);
            }

            @Override
            public void onLeftTopHeaderClick() {
                Log.e("SampleActivity", "onLeftTopHeaderClick ");
            }
        });

        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int row, int column) {
                Log.e("SampleActivity", "onItemLongClick = " + row + " | " + column);
            }

            @Override
            public void onLeftTopHeaderLongClick() {
                Log.e("SampleActivity", "onLeftTopHeaderLongClick ");
            }
        });
        tableLayout.setAdapter(adapter);
    }
}
