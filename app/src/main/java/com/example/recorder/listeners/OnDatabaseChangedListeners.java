package com.example.recorder.listeners;

public interface OnDatabaseChangedListeners {
    void onNewDatabaseEntryAdded();
    void onNewDatabaseEntryRemoved();
    void onNewDatabaseEntryRenamed();
}
