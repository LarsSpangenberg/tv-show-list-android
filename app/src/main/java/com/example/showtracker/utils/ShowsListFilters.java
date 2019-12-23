package com.example.showtracker.utils;

import com.example.showtracker.data.entities.*;

import java.util.*;

public class ShowsListFilters {
    private List<Show.Status> statusFilters = new ArrayList<>();
    private List<String> tagIds = new ArrayList<>();

    public List<String> getFilteredTagIds() {
        return tagIds;
    }

    public List<Show.Status> getStatusFilters() {
        return this.statusFilters;
    }

    public List<Integer> getFilteredStatusCodes() {
        List<Integer> statusFilterCodes = new ArrayList<>();
        for (Show.Status status : this.statusFilters) {
            statusFilterCodes.add(status.getCode());
        }
        return statusFilterCodes;
    }

    public void handleStatusFilter(Show.Status status) {
        if (statusFilters.contains(status)) {
            this.statusFilters.remove(status);
        } else {
            this.statusFilters.add(status);
        }
    }

    public void handleTagFilter(String tagId) {
        if (this.tagIds.contains(tagId)) {
            this.tagIds.remove(tagId);
        } else {
            this.tagIds.add(tagId);
        }
    }
}
