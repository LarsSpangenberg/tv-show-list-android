package com.example.showtracker.common;

import android.app.*;

import com.example.showtracker.common.dependencyinjection.application.*;

public class CustomApplication extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(new ApplicationModule(this))
            .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
