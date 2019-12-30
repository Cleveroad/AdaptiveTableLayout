package com.cleveroad.sample.datasource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.cleveroad.sample.utils.ClosableUtil;
import com.cleveroad.sample.utils.CsvUtils;
import com.cleveroad.sample.utils.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.WeakHashMap;

public class CsvFileDataSourceImpl implements TableDataSource<String, String, String, String> {
    private static final String TAG = CsvFileDataSourceImpl.class.getSimpleName();
    private static final int READ_FILE_LINES_LIMIT = 200;
    private static final int CSV_LOADER = 0;
    private final Context mContext;
    private final Uri mCsvFileUri;
    private final Map<Integer, List<String>> mItemsCache = new WeakHashMap<>();
    @SuppressLint("UseSparseArrays")
    private final SparseArray<SparseArray<String>> mChangedItems = new SparseArray<>();
    private Uri mCsvTempFileUri;
    private int mRowsCount;
    private int mColumnsCount;

    public CsvFileDataSourceImpl(Context context, Uri csvFileUri) {
        mContext = context;
        mCsvFileUri = csvFileUri;
        init();
    }

    @Override
    public int getRowsCount() {
        return mRowsCount;
    }

    @Override
    public int getColumnsCount() {
        return mColumnsCount;
    }

    @Override
    public String getFirstHeaderData() {
        return getItemData(0, 0);
    }

    @Override
    public String getRowHeaderData(int index) {
        return getItemData(index, 0);
    }

    @Override
    public String getColumnHeaderData(int index) {
        return getItemData(0, index);
    }

    @Override
    public String getItemData(int rowIndex, int columnIndex) {
        try {
            List<String> rowList = getRow(rowIndex);
            return rowList == null ? "" : rowList.get(columnIndex);
        } catch (Exception e) {
            Log.e(TAG, "get rowIndex=" + rowIndex + "; colIndex=" + columnIndex + ";\ncache = " +
                    mItemsCache.toString(), e);
            return null;
        }
    }

    public List<String> getRowValues(int rowIndex) {
        return getRow(rowIndex);
    }

    public Uri getCsvFileUri() {
        return mCsvFileUri;
    }

    public Uri getCsvTempFileUri() {
        return mCsvTempFileUri;
    }

    public void updateItem(int rowIndex, int columnIndex, String value) {
        SparseArray<String> rowItems = mChangedItems.get(rowIndex);
        if (rowItems == null) {
            rowItems = new SparseArray<>();
            mChangedItems.put(rowIndex, rowItems);
        }
        rowItems.put(columnIndex, value);
    }

    public void applyChanges(
            LoaderManager loaderManager,
            final Map<Integer, Integer> rowModifications,
            final Map<Integer, Integer> columnModifications,
            final boolean isSolidRowHeader,
            final UpdateFileCallback callback) {

        loaderManager.restartLoader(CSV_LOADER, Bundle.EMPTY, new LoaderManager.LoaderCallbacks<String>() {
            @NonNull
            @Override
            public Loader<String> onCreateLoader(int id, Bundle args) {
                return new UpdateCsvFileLoader(
                        mContext,
                        CsvFileDataSourceImpl.this,
                        rowModifications,
                        columnModifications,
                        isSolidRowHeader);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<String> loader, String data) {
                callback.onFileUpdated(data, data != null && !data.isEmpty());
            }

            @Override
            public void onLoaderReset(@NonNull Loader<String> loader) {
                //do nothing
            }
        });
    }

