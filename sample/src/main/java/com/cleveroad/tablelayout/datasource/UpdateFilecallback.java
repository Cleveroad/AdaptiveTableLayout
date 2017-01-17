package com.cleveroad.tablelayout.datasource;

import android.support.annotation.Nullable;

public interface UpdateFilecallback {
    void onFileUpdated(String fileName, boolean isSuccess, @Nullable Throwable throwable);
}
