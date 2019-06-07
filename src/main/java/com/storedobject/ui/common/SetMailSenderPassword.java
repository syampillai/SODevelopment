package com.storedobject.ui.common;

import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;

public class SetMailSenderPassword extends DataForm implements Transactional {

    public SetMailSenderPassword() {
        super("Set Mail Sender Password");
    }

    @Override
    protected void buildFields() {
    }

    @Override
    protected boolean process() {
        return true;
    }
}