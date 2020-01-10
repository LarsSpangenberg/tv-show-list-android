package com.example.showtracker.screens.common.utils;

import android.view.*;

import com.example.showtracker.*;

import java.util.*;

/**
 * use in an activity through composition if it needs methods to handle selection of data
 * (onLongClick) for this app. Usually used along with edit and delete in a list.
 *
 * if the delete and/or edit button is set with a MenuItem then this class will also handle the
 * visibility and icon of said buttons.
 **/
public class ListItemSelectionHandler {
    private List<String> selectionIds = new ArrayList<>();
    private MenuItem deleteButton;
    private MenuItem editButton;

    public List<String> getSelectionIds() {
        return selectionIds;
    }

    public void handleSelection(String itemId) {
        if (selectionIds.contains(itemId)) selectionIds.remove(itemId);
        else selectionIds.add(itemId);

        if (deleteButton != null) handleDeleteIcon();
        if (editButton != null) handleEditIcon();
    }

    public void unselectItem(String itemId) {
        selectionIds.remove(itemId);

        if (deleteButton != null) handleDeleteIcon();
        if (editButton != null) handleEditIcon();
    }

    public void resetSelection() {
        selectionIds.clear();
        if (deleteButton != null) deleteButton.setVisible(false);
        if (editButton != null) editButton.setVisible(false);
    }

    public void enableDeleteButton(MenuItem deleteButton) {
        this.deleteButton = deleteButton;
        if (this.deleteButton != null) handleDeleteIcon();
    }

    public void setEditButton(MenuItem editButton) {
        this.editButton = editButton;
        if (this.editButton != null) handleEditIcon();
    }

    /**
     * if no items are selected, don't show the delete icon.
     * if one item is selected, show the delete icon.
     * if more than one item is selected, show the multiple delete icon.
     **/
    private void handleDeleteIcon() {
        int selectedCount = selectionIds.size();
        if (selectedCount == 0) {
            if (deleteButton.isVisible()) deleteButton.setVisible(false);
        } else {
            if (!deleteButton.isVisible()) deleteButton.setVisible(true);
            if (selectedCount == 1) {
                deleteButton.setIcon(R.drawable.ic_delete_black_24dp);
            } else {
                deleteButton.setIcon(R.drawable.ic_delete_sweep_black_24dp);
            }
        }
    }

    /**
     * show the edit icon only when one item is selected
     **/
    private void handleEditIcon() {
        if (selectionIds.size() == 1) {
            if (!editButton.isVisible()) editButton.setVisible(true);
        } else if (editButton.isVisible()) {
            editButton.setVisible(false);
        }
    }

    public boolean isSelected(String id) {
        return getSelectionIds().contains(id);
    }
}
