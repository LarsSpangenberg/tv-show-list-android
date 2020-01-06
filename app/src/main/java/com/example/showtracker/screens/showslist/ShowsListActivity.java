package com.example.showtracker.screens.showslist;

import android.app.*;
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
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.activities.*;
import com.example.showtracker.screens.showdetails.*;
import com.example.showtracker.screens.common.viewmodel.*;
import com.example.showtracker.screens.showslist.ShowsListViewModel.*;
import com.google.android.material.floatingactionbutton.*;

import java.util.*;

import static com.example.showtracker.data.lists.entities.ListEntity.*;
import static com.example.showtracker.data.shows.entities.Show.*;
import static com.example.showtracker.screens.common.utils.ListItemSortHandler.SHOW_SORT_MODE;
import static com.example.showtracker.screens.common.utils.ListItemSortHandler.SORT_BY_CUSTOM;
import static com.example.showtracker.screens.common.utils.ListItemSortHandler.SORT_BY_NAME;

public class ShowsListActivity extends BaseListActivity
    implements ShowRVAdapter.ShowRVEventListener {
    private static final String TAG = "ShowsListActivity";

    private ShowsListViewModel viewModel;
    private ListEntity currentList;
    private List<Tag> allTags = new ArrayList<>();

    private ShowRVAdapter adapter;
    private ShowsListFilters filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shows_list);
        Toolbar toolbar = findViewById(R.id.toolbar);

        RecyclerView recyclerView = findViewById(R.id.shows_list);
        adapter = new ShowRVAdapter(this, this);
        filters = new ShowsListFilters();

        ItemTouchHelper.Callback callback = new ItemMoveCallback(this.adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(this.adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        currentList = (ListEntity) intent.getSerializableExtra(ListEntity.class.getSimpleName());

        ViewModelWithIdFactory factory = getPresentationComponent().getViewModelWithIdFactory();
        factory.setId(currentList.id);
        viewModel = ViewModelProviders.of(this, factory).get(ShowsListViewModel.class);

        toolbar.setTitle(this.currentList.toString());
        setSupportActionBar(toolbar);

        subscribeUIShows();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowsListActivity.this, ShowDetailActivity.class);
                intent.putExtra(LIST_ID, currentList.id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shows_list_menu, menu);
        setDeleteButton(menu.findItem(R.id.shows_list_delete));
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int sortMode = prefs.getInt(SHOW_SORT_MODE, SORT_BY_CUSTOM);
        menu.findItem(R.id.sl_menu_sort_by_name).setChecked(sortMode == SORT_BY_NAME);
        menu.findItem(R.id.sl_menu_sort_by_custom).setChecked(sortMode == SORT_BY_CUSTOM);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.shows_list_delete:
                deleteSelectedShows();
                return true;
            case R.id.sl_menu_sort_by_name:
                sortItems(SORT_BY_NAME);
                return true;
            case R.id.sl_menu_sort_by_custom:
                sortItems(SORT_BY_CUSTOM);
                return true;
            case R.id.sl_menu_filter_status:
                showStatusFilterDialog();
                return true;
            case R.id.sl_menu_filter_tag:
                showTagFilterDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShowClick(@NonNull Show show) {
        Intent intent = new Intent(this, ShowDetailActivity.class);
        Log.d(TAG, "onShowClick: show " + show.title + show.id + " clicked");
        intent.putExtra(SHOW_ID, show.id);
        intent.setAction(Intent.ACTION_EDIT);
        startActivity(intent);
    }

    @Override
    public void onLongClick(@NonNull Show show, int position) {
        handleSelection(show.id);
        this.adapter.setSelection(getSelection());
        this.adapter.notifyItemChanged(position);
    }


    @Override
    public boolean onItemMoved(@NonNull Show toMove, @NonNull Show target) {
        boolean itemMoveSuccess = false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int sortMode = prefs.getInt(SHOW_SORT_MODE, SORT_BY_CUSTOM);
        if (sortMode == SORT_BY_CUSTOM) {
            Log.d(TAG, "onListDragAndDrop: moving " + toMove.title + " to where " + target.title + " currently is");
            Log.d(TAG, "onListDragAndDrop: " + getSelection().toString());
            this.viewModel.moveShow(toMove, target);
            itemMoveSuccess = true;
        }
        unselectItem(toMove.id);
        return itemMoveSuccess;
    }

    private void sortItems(int sortBy) {
        SharedPreferences.Editor prefsEditor = PreferenceManager
            .getDefaultSharedPreferences(this)
            .edit();
        switch (sortBy) {
            case SORT_BY_NAME:
                prefsEditor.putInt(SHOW_SORT_MODE, SORT_BY_NAME).apply();
                this.adapter.sortItems(SORT_BY_NAME);
                break;
            case SORT_BY_CUSTOM:
                prefsEditor.putInt(SHOW_SORT_MODE, SORT_BY_CUSTOM).apply();
                this.adapter.sortItems(SORT_BY_CUSTOM);
                break;
        }
    }

    private void showStatusFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Status[] items = Status.values();
        int itemCount = items.length;
        String[] statusFilterNames = new String[itemCount];
        final boolean[] checkedItems = new boolean[itemCount];
        List<Status> statusFilter = this.filters.getStatusFilters();

        for (int i = 0; i < itemCount; i++) {
            Status status = items[i];
            statusFilterNames[i] = status.toString();
            checkedItems[i] = statusFilter.contains(status);
        }

        builder.setTitle("Filter by Status");
        builder.setMultiChoiceItems(
            statusFilterNames,
            checkedItems,
            new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    filters.handleStatusFilter(items[which]);
                }
            }
        );
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.setShowsListData(filters);
                subscribeUIShows();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showTagFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int itemCount = this.allTags.size();
        String[] tagFilterNames = new String[itemCount];
        final boolean[] checkedItems = new boolean[itemCount];
        List<String> tagFilter = this.filters.getFilteredTagIds();

        for (int i = 0; i < itemCount; i++) {
            Tag tag = this.allTags.get(i);
            tagFilterNames[i] = tag.toString();
            checkedItems[i] = tagFilter.contains(tag.id);
        }

        builder.setTitle("Filter by Tags");
        builder.setMultiChoiceItems(
            tagFilterNames,
            checkedItems,
            new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    filters.handleTagFilter(allTags.get(which).id);
                }
            }
        );
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.setShowsListData(filters);
                subscribeUIShows();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setFilterDisplays() {
        TextView statusFilterDisplay = findViewById(R.id.sl_status_filter_display);
        TextView tagFilterDisplay = findViewById(R.id.sl_tag_filter_display);

        List<String> tagIds = this.filters.getFilteredTagIds();
        List<Status> statusFilters = this.filters.getStatusFilters();
        StringBuilder builder;

        if (tagIds.isEmpty()) {
            tagFilterDisplay.setVisibility(View.GONE);
        } else {
            builder = new StringBuilder();
            boolean firstItem = true;
            builder.append("Tag Filters: ");
            for (Tag tag : this.allTags) {
                if (tagIds.contains(tag.id)) {
                    if (firstItem) {
                        firstItem = false;
                    } else {
                        builder.append(", ");
                    }
                    builder.append(tag.name);
                }
            }
            tagFilterDisplay.setVisibility(View.VISIBLE);
            tagFilterDisplay.setText(builder.toString());
        }

        if (statusFilters.isEmpty()) {
            statusFilterDisplay.setVisibility(View.GONE);
        } else {
            builder = new StringBuilder();
            builder.append("Status Filters: ");
            for (int i = 0; i < statusFilters.size(); i++) {
                if (i != 0) {
                    builder.append(", ");
                }
                builder.append(statusFilters.get(i).toString());
            }
            statusFilterDisplay.setVisibility(View.VISIBLE);
            statusFilterDisplay.setText(builder.toString());
        }

    }

    private void deleteSelectedShows() {
        Log.d(TAG, "deleteSelectedShows: deleting " + getSelection().size() + " items");
        this.viewModel.deleteShowsFromList(this.currentList.id, getSelection());
        resetSelection();
    }

    private void prepareUiData(List<ShowWithTags> shows, List<Tag> allTags) {
        TextView noResultsMessage = findViewById(R.id.sl_empty_list_message);
        if (shows.isEmpty()) {
            noResultsMessage.setVisibility(View.VISIBLE);
        } else {
            noResultsMessage.setVisibility(View.GONE);
        }
        this.adapter.setShows(shows);
        this.adapter.setAllTags(allTags);
        this.allTags = allTags;
        setFilterDisplays();
        invalidateOptionsMenu();
    }

    private void subscribeUIShows() {
        viewModel.getShowsListData().observe(
            this,
            new Observer<ShowsListData>() {
                @Override
                public void onChanged(ShowsListData data) {
                    if (data != null) {
                        prepareUiData(data.shows, data.allTags);
                    }
                }
            }
        );
    }
}
