package com.example.showtracker.screens.common.viewmodel;

import androidx.annotation.*;
import androidx.lifecycle.*;

import com.example.showtracker.common.*;
import com.example.showtracker.screens.lists.*;
import com.example.showtracker.screens.tags.*;

public class ViewModelFactory implements ViewModelProvider.Factory {


    private CustomApplication application;

    public ViewModelFactory(CustomApplication application) {

        this.application = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        ViewModel viewModel;
        if (modelClass == ListsViewModel.class) {
            viewModel = new ListsViewModel(application);
        } else if (modelClass == TagListViewModel.class) {
            viewModel = new TagListViewModel(application);
        } else {
            throw new RuntimeException("invalid view model class: " + modelClass);
        }
        return (T) viewModel;
    }
}
