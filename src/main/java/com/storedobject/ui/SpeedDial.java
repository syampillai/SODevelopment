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

    private final github.tobsef.vaadin.paperfab.SpeedDial speedDial;

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
        speedDial = new github.tobsef.vaadin.paperfab.SpeedDial();
        if(icon != null) {
            setIcon(icon);
        }
        if(clickHandler != null) {
            addClickHandler(clickHandler);
        }
    }

    public SpeedDial(VaadinIcon icon, ClickHandler clickHandler) {
        this((String)null, clickHandler);
        if(icon != null) {
            setIcon(icon);
        }
    }

    @Override
    protected github.tobsef.vaadin.paperfab.SpeedDial initContent() {
        return speedDial;
    }

    @Override
    public void setIcon(String icon) {
        if(icon != null) {
            speedDial.getElement().setProperty("icon", icon);
        }
    }

    @Override
    public String getIcon() {
        return speedDial.getElement().getProperty("icon");
    }

    public void open() {
        speedDial.open();
    }

    public void close() {
        speedDial.close();
    }

    public Registration addClickHandler(ClickHandler clickHandler) {
        assert clickHandler != null;
        return speedDial.addClickListener(e -> clickHandler.onComponentEvent(new ClickEvent<>(speedDial)));
    }

    public SpeedDialAction addMenuItem(String item, VaadinIcon icon) {
        return addMenuItem(item, icon(icon));
    }

    public SpeedDialAction addMenuItem(String item, VaadinIcon icon, ClickHandler clickHandler) {
        return addMenuItem(item, icon(icon), clickHandler);
    }

    public SpeedDialAction addMenuItem(String item, String icon) {
        return addMenuItem(item, icon(icon));
    }

    public SpeedDialAction addMenuItem(String item, String icon, ClickHandler clickHandler) {
        return addMenuItem(item, icon(icon), clickHandler);
    }

    public SpeedDialAction addMenuItem(String item, com.vaadin.flow.component.icon.Icon icon) {
        return sanitize(speedDial.addMenuItem(item, icon(icon)));
    }

    public SpeedDialAction addMenuItem(String item, com.vaadin.flow.component.icon.Icon icon, ClickHandler clickHandler) {
        if(clickHandler == null) {
            return addMenuItem(item, icon);
        }
        return sanitize(speedDial.addMenuItem(item, icon(icon), e -> clickHandler.onComponentEvent(new ClickEvent<>(e.getSource()))));
    }

    private static com.vaadin.flow.component.icon.Icon icon(com.vaadin.flow.component.icon.Icon icon) {
        //return icon == null ? new Icon(VaadinIcon.ABACUS) : icon;
        return null;
    }

    private static com.vaadin.flow.component.icon.Icon icon(String icon) {
        //return icon == null ? new Icon(VaadinIcon.ABACUS) : new Icon(icon);
        return null;
    }

    private static com.vaadin.flow.component.icon.Icon icon(VaadinIcon icon) {
        //return icon == null ? new Icon(VaadinIcon.ABACUS) : new Icon(icon);
        return null;
    }

    private static SpeedDialAction sanitize(SpeedDialAction sda) {
        sda.setColorActionLabelText("#FFFFFF");
        sda.setColorActionLabel("#000000");
        return sda;
    }
}
