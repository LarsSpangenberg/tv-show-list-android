package com.example.showtracker.screens.showdetails;

import android.content.*;
import android.os.*;
import android.view.*;

import androidx.core.app.*;
import androidx.lifecycle.Observer;
import androidx.lifecycle.*;

import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.screens.common.activities.*;
import com.example.showtracker.screens.common.screensnavigator.*;
import com.example.showtracker.screens.common.viewmodel.*;
import com.example.showtracker.screens.common.views.*;
import com.example.showtracker.screens.showdetails.dataholder.*;
import com.google.android.material.snackbar.*;

import java.util.*;

import javax.inject.*;

import static com.example.showtracker.data.lists.entities.ListEntity.*;
import static com.example.showtracker.data.shows.entities.Show.*;

public class ShowDetailsActivity extends BaseActivity implements ShowDetailsViewMvcImpl.Listener {
    private Show show;
    private List<String> showsTags;
    private List<String> showsLists;

    private boolean editMode = false;

    @Inject ScreensNavigator screensNavigator;
    @Inject ViewModelWithIdFactory viewModelFactory;
    @Inject ViewMvcFactory viewMvcFactory;

    private ShowDetailsViewModel viewModel;
    private ShowDetailsViewMvc viewMvc;

    public static void start(Context context) {
        // create new show
        Intent intent = new Intent(context, ShowDetailsActivity.class);
        context.startActivity(intent);
    }

    public static void start(Context context, String showId) {
        // edit existing show
        Intent intent = new Intent(context, ShowDetailsActivity.class);
        intent.putExtra(SHOW_ID, showId);
        intent.setAction(Intent.ACTION_EDIT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresentationComponent().inject(this);

        viewMvc = viewMvcFactory.getShowDetailsViewMvc(null);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_EDIT)) {
            setUpEditExistingShow(intent);
        } else {
            setUpNewShow(intent);
        }
        viewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(ShowDetailsViewModel.class);

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
    protected void onResume() {
        super.onResume();
        if (!editMode && show != null) {
            viewModel.setDetailsLiveData(show.getId());
            editMode = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveShow();
    }

    @Override
    public void onBackPressed() {
        if (viewMvc.isTitleEditTextEmpty()) {
            showUnableToSaveMessage();
        } else {
            screensNavigator.navigateUp();
        }
    }

    @Override
    public void onNavigateUpClicked() {
        onBackPressed();
    }

    @Override
    public void onListChipClick(String listId, boolean isChecked) {
        if (isChecked) {
            showsLists.add(listId);
        } else {
            showsLists.remove(listId);
        }
    }

    @Override
    public void onTagChipClick(String tagId, boolean isChecked) {
        if (isChecked) {
            showsTags.add(tagId);
        } else {
            showsTags.remove(tagId);
        }
    }

    private void setUpEditExistingShow(Intent intent) {
        editMode = true;
        viewModelFactory.setId(intent.getStringExtra(SHOW_ID));
    }

    private void setUpNewShow(Intent intent) {
        intent.getStringExtra(LIST_ID);
        showsTags = new ArrayList<>();
        showsLists = new ArrayList<>();
        showsLists.add(intent.getStringExtra(LIST_ID));

        viewMvc.requestFocusOnTitleEditText();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void saveShow() {
        Show show = viewMvc.getUserInput(editMode);

        if (show != null) {
            if (editMode) {
                viewModel.updateShow(show);
                viewModel.saveShowsLists(show.getId(), showsLists);
                viewModel.saveShowsTags(show.getId(), showsTags);
            } else {
                viewModel.createNewShow(show, showsLists, showsTags);
            }
        }
    }

    private void bindShowDetails(ShowDetails showData) {
        show = showData.show;
        showsTags = showData.tagIds;
        showsLists = showData.listIds;

        viewMvc.bindShow(showData);
    }

    private void showUnableToSaveMessage() {
        Snackbar snackbar = Snackbar.make(
            viewMvc.getRootView(),
            "No Title was entered.\nContinue without saving?",
            Snackbar.LENGTH_LONG
        );
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(ShowDetailsActivity.this);
            }
        });
        snackbar.show();
    }

    private void registerViewModelObservers() {
        viewModel.getAllShowDetails().observe(
            this,
            new Observer<ShowDetailsData>() {
                @Override
                public void onChanged(ShowDetailsData details) {
                    if (details != null) {
                        if (editMode) bindShowDetails(details.show);
                        viewMvc.bindLists(details.allLists, showsLists);
                        viewMvc.bindTags(details.allTags, showsTags);
                    }
                }
            }
        );
    }

    private void unregisterViewModelObservers() {
        viewModel.getAllShowDetails().removeObservers(this);
    }
}
