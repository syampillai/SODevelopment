package com.storedobject.ui.common;

import com.storedobject.vaadin.DataForm;

public class MailLog extends DataForm {

    public MailLog() {
        super("Mail Log");
    }

    @Override
    protected boolean process() {
        return true;
    }
}
