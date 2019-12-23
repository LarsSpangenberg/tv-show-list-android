package com.example.showtracker.data.entities;

import androidx.room.*;

import java.util.*;

public class ListWithShows {
    @Embedded
    public ListOfShows list;
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
