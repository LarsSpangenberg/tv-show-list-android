package com.example.showtracker.viewmodels;

import androidx.lifecycle.*;

import com.example.showtracker.*;
import com.example.showtracker.data.*;
import com.example.showtracker.data.entities.*;

import java.util.*;

public class TagListViewModel extends ViewModel {
    private TagsRepository tagsRepository;
    private LiveData<List<Tag>> tagList;

    public TagListViewModel(MyApplication application) {
        tagsRepository = application.getApplicationComponent().getTagsRepository();
        tagList = tagsRepository.getAll();
    }

    public LiveData<List<Tag>> getTagList() {
        return tagList;
    }

    public void createNewTag(Tag tag) {
        tagsRepository.insert(tag);
    }

    public void renameTag(Tag tag) {
        tagsRepository.update(tag);
    }

    public void moveTag(Tag toMove, Tag target) {
        tagsRepository.moveTag(toMove, target);
    }

    public void deleteSelection(List<String> selection) {
        tagsRepository.delete(selection);
    }
}
