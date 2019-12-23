package com.example.showtracker.data.entities;

import androidx.room.*;

import java.util.*;

public class ShowWithTags {
    @Embedded
    public Show show;

    @Relation(
        parentColumn = "id",
        entityColumn = "showId",
        entity = ShowTagJoin.class,
        projection = "tagId"
    )
    public List<String> tagIds;

}
