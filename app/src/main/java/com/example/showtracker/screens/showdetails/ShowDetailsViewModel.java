package com.example.showtracker.screens.showdetails;

import androidx.annotation.*;
import androidx.lifecycle.Observer;
import androidx.lifecycle.*;

import com.example.showtracker.common.*;
import com.example.showtracker.common.dependencyinjection.application.*;
import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.data.shows.*;
import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.data.tags.entities.*;

import java.util.*;

public class ShowDetailsViewModel extends ViewModel {
    private ShowsRepository showsRepository;
    private LiveData<List<ListEntity>> allLists;
    private LiveData<List<Tag>> allTags;
    private LiveData<ShowDetails> showDetails;
    private MediatorLiveData<ShowDetailsData> showDetailsComplete;

    public ShowDetailsViewModel(
        @NonNull MyApplication application,
        String showId
    ) {
        ApplicationComponent appComponent = application.getApplicationComponent();

        showsRepository = appComponent.getShowsRepository();
        showDetails = showsRepository.getShowDetails(showId);

        allLists = appComponent.getListsRepository().getAllEditableLists();
        allTags = appComponent.getTagsRepository().getAll();

        setDetailsLiveData(showId);
    }

    public MediatorLiveData<ShowDetailsData> getAllShowDetails() {
        return this.showDetailsComplete;
    }

    public void createNewShow(Show show, List<String> listIds, List<String> tagIds) {
        this.showsRepository.createNewShow(show, listIds.get(0));
        if (listIds.size() > 1) {
            this.showsRepository.saveShowsLists(listIds, show.id);
        }
        if (tagIds.size() > 0) {
            this.showsRepository.saveShowsTags(tagIds, show.id);
        }
    }

    public void updateShow(Show show) {
        this.showsRepository.updateShow(show);
    }

//    public void saveShowsLists(String showId, String listId) {
//        this.showsRepository.addShowToList(listId, showId);
//    }

    public void saveShowsLists(String showId, List<String> listIds) {
        this.showsRepository.saveShowsLists(listIds, showId);
    }

//    public void deleteShowsFromList(Show show, String listId) {
//        this.showsRepository.deleteShowsFromList(listId, show);
//    }

    public void saveShowsTags(String showId, List<String> tagIds) {
        this.showsRepository.saveShowsTags(tagIds, showId);
    }

    public void setDetailsLiveData(final String showId) {
        // if no showId was passed then a new show is being created, access to all tags and lists is
        // still needed for ShowDetailsActivity UI, use this method to reinitialize LiveData of
        // newly created Show with its id
        this.showDetailsComplete = new MediatorLiveData<>();
        if (showId != null) {
            this.showDetailsComplete.addSource(showDetails, new Observer<ShowDetails>() {
                @Override
                public void onChanged(ShowDetails details) {
                    showDetailsComplete.setValue(combineDetails(showDetails, allLists, allTags));
                }
            });
        }
        this.showDetailsComplete.addSource(allLists, new Observer<List<ListEntity>>() {
            @Override
            public void onChanged(List<ListEntity> lists) {
                if (showId != null) {
                    showDetailsComplete.setValue(combineDetails(showDetails, allLists, allTags));
                } else {
                    showDetailsComplete.setValue(combineDetails(allLists, allTags));
                }
            }
        });
        this.showDetailsComplete.addSource(allTags, new Observer<List<Tag>>() {
            @Override
            public void onChanged(List<Tag> tags) {
                if (showId != null) {
                    showDetailsComplete.setValue(combineDetails(showDetails, allLists, allTags));
                } else {
                    showDetailsComplete.setValue(combineDetails(allLists, allTags));
                }
            }
        });
    }

    private ShowDetailsData combineDetails(
        LiveData<ShowDetails> showDetails,
        LiveData<List<ListEntity>> allLists,
        LiveData<List<Tag>> allTags
    ) {
        ShowDetails sd = showDetails.getValue();
        ShowDetailsData data = combineDetails(allLists, allTags);

        if (sd != null && data != null) {
            data.show = sd;
            return data;
        }
        return null;
    }

    private ShowDetailsData combineDetails(
        LiveData<List<ListEntity>> allLists,
        LiveData<List<Tag>> allTags
    ) {
        List<ListEntity> al = allLists.getValue();
        List<Tag> at = allTags.getValue();

        if (al != null && at != null) {
            return new ShowDetailsData(al, at);
        }
        return null;
    }

    public class ShowDetailsData {
        public ShowDetails show;
        public List<Tag> allTags;
        public List<ListEntity> allLists;

        ShowDetailsData(List<ListEntity> allLists, List<Tag> allTags) {
            this.show = null;
            this.allLists = allLists;
            this.allTags = allTags;
        }
    }
}
