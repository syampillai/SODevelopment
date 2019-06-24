package com.storedobject.ui;

import com.storedobject.vaadin.HTMLGenerator;

@SuppressWarnings("UnusedReturnValue")
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
        return this;
    }

    @Override
    default boolean isEmpty() {
        return false;
    }

    @Override
    default StyledBuilder newLine(boolean force) {
        return this;
    }

    @Override
    default StyledBuilder append(Object anything) {
        return this;
    }

    @Override
    default StyledBuilder append(Object anything, String color) {
        return this;
    }

    @Override
    default StyledBuilder append(Object anything, String... style) {
        return this;
    }

    default StyledBuilder appendHTML(String html) {
        return this;
    }

    default StyledBuilder appendIcon(String icon) {
        return this;
    }

    default StyledBuilder appendWithTag(String text, String tag) {
        return this;
    }

    default StyledBuilder appendWithTag(String text, String tag, String attributes) {
        return this;
    }

    default void clear() {
        clearContent();
    }

    @Override
    default StyledBuilder clearContent() {
        return this;
    }

    default StyledBuilder space(int count) {
        return this;
    }

    default StyledBuilder drawLine() {
        return this;
    }

    default StyledBuilder blackMessage(Object any) {
        return this;
    }

    default StyledBuilder blueMessage(Object any) {
        return this;
    }

    default StyledBuilder redMessage(Object any) {
        return this;
    }

    @Override
    default String getHTML() {
        return null;
    }
}