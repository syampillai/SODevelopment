package com.storedobject.ui;

import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.FreeFormatField;
import com.storedobject.vaadin.TextArea;
import com.storedobject.vaadin.util.HasTextValue;

public class Test extends DataForm {

    public Test() {
        super("Test", false);
        addField(new TF("Test"));
    }

    @Override
    protected boolean process() {
        return false;
    }

    private static class TF extends FreeFormatField<String> {

        protected TF(String label) {
            super(label, "", new TextArea());
        }

        @Override
        protected HasTextValue createField() {
            return new TextArea();
        }

        @Override
        protected String getModelValue(String string) {
            return "{\n" + string + "\n}";
        }
    }
}
