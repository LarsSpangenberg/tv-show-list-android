package com.example.showtracker.data.entities;

import androidx.annotation.*;
import androidx.room.*;

import static androidx.room.ForeignKey.*;

@Entity(
    tableName = "lists_synced_to_all_shows",
    primaryKeys = {"listId"},
    indices = {@Index("listId")},
    foreignKeys = {
        @ForeignKey(
            entity = ListOfShows.class,
            parentColumns = "id",
            childColumns = "listId",
            onDelete = CASCADE
        )
    })
public class ListsSyncedToAllShows {
    @NonNull
    public String listId;
}
