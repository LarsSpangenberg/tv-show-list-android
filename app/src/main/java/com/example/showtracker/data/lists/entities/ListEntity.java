package com.example.showtracker.data.lists.entities;

import androidx.annotation.*;
import androidx.room.*;

import com.example.showtracker.data.common.*;

import java.io.*;
import java.util.*;

@Entity(tableName = "lists")
public class ListEntity implements Serializable, listItem {
    @Ignore
    public static final String LIST_ID = "LIST_ID";

    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public int position;

    public ListEntity(String name) {
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
