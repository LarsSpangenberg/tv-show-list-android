package com.example.showtracker.data.common.joins;

import androidx.annotation.*;
import androidx.room.*;

import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.data.tags.entities.*;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
    tableName = "show_tag_join",
    primaryKeys = {"showId", "tagId"},
    indices = {@Index("tagId")},
    foreignKeys = {
        @ForeignKey(
            entity = Show.class,
            parentColumns = "id",
            childColumns = "showId",
            onDelete = CASCADE
        ),
        @ForeignKey(
            entity = Tag.class,
            parentColumns = "id",
            childColumns = "tagId",
            onDelete = CASCADE
        )
    }
)
public class ShowTagJoin {
    @NonNull
    public String tagId;
    @NonNull
    public String showId;

    public ShowTagJoin(@NonNull final String showId, @NonNull final String tagId) {
        this.tagId = tagId;
        this.showId = showId;
    }
}
