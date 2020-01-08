package com.example.showtracker.screens.lists;

import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.screens.common.views.*;

import java.util.*;

public interface ListsViewMvc extends ObservableViewMvc<ListsViewMvc.Listener> {
    interface Listener {
        void onListClick(ListWithShows list);
        void onListDragAndDrop(ListEntity toMove, ListEntity target);
        void onSortItems(int sortBy);
        void onFabClick();
    }
    void bindLists(List<ListWithShows> lists);
    void sortItems(int sortBy);
}
