package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.Id;
import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;
import com.storedobject.vaadin.IncludeField;
import com.storedobject.vaadin.ObjectForm;
import com.vaadin.flow.component.Component;

/**
 * A Data Form for editing one or more objects that has multiple steps of content views. All content views may not have to be using the same object.
 *
 * @author Syam
 */
public class SteppedObjectForm<T extends StoredObject> extends SteppedView implements ObjectSetter<T> {

    public SteppedObjectForm(Class<T> objectClass, int numberOfSteps) {
        this(objectClass, numberOfSteps, null);
    }

    public SteppedObjectForm(Class<T> objectClass, int numberOfSteps, String caption) {
        super(numberOfSteps, caption == null ? StringUtility.makeLabel(objectClass) : caption);
    }

    @Override
    protected final boolean commit(int step) throws Exception {
        return false;
    }

    @Override
    protected final Component getStepComponent(int step) {
        return null;
    }

    protected final ObjectForm<T> getObjectForm(int step) {
        return null;
    }

    protected ObjectForm<T> createObjectForm(int step) {
        return null;
    }

    protected final Class<T> getObjectClass(int step) {
        return null;
    }

    protected IncludeField getIncludeFieldChecker(int step) {
        return null;
    }

    protected StringList getFields(int step) {
        return null;
    }

    @Override
    protected void enter(int step) {
    }

    @Override
    public final Class<T> getObjectClass() {
        return null;
    }

    @Override
    public void setObject(T object) {
    }

    public T getObject() {
        return null;
    }

    public Id getObjectId() {
        return null;
    }
}