    public void applyChanges(
            LoaderManager loaderManager,
            final Map<Integer, Integer> rowModifications,
            final Map<Integer, Integer> columnModifications,
            final boolean isSolidRowHeader,
            final int actionChangeData,
            final int position,
            final boolean beforeOrAfter,
            final UpdateFileCallback callback) {

        loaderManager.restartLoader(CSV_LOADER, Bundle.EMPTY, new LoaderManager.LoaderCallbacks<String>() {
            @NonNull
            @Override
            public Loader<String> onCreateLoader(int id, Bundle args) {
                return new UpdateCsvFileLoader(
                        mContext,
                        CsvFileDataSourceImpl.this,
                        rowModifications,
                        columnModifications,
                        actionChangeData,
                        position,
                        beforeOrAfter,
                        isSolidRowHeader);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<String> loader, String data) {
                callback.onFileUpdated(data);
            }

            @Override
            public void onLoaderReset(@NonNull Loader<String> loader) {
                //do nothing
            }
        });
    }

    private InputStreamReader getInputStreamReader() throws IOException {
        return new FileReader(mCsvTempFileUri.getEncodedPath());
    }

    public void destroy() {
        File originalFile = new File(mCsvTempFileUri.getEncodedPath());
        if (originalFile.exists()) {
            originalFile.delete();
        }
        mItemsCache.clear();
        mChangedItems.clear();
    }

    void init() {
        File tempFile = FileUtils.createTempFile(mContext);

        try {
            File originalFile = new File(mCsvFileUri.getEncodedPath());
            FileUtils.copy(originalFile, tempFile);
        } catch (IOException e) {
            Log.e(TAG, "init method error ", e);
        } finally {
            mCsvTempFileUri = Uri.fromFile(tempFile);
        }
        mRowsCount = calculateLinesCount();
        mColumnsCount = getRow(0).size();
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
            Log.e(TAG, "calculateLinesCount method error ", e);
        } finally {
            ClosableUtil.closeWithoutException(fileReader);
            ClosableUtil.closeWithoutException(lineNumberReader);
        }
        return 0;
    }

    List<String> getRow(int rowIndex) {
        List<String> result = new ArrayList<>();

        // cache logic. Show field after not saved changes
        List<String> cachedRow = mItemsCache.get(rowIndex);
        if (cachedRow != null && !cachedRow.isEmpty()) {
            SparseArray<String> changedRow = mChangedItems.get(rowIndex);
            if (changedRow != null && changedRow.size() > 0) {
                for (int count = cachedRow.size(), i = 0; i < count; i++) {
                    String cachedItem = cachedRow.get(i);
                    String changedItem = changedRow.get(i);
                    result.add(TextUtils.isEmpty(changedItem) ? cachedItem : changedItem);
                }
            } else {
                result.addAll(cachedRow);
            }
        }


        if (!result.isEmpty()) {
            return result;
        }

        //read from file
        InputStreamReader fileReader = null;

        try {
            fileReader = getInputStreamReader();
            int cacheRowIndex = rowIndex < READ_FILE_LINES_LIMIT ? 0 : rowIndex - READ_FILE_LINES_LIMIT;
            //skip upper lines
            Scanner scanner = new Scanner(fileReader).skip("(?:.*\\r?\\n|\\r){" + cacheRowIndex + "}");

//            for (int i = 0; i < cacheRowIndex; i++) {
//                scanner.nextLine();
//            }

            int cacheRowLimitIndex = cacheRowIndex + READ_FILE_LINES_LIMIT + READ_FILE_LINES_LIMIT;
            //on scroll to bottom
            for (int i = cacheRowIndex; i < getRowsCount() && i < cacheRowLimitIndex && scanner.hasNextLine(); i++) {
                List<String> line = new ArrayList<>(CsvUtils.parseLine(scanner.nextLine()));
                mItemsCache.put(i, line);
                if (i == rowIndex) {
                    result.addAll(line);
                }
            }

            // clear cache
            Iterator<Map.Entry<Integer, List<String>>> iterator = mItemsCache.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, List<String>> entry = iterator.next();
                if (entry.getKey() < cacheRowIndex || entry.getKey() > cacheRowLimitIndex) {
                    iterator.remove();
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "getRow method error ", e);
        } finally {
            ClosableUtil.closeWithoutException(fileReader);
        }

        return result;
    }
}