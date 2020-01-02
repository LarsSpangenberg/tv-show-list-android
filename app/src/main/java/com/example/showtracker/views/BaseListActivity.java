package com.example.showtracker.views;

import android.util.*;
import android.view.*;

import com.example.showtracker.*;
import com.example.showtracker.screens.*;

import java.util.*;

// TODO: refactor logic in this class to use composition instead of inheritance

/**
 * extend this class if an activity needs methods to handle selection of data (onLongClick) for
 * this app. Usually used along with edit and delete in a RecyclerView
 *
 * if the delete and/or edit button is set with a MenuItem then this class will also handle the
 * visibility of said buttons.
 **/
public abstract class BaseListActivity extends BaseActivity {
    private static final String TAG = "BaseListActivity";
    public static final int SORT_BY_NAME = 0;
    public static final int SORT_BY_CUSTOM = 1;
    private List<String> selectionIds = new ArrayList<>();
    private MenuItem deleteButton;
    private MenuItem editButton;


    List<String> getSelection() {
        return this.selectionIds;
    }

    void handleSelection(String itemId) {

        Log.d(TAG, "handleSelection: selected " + this.selectionIds.contains(itemId));
        Log.d(TAG, "handleSelection: " + this.selectionIds.toString());
        if (this.selectionIds.contains(itemId)) this.selectionIds.remove(itemId);
        else this.selectionIds.add(itemId);

        if (this.deleteButton != null) handleDeleteIcon();
        if (this.editButton != null) handleEditIcon();
    }

    void unselectItem(String itemId) {
        this.selectionIds.remove(itemId);

        if (this.deleteButton != null) handleDeleteIcon();
        if (this.editButton != null) handleEditIcon();
    }

    void resetSelection() {
//        this.selection.clear();
        this.selectionIds.clear();
        if (this.deleteButton != null) this.deleteButton.setVisible(false);
        if (this.editButton != null) this.editButton.setVisible(false);
    }

    void setDeleteButton(MenuItem deleteButton) {
        this.deleteButton = deleteButton;
        if (this.deleteButton != null) handleDeleteIcon();
    }

    void setEditButton(MenuItem editButton) {
        this.editButton = editButton;
        if (this.editButton != null) handleEditIcon();
    }

    /**
     * if no items are selected, don't show the delete icon.
     * if one item is selected, show the delete icon.
     * if more than one item is selected, show the multiple delete icon.
     **/
    private void handleDeleteIcon() {
        int selectedCount = this.selectionIds.size();
        if (selectedCount == 0) {
            if (this.deleteButton.isVisible()) this.deleteButton.setVisible(false);
        } else {
            if (!this.deleteButton.isVisible()) this.deleteButton.setVisible(true);
            if (selectedCount == 1) {
                this.deleteButton.setIcon(R.drawable.ic_delete_black_24dp);
            } else {
                this.deleteButton.setIcon(R.drawable.ic_delete_sweep_black_24dp);
            }
        }
    }

    /**
     * show the edit icon only when one item is selected
     **/
    private void handleEditIcon() {
        if (this.selectionIds.size() == 1) {
            if (!this.editButton.isVisible()) this.editButton.setVisible(true);
        } else if (this.editButton.isVisible()) {
            this.editButton.setVisible(false);
        }
    }
}
