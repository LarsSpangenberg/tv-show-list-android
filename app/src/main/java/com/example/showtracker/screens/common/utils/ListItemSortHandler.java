package com.example.showtracker.screens.common.utils;

import android.content.*;

import com.example.showtracker.data.lists.entities.*;

import java.util.*;

public class ListItemSortHandler {
    public static final String LIST_SORT_MODE = "LIST_SORT_MODE";
    public static final String SHOW_SORT_MODE = "SHOW_SORT_MODE";
    public static final String TAG_SORT_MODE = "TAG_SORT_MODE";
    public static final int SORT_BY_NAME = 0;
    public static final int SORT_BY_CUSTOM = 1;

    public interface Sortable {
        String getName();
        int getPosition();
    }

    public static <T extends Sortable> void sortItems(SharedPreferences prefs, List<T> items) {
        if (items.isEmpty()) return;
        sortItems(
            prefs,
            prefs.getInt(getSortModeKey(items.get(0).getClass()), SORT_BY_CUSTOM),
            items
        );
    }

    public static <T extends Sortable> void sortItems(
        SharedPreferences prefs,
        int sortBy,
        List<T> items
    ) {
        if (items.isEmpty()) return;

        SharedPreferences.Editor prefsEditor = prefs.edit();
        Comparator<T> comparator = null;
        String sortModeKey = getSortModeKey(items.get(0).getClass());
        switch (sortBy) {
            case SORT_BY_NAME:
                prefsEditor.putInt(sortModeKey, SORT_BY_NAME).apply();
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
                prefsEditor.putInt(sortModeKey, SORT_BY_CUSTOM).apply();
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
    }

    private static String getSortModeKey(Class<? extends Sortable> itemClass) {
        if (itemClass == ListWithShows.class) {
            return LIST_SORT_MODE;
        } else {
            throw new RuntimeException(
                "The Sort Handler cannot handle items of type " + itemClass.toString() + " until"
                + "\nnecessary changes have been made. if using a new Sortable type, adjust the "
                + "\nListItemSortHandler accordingly and specify a new sort mode"
            );
        }
    }
}
