package com.example.showtracker.data.lists.entities;

import androidx.annotation.*;
import androidx.room.*;

import static androidx.room.ForeignKey.*;

@Entity(
    tableName = "lists_synced_to_all_shows",
    primaryKeys = {"listId"},
    indices = {@Index("listId")},
    foreignKeys = {
        @ForeignKey(
            entity = ListEntity.class,
            parentColumns = "id",
            childColumns = "listId",
            onDelete = CASCADE
        )
    })
public class ListsSyncedToAllShows {
    @NonNull
    public String listId;
}
