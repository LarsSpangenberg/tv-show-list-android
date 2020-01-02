package com.example.showtracker.common.dependencyinjection.application;

import android.app.*;
import android.content.*;

import com.example.showtracker.common.*;

import dagger.*;

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    private AppDatabase getRoomDatabase(Context context) {
        return AppDatabase.getInstance(context);
    }
}
