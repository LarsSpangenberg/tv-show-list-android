package com.example.showtracker.screens.showslist;

import android.content.*;
import android.util.*;
import android.view.*;

import androidx.annotation.*;
import androidx.recyclerview.widget.*;

import com.example.showtracker.data.shows.entities.*;
import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.views.*;
import com.example.showtracker.screens.showslist.showslistitem.*;

import java.util.*;

import static com.example.showtracker.screens.common.utils.ListItemSortHandler.*;

public class ShowsRecyclerViewAdapter
    extends RecyclerView.Adapter<ShowsRecyclerViewAdapter.ShowsViewHolder>
    implements ItemMoveCallback.Listener, ShowsListItemViewMvc.Listener {

    private static final String TAG = "ShowsRecyclerViewAdapte";
    private SharedPreferences prefs;
    private ListItemSelectionHandler selectionHandler;
    private ListItemSortHandler<ShowWithTags> sortHandler;
    private ViewMvcFactory viewMvcFactory;

    private Listener listener;

    private List<ShowWithTags> shows;
    private List<Tag> allTags;

    public interface Listener {
        void onShowClick(Show show);
        void onShowDragAndDrop(Show toMove, Show target);
    }

    public ShowsRecyclerViewAdapter(
        SharedPreferences prefs,
        ListItemSelectionHandler selectionHandler,
        Listener listener,
        ViewMvcFactory viewMvcFactory
    ) {
        this.prefs = prefs;
        this.selectionHandler = selectionHandler;
        this.listener = listener;
        this.viewMvcFactory = viewMvcFactory;
        sortHandler = new ListItemSortHandler<>(prefs);
    }

    @NonNull
    @Override
    public ShowsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called");
        ShowsListItemViewMvc viewHolderViewMvc = viewMvcFactory.getShowsListItemViewMvc(parent);
        viewHolderViewMvc.registerListener(this);
        return new ShowsViewHolder(viewHolderViewMvc);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowsViewHolder holder, int position) {
        ShowWithTags current = shows.get(position);
        Log.d(TAG, "onBindViewHolder: called");
        holder.viewMvc.bindShow(
            current.getShow(),
            position,
            tagsIdsToTagNameText(current.getTags())
        );

    }

    @Override
    public int getItemCount() {
        if (shows != null) return shows.size();
        return 0;
    }

    @Override
    public void onDrag(int fromPosition, int toPosition) {
        if (prefs.getInt(SHOW_SORT_MODE, SORT_BY_CUSTOM) == SORT_BY_CUSTOM) {
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onDrop(int fromPosition, int toPosition) {
        Show toMove = shows.get(fromPosition).getShow();
        Show target = shows.get(toPosition).getShow();
        listener.onShowDragAndDrop(toMove, target);
        selectionHandler.unselectItem(toMove.getId());

        if (prefs.getInt(SHOW_SORT_MODE, SORT_BY_CUSTOM) != SORT_BY_CUSTOM) {
            notifyItemChanged(fromPosition);
        }
    }

    @Override
    public void onShowClick(Show show) {
        listener.onShowClick(show);
    }

    @Override
    public void onLongShowClick(Show show, int position) {
        selectionHandler.handleSelection(show.getId());
        notifyItemChanged(position);
    }

    public void bindShows(List<ShowWithTags> shows) {
        sortHandler.setItems(shows);
        this.shows = sortHandler.getSortedItems();
        Log.d(TAG, "bindShows: " + this.shows);
        notifyDataSetChanged();
    }

    public void bindTags(List<Tag> tags) {
        allTags = tags;
        notifyDataSetChanged();
    }

    public void sortShows(int sortBy) {
        shows = sortHandler.getSortedItems(sortBy);
        notifyDataSetChanged();
    }

    private String tagsIdsToTagNameText(List<String> tagIds) {
        StringBuilder builder = new StringBuilder();
        for (Tag tag : allTags) {
            if (tagIds.contains(tag.id)) {
                if (builder.length() != 0) {
                    builder.append(", ");
                }
                builder.append(tag.name);
            }
        }
        return builder.toString();
    }

    static class ShowsViewHolder extends RecyclerView.ViewHolder {
        private ShowsListItemViewMvc viewMvc;

        public ShowsViewHolder(ShowsListItemViewMvc viewMvc) {
            super(viewMvc.getRootView());
            this.viewMvc = viewMvc;
        }
    }
}
