package com.example.showtracker.screens.common.utils;

import com.example.showtracker.data.shows.entities.*;

import java.util.*;

public class ShowsListFilters {
    private List<Show.Status> statusFilters = new ArrayList<>();
    private List<String> tagIds = new ArrayList<>();

    public List<String> getFilteredTagIds() {
        return tagIds;
    }

    public List<Show.Status> getStatusFilters() {
        return statusFilters;
    }

    public List<Integer> getFilteredStatusCodes() {
        List<Integer> statusFilterCodes = new ArrayList<>();
        for (Show.Status status : statusFilters) {
            statusFilterCodes.add(status.getCode());
        }
        return statusFilterCodes;
    }

    public void handleStatusFilter(Show.Status status) {
        if (statusFilters.contains(status)) {
            statusFilters.remove(status);
        } else {
            statusFilters.add(status);
        }
    }

    public void handleTagFilter(String tagId) {
        if (tagIds.contains(tagId)) {
            tagIds.remove(tagId);
        } else {
            tagIds.add(tagId);
        }
    }
}
