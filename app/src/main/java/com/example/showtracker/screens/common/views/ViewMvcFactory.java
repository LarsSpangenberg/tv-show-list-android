package com.example.showtracker.screens.common.views;

import android.content.*;
import android.view.*;

import androidx.annotation.*;
import androidx.appcompat.widget.*;

import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.screens.common.toolbar.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.lists.*;
import com.example.showtracker.screens.lists.listslistitem.*;
import com.example.showtracker.screens.showdetails.*;
import com.example.showtracker.screens.showslist.*;
import com.example.showtracker.screens.showslist.showslistitem.*;
import com.example.showtracker.screens.tags.*;
import com.example.showtracker.screens.tags.taglistitem.*;

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
        return new ListsViewMvcImpl(layoutInflater, prefs, selectionHandler, parent, this);
    }

    public ShowsListViewMvc getShowsListViewMvc(ListEntity current, @Nullable ViewGroup parent) {
        return new ShowsListViewMvcImpl(
            current,
            layoutInflater,
            prefs,
            selectionHandler,
            parent,
            this
        );
    }

    public ShowDetailsViewMvc getShowDetailsViewMvc(@Nullable ViewGroup parent) {
        return new ShowDetailsViewMvcImpl(layoutInflater, parent, this);
    }

    public TagListViewMvc getTagListViewMvc(@Nullable ViewGroup parent) {
        return new TagListViewMvcImpl(layoutInflater, prefs, selectionHandler, parent, this);
    }

    public ListsListItemViewMvc getListsListItemViewMvc(@Nullable ViewGroup parent) {
        return new ListsListItemViewMvcImpl(layoutInflater, selectionHandler, parent);
    }

    public ShowsListItemViewMvc getShowsListItemViewMvc(@Nullable ViewGroup parent) {
        return new ShowsListItemViewMvcImpl(layoutInflater, selectionHandler, parent);
    }

    public TagListItemViewMvc getTagListItemViewMvc(@Nullable ViewGroup parent) {
        return new TagListItemViewMvcImpl(layoutInflater, selectionHandler, parent);
    }
}
