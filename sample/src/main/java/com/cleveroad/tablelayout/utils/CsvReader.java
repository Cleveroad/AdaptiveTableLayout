package com.cleveroad.tablelayout.utils;

import android.content.Context;

import com.cleveroad.tablelayout.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CsvReader {
    private static final String CSV_DELIMITER = ",";
    private final Context mContext;
    private final Logger mLogger;

    public CsvReader(Context context) {
        mContext = context;
        mLogger = Logger.getLogger(CsvReader.class.getSimpleName());


    }

    public List<List<String>> read(InputStream inputStream) {

        List<List<String>> result = new ArrayList<>();

        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        String line;

        try {
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null) {
                result.add(new ArrayList<>(Arrays.asList(line.split(CSV_DELIMITER))));
            }

        } catch (Exception e) {
            mLogger.log(Level.WARNING, "reading csv file error", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    mLogger.log(Level.WARNING, "closing bufferedReader error", e);
                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    mLogger.log(Level.WARNING, "closing inputStreamReader error", e);
                }
            }
        }

        return result;
    }
}
