package com.troshchiy.drawellipse;

import android.app.Application;

public class App extends Application {

    public static App APP;

    @Override
    public void onCreate() {
        super.onCreate();
        APP = this;
    }
}