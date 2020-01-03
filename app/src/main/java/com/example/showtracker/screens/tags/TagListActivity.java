package com.example.showtracker.screens.tags;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.*;
import androidx.recyclerview.widget.*;

import com.example.showtracker.R;
import com.example.showtracker.common.utils.*;
import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.activities.*;
import com.example.showtracker.screens.common.viewmodel.*;
import com.google.android.material.floatingactionbutton.*;
import com.google.android.material.snackbar.*;

import java.util.*;

import static com.example.showtracker.screens.common.activities.AddEditActivity.*;
//import static com.example.showtracker.views.AddEditTagActivity.*;

public class TagListActivity extends BaseListActivity
    implements TagListRVAdapter.TagRVEventListener {
    public static final int ACTIVITY_REQUEST_CODE_EDIT_TAG = 1;
    public static final String TAG_SORT_MODE = "TAG_SORT_MODE";

    private TagListViewModel viewModel;
    private TagListRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewModelFactory viewModelFactory = getPresentationComponent().getViewModelFactory();
        this.viewModel = ViewModelProviders.of(this, viewModelFactory).get(TagListViewModel.class);

        this.adapter = new TagListRVAdapter(this, this);
        RecyclerView rv = findViewById(R.id.tags_list);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(this.adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);

        rv.setAdapter(this.adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        subscribeUITagList();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TagListActivity.this, AddEditActivity.class);
                intent.putExtra(ITEM_TYPE, TAG_ITEM);
                startActivityForResult(intent, ACTIVITY_REQUEST_CODE_EDIT_TAG);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tag_list_menu, menu);
        setDeleteButton(menu.findItem(R.id.tag_list_menu_delete));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.tag_list_menu_sort_by_name:
                sortItems(SORT_BY_NAME);
                return true;
            case R.id.tag_list_menu_sort_by_custom:
                sortItems(SORT_BY_CUSTOM);
                return true;
            case R.id.tag_list_menu_delete:
                deleteSelectedTags();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_REQUEST_CODE_EDIT_TAG) {
            if (resultCode == RESULT_OK) {
                String action = data.getAction();

                if (action != null && action.equals(Intent.ACTION_EDIT)) {
//                    result from activity to edit an existing tag
                    Tag tag = (Tag) data.getSerializableExtra(ITEM_TO_EDIT);
                    tag.name = data.getStringExtra(EXTRA_REPLY);
                    viewModel.renameTag(tag);
                } else {
//                    result from activity to create a new tag
                    Tag newTag = new Tag(data.getStringExtra(EXTRA_REPLY));
                    viewModel.createNewTag(newTag);
                }
            } else if (resultCode == RESULT_EMPTY) {
                Snackbar
                    .make(
                        getWindow().getDecorView().getRootView(),
                        "Tag name cannot be empty. No changes were made.",
                        Snackbar.LENGTH_LONG
                    )
                    .show();
            }

            resetSelection();
            this.adapter.resetSelection();
        }
    }

    @Override
    public void onTagClick(@NonNull Tag tag) {
        Intent intent = new Intent(this, AddEditActivity.class);
        intent.putExtra(ITEM_TO_EDIT, tag);
        intent.putExtra(ITEM_NAME, tag.name);
        intent.putExtra(ITEM_TYPE, TAG_ITEM);
        intent.setAction(Intent.ACTION_EDIT);

        startActivityForResult(intent, ACTIVITY_REQUEST_CODE_EDIT_TAG);
    }

    @Override
    public void onTagLongClick(@NonNull Tag tag, int position) {
        handleSelection(tag.id);
        this.adapter.setSelection(getSelection());
        this.adapter.notifyItemChanged(position);
    }

    @Override
    public boolean onItemMoved(Tag toMove, Tag target) {
        boolean itemMoved = false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int sortMode = prefs.getInt(TAG_SORT_MODE, SORT_BY_CUSTOM);
        if (sortMode == SORT_BY_CUSTOM) {
            this.viewModel.moveTag(toMove, target);
            itemMoved = true;
        }
        unselectItem(toMove.id);
        return itemMoved;
    }

    private void sortItems(int sortBy) {
        SharedPreferences.Editor prefsEditor = PreferenceManager
            .getDefaultSharedPreferences(this)
            .edit();
        switch (sortBy) {
            case SORT_BY_NAME:
                prefsEditor.putInt(TAG_SORT_MODE, SORT_BY_NAME).apply();
                this.adapter.sortItems(SORT_BY_NAME);
                break;
            case SORT_BY_CUSTOM:
                prefsEditor.putInt(TAG_SORT_MODE, SORT_BY_CUSTOM);
                this.adapter.sortItems(SORT_BY_CUSTOM);
                break;
        }
    }

    private void deleteSelectedTags() {
        viewModel.deleteSelection(getSelection());
        resetSelection();
    }

    private void handleNoResults(boolean isEmpty) {
        TextView noResultsMessage = findViewById(R.id.tag_list_empty_list_message);
        if (isEmpty) {
            noResultsMessage.setVisibility(View.VISIBLE);
        } else {
            noResultsMessage.setVisibility(View.GONE);
        }
    }

    private void subscribeUITagList() {
        viewModel.getTagList().observe(this, new Observer<List<Tag>>() {
            @Override
            public void onChanged(List<Tag> tags) {
                adapter.setTagList(tags);
                handleNoResults(tags.isEmpty());
            }
        });
    }
}
