package com.example.showtracker.screens.showslist;

import android.content.*;
import android.os.*;

import androidx.annotation.*;
import androidx.lifecycle.Observer;
import androidx.lifecycle.*;

import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.activities.*;
import com.example.showtracker.screens.common.dialogs.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.viewmodel.*;
import com.example.showtracker.screens.common.views.*;
import com.example.showtracker.screens.showdetails.*;
import com.example.showtracker.screens.showslist.ShowsListViewModel.*;

import java.util.*;

import javax.inject.*;

import static com.example.showtracker.data.lists.entities.ListEntity.*;
import static com.example.showtracker.data.shows.entities.Show.*;
import static com.example.showtracker.screens.common.utils.ListItemSortHandler.*;

public class ShowsListActivity extends BaseActivity
    implements ShowsListViewMvc.Listener{

    private ListEntity currentList;
    private List<Tag> allTags = new ArrayList<>();

    @Inject SharedPreferences prefs;
    @Inject ShowsListFilters filters;
    @Inject ListItemSelectionHandler selectionHandler;
    @Inject ViewModelWithIdFactory viewModelFactory;
    @Inject ViewMvcFactory viewMvcFactory;

    private ShowsListViewMvc viewMvc;
    private ShowsListViewModel viewModel;

    public static void start(ListEntity list, Context context) {
        Intent intent = new Intent(context, ShowsListActivity.class);
        intent.putExtra(ListEntity.class.getSimpleName(), list);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresentationComponent().inject(this);

        Intent intent = getIntent();
        currentList = (ListEntity) intent.getSerializableExtra(ListEntity.class.getSimpleName());

        viewModelFactory.setId(currentList.getId());
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ShowsListViewModel.class);

        viewMvc = viewMvcFactory.getShowsListViewMvc(currentList, null);

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
    public void onNavigationUpClick() {
        onBackPressed();
    }


    @Override
    public void onShowClick(@NonNull Show show) {
        ShowDetailsActivity.start(this, show.getId());
    }


    @Override
    public void onShowDragAndDrop(@NonNull Show toMove, @NonNull Show target) {
        int sortMode = prefs.getInt(SHOW_SORT_MODE, SORT_BY_CUSTOM);
        if (sortMode == SORT_BY_CUSTOM) {
            viewModel.moveShow(toMove, target);
        }
    }

    @Override
    public void onFabClick() {
        Intent intent = new Intent(this, ShowDetailsActivity.class);
        intent.putExtra(LIST_ID, currentList.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteShowClick() {
        viewModel.deleteShowsFromList(currentList.getId(), selectionHandler.getSelectionIds());
        selectionHandler.resetSelection();
    }

    @Override
    public void onStatusFilterMenuClick() {
        final Status[] items = Status.values();
        int itemCount = items.length;
        String[] statusFilterNames = new String[itemCount];
        final boolean[] checkedItems = new boolean[itemCount];
        List<Status> statusFilter = filters.getStatusFilters();

        for (int i = 0; i < itemCount; i++) {
            Status status = items[i];
            statusFilterNames[i] = status.toString();
            checkedItems[i] = statusFilter.contains(status);
        }

        showFilterDialog(
            "Filter by Status",
            statusFilterNames,
            checkedItems,
            new FiltersDialog.Listener() {
                @Override
                public void onItemClick(int which) {
                    filters.handleStatusFilter(items[which]);

                }

                @Override
                public void onPositiveButtonClicked() {
                    viewModel.setShowsListData(filters);
                    registerViewModelObservers();
                }
            }
        );
    }

    @Override
    public void onTagFilterMenuClick() {
        int itemCount = allTags.size();
        String[] tagFilterNames = new String[itemCount];
        final boolean[] checkedItems = new boolean[itemCount];
        List<String> tagFilter = filters.getFilteredTagIds();

        for (int i = 0; i < itemCount; i++) {
            Tag tag = allTags.get(i);
            tagFilterNames[i] = tag.getName();
            checkedItems[i] = tagFilter.contains(tag.getId());
        }

        showFilterDialog(
            "Filter by Tags",
            tagFilterNames,
            checkedItems,
            new FiltersDialog.Listener() {
                @Override
                public void onItemClick(int which) {
                    filters.handleTagFilter(allTags.get(which).id);

                }

                @Override
                public void onPositiveButtonClicked() {
                    viewModel.setShowsListData(filters);
                    registerViewModelObservers();
                }
            }
        );
    }

    private void showFilterDialog(
        String title,
        String[] filterNames,
        boolean[] checkedItems,
        FiltersDialog.Listener listener
    ) {
        FiltersDialog dialog = FiltersDialog.newInstance(
            title,
            filterNames,
            checkedItems
        );
        dialog.setListener(listener);
        dialog.show(getSupportFragmentManager(), FiltersDialog.TAG);
    }

    private String getTagFilterReferenceText() {
        List<String> tagIds = filters.getFilteredTagIds();
        StringBuilder builder = new StringBuilder();

        if (!tagIds.isEmpty()) {
            boolean firstItem = true;
            builder.append("Tag Filters: ");
            for (Tag tag : allTags) {
                if (tagIds.contains(tag.id)) {
                    if (firstItem) {
                        firstItem = false;
                    } else {
                        builder.append(", ");
                    }
                    builder.append(tag.name);
                }
            }
        }
        return builder.toString();

    }

    private String getStatusFilterReferenceText() {
        List<Show.Status> statusFilters = filters.getStatusFilters();
        StringBuilder builder = new StringBuilder();

        if (!statusFilters.isEmpty()) {
            builder.append("Status Filters: ");
            for (int i = 0; i < statusFilters.size(); i++) {
                if (i != 0) {
                    builder.append(", ");
                }
                builder.append(statusFilters.get(i).toString());
            }
        }
        return builder.toString();
    }

    private void prepareUiData(List<ShowWithTags> shows, List<Tag> tags) {
        viewMvc.bindShowsAndTags(shows, tags);
        viewMvc.setStatusFilterReferenceText(getStatusFilterReferenceText());
        viewMvc.setTagFilterReferenceText(getTagFilterReferenceText());
        allTags = tags;
    }

    private void registerViewModelObservers() {
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

    private void unregisterViewModelObservers() {
        viewModel.getShowsListData().removeObservers(this);
    }
}
