package com.example.showtracker.common.dependencyinjection.presentation;

import android.content.*;

import com.example.showtracker.screens.common.screensnavigator.*;
import com.example.showtracker.screens.common.viewmodel.*;
import com.example.showtracker.screens.common.views.*;
import com.example.showtracker.screens.lists.*;

import dagger.*;

@Subcomponent(modules = {PresentationModule.class, ViewModelModule.class})
public interface PresentationComponent {
    ViewModelFactory getViewModelFactory();
    ViewModelWithIdFactory getViewModelWithIdFactory();
    ViewMvcFactory getViewMvcFactory();
    SharedPreferences getSharedPreferences();
    ScreensNavigator getScreensNavigator();

    void inject(ListsActivity listsActivity);
}
