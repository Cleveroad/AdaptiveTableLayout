package com.cleveroad.sample;

import android.app.Application;
import android.content.Context;

public class SampleApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        SampleApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return SampleApplication.context;
    }
}
