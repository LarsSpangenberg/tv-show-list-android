package com.example.showtracker.screens.common.activities;

import android.app.*;
import android.content.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.ActionBar;

import com.example.showtracker.*;
import com.example.showtracker.data.lists.entities.*;
import com.example.showtracker.data.tags.entities.*;
import com.google.android.material.textfield.*;

import java.io.*;

import static android.content.Intent.*;

public class SimpleAddEditActivity extends BaseActivity {
    public static final int ACTIVITY_REQUEST_CODE_EDIT_LIST = 1;
    public static final int ACTIVITY_REQUEST_CODE_EDIT_TAG = 1;

    public static final String EXTRA_REPLY =
        "com.example.showtracker.view.SimpleAddEditActivity.REPLY";
    public static final String ITEM_TO_EDIT = "ITEM_TO_EDIT";
    public static final String ITEM_NAME = "ITEM_NAME";
    public static final String ITEM_TYPE = "ITEM_TYPE";
    public static final int TAG_ITEM = 0;
    public static final int LIST_ITEM = 1;
    public static final int RESULT_EMPTY = 2;

    public static void startRenameList(Activity activity, ListEntity list) {
            Intent intent = new Intent(activity, SimpleAddEditActivity.class);
            intent.putExtra(ITEM_TO_EDIT, list);
            intent.putExtra(ITEM_NAME, list.name);
            intent.putExtra(ITEM_TYPE, LIST_ITEM);
            intent.setAction(Intent.ACTION_EDIT);

            activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE_EDIT_LIST);
    }

    public static void startRenameTag(Activity activity, Tag tag) {
        Intent intent = new Intent(activity, SimpleAddEditActivity.class);
        intent.putExtra(ITEM_TO_EDIT, tag);
        intent.putExtra(ITEM_NAME, tag.name);
        intent.putExtra(ITEM_TYPE, TAG_ITEM);
        intent.setAction(Intent.ACTION_EDIT);

        activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE_EDIT_TAG);
    }

    public static void startNewList(Activity activity) {
        Intent intent = new Intent(activity, SimpleAddEditActivity.class);
        intent.putExtra(ITEM_TYPE, LIST_ITEM);
        activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE_EDIT_LIST);
    }

    public static void startNewTag(Activity activity) {
        Intent intent = new Intent(activity, SimpleAddEditActivity.class);
        intent.putExtra(ITEM_TYPE, TAG_ITEM);
        activity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE_EDIT_TAG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }

        final TextInputEditText textInput = findViewById(R.id.addedit_name_input);
        TextInputLayout nameLayout = findViewById(R.id.addedit_input_layout);
        Button saveButton = findViewById(R.id.addedit_save_button);

        final Intent intent = getIntent();
        final String action = intent.getAction();
        int itemType = intent.getIntExtra(ITEM_TYPE, -1);
        final Serializable item = intent.getSerializableExtra(ITEM_TO_EDIT);
        String name = intent.getStringExtra(ITEM_NAME);

        switch (itemType) {
            case TAG_ITEM:
                nameLayout.setHint(getText(R.string.addtag_name_hint));
                if (name == null) {
                    saveButton.setText(R.string.addtag_save_button_create);
                }
                break;
            case LIST_ITEM:
                nameLayout.setHint(getText(R.string.addlist_list_name_hint));
                if (name == null) {
                    saveButton.setText(R.string.addlist_save_button_create);
                }
                break;
            default:
                throw new IllegalArgumentException(
                    "ITEM_TYPE is a required value for this activity's intent"
                );
        }

        if (name != null) {
            saveButton.setText(R.string.addedit_save_button_edit);
            textInput.setText(name);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                Editable newItemName = textInput.getText();

                if (newItemName != null && !TextUtils.isEmpty(newItemName)) {
                    if (action != null && action.equals(ACTION_EDIT)) {
//                        existing item is edited
                        resultIntent.setAction(action);
                        resultIntent.putExtra(ITEM_TO_EDIT, item);
                        resultIntent.putExtra(EXTRA_REPLY, newItemName.toString());
                    } else {
//                        new item is added
                        resultIntent.putExtra(EXTRA_REPLY, newItemName.toString());
                    }
                    setResult(RESULT_OK, resultIntent);
                } else {
                    setResult(RESULT_EMPTY, resultIntent);
                }
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}
