package com.storedobject.ui.common;

import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.core.SystemUser;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.util.LogicParser;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.RadioChoiceField;
import com.storedobject.vaadin.View;

/**
 * Editor for {@link com.storedobject.core.SystemUser}.
 *
 * @author Syam
 */
public class SystemUserEditor extends ObjectEditor<SystemUser> {

    private Person person;
    private Adder adder;

    public SystemUserEditor() {
        this(0, null);
    }

    public SystemUserEditor(int actions) {
        this(actions, null);
    }

    public SystemUserEditor(int actions, String caption) {
        super(SystemUser.class, actions, caption);
        setSearchFilter(Application.getUserVisibility("edit"));
        LogicParser.checkOverride(this);
    }

    @Override
    public ObjectField.Type getObjectFieldType(String fieldName) {
        if("Person".equals(fieldName)) {
            return ObjectField.Type.FORM;
        }
        return super.getObjectFieldType(fieldName);
    }

    @Override
    protected SystemUser createObjectInstance() {
        SystemUser su = new SystemUser();
        if(person != null) {
            su.setPerson(person.getId());
        }
        return su;
    }

    @Override
    public void doAdd() {
        person = null;
        if(adder == null) {
            adder = new Adder();
        }
        adder.execute();
    }

    void doAdd2() {
        super.doAdd();
    }

    private class Adder extends DataForm {

        private final RadioChoiceField newOrExisting = new RadioChoiceField("For",
                new String[] { "New Person", "Existing Person"});
        private final ObjectField<Person> pField = new ObjectField<>("Person", Person.class);
        private final ELabelField warning = new ELabelField();

        public Adder() {
            super("Create a New System User");
            newOrExisting.addValueChangeListener(e -> setFieldVisible(e.getValue() == 1, pField));
            pField.addValueChangeListener(e -> personSet());
            addField(newOrExisting, pField, warning);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            pField.setValue((Person) null);
            warning.clearContent().update();
            newOrExisting.setValue(0);
            setFieldHidden(pField);
            super.execute(parent, doNotLock);
        }

        private void personSet() {
            warning.clearContent();
            Person p = pField.getObject();
            if(p != null) {
                int count = StoredObject.count(SystemUser.class, "Person=" + p.getId());
                if(count > 0) {
                    StringBuilder s = new StringBuilder();
                    s.append(count == 1 ? "One" : count).append(" login").append(count == 1 ? "" : "s").
                            append(" already exist").append(count == 1 ? "s" : "").
                            append("! Another one will be created!!");
                    warning.append(s, Application.COLOR_ERROR);
                } else {
                    warning.append("A login will be created for the selected person.", Application.COLOR_SUCCESS);
                }
            }
            warning.update();
        }

        @Override
        protected void cancel() {
            super.cancel();
            doCancel();
        }

        @Override
        protected boolean process() {
            if(newOrExisting.getValue() == 1) {
                person = pField.getObject();
                if(person == null) {
                    message("Please select the person");
                    pField.focus();
                    return false;
                }
            }
            close();
            doAdd2();
            return true;
        }
    }
}
