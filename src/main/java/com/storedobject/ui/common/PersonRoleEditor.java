package com.storedobject.ui.common;

import com.storedobject.core.*;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.IdInput;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectField;
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
        if(createObj().getContactGroupingCode() == 0) {
            addIncludeFieldChecker(name -> !name.endsWith(".c"));
        }
        addConstructedListener(f -> con());
    }

    public PersonRoleEditor(String className) throws Exception {
        super(className);
        if(createObj().getContactGroupingCode() == 0) {
            addIncludeFieldChecker(name -> !name.endsWith(".c"));
        }
        addConstructedListener(f -> con());
    }

    @Override
    protected HasValue<?, ?> createField(String fieldName, String label) {
        if("Organization".equals(fieldName)) {
            return new ObjectField<>(label, Entity.class, ObjectField.Type.FORM);
        }
        return super.createField(fieldName, label);
    }

    private void con() {
        //noinspection unchecked
        seField = (IdInput<SystemEntity>) getField("SystemEntity");
        setFieldVisible(TransactionManager.isMultiTenant(), (HasValue<?, ?>) seField);
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
    public void createNewEntityOnAdd() {
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
            newOrExisting.addValueChangeListener(e -> setFieldVisible(e.getValue() == 1, mField));
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
            newOrExisting.setValue(0);
            setFieldHidden(mField);
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
                    warning.append("An entry will be created for the selected entity.", "blue");
                } else {
                    warning.append("An entry already exists! It will be edited.", "red");
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
