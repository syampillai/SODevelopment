package com.storedobject.ui;

import com.storedobject.common.Action;
import com.storedobject.common.StyledBuilder;

public class Alert extends com.storedobject.vaadin.Alert implements StyledBuilder {

    public Alert(String htmlText) {
        this(htmlText, null);
    }

    public Alert(String htmlText, Action action) {
        super(new ELabel(htmlText), action);
    }

    @Override
    public ELabel getContent() {
        return (ELabel)super.getContent();
    }

    @Override
    public StyledBuilder append(Object object) {
        getContent().append(object);
        return this;
    }

    @Override
    public StyledBuilder append(Object object, String color) {
        getContent().append(object, color);
        return this;
    }

    @Override
    public StyledBuilder append(Object object, String... styles) {
        getContent().append(object, styles);
        return this;
    }

    @Override
    public boolean isEmpty() {
        return getContent().isEmpty();
    }

    @Override
    public StyledBuilder newLine() {
        getContent().newLine();
        return this;
    }

    @Override
    public StyledBuilder newLine(boolean force) {
        getContent().newLine(force);
        return this;
    }

    @Override
    public StyledBuilder update() {
        getContent().update();
        return this;
    }

    @Override
    public StyledBuilder clearContent() {
        getContent().clearContent();
        return this;
    }
}