package com.example.showtracker.common.dependencyinjection.application;

import android.app.*;

import androidx.annotation.*;
import androidx.room.*;
import androidx.sqlite.db.*;

import com.example.showtracker.common.*;
import com.example.showtracker.data.*;
import com.example.showtracker.data.common.*;
import com.example.showtracker.data.lists.*;
import com.example.showtracker.data.shows.*;
import com.example.showtracker.data.tags.*;

import javax.inject.*;

import dagger.*;

@Module
public class ApplicationModule {

    private final Application application;
    private AppDatabase database;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    AppDatabase getRoomDatabase() {
        if (database == null) {
            database = Room.databaseBuilder(
                application,
                AppDatabase.class,
                Constants.DB_NAME
            ).addCallback(new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    DataBaseInitializer.populateDb(database);
                    database.populateInitialData();
                }
            }).build();
        }
        return database;
    }

    @Singleton
    @Provides
    ListsRepository getListsRepository(AppDatabase db) {
        return new ListsRepository(db);
    }

    @Singleton
    @Provides
    ShowsRepository getShowsRepository(AppDatabase db) {
        return new ShowsRepository(db);
    }

    @Singleton
    @Provides
    TagsRepository getTagsRepository(AppDatabase db) {
        return new TagsRepository(db);
    }

    @Provides
    MyApplication getApplication() {
        return (MyApplication) application;
    }
}
