package com.example.showtracker.screens.showslist;

import android.util.*;

import androidx.lifecycle.Observer;
import androidx.lifecycle.*;

import com.example.showtracker.common.*;
import com.example.showtracker.common.dependencyinjection.application.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.data.shows.*;
import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.data.tags.entities.*;

import java.util.*;

public class ShowsListViewModel extends ViewModel {
    private static final String TAG = "ShowsListViewModel";
    private ShowsRepository showsRepository;
    private LiveData<List<ShowWithTags>> shows;
    private LiveData<List<Tag>> allTags;
    private MediatorLiveData<ShowsListData> showsListData;
    private String listId;


    public ShowsListViewModel(CustomApplication application, String listId) {
        Log.d(TAG, "ShowsListViewModel: passed id " + listId + " to VM");
        this.listId = listId;

        ApplicationComponent appComponent = application.getApplicationComponent();
        showsRepository = appComponent.getShowsRepository();
        allTags = appComponent.getTagsRepository().getAll();

        setShowsListData();
    }

    public void setShowsListData(ShowsListFilters filters) {
        shows = showsRepository.getShowsInList(listId, filters);
        setShowsListLiveData();
    }

    private void setShowsListData() {
        shows = showsRepository.getShowsInList(listId);
        setShowsListLiveData();
    }

    private void setShowsListLiveData() {
        showsListData = new MediatorLiveData<>();
        showsListData.addSource(shows, new Observer<List<ShowWithTags>>() {
            @Override
            public void onChanged(List<ShowWithTags> showsWithTags) {
                showsListData.setValue(combineShowsListData(shows, allTags));
            }
        });
        showsListData.addSource(allTags, new Observer<List<Tag>>() {
            @Override
            public void onChanged(List<Tag> tags) {
                showsListData.setValue(combineShowsListData(shows, allTags));
            }
        });
    }

    public LiveData<ShowsListData> getShowsListData() {
        return showsListData;
    }

//    public List<Tag> getAllTags() {
//        return this.allTags.getValue();
//    }

    public void deleteShowsFromList(String listId, List<String> showIds) {
        showsRepository.deleteShowFromList(listId, showIds);
    }

    public void moveShow(Show toMove, Show target) {
        showsRepository.moveShow(toMove, target);
    }

    private ShowsListData combineShowsListData(LiveData<List<ShowWithTags>> shows, LiveData<List<Tag>> allTags) {
        Log.d(TAG, "combineShowsListData: starts");
        List<ShowWithTags> showsValue = shows.getValue();
        List<Tag> allTagsValue = allTags.getValue();

        if (showsValue != null && allTagsValue != null) {
            Log.d(TAG, "combineShowsListData: shows: " + showsValue.toString());
            Log.d(TAG, "combineShowsListData: tags: " + allTagsValue.toString());
            return new ShowsListData(showsValue, allTagsValue);
        }

        return null;
    }

    public class ShowsListData {
        public List<ShowWithTags> shows;
        public List<Tag> allTags;

        ShowsListData(List<ShowWithTags> shows, List<Tag> allTags) {
            this.shows = shows;
            this.allTags = allTags;
        }
    }
}
