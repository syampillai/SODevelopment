package com.storedobject.ui.common;

import com.storedobject.mail.MailSender;
import com.storedobject.ui.ObjectEditor;
import com.vaadin.flow.component.HasValue;

import java.util.stream.Stream;

public class ManageMailSenders extends ObjectEditor<MailSender> {

    public ManageMailSenders() {
        super(MailSender.class);
    }

    @Override
    public Stream<String> getFieldNames() {
        return null;
    }

    @Override
    protected HasValue<?, ?> createField(String fieldName) {
        return null;
    }
}
