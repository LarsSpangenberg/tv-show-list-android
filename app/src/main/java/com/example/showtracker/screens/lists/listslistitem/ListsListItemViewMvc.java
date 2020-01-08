package com.example.showtracker.screens.lists.listslistitem;

import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.screens.common.views.*;

public interface ListsListItemViewMvc extends ObservableViewMvc<ListsListItemViewMvc.Listener> {
    interface Listener {
        void onListClick(ListWithShows list);
        void onListLongClick(ListWithShows list, int position);
    }

    void bindList(ListWithShows list, int position);
}
