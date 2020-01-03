package com.example.showtracker.adapters;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.recyclerview.widget.*;

import com.example.showtracker.*;
import com.example.showtracker.data.entities.*;
import com.example.showtracker.common.utils.*;

import java.util.*;

import static com.example.showtracker.screens.BaseListActivity.*;
import static com.example.showtracker.views.ShowsListActivity.*;

public class ShowRVAdapter extends RecyclerView.Adapter<ShowRVAdapter.ShowViewHolder> implements
    ItemMoveCallback.ItemTouchListener {

    private static final String TAG = "ShowRVAdapter";
    private List<ShowWithTags> shows;
    private List<Tag> allTags;
    private List<String> selection;
    private ShowRVEventListener eventListener;
    private Context context;

    public interface ShowRVEventListener {
        void onShowClick(@NonNull Show show);
        void onLongClick(@NonNull Show show, int position);
        boolean onItemMoved(@NonNull Show toMove, @NonNull Show target);
    }

    public ShowRVAdapter(Context context, ShowRVEventListener listener) {
        this.context = context;
        this.eventListener = listener;
        this.selection = new ArrayList<>();
    }

    @NonNull
    @Override
    public ShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater
            .from(context)
            .inflate(R.layout.shows_list_item, parent, false);
        return new ShowViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowViewHolder holder, int position) {
        if (this.shows != null) {
            Log.d(TAG, "onBindViewHolder: " + this.selection.toString());
            ShowWithTags current = shows.get(position);
            holder.bind(
                current.show,
                position,
                selection.contains(current.show.id),
                showsTagsToString(current.tagIds),
                eventListener
            );
        }
    }

    @Override
    public int getItemCount() {
        if (this.shows != null) return this.shows.size();
        return 0;
    }

    @Override
    public void onDrag(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onDrop(int fromPosition, int toPosition) {
        Show toMove = this.shows.get(fromPosition).show;
        Show target = this.shows.get(toPosition).show;
        boolean itemMoveSuccess = this.eventListener.onItemMoved(toMove, target);
        if (!itemMoveSuccess) notifyItemChanged(fromPosition);
    }

    public void setShows(List<ShowWithTags> shows) {
        this.shows = shows;
        sortItems();
        notifyDataSetChanged();
    }

    public void setAllTags(List<Tag> tags) {
        this.allTags = tags;
        notifyDataSetChanged();
    }

    public void setSelection(List<String> selectionIds) {
        this.selection = selectionIds;
    }

    public void sortItems() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        sortItems(prefs.getInt(SHOW_SORT_MODE, SORT_BY_CUSTOM));
    }

    public void sortItems(int sortBy) {
        // TODO create utility sort class to use with lists, tags, and shows RV to keep code dry.
        Comparator<ShowWithTags> comparator = null;
        switch (sortBy) {
            case SORT_BY_NAME:
                comparator = new Comparator<ShowWithTags>() {
                    @Override
                    public int compare(ShowWithTags o1, ShowWithTags o2) {
                        String title1 = o1.show.title;
                        String title2 = o2.show.title;
                        return title1.compareToIgnoreCase(title2);
                    }
                };
                break;
            case SORT_BY_CUSTOM:
                comparator = new Comparator<ShowWithTags>() {
                    @Override
                    public int compare(ShowWithTags o1, ShowWithTags o2) {
                        int position1 = o1.show.position;
                        int position2 = o2.show.position;
                        return position1 - position2;
                    }
                };
                break;
        }

        if (comparator != null) {
            Collections.sort(this.shows, comparator);
            notifyDataSetChanged();
        }
    }

    private String showsTagsToString(List<String> tagIds) {
        StringBuilder builder = new StringBuilder();
        for (Tag tag : this.allTags) {
            if (tagIds.contains(tag.id)) {
                if (builder.length() != 0) {
                    builder.append(", ");
                }
                builder.append(tag.name);
            }
        }
        return builder.toString();
    }

    static class ShowViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView season;
        private final TextView episode;
        private final TextView status;
        private final TextView comment;
        private final TextView tags;
        private Context context;

        ShowViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            this.title = itemView.findViewById(R.id.sli_title);
            this.season = itemView.findViewById(R.id.sli_season);
            this.episode = itemView.findViewById(R.id.sli_episode);
            this.status = itemView.findViewById(R.id.sli_status);
            this.comment = itemView.findViewById(R.id.sli_comment);
            this.tags = itemView.findViewById(R.id.sli_tags);
        }

        void bind(
            final Show show,
            final int position,
            boolean isSelected,
            String tagText,
            final ShowRVEventListener listener
        ) {
            Log.d(TAG, "bind: " + show.toString());
            Resources resources = this.context.getResources();
            this.itemView.setSelected(isSelected);
            this.title.setText(show.title);
            this.season.setText(resources.getString(R.string.sli_season, show.season));
            this.episode.setText(resources.getString(R.string.sli_episode, show.episode));
            this.status.setText(show.status.toString());
            assignColorToStatus(show.status, resources);

            if (tagText.isEmpty()) {
                this.tags.setVisibility(View.INVISIBLE);
            } else {
                this.tags.setVisibility(View.VISIBLE);
                this.tags.setText(tagText);
            }

            if (show.comment.isEmpty()) {
                this.comment.setVisibility(View.GONE);
            } else {
                this.comment.setVisibility(View.VISIBLE);
                this.comment.setText(show.comment);
            }

            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShowClick(show);
                }
            });

            this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClick(show, position);
                    return true;
                }
            });
        }

        private void assignColorToStatus(Show.Status status, Resources resources) {
            TypedArray ta = resources.obtainTypedArray(R.array.sl_colors);
            int color = ta.getColor(status.getCode() - 1, Color.TRANSPARENT);
            Drawable background = resources.getDrawable(R.drawable.sli_status_background);
            background.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            this.status.setBackground(background);
            ta.recycle();
        }
    }
}
