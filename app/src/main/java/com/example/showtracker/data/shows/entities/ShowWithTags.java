package com.example.showtracker.data.shows.entities;

import androidx.room.*;

import com.example.showtracker.data.common.joins.*;

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
