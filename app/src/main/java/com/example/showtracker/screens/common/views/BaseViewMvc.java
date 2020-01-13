package com.example.showtracker.screens.common.views;

import android.content.*;
import android.content.res.*;
import android.view.*;

import androidx.annotation.*;

public abstract class BaseViewMvc implements ViewMvc {
    private View rootView;

    @Override
    public View getRootView() {
        return rootView;
    }

    protected void setRootView(View rootView) {
        this.rootView = rootView;
    }

    protected  <T extends View> T findViewById(@IdRes int id) {
        return getRootView().findViewById(id);
    }

    protected Context getContext() {
        return getRootView().getContext();
    }

    protected Resources getResources() {
        return getContext().getResources();
    }

    protected String getString(@StringRes int id) {
        return getContext().getString(id);
    }

    protected String getString(@StringRes int id, Object... formatArgs) {
        return getContext().getString(id, formatArgs);
    }
}
