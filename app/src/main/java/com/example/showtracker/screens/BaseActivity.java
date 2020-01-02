package com.example.showtracker.screens;

import androidx.appcompat.app.*;

import com.example.showtracker.*;
import com.example.showtracker.common.dependencyinjection.application.*;
import com.example.showtracker.common.dependencyinjection.presentation.*;

public class BaseActivity extends AppCompatActivity {
    protected PresentationComponent getPresentationComponent() {
        return getApplicationComponent().newPresentationComponent(new PresentationModule());
    }

    private ApplicationComponent getApplicationComponent() {
        return ((MyApplication) getApplication()).getApplicationComponent();
    }
}
