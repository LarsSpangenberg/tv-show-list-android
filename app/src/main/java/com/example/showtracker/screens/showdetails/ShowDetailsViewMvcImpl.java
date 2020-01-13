package com.example.showtracker.screens.showdetails;

import android.graphics.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.appcompat.widget.Toolbar;

import com.example.showtracker.*;
import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.toolbar.*;
import com.example.showtracker.screens.common.views.*;
import com.google.android.material.appbar.*;
import com.google.android.material.chip.*;
import com.google.android.material.textfield.*;

import java.util.*;

public class ShowDetailsViewMvcImpl extends BaseObservableViewMvc<ShowDetailsViewMvc.Listener>
    implements ShowDetailsViewMvc {

    private TextInputLayout titleLayout;
    private EditText title;
    private EditText season;
    private EditText episode;
    private Spinner status;
    private ChipGroup listChips;
    private ChipGroup tagChips;
    private EditText comment;

    private boolean listChipsInitialized = false;
    private boolean tagChipsInitialized = false;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Show show;
    private final ToolbarViewMvc toolbarViewMvc;

    public ShowDetailsViewMvcImpl(
        LayoutInflater inflater,
        ViewGroup parent,
        ViewMvcFactory viewMvcFactory
    ) {
        setRootView(inflater.inflate(R.layout.activity_show_detail, parent, false));

        titleLayout = findViewById(R.id.sd_title_input_layout);
        title = findViewById(R.id.sd_title_input);
        season = findViewById(R.id.sd_season_input);
        episode = findViewById(R.id.sd_episode_input);
        status = findViewById(R.id.sd_status_spinner);
        listChips = findViewById(R.id.sd_lists_chipgroup);
        tagChips = findViewById(R.id.sd_tags_chipgroup);
        comment = findViewById(R.id.sd_comment_input);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarViewMvc = viewMvcFactory.getToolbarViewMvc(toolbar);

        initToolBar();
        initStatusSpinner();
        registerTitleFocusListener();
        registerIncDecClickListeners();
    }

    @Override
    public void bindShow(ShowDetails showData) {
        show = showData.show;

        collapsingToolbarLayout.setTitle(show.title);
        title.setText(show.title);
        season.setText(String.valueOf(show.season));
        episode.setText(String.valueOf(show.episode));
        comment.setText(show.comment);


        Show.Status[] statusValues = Show.Status.values();
        for (int i = 0; i < statusValues.length; i++) {
            if (statusValues[i] == show.status) {
                status.setSelection(i);
                break;
            }
        }
    }


    @Override
    public void bindLists(List<ListEntity> allLists, List<String> showsListIds) {
        if (!listChipsInitialized) {
            for (int i = 0; i < allLists.size(); i++) {
                ListEntity list = allLists.get(i);
                final String id = list.id;
                boolean isChecked = (showsListIds != null && showsListIds.contains(id));

                Chip chip = buildChip(
                    list.name,
                    isChecked,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean isChecked = ((Chip) v).isChecked();
                            for (Listener listener : getListeners()) {
                                listener.onListChipClick(id, isChecked);
                            }

                        }
                    }
                );

                listChips.addView(chip);
            }
            listChipsInitialized = true;
        }
    }

    @Override
    public void bindTags(List<Tag> allTags, List<String> showsTagIds) {
        if (!tagChipsInitialized) {
            if (allTags.isEmpty()) {
                Chip chip = buildChip(R.string.sd_no_tags_message);
                this.tagChips.addView(chip);
            } else {
                for (Tag tag : allTags) {
                    final String id = tag.id;
                    boolean isChecked = (showsTagIds != null && showsTagIds.contains(tag.id));

                    Chip chip = buildChip(
                        tag.getName(),
                        isChecked,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean isChecked = ((Chip) v).isChecked();
                                for (Listener listener : getListeners()) {
                                    listener.onTagChipClick(id, isChecked);
                                }
                            }
                        }
                    );
                    tagChips.addView(chip);
                }
            }
            tagChipsInitialized = true;
        }
    }

    @Override
    public Show getUserInput(boolean editMode) {
        String title = this.title.getText().toString();
        if (title.isEmpty()) return null;
        // returns null if there is no title;

        Show show;
        if (editMode) {
            show = this.show;
            show.title = title;
        } else {
            show = new Show(title);
        }

        show.season = Integer.parseInt(season.getText().toString());
        show.episode = Integer.parseInt(episode.getText().toString());
        show.status = (Show.Status) status.getSelectedItem();
        show.comment = comment.getText().toString();

        return show;
    }

    @Override
    public void requestFocusOnTitleEditText() {
        title.requestFocus();
    }

    @Override
    public boolean isTitleEditTextEmpty() {
        return title.getText().toString().isEmpty();
    }

    private void initToolBar() {
        collapsingToolbarLayout = findViewById(R.id.sd_collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbarLayout.setCollapsedTitleTextColor(
            getResources().getColor(android.R.color.primary_text_dark)
        );

        toolbarViewMvc.enableUpButtonAndListen(new ToolbarViewMvc.NavigateUpClickListener() {
            @Override
            public void onNavigateUpClicked() {
                for (Listener listener : getListeners()) {
                    listener.onNavigateUpClicked();
                }
            }
        });
    }

    private void initStatusSpinner() {
        ArrayAdapter<Show.Status> adapter = new ArrayAdapter<>(
            getContext(),
            R.layout.spinner_item,
            Show.Status.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);
    }

    private Chip buildChip(@StringRes int messageRes) {
        Chip chip = new Chip(getContext());
        chip.setChipDrawable(ChipDrawable.createFromResource(getContext(), R.xml.chip_drawable));
        chip.setText(messageRes);
        chip.setCheckable(false);
        chip.setClickable(false);
        return chip;
    }

    private Chip buildChip(String name, boolean isChecked, View.OnClickListener listener) {
        Chip chip = new Chip(getContext());
        chip.setChipDrawable(ChipDrawable.createFromResource(getContext(), R.xml.chip_drawable));
        chip.setCheckedIconVisible(false);
        chip.setText(name);
        chip.setChecked(isChecked);
        chip.setOnClickListener(listener);
        return chip;
    }

    private void registerTitleFocusListener() {
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
    }

    private void registerIncDecClickListeners() {
        IncDecClickListener listener = new IncDecClickListener();
        findViewById(R.id.sd_season_increment).setOnClickListener(listener);
        findViewById(R.id.sd_season_decrement).setOnClickListener(listener);
        findViewById(R.id.sd_episode_increment).setOnClickListener(listener);
        findViewById(R.id.sd_episode_decrement).setOnClickListener(listener);
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
