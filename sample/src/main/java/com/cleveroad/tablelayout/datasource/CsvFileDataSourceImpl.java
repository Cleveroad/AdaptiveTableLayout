package com.cleveroad.tablelayout.datasource;

import android.net.Uri;
import android.util.Log;

import com.cleveroad.tablelayout.utils.CsvUtils;
import com.cleveroad.tablelayout.utils.StringUtils;

import java.io.Closeable;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.WeakHashMap;

public class CsvFileDataSourceImpl implements TableDataSource<String, String, String, String> {
    private static final String TAG = CsvFileDataSourceImpl.class.getSimpleName();
    private static final int READ_FILE_LINES_LIMIT = 50;
    private final Uri mCsvFileUri;
    private List<String> mColumnHeaders = new ArrayList<>();
    private Map<Integer, List<String>> mItemsCache = new WeakHashMap<>();
    private Map<Integer, List<String>> mChangedItems = new HashMap<>();
    private int mRowsCount;

    public CsvFileDataSourceImpl(Uri csvFileUri) {
        mCsvFileUri = csvFileUri;
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

    private static List<String> modifyListPositions(List<String> inputList, Map<Integer, Integer> modifications) {
        List<String> result = new ArrayList<>(inputList.size());
        for (int i = 0, size = inputList.size(); i < size; i++) {
            Integer newPosition = null;
            if (i != 0) {
                //do not move fixed first column
                newPosition = modifications.get(i - 1); //TODO: simplify logic
                if (newPosition != null) {
                    newPosition++;
                }
            }

            String value = inputList.get(newPosition != null ? newPosition : i);
            if (value.contains(",")) {
                value = "\"" + value + "\"";
            }
            result.add(value);
        }

        return result;
    }

    @Override
    public int getRowsCount() {
        return mRowsCount == 0 ? 0 : (mRowsCount + 1);
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

    public List<String> getColumnHeaders() {
        return new ArrayList<>(mColumnHeaders);
    }

    public List<String> getRowValues(int rowIndex) {
        return getRow(rowIndex);
    }

    public void updateRow(int rowIndex, List<String> rowItems) {
        mChangedItems.put(rowIndex, rowItems);
    }

    //TODO: to worker thread
    //TODO: show error messages
    public void applyChanges(Map<Integer, Integer> rowModifications, Map<Integer, Integer> columnModifications) {
        OutputStreamWriter writer = null;

        try {
            writer = new FileWriter(mCsvFileUri.getEncodedPath() + "_new.csv");
            writer.write(StringUtils.toString(modifyListPositions(getColumnHeaders(), columnModifications), ","));
            writer.write("\n");
            for (int i = 0, size = getRowsCount(); i < size; i++) {
                Integer newRowPosition = rowModifications.get(i);
                List<String> row = getRow(newRowPosition != null ? newRowPosition : i);
                writer.write(StringUtils.toString(modifyListPositions(row, columnModifications), ","));
                if(i != size - 1) {
                    writer.write("\n");
                }
            }
            //TODO: delete old file
            //TODO rename new file "XXX_new.csv" to "XXX"
            //TODO: refresh CsvFileDataSourceImpl - remove all cache & reload data
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            closeWithoutException(writer);
        }
    }

    protected InputStreamReader getInputStreamReader() throws IOException {
        return new FileReader(mCsvFileUri.getEncodedPath());
    }

    public void destroy() {
//        mItemsCache.clear();
//        mColumnHeaders.clear();
//        mChangedItems.clear();
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
            return lineNumberReader.getLineNumber();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            closeWithoutException(fileReader);
            closeWithoutException(lineNumberReader);
        }
        return 0;
    }

    private List<String> readColumnHeaders() {

        InputStreamReader fileReader = null;

        try {
            fileReader = getInputStreamReader();
            Scanner scanner = new Scanner(fileReader);
            return new ArrayList<>(CsvUtils.parseLine(scanner.nextLine()));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            closeWithoutException(fileReader);
        }

        return new ArrayList<>();
    }

    private List<String> getRow(int rowIndex) {
        List<String> result = mChangedItems.containsKey(rowIndex)
                ? mChangedItems.get(rowIndex) : mItemsCache.get(rowIndex);
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
                    result = new ArrayList<>(CsvUtils.parseLine(scanner.nextLine()));
                    mItemsCache.put(i - 1, result);
                } else {
                    mItemsCache.put(i - 1, new ArrayList<>(CsvUtils.parseLine(scanner.nextLine())));
                }

                if (mItemsCache.containsKey(i)) {
                    Log.i(TAG, "scroll to bottom -> contains #" + i + "; break");
                    break;
                }
            }

            //on scroll to top
            for (int i = rowIndexInFile - 1; i > 1/*rows header*/ && i > rowIndexInFile - READ_FILE_LINES_LIMIT; i--) {
                mItemsCache.put(i - 1, new ArrayList<>(CsvUtils.parseLine(scanner.nextLine())));
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
