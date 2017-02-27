package com.cleveroad.sample.datasource;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.cleveroad.sample.utils.ClosableUtil;
import com.cleveroad.sample.utils.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateCsvFileLoader extends AsyncTaskLoader<String> {
    private static final String TAG = UpdateCsvFileLoader.class.getSimpleName();
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault());
    private final CsvFileDataSourceImpl mCsvFileDataSource;
    private final Map<Integer, Integer> mRowModifications;
    private final Map<Integer, Integer> mColumnModifications;
    private final boolean mIsSolidRowHeader;

    public UpdateCsvFileLoader(Context context,
                               CsvFileDataSourceImpl csvFileDataSource,
                               Map<Integer, Integer> rowModifications,
                               Map<Integer, Integer> columnModifications,
                               boolean isSolidRowHeader) {
        super(context);
        mCsvFileDataSource = csvFileDataSource;
        mRowModifications = rowModifications;
        mColumnModifications = columnModifications;
        mIsSolidRowHeader = isSolidRowHeader;
        onContentChanged();
    }

    private List<String> modifyListPositions(List<String> inputList, Map<Integer, Integer>
            modifications) {
        List<String> result = new ArrayList<>(inputList.size());
        for (int i = 0, size = inputList.size(); i < size; i++) {
            Integer newPosition = modifications.get(i);
            String value = inputList.get(newPosition != null ? newPosition : i);
            if (value.contains(",")) {
                value = "\"" + value + "\"";
            }
            result.add(value);
        }

        return result;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (takeContentChanged()) {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        return applyChanges();
    }

    private String applyChanges() {
        OutputStreamWriter writer = null;
        final String originalFilePath = mCsvFileDataSource.getCsvFileUri().getEncodedPath();
        File originalFile = new File(originalFilePath);
        final String originalFileName = originalFile.getName();

        final String newFilePath = originalFilePath.replace(".csv", "_" + DATE_FORMATTER.format(new Date()) + ".csv");
        File changedFile = new File(newFilePath);

        boolean isNeedToReplace;

        try {
            isNeedToReplace = changedFile.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "applyChanges method error ", e);
            isNeedToReplace = false;
            String newFileName = originalFileName.replace(".csv", "_" + DATE_FORMATTER.format(new Date()) + ".csv");
            changedFile = new File(Environment.getExternalStorageDirectory(), newFileName);
            try {
                changedFile.createNewFile();
            } catch (IOException e1) {
                Log.e(TAG, "applyChanges method error ", e1);
                return "";
            }
        }

        try {
            if (changedFile.exists()) {
                writer = new FileWriter(changedFile);
                for (int i = 0, size = mCsvFileDataSource.getRowsCount(); i < size; i++) {
                    Integer newRowPosition = mRowModifications.get(i);
                    List<String> row = mCsvFileDataSource.getRow(newRowPosition != null ?
                            newRowPosition : i);

                    if (!mIsSolidRowHeader && !row.isEmpty()) {
                        String first = mCsvFileDataSource.getItemData(i, 0);
                        row.remove(0);
                        row.add(0, first);
                    }

                    if (row != null && !row.isEmpty()) {
                        List<String> modifiedRow = modifyListPositions(row, mColumnModifications);
                        writer.write(StringUtils.toString(modifiedRow, ","));
                        writer.write("\n");
                    }
                }
            } else {
                Log.e(TAG, "Not created file path = " + changedFile);
            }
        } catch (Exception e) {
            Log.e(TAG, "applyChanges method error ", e);
            return "";
        } finally {
            ClosableUtil.closeWithoutException(writer);
        }

        try {

            //delete old file and rename new file
            boolean result = isNeedToReplace &&
                    originalFile.exists() &&
                    originalFile.delete() &&
                    changedFile.renameTo(new File(originalFilePath));
            //invalidate cache
            if (result) {
                mCsvFileDataSource.destroy();
                mCsvFileDataSource.init();
            }
            return isNeedToReplace ? originalFilePath : changedFile.getPath();
        } catch (Exception e) {
            Log.e(TAG, "applyChanges method error ", e);
        }

        mCsvFileDataSource.destroy();
        mCsvFileDataSource.init();
        return originalFilePath;
    }
}
