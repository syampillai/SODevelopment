package com.storedobject.ui;

import com.storedobject.core.StringUtility;
import com.storedobject.vaadin.Box;
import com.storedobject.vaadin.CustomField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.html.Div;

public class ELabelField extends CustomField<Object> implements StyledBuilder, HasText {

    private final ELabel label = new ELabel();
    private Application application;

    public ELabelField() {
        this(null);
    }

    public ELabelField(String label) {
        super("");
        Div div = new Div(this.label);
        Box box = new Box(div);
        box.setStyle("display", "block");
        box.setPadding("4px");
        box.setBorderWidth(1);
        box.setBorderStyle("dashed");
        box.alignSizing();
        box.setStyle("min-height", "32px");
        box.setStyle("line-height", "1.5em");
        add(div);
        setLabel(label);
    }

    public ELabelField(String label, Object text, String... style) {
        this(label);
        this.label.append(text, style).update();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getApplication();
    }

    @Override
    protected String generateModelValue() {
        return label.getValue();
    }

    @Override
    protected void setPresentationValue(Object value) {
    }

    public void setValue(String value) {
        super.setValue(value);
        label.setValue(value);
    }

    @Override
    public void clear() {
        setValue("");
        label.clearContent().update();
    }

    @Override
    public void setValue(Object object) {
        setValue(StringUtility.toString(object));
    }

    @Override
    public boolean isNewLine() {
        return label.isNewLine();
    }

    @Override
    public StyledBuilder clearContent() {
        clear();
        return this;
    }

    @Override
    public StyledBuilder getInternalStyledBuilder() {
        return label;
    }

    @Override
    public Application getApplication() {
        if(application != null) {
            return application;
        }
        application = Application.get();
        return application;
    }

    @Override
    public String getText() {
        return label.getValue();
    }

    @Override
    public void setText(String text) {
        setValue(text);
    }
}
