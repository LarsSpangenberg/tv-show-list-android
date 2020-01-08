package com.example.showtracker.screens.common.activities;

import androidx.appcompat.app.*;

import com.example.showtracker.common.*;
import com.example.showtracker.common.dependencyinjection.application.*;
import com.example.showtracker.common.dependencyinjection.presentation.*;

public class BaseActivity extends AppCompatActivity {
    private boolean isInjectorUsed;

    protected PresentationComponent getPresentationComponent() {
        if (isInjectorUsed) {
            throw new RuntimeException("no need to use injector more than once");
        }
        isInjectorUsed = true;
        return getApplicationComponent().newPresentationComponent(new PresentationModule(this));
    }

    private ApplicationComponent getApplicationComponent() {
        return ((CustomApplication) getApplication()).getApplicationComponent();
    }
}
