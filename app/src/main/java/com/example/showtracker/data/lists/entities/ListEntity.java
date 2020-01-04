package com.example.showtracker.data.lists.entities;

import androidx.annotation.*;
import androidx.room.*;

import com.example.showtracker.data.common.*;

import java.io.*;
import java.util.*;

@Entity(tableName = "lists")
public class ListEntity implements Serializable, ListItem {
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

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
