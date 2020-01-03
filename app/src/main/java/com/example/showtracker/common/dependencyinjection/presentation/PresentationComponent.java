package com.example.showtracker.common.dependencyinjection.presentation;

import com.example.showtracker.screens.common.viewmodel.*;

import dagger.*;

@Subcomponent(modules = {PresentationModule.class, ViewModelModule.class})
public interface PresentationComponent {
    ViewModelFactory getViewModelFactory();
    ViewModelWithIdFactory getViewModelWithIdFactory();
}
