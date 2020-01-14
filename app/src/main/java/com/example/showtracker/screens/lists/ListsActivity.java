package com.example.showtracker.screens.lists;

import android.content.*;
import android.os.*;

import androidx.lifecycle.Observer;
import androidx.lifecycle.*;

import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.screens.common.activities.*;
import com.example.showtracker.screens.common.screensnavigator.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.viewmodel.*;
import com.example.showtracker.screens.common.views.*;
import com.google.android.material.snackbar.*;

import java.util.*;

import javax.inject.*;

import static com.example.showtracker.screens.common.activities.SimpleAddEditActivity.*;
import static com.example.showtracker.screens.common.utils.ListItemSortHandler.*;

public class ListsActivity extends BaseActivity
    implements ListsViewMvcImpl.Listener {

    private ListsViewModel viewModel;
    private ListsViewMvc viewMvc;

    private List<ListWithShows> allLists = new ArrayList<>();

    @Inject SharedPreferences prefs;
    @Inject ScreensNavigator screensNavigator;
    @Inject ListItemSelectionHandler selectionHandler;
    @Inject ViewModelFactory viewModelFactory;
    @Inject ViewMvcFactory viewMvcFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresentationComponent().inject(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ListsViewModel.class);

        viewMvc = viewMvcFactory.getListsViewMvc(null);

        setContentView(viewMvc.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewMvc.registerListener(this);
        registerViewModelObservers();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewMvc.unregisterListener(this);
        unregisterViewModelObservers();
    }

    @Override
    public void onListClick(ListWithShows list) {
        selectionHandler.resetSelection();
        screensNavigator.toShowsList(list.getList());
    }

    @Override
    public void onListDragAndDrop(ListEntity toMove, ListEntity target) {
        int sortMode = prefs.getInt(LIST_SORT_MODE, SORT_BY_CUSTOM);
        if (sortMode == SORT_BY_CUSTOM) {
            viewModel.moveList(toMove, target);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_REQUEST_CODE_EDIT_LIST) {
            if (resultCode == RESULT_OK) {
                String action = data.getAction();
                if (action != null && action.equals(Intent.ACTION_EDIT)) {
                    renameList(data);

                } else {
                    createNewList(data);
                }
            } else if (resultCode == RESULT_EMPTY) {
                showUnableToSaveMessage();
            }
            selectionHandler.resetSelection();
        }
    }

    @Override
    public void onFabClick() {
        screensNavigator.toNewList();
    }

    @Override
    public void onTagListClick() {
        screensNavigator.toTagList();
    }

    @Override
    public void onDeleteListClick() {
        viewModel.deleteSelection(selectionHandler.getSelectionIds());
        selectionHandler.resetSelection();
    }

    @Override
    public void onEditListClick() {
        ListEntity selectedList = getSelectedList();
        if (selectedList != null) {
            screensNavigator.toRenameList(selectedList);
        }
    }

    private ListEntity getSelectedList() {
        if (selectionHandler.getSelectionIds().size() != 1) return null;

        String listId = selectionHandler.getSelectionIds().get(0);
        ListEntity selectedList = null;
        for (ListWithShows list : allLists) {
            if (list.getId().equals(listId)) {
                selectedList = list.getList();
            }
        }
        return selectedList;
    }

    private void createNewList(Intent data) {
        ListEntity newList = new ListEntity(data.getStringExtra(EXTRA_REPLY));
        viewModel.createNewList(newList);
    }

    private void renameList(Intent data) {
        ListEntity list = (ListEntity) data.getSerializableExtra(ITEM_TO_EDIT);
        list.name = data.getStringExtra(EXTRA_REPLY);
        viewModel.renameList(list);
    }

    private void showUnableToSaveMessage() {
        Snackbar
            .make(
                viewMvc.getRootView(),
                "List name cannot be empty. No changes were made.",
                Snackbar.LENGTH_LONG
            )
            .show();
    }

    private void registerViewModelObservers() {
        viewModel.getAllListsWithShowId().observe(this, new Observer<List<ListWithShows>>() {
            @Override
            public void onChanged(List<ListWithShows> lists) {
                viewMvc.bindLists(lists);
                allLists = lists;
            }
        });
    }

    private void unregisterViewModelObservers() {
        viewModel.getAllListsWithShowId().removeObservers(this);
    }
}
