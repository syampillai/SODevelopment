package com.storedobject.ui;

import com.storedobject.core.Person;
import com.storedobject.vaadin.MenuBar;
import com.storedobject.vaadin.MenuBarItem;
import com.vaadin.flow.component.Component;

public class Test extends ObjectGrid<Person> {

    public Test() {
        super(Person.class);
        load();
   }

    @Override
    public Component createHeader() {
        MenuBar m = new MenuBar();
        m.addMenuItem("One", c -> message("One " + ((MenuBarItem)c).isChecked()));
        MenuBarItem mi = m.addMenuItem("Two");
        MenuBarItem p = mi;
        mi = mi.addMenuItem("Another", c -> {
            message("One " + ((MenuBarItem)c).isChecked());
            if(!((MenuBarItem)c).isChecked()) {
                p.remove((MenuBarItem)c);
            }
        });
        mi.setCheckable(true);
        return m;
    }
}
