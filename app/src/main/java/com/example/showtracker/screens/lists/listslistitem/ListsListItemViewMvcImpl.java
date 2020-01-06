package com.example.showtracker.screens.lists.listslistitem;

import android.view.*;
import android.widget.*;

import com.example.showtracker.*;
import com.example.showtracker.data.lists.entities.*;

import java.util.*;

public class ListsListItemViewMvcImpl implements ListsListItemViewMvc {
    private final TextView listName;
    private final TextView showCount;

    private final View rootView;

    private List<Listener> listeners = new ArrayList<>(1);

    public ListsListItemViewMvcImpl(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.lists_list_item, parent, false);
        listName = findViewById(R.id.tag_name);
        showCount = findViewById(R.id.lists_show_count);
    }

    @Override
    public void bindList(
        final ListWithShows list,
        final int position,
        boolean isSelected
    ) {
        View itemView = getRootView();
        itemView.setSelected(isSelected);
        listName.setText(list.list.name);
        showCount.setText(String.valueOf(list.getShowCount()));


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Listener listener : listeners) {
                    listener.onListClick(list);
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (Listener listener : listeners) {
                    listener.onListLongClick(list, position);
                }
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

    private <T extends View> T findViewById(int id) {
        return getRootView().findViewById(id);
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }
}
