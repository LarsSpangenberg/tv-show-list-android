package com.example.showtracker.data.lists.entities;

import androidx.room.*;

import com.example.showtracker.data.common.joins.*;
import com.example.showtracker.screens.common.utils.*;

import java.util.*;

public class ListWithShows implements ListItemSortHandler.Sortable {
    @Embedded
    public ListEntity list;
    @Relation(
        parentColumn = "id",
        entityColumn = "listId",
        entity = ListShowJoin.class,
        projection = "showId"
    )
    public List<String> showIds;

    @Ignore
    public int getShowCount() {
        return showIds.size();
    }

    public String getId() {
        return list.id;
    }

    public ListEntity getList() {
        return list;
    }

    @Override
    public String getName() {
        return getList().name;
    }

    @Override
    public int getPosition() {
        return getList().position;
    }
}
