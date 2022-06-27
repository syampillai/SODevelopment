package com.storedobject.ui;

import com.storedobject.vaadin.*;

public class Test extends DataForm {

    public Test() {
        super("Test");
        add(new ELabel().appendHTML("<script><html><style><body><head>Hello</p>").update());
    }

    @Override
    protected boolean process() {
        return false;
    }
}
