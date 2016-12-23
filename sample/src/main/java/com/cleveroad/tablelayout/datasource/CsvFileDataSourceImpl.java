package com.cleveroad.tablelayout.datasource;

import android.util.Log;

import java.io.Closeable;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.WeakHashMap;

public abstract class CsvFileDataSourceImpl implements TableDataSource<String, String, String, String> {
    private static final String TAG = CsvFileDataSourceImpl.class.getSimpleName();
    private static final String CSV_DELIMITER = ",";
    private static final int READ_FILE_LINES_LIMIT = 50;
    private List<String> mColumnHeaders = new ArrayList<>();
    private Map<Integer, List<String>> mItemsCache = new WeakHashMap<>();
    private int mRowsCount;

    public CsvFileDataSourceImpl() {
        init();
    }

    private static void closeWithoutException(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public int getRowsCount() {
        return mRowsCount;
    }

    @Override
    public int getColumnsCount() {
        return mColumnHeaders.size() - 1;
    }

    @Override
    public String getFirstHeaderData() {
        return mColumnHeaders.get(0);
    }

    @Override
    public String getRowHeaderData(int index) {
        return getRow(index).get(0 /*column header*/);
    }

    @Override
    public String getColumnHeaderData(int index) {
        return mColumnHeaders.get(index + 1 /*first header*/);
    }

    @Override
    public String getItemData(int rowIndex, int columnIndex) {
        try {
            return getRow(rowIndex).get(columnIndex + 1 /*column header*/);
        } catch (Exception e) {
            Log.e(TAG, "get rowIndex=" + rowIndex + "; colIndex=" + columnIndex + ";\ncache = " + mItemsCache.toString(), e);
            return null;
        }
    }

    protected abstract InputStreamReader getInputStreamReader() throws Exception;

    public void destroy() {
        //close streams
    }

    private void init() {
        mRowsCount = calculateLinesCount() - 1 /*row header*/;
        mColumnHeaders.addAll(readColumnHeaders());
    }

    private int calculateLinesCount() {
        //rows count
        InputStreamReader fileReader = null;
        LineNumberReader lineNumberReader = null;
        try {
            fileReader = getInputStreamReader();
            lineNumberReader = new LineNumberReader(fileReader);
            lineNumberReader.skip(Long.MAX_VALUE);
            return lineNumberReader.getLineNumber() + 1; //Add 1 because line index starts at 0
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            try {
                if (lineNumberReader != null) {
                    lineNumberReader.close();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return 0;
    }

    private List<String> readColumnHeaders() {

        InputStreamReader fileReader = null;

        try {
            fileReader = getInputStreamReader();
            Scanner scanner = new Scanner(fileReader);
            return new ArrayList<>(Arrays.asList(scanner.nextLine().split(CSV_DELIMITER)));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            closeWithoutException(fileReader);
        }

        return new ArrayList<>();
    }

    private List<String> getRow(int rowIndex) {
        List<String> result = mItemsCache.get(rowIndex);
        if (result != null) {
            return result;
        }

        //read from file
        final int rowIndexInFile = rowIndex + 1 /*skip header*/;

        InputStreamReader fileReader = null;

        try {
            Log.i(TAG, "Load row #" + rowIndex);
            fileReader = getInputStreamReader();
            Scanner scanner = new Scanner(fileReader);
            //skip rowIndexInFile lines
            for (int i = 0; i < rowIndexInFile; i++) {
                scanner.nextLine();
            }

            //on scroll to bottom
            for (int i = rowIndexInFile; i < getRowsCount() + 1 && i < rowIndexInFile + READ_FILE_LINES_LIMIT; i++) {
                if (i - 1 == rowIndex) {
                    result = new ArrayList<>(Arrays.asList(scanner.nextLine().split(CSV_DELIMITER)));
                    mItemsCache.put(i - 1, result);
                } else {
                    mItemsCache.put(i - 1, new ArrayList<>(Arrays.asList(scanner.nextLine().split(CSV_DELIMITER))));
                }

                if (mItemsCache.containsKey(i)) {
                    Log.i(TAG, "scroll to bottom -> contains #" + i + "; break");
                    break;
                }
            }

            //on scroll to top
            for (int i = rowIndexInFile - 1; i > 1/*rows header*/ && i > rowIndexInFile - READ_FILE_LINES_LIMIT; i--) {
                mItemsCache.put(i - 1, new ArrayList<>(Arrays.asList(scanner.nextLine().split(CSV_DELIMITER))));
                if (mItemsCache.containsKey(i - 2)) {
                    Log.i(TAG, "scroll to top -> contains #" + (i - 2) + "; break");
                    break;
                }
            }


        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            closeWithoutException(fileReader);
        }

        return result;
    }
}
