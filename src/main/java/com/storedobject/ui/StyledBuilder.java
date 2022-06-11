package com.storedobject.ui;

import com.storedobject.vaadin.HTMLGenerator;

public interface StyledBuilder extends com.storedobject.common.StyledBuilder, HTMLGenerator {

    StyledBuilder getInternalStyledBuilder();
    Application getApplication();

    default void setValue(Object object, String... style) {
        getInternalStyledBuilder().setValue(object, style);
    }

    default void setValue(String value) {
        getInternalStyledBuilder().setValue(value);
    }

    default String getValue() {
        return getInternalStyledBuilder().getValue();
    }

    @Override
    default StyledBuilder update() {
        Application a = getApplication();
        if(a != null) {
            a.access(getInternalStyledBuilder()::update);
        } else {
            getInternalStyledBuilder().update();
        }
        return this;
    }

    @Override
    default boolean isEmpty() {
        String v = getValue();
        return v == null || v.isEmpty();
    }

    @Override
    default StyledBuilder newLine(boolean force) {
        getInternalStyledBuilder().newLine(force);
        return this;
    }

    @Override
    default StyledBuilder append(Object anything) {
        getInternalStyledBuilder().append(anything);
        return this;
    }

    @Override
    default StyledBuilder append(Object anything, String color) {
        getInternalStyledBuilder().append(anything, color);
        return this;
    }

    @Override
    default StyledBuilder append(Object anything, String... style) {
        getInternalStyledBuilder().append(anything, style);
        return this;
    }

    default StyledBuilder appendHTML(String html) {
        getInternalStyledBuilder().appendHTML(html);
        return this;
    }

    default StyledBuilder appendIcon(String icon) {
        return appendWithTag("","iron-icon", "icon='" + icon + "'");
    }

    default StyledBuilder appendWithTag(String text, String tag) {
        return appendWithTag(text, tag, null);
    }

    default StyledBuilder appendWithTag(String text, String tag, String attributes) {
        return appendHTML("<" + tag + (attributes != null && !attributes.isEmpty() ? (" " + attributes) : "") + ">" + text + "</" + tag + ">");
    }

    default void clear() {
        clearContent();
    }

    @Override
    default StyledBuilder clearContent() {
        getInternalStyledBuilder().clearContent();
        return update();
    }

    default StyledBuilder space(int count) {
        getInternalStyledBuilder().space(count);
        return this;
    }

    default StyledBuilder drawLine() {
        getInternalStyledBuilder().drawLine();
        return this;
    }

    default StyledBuilder blackMessage(Object any) {
        append(any).newLine();
        update();
        return this;
    }

    default StyledBuilder blueMessage(Object any) {
        append(any, "blue").newLine();
        update();
        return this;
    }

    default StyledBuilder redMessage(Object any) {
        append(any, "red").newLine().newLine();
        update();
        return this;
    }

    @Override
    default String getHTML() {
        return getInternalStyledBuilder().getHTML();
    }
}