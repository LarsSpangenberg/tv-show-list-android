package com.example.showtracker.screens.common.dialogs;

import androidx.fragment.app.*;

import com.example.showtracker.common.*;
import com.example.showtracker.common.dependencyinjection.application.*;
import com.example.showtracker.common.dependencyinjection.presentation.*;

public abstract class BaseDialog extends DialogFragment {
    private boolean isInjectorUsed;

    protected PresentationComponent getPresentationComponent() {
        if (isInjectorUsed) {
            throw new RuntimeException("no need to use injector more than once");
        }
        isInjectorUsed = true;

        return getApplicationComponent().newPresentationComponent(
            new PresentationModule(requireActivity())
        );
    }

    private ApplicationComponent getApplicationComponent() {
        return ((CustomApplication) requireActivity().getApplication()).getApplicationComponent();
    }
}
