package com.example.showtracker.screens.showslist.showslistitem;

import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.screens.common.views.*;

public interface ShowsListItemViewMvc extends ObservableViewMvc<ShowsListItemViewMvc.Listener> {
    interface Listener {
        void onShowClick(Show show);
        void onLongShowClick(Show show, int position);
    }

    void bindShow(Show show, int position, String tagText);
}
