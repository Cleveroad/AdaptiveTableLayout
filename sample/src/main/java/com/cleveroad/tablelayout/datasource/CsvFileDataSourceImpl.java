package com.cleveroad.tablelayout.datasource;

import com.cleveroad.tablelayout.utils.ClosableUtil;
import com.cleveroad.tablelayout.utils.CsvUtils;
import com.cleveroad.tablelayout.utils.StringUtils;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
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
    private final List<String> mColumnHeaders = new ArrayList<>();
    private final Map<Integer, List<String>> mItemsCache = new WeakHashMap<>();
    @SuppressLint("UseSparseArrays")
    private final Map<Integer, List<String>> mChangedItems = new HashMap<>();
    private int mRowsCount;

    public CsvFileDataSourceImpl(Uri csvFileUri) {
        mCsvFileUri = csvFileUri;
        init();
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

    public Uri getCsvFileUri() {
        return mCsvFileUri;
    }

    public void updateRow(int rowIndex, List<String> rowItems) {
        mChangedItems.put(rowIndex, rowItems);
    }

    //TODO: to worker thread
    public boolean applyChanges(
            Map<Integer, Integer> rowModifications,
            Map<Integer, Integer> columnModifications) {
//        OutputStreamWriter writer = null;
//        final String oldFileName = mCsvFileUri.getEncodedPath();
//        final String newFileName = oldFileName + "_new.csv";
//
//        try {
//            writer = new FileWriter(newFileName);
//            writer.write(StringUtils.toString(modifyListPositions(getColumnHeaders(),
//                    columnModifications), ","));
//            writer.write("\n");
//            for (int i = 0, size = getRowsCount(); i < size; i++) {
//                Integer newRowPosition = rowModifications.get(i);
//                List<String> row = getRow(newRowPosition != null ? newRowPosition : i);
//                writer.write(StringUtils.toString(modifyListPositions(row, columnModifications),
//                        ","));
//                if (i != size - 1) {
//                    writer.write("\n");
//                }
//            }
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
//            return false;
//        } finally {
//            ClosableUtil.closeWithoutException(writer);
//        }
//
//        try {
//            File oldFile = new File(oldFileName);
//            File newFile = new File(newFileName);
//
//            //delete old file and rename new file
//            boolean result = oldFile.exists()
//                    && oldFile.delete()
//                    && newFile.renameTo(new File(oldFileName));
//            //invalidate cache
//            if (result) {
//                destroy();
//                init();
//            }
//            return result;
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
//        }
//
        return false;
    }

    protected InputStreamReader getInputStreamReader() throws IOException {
        return new FileReader(mCsvFileUri.getEncodedPath());
    }

    public void destroy() {
        mItemsCache.clear();
        mColumnHeaders.clear();
        mChangedItems.clear();
    }

    void init() {
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
            ClosableUtil.closeWithoutException(fileReader);
            ClosableUtil.closeWithoutException(lineNumberReader);
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
            ClosableUtil.closeWithoutException(fileReader);
        }

        return new ArrayList<>();
    }

    List<String> getRow(int rowIndex) {
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
            ClosableUtil.closeWithoutException(fileReader);
        }

        return result;
    }
}
