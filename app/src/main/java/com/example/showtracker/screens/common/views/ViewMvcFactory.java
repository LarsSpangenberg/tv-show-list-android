package com.example.showtracker.screens.common.views;

import android.content.*;
import android.view.*;

import androidx.annotation.*;
import androidx.appcompat.widget.*;

import com.example.showtracker.screens.common.toolbar.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.lists.*;
import com.example.showtracker.screens.lists.listslistitem.*;

public class ViewMvcFactory {
    private final LayoutInflater layoutInflater;
    private final SharedPreferences prefs;
    private final ListItemSelectionHandler selectionHandler;

    public ViewMvcFactory(
        LayoutInflater layoutInflater,
        SharedPreferences prefs,
        ListItemSelectionHandler selectionHandler
    ) {
        this.layoutInflater = layoutInflater;
        this.prefs = prefs;
        this.selectionHandler = selectionHandler;
    }

    public ToolbarViewMvc getToolbarViewMvc(Toolbar toolbar) {
        return new ToolbarViewMvc(selectionHandler, toolbar);
    }

    public ListsViewMvc getListsViewMvc(@Nullable ViewGroup parent) {
        return new ListsViewMvcImpl(
            layoutInflater,
            prefs,
            selectionHandler,
            parent,
            this
        );
    }

    public ListsListItemViewMvc getListsListItemViewMvc(@Nullable ViewGroup parent) {
        return new ListsListItemViewMvcImpl(layoutInflater, selectionHandler, parent);
    }
}
