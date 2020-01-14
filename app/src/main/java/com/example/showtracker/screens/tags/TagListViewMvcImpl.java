package com.example.showtracker.screens.tags;

import android.content.*;
import android.view.*;
import android.widget.*;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.*;

import com.example.showtracker.*;
import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.toolbar.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.views.*;
import com.google.android.material.floatingactionbutton.*;

import java.util.*;

import static com.example.showtracker.screens.common.utils.ListItemSortHandler.SORT_BY_CUSTOM;
import static com.example.showtracker.screens.common.utils.ListItemSortHandler.SORT_BY_NAME;

public class TagListViewMvcImpl extends BaseObservableViewMvc<TagListViewMvc.Listener>
    implements TagListRecyclerViewAdapter.Listener, ToolbarViewMvc.MenuItemClickListener,
    TagListViewMvc {

    private TagListRecyclerViewAdapter adapter;
    private final ToolbarViewMvc toolbarViewMvc;

    public TagListViewMvcImpl(
        LayoutInflater inflater,
        SharedPreferences prefs,
        ListItemSelectionHandler selectionHandler,
        ViewGroup parent,
        ViewMvcFactory viewMvcFactory
    ) {
        setRootView(inflater.inflate(R.layout.activity_tag_list, parent, false));

        adapter = new TagListRecyclerViewAdapter(prefs, selectionHandler, this, viewMvcFactory);

        RecyclerView recyclerView = findViewById(R.id.tags_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper.Callback callback = new ItemMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarViewMvc = viewMvcFactory.getToolbarViewMvc(toolbar);

        initToolbar();
        initFab();
    }

    @Override
    public void bindTags(List<Tag> tags) {
        adapter.bindTags(tags);
        handleNoResults(tags.isEmpty());
    }

    private void initToolbar() {
        toolbarViewMvc.setTitle(getString(R.string.title_activity_tag));
        toolbarViewMvc.registerListener(this);
        toolbarViewMvc.inflateMenu(R.menu.tag_list_menu);
        toolbarViewMvc.enableDeleteButton(R.id.tag_list_menu_delete);
        toolbarViewMvc.enableUpButtonAndListen(new ToolbarViewMvc.NavigateUpClickListener() {
            @Override
            public void onNavigateUpClicked() {
                for (Listener listener : getListeners()) {
                    listener.onNavigateUpClick();
                }
            }
        });
    }

    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Listener listener : getListeners()) {
                    listener.onFabClick();
                }
            }
        });
    }

    private void handleNoResults(boolean isEmpty) {
        TextView noResultsMessage = findViewById(R.id.tag_list_empty_list_message);
        if (isEmpty) {
            noResultsMessage.setVisibility(View.VISIBLE);
        } else {
            noResultsMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        for (Listener listener : getListeners()) {
            switch (id) {
                case R.id.tag_list_menu_sort_by_name:
                    adapter.sortTags(SORT_BY_NAME);
                    break;
                case R.id.tag_list_menu_sort_by_custom:
                    adapter.sortTags(SORT_BY_CUSTOM);
                    break;
                case R.id.tag_list_menu_delete:
                    listener.onDeleteTagClick();
                    break;
            }
        }
    }

    @Override
    public void onTagClick(Tag tag) {
        for (Listener listener : getListeners()) {
            listener.onTagClick(tag);
        }
    }

    @Override
    public void onTagDragAndDrop(Tag toMove, Tag target) {
        for (Listener listener : getListeners()) {
            listener.onTagDragAndDrop(toMove, target);
        }
    }
}
