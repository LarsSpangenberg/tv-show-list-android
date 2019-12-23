package com.example.showtracker.data.entities;

import androidx.annotation.*;
import androidx.room.*;

import java.io.*;
import java.util.*;

@Entity(tableName = "lists")
public class ListOfShows implements Serializable, listItem {
    @Ignore
    public static final String LIST_ID = "LIST_ID";

    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public int position;

    public ListOfShows(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    @NonNull
    @Override
    public String getId() {
        return this.id;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
