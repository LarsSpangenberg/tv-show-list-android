package com.example.showtracker.viewmodels;

import android.app.*;
import android.util.*;

import androidx.annotation.*;
import androidx.lifecycle.*;
import androidx.lifecycle.Observer;

import com.example.showtracker.data.*;
import com.example.showtracker.data.entities.*;
import com.example.showtracker.utils.*;

import java.util.*;

public class ShowsListViewModel extends AndroidViewModel {
    private static final String TAG = "ShowsListViewModel";
    private ShowsRepository showsRepository;
    private TagsRepository tagsRepository;
    private LiveData<List<ShowWithTags>> shows;
    private LiveData<List<Tag>> allTags;
    private MediatorLiveData<ShowsListData> showsListData;
    private String listId;


    public ShowsListViewModel(@NonNull Application application, String listId) {
        super(application);
        Log.d(TAG, "ShowsListViewModel: passed id " + listId + " to VM");
        this.listId = listId;
        this.showsRepository = ShowsRepository.getInstance(application);
        this.tagsRepository = TagsRepository.getInstance(application);
        this.allTags = this.tagsRepository.getAll();

        setShowsListData();
    }

    public void setShowsListData(ShowsListFilters filters) {
        this.shows = this.showsRepository.getShowsInList(this.listId, filters);
        setShowsListLiveData();
    }

    private void setShowsListData() {
        this.shows = this.showsRepository.getShowsInList(this.listId);
        setShowsListLiveData();
    }

    private void setShowsListLiveData() {
        this.showsListData = new MediatorLiveData<>();
        this.showsListData.addSource(this.shows, new Observer<List<ShowWithTags>>() {
            @Override
            public void onChanged(List<ShowWithTags> showsWithTags) {
                showsListData.setValue(combineShowsListData(shows, allTags));
            }
        });
        this.showsListData.addSource(this.allTags, new Observer<List<Tag>>() {
            @Override
            public void onChanged(List<Tag> tags) {
                showsListData.setValue(combineShowsListData(shows, allTags));
            }
        });
    }

    public LiveData<ShowsListData> getShowsListData() {
        return this.showsListData;
    }

//    public List<Tag> getAllTags() {
//        return this.allTags.getValue();
//    }

    public void deleteShowsFromList(String listId, List<String> showIds) {
        this.showsRepository.deleteShowFromList(listId, showIds);
    }

    public void moveShow(Show toMove, Show target) {
        this.showsRepository.moveShow(toMove, target);
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
