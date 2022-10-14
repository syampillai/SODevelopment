package com.storedobject.ui;

import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;

public class Test extends DataForm {

    public Test() {
        super("Chart");
        FileField ff = new FileField("Doc");
        addField(ff);
        ff.allowDownload();
        add(new Button("Test", e -> ff.setReadOnly(!ff.isReadOnly())));
    }

    @Override
    protected boolean process() {
        return true;
    }
}
