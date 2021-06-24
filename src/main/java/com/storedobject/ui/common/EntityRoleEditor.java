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
 * An editor for {@link EntityRole} in which the fields of the {@link Entity} get merged seamlessly with the fields
 * of the {@link EntityRole}.
 *
 * @param <T> Type of Entity Role.
 * @author Syam
 */
public class EntityRoleEditor<T extends EntityRole> extends ObjectEditor<T> {

    private IdInput<SystemEntity> seField;
    private T entityRole;
    private DataForm adder;
    private boolean alwaysNewEntity;

    /**
     * Constructor.
     *
     * @param objectClass Entity role class.
     */
    public EntityRoleEditor(Class<T> objectClass) {
        this(objectClass, 0, null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Entity role class.
     * @param actions Actions allowed (ORed values of {@link com.storedobject.core.EditorAction}).
     */
    public EntityRoleEditor(Class<T> objectClass, int actions) {
        this(objectClass, actions, null);
    }

    /**
     * Constructor.
     *
     * @param objectClass Entity role class.
     * @param actions Actions allowed (ORed values of {@link com.storedobject.core.EditorAction}).
     * @param caption Caption.
     */
    public EntityRoleEditor(Class<T> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
        if(createObj().getContactGroupingCode() == 0) {
            addIncludeFieldChecker(name -> !name.endsWith(".c"));
        }
        addConstructedListener(f -> con());
    }

    /**
     * Constructor.
     *
     * @param className Name of the Entity role class.
     */
    public EntityRoleEditor(String className) throws Exception {
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
        if(OfEntitySelf.class.isAssignableFrom(getObjectClass())) {
            setFieldHidden((HasValue<?, ?>) seField);
        } else {
            setFieldReadOnly((HasValue<?, ?>) seField);
        }
    }

    @Override
    protected T createObjectInstance() {
        if(alwaysNewEntity) {
            return super.createObjectInstance();
        }
        return entityRole;
    }

    T createObj() {
        return super.createObjectInstance();
    }

    @Override
    public void doAdd() {
        if(alwaysNewEntity) {
            super.doAdd();
            return;
        }
        entityRole = null;
        if(adder == null) {
            adder = new Adder();
        }
        adder.execute();
    }

    void doAdd2() {
        super.doAdd();
    }

    /**
     * If this method is invoked, a new entity will be created for every role added (means, the feature that asks
     * to choose the entity when adding a role will be switched off).
     */
    public void createNewEntityOnAdd() {
        alwaysNewEntity = true;
    }

    private class Adder extends DataForm {

        private final ObjectField<SystemEntity> systemEntityField = new ObjectField<>("Of", SystemEntity.class);
        private final RadioChoiceField newOrExisting = new RadioChoiceField("For",
                new String[] { "New Entity", "Existing Entity"});
        private final ObjectField<Entity> mField = new ObjectField<>("Entity", Entity.class);
        private final ELabelField warning = new ELabelField();
        private T localRole;

        public Adder() {
            super("Create a New " + StringUtility.makeLabel(EntityRoleEditor.this.getObjectClass()));
            if(OfEntitySelf.class.isAssignableFrom(getObjectClass())) {
                setFieldReadOnly(mField);
                newOrExisting.setValue(1);
                setFieldHidden(newOrExisting);
            } else {
                newOrExisting.addValueChangeListener(e -> setFieldVisible(e.getValue() == 1, mField));
                mField.addValueChangeListener(e -> entitySet());
            }
            addField(systemEntityField, newOrExisting, mField, warning);
            if(!TransactionManager.isMultiTenant()) {
                systemEntityField.setValue(getTransactionManager().getEntity());
                setFieldHidden(systemEntityField);
                if(OfEntitySelf.class.isAssignableFrom(getObjectClass())) {
                    SystemEntity se = getTransactionManager().getEntity();
                    mField.setValue(se == null ? null : se.getEntityId());
                }
            } else {
                if(OfEntitySelf.class.isAssignableFrom(getObjectClass())) {
                    systemEntityField.addValueChangeListener(e -> {
                        entitySet();
                        if(e.getValue() != null) {
                            mField.setValue(systemEntityField.getObject().getEntityId());
                        }
                    });
                } else {
                    systemEntityField.addValueChangeListener(e -> entitySet());
                }
            }
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            if(!OfEntitySelf.class.isAssignableFrom(getObjectClass())) {
                mField.setValue((Entity) null);
                newOrExisting.setValue(0);
                setFieldHidden(mField);
            }
            warning.clearContent().update();
            super.execute(parent, doNotLock);
        }

        private void entitySet() {
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
            Entity e = mField.getObject();
            if(e != null) {
                localRole = EntityRole.getByEntityId(se, EntityRoleEditor.this.getObjectClass(), e.getId());
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
                entityRole = null;
                if(localRole == null) {
                    Entity e = mField.getObject();
                    if(e == null) {
                        message("Please select the entity");
                        mField.focus();
                        return false;
                    }
                    localRole = createObj();
                    localRole.setSystemEntity(se);
                    localRole.setOrganization(e);
                }
                close();
                EntityRoleEditor.this.setObject(localRole);
                doEdit();
                return true;
            } else {
                entityRole = createObj();
                entityRole.setSystemEntity(se);
            }
            close();
            doAdd2();
            return true;
        }
    }
}
