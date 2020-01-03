package com.example.showtracker.data.tags;

import android.os.*;

import androidx.annotation.*;
import androidx.lifecycle.*;

import com.example.showtracker.data.common.*;
import com.example.showtracker.data.tags.entities.*;

import java.util.*;

public class TagsRepository {
    private TagDao tagDao;

    public TagsRepository(@NonNull AppDatabase db) {
        this.tagDao = db.tagDao();
    }

//    public LiveData<List<Tag>> findTagsByShowId(String showId) {
//        return this.tagDao.findTagsByShowId(showId);
//    }

    public LiveData<List<Tag>> getAll() {
        return this.tagDao.getAll();
    }

    public void insert(Tag tag) {
        new InsertAsync(this.tagDao).execute(tag);
    }

    public void update(Tag tag) {
        new UpdateAsync(this.tagDao).execute(tag);
    }

    public void delete(List<String> selection) {
        new DeleteAsync(this.tagDao).execute(selection.toArray(new String[0]));
    }

    public void moveTag(Tag toMove, Tag target) {
        Tag[] tagsToMove = new Tag[2];
        tagsToMove[0] = toMove;
        tagsToMove[1] = target;
        new MoveTagPositionAsync(this.tagDao).execute(tagsToMove);
    }

    private static class InsertAsync extends AsyncTask<Tag, Void, Void> {
        private TagDao tagDao;

        InsertAsync(TagDao tagDao) {
            this.tagDao = tagDao;
        }

        @Override
        protected Void doInBackground(Tag... tags) {
            this.tagDao.addTag(tags[0]);
            return null;
        }
    }

    private static class UpdateAsync extends AsyncTask<Tag, Void, Void> {
        private TagDao tagDao;

        UpdateAsync(TagDao tagDao) {
            this.tagDao = tagDao;
        }

        @Override
        protected Void doInBackground(Tag... tags) {
            this.tagDao.update(tags[0]);
            return null;
        }
    }


    private static class DeleteAsync extends AsyncTask<String, Void, Void> {
        private TagDao tagDao;

        DeleteAsync(TagDao tagDao) {
            this.tagDao = tagDao;
        }

        @Override
        protected Void doInBackground(String... selectionIds) {
            this.tagDao.delete(selectionIds);
            return null;
        }
    }

    private static class MoveTagPositionAsync extends AsyncTask<Tag, Void, Void> {
        private TagDao tagDao;

        MoveTagPositionAsync(TagDao tagDao) {
            this.tagDao = tagDao;
        }

        @Override
        protected Void doInBackground(Tag... tagsToMove) {
            if (tagsToMove.length == 2) {
                this.tagDao.moveTagPosition(tagsToMove[0], tagsToMove[1]);
            }
            return null;
        }
    }

}