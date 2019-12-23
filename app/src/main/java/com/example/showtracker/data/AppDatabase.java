package com.example.showtracker.data;

import android.content.*;
import android.os.*;
import android.util.*;

import androidx.annotation.*;
import androidx.room.*;
import androidx.sqlite.db.*;

import com.example.showtracker.data.dao.*;
import com.example.showtracker.data.entities.*;
import com.example.showtracker.utils.*;

@Database(
    entities = {
        ListOfShows.class,
        Show.class,
        Tag.class,
        ListShowJoin.class,
        ShowTagJoin.class,
        ListsSyncedToAllShows.class
    },
    version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";
    private static final String DB_NAME = "show_tracker.db";
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                        .databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DB_NAME
                        )
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onOpen(db);
                                DataBaseInitializer.populateDb(getInstance(context));
                                getInstance(context).populateInitialData();
                            }
                        })
                        .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract ListDao listDao();

    public abstract ShowDao showDao();

    public abstract TagDao tagDao();

    private void populateInitialData() {
        ListOfShows allShowsList = new ListOfShows("All Shows");
        Tag favoriteTag = new Tag("Favorite");
        Log.d(TAG, "populateInitialData: " + allShowsList.toString());
        new DefaultInsertAsync(
            INSTANCE.listDao(),
            INSTANCE.showDao(),
            INSTANCE.tagDao(),
            allShowsList,
            favoriteTag
        ).execute();
    }

    private static class DefaultInsertAsync extends AsyncTask<Void, Void, Void> {
        private ListDao listDao;
        private ShowDao showDao;
        private TagDao tagDao;
        private ListOfShows list;
        private Tag tag;

        DefaultInsertAsync(
            ListDao listDao,
            ShowDao showDao,
            TagDao tagDao,
            ListOfShows list,
            Tag tag
        ) {
            this.listDao = listDao;
            this.showDao = showDao;
            this.tagDao = tagDao;
            this.list = list;
            this.tag = tag;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            this.tagDao.addTag(this.tag);
            this.listDao.addList(this.list);
            if (this.list.name.equals("All Shows")) {
                this.showDao.handleAllShowsSyncToList(this.list.id);
            }
            return null;
        }
    }
}
