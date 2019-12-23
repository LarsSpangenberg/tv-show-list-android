package com.example.showtracker.utils;

import android.os.*;
import android.util.*;

import com.example.showtracker.data.*;
import com.example.showtracker.data.entities.*;

import java.util.*;

public class DataBaseInitializer {
    private static final String TAG = "DataBaseInitializer";

    public static void populateDb(final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    private static ListOfShows addList(final AppDatabase db, String name) {
        ListOfShows list = new ListOfShows(name);
        if (name.equals("All Shows")) {
             db.showDao().handleAllShowsSyncToList(list.id);
        }
        long listId = db.listDao().addList(list);

        Log.d(TAG, "addList: adding " + list.name + " with id " + listId);
        return list;
    }

    private static Show addShow(final AppDatabase db, String title, ListOfShows list) {
        Log.d(TAG, "addShow: attempting to addTag show with title " + title);
        Show show = new Show(title);
        db.showDao().addShowWithListId(show, list.id);
        Log.d(
            TAG,
            "addShow: added " + show.title + " with id " + show.id + " to list "
            + list.name + " with id " + list.id
        );
        return show;
    }

    private static void addShowToList(final AppDatabase db, Show show, ListOfShows list) {

        ListShowJoin join = new ListShowJoin(list.id, show.id);
        db.showDao().addShowToList(join);
        Log.d(TAG, "saveShowsLists: new join : " + join.toString());
        Log.d(TAG, "saveShowsLists: " + show.title + " added to list " + list.name);

    }

    private static void addTag(final AppDatabase db, String tagName) {
        Tag newTag = new Tag(tagName);
        db.tagDao().addTag(newTag);
        Log.d(TAG, "addTag: created new Tag " + newTag.name + " with the id " + newTag.id);
    }

    private static void populateWithTestData(AppDatabase db) {
        db.showDao().deleteAllShowRelated();
        db.listDao().deleteAll();
        db.tagDao().deleteAll();
        Log.d(TAG, "populateWithTestData: db reset");

        // this call needs to be done after db reset but before all other entries are created
        // uncomment in AppDatabase when the Initiallizer isn't needed anymore
        // can't run it here since it's also wrapped in another AsyncTask
//        db.populateInitialData();

        // list with name All Shows is synced to all shows
//        ListOfShows allShowsList = addList(db, "All Shows");

        ListOfShows animeList = addList(db, "Anime");
        ListOfShows comedyList = addList(db, "Comedy");
        ListOfShows actionList = addList(db, "Action Shows");
        ListOfShows superHeroList = addList(db, "Superhero Shows");
        Log.d(TAG, "populateWithTestData: all lists added");
        Log.d(TAG, "populateWithTestData: ==============================");

        Show naruto = addShow(db, "Black Clover", animeList);
        Show steinsGate = addShow(db, "Steins Gate", animeList);
        Show fireForce = addShow(db, "Fire Force", animeList);
        Show gintama = addShow(db, "Gintama", animeList);
        Log.d(TAG, "populateWithTestData: all shows added");
        Log.d(TAG, "populateWithTestData: ====================================");

        addShowToList(db, gintama, comedyList);
        addShowToList(db, naruto, actionList);
        Log.d(TAG, "populateWithTestData: added some shows to other lists");
        Log.d(TAG, "populateWithTestData: ==========================================");

        addTag(db, "seasonal");
        addTag(db, "cheesy");
        addTag(db, "Wholesome");
        Log.d(TAG, "populateWithTestData: created some tags");
        Log.d(TAG, "populateWithTestData: ============================================");




//        Testing deleting multiple lists
//        List<String> listsToDelete = new ArrayList<>();
//        listsToDelete.addTag(animeList.id);
//        listsToDelete.addTag(actionList.id);
//        listsToDelete.addTag(comedyList.id);
//        listsToDelete.addTag(superHeroList.id);
//
//        db.listDao().deleteListsWithShows(listsToDelete);
//        Log.d(TAG, "populateWithTestData: shows deleted the ones left are " + db.showDao()
//        .getAllEditableLists().toString());
//
        Log.d(TAG, "populateWithTestData: joins are: ");
        List<ListShowJoin> joins = db.listDao().getAllListShowJoins();
        for (ListShowJoin join : joins) {
            Log.d(TAG,
                  "populateWithTestData: " + db.showDao().findById(join.showId).title + " to " + db
                      .listDao()
                      .findById(join.listId).name
            );
        }

    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final AppDatabase db;

        PopulateDbAsync(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            populateWithTestData(db);
            return null;
        }
    }
}
