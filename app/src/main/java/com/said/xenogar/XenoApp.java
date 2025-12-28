package com.said.xenogar;

import android.app.Application;

import com.said.xenogar.data.local.AppDatabase;

public class XenoApp extends Application {
    public AppDatabase getDatabase(){
        return AppDatabase.getAppDatabase(this);
    }
}
