package com.example.showtracker.screens.showslist;

import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.views.*;

import java.util.*;

public interface ShowsListViewMvc extends ObservableViewMvc<ShowsListViewMvc.Listener> {
    interface Listener {
        void onShowClick(Show show);
        void onShowDragAndDrop(Show toMove, Show target);
        void onFabClick();
        void onDeleteShowClick();
        void onTagFilterMenuClick();
        void onStatusFilterMenuClick();
        void onNavigationUpClick();
    }

    void bindShowsAndTags(List<ShowWithTags> shows, List<Tag> allTags);
    void sortShows(int sortBy);
    void setStatusFilterReferenceText(String statusFilters);
    void setTagFilterReferenceText(String tagFilters);
}
