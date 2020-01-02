package com.example.showtracker.viewmodels;

import android.app.*;

import androidx.annotation.*;
import androidx.lifecycle.*;

public class ViewModelFactoryWithId implements ViewModelProvider.Factory {

    private Application application;
    private String id;

    public ViewModelFactoryWithId(Application application, String id) {
        this.application = application;
        this.id = id;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == ShowsListViewModel.class) {
            return (T) new ShowsListViewModel(application, id);
        } else if (modelClass == ShowDetailsViewModel.class) {
            return (T) new ShowDetailsViewModel(application, id);
        } else {
            throw new RuntimeException("invalid view model class: " + modelClass);
        }
    }
}
