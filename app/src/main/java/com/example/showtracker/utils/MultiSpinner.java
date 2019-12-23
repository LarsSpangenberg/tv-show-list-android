package com.example.showtracker.utils;

import android.content.*;
import android.util.*;
import android.widget.*;

import androidx.appcompat.app.*;
import androidx.appcompat.widget.*;

import java.util.*;


public class MultiSpinner extends AppCompatSpinner implements
    DialogInterface.OnMultiChoiceClickListener,
    DialogInterface.OnCancelListener {

    public interface OnItemClickListener {
        void onSpinnerItemClick(MultiSpinnerData item, boolean isSelected);
    }

    private List<MultiSpinnerData> all = new ArrayList<>();
    private boolean[] selected;
    private String defaultText;
    private OnItemClickListener listener;

    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(DialogInterface dialog, int i, boolean isChecked) {
        selected[i] = isChecked;
        listener.onSpinnerItemClick(this.all.get(i), isChecked);
    }



    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        buildSpinnerText();
    }

    @Override
    public boolean performClick() {
        super.performClick();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        int count = this.all.size();
        String[] listItemData = new String[count];

        for (int i = 0; i < count; i++) {
            listItemData[i] = this.all.get(i).toString();
        }

        builder.setMultiChoiceItems(listItemData, selected, this);
//        builder.setPositiveButton(
//            android.R.string.ok,
//            new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            }
//        );
        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    public void setItems(
        List<? extends MultiSpinnerData> selected,
        List<? extends MultiSpinnerData> all,
        String defaultText,
        OnItemClickListener listener
    ) {
        // order by selected first and unselected last,
        // sort order of each, selected and unselected, is preserved
        this.all.addAll(selected);

        for (MultiSpinnerData item : all) {
            if (!selected.contains(item)) {
                this.all.add(item);
            }
        }

        this.defaultText = defaultText;
        this.listener = listener;

        // set up selected items
        this.selected = new boolean[this.all.size()];
        for (int i = 0; i < selected.size(); i++) {
            this.selected[i] = true;
        }

        buildSpinnerText();
    }

    private void buildSpinnerText() {
        // build a spinner text out of the selected items
        StringBuilder spinnerBuilder = new StringBuilder();
        for (int i = 0; i < this.all.size(); i++) {
            if (this.selected[i]) {
                if (i == 0) {
                    spinnerBuilder.append(this.all.get(i));
                } else {
                    spinnerBuilder.append(", ");
                    spinnerBuilder.append(this.all.get(i));
                }
            }
        }
        String spinnerText;
        if (spinnerBuilder.length() == 0) {
            spinnerText = this.defaultText;
        } else {
            spinnerText = spinnerBuilder.toString();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            getContext(),
            android.R.layout.simple_spinner_item,
            new String[]{spinnerText}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(adapter);
    }
}

