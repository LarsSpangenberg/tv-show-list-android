package com.example.showtracker.screens.showslist;

import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.*;

import com.example.showtracker.*;
import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.toolbar.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.views.*;
import com.google.android.material.floatingactionbutton.*;

import java.util.*;

import static com.example.showtracker.screens.common.utils.ListItemSortHandler.SHOW_SORT_MODE;
import static com.example.showtracker.screens.common.utils.ListItemSortHandler.SORT_BY_CUSTOM;
import static com.example.showtracker.screens.common.utils.ListItemSortHandler.SORT_BY_NAME;

public class ShowsListViewMvcImpl extends BaseObservableViewMvc<ShowsListViewMvc.Listener>
    implements ShowsRecyclerViewAdapter.Listener,
    ToolbarViewMvc.MenuItemClickListener,
    ToolbarViewMvc.NavigateUpClickListener,
    ShowsListViewMvc {

    private static final String TAG = "ShowsListViewMvcImpl";
    private final ShowsRecyclerViewAdapter adapter;
    private final ToolbarViewMvc toolbarViewMvc;
    private ListEntity currentList;
    private SharedPreferences prefs;
    private ShowsListFilters filters;

    public ShowsListViewMvcImpl(
        ListEntity currentList,
        LayoutInflater inflater,
        SharedPreferences prefs,
        ListItemSelectionHandler selectionHandler,
        ShowsListFilters filters,
        ViewGroup parent,
        ViewMvcFactory viewMvcFactory
    ) {
        this.currentList = currentList;
        this.prefs = prefs;
        this.filters = filters;
        setRootView(inflater.inflate(R.layout.activity_shows_list, parent, false));

        adapter = new ShowsRecyclerViewAdapter(prefs, selectionHandler, this, viewMvcFactory);

        RecyclerView recyclerView = findViewById(R.id.shows_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper.Callback callback = new ItemMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarViewMvc = viewMvcFactory.getToolbarViewMvc(toolbar);

        initToolbar();
        initFab();
    }

    private void initToolbar() {
        toolbarViewMvc.setTitle(currentList.toString());
        toolbarViewMvc.inflateMenu(R.menu.shows_list_menu);
        toolbarViewMvc.registerListener(this);
        toolbarViewMvc.enableUpButtonAndListen(this);
        toolbarViewMvc.enableDeleteButton();

        handleSortMenuCheckBox();
    }

    @Override
    public void bindShowsAndTags(
        List<ShowWithTags> shows,
        List<Tag> allTags,
        String statusFilterText,
        String tagFilterText
    ) {
        Log.d(TAG, "bindShowsAndTags: " + shows.toString());
        adapter.bindTags(allTags);
        adapter.bindShows(shows);

        TextView noResultsMessage = findViewById(R.id.sl_empty_list_message);
        if (shows.isEmpty()) {
            noResultsMessage.setVisibility(View.VISIBLE);
        } else {
            noResultsMessage.setVisibility(View.GONE);
        }
//        adapter.bindShows(shows);
//        adapter.bindTags(allTags);
//        this.allTags = allTags;
//        setFilterDisplays(allTags);
//        invalidateOptionsMenu();
        setStatusFilterReferenceText(statusFilterText);
        setTagFilterReferenceText(tagFilterText);
        handleSortMenuCheckBox();

    }

    @Override
    public void sortShows(int sortBy) {
        adapter.sortShows(sortBy);
    }

    @Override
    public void onShowClick(Show show) {
        for (Listener listener : getListeners()) {
            listener.onShowClick(show);
        }
    }

    @Override
    public void onShowDragAndDrop(Show toMove, Show target) {
        for (Listener listener : getListeners()) {
            listener.onShowDragAndDrop(toMove, target);
        }
    }

    @Override
    public void onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        for (Listener listener : getListeners()) {
            switch (id) {
                case R.id.shows_list_delete:
                    listener.onDeleteShowClick();
                    break;
                case R.id.sl_menu_sort_by_name:
                    sortShows(SORT_BY_NAME);
                    break;
                case R.id.sl_menu_sort_by_custom:
                    sortShows(SORT_BY_CUSTOM);
                    break;
                case R.id.sl_menu_filter_status:
                    listener.onStatusFilterMenuClick();
                    break;
                case R.id.sl_menu_filter_tag:
                    listener.onTagFilterMenuClick();
                    break;
            }
        }
    }

    @Override
    public void onNavigateUpClicked() {
        for (Listener listener : getListeners()) {
            listener.onNavigationUpClick();
        }
    }


//    private void setFilterDisplays(List<Tag> allTags) {
//        // shows which filters have been selected at the bottom of the screen for the user's reference
//
//        List<String> tagIds = filters.getFilteredTagIds();
//        List<Show.Status> statusFilters = filters.getStatusFilters();
//        StringBuilder builder;
//
//        if (tagIds.isEmpty()) {
//        } else {
//            builder = new StringBuilder();
//            boolean firstItem = true;
//            builder.append("Tag Filters: ");
//            for (Tag tag : allTags) {
//                if (tagIds.contains(tag.id)) {
//                    if (firstItem) {
//                        firstItem = false;
//                    } else {
//                        builder.append(", ");
//                    }
//                    builder.append(tag.name);
//                }
//            }
//
//        }
//
//        if (statusFilters.isEmpty()) {
//            statusFilterDisplay.setVisibility(View.GONE);
//        } else {
//            builder = new StringBuilder();
//            builder.append("Status Filters: ");
//            for (int i = 0; i < statusFilters.size(); i++) {
//                if (i != 0) {
//                    builder.append(", ");
//                }
//                builder.append(statusFilters.get(i).toString());
//            }
//
//        }
//
//    }
    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Listener listener : getListeners()) {
                    listener.onFabClick();
                }
            }
        });
    }

    private void handleSortMenuCheckBox() {
        int sortMode = prefs.getInt(SHOW_SORT_MODE, SORT_BY_CUSTOM);
        toolbarViewMvc
            .findMenuItem(R.id.sl_menu_sort_by_name)
            .setChecked(sortMode == SORT_BY_NAME);
        toolbarViewMvc
            .findMenuItem(R.id.sl_menu_sort_by_custom)
            .setChecked(sortMode == SORT_BY_CUSTOM);
    }

    private void setStatusFilterReferenceText(String statusFilters) {
        TextView statusFilterDisplay = findViewById(R.id.sl_status_filter_display);
        if (statusFilters.isEmpty()) {
            statusFilterDisplay.setVisibility(View.GONE);
        } else {
            statusFilterDisplay.setVisibility(View.VISIBLE);
            statusFilterDisplay.setText(statusFilters);
        }
    }

    private void setTagFilterReferenceText(String tagFilters) {
        TextView tagFilterDisplay = findViewById(R.id.sl_tag_filter_display);
        if (tagFilters.isEmpty()) {
            tagFilterDisplay.setVisibility(View.GONE);
        } else {
            tagFilterDisplay.setVisibility(View.VISIBLE);
            tagFilterDisplay.setText(tagFilters);
        }
    }
}
