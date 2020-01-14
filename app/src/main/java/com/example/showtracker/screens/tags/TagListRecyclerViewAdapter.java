package com.example.showtracker.screens.tags;

import android.content.*;
import android.view.*;

import androidx.annotation.*;
import androidx.recyclerview.widget.*;

import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.views.*;
import com.example.showtracker.screens.tags.taglistitem.*;

import java.util.*;

import static com.example.showtracker.screens.common.utils.ListItemSortHandler.*;

public class TagListRecyclerViewAdapter extends RecyclerView.Adapter<TagListRecyclerViewAdapter.ViewHolder>
    implements ItemMoveCallback.Listener, TagListItemViewMvc.Listener {

    private final SharedPreferences prefs;
    private final ViewMvcFactory viewMvcFactory;
    private final ListItemSelectionHandler selectionHandler;
    private final ListItemSortHandler<Tag> sortHandler;

    private List<Tag> tagList;

    private Listener listener;

    public interface Listener {
        void onTagClick(Tag tag);
        void onTagDragAndDrop(Tag toMove, Tag target);
    }

    public TagListRecyclerViewAdapter(
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TagListItemViewMvc listItemViewMvc = viewMvcFactory.getTagListItemViewMvc(parent);
        listItemViewMvc.registerListener(this);
        return new ViewHolder(listItemViewMvc);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tag current = tagList.get(position);
        holder.viewMvc.bindTag(current, position);
    }

    @Override
    public int getItemCount() {
        if (tagList != null) return tagList.size();
        return 0;
    }

    @Override
    public void onTagClick(Tag tag) {
        listener.onTagClick(tag);
    }

    @Override
    public void onTagLongClick(Tag tag, int position) {
        selectionHandler.handleSelection(tag.getId());
        notifyItemChanged(position);
    }

    @Override
    public void onDrag(int fromPosition, int toPosition) {
        if (prefs.getInt(TAG_SORT_MODE, SORT_BY_CUSTOM) == SORT_BY_CUSTOM) {
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onDrop(int fromPosition, int toPosition) {
        Tag toMove = tagList.get(fromPosition);
        Tag target = tagList.get(toPosition);
        listener.onTagDragAndDrop(toMove, target);
        selectionHandler.unselectItem(toMove.getId());

        if (prefs.getInt(TAG_SORT_MODE, SORT_BY_CUSTOM) != SORT_BY_CUSTOM) {
            notifyItemChanged(fromPosition);
        }
    }

    public void bindTags(List<Tag> allTags) {
        sortHandler.setItems(allTags);
        tagList = sortHandler.getSortedItems();
        notifyDataSetChanged();
    }

    public void sortTags(int sortBy) {
        tagList = sortHandler.getSortedItems(sortBy);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TagListItemViewMvc viewMvc;

        public ViewHolder(TagListItemViewMvc viewMvc) {
            super(viewMvc.getRootView());
            this.viewMvc = viewMvc;
        }
    }
}
