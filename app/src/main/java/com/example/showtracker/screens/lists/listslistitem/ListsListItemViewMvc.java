package com.example.showtracker.screens.lists.listslistitem;

import android.view.*;

import com.example.showtracker.data.lists.entities.*;

public interface ListsListItemViewMvc {
    interface Listener {
        void onListClick(ListWithShows list);
        void onListLongClick(ListWithShows list, int position);
    }

    View getRootView();
    void registerListener(Listener listener);
    void unregisterListener(Listener listener);
    void bindList(
        ListWithShows list,
        int position,
        boolean isSelected
    );
}
