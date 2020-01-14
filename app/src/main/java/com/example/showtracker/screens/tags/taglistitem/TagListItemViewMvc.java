package com.example.showtracker.screens.tags.taglistitem;

import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.views.*;

public interface TagListItemViewMvc extends ObservableViewMvc<TagListItemViewMvc.Listener> {
    interface Listener {
        void onTagClick(Tag tag);
        void onTagLongClick(Tag tag, int position);
    }

    void bindTag(Tag tag, int position);
}
