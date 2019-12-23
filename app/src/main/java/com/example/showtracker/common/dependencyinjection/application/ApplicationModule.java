package com.example.showtracker.common.dependencyinjection.application;

import android.content.*;

import com.example.showtracker.common.*;

import dagger.*;

@Module
public class ApplicationModule {

    private AppDatabase getRoomDatabase(Context context) {
        return AppDatabase.getInstance(context);
    }
}
