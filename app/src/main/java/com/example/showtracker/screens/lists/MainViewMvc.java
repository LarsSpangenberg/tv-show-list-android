package com.example.showtracker.screens.lists;

import android.content.*;
import android.view.*;

import androidx.recyclerview.widget.*;

import com.example.showtracker.*;
import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.screens.common.utils.*;

import java.util.*;

public class MainViewMvc implements ListsRecyclerViewAdapter.Listener {

    public interface Listener {
        void onListClick(ListWithShows list);
        boolean onListDragAndDrop(ListEntity toMove, ListEntity target);
        void onSortItems(int sortBy);
    }

    private ListsRecyclerViewAdapter adapter;

    private final View rootView;

    private final List<Listener> listeners = new ArrayList<>(1);


    public MainViewMvc(
        LayoutInflater inflater,
        SharedPreferences prefs,
        ListItemSelectionHandler selectionHandler,
        ViewGroup parent
    ) {
        rootView = inflater.inflate(R.layout.activity_main, parent, false);


        adapter = new ListsRecyclerViewAdapter(inflater, prefs, selectionHandler, this);
        RecyclerView recyclerView = findViewById(R.id.lists_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper.Callback callback = new ItemMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    public void bindLists(List<ListWithShows> lists) {
        adapter.bindLists(lists);
    }

    @Override
    public void onListClick(ListWithShows list) {
        for (Listener listener : listeners) {
            listener.onListClick(list);
        }
    }

    @Override
    public boolean onListDragAndDrop(ListEntity toMove, ListEntity target) {
        boolean itemMoved = false;
        for (Listener listener : listeners) {
            itemMoved = listener.onListDragAndDrop(toMove, target);
        }
        return itemMoved;
    }

    public void sortItems(int sortBy) {
        adapter.sortItems(sortBy);
    }


    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    private Context getContext() {
        return getRootView().getContext();
    }

    private <T extends View> T findViewById(int id) {
        return getRootView().findViewById(id);
    }

    public View getRootView() {
        return rootView;
    }
}
