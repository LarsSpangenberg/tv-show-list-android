package com.example.showtracker.data.tags;

import androidx.lifecycle.*;

import com.example.showtracker.data.*;
import com.example.showtracker.data.tags.entities.*;

import java.util.*;
import java.util.concurrent.*;

public class TagsRepository {
    private TagDao tagDao;
    private ThreadPoolExecutor backgroundThread;

    public TagsRepository(AppDatabase db, ThreadPoolExecutor backgroundThread) {
        this.tagDao = db.tagDao();
        this.backgroundThread = backgroundThread;
    }

    public LiveData<List<Tag>> getAll() {
        return this.tagDao.getAll();
    }

    public void insert(final Tag tag) {
        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                tagDao.addTag(tag);
            }
        });
    }

    public void update(final Tag tag) {
        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                tagDao.update(tag);
            }
        });
    }

    public void delete(List<String> tagIds) {
        final String[] tagIdsArray = tagIds.toArray(new String[0]);
        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                tagDao.delete(tagIdsArray);
            }
        });
    }

    public void moveTag(final Tag toMove, final Tag target) {
        backgroundThread.execute(new Runnable() {
            @Override
            public void run() {
                tagDao.moveTagPosition(toMove, target);
            }
        });
    }
}
