package com.example.showtracker.screens.tags;

import android.content.*;
import android.os.*;

import androidx.lifecycle.Observer;
import androidx.lifecycle.*;

import com.example.showtracker.data.tags.entities.*;
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
//import static com.example.showtracker.views.AddEditTagActivity.*;

public class TagListActivity extends BaseActivity
    implements TagListViewMvcImpl.Listener {

    @Inject SharedPreferences prefs;
    @Inject ScreensNavigator screensNavigator;
    @Inject ListItemSelectionHandler selectionHandler;
    @Inject ViewModelFactory viewModelFactory;
    @Inject ViewMvcFactory viewMvcFactory;

    private TagListViewModel viewModel;
    private TagListViewMvc viewMvc;

    public static void start(Context context) {
        Intent intent = new Intent(context, TagListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresentationComponent().inject(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TagListViewModel.class);
        viewMvc = viewMvcFactory.getTagListViewMvc(null);

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
    public void onNavigateUpClick() {
        screensNavigator.navigateUp();
    }

    @Override
    public void onFabClick() {
        screensNavigator.toNewTag();
    }

    @Override
    public void onTagClick(Tag tag) {
        screensNavigator.toRenameTag(tag);
    }

    @Override
    public void onTagDragAndDrop(Tag toMove, Tag target) {
        int sortMode = prefs.getInt(TAG_SORT_MODE, SORT_BY_CUSTOM);
        if (sortMode == SORT_BY_CUSTOM) {
            viewModel.moveTag(toMove, target);
        }
    }

    @Override
    public void onDeleteTagClick() {
        viewModel.deleteSelection(selectionHandler.getSelectionIds());
        selectionHandler.resetSelection();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_REQUEST_CODE_EDIT_TAG) {
            if (resultCode == RESULT_OK) {
                String action = data.getAction();

                if (action != null && action.equals(Intent.ACTION_EDIT)) {
                    renameTag(data);
                } else {
                    createNewTag(data);
                }
            } else if (resultCode == RESULT_EMPTY) {
                showUnableToSaveMessage();
            }

            selectionHandler.resetSelection();
        }
    }

    private void createNewTag(Intent data) {
        Tag newTag = new Tag(data.getStringExtra(EXTRA_REPLY));
        viewModel.createNewTag(newTag);
    }

    private void renameTag(Intent data) {
        Tag tag = (Tag) data.getSerializableExtra(ITEM_TO_EDIT);
        tag.name = data.getStringExtra(EXTRA_REPLY);
        viewModel.renameTag(tag);
    }

    private void showUnableToSaveMessage() {
        Snackbar
            .make(
                viewMvc.getRootView(),
                "Tag name cannot be empty. No changes were made.",
                Snackbar.LENGTH_LONG
            )
            .show();
    }

    private void registerViewModelObservers() {
        viewModel.getTagList().observe(this, new Observer<List<Tag>>() {
            @Override
            public void onChanged(List<Tag> tags) {
                viewMvc.bindTags(tags);
            }
        });
    }

    private void unregisterViewModelObservers() {
        viewModel.getTagList().removeObservers(this);
    }
}
