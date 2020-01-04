package com.example.showtracker.data.lists;

import androidx.lifecycle.*;

import com.example.showtracker.data.*;
import com.example.showtracker.data.lists.entities.*;

import java.util.*;
import java.util.concurrent.*;

public class ListsRepository {
    private ListDao listDao;
    private ThreadPoolExecutor backgroundThread;

    public ListsRepository(AppDatabase db, ThreadPoolExecutor backgroundThread) {
        this.listDao = db.listDao();
        this.backgroundThread = backgroundThread;
    }

    public LiveData<List<ListEntity>> getAllEditableLists() {
        return listDao.getAllEditableLists();
    }

    public LiveData<List<ListWithShows>> getAllListsWithShowId() {
        return listDao.getAllListsWithShows();
    }

    public void insert(final ListEntity list) {
        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                listDao.addList(list);
            }
        });
    }

    public void update(final ListEntity list) {
        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                listDao.update(list);
            }
        });
    }

    public void delete(List<String> listIds) {
        final String[] idsArray = listIds.toArray(new String[0]);
        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                listDao.deleteListsWithShows(idsArray);
            }
        });
    }

    public void moveList(final ListEntity toMove, final ListEntity target) {
        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                listDao.moveListPosition(toMove, target);
            }
        });
    }
}
