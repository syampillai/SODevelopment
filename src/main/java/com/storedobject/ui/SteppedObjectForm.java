package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.Id;
import com.storedobject.core.ObjectSetter;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;
import com.storedobject.vaadin.IncludeField;
import com.storedobject.vaadin.ObjectForm;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;

import java.util.ArrayList;

/**
 * A Form for editing an object that has multiple steps of content views.
 *
 * @param <T> Type of object.
 * @author Syam
 */
public class SteppedObjectForm<T extends StoredObject> extends SteppedView implements ObjectSetter<T> {

    private final Class<T> objectClass;
    private final ArrayList<ObjectForm<T>> forms = new ArrayList<>();
    private T object;

    /**
     * Constructor.
     *
     * @param objectClass Class of the object
     * @param numberOfSteps Number of steps
     */
    public SteppedObjectForm(Class<T> objectClass, int numberOfSteps) {
        this(objectClass, numberOfSteps, null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Class of the object
     * @param numberOfSteps Number of steps
     * @param caption Caption for the view
     */
    public SteppedObjectForm(Class<T> objectClass, int numberOfSteps, String caption) {
        super(numberOfSteps, caption == null ? StringUtility.makeLabel(objectClass) : caption);
        this.objectClass = objectClass;
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        getComponent();
        if(object == null) {
            setObject(forms.get(0).getObject(true));
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected final boolean commit(int step) throws Exception {
        ObjectForm<T> of = getObjectForm(step);
        if(of == null || !of.commit()) {
            return false;
        }
        return super.commit(step);
    }

    @Override
    protected final Component getStepComponent(int step) {
        ObjectForm<T> of;
        of = createObjectForm(step);
        if(of == null) {
            of = new ObjectForm<>(objectClass);
            IncludeField includeField = getIncludeFieldChecker(step);
            if (includeField == null) {
                StringList list = getFields(step);
                if (list != null) {
                    of.setIncludeFieldChecker(list::contains);
                }
            } else {
                of.setIncludeFieldChecker(includeField);
            }
        }
        of.setView(this);
        forms.add(of);
        return of.getComponent();
    }

    /**
     * Get the form for the step.
     *
     * @param step Step
     * @return Form for the step.
     */
    protected final ObjectForm<T> getObjectForm(int step) {
        return step > 0 && step <= forms.size() ? forms.get(step - 1) : null;
    }

    /**
     * Create the form for the step.
     *
     * @param step Step
     * @return Form for the step. If returned <code>null</code> a default form will be created for the class of the object.
     * Default implementation returns <code>null</code>.
     */
    protected ObjectForm<T> createObjectForm(int step) {
        return null;
    }

    /**
     * Return the "include field checker" for the step. If this method returns a <code>non-null</code> value, that will be
     * used to filter out field names of the object in this step, otherwise, {@link #getFields(int)} will be used to identify the fields.
     *
     * @param step Step
     * @return Default implementation returns <code>null</code>.
     */
    protected IncludeField getIncludeFieldChecker(int step) {
        return null;
    }

    /**
     * Get field names for the step. This will be invoked only if {@link #getIncludeFieldChecker(int)} returns <code>null</code>.
     *
     * @param step Step
     * @return Default implementation returns <code>null</code>.
     */
    protected StringList getFields(int step) {
        return null;
    }

    @Override
    protected void enter(int step) {
        ObjectForm<T> of = getObjectForm(step);
        if(of != null) {
            of.setObject(object, true);
        }
    }

    /**
     * Get the class of the object.
     *
     * @return Class of the object.
     */
    @Override
    public final Class<T> getObjectClass() {
        return objectClass;
    }

    /**
     * Set the object (to all the steps).
     *
     * @param object Object to set
     */
    @Override
    public void setObject(T object) {
        getComponent();
        this.object = object;
        forms.forEach(f -> f.setObject(object, true));
    }

    /**
     * Get the currenct object value.
     *
     * @return Current object value.
     */
    public T getObject() {
        return object;
    }

    /**
     * Get the Id of the current object value.
     *
     * @return Id of the current object or <code>null</code> if the object value is <code>null</code>.
     */
    public Id getObjectId() {
        return object == null ? null : object.getId();
    }
}
