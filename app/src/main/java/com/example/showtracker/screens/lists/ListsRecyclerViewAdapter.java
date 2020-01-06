package com.example.showtracker.screens.lists;

import android.content.*;
import android.util.*;
import android.view.*;

import androidx.annotation.*;
import androidx.recyclerview.widget.*;

import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.lists.listslistitem.*;

import java.util.*;

import static com.example.showtracker.screens.common.utils.ListItemSortHandler.LIST_SORT_MODE;
import static com.example.showtracker.screens.common.utils.ListItemSortHandler.SORT_BY_CUSTOM;

public class ListsRecyclerViewAdapter
    extends RecyclerView.Adapter<ListsRecyclerViewAdapter.ViewHolder>
    implements ItemMoveCallback.ItemTouchListener, ListsListItemViewMvc.Listener {
    private static final String TAG = "ListsRecyclerViewAdapte";

    private List<ListWithShows> lists;
    private SharedPreferences prefs;
    private ListItemSelectionHandler selectionHandler;
    private Listener listener;
    private LayoutInflater inflater;

    public interface Listener {
        void onListClick(ListWithShows list);
        boolean onListDragAndDrop(ListEntity toMove, ListEntity target);
    }

    public ListsRecyclerViewAdapter(
        LayoutInflater inflater,
        SharedPreferences prefs,
        ListItemSelectionHandler selectionHandler,
        Listener listener
    ) {
        this.inflater = inflater;
        this.prefs = prefs;
        this.selectionHandler = selectionHandler;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListsListItemViewMvc viewMvc = new ListsListItemViewMvcImpl(inflater, parent);
        viewMvc.registerListener(this);
        return new ViewHolder(viewMvc);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (lists != null) {
            ListWithShows current = lists.get(position);
            holder.viewMvc.bindList(
                current,
                position,
                selectionHandler.isSelected(current.getId())
            );
        }
    }

    @Override
    public int getItemCount() {
        if (lists != null) return lists.size();
        return 0;
    }

    @Override
    public void onDrag(int fromPosition, int toPosition) {
        if (prefs.getInt(LIST_SORT_MODE, SORT_BY_CUSTOM) == SORT_BY_CUSTOM) {
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onDrop(int fromPosition, int toPosition) {
        ListEntity toMove = lists.get(fromPosition).list;
        ListEntity target = lists.get(toPosition).list;
        boolean itemMoved = listener.onListDragAndDrop(toMove, target);
        Log.d(TAG, "onDrop: " + itemMoved);
        if (!itemMoved) notifyDataSetChanged();
    }

    public void bindLists(List<ListWithShows> lists) {
        this.lists = lists;
        sortItems();
        notifyDataSetChanged();
    }

    public void sortItems() {
        ListItemSortHandler.sortItems(prefs, lists);
    }

    public void sortItems(int sortBy) {
        ListItemSortHandler.sortItems(prefs, sortBy, lists);
        notifyDataSetChanged();
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ListsListItemViewMvc viewMvc;

        public ViewHolder(ListsListItemViewMvc viewMvc) {
            super(viewMvc.getRootView());
            this.viewMvc = viewMvc;
        }
    }
}
