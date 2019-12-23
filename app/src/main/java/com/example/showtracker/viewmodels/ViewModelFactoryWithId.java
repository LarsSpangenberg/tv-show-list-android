package com.example.showtracker.viewmodels;

import android.app.*;

import androidx.annotation.*;
import androidx.lifecycle.*;

import java.lang.reflect.*;

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
        try {
            return modelClass
                .getConstructor(application.getClass(), String.class)
                .newInstance(application, id);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("could not get constructor of " + modelClass, e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("cannot create and instance class of " + modelClass, e);
        }
    }
}
