package com.example.showtracker.screens.common.utils;

import android.content.*;

import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.data.tags.entities.*;

import java.util.*;

public class ListItemSortHandler<T extends ListItemSortHandler.Sortable> {
    public static final String LIST_SORT_MODE = "LIST_SORT_MODE";
    public static final String SHOW_SORT_MODE = "SHOW_SORT_MODE";
    public static final String TAG_SORT_MODE = "TAG_SORT_MODE";
    public static final int SORT_BY_NAME = 0;
    public static final int SORT_BY_CUSTOM = 1;
    private SharedPreferences prefs;
    private List<T> items = new ArrayList<>();

    public interface Sortable {
        String getName();
        int getPosition();
    }

    public ListItemSortHandler(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public List<T> getSortedItems() {
        if (items.isEmpty()) return items;

        return getSortedItems(prefs.getInt(getSortModeKey(), SORT_BY_CUSTOM));
    }

    public List<T> getSortedItems(int sortBy) {
        if (items.isEmpty()) return items;

        SharedPreferences.Editor prefsEditor = prefs.edit();
        Comparator<T> comparator = null;
        switch (sortBy) {
            case SORT_BY_NAME:
                prefsEditor.putInt(getSortModeKey(), SORT_BY_NAME).apply();
                comparator = new Comparator<T>() {
                    @Override
                    public int compare(T o1, T o2) {
                        String name1 = o1.getName();
                        String name2 = o2.getName();
                        return name1.compareToIgnoreCase(name2);
                    }
                };
                break;
            case SORT_BY_CUSTOM:
                prefsEditor.putInt(getSortModeKey(), SORT_BY_CUSTOM).apply();
                comparator = new Comparator<T>() {
                    @Override
                    public int compare(T o1, T o2) {
                        int position1 = o1.getPosition();
                        int position2 = o2.getPosition();
                        return position1 - position2;
                    }
                };
                break;
        }

        if (comparator != null) {
            Collections.sort(items, comparator);
        }

        return items;
    }

    private String getSortModeKey() {
        Class<? extends Sortable> itemClass = items.get(0).getClass();
        if (itemClass == ListWithShows.class) {
            return LIST_SORT_MODE;
        } else if (itemClass == ShowWithTags.class) {
            return SHOW_SORT_MODE;
        } else if (itemClass == Tag.class) {
            return TAG_SORT_MODE;
        } else {
            throw new RuntimeException(
                "The Sort Handler cannot handle items of type " + itemClass.toString() + " until"
                + "\nnecessary changes have been made. if using a new Sortable type, adjust the "
                + "\nListItemSortHandler accordingly and specify a new sort mode"
            );
        }
    }
}
