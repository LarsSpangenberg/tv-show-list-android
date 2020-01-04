package com.example.showtracker.data.shows;

import androidx.lifecycle.*;

import com.example.showtracker.data.*;
import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.screens.common.utils.*;

import java.util.*;
import java.util.concurrent.*;

public class ShowsRepository {
    private ShowDao showDao;
    private ThreadPoolExecutor backgroundThread;

    public ShowsRepository(AppDatabase db, ThreadPoolExecutor backgroundThread) {
        this.showDao = db.showDao();
        this.backgroundThread = backgroundThread;
    }

    public LiveData<List<ShowWithTags>> getShowsInList(String listId, ShowsListFilters filters) {
        List<Integer> statusFilters = filters.getFilteredStatusCodes();
        List<String> tagFilters = filters.getFilteredTagIds();
        boolean filteringByStatus = !statusFilters.isEmpty();
        boolean filteringByTags = !tagFilters.isEmpty();

        if (filteringByStatus) {
            if (filteringByTags) {
                return showDao.getShowsInListFiltered(listId, statusFilters, tagFilters);
            }
            return showDao.getShowsInListByStatus(listId, statusFilters);
        } else if (filteringByTags) {
            return showDao.getShowsInListByTag(listId,tagFilters);
        }
        return getShowsInList(listId);
    }

    public LiveData<List<ShowWithTags>> getShowsInList(String listId) {
        return showDao.getShowsInList(listId);
    }

    public LiveData<ShowDetails> getShowDetails(String showId) {
        return showDao.getShowDetails(showId);
    }

    public void newShow(final Show show, final String listId) {
        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                showDao.addShowWithListId(show, listId);
            }
        });
    }

    public void updateShow(final Show show) {
        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                showDao.updateShow(show);
            }
        });
    }

    public void deleteShowFromList(final String listId, List<String> showIds) {
        final String[] showIdsArray = showIds.toArray(new String[0]);
        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                showDao.deleteShowFromList(listId, showIdsArray);
            }
        });
    }

    public void moveShow(final Show toMove, final Show target) {
        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                showDao.moveShowPosition(toMove, target);
            }
        });
    }

    public void saveShowsLists(final String showId, List<String> listIds) {
        int listCount = listIds.size();
        final String[] listIdsArray = new String[listCount];
        for (int i = 0; i < listCount; i++) {
            listIdsArray[i] = listIds.get(i);
        }

        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                showDao.updateShowsLists(showId, listIdsArray);
            }
        });
    }

    public void saveShowsTags(final String showId, List<String> tagIds) {
        int tagCount = tagIds.size();
        final String[] tagIdsArray = new String[tagCount];
        for (int i = 0; i < tagCount; i++) {
            tagIdsArray[i] = tagIds.get(i);
        }

        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                showDao.updateShowsTags(showId, tagIdsArray);
            }
        });
    }
}
