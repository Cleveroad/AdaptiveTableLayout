package com.cleveroad.tablelayout.datasource;

import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class CsvFileDataSourceImpl implements TableDataSource<String, String, String, String> {
    private static final String TAG = CsvFileDataSourceImpl.class.getSimpleName();
    private final File mCsvFile;
    private List<String> mColumnHeaders = new ArrayList<>();
    private Map<Integer, String> mRowHeadersCahce = new WeakHashMap<>();
    private Map<Integer, List<String>> mItemsCache = new WeakHashMap<>();
    private int mRowsCount;

    public CsvFileDataSourceImpl(File csvFile) {
        mCsvFile = csvFile;
        init();
    }

    @Override
    public int getRowsCount() {
        return mRowsCount;
    }

    @Override
    public int getColumnsCount() {
        return mColumnHeaders.size();
    }

    @Override
    public String getFirstHeaderData() {
        return mCsvFile.getName();
    }

    @Override
    public String getRowHeaderData(int index) {
        return "" + index;
    }

    @Override
    public String getColumnHeaderData(int index) {
        return "" + index;
    }

    @Override
    public String getItemData(int rowIndex, int columnIndex) {
        return "[" + rowIndex + ";" + columnIndex + "]";
    }

    public void destroy() {
        //close streams
    }

    private void init() {
        //rows count
        FileReader fileReader = null;
        LineNumberReader lineNumberReader = null;
        try {
            fileReader = new FileReader(mCsvFile);
            lineNumberReader = new LineNumberReader(fileReader);
            lineNumberReader.skip(Long.MAX_VALUE);
            mRowsCount = lineNumberReader.getLineNumber() + 1; //Add 1 because line index starts at 0
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                if(fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            try {
                if(lineNumberReader != null) {
                    lineNumberReader.close();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
