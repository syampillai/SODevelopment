package com.storedobject.ui.util;

import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.shared.Registration;

public class AcceptAbandonButtons extends ButtonLayout implements HasValue<HasValue.ValueChangeEvent<String>, String> {

    public AcceptAbandonButtons(Runnable acceptChanges, Runnable abandonChanges) {
        Button b1 = new Button("Save", e-> acceptChanges.run()).asSmall();
        b1.setText("");
        b1.getElement().setAttribute("title", "Accept changes");
        Button b2 = new Button("Cancel", e -> abandonChanges.run()).asSmall();
        b2.setText("");
        b2.getElement().setAttribute("title", "Abandon changes");
        add(b1, b2);
    }

    public void hideButtons() {
        getChildren().filter(b -> b instanceof Button).forEach(b -> b.setVisible(false));
    }

    @Override
    public void setValue(String s) {
    }

    @Override
    public String getValue() {
        return "";
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<String>> valueChangeListener) {
        return null;
    }

    @Override
    public void setReadOnly(boolean b) {
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean b) {
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }
}
