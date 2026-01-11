package com.minghua.organizer.service;

import com.minghua.organizer.record.MigrationSummary;
import com.minghua.organizer.service.MigrationListener;

import java.util.ArrayList;
import java.util.List;

class TestListener implements MigrationListener {
    MigrationSummary summary;
    List<String> infos = new ArrayList<>();
    List<String> errors = new ArrayList<>();

    @Override
    public void onInfo(String message) {
        infos.add(message);
    }

    @Override
    public void onError(String message) {
        errors.add(message);
    }

    @Override
    public void onComplete(MigrationSummary summary) {
        this.summary = summary;
    }
}