package com.example.showtracker.viewmodels;

import android.app.*;

import androidx.annotation.*;
import androidx.lifecycle.*;

public class ViewModelWithIdFactory implements ViewModelProvider.Factory {

    private Application application;
    private String id;

    public ViewModelWithIdFactory(Application application, String id) {
        this.application = application;
        this.id = id;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        ViewModel viewModel;
        if (modelClass == ShowsListViewModel.class) {
            viewModel = new ShowsListViewModel(application, id);
        } else if (modelClass == ShowDetailsViewModel.class) {
            viewModel = new ShowDetailsViewModel(application, id);
        } else {
            throw new RuntimeException("invalid view model class: " + modelClass);
        }
        return (T) viewModel;
    }
}
