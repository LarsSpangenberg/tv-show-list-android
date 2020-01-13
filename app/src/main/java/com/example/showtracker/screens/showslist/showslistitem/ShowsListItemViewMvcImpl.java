package com.example.showtracker.screens.showslist.showslistitem;

import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;

import com.example.showtracker.*;
import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.views.*;

public class ShowsListItemViewMvcImpl extends BaseObservableViewMvc<ShowsListItemViewMvc.Listener>
    implements ShowsListItemViewMvc {
    private final TextView title;
    private final TextView season;
    private final TextView episode;
    private final TextView status;
    private final TextView comment;
    private final TextView tags;
    private ListItemSelectionHandler selectionHandler;

    public ShowsListItemViewMvcImpl(
        LayoutInflater inflater,
        ListItemSelectionHandler selectionHandler,
        ViewGroup parent
    ) {
        setRootView(inflater.inflate(R.layout.shows_list_item, parent, false));
        title = findViewById(R.id.sli_title);
        season = findViewById(R.id.sli_season);
        episode = findViewById(R.id.sli_episode);
        status = findViewById(R.id.sli_status);
        comment = findViewById(R.id.sli_comment);
        tags = findViewById(R.id.sli_tags);
        this.selectionHandler = selectionHandler;
    }

    @Override
    public void bindShow(final Show show, final int position, String tagText) {
        setSelected(show.getId());

        title.setText(show.getTitle());
        season.setText(getString(R.string.sli_season, show.getSeason()));
        episode.setText(getString(R.string.sli_episode, show.getEpisode()));
        status.setText(show.getStatus().toString());
        assignColorToStatus(show.getStatus());

        if (tagText.isEmpty()) {
            tags.setVisibility(View.INVISIBLE);
        } else {
            tags.setVisibility(View.VISIBLE);
            tags.setText(tagText);
        }

        if (show.comment.isEmpty()) {
            comment.setVisibility(View.GONE);
        } else {
            comment.setVisibility(View.VISIBLE);
            comment.setText(show.comment);
        }

        getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Listener listener : getListeners()) {
                    listener.onShowClick(show);
                }
            }
        });

        getRootView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (Listener listener : getListeners()) {
                    listener.onLongShowClick(show, position);
                }
                return true;
            }
        });
    }

    private void setSelected(String id) {
        getRootView().setSelected(selectionHandler.isSelected(id));
    }

    private void assignColorToStatus(Show.Status status) {
        Resources resources = getResources();
        TypedArray ta = resources.obtainTypedArray(R.array.sl_colors);
        int color = ta.getColor(status.getCode() - 1, Color.TRANSPARENT);

        Drawable background = resources.getDrawable(R.drawable.sli_status_background);
        background.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));

        this.status.setBackground(background);

        ta.recycle();
    }
}

