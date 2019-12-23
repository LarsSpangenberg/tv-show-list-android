package com.example.showtracker.viewmodels;

import android.app.*;

import androidx.annotation.*;
import androidx.lifecycle.*;

import com.example.showtracker.data.*;
import com.example.showtracker.data.entities.*;

import java.util.*;

public class TagListViewModel extends AndroidViewModel {
    private TagsRepository tagsRepository;
    private LiveData<List<Tag>> tagList;

    public TagListViewModel(@NonNull Application application) {
        super(application);
        this.tagsRepository = TagsRepository.getInstance(application);
        this.tagList = this.tagsRepository.getAll();
    }

    public LiveData<List<Tag>> getTagList() {
        return this.tagList;
    }

    public void createNewTag(Tag tag) {
        this.tagsRepository.insert(tag);
    }

    public void renameTag(Tag tag) {
        this.tagsRepository.update(tag);
    }

    public void moveTag(Tag toMove, Tag target) {
        this.tagsRepository.moveTag(toMove, target);
    }

    public void deleteSelection(List<String> selection) {
        this.tagsRepository.delete(selection);
    }
}
