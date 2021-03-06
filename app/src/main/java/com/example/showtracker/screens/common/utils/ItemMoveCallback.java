package com.example.showtracker.screens.common.utils;

import android.util.*;

import androidx.annotation.*;
import androidx.recyclerview.widget.*;

public class ItemMoveCallback extends ItemTouchHelper.Callback {

    private static final String TAG = "ItemMoveCallback";
    private final Listener listener;
    private int currentPosition = -1;

    public interface Listener {
        void onDrag(int fromPosition, int toPosition);
        void onDrop(int fromPosition, int toPosition);
    }

    public ItemMoveCallback(Listener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(
        @NonNull RecyclerView recyclerView,
        @NonNull RecyclerView.ViewHolder viewHolder
    ) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(
        @NonNull RecyclerView recyclerView,
        @NonNull RecyclerView.ViewHolder viewHolder,
        @NonNull RecyclerView.ViewHolder target
    ) {
        Log.d(TAG, "onMove: moving item " + viewHolder.getAdapterPosition() + " to position " + target.getAdapterPosition());
        if (this.currentPosition == -1) {
            this.currentPosition = viewHolder.getAdapterPosition();
        }
        this.listener.onDrag(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }


    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            Log.d(TAG, "onSelectedChanged: select changed for item " + viewHolder.getAdapterPosition());
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(
        @NonNull RecyclerView recyclerView,
        @NonNull RecyclerView.ViewHolder viewHolder
    ) {
        Log.d(TAG, "clearView: position " + viewHolder.getAdapterPosition());
        if (currentPosition != -1) {
            listener.onDrop(currentPosition, viewHolder.getAdapterPosition());
            currentPosition = -1;
        }
        super.clearView(recyclerView, viewHolder);
    }
}
