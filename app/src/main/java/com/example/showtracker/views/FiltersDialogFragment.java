package com.example.showtracker.views;

import android.app.*;
import android.content.*;
import android.os.*;

import androidx.annotation.*;
import androidx.fragment.app.DialogFragment;

public class FiltersDialogFragment extends DialogFragment {
    String[] items;
    boolean[] checkedItems;

    public FiltersDialogFragment(String[] items, boolean[] checkedItems) {
        this.items = items;
        this.checkedItems = checkedItems;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMultiChoiceItems(items, checkedItems, new DialogItemClickHandler());
        return builder.create();
    }

    public class DialogItemClickHandler implements DialogInterface.OnMultiChoiceClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {

        }
    }
}
