package com.example.showtracker.views;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.*;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.*;
import androidx.lifecycle.Observer;
import androidx.lifecycle.*;

import com.example.showtracker.R;
import com.example.showtracker.data.entities.*;
import com.example.showtracker.viewmodels.*;
import com.example.showtracker.viewmodels.ShowDetailsViewModel.*;
import com.google.android.material.appbar.*;
import com.google.android.material.chip.*;
import com.google.android.material.snackbar.*;
import com.google.android.material.textfield.*;

import java.util.*;

import static com.example.showtracker.data.entities.ListOfShows.*;
import static com.example.showtracker.data.entities.Show.*;

public class ShowDetailActivity extends AppCompatActivity {

    private static final String TAG = "ShowDetailActivity";
    private ShowDetailsViewModel viewModel;
    private Show show;
    private List<String> showsTags;
    private List<String> showsLists;
    private boolean listChipsSet = false;
    private boolean tagChipsSet = false;
    private boolean editMode = false;

    private TextInputLayout titleLayout;
    private EditText title;
    private EditText season;
    private EditText episode;
    private Spinner status;
    private ChipGroup lists;
    private ChipGroup tags;
    private EditText comment;
    private CollapsingToolbarLayout collapsingToolbarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // TODO add a more convenient way to turn off keyboard
        // TODO refine enter behavior of the text inputs

        this.titleLayout = findViewById(R.id.sd_title_input_layout);
        this.title = findViewById(R.id.sd_title_input);
        this.season = findViewById(R.id.sd_season_input);
        this.episode = findViewById(R.id.sd_episode_input);
        this.status = findViewById(R.id.sd_status_spinner);
        this.lists = findViewById(R.id.sd_lists_chipgroup);
        this.tags = findViewById(R.id.sd_tags_chipgroup);
        this.comment = findViewById(R.id.sd_comment_input);
        setStatusSpinner();
        findViewById(R.id.sd_season_increment).setOnClickListener(new IncDecClickListener());
        findViewById(R.id.sd_season_decrement).setOnClickListener(new IncDecClickListener());
        findViewById(R.id.sd_episode_increment).setOnClickListener(new IncDecClickListener());
        findViewById(R.id.sd_episode_decrement).setOnClickListener(new IncDecClickListener());

        this.collapsingToolbarLayout = findViewById(R.id.sd_collapsing_toolbar);
        this.collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        this.collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.primary_text_dark));

        this.title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String titleText = ((EditText) v).getText().toString();
                    if (titleText.isEmpty()) {
                        titleLayout.setError("Title cannot be empty. Changes won't be saved");
                    } else if (titleLayout.getError() != null) {
                        titleLayout.setError(null);
                    }
                }
            }
        });

        Intent intent = getIntent();
        String action = intent.getAction();
        String showId = null;
        if (action != null && action.equals(Intent.ACTION_EDIT)) {
            // user is updating an existing Show
            this.editMode = true;
            showId = intent.getStringExtra(SHOW_ID);
        } else {
            // user is creating new Show
            intent.getStringExtra(LIST_ID);
            this.showsTags = new ArrayList<>();
            this.showsLists = new ArrayList<>();
            this.showsLists.add(intent.getStringExtra(LIST_ID));
            this.title.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        ViewModelFactoryWithId factory = new ViewModelFactoryWithId(getApplication(), showId);
        this.viewModel = ViewModelProviders.of(this, factory).get(ShowDetailsViewModel.class);

        subscribeUIShowDetails();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (this.title.getText().toString().isEmpty()) {
                showNoTitleMessage();
            } else {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (this.title.getText().toString().isEmpty()) {
            showNoTitleMessage();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!this.editMode && this.show != null) {
            viewModel.setDetailsLiveData(this.show.id);
            this.editMode = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveShow();
    }

    private void saveShow() {
        Show show;
        String title = this.title.getText().toString();
        // don't save anything if there is no title;
        if (title.isEmpty()) return;
        if (editMode) {
            show = this.show;
            show.title = title;
        } else {
            show = new Show(title);
        }
        show.season = Integer.parseInt(this.season.getText().toString());
        show.episode = Integer.parseInt(this.episode.getText().toString());
        show.status = (Show.Status) this.status.getSelectedItem();
        show.comment = this.comment.getText().toString();
        if (editMode) {
            viewModel.updateShow(show);
            viewModel.saveShowsLists(show.id, this.showsLists);
            viewModel.saveShowsTags(show.id, this.showsTags);
        } else {
            viewModel.createNewShow(show, this.showsLists, this.showsTags);
        }
    }

    private void setShowDetails(ShowDetails showData) {
        this.show = showData.show;
        Log.d(TAG, "setShowDetails: " + showData.listIds + " & " + showData.tagIds);
        this.collapsingToolbarLayout.setTitle(this.show.title);
        this.title.setText(this.show.title);
        this.season.setText(String.valueOf(this.show.season));
        this.episode.setText(String.valueOf(this.show.episode));
        this.comment.setText(this.show.comment);
        this.showsTags = showData.tagIds;
        this.showsLists = showData.listIds;

        Show.Status[] statusValues = Show.Status.values();
        for (int i = 0; i < statusValues.length; i++) {
            if (statusValues[i] == this.show.status) {
                this.status.setSelection(i);
                break;
            }
        }
    }

    private void setStatusSpinner() {
        ArrayAdapter<Show.Status> adapter = new ArrayAdapter<>(
            this,
            R.layout.spinner_item,
            Show.Status.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.status.setAdapter(adapter);
    }

    private void setLists(List<ListOfShows> lists) {
        if (!this.listChipsSet) {
            for (int i = 0; i < lists.size(); i++) {
                ListOfShows list = lists.get(i);
                final String id = list.id;
                Chip chip = new Chip(this);

                chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.chip_drawable));
                chip.setCheckedIconVisible(false);
                chip.setText(list.name);
                if (this.showsLists != null && this.showsLists.contains(id)) chip.setChecked(true);
                chip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Chip ch = (Chip) v;
                        if (ch.isChecked()) {
                            showsLists.add(id);
                            Log.d(TAG, "onClick: adding");
                        } else if (!ch.isChecked()) {
                            showsLists.remove(id);
                            Log.d(TAG, "onClick: removing");
                        }
                    }
                });
                this.lists.addView(chip);
            }
            this.listChipsSet = true;
        }
    }

    private void setTags(List<Tag> tags) {
        if (!this.tagChipsSet) {
            if (tags.isEmpty()) {
                Chip chip = new Chip(this);
                chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.chip_drawable));
                chip.setText(R.string.sd_no_tags_message);
                chip.setCheckable(false);
                chip.setClickable(false);
                this.tags.addView(chip);
            } else {
                for (Tag tag : tags) {
                    final String id = tag.id;
                    Log.d(TAG, "setTags: tag id:" + id);
                    Chip chip = new Chip(this);
                    chip.setChipDrawable(ChipDrawable.createFromResource(
                        this,
                        R.xml.chip_drawable
                    ));
                    chip.setCheckedIconVisible(false);
                    chip.setText(tag.name);
                    if (this.showsTags != null && this.showsTags.contains(tag.id)) {
                        chip.setChecked(true);
                    }
                    chip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Chip ch = (Chip) v;
                            if (ch.isChecked()) {
                                showsTags.add(id);
                                Log.d(TAG, "onClick: adding");
                            } else if (!ch.isChecked()) {
                                showsTags.remove(id);
                                Log.d(TAG, "onClick: removing");
                            }
                        }
                    });
                    this.tags.addView(chip);
                }
            }
            this.tagChipsSet = true;
        }
    }

    private void subscribeUIShowDetails() {
        viewModel.getAllShowDetails().observe(
            this,
            new Observer<ShowDetailsData>() {
                @Override
                public void onChanged(ShowDetailsData details) {
                    if (details != null) {
                        if (editMode) setShowDetails(details.show);
                        setLists(details.allLists);
                        setTags(details.allTags);
                    }
                }
            }
        );
    }

    private void showNoTitleMessage() {
        Snackbar snackbar = Snackbar.make(
            this.title,
            "No Title was entered.\nContinue without saving?",
            Snackbar.LENGTH_LONG
        );
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(ShowDetailActivity.this);
            }
        });
        snackbar.show();
    }

    private class IncDecClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            int value;
            switch (id) {
                case R.id.sd_season_increment:
                    value = Integer.parseInt(season.getText().toString());
                    season.setText(String.valueOf(++value));
                    break;
                case R.id.sd_season_decrement:
                    value = Integer.parseInt(season.getText().toString());
                    if (value > 0) {
                        season.setText(String.valueOf(--value));
                    }
                    break;
                case R.id.sd_episode_increment:
                    value = Integer.parseInt(episode.getText().toString());
                    episode.setText(String.valueOf(++value));
                    break;
                case R.id.sd_episode_decrement:
                    value = Integer.parseInt(episode.getText().toString());
                    if (value > 0) {
                        episode.setText(String.valueOf(--value));
                    }
                    break;
            }
        }
    }
}
