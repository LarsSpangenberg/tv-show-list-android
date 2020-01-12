package com.example.showtracker.screens.common.dialogs;

import android.app.*;
import android.content.*;
import android.os.*;

import androidx.annotation.*;

public class FiltersDialog extends BaseDialog {
    public static final String TAG = "FiltersDialog";
    private static final String CHECKED_ITEMS = "CHECKED_ITEMS";
    private static final String FILTER_NAMES = "FILTER_NAMES";
    private static final String DIALOG_TITLE = "DIALOG_TITLE";

    private Listener listener;

    public interface Listener {
        void onItemClick(int which);
        void onPositiveButtonClicked();
    }

    public static FiltersDialog newInstance(
        String title,
        String[] filterNames,
        boolean[] checkedItems
    ) {
        FiltersDialog filtersDialog = new FiltersDialog();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, title);
        args.putStringArray(FILTER_NAMES, filterNames);
        args.putBooleanArray(CHECKED_ITEMS, checkedItems);
        filtersDialog.setArguments(args);
        return filtersDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = requireArguments();
        String title = args.getString(DIALOG_TITLE);
        final String[] filterNames = args.getStringArray(FILTER_NAMES);
        final boolean[] checkedItems = args.getBooleanArray(CHECKED_ITEMS);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMultiChoiceItems(
            filterNames,
            checkedItems,
            new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    listener.onItemClick(which);
                }
            }
        );

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                listener.onPositiveButtonClicked();
            }
        });

        return builder.create();
    }


    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
