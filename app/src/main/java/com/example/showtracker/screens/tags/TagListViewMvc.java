package com.example.showtracker.screens.tags;

import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.views.*;

import java.util.*;

public interface TagListViewMvc extends ObservableViewMvc<TagListViewMvc.Listener> {
    interface Listener {
        void onTagClick(Tag tag);
        void onTagDragAndDrop(Tag toMove, Tag target);
        void onDeleteTagClick();
        void onFabClick();
        void onNavigateUpClick();
    }

    void bindTags(List<Tag> tags);
}
