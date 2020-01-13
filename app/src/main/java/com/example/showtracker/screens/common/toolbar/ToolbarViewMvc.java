package com.example.showtracker.screens.common.toolbar;

import android.view.*;

import androidx.annotation.*;
import androidx.appcompat.widget.*;

import com.example.showtracker.*;
import com.example.showtracker.screens.common.utils.*;
import com.example.showtracker.screens.common.views.*;

public class ToolbarViewMvc extends BaseObservableViewMvc<ToolbarViewMvc.MenuItemClickListener> {
    private NavigateUpClickListener navigateUpClickListener;
    private ListItemSelectionHandler selectionHandler;
    private Toolbar toolbar;

    public interface MenuItemClickListener {
        void onMenuItemClick(MenuItem menuItem);
    }

    public interface NavigateUpClickListener {
        void onNavigateUpClicked();
    }

    public ToolbarViewMvc(ListItemSelectionHandler selectionHandler, Toolbar toolbar) {
        this.toolbar = toolbar;
        this.selectionHandler = selectionHandler;

        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateUpClickListener.onNavigateUpClicked();
            }
        });
    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    public void inflateMenu(@MenuRes int menuId) {
        toolbar.inflateMenu(menuId);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                for (MenuItemClickListener listener: getListeners()) {
                    listener.onMenuItemClick(item);
                }
                return true;
            }
        });
    }

    public void enableUpButtonAndListen(NavigateUpClickListener navigateUpClickListener) {
        this.navigateUpClickListener = navigateUpClickListener;
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
    }

    public void enableDeleteButton(@IdRes int deleteButtonId) {
        selectionHandler.enableDeleteButton(findMenuItem(deleteButtonId));
    }

    public void enableEditButton(@IdRes int editButtonId) {
        selectionHandler.setEditButton(findMenuItem(editButtonId));
    }

    public MenuItem findMenuItem(int id) {
        return getMenu().findItem(id);
    }

    private Menu getMenu() {
        return toolbar.getMenu();
    }
}
