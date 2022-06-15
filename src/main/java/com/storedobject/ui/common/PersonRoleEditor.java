package com.storedobject.ui.common;

import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.RadioChoiceField;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasValue;

/**
 * An editor for {@link PersonRole} in which the fields of the {@link Person} get merged seamlessly with the fields
 * of the {@link PersonRole}.
 *
 * @param <T> Type of Person Role.
 * @author Syam
 */
public class PersonRoleEditor<T extends PersonRole> extends ObjectEditor<T> {

    private IdInput<SystemEntity> seField;
    private T personRole;
    private Adder adder;
    private boolean alwaysNewPerson;

    public PersonRoleEditor(Class<T> objectClass) {
        this(objectClass, 0, null);
    }

    public PersonRoleEditor(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    public PersonRoleEditor(Class<T> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
        if(getContactGroupingCode() == 0) {
            addIncludeFieldChecker(name -> !name.endsWith(".c"));
        }
        addConstructedListener(f -> con());
    }

    public PersonRoleEditor(String className) throws Exception {
        super(className);
        if(getContactGroupingCode() == 0) {
            addIncludeFieldChecker(name -> !name.endsWith(".c"));
        }
        addConstructedListener(f -> con());
    }

    private int getContactGroupingCode() {
        try {
            return getObjectClass().getDeclaredConstructor().newInstance().getContactGroupingCode();
        } catch(Throwable e) {
            return 0;
        }
    }

    /**
     * Can new persons can be created or existing persons can be edited?
     *
     * @return True/false.
     */
    public boolean canEditPerson() {
        return true;
    }

    @Override
    public ObjectField.Type getObjectFieldType(String fieldName) {
        if("Person".equals(fieldName)) {
            return ObjectField.Type.FORM;
        }
        return super.getObjectFieldType(fieldName);
    }

    private void con() {
        //noinspection unchecked
        seField = (IdInput<SystemEntity>) getField("SystemEntity");
        setFieldVisible(TransactionManager.isMultiTenant(), (HasValue<?, ?>) seField);
        if(!canEditPerson()) {
            setFieldReadOnly("Person");
        }
    }

    @Override
    protected T createObjectInstance() {
        if(alwaysNewPerson) {
            return super.createObjectInstance();
        }
        return personRole;
    }

    T createObj() {
        return super.createObjectInstance();
    }

    @Override
    public void doAdd() {
        if(alwaysNewPerson) {
            super.doAdd();
            return;
        }
        personRole = null;
        if(adder == null) {
            adder = new Adder();
        }
        adder.execute();
    }

    void doAdd2() {
        super.doAdd();
    }

    /**
     * If this method is invoked, a new person will be created for every role added (means, the feature that asks
     * to choose the person when adding a role will be switched off).
     */
    public void createNewPersonOnAdd() {
        alwaysNewPerson = true;
    }

    private class Adder extends DataForm {

        private final ObjectField<SystemEntity> systemEntityField = new ObjectField<>("Of", SystemEntity.class);
        private final RadioChoiceField newOrExisting = new RadioChoiceField("For",
                new String[] { "New Person", "Existing Person"});
        private final ObjectField<Person> mField = new ObjectField<>("Person", Person.class);
        private final ELabelField warning = new ELabelField();
        private T localRole;

        public Adder() {
            super("Create a New " + StringUtility.makeLabel(PersonRoleEditor.this.getObjectClass()));
            addConstructedListener(f -> {
                if(canEditPerson()) {
                    newOrExisting.addValueChangeListener(e -> setFieldVisible(e.getValue() == 1, mField));
                } else {
                    newOrExisting.setValue(1);
                    setFieldHidden(newOrExisting);
                }
            });
            mField.addValueChangeListener(e -> personSet());
            addField(systemEntityField, newOrExisting, mField, warning);
            if(!TransactionManager.isMultiTenant()) {
                systemEntityField.setValue(getTransactionManager().getEntity());
                setFieldHidden(systemEntityField);
            } else {
                systemEntityField.addValueChangeListener(e -> personSet());
            }
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            mField.setValue((Person) null);
            warning.clearContent().update();
            if(canEditPerson()) {
                newOrExisting.setValue(0);
                setFieldHidden(mField);
            } else {
                newOrExisting.setValue(1);
                setFieldVisible(mField);
            }
            super.execute(parent, doNotLock);
        }

        private void personSet() {
            localRole = null;
            SystemEntity se = systemEntityField.getObject();
            if(se == null) {
                systemEntityField.focus();
                return;
            }
            if(mField.isEmpty()) {
                mField.focus();
                return;
            }
            warning.clearContent();
            Person p = mField.getObject();
            if(p != null) {
                localRole = PersonRole.getByPersonId(se, PersonRoleEditor.this.getObjectClass(), p.getId());
                if(localRole == null) {
                    warning.append("An entry will be created for the selected person.", Application.COLOR_SUCCESS);
                } else {
                    warning.append("An entry already exists! It will be edited.", Application.COLOR_ERROR);
                }
            }
            warning.update();
        }

        @Override
        protected void cancel() {
            super.cancel();
            clearAlerts();
            doCancel();
        }

        @Override
        protected boolean process() {
            SystemEntity se = systemEntityField.getObject();
            if(se == null) {
                message("Please select the organization");
                systemEntityField.focus();
                return false;
            }
            seField.setValue(se);
            if(newOrExisting.getValue() == 1) {
                personRole = null;
                if(localRole == null) {
                    Person p = mField.getObject();
                    if(p == null) {
                        message("Please select the person");
                        mField.focus();
                        return false;
                    }
                    localRole = createObj();
                    localRole.setSystemEntity(se);
                    localRole.setPerson(p);
                }
                close();
                PersonRoleEditor.this.setObject(localRole);
                doEdit();
                return true;
            } else {
                personRole = createObj();
                personRole.setSystemEntity(se);
            }
            close();
            doAdd2();
            return true;
        }
    }
}
