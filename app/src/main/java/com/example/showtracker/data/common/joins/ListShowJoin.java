package com.example.showtracker.data.common.joins;

import androidx.annotation.*;
import androidx.room.*;

import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.data.shows.entities.*;

import static androidx.room.ForeignKey.CASCADE;

/**
 * delete shows first, lists after in that order.
 * resetSelection CASCADE on both ids makes sure you don't have to delete joins separately
 * before deleting the parent entity as well, otherwise the compiler will throw FK constraint error
 * to prevent orphaned records in the joining table
* */
@Entity(
    tableName = "list_show_join",
    primaryKeys = {"listId", "showId"},
    indices = {@Index("showId")},
    foreignKeys = {
        @ForeignKey(
            entity = ListEntity.class,
            parentColumns = "id",
            childColumns = "listId",
            onDelete = CASCADE
        ),
        @ForeignKey(
            entity = Show.class,
            parentColumns = "id",
            childColumns = "showId",
            onDelete = CASCADE
        )
    })
public class ListShowJoin {
    @NonNull
    public String listId;
    @NonNull
    public String showId;

    public ListShowJoin(@NonNull final String listId, @NonNull final String showId) {
        this.listId = listId;
        this.showId = showId;
    }

    @Ignore
    @NonNull
    @Override
    public String toString() {
        return "list id: " + listId + ", show id: " + showId;
    }
}
