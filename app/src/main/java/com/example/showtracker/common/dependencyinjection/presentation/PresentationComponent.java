package com.example.showtracker.common.dependencyinjection.presentation;

import com.example.showtracker.viewmodels.*;

import dagger.*;

@Subcomponent(modules = {PresentationModule.class, ViewModelModule.class})
public interface PresentationComponent {
    ViewModelFactory getViewModelFactory();
    ViewModelWithIdFactory getViewModelWithIdFactory();
}
