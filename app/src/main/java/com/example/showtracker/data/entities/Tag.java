package com.example.showtracker.data.entities;

import androidx.annotation.*;
import androidx.room.*;

import java.io.*;
import java.util.*;

@Entity(tableName = "tags")
public class Tag implements Serializable, listItem {
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

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
