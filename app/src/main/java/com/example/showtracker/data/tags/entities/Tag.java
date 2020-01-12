package com.example.showtracker.data.tags.entities;

import androidx.annotation.*;
import androidx.room.*;

import com.example.showtracker.data.common.interfaces.*;
import com.example.showtracker.screens.common.utils.*;

import java.io.*;
import java.util.*;

@Entity(tableName = "tags")
public class Tag implements Serializable, ListItem, ListItemSortHandler.Sortable {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public int position;

    public Tag(String name) {
        id = UUID.randomUUID().toString();
        this.name = name;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
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
        return name;
    }
}
