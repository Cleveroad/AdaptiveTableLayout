package com.cleveroad.sample.datasource;

public class Constants {

    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_VALUE = "EXTRA_VALUE";
    public static final String EXTRA_COLUMN_NUMBER = "EXTRA_COLUMN_NUMBER";
    public static final String EXTRA_ROW_NUMBER = "EXTRA_ROW_NUMBER";
    public static final String EXTRA_BEFORE_OR_AFTER = "EXTRA_BEFORE_OR_AFTER";

    private static int requestCode = 1;
    private static int actionChangeData = 1;

    public static final int REQUEST_CODE_EDIT_SONG = generateRequestCode();
    public static final int REQUEST_CODE_DELETE_ROW = generateRequestCode();
    public static final int REQUEST_CODE_ADD_ROW = generateRequestCode();
    public static final int REQUEST_CODE_ADD_ROW_CONFIRMED = generateRequestCode();
    public static final int REQUEST_CODE_ADD_COLUMN_CONFIRMED = generateRequestCode();
    public static final int REQUEST_CODE_DELETE_COLUMN = generateRequestCode();
    public static final int REQUEST_CODE_ADD_COLUMN = generateRequestCode();
    public static final int REQUEST_CODE_DELETE_ROW_CONFIRMED = generateRequestCode();
    public static final int REQUEST_CODE_DELETE_COLUMN_CONFIRMED = generateRequestCode();
    public static final int REQUEST_CODE_SETTINGS = generateRequestCode();

    public static final int ADD_ROW = generateActionChangeData();
    public static final int DELETE_ROW = generateActionChangeData();
    public static final int ADD_COLUMN = generateActionChangeData();
    public static final int DELETE_COLUMN = generateActionChangeData();

    private static int generateActionChangeData(){
        return ++actionChangeData;
    }

    private static int generateRequestCode(){
        return ++requestCode;
    }
}