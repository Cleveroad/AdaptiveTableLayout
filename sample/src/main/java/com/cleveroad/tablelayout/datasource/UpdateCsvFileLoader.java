package com.cleveroad.tablelayout.datasource;

import com.cleveroad.tablelayout.utils.ClosableUtil;
import com.cleveroad.tablelayout.utils.StringUtils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateCsvFileLoader extends AsyncTaskLoader<Boolean> {
    private static final String TAG = UpdateCsvFileLoader.class.getSimpleName();
    private final CsvFileDataSourceImpl mCsvFileDataSource;
    private final Map<Integer, Integer> mRowModifications;
    private final Map<Integer, Integer> mColumnModifications;

    public UpdateCsvFileLoader(Context context,
                               CsvFileDataSourceImpl csvFileDataSource,
                               Map<Integer, Integer> rowModifications,
                               Map<Integer, Integer> columnModifications) {
        super(context);
        mCsvFileDataSource = csvFileDataSource;
        mRowModifications = rowModifications;
        mColumnModifications = columnModifications;

        onContentChanged();
    }

    private static List<String> modifyListPositions(List<String> inputList, Map<Integer, Integer>
            modifications) {
        List<String> result = new ArrayList<>(inputList.size());
        for (int i = 0, size = inputList.size(); i < size; i++) {
            Integer newPosition = null;
            if (i != 0) {
                //do not move first fixed column
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

    public boolean applyChanges() {
        OutputStreamWriter writer = null;
        final String oldFileName = mCsvFileDataSource.getCsvFileUri().getEncodedPath();
        final String newFileName = oldFileName + "_new.csv";

        try {
            writer = new FileWriter(newFileName);
            writer.write(StringUtils.toString(modifyListPositions(mCsvFileDataSource
                            .getColumnHeaders(),
                    mColumnModifications), ","));
            writer.write("\n");
            for (int i = 0, size = mCsvFileDataSource.getRowsCount(); i < size; i++) {
                Integer newRowPosition = mRowModifications.get(i);
                List<String> row = mCsvFileDataSource.getRow(newRowPosition != null ?
                        newRowPosition : i);
                writer.write(StringUtils.toString(modifyListPositions(row, mColumnModifications),
                        ","));
                if (i != size - 1) {
                    writer.write("\n");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        } finally {
            ClosableUtil.closeWithoutException(writer);
        }

        try {
            File oldFile = new File(oldFileName);
            File newFile = new File(newFileName);

            //delete old file and rename new file
            boolean result = oldFile.exists()
                    && oldFile.delete()
                    && newFile.renameTo(new File(oldFileName));
            //invalidate cache
            if (result) {
                mCsvFileDataSource.destroy();
                mCsvFileDataSource.init();
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return false;
    }
}
