package com.example.showtracker.screens.lists;

import android.content.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.*;

import com.example.showtracker.*;
import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.screens.common.toolbar.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.views.*;
import com.google.android.material.floatingactionbutton.*;

import java.util.*;

import static com.example.showtracker.screens.common.utils.ListItemSortHandler.*;

public class ListsViewMvcImpl extends BaseObservableViewMvc<ListsViewMvc.Listener>
    implements ListsRecyclerViewAdapter.Listener,
    ToolbarViewMvc.MenuItemClickListener,
    ListsViewMvc {

    private ListsRecyclerViewAdapter adapter;
    private ToolbarViewMvc toolbarViewMvc;

    public ListsViewMvcImpl(
        LayoutInflater inflater,
        SharedPreferences prefs,
        ListItemSelectionHandler selectionHandler,
        @Nullable ViewGroup parent,
        ViewMvcFactory viewMvcFactory
    ) {
        setRootView(inflater.inflate(R.layout.activity_lists, parent, false));

        adapter = new ListsRecyclerViewAdapter(
            prefs,
            selectionHandler,
            this,
            viewMvcFactory
        );

        RecyclerView recyclerView = findViewById(R.id.lists_list);
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
        toolbarViewMvc.setTitle(getString(R.string.app_name));
        toolbarViewMvc.registerListener(this);
        toolbarViewMvc.inflateMenu(R.menu.lists_menu);
        toolbarViewMvc.enableDeleteButton();
        toolbarViewMvc.enableEditButton();
    }

    @Override
    public void bindLists(List<ListWithShows> lists) {
        adapter.bindLists(lists);
        setNoResultsText(lists.isEmpty());
    }

    @Override
    public void onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        for (Listener listener : getListeners()) {
            switch (id) {
                case R.id.main_menu_delete_selection:
                    listener.onDeleteListClick();
                    break;
                case R.id.main_menu_rename_list:
                    listener.onEditListClick();
                    break;
                case R.id.main_menu_sort_by_name:
                    sortItems(SORT_BY_NAME);
                    break;
                case R.id.main_menu_sort_by_custom:
                    sortItems(SORT_BY_CUSTOM);
                    break;
                case R.id.main_menu_tag_list:
                    listener.onTagListClick();
                    break;
            }
        }
    }


    @Override
    public void onListClick(ListWithShows list) {
        for (Listener listener : getListeners()) {
            listener.onListClick(list);
        }
    }

    @Override
    public void onListDragAndDrop(ListEntity toMove, ListEntity target) {
        for (Listener listener : getListeners()) {
            listener.onListDragAndDrop(toMove, target);
        }
    }

    @Override
    public void sortItems(int sortBy) {
        adapter.sortItems(sortBy);
    }

    private void setNoResultsText(boolean isEmpty) {
        TextView noResultsMessage = findViewById(R.id.main_empty_list_message);
        if (isEmpty) {
            noResultsMessage.setVisibility(View.VISIBLE);
        } else {
            noResultsMessage.setVisibility(View.GONE);
        }
    }

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
}
