package com.example.showtracker.common.dependencyinjection.presentation;

import android.app.*;
import android.content.*;
import android.view.*;

import com.example.showtracker.screens.common.views.*;

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

    @Provides
    LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(activity);
    }

    @Provides
    ViewMvcFactory getViewMvcFactory(
        LayoutInflater layoutInflater,
        SharedPreferences prefs
    ) {
        return new ViewMvcFactory(layoutInflater, prefs);
    }
}
