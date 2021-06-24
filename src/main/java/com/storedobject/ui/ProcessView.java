package com.storedobject.ui;

import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.Component;

public abstract class ProcessView extends TextView {

    protected ButtonLayout buttonPanel;
    protected Button proceedButton, cancelButton;

    public ProcessView(String caption) {
        super(caption);
    }

    @Override
    protected Component getTopComponent() {
        if(buttonPanel == null) {
            buttonPanel = new ButtonLayout();
        }
        if(proceedButton == null) {
            proceedButton = new Button("Proceed", "ok", this);
        }
        if(cancelButton == null) {
            cancelButton = new Button("Cancel", this);
        }
        buttonPanel.add(proceedButton, cancelButton);
        return buttonPanel;
    }

    @Override
    protected void startProcess() {
    }

    @Override
    public void clicked(Component c) {
        if(c == cancelButton) {
            close();
            return;
        }
        if(c == proceedButton) {
            proceedButton = null;
            super.startProcess();
            return;
        }
        if(proceedButton == null) {
            warning("Already processed... Please exit by pressing 'Cancel' now...");
        }
    }
}
