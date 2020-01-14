package com.example.showtracker.screens.tags.taglistitem;

import android.view.*;
import android.widget.*;

import androidx.annotation.*;

import com.example.showtracker.*;
import com.example.showtracker.data.tags.entities.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.views.*;

public class TagListItemViewMvcImpl extends BaseObservableViewMvc<TagListItemViewMvc.Listener>
    implements TagListItemViewMvc {
    private TextView name;

    private ListItemSelectionHandler selectionHandler;

    public TagListItemViewMvcImpl(
        LayoutInflater inflater,
        ListItemSelectionHandler selectionHandler,
        @NonNull ViewGroup parent
    ) {
        this.selectionHandler = selectionHandler;
        setRootView(inflater.inflate(R.layout.tag_list_item, parent, false));
        name = findViewById(R.id.tag_name);
    }

    @Override
    public void bindTag(final Tag tag, final int position) {
        name.setText(tag.name);

        getRootView().setSelected(selectionHandler.getSelectionIds().contains(tag.getId()));
        getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Listener listener : getListeners()) {
                    listener.onTagClick(tag);
                }
            }
        });
        getRootView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (Listener listener : getListeners()) {
                    listener.onTagLongClick(tag, position);
                }
                return true;
            }
        });
    }
}
