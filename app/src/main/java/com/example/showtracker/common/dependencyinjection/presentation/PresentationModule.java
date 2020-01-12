package com.example.showtracker.common.dependencyinjection.presentation;

import android.app.*;
import android.content.*;
import android.view.*;

import com.example.showtracker.screens.common.screensnavigator.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.views.*;

import dagger.*;

@Module
public class PresentationModule {
    private final Activity activity;
    private ListItemSelectionHandler selectionHandler;

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
        SharedPreferences prefs,
        ListItemSelectionHandler selectionHandler
    ) {
        return new ViewMvcFactory(layoutInflater, prefs, selectionHandler);
    }

    @Provides
    ScreensNavigator getScreensNavigator() {
        return new ScreensNavigator(activity);
    }

    @Provides
    ListItemSelectionHandler getSelectionHandler() {
        if (selectionHandler == null) {
            selectionHandler = new ListItemSelectionHandler();
        }
        return selectionHandler;
    }

    @Provides
    ShowsListFilters getShowFilterHandler() {
        return new ShowsListFilters();
    }
}
