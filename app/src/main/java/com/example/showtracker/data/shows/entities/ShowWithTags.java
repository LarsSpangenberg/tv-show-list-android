package com.example.showtracker.data.shows.entities;

import androidx.annotation.*;
import androidx.room.*;

import com.example.showtracker.data.common.joins.*;
import com.example.showtracker.screens.common.utils.*;

import java.util.*;

public class ShowWithTags implements ListItemSortHandler.Sortable {
    @Embedded
    public Show show;

    @Relation(
        parentColumn = "id",
        entityColumn = "showId",
        entity = ShowTagJoin.class,
        projection = "tagId"
    )
    public List<String> tagIds;

    @Override
    public String getName() {
        return getShow().title;
    }

    @Override
    public int getPosition() {
        return getShow().getPosition();
    }

    public Show getShow() {
        return show;
    }

    public String getId() {
        return getShow().getId();
    }

    public int getSeason() {
        return getShow().season;
    }

    public int getEpisode() {
        return getShow().episode;
    }

    public Show.Status getStatus() {
        return getShow().status;
    }

    public String getComment() {
        return getShow().comment;
    }

    public List<String> getTags() {
        return tagIds;
    }

    @Ignore

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
