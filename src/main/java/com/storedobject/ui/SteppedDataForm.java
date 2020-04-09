package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.vaadin.IncludeField;
import com.storedobject.vaadin.ObjectForm;
import com.vaadin.flow.component.Component;

/**
 * A Data Form for editing one or more objects that has multiple steps of content views. All content views may not have to be using the same object.
 *
 * @author Syam
 */
public abstract class SteppedDataForm extends SteppedView {

    public SteppedDataForm(int numberOfSteps) {
        this(numberOfSteps, null);
    }

    public SteppedDataForm(int numberOfSteps, String caption) {
        super(numberOfSteps, caption);
    }

    @Override
    protected final boolean commit(int step) throws Exception {
        return false;
    }

    @Override
    protected final Component getStepComponent(int step) {
        return null;
    }

    protected final ObjectForm<?> getObjectForm(int step) {
        return null;
    }

    protected ObjectForm<?> createObjectForm(int step) {
        return null;
    }

    protected Class<?> getObjectClass(int step) {
        return null;
    }

    protected IncludeField getIncludeFieldChecker(int step) {
        return null;
    }

    protected StringList getFields(int step) {
        return null;
    }

    protected void setObject(Object object, int... steps) {
    }

    protected Object getObject(int step) {
        return null;
    }
}