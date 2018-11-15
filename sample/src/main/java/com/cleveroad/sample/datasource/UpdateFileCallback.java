package com.cleveroad.sample.datasource;

public interface UpdateFileCallback {

    void onFileUpdated(String fileName, boolean isSuccess);

    void onFileUpdated(String fileName);
}
