package com.cleveroad.sample.datasource;

import android.content.Context;
import android.os.Environment;
import androidx.loader.content.AsyncTaskLoader;
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

import static com.cleveroad.sample.datasource.Constants.ADD_COLUMN;
import static com.cleveroad.sample.datasource.Constants.ADD_ROW;
import static com.cleveroad.sample.datasource.Constants.DELETE_COLUMN;
import static com.cleveroad.sample.datasource.Constants.DELETE_ROW;

public class UpdateCsvFileLoader extends AsyncTaskLoader<String> {
    private static final String TAG = UpdateCsvFileLoader.class.getSimpleName();
    private static final String SEPARATOR = ",";
    private static final String TEMP_CSV = "_tempCSV.csv";
    private static final String CSV = ".csv";
    private static final String NEW_LINE = "\n";
    private final SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault());
    private final CsvFileDataSourceImpl mCsvFileDataSource;
    private final Map<Integer, Integer> mRowModifications;
    private final Map<Integer, Integer> mColumnModifications;
    private final boolean mIsSolidRowHeader;

    private int mActionChangeData;
    private int mPosition;
    private boolean mBeforeOrAfter;

    UpdateCsvFileLoader(Context context,
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

    UpdateCsvFileLoader(Context context,
                        CsvFileDataSourceImpl csvFileDataSource,
                        Map<Integer, Integer> rowModifications,
                        Map<Integer, Integer> columnModifications,
                        int actionChangeData,
                        int position,
                        boolean beforeOrAfter,
                        boolean isSolidRowHeader) {
        this(context, csvFileDataSource, rowModifications, columnModifications, isSolidRowHeader);
        mActionChangeData = actionChangeData;
        mPosition = position;
        mBeforeOrAfter = beforeOrAfter;
        onContentChanged();
    }

    private List<String> modifyListPositions(List<String> inputList, Map<Integer, Integer>
            modifications) {
        List<String> result = new ArrayList<>(inputList.size());
        for (int i = 0, size = inputList.size(); i < size; i++) {
            Integer newPosition = modifications.get(i);
            String value = inputList.get(newPosition != null ? newPosition : i);
            if (value.contains(SEPARATOR)) {
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
        if (mActionChangeData == 0) {
            return saveChange();
        } else {
            return applyChanges();
        }
    }

    private String saveChange() {

        final String originalFilePath = mCsvFileDataSource.getCsvFileUri().getEncodedPath();
        File originalFile = new File(originalFilePath);
        final String originalFileName = originalFile.getName();

        final String newFilePath;
        if (originalFilePath.contains(TEMP_CSV)) {
            newFilePath = originalFilePath.replace(TEMP_CSV, CSV);
        } else {
            newFilePath = originalFilePath.replace(CSV, TEMP_CSV);
        }
        File changedFile = new File(newFilePath);

        boolean isNeedToReplace;

        try {
            isNeedToReplace = changedFile.isFile() || changedFile.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "applyChanges method error ", e);
            isNeedToReplace = false;
            String newFileName;
            if (originalFileName.contains(TEMP_CSV)) {
                newFileName = originalFileName.replace(TEMP_CSV, CSV);
            } else {
                newFileName = originalFileName.replace(CSV, TEMP_CSV);
            }

            changedFile = new File(Environment.getExternalStorageDirectory(), newFileName);
            try {
                changedFile.createNewFile();
            } catch (IOException e1) {
                Log.e(TAG, "applyChanges method error ", e1);
                return "";
            }
        }

        OutputStreamWriter writer = null;
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
                        writer.write(StringUtils.toString(modifiedRow, SEPARATOR));
                        writer.write(NEW_LINE);
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
            String newFileName;
            if (originalFileName.contains(TEMP_CSV)) {
                newFileName = originalFileName.replace(TEMP_CSV, CSV);
            } else {
                newFileName = originalFileName;
            }

            //delete old file and rename new file
            boolean result = isNeedToReplace &&
                    originalFile.exists() &&
                    originalFile.delete() &&
                    changedFile.renameTo(new File(newFileName));
            //invalidate cache
            if (result) {
                mCsvFileDataSource.destroy();
                mCsvFileDataSource.init();
            }
            return isNeedToReplace ? changedFile.getPath() : originalFilePath;
        } catch (Exception e) {
            Log.e(TAG, "applyChanges method error ", e);
        }

        mCsvFileDataSource.destroy();
        mCsvFileDataSource.init();
        return originalFilePath;
    }

    private String applyChanges() {

        final String originalFilePath = mCsvFileDataSource.getCsvFileUri().getEncodedPath();
        File originalFile = new File(originalFilePath);
        final String originalFileName = originalFile.getName();

        final String newFilePath;
        if (originalFilePath.contains(TEMP_CSV)) {
            newFilePath = originalFilePath.replace(CSV, "_" + mDateFormatter.format(new Date()) + CSV);
        } else {
            newFilePath = originalFilePath.replace(CSV, TEMP_CSV);
        }

        File changedFile = new File(newFilePath);

        boolean isNeedToReplace;

        try {
            isNeedToReplace = changedFile.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "applyChanges method error ", e);
            isNeedToReplace = false;
            String newFileName;
            if (originalFileName.contains(TEMP_CSV)) {
                newFileName = originalFileName.replace(TEMP_CSV, "_" + mDateFormatter.format(new Date()) + CSV);
            } else {
                newFileName = originalFileName.replace(CSV, TEMP_CSV);
            }
            changedFile = new File(Environment.getExternalStorageDirectory(), newFileName);
            try {
                changedFile.createNewFile();
            } catch (IOException e1) {
                Log.e(TAG, "applyChanges method error ", e1);
                return "";
            }
        }

        if (mActionChangeData == ADD_ROW) {
            addRow(changedFile);
        } else if (mActionChangeData == DELETE_ROW) {
            deleteRow(changedFile);
        } else if (mActionChangeData == ADD_COLUMN) {
            addColumn(changedFile);
        } else if (mActionChangeData == DELETE_COLUMN) {
            deleteColumn(changedFile);
        }

        try {

            if (changedFile.getPath().contains(TEMP_CSV)) {
                return changedFile.getPath();
            } else {

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
            }
        } catch (Exception e) {
            Log.e(TAG, "applyChanges method error ", e);
        }

        mCsvFileDataSource.destroy();
        mCsvFileDataSource.init();
        return originalFilePath;
    }

    private String addRow(File changedFile) {
        StringBuilder emptyRow = new StringBuilder();

        for (int i = 0; i < mCsvFileDataSource.getColumnsCount(); i++) {
            emptyRow.append(SEPARATOR);
        }

        OutputStreamWriter writer = null;
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
                        boolean addRowHere = newRowPosition == null && mPosition == i
                                || newRowPosition != null && mPosition == newRowPosition;
                        if (mBeforeOrAfter && addRowHere) {
                            writer.write(emptyRow.toString());
                            writer.write(NEW_LINE);
                        }
                        writer.write(StringUtils.toString(modifiedRow, SEPARATOR));
                        writer.write(NEW_LINE);
                        if (!mBeforeOrAfter && addRowHere) {
                            writer.write(emptyRow.toString());
                            writer.write(NEW_LINE);
                        }
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

        return changedFile.getPath();
    }

    private String deleteRow(File changedFile) {
        OutputStreamWriter writer = null;
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

                    boolean deleteThisRow = newRowPosition == null && mPosition == i
                            || newRowPosition != null && mPosition == newRowPosition;
                    if (row != null && !row.isEmpty() && !deleteThisRow) {
                        List<String> modifiedRow = modifyListPositions(row, mColumnModifications);
                        writer.write(StringUtils.toString(modifiedRow, SEPARATOR));
                        writer.write(NEW_LINE);
                    } else {
                        // include an empty line if you deleted the last line
                        if (row != null && mCsvFileDataSource.getRowsCount() == 2){
                            StringBuilder emptyRow = new StringBuilder();
                            for(int j = 0; j < row.size() - 1; j++){
                                emptyRow.append(SEPARATOR);
                            }
                            writer.write(emptyRow.toString());
                            writer.write(NEW_LINE);
                        }
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

        return changedFile.getPath();
    }

    private String addColumn(File changedFile) {
        OutputStreamWriter writer = null;
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
                        StringBuilder item = new StringBuilder();
                        for (int j = 0; j < modifiedRow.size(); j++) {
                            Integer newColumnPosition = mColumnModifications.get(j);
                            boolean addColumnHere = newColumnPosition == null && mPosition == j
                                    || newColumnPosition != null && mPosition == newColumnPosition;
                            if (mBeforeOrAfter && addColumnHere) {
                                item.append(SEPARATOR);
                            }
                            item.append(modifiedRow.get(j));
                            item.append(SEPARATOR);
                            if (!mBeforeOrAfter && addColumnHere) {
                                item.append(SEPARATOR);
                            }
                        }
                        item.deleteCharAt(item.length() - SEPARATOR.length());
                        writer.write(item.toString());
                        writer.write(NEW_LINE);
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

        return changedFile.getPath();
    }

    private String deleteColumn(File changedFile) {
        OutputStreamWriter writer = null;
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
                        StringBuilder item = new StringBuilder();
                        for (int j = 0; j < modifiedRow.size(); j++) {
                            Integer newColumnPosition = mColumnModifications.get(j);
                            if (newColumnPosition == null && mPosition != j
                                    || newColumnPosition != null && mPosition != newColumnPosition) {
                                item.append(modifiedRow.get(j));
                                item.append(SEPARATOR);
                            }
                        }
                        if (modifiedRow.size() > 2) {
                            item.deleteCharAt(item.length() - SEPARATOR.length());
                        }
                        writer.write(item.toString());
                        writer.write(NEW_LINE);
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

        return changedFile.getPath();
    }
}