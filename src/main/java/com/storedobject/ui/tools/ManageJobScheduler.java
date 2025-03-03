package com.storedobject.ui.tools;

import com.storedobject.core.JSON;
import com.storedobject.job.Scheduler;
import com.storedobject.ui.JSONField;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TimerComponent;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.icon.VaadinIcon;

public class ManageJobScheduler extends DataForm {

    private final JSONField statusField = new JSONField("Scheduler Status");
    TimerComponent timer = new TimerComponent();

    public ManageJobScheduler() {
        super("Manage Job Scheduler");
        addField(statusField);
        statusField.setMaxHeight("40vh");
        statusField.setMinWidth("70vw");
        add(timer);
        timer.setPrefix("Restarting in ");
        timer.setSuffix(" seconds");
        timer.addListener(e -> restarted());
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

    private void restarted() {
        timer.setVisible(false);
        buttonPanel.setEnabled(true);
        showStatus();
    }

    private void showStatus() {
        statusField.setValue(JSON.create(Scheduler.getStatus()));
    }

    @Override
    protected boolean process() {
        buttonPanel.setEnabled(false);
        timer.setVisible(true);
        timer.countDown(10);
        Thread.startVirtualThread(Scheduler::restart);
        return false;
    }
}
