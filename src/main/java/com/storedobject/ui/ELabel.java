package com.storedobject.ui;

import com.storedobject.common.HTMLText;
import com.storedobject.vaadin.StyledText;
import com.vaadin.flow.component.HasText;

public class ELabel extends StyledText implements StyledBuilder, HasText {

    protected final HTMLText label = null;

    public ELabel() {
        this((String)null);
    }

    public ELabel(String text) {
        this(text, new String[0]);
    }

    public ELabel(Object object, String... style) {
        this(new HTMLText(object, style));
    }

    public ELabel(HTMLText text) {
        this(text, true);
    }

    protected ELabel(HTMLText text, boolean update) {
        super(null);
    }

    @Override
    public StyledBuilder newLine(boolean force) {
        return this;
    }

    @Override
    public StyledBuilder drawLine() {
        return this;
    }

    @Override
    public ELabel append(Object object, String... style) {
        return this;
    }

    @Override
    public ELabel append(Object object, String color) {
        return this;
    }

    @Override
    public ELabel append(Object object) {
        return this;
    }

    @Override
    public ELabel appendHTML(String html) {
        return this;
    }

    @Override
    public ELabel space(int count) {
        return this;
    }

    @Override
    public ELabel clearContent() {
        return this;
    }

    @Override
    public void clear() {
    }

    @Override
    public ELabel update() {
        return this;
    }

    @Override
    public void setText(String htmlText) {
    }

    @Override
    public boolean isEmpty() {
        return false;
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
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public Application getApplication() {
        return Application.get();
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public String getHTML() {
        return null;
    }
}