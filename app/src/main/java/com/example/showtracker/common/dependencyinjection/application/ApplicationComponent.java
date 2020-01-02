package com.example.showtracker.common.dependencyinjection.application;

import com.example.showtracker.common.dependencyinjection.presentation.*;

import javax.inject.*;

import dagger.*;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    PresentationComponent newPresentationComponent(PresentationModule presentationModule);
}
