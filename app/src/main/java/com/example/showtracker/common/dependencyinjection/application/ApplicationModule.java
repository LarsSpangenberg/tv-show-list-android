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

import java.util.concurrent.*;

import javax.inject.*;

import dagger.*;

@Module
public class ApplicationModule {

    private final Application application;
    private AppDatabase database;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Singleton
    @Provides
    ThreadPoolExecutor getThreadPool() {
        return new ThreadPoolExecutor(
            3,
            Integer.MAX_VALUE,
            60L,
            TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>()
        );
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
    ListsRepository getListsRepository(AppDatabase db, ThreadPoolExecutor backgroundThread) {
        return new ListsRepository(db, backgroundThread);
    }

    @Singleton
    @Provides
    ShowsRepository getShowsRepository(AppDatabase db, ThreadPoolExecutor backgroundThread) {
        return new ShowsRepository(db, backgroundThread);
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
