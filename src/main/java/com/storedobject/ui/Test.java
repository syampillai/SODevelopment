package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.Signature;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.Component;

public class Test extends ObjectGrid<Signature> {

    public Test() {
        super(Signature.class, StringList.create("Person", "Signature"));
        createComponentColumn("Signature", this::getSignature);
        load();
    }

    public Component getSignature(Signature si) {
        return new Image(si);
    }

    @Override
    public Component createHeader() {
        ButtonLayout bl = new ButtonLayout();
        bl.add(new Button("Exit", b -> close()));
        return bl;
    }
}
