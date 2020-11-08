package com.storedobject.ui.common;

import com.storedobject.core.SystemEntity;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.TimeZoneField;
import com.vaadin.flow.component.HasValue;

public class SystemEntityEditor extends ObjectEditor<SystemEntity> {

    public SystemEntityEditor() {
        super(SystemEntity.class);
    }

    public SystemEntityEditor(int actions) {
        super(SystemEntity.class, actions);
    }

    public SystemEntityEditor(int actions, String caption) {
        super(SystemEntity.class, actions, caption);
    }

    public SystemEntityEditor(String className) throws Exception {
        super(className);
    }

    @Override
    protected HasValue<?, ?> createField(String fieldName, String label) {
        if("TimeZone".equals(fieldName)) {
            return new TimeZoneField(label);
        }
        return super.createField(fieldName, label);
    }
}