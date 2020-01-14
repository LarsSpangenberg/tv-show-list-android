package com.example.showtracker.screens.common.screensnavigator;

import android.app.*;

import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.activities.*;
import com.example.showtracker.screens.showdetails.*;
import com.example.showtracker.screens.showslist.*;
import com.example.showtracker.screens.tags.*;

public class ScreensNavigator {
    private Activity activity;

    public ScreensNavigator(Activity activity) {
        this.activity = activity;
    }

    public void toShowsList(ListEntity list) {
        ShowsListActivity.start(list, activity);
    }

    public void toTagList() {
        TagListActivity.start(activity);
    }

    public void toEditShow(String showId) {
        ShowDetailsActivity.start(activity, showId);
    }

    public void toRenameList(ListEntity list) {
        SimpleAddEditActivity.startRenameList(activity, list);
    }

    public void toRenameTag(Tag tag) {
        SimpleAddEditActivity.startRenameTag(activity, tag);
    }

    public void toNewShow() {
        ShowDetailsActivity.start(activity);
    }

    public void toNewList() {
        SimpleAddEditActivity.startNewList(activity);
    }

    public void toNewTag() {
        SimpleAddEditActivity.startNewTag(activity);
    }

    public void navigateUp() {
        if (activity.onNavigateUp()) {
            return;
        }
        activity.onBackPressed();
    }
}
