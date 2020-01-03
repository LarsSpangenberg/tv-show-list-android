package com.example.showtracker.screens.lists;

import androidx.lifecycle.*;

import com.example.showtracker.common.*;
import com.example.showtracker.data.lists.*;
import com.example.showtracker.data.lists.entities.*;

import java.util.*;

public class MainViewModel extends ViewModel {
    private ListsRepository listsRepository;
    private LiveData<List<ListWithShows>> listsWithShows;

    public MainViewModel(MyApplication application) {
        this.listsRepository = application.getApplicationComponent().getListsRepository();
        this.listsWithShows = this.listsRepository.getAllListsWithShowId();
    }

    public LiveData<List<ListWithShows>> getAllListsWithShowId() {
        return this.listsWithShows;
    }

    public void createNewList(ListEntity list) {
        this.listsRepository.insert(list);
    }

    public void renameList(ListEntity list) {
        this.listsRepository.update(list);
    }

//    public void renameList(String listId, String newName) {
//        this.listsRepository.update(listId, newName);
//    }

    public void moveList(ListEntity toMove, ListEntity target) {
        this.listsRepository.moveList(toMove, target);
    }

//    public void deleteSelection(List<ListEntity> selection) {
//        this.listsRepository.delete(selection);
//    }

    public void deleteSelection(List<String> selection) {
        this.listsRepository.delete(selection);
    }
}
