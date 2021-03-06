package com.example.showtracker.screens.lists;

import android.content.*;
import android.view.*;

import androidx.annotation.*;
import androidx.recyclerview.widget.*;

import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.views.*;
import com.example.showtracker.screens.lists.listslistitem.*;

import java.util.*;

import static com.example.showtracker.screens.common.utils.ListItemSortHandler.*;

public class ListsRecyclerViewAdapter
    extends RecyclerView.Adapter<ListsRecyclerViewAdapter.ViewHolder>
    implements ItemMoveCallback.Listener, ListsListItemViewMvc.Listener {

    private List<ListWithShows> lists;
    private ListItemSelectionHandler selectionHandler;
    private ListItemSortHandler<ListWithShows> sortHandler;
    private ViewMvcFactory viewMvcFactory;
    private Listener listener;

    private SharedPreferences prefs;

    public interface Listener {
        void onListClick(ListWithShows list);
        void onListDragAndDrop(ListEntity toMove, ListEntity target);
    }

    public ListsRecyclerViewAdapter(
        SharedPreferences prefs,
        ListItemSelectionHandler selectionHandler,
        Listener listener,
        ViewMvcFactory viewMvcFactory
    ) {
        this.prefs = prefs;
        this.listener = listener;
        this.viewMvcFactory = viewMvcFactory;
        this.selectionHandler = selectionHandler;
        sortHandler = new ListItemSortHandler<>(prefs);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListsListItemViewMvc listItemViewMvc = viewMvcFactory.getListsListItemViewMvc(parent);
        listItemViewMvc.registerListener(this);
        return new ViewHolder(listItemViewMvc);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.viewMvc.bindList(lists.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (lists != null) return lists.size();
        return 0;
    }

    @Override
    public void onListClick(ListWithShows list) {
        listener.onListClick(list);
    }

    @Override
    public void onListLongClick(ListWithShows list, int position) {
        selectionHandler.handleSelection(list.getId());
        notifyItemChanged(position);
    }

    @Override
    public void onDrag(int fromPosition, int toPosition) {
        if (prefs.getInt(LIST_SORT_MODE, SORT_BY_CUSTOM) == SORT_BY_CUSTOM) {
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onDrop(int fromPosition, int toPosition) {
        ListEntity toMove = lists.get(fromPosition).getList();
        ListEntity target = lists.get(toPosition).getList();
        listener.onListDragAndDrop(toMove, target);
        selectionHandler.unselectItem(toMove.getId());

        if (prefs.getInt(LIST_SORT_MODE, SORT_BY_CUSTOM) != SORT_BY_CUSTOM) {
            notifyItemChanged(fromPosition);
        }
    }

    public void bindLists(List<ListWithShows> lists) {
        sortHandler.setItems(lists);
        this.lists = sortHandler.getSortedItems();
        notifyDataSetChanged();
    }

    public void sortItems(int sortBy) {
        lists = sortHandler.getSortedItems(sortBy);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ListsListItemViewMvc viewMvc;

        public ViewHolder(ListsListItemViewMvc viewMvc) {
            super(viewMvc.getRootView());
            this.viewMvc = viewMvc;
        }
    }
}
