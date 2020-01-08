package com.example.showtracker.screens.common.views;

import android.content.*;
import android.view.*;

import androidx.annotation.*;

import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.lists.*;
import com.example.showtracker.screens.lists.listslistitem.*;

public class ViewMvcFactory {
    private final LayoutInflater layoutInflater;
    private SharedPreferences prefs;

    public ViewMvcFactory(LayoutInflater layoutInflater, SharedPreferences prefs) {
        this.layoutInflater = layoutInflater;
        this.prefs = prefs;
    }

    public ListsViewMvc getListsViewMvc(ListItemSelectionHandler selectionHandler, @Nullable ViewGroup parent) {
        return new ListsViewMvcImpl(
            layoutInflater,
            prefs,
            selectionHandler,
            parent,
            this
        );
    }

    public ListsListItemViewMvc getListsListItemViewMvc(
        ListItemSelectionHandler selectionHandler,
        @Nullable ViewGroup parent
    ) {
        return new ListsListItemViewMvcImpl(layoutInflater, selectionHandler, parent);
    }
}
