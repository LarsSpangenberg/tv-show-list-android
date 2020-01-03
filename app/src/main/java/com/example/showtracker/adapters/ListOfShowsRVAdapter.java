package com.example.showtracker.adapters;

import android.content.*;
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

import static com.example.showtracker.screens.BaseListActivity.SORT_BY_CUSTOM;
import static com.example.showtracker.screens.BaseListActivity.SORT_BY_NAME;
import static com.example.showtracker.views.MainActivity.LIST_SORT_MODE;

public class ListOfShowsRVAdapter
    extends RecyclerView.Adapter<ListOfShowsRVAdapter.ListViewHolder>
    implements ItemMoveCallback.ItemTouchListener {
    private static final String TAG = "ListOfShowsRVAdapter";
    private List<ListWithShows> lists;
//    private List<ListOfShows> selection;
    private List<String> selectionIds;
    private ListsRVEventListener eventListener;
    private Context context;

    public interface ListsRVEventListener {
        void onListClick(@NonNull ListWithShows list);
        void onListLongClick(@NonNull ListWithShows list, int position);
        boolean onItemMoved(@NonNull ListOfShows toMove, @NonNull ListOfShows target);
    }

    public ListOfShowsRVAdapter(Context context, ListsRVEventListener listener) {
        this.context = context;
        this.eventListener = listener;
        this.selectionIds = new ArrayList<>();
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.lists_list_item, parent, false);
        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: starts");
        Log.d(TAG, "onBindViewHolder: " + this.selectionIds.toString());
        if (this.lists != null) {
            ListWithShows current = this.lists.get(position);
            Log.d(TAG, "onBindViewHolder: current is " + current.list.name);
            Log.d(TAG, "onBindViewHolder: selected " + this.selectionIds.contains(current.list.id));
            holder.bind(
                current,
                position,
                this.selectionIds.contains(current.list.id),
                this.eventListener
            );
        } else {
            holder.listName.setText("No lists available");
        }
    }

    @Override
    public int getItemCount() {
        if (this.lists != null) return this.lists.size();
        return 0;
    }

    @Override
    public void onDrag(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onDrop(int fromPosition, int toPosition) {
        ListOfShows toMove = this.lists.get(fromPosition).list;
        ListOfShows target = this.lists.get(toPosition).list;
        boolean itemMoved = this.eventListener.onItemMoved(toMove, target);
        if (!itemMoved) notifyItemChanged(fromPosition);
    }

    public void setLists(List<ListWithShows> lists) {
        this.lists = lists;
        sortItems();
        notifyDataSetChanged();
    }

//    public void setSelection(List<ListOfShows> selection) {
//        Log.d(TAG, "setSelection: starts");
//        this.selection = selection;
//    }

    public ListOfShows findListById(String listId) {
        for (ListWithShows list : this.lists) {
            if (list.list.id.equals(listId)) return list.list;
        }
        return null;
    }

    public void setSelection(List<String> selectionIds) {
        Log.d(TAG, "setSelection: starts");
        this.selectionIds = selectionIds;
    }

    public void resetSelection() {
        this.selectionIds.clear();
        notifyDataSetChanged();
    }

    public void sortItems() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        sortItems(prefs.getInt(LIST_SORT_MODE, SORT_BY_CUSTOM));
    }

    public void sortItems(int sortBy) {
        Comparator<ListWithShows> comparator = null;
        switch (sortBy) {
            case SORT_BY_NAME:
                comparator = new Comparator<ListWithShows>() {
                    @Override
                    public int compare(ListWithShows o1, ListWithShows o2) {
                        String name1 = o1.list.name;
                        String name2 = o2.list.name;
                        return name1.compareToIgnoreCase(name2);
                    }
                };
                break;
            case SORT_BY_CUSTOM:
                comparator = new Comparator<ListWithShows>() {
                    @Override
                    public int compare(ListWithShows o1, ListWithShows o2) {
                        int position1 = o1.list.position;
                        int position2 = o2.list.position;
                        return position1 - position2;
                    }
                };
                break;
        }

        if (comparator != null) {
            Collections.sort(this.lists, comparator);
            notifyDataSetChanged();
        }
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        private final TextView listName;
        private final TextView showCount;

        ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.listName = itemView.findViewById(R.id.tag_name);
            this.showCount = itemView.findViewById(R.id.lists_show_count);
        }

        void bind(
            @NonNull final ListWithShows list,
            final int position,
            boolean isSelected,
            final ListsRVEventListener listener
        ) {
            itemView.setSelected(isSelected);
            listName.setText(list.list.name);
            showCount.setText(String.valueOf(list.getShowCount()));


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onListClick(list);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onListLongClick(list, position);
                    return true;
                }
            });

//            FIXME cardview and text turns purple on focus with tab key (now it crashes the app)
//            cardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (hasFocus) cardView.setfoc(true);
//                    else cardView.setSelected(false);
//                }
//            });
        }
    }
}
