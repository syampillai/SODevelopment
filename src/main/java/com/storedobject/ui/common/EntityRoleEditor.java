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

import java.util.List;

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
        if(getContactGroupingCode() == 0) {
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

    @Override
    protected HasValue<?, ?> createField(String fieldName, String label) {
        if(self() && "Organization".equals(fieldName)) {
            List<Entity> entityList = StoredObject.list(SystemEntity.class).map(SystemEntity::getEntity).toList();
            return new ObjectField<>(label, entityList);
        }
        return super.createField(fieldName, label);
    }

    @Override
    public ObjectField.Type getObjectFieldType(String fieldName) {
        if("Organization".equals(fieldName)) {
            return ObjectField.Type.FORM;
        }
        return super.getObjectFieldType(fieldName);
    }

    /**
     * Can new entities can be created or existing entities can be edited?
     *
     * @return True/false.
     */
    public boolean canEditEntity() {
        return true;
    }

    private void con() {
        //noinspection unchecked
        seField = (IdInput<SystemEntity>) getField("SystemEntity");
        setFieldVisible(TransactionManager.isMultiTenant(), (HasValue<?, ?>) seField);
        if(self()) {
            setFieldHidden((HasValue<?, ?>) seField);
        } else {
            setFieldReadOnly((HasValue<?, ?>) seField);
        }
        if(!self() && !canEditEntity()) {
            setFieldReadOnly("Organization");
        }
    }

    @Override
    protected T createObjectInstance() {
        if(alwaysNewEntity || self()) {
            T role = super.createObjectInstance();
            SystemEntity se = getTransactionManager().getEntity();
            role.setSystemEntity(se);
            if(se != null) {
                //noinspection unchecked
                ((ObjectField<Entity>) getField("Organization")).setValue(se.getEntityId());
            }
            return role;
        }
        return entityRole;
    }

    T createObj() {
        return super.createObjectInstance();
    }

    @Override
    public void doAdd() {
        if(alwaysNewEntity || self()) {
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

    private boolean self() {
        return OfEntitySelf.class.isAssignableFrom(getObjectClass());
    }

    private class Adder extends DataForm {

        private final ObjectField<SystemEntity> systemEntityField = new ObjectField<>("Of", SystemEntity.class);
        private final RadioChoiceField newOrExisting = new RadioChoiceField("For",
                new String[] { "New Entity", "Existing Entity"});
        private final ObjectField<Entity> entityField= new ObjectField<>("Entity", Entity.class);
        private final ELabelField warning = new ELabelField();
        private T localRole;

        public Adder() {
            super("Create a New " + StringUtility.makeLabel(EntityRoleEditor.this.getObjectClass()));
            addField(systemEntityField, newOrExisting, entityField, warning);
            addConstructedListener(f -> {
                if(canEditEntity()) {
                    newOrExisting.addValueChangeListener(e -> setFieldVisible(e.getValue() == 1, entityField));
                } else {
                    newOrExisting.setValue(1);
                    setFieldHidden(newOrExisting);
                }
            });
            entityField.addValueChangeListener(e -> entitySet());
            addField(systemEntityField, newOrExisting, entityField, warning);
            if(!TransactionManager.isMultiTenant()) {
                systemEntityField.setValue(getTransactionManager().getEntity());
                setFieldHidden(systemEntityField);
            } else {
                systemEntityField.addValueChangeListener(e -> entitySet());
            }
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            entityField.setValue((Entity) null);
            warning.clearContent().update();
            if(canEditEntity()) {
                newOrExisting.setValue(0);
                setFieldHidden(entityField);
            } else {
                newOrExisting.setValue(1);
                setFieldVisible(entityField);
            }
            super.execute(parent, doNotLock);
        }

        private void entitySet() {
            localRole = null;
            SystemEntity se = systemEntityField.getObject();
            if(se == null) {
                systemEntityField.focus();
                return;
            }
            if(entityField.isEmpty()) {
                entityField.focus();
                return;
            }
            warning.clearContent();
            Entity e = entityField.getObject();
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
                entityRole = null;
                if(localRole == null) {
                    Entity e = entityField.getObject();
                    if(e == null) {
                        message("Please select the entity");
                        entityField.focus();
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
