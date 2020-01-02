package com.example.showtracker.viewmodels;

import androidx.annotation.*;
import androidx.lifecycle.*;

import com.example.showtracker.*;

public class ViewModelWithIdFactory implements ViewModelProvider.Factory {

    private final MyApplication application;
    private String id;


    public ViewModelWithIdFactory(MyApplication application) {
        this.application = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        ViewModel viewModel;
        if (modelClass == ShowsListViewModel.class) {
            if (id == null || id.isEmpty()) {
                throw new RuntimeException("need to set an id before using this factory");
            }
            viewModel = new ShowsListViewModel(application, id);
        } else if (modelClass == ShowDetailsViewModel.class) {
            viewModel = new ShowDetailsViewModel(application, id);
        } else {
            throw new RuntimeException("invalid view model class: " + modelClass);
        }
        return (T) viewModel;
    }

    public void setId(String id) {
        this.id = id;
    }
}
