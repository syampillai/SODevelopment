package com.storedobject.ui.common;

import com.storedobject.core.TextContent;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.IntegerField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

public class AbstractTextContentEditor<T extends TextContent> extends ObjectEditor<T> {

    public AbstractTextContentEditor(Class<T> objectClass) {
        super(objectClass);
    }

    public AbstractTextContentEditor(Class<T> objectClass, int actions) {
        super(objectClass, actions);
    }

    public AbstractTextContentEditor(Class<T> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public AbstractTextContentEditor(String className) throws Exception {
        super(className);
    }

    @Override
    protected void formConstructed() {
        super.formConstructed();
        setFieldReadOnly("Version");
    }

    @Override
    protected void customizeField(String fieldName, HasValue<?, ?> field) {
        switch(fieldName) {
            case "Notes", "Content" -> ((Component) field).getElement().setAttribute("colspan", "2");
        }
        super.customizeField(fieldName, field);
    }

    @Override
    protected boolean save() throws Exception {
        if(super.save()) {
            SOServlet.removeCache(getObject());
            return true;
        }
        return false;
    }
}