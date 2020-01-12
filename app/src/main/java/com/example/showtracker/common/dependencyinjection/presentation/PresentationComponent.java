package com.example.showtracker.common.dependencyinjection.presentation;

import com.example.showtracker.screens.common.viewmodel.*;
import com.example.showtracker.screens.common.views.*;
import com.example.showtracker.screens.lists.*;
import com.example.showtracker.screens.showslist.*;

import dagger.*;

@Subcomponent(modules = {PresentationModule.class, ViewModelModule.class})
public interface PresentationComponent {
    ViewModelFactory getViewModelFactory();
    ViewModelWithIdFactory getViewModelWithIdFactory();
    ViewMvcFactory getViewMvcFactory();

    void inject(ListsActivity listsActivity);
    void inject(ShowsListActivity showsListActivity);
}
