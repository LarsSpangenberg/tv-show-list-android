package com.example.showtracker.data.lists.entities;

import androidx.room.*;

import com.example.showtracker.data.common.joins.*;

import java.util.*;

public class ListWithShows {
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
}
