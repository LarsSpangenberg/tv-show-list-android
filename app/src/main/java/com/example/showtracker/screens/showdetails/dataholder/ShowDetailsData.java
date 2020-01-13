package com.example.showtracker.screens.showdetails.dataholder;

import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.data.tags.entities.*;

import java.util.*;

public class ShowDetailsData {
    public ShowDetails show;
    public List<Tag> allTags;
    public List<ListEntity> allLists;

    public ShowDetailsData(List<ListEntity> allLists, List<Tag> allTags) {
        this.show = null;
        this.allLists = allLists;
        this.allTags = allTags;
    }
}
