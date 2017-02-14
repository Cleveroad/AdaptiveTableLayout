package com.cleveroad.tablelayout.datasource;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.cleveroad.tablelayout.utils.ClosableUtil;
import com.cleveroad.tablelayout.utils.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateCsvFileLoader extends AsyncTaskLoader<Boolean> {
    //    private static final String TAG = UpdateCsvFileLoader.class.getSimpleName();
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd_hh_mm_ss", Locale.getDefault());
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
    public Boolean loadInBackground() {
        return applyChanges();
    }

    private boolean applyChanges() {
        Log.e("Menu", "applyChanges");
        //TODO FIX SAVING FILE!!!!
        OutputStreamWriter writer = null;
        final String oldFileName = mCsvFileDataSource.getCsvFileUri().getEncodedPath();

        final String newFileName = oldFileName.replace(".csv", DATE_FORMATTER.format(new Date()) + ".csv");
        File realCsvFile = new File(newFileName);
        File file = new File(Environment.getExternalStorageDirectory(), realCsvFile.getName());
        try {
            if (file.exists()) {
                file.delete();
            }
            if (file.createNewFile()) {
                writer = new FileWriter(file);
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
                Log.e("Files", "Not created file path = " + newFileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Log.e(TAG, e.getMessage());
            return false;
        } finally {
            ClosableUtil.closeWithoutException(writer);
        }

        mCsvFileDataSource.destroy();
        mCsvFileDataSource.init();

        return true;
    }
}
