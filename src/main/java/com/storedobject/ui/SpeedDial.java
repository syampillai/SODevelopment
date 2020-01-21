package com.storedobject.ui;

import com.storedobject.vaadin.ClickHandler;
import com.storedobject.vaadin.HasIcon;
import com.storedobject.vaadin.Icon;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.shared.Registration;
import github.tobsef.vaadin.paperfab.SpeedDialAction;

public class SpeedDial extends Composite<github.tobsef.vaadin.paperfab.SpeedDial> implements HasIcon {

    private github.tobsef.vaadin.paperfab.SpeedDial s;

    public SpeedDial() {
        this((String)null);
    }

    public SpeedDial(String icon) {
        this(icon, null);
    }

    public SpeedDial(VaadinIcon icon) {
        this(icon, null);
    }

    public SpeedDial(String icon, ClickHandler clickHandler) {
    }

    public SpeedDial(VaadinIcon icon, ClickHandler clickHandler) {
    }

    public void open() {
    }

    public void close() {
    }

    public Registration addClickHandler(ClickHandler clickHandler) {
        return s.addClickListener(e -> clickHandler.onComponentEvent(new ClickEvent<>(s)));
    }

    public SpeedDialAction addMenuItem(String item, VaadinIcon icon) {
        return s.addMenuItem(item, icon);
    }

    public SpeedDialAction addMenuItem(String item, VaadinIcon icon, ClickHandler clickHandler) {
        return s.addMenuItem(item, icon);
    }

    public SpeedDialAction addMenuItem(String item, String icon) {
        return s.addMenuItem(item, (Icon)null);
    }

    public SpeedDialAction addMenuItem(String item, String icon, ClickHandler clickHandler) {
        return addMenuItem(item, (Icon)null, clickHandler);
    }

    public SpeedDialAction addMenuItem(String item, com.vaadin.flow.component.icon.Icon icon) {
        return s.addMenuItem(item, icon);
    }

    public SpeedDialAction addMenuItem(String item, com.vaadin.flow.component.icon.Icon icon, ClickHandler clickHandler) {
        return s.addMenuItem(item, icon);
    }
}
