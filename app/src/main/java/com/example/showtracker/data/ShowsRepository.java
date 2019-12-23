package com.example.showtracker.data;

import android.app.*;
import android.os.*;

import androidx.annotation.*;
import androidx.lifecycle.*;

import com.example.showtracker.common.*;
import com.example.showtracker.data.dao.*;
import com.example.showtracker.data.entities.*;
import com.example.showtracker.common.utils.*;

import java.util.*;

public class ShowsRepository {
    private static final String TAG = "ShowsRepository";
    private static volatile ShowsRepository INSTANCE;
    private ShowDao showDao;

    private ShowsRepository(@NonNull Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.showDao = db.showDao();

    }

    public static ShowsRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ShowsRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ShowsRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<ShowWithTags>> getShowsInList(String listId, ShowsListFilters filters) {
//        List<Show.Status> statusFilters = filters.getStatusFilters();
        List<Integer> statusFilters = filters.getFilteredStatusCodes();
        List<String> tagFilters = filters.getFilteredTagIds();

        if (!statusFilters.isEmpty()) {
            if (!tagFilters.isEmpty()) {
                return this.showDao.getShowsInListFiltered(listId, statusFilters, tagFilters);
            }
            return this.showDao.getShowsInListByStatus(listId, statusFilters);
        } else if (!tagFilters.isEmpty()) {
            return this.showDao.getShowsInListByTag(listId,tagFilters);
        }
        return getShowsInList(listId);
    }

    public LiveData<List<ShowWithTags>> getShowsInList(String listId) {
        return this.showDao.getShowsInList(listId);
    }

    public LiveData<ShowDetails> getShowDetails(String showId) {
        return this.showDao.getShowDetails(showId);
    }

    public void createNewShow(Show show, String listId) {
        new UpdateOrInsertAsync(this.showDao, listId).execute(show);
    }

    public void updateShow(Show show) {
        new UpdateOrInsertAsync(this.showDao).execute(show);
    }

//    public void deleteShowsFromList(String listId, Show... shows) {
//        new RemoveFromListAsync(this.showDao, listId).execute(shows);
//    }
//
//    public void deleteShowsFromList(String listId, List<Show> shows) {
//        deleteShowsFromList(listId, shows.toArray(new Show[0]));
//    }

    public void deleteShowFromList(String listId, List<String> showIds) {
        new RemoveFromListAsync(this.showDao,listId).execute(showIds.toArray(new String[0]));
    }

    public void saveShowsLists(List<String> listIds, String showId) {
        int listCount = listIds.size();
        String[] listIdArray = new String[listCount];
        for (int i = 0; i < listCount; i++) {
            listIdArray[i] = listIds.get(i);
        }

        new SaveShowsListsAsync(this.showDao, showId).execute(listIdArray);
    }

    public void saveShowsTags(List<String> tagIds, String showId) {
        int tagCount = tagIds.size();
        String[] tagJoinArray = new String[tagCount];
        for (int i = 0; i < tagCount; i++) {
            tagJoinArray[i] = tagIds.get(i);
        }

        new SaveShowTagsAsync(this.showDao, showId).execute(tagJoinArray);
    }

    public void moveShow(Show toMove, Show target) {
        Show[] showsToMove = new Show[2];
        showsToMove[0] = toMove;
        showsToMove[1] = target;
        new MoveShowPositionAsync(this.showDao).execute(showsToMove);
    }

    private static class UpdateOrInsertAsync extends AsyncTask<Show, Void, Void> {

        private ShowDao showDao;
        private String listId;

        UpdateOrInsertAsync(ShowDao showDao) {
            this.showDao = showDao;
        }

        UpdateOrInsertAsync(ShowDao showDao, String listId) {
            this.showDao = showDao;
            this.listId = listId;
        }

        @Override
        protected Void doInBackground(Show... shows) {
            if (listId == null) {
                this.showDao.updateShow(shows[0]);
            } else {
                this.showDao.addShowWithListId(shows[0], this.listId);
            }
            return null;
        }
    }

    private static class RemoveFromListAsync extends AsyncTask<String, Void, Void> {
        private ShowDao showDao;
        private String listId;

        RemoveFromListAsync(ShowDao showDao, String listId) {
            this.showDao = showDao;
            this.listId = listId;
        }

        @Override
        protected Void doInBackground(String... showIds) {
            this.showDao.deleteShowFromList(this.listId, showIds);
            return null;
        }
    }

    private static class SaveShowTagsAsync extends AsyncTask<String, Void, Void> {
        private ShowDao showDao;
        private String showId;

        SaveShowTagsAsync(ShowDao showDao, String showId) {
            this.showDao = showDao;
            this.showId = showId;
        }

        @Override
        protected Void doInBackground(String... tagIds) {
            this.showDao.saveShowsTags(this.showId, tagIds);
            return null;
        }
    }

    private static class SaveShowsListsAsync extends AsyncTask<String, Void, Void> {
        private ShowDao showDao;
        private String showId;

        SaveShowsListsAsync(ShowDao showDao, String showId) {
            this.showDao = showDao;
            this.showId = showId;
        }

        @Override
        protected Void doInBackground(String... listIds) {
            this.showDao.saveShowsLists(this.showId, listIds);
            return null;
        }
    }

    private static class MoveShowPositionAsync extends AsyncTask<Show, Void, Void> {
        private ShowDao showDao;

        MoveShowPositionAsync(ShowDao showDao) {
            this.showDao = showDao;
        }

        @Override
        protected Void doInBackground(Show... showsToMove) {
            if (showsToMove.length == 2) {
                this.showDao.moveShowPosition(showsToMove[0], showsToMove[1]);
            }
            return null;
        }
    }

}
