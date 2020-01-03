package com.example.showtracker.screens.lists;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.*;
import androidx.recyclerview.widget.*;

import com.example.showtracker.R;
import com.example.showtracker.common.utils.*;
import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.screens.common.activities.*;
import com.example.showtracker.screens.showslist.*;
import com.example.showtracker.screens.tags.*;
import com.example.showtracker.screens.common.viewmodel.*;
import com.google.android.material.floatingactionbutton.*;
import com.google.android.material.snackbar.*;

import java.util.*;

import static com.example.showtracker.data.lists.entities.ListEntity.*;
import static com.example.showtracker.screens.common.activities.AddEditActivity.*;

public class MainActivity extends BaseListActivity
    implements ListOfShowsRVAdapter.ListsRVEventListener {
    public static final int ACTIVITY_REQUEST_CODE_EDIT_LIST = 1;
    private static final String TAG = "MainActivity";
    public static final String LIST_SORT_MODE = "LIST_SORT_MODE";

    private ListOfShowsRVAdapter adapter;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewModelFactory viewModelFactory = getPresentationComponent().getViewModelFactory();
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
        adapter = new ListOfShowsRVAdapter(this, this);
        RecyclerView recyclerView = findViewById(R.id.lists_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new ItemMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        subscribeUILists();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                intent.putExtra(ITEM_TYPE, LIST_ITEM);
                startActivityForResult(intent, ACTIVITY_REQUEST_CODE_EDIT_LIST);
            }
        });
    }

    @Override
    public void onListClick(@NonNull ListWithShows list) {
        Intent intent = new Intent(this, ShowsListActivity.class);
        intent.putExtra(LIST_ID, list.list.id);
        intent.putExtra(ListEntity.class.getSimpleName(), list.list);
        startActivity(intent);
    }


    @Override
    public void onListLongClick(@NonNull ListWithShows list, int position) {
        Log.d(TAG, "onListLongClick: started");
        handleSelection(list.list.id);
        this.adapter.setSelection(getSelection());
        this.adapter.notifyItemChanged(position);
    }

    @Override
    public boolean onItemMoved(@NonNull ListEntity toMove, @NonNull ListEntity target) {
        boolean itemMoved = false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int sortMode = prefs.getInt(LIST_SORT_MODE, SORT_BY_CUSTOM);
        if (sortMode == SORT_BY_CUSTOM) {
            this.viewModel.moveList(toMove, target);
            itemMoved = true;
        }
        unselectItem(toMove.id);
//        resetSelection();
        this.adapter.setSelection(getSelection());
//        this.adapter.resetSelection();
        Log.d(TAG, "onItemMoved: " + getSelection().toString());
        return itemMoved;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        setDeleteButton(menu.findItem(R.id.main_menu_delete_selection));
        setEditButton(menu.findItem(R.id.main_menu_rename_list));
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
                sortItems(SORT_BY_NAME);
                return true;
            case R.id.main_menu_sort_by_custom:
                sortItems(SORT_BY_CUSTOM);
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
//                    result from activity to edit an existing list

                    ListEntity list = (ListEntity) data.getSerializableExtra(ITEM_TO_EDIT);
                    list.name = data.getStringExtra(EXTRA_REPLY);
                    viewModel.renameList(list);

                } else {
//                    result from activity to create a new list
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

            resetSelection();
            this.adapter.resetSelection();
        }
    }


    private void sortItems(int sortBy) {
        SharedPreferences.Editor prefsEditor = PreferenceManager
            .getDefaultSharedPreferences(this)
            .edit();
        switch (sortBy) {
            case SORT_BY_NAME:
                prefsEditor.putInt(LIST_SORT_MODE, SORT_BY_NAME).apply();
                this.adapter.sortItems(SORT_BY_NAME);
                break;
            case SORT_BY_CUSTOM:
                prefsEditor.putInt(LIST_SORT_MODE, SORT_BY_CUSTOM);
                this.adapter.sortItems(SORT_BY_CUSTOM);
                break;
        }
    }

    private void deleteSelectedLists() {
        Log.d(TAG, "deleteSelectedLists: deleting " + getSelection().size() + " items");
        viewModel.deleteSelection(getSelection());
        resetSelection();
    }

    private void editSelectedList() {
        if (getSelection().size() != 1) return;

        String listId = getSelection().get(0);
        ListEntity list = this.adapter.findListById(listId);

        if (list != null) {
            Intent intent = new Intent(this, AddEditActivity.class);
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

    private void handleNoResults(boolean isEmpty) {
        TextView noResultsMessage = findViewById(R.id.main_empty_list_message);
        if (isEmpty) {
            noResultsMessage.setVisibility(View.VISIBLE);
        } else {
            noResultsMessage.setVisibility(View.GONE);
        }
    }

    private void subscribeUILists() {
        viewModel.getAllListsWithShowId().observe(this, new Observer<List<ListWithShows>>() {
            @Override
            public void onChanged(List<ListWithShows> lists) {
                adapter.setLists(lists);
                handleNoResults(lists.isEmpty());
            }
        });
    }
}
