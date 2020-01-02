package com.example.showtracker.common.dependencyinjection.presentation;

import com.example.showtracker.*;
import com.example.showtracker.viewmodels.*;

import dagger.*;

@Module
public class ViewModelModule {

//    private final ApplicationComponent applicationComponent;
//
//    public ViewModelModule(ApplicationComponent applicationComponent) {
//        this.applicationComponent = applicationComponent;
//    }

    @Provides
    ViewModelFactory viewModelFactory(MyApplication application) {
        return new ViewModelFactory(application);
    }

    @Provides
    ViewModelWithIdFactory viewModelWithIdFactory(MyApplication application) {
        return new ViewModelWithIdFactory(application);
    }

//    @Provides
//    MainViewModel mainViewModel(ListsRepository listsRepository) {
//        return new MainViewModel(listsRepository);
//    }

//    @Provides
//    ShowsListViewModel showsListViewModel() {
//        return new ShowsListViewModel(applicationComponent);
//    }
//
//    @Provides
//    ShowDetailsViewModel showDetailsViewModel() {
//        return new ShowDetailsViewModel(applicationComponent);
//    }


//    @Provides
//    TagListViewModel tagListViewModel(TagsRepository tagsRepository) {
//        return new TagListViewModel(tagsRepository);
//    }


}
