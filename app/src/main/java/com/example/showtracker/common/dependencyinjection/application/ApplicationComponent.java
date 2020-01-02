package com.example.showtracker.common.dependencyinjection.application;

import com.example.showtracker.common.dependencyinjection.presentation.*;
import com.example.showtracker.data.*;

import javax.inject.*;

import dagger.*;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    PresentationComponent newPresentationComponent(PresentationModule presentationModule);
    ListsRepository getListsRepository();
    ShowsRepository getShowsRepository();
    TagsRepository getTagsRepository();
}
