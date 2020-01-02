package com.example.showtracker.common.dependencyinjection.presentation;

import android.app.*;
import android.content.*;

import dagger.*;

@Module
public class PresentationModule {

    private final Activity activity;

    public PresentationModule(Activity activity) {
        this.activity = activity;
    }


    @Provides
    Context context(Activity activity) {
        return activity;
    }
}
