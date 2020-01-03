package com.example.showtracker.common.dependencyinjection.application;

import android.app.*;

import androidx.annotation.*;
import androidx.room.*;
import androidx.sqlite.db.*;

import com.example.showtracker.common.*;
import com.example.showtracker.common.utils.*;
import com.example.showtracker.data.common.*;
import com.example.showtracker.data.lists.*;
import com.example.showtracker.data.shows.*;
import com.example.showtracker.data.tags.*;

import javax.inject.*;

import dagger.*;

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Singleton
    @Provides
    AppDatabase getRoomDatabase() {
        return Room.databaseBuilder(
            application,
            AppDatabase.class,
            Constants.DB_NAME
        ).addCallback(new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
                DataBaseInitializer.populateDb(getRoomDatabase());
                getRoomDatabase().populateInitialData();
            }
        }).build();
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
