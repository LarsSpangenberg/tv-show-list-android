package com.example.showtracker.data;

import android.app.*;
import android.os.*;

import androidx.lifecycle.*;

import com.example.showtracker.common.*;
import com.example.showtracker.data.dao.*;
import com.example.showtracker.data.entities.*;

import java.util.*;

public class ListsRepository {
    private static volatile ListsRepository INSTANCE;
    private ListDao listDao;

    private ListsRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.listDao = db.listDao();
    }

    public static ListsRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ListsRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ListsRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<ListOfShows>> getAllEditableLists() {
        return this.listDao.getAllEditableLists();
    }

    public LiveData<List<ListWithShows>> getAllListsWithShowId() {
        return this.listDao.getAllListsWithShows();
    }

    public void insert(ListOfShows list) {
        new InsertAsync(this.listDao).execute(list);
    }

    public void update(ListOfShows list) {
        new UpdateAsync(this.listDao).execute(list);
    }

//    public void rename(String listId, String newName) {
//        String[] listData = new String[2];
//        listData[0] = listId;
//        listData[1] = newName;
//        new RenameAsync(this.listDao).execute(listData);
//    }

//    public void delete(List<ListOfShows> lists) {
//        int listCount = lists.size();
//        String[] listIdsArray = new String[listCount];
//
//        for (int i = 0; i < listCount; i++) {
//            listIdsArray[i] = lists.get(i).id;
//        }
//
//        new DeleteAsync(this.listDao).execute(listIdsArray);
//    }

    public void delete(List<String> listIds) {
        new DeleteAsync(this.listDao).execute(listIds.toArray(new String[0]));
    }

    public void moveList(ListOfShows toMove, ListOfShows target) {
        ListOfShows[] listsToMove = new ListOfShows[2];
        listsToMove[0] = toMove;
        listsToMove[1] = target;
        new MoveListPositionAsync(this.listDao).execute(listsToMove);
    }

    private static class InsertAsync extends AsyncTask<ListOfShows, Void, Void> {
        private ListDao listDao;

        InsertAsync(ListDao listDao) {
            this.listDao = listDao;
        }

        @Override
        protected Void doInBackground(ListOfShows... list) {
            this.listDao.addList(list[0]);
            return null;
        }
    }

    private static class UpdateAsync extends AsyncTask<ListOfShows, Void, Void> {
        private ListDao listDao;

        public UpdateAsync(ListDao listDao) {
            this.listDao = listDao;
        }

        @Override
        protected Void doInBackground(ListOfShows... list) {
            this.listDao.update(list[0]);
            return null;
        }
    }

//    private static class RenameAsync extends AsyncTask<String, Void, Void> {
//        private ListDao listDao;
//
//        RenameAsync(ListDao listDao) {
//            this.listDao = listDao;
//        }
//
//        @Override
//        protected Void doInBackground(String... data) {
//            this.listDao.update(data[0], data[1]);
//            return null;
//        }
//    }

    private static class DeleteAsync extends AsyncTask<String, Void, Void> {
        private ListDao listDao;

        DeleteAsync(ListDao listDao) {
            this.listDao = listDao;
        }

        @Override
        protected Void doInBackground(String... listIds) {
            this.listDao.deleteListsWithShows(listIds);
            return null;
        }
    }

    private static class MoveListPositionAsync extends AsyncTask<ListOfShows, Void, Void> {
        private ListDao listDao;

        MoveListPositionAsync(ListDao listDao) {
            this.listDao = listDao;
        }

        @Override
        protected Void doInBackground(ListOfShows... listsToMove) {
            if (listsToMove.length == 2) {
                this.listDao.moveListPostion(listsToMove[0], listsToMove[1]);
            }
            return null;
        }
    }

}
