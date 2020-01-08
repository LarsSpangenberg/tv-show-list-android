package com.example.showtracker.screens.lists.listslistitem;

import android.view.*;
import android.widget.*;

import com.example.showtracker.*;
import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.views.*;

public class ListsListItemViewMvcImpl extends BaseObservableViewMvc<ListsListItemViewMvc.Listener>
    implements ListsListItemViewMvc {

    private final TextView listName;
    private final TextView showCount;
    private ListItemSelectionHandler selectionHandler;

    public ListsListItemViewMvcImpl(
        LayoutInflater inflater,
        ListItemSelectionHandler selectionHandler,
        ViewGroup parent
    ) {
        this.selectionHandler = selectionHandler;
        setRootView(inflater.inflate(R.layout.lists_list_item, parent, false));
        listName = findViewById(R.id.tag_name);
        showCount = findViewById(R.id.lists_show_count);
    }

    @Override
    public void bindList(final ListWithShows list, final int position) {
        View rootView = getRootView();
        setSelected(list.getId());
        listName.setText(list.getName());
        showCount.setText(String.valueOf(list.getShowCount()));

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Listener listener : getListeners()) {
                    listener.onListClick(list);
                }
            }
        });
        rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (Listener listener : getListeners()) {
                    listener.onListLongClick(list, position);
                }
                return true;
            }
        });
    }

    private void setSelected(String id) {
        getRootView().setSelected(selectionHandler.isSelected(id));
    }
}
