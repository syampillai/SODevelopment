package com.storedobject.core;

import com.storedobject.common.StyledBuilder;

public class HTMLText implements StyledBuilder {

    public HTMLText() {
    }

    public HTMLText(Object object, String... style) {
    }

    public StringBuilder getText() {
        return null;
    }

    public void setText(String text, String... style) {
    }

    public HTMLText newLine(boolean force) {
        return this;
    }

    public HTMLText newLine() {
        return this;
    }

    public HTMLText drawLine() {
        return this;
    }

    @Override
    public StyledBuilder append(Object object) {
        return null;
    }

    @Override
    public HTMLText append(Object object, String color) {
        return this;
    }

    @Override
    public HTMLText append(Object object, String... style) {
        return this;
    }

    public HTMLText appendHTML(String html) {
        return this;
    }

    public HTMLText clear() {
        return this;
    }

    public HTMLText clearContent() {
        return this;
    }

    public boolean isEmpty() {
        return true;
    }

    public boolean isNewLine() {
        return true;
    }

    public HTMLText space(int count) {
        return this;
    }

    public void setValue(Object object, String... style) {
    }

    public static String encode(Object object) {
        return null;
    }
}
