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
        return null;
    }

    @Override
    protected void startProcess() {
    }
}
