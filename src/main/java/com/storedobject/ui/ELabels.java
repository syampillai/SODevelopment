package com.storedobject.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public class ELabels extends Div implements StyledBuilder {

    public ELabels() {
        this(null);
    }

    public ELabels(String text) {
        this(text, new String[0]);
    }

    public ELabels(Object object, String... style) {
        setValue(object, style);
    }

    @Override
    public Application getApplication() {
        return null;
    }

    @Override
    public void setText(String text) {
        setValue((Object)text);
    }

    @Override
    public StyledBuilder newLine(boolean force) {
        return this;
    }

    @Override
    public StyledBuilder getInternalStyledBuilder() {
        return null;
    }

    @Override
    public void setValue(Object object, String... style) {
    }

    @Override
    public void setValue(String value) {
        setValue((Object)value);
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public StyledBuilder update() {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return getChildren().findAny().isPresent();
    }

    @Override
    public StyledBuilder append(Object anything) {
        return this;
    }

    @Override
    public StyledBuilder append(Object anything, String color) {
        return this;
    }

    @Override
    public StyledBuilder append(Object anything, String... style) {
        return this;
    }

    @Override
    public StyledBuilder appendHTML(String html) {
        return this;
    }

    @Override
    public void clear() {
        removeAll();
    }

    @Override
    public StyledBuilder clearContent() {
        return this;
    }

    @Override
    public StyledBuilder space(int count) {
        return this;
    }

    @Override
    public StyledBuilder drawLine() {
        return this;
    }

    @Override
    public void add(Component... components) {
    }

    @Override
    public void removeAll() {
    }

    @Override
    public void remove(Component... components) {
    }
}
