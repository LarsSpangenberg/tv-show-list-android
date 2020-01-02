package com.example.showtracker.viewmodels;

import androidx.lifecycle.*;

import com.example.showtracker.*;
import com.example.showtracker.data.*;
import com.example.showtracker.data.entities.*;

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

    public void createNewList(ListOfShows list) {
        this.listsRepository.insert(list);
    }

    public void renameList(ListOfShows list) {
        this.listsRepository.update(list);
    }

//    public void renameList(String listId, String newName) {
//        this.listsRepository.update(listId, newName);
//    }

    public void moveList(ListOfShows toMove, ListOfShows target) {
        this.listsRepository.moveList(toMove, target);
    }

//    public void deleteSelection(List<ListOfShows> selection) {
//        this.listsRepository.delete(selection);
//    }

    public void deleteSelection(List<String> selection) {
        this.listsRepository.delete(selection);
    }
}
