package com.storedobject.ui;

import com.storedobject.core.EditorAction;
import com.storedobject.core.Entity;
import com.storedobject.core.Person;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.Tabs;
import com.storedobject.vaadin.View;

public class Test extends View implements CloseableView {

    public Test() {
        super("Multiple Object Editors");
        Tabs tabs = new Tabs();
        ObjectEditor<Person> pe = ObjectEditor.create(Person.class, EditorAction.ALL | EditorAction.NO_EXIT);
        tabs.createTab("Person", pe.getComponent());
        ObjectEditor<Entity> ee = ObjectEditor.create(Entity.class, EditorAction.ALL | EditorAction.NO_EXIT);
        tabs.createTab("Entity", ee.getComponent());
        setComponent(tabs);
    }
}