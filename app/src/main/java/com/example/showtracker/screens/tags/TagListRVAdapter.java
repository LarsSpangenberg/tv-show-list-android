package com.example.showtracker.screens.tags;

import android.content.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.recyclerview.widget.*;

import com.example.showtracker.*;
import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.utils.*;

import java.util.*;

import static com.example.showtracker.screens.common.utils.ListItemSortHandler.*;

public class TagListRVAdapter extends RecyclerView.Adapter<TagListRVAdapter.TagViewHolder>
    implements ItemMoveCallback.Listener {
    private static final String TAG = "TagListRVAdapter";
    private List<Tag> tagList;
    private List<String> selection;
    private TagRVEventListener eventListener;
    private Context context;

    public interface TagRVEventListener {
        void onTagClick(@NonNull Tag tag);
        void onTagLongClick(@NonNull Tag tag, int position);
        boolean onItemMoved(Tag toMove, Tag target);
    }

    public TagListRVAdapter(Context context, TagRVEventListener listener) {
        this.context = context;
        this.eventListener = listener;
        this.selection = new ArrayList<>();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.tag_list_item, parent, false);
        return new TagViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        if (this.tagList != null) {
            Tag current = this.tagList.get(position);
            holder.bind(
                current,
                position,
                this.selection.contains(current.id),
                this.eventListener
            );
        }
    }

    @Override
    public int getItemCount() {
        if (this.tagList != null) return this.tagList.size();
        return 0;
    }

    @Override
    public void onDrag(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onDrop(int fromPosition, int toPosition) {
        Tag toMove = this.tagList.get(fromPosition);
        Tag target = this.tagList.get(toPosition);
        boolean itemMoved = this.eventListener.onItemMoved(toMove, target);
        if (!itemMoved) notifyDataSetChanged();
    }

    public void setTagList(List<Tag> allTags) {
        this.tagList = allTags;
        sortItems();
        notifyDataSetChanged();
    }

    public void setSelection(List<String> selectionIds) {
        this.selection = selectionIds;
    }

//    public Tag findTagById(String tagId) {
//        for (Tag tag : this.tagList) {
//            if (tagId.equals(tag.id)) {
//                return tag;
//            }
//        }
//        return null;
//    }

    public void resetSelection() {
        this.selection.clear();
        notifyDataSetChanged();
    }

    public void sortItems() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        sortItems(prefs.getInt(TAG_SORT_MODE, SORT_BY_CUSTOM));
    }

    public void sortItems(int sortBy) {
        Comparator<Tag> comparator = null;
        switch (sortBy) {
            case SORT_BY_NAME:
                comparator = new Comparator<Tag>() {
                    @Override
                    public int compare(Tag o1, Tag o2) {
                        String title1 = o1.name;
                        String title2 = o2.name;
                        return title1.compareToIgnoreCase(title2);
                    }
                };
                break;
            case SORT_BY_CUSTOM:
                comparator = new Comparator<Tag>() {
                    @Override
                    public int compare(Tag o1, Tag o2) {
                        int position1 = o1.position;
                        int position2 = o2.position;
                        return position1 - position2;
                    }
                };
                break;
        }

        if (comparator != null) {
            Collections.sort(this.tagList, comparator);
            notifyDataSetChanged();
        }
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        TagViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.tag_name);
        }

        void bind(
            final Tag tag,
            final int position,
            boolean isSelected,
            final TagRVEventListener listener
        ) {
            Log.d(TAG, "bind: " + tag.toString());
            this.name.setText(tag.name);
            this.itemView.setSelected(isSelected);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTagClick(tag);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onTagLongClick(tag, position);
                    return true;
                }
            });
        }
    }
}
