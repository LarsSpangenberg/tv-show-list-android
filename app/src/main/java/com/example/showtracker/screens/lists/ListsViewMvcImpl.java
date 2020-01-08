package com.example.showtracker.screens.lists;

import android.content.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.recyclerview.widget.*;

import com.example.showtracker.*;
import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.views.*;
import com.google.android.material.floatingactionbutton.*;

import java.util.*;

public class ListsViewMvcImpl extends BaseObservableViewMvc<ListsViewMvc.Listener>
    implements ListsRecyclerViewAdapter.Listener, ListsViewMvc {

    private ListsRecyclerViewAdapter adapter;

    public ListsViewMvcImpl(
        LayoutInflater inflater,
        SharedPreferences prefs,
        ListItemSelectionHandler selectionHandler,
        @Nullable ViewGroup parent,
        ViewMvcFactory viewMvcFactory
    ) {
        setRootView(inflater.inflate(R.layout.activity_main, parent, false));
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

        initFab();
    }

    @Override
    public void bindLists(List<ListWithShows> lists) {
        adapter.bindLists(lists);
        setNoResultsText(lists.isEmpty());
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
