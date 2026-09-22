package com.storedobject.ui.ai;

import com.storedobject.core.StoredObject;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.View;

public class AcceptObject<O extends StoredObject> extends AcceptData {

    private final ObjectField<O> objectField;
    private O object;

    public AcceptObject(Knowledge knowledge, Class<O> objectClass, String purpose) {
        super(knowledge, "Select");
        this.objectField = new ObjectField<>(purpose, objectClass);
        addField(objectField);
        setRequired(objectField);
    }

    public ObjectField<O> getObjectField() {
        return objectField;
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        object = null;
        map.remove("object");
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        clearAlerts();
        object = objectField.getObject();
        map.put("object", object);
        return true;
    }

    public O getObject() {
        getMap();
        return object;
    }
}
