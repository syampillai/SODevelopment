package com.storedobject.ui;

import com.storedobject.common.JSON;
import com.storedobject.core.LogicRedirected;
import com.storedobject.core.Person;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class Test extends ObjectListEditor<Person> {

    public Test() {
        super(Person.class);
        load();
        buttonPanel.add(new Button("Exit", e -> close()));
    }
}
