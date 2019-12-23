package com.example.showtracker.viewmodels;

import android.app.*;

import androidx.annotation.*;
import androidx.lifecycle.Observer;
import androidx.lifecycle.*;

import com.example.showtracker.data.*;
import com.example.showtracker.data.entities.*;

import java.util.*;

public class ShowDetailsViewModel extends AndroidViewModel {
    private ShowsRepository showsRepository;
    private ListsRepository listsRepository;
    private TagsRepository tagsRepository;
    private LiveData<List<ListOfShows>> allLists;
    private LiveData<List<Tag>> allTags;
    private LiveData<ShowDetails> showDetails;
    private MediatorLiveData<ShowDetailsData> showDetailsComplete;

    public ShowDetailsViewModel(@NonNull Application application, String showId) {
        super(application);
        this.showsRepository = ShowsRepository.getInstance(application);
        this.listsRepository = ListsRepository.getInstance(application);
        this.tagsRepository = TagsRepository.getInstance(application);
        this.showDetails = this.showsRepository.getShowDetails(showId);
        this.allLists = this.listsRepository.getAllEditableLists();
        this.allTags = this.tagsRepository.getAll();

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
        this.showDetailsComplete.addSource(allLists, new Observer<List<ListOfShows>>() {
            @Override
            public void onChanged(List<ListOfShows> lists) {
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
        LiveData<List<ListOfShows>> allLists,
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
        LiveData<List<ListOfShows>> allLists,
        LiveData<List<Tag>> allTags
    ) {
        List<ListOfShows> al = allLists.getValue();
        List<Tag> at = allTags.getValue();

        if (al != null && at != null) {
            return new ShowDetailsData(al, at);
        }
        return null;
    }

    public class ShowDetailsData {
        public ShowDetails show;
        public List<Tag> allTags;
        public List<ListOfShows> allLists;

        ShowDetailsData(List<ListOfShows> allLists, List<Tag> allTags) {
            this.show = null;
            this.allLists = allLists;
            this.allTags = allTags;
        }
    }
}
