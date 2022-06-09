package com.storedobject.ui;

import com.storedobject.core.DateUtility;
import com.storedobject.vaadin.DateField;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.template.Id;

public class Test extends TemplateDataForm {

    @Id
    private TextField textField = new TextField();
    @Id
    private final DateField dateField = new DateField();

    public Test() {
        super();
        setRequired(textField);
        center();
    }

    @Override
    protected boolean process() {
        new VerifyOTP("+971506590362", "syam@habibbank.com", () -> {}, () -> {}, () -> {}).execute();
        return false;
    }

    @Override
    protected Component createComponentForId(String id) {
        return switch(id) {
            case "textField" -> textField;
            case "dateField" -> dateField;
            default -> null;
        };
    }

    @Override
    protected String getFieldNameForId(String id) {
        if("textField".equals(id)) {
            return "Your Name";
        }
        return super.getFieldNameForId(id);
    }
}
