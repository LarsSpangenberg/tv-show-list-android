package com.example.showtracker.screens.common.views;

import java.util.*;

public abstract class BaseObservableViewMvc<ListenerType> extends BaseViewMvc
    implements ObservableViewMvc<ListenerType> {

    private Set<ListenerType> listeners = new HashSet<>();

    @Override
    public void registerListener(ListenerType listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterListener(ListenerType listener) {
        listeners.remove(listener);
    }

    protected Set<ListenerType> getListeners() {
        return Collections.unmodifiableSet(listeners);
    }
}
