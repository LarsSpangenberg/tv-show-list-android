package com.example.showtracker.screens.lists;

import androidx.lifecycle.*;

import com.example.showtracker.common.*;
import com.example.showtracker.data.lists.*;
import com.example.showtracker.data.lists.entities.*;

import java.util.*;

public class ListsViewModel extends ViewModel {
    private ListsRepository listsRepository;
    private LiveData<List<ListWithShows>> listsWithShows;

    public ListsViewModel(CustomApplication application) {
        listsRepository = application.getApplicationComponent().getListsRepository();
        listsWithShows = listsRepository.getAllListsWithShowId();
    }

    public LiveData<List<ListWithShows>> getAllListsWithShowId() {
        return listsWithShows;
    }

    public void createNewList(ListEntity list) {
        listsRepository.insert(list);
    }

    public void renameList(ListEntity list) {
        listsRepository.update(list);
    }

    public void moveList(ListEntity toMove, ListEntity target) {
        listsRepository.moveList(toMove, target);
    }

    public void deleteSelection(List<String> selection) {
        listsRepository.delete(selection);
    }
}
