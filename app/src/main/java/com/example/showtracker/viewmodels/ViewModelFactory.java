package com.example.showtracker.viewmodels;

import androidx.annotation.*;
import androidx.lifecycle.*;

import com.example.showtracker.*;

public class ViewModelFactory implements ViewModelProvider.Factory {


    private MyApplication application;

    public ViewModelFactory(MyApplication application) {

        this.application = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        ViewModel viewModel;
        if (modelClass == MainViewModel.class) {
            viewModel = new MainViewModel(application);
        } else if (modelClass == ShowDetailsViewModel.class) {
            viewModel = new TagListViewModel(application);
        } else {
            throw new RuntimeException("invalid view model class: " + modelClass);
        }
        return (T) viewModel;
    }
}
