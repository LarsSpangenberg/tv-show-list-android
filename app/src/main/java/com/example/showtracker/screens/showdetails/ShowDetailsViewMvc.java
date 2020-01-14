package com.example.showtracker.screens.showdetails;

import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.views.*;

import java.util.*;

public interface ShowDetailsViewMvc extends ObservableViewMvc<ShowDetailsViewMvc.Listener> {
     interface Listener {
        void onListChipClick(String listId, boolean isChecked);
        void onTagChipClick(String tagId, boolean isChecked);
        void onNavigateUpClicked();
    }
    void bindShow(ShowDetails showData);
    void bindLists(List<ListEntity> allLists, List<String> showsListIds);
    void bindTags(List<Tag> allTags, List<String> showsTagIds);
    Show getUserInput(boolean editMode);
    void requestFocusOnTitleEditText();
    boolean isTitleEditTextEmpty();
}
