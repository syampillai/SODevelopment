package com.storedobject.ui.tools;

import com.storedobject.core.JSON;
import com.storedobject.job.Scheduler;
import com.storedobject.ui.JSONField;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.icon.VaadinIcon;

public class ManageJobScheduler extends DataForm {

    private final JSONField statusField = new JSONField("Scheduler Status");

    public ManageJobScheduler() {
        super("Manage Job Scheduler");
        addField(statusField);
        setFieldReadOnly(statusField);
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        ok.setText("Restart");
        cancel.setText("Quit");
        buttonPanel.removeAll();
        buttonPanel.add(ok, new Button("Status", VaadinIcon.CURLY_BRACKETS, e -> showStatus()), cancel);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        super.execute(parent, doNotLock);
        showStatus();
    }

    private void showStatus() {
        statusField.setValue(JSON.create(Scheduler.getStatus()));
    }

    @Override
    protected boolean process() {
        Scheduler.restart();
        return false;
    }
}
