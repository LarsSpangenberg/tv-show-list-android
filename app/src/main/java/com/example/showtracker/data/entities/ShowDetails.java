package com.example.showtracker.data.entities;

import androidx.room.*;

import java.util.*;

public class ShowDetails {
    @Embedded
    public Show show;

    @Relation(
        parentColumn = "id",
        entityColumn = "showId",
        entity = ShowTagJoin.class,
        projection = "tagId"
    )
    public List<String> tagIds;

    @Relation(
        parentColumn = "id",
        entityColumn = "showId",
        entity = ListShowJoin.class,
        projection = "listId"
    )
    public List<String> listIds;


}
