package com.storedobject.ui;

import com.storedobject.common.HTMLText;
import com.storedobject.vaadin.StyledText;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.icon.Icon;

public class ELabel extends StyledText implements StyledBuilder, HasText {

    protected final HTMLText label;

    public ELabel() {
        this((String)null);
    }

    public ELabel(String text) {
        this(text, new String[0]);
    }

    public ELabel(Object object, String... style) {
        this(new HTMLText(convert(object), style));
    }

    public ELabel(HTMLText text) {
        this(text, true);
    }

    protected ELabel(HTMLText text, boolean update) {
        super(null);
        label = text;
        if(update) {
            update();
        }
    }

    private static Object convert(Object object) {
        if(object == null) {
            return "";
        }
        if(object instanceof Icon) {
            HTMLText h = new HTMLText();
            h.appendHTML("<iron-icon icon=\"" + ((Icon)object).getElement().getAttribute("icon") + "\"></iron-icon>");
            return h;
        }
        if(object instanceof ELabel) {
            return ((ELabel)object).label;
        }
        Application a = Application.get();
        if(a != null) {
            return a.getEnvironment().toDisplay(object);
        }
        return object;
    }

    @Override
    public StyledBuilder newLine(boolean force) {
        label.newLine(force);
        return this;
    }

    @Override
    public boolean isNewLine() {
        return label.isNewLine();
    }

    @Override
    public StyledBuilder drawLine() {
        label.drawLine();
        return this;
    }

    @Override
    public ELabel append(Object object, String... style) {
        label.append(convert(object), style);
        return this;
    }

    @Override
    public ELabel append(Object object, String color) {
        label.append(convert(object), color);
        return this;
    }

    @Override
    public ELabel append(Object object) {
        label.append(convert(object));
        return this;
    }

    @Override
    public ELabel appendHTML(String html) {
        label.appendHTML(html);
        return this;
    }

    @Override
    public ELabel space(int count) {
        label.space(count);
        return this;
    }

    @Override
    public ELabel clearContent() {
        label.clear();
        return this;
    }

    @Override
    public void clear() {
        label.clear();
    }

    @Override
    public ELabel update() {
        super.setText(label.toString());
        return this;
    }

    @Override
    public void setText(String htmlText) {
        if(label != null) {
            label.clear().appendHTML(htmlText);
        }
        super.setText(htmlText);
    }

    @Override
    public boolean isEmpty() {
        return label.isEmpty();
    }

    @Override
    public StyledBuilder getInternalStyledBuilder() {
        return null;
    }

    @Override
    public void setValue(Object object, String... style) {
        label.setValue(object, style);
        update();
    }

    @Override
    public void setValue(String value) {
        if(value != null) {
            value = value.replace(" ", "&nbsp;");
        }
        setText(value);
    }

    @Override
    public String getValue() {
        update();
        return getText();
    }

    @Override
    public Application getApplication() {
        return Application.get();
    }

    @Override
    public String toString() {
        return getText();
    }

    @Override
    public String getHTML() {
        update();
        return super.getHTML();
    }
}