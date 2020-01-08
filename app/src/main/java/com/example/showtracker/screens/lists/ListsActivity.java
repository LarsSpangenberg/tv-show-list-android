package com.example.showtracker.screens.lists;

import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;

import androidx.appcompat.widget.*;
import androidx.lifecycle.Observer;
import androidx.lifecycle.*;

import com.example.showtracker.R;
import com.example.showtracker.common.dependencyinjection.presentation.*;
import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.screens.common.activities.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.viewmodel.*;
import com.example.showtracker.screens.showslist.*;
import com.example.showtracker.screens.tags.*;
import com.google.android.material.snackbar.*;

import java.util.*;

import static com.example.showtracker.data.lists.entities.ListEntity.*;
import static com.example.showtracker.screens.common.activities.SimpleAddEditActivity.*;
import static com.example.showtracker.screens.common.utils.ListItemSortHandler.*;

public class ListsActivity extends BaseActivity
    implements ListsViewMvcImpl.Listener {
    private static final String TAG = "ListsActivity";
    public static final int ACTIVITY_REQUEST_CODE_EDIT_LIST = 1;

    private ListsViewModel viewModel;
    private ListsViewMvc viewMvc;
    private ListItemSelectionHandler selectionHandler = new ListItemSelectionHandler();

    private List<ListWithShows> allLists = new ArrayList<>();
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PresentationComponent presentationComponent = getPresentationComponent();

        prefs = presentationComponent.getSharedPreferences();

        viewMvc = presentationComponent
            .getViewMvcFactory()
            .getListsViewMvc(selectionHandler, null);

        ViewModelFactory viewModelFactory = presentationComponent.getViewModelFactory();
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ListsViewModel.class);

        setContentView(viewMvc.getRootView());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewMvc.registerListener(this);
        subscribeUiLists();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewMvc.unregisterListener(this);
        unsubscribeUiLists();
    }

    @Override
    public void onListClick(ListWithShows list) {
        Intent intent = new Intent(this, ShowsListActivity.class);
        intent.putExtra(LIST_ID, list.getId());
        intent.putExtra(ListEntity.class.getSimpleName(), list.getList());
        selectionHandler.resetSelection();
        startActivity(intent);
    }

    @Override
    public void onListDragAndDrop(ListEntity toMove, ListEntity target) {
        int sortMode = prefs.getInt(LIST_SORT_MODE, SORT_BY_CUSTOM);
        if (sortMode == SORT_BY_CUSTOM) {
            viewModel.moveList(toMove, target);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        selectionHandler.setDeleteButton(menu.findItem(R.id.main_menu_delete_selection));
        selectionHandler.setEditButton(menu.findItem(R.id.main_menu_rename_list));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.main_menu_delete_selection:
                deleteSelectedLists();
                return true;
            case R.id.main_menu_rename_list:
                editSelectedList();
                return true;
            case R.id.main_menu_sort_by_name:
                onSortItems(SORT_BY_NAME);
                return true;
            case R.id.main_menu_sort_by_custom:
                onSortItems(SORT_BY_CUSTOM);
                return true;
            case R.id.main_menu_tag_list:
                startTagList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_REQUEST_CODE_EDIT_LIST) {
            if (resultCode == RESULT_OK) {
                String action = data.getAction();

                if (action != null && action.equals(Intent.ACTION_EDIT)) {
                    // result from activity to edit an existing list

                    ListEntity list = (ListEntity) data.getSerializableExtra(ITEM_TO_EDIT);
                    list.name = data.getStringExtra(EXTRA_REPLY);
                    viewModel.renameList(list);

                } else {
                    // result from activity to create a new list
                    ListEntity newList = new ListEntity(data.getStringExtra(EXTRA_REPLY));
                    viewModel.createNewList(newList);
                }
            } else if (resultCode == RESULT_EMPTY) {
                Snackbar
                    .make(
                        getWindow().getDecorView().getRootView(),
                        "List name cannot be empty. No changes were made.",
                        Snackbar.LENGTH_LONG
                    )
                    .show();
            }
            selectionHandler.resetSelection();
        }
    }

    @Override
    public void onSortItems(int sortBy) {
        viewMvc.sortItems(sortBy);
    }

    @Override
    public void onFabClick() {
        Intent intent = new Intent(this, SimpleAddEditActivity.class);
        intent.putExtra(ITEM_TYPE, LIST_ITEM);
        startActivityForResult(intent, ACTIVITY_REQUEST_CODE_EDIT_LIST);
    }

    private void deleteSelectedLists() {
        viewModel.deleteSelection(selectionHandler.getSelection());
        selectionHandler.resetSelection();
    }

    private void editSelectedList() {
        if (selectionHandler.getSelection().size() != 1) return;

        String listId = selectionHandler.getSelection().get(0);
        ListEntity list = null;
        for (ListWithShows l : allLists) {
            if (l.getId().equals(listId)) {
                list = l.getList();
                break;
            }
        }

        if (list != null) {
            Intent intent = new Intent(this, SimpleAddEditActivity.class);
            intent.putExtra(ITEM_TO_EDIT, list);
            intent.putExtra(ITEM_NAME, list.name);
            intent.putExtra(ITEM_TYPE, LIST_ITEM);
            intent.setAction(Intent.ACTION_EDIT);

            startActivityForResult(intent, ACTIVITY_REQUEST_CODE_EDIT_LIST);
        }
    }

    private void startTagList() {
        Intent intent = new Intent(this, TagListActivity.class);
        startActivity(intent);
    }

    private void subscribeUiLists() {
        viewModel.getAllListsWithShowId().observe(this, new Observer<List<ListWithShows>>() {
            @Override
            public void onChanged(List<ListWithShows> lists) {
                viewMvc.bindLists(lists);
                allLists = lists;
            }
        });
    }

    private void unsubscribeUiLists() {
        viewModel.getAllListsWithShowId().removeObservers(this);
    }
}
