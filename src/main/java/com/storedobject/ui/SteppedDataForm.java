package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.vaadin.IncludeField;
import com.storedobject.vaadin.ObjectForm;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;

import java.util.stream.IntStream;

/**
 * A Data Form for editing one or more objects that has multiple steps of content views.
 * All content views may not have to be using the same object.
 *
 * @author Syam
 */
public abstract class SteppedDataForm extends SteppedView {

    private Object object;

    /**
     * Constructor.
     *
     * @param numberOfSteps Number of steps
     */
    public SteppedDataForm(int numberOfSteps) {
        this(numberOfSteps, null);
    }

    /**
     * Constructor.
     *
     * @param numberOfSteps Number of steps
     * @param caption Caption for the view
     */
    public SteppedDataForm(int numberOfSteps, String caption) {
        super(numberOfSteps, caption);
    }

    @Override
    ObjectForm<?> getForm(int step) {
        return (ObjectForm<?>)super.getForm(step);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        forms.stream().map(f -> (ObjectForm<?>)f).forEach(f -> {
            Object o = f.getObject(false);
            if(o == null) {
                o = f.getObject(true);
                @SuppressWarnings("rawtypes") ObjectForm form = f;
                //noinspection unchecked
                form.setObject(o, true);
            }
        });
        super.execute(parent, doNotLock);
    }

    @Override
    protected final boolean commit(int step) {
        ObjectForm<?> of = getObjectForm(step);
        if(of.commit()) {
            object = of.getObject();
            return true;
        }
        return false;
    }

    @Override
    protected final Component getStepComponent(int step) {
        ObjectForm<?> of;
        of = createObjectForm(step);
        if(of == null) {
            of = new Form(step);
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
    protected final ObjectForm<?> getObjectForm(int step) {
        ObjectForm<?> form = getForm(step);
        if(form != null) {
            return form;
        }
        throw new SORuntimeException();
    }

    /**
     * Create the form for the step.
     *
     * @param step Step
     * @return Form for the step. If returned <code>null</code> a default form will be created for the class returned by
     * {@link #getObjectClass(int)}.
     * Default implementation returns <code>null</code>. So, either this method or {@link #getObjectClass(int)} should
     * return a <code>non-null</code> value.
     */
    protected ObjectForm<?> createObjectForm(int step) {
        return null;
    }

    /**
     * If {@link #createObjectForm(int)} returns <code>null</code>, the class returned by this method is used to create
     * the form for the step.
     *
     * @param step Step
     * @return Default implementation returns <code>null</code>. So, either this method or {@link #createObjectForm(int)} should
     * return a <code>non-null</code> value.
     */
    protected Class<?> getObjectClass(int step) {
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

    /**
     * Set the object to the given steps.
     *
     * @param object Object to set
     * @param steps Steps
     */
    protected void setObject(Object object, int... steps) {
        getComponent();
        IntStream s;
        if(steps != null && steps.length > 0) {
            s = IntStream.of(steps);
        } else {
            s = IntStream.rangeClosed(1, forms.size());
        }
        s.forEach(step -> {
            if(step > 0 && step <= forms.size()) {
                @SuppressWarnings("rawtypes") ObjectForm form = getForm(step);
                //noinspection unchecked
                if(object == null || form.getObjectClass().isAssignableFrom(object.getClass())) {
                    //noinspection unchecked
                    form.setObject(object, true);
                }
            }
        });
    }

    /**
     * Get the object value from a given step.
     *
     * @param step Step
     * @return Object value
     */
    protected Object getObject(int step) {
        getComponent();
        if(step < 1 || step > forms.size()) {
            return null;
        }
        return getForm(step).getObject();
    }

    @Override
    protected void enter(int step) {
        if(object != null) {
            @SuppressWarnings("rawtypes") ObjectForm of;
            for(int s = step; s <= forms.size(); s++) {
                of = getObjectForm(s);
                if(of.getObject() == object) {
                    //noinspection unchecked
                    of.setObject(object, true);
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private class Form extends ObjectForm {

        private final StringList fieldList;

        public Form(int step) {
            //noinspection unchecked
            super(SteppedDataForm.this.getObjectClass(step));
            IncludeField includeField = getIncludeFieldChecker(step);
            if (includeField == null) {
                fieldList = getFields(step);
                if (fieldList != null) {
                    setIncludeFieldChecker(fieldList::contains);
                }
            } else {
                setIncludeFieldChecker(includeField);
                fieldList = null;
            }
        }

        @Override
        protected int getFieldOrder(String fieldName) {
            if(fieldList != null) {
                return fieldList.indexOf(fieldName);
            }
            return super.getFieldOrder(fieldName);
        }
    }
}
