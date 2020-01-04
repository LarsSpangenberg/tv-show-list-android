package com.example.showtracker.data.tags.entities;

import androidx.annotation.*;
import androidx.room.*;

import com.example.showtracker.data.common.*;

import java.io.*;
import java.util.*;

@Entity(tableName = "tags")
public class Tag implements Serializable, ListItem {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public int position;

    public Tag(String name) {
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
