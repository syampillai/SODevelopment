package com.storedobject.ui;

import com.storedobject.vaadin.ApplicationMenu;
import com.storedobject.vaadin.ApplicationMenuItem;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;

@Tag("div")
public class Menu extends Component implements ApplicationMenu {

    public Menu() {
    }

    @Override
    public HasComponents getMenuPane() {
        return this;
    }

    @Override
    public void insert(int position, ApplicationMenuItem menuItem) {
    }

    @Override
    public void remove(ApplicationMenuItem menuItem) {
    }
}