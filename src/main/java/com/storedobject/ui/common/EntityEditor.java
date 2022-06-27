package com.storedobject.ui.common;

import com.storedobject.core.*;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityEditor extends ObjectEditor<Entity> {

    private static final String[] REL = new String[] {
            "Supplies items to us",
            "We sell items/services to them",
            "Does repair/maintenance work for us",
            "Occasionally, they keep stock with with us",
            "We rent items out to them",
            "We lease items from them",
            "Provides services to us"
    };
    private final Map<Id, Integer> relCache;
    private Button editRel;
    private PopupButton supplierMenu, customerMenu, serviceProviderMenu;
    private RForm RFormEditor;
    private ObjectEditor<EntityRole> supplierEditor, customerEditor, serviceProviderEditor;
    private Class<EntityRole> serviceProviderClass;

    public EntityEditor() {
        this(EditorAction.ALL);
    }

    public EntityEditor(int actions) {
        this(actions, null);
    }

    public EntityEditor(int actions, String caption) {
        this(actions, caption, null);
    }

    protected EntityEditor(int actions, String caption, String allowedActions) {
        super(Entity.class, actions, caption, allowedActions);
        if(StoredObject.exists(InventoryStore.class, null, true)) {
            relCache = new HashMap<>();
            addField(new TextArea("Relationships"), this::getRS);
            Class<?> rClass;
            editRel = new Button("Define Relationships", VaadinIcon.CLUSTER, e -> editRel());
            try {
                rClass = JavaClassLoader.createClassFromProperty("SUPPLIER-CLASS");
                if(rClass != null && EntityRole.class.isAssignableFrom(rClass)) {
                    //noinspection unchecked
                    supplierEditor = ObjectEditor.create((Class<EntityRole>)rClass);
                    supplierMenu = new PopupButton("Vendor/Supplier", VaadinIcon.INVOICE);
                    setUpEditor(supplierEditor, supplierMenu);
                }
            } catch(SOException ignored) {
            }
            try {
                rClass = JavaClassLoader.createClassFromProperty("CUSTOMER-CLASS");
                if(rClass != null && EntityRole.class.isAssignableFrom(rClass)) {
                    //noinspection unchecked
                    customerEditor = ObjectEditor.create((Class<EntityRole>)rClass);
                    customerMenu = new PopupButton("Customer", VaadinIcon.USER);
                    setUpEditor(customerEditor, customerMenu);
                }
            } catch(SOException ignored) {
            }
            try {
                rClass = JavaClassLoader.createClassFromProperty("SERVICE-PROVIDER-CLASS");
                if(rClass != null && EntityRole.class.isAssignableFrom(rClass)) {
                    //noinspection unchecked
                    serviceProviderClass = (Class<EntityRole>) rClass;
                    if(!(supplierEditor != null && supplierEditor.getObjectClass() == rClass)) {
                        //noinspection unchecked
                        serviceProviderEditor = ObjectEditor.create((Class<EntityRole>) rClass);
                        serviceProviderMenu = new PopupButton("Service Provider", VaadinIcon.USER);
                        setUpEditor(serviceProviderEditor, serviceProviderMenu);
                    }
                }
            } catch(SOException ignored) {
            }
        } else {
            relCache = null;
        }
    }

    private void setUpEditor(ObjectEditor<EntityRole> ed, PopupButton button) {
        ed.setFieldReadOnly("SystemEntity", "Organization");
        ed.setNewObjectGenerator(() -> createEntityRole(ed));
        button.add(new Button("View Details", e -> viewEntityRole(ed)));
        button.add(new Button("Edit Details", e -> editEntityRole(ed)));
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        if(editRel != null) {
            Entity e = getObject();
            if(e == null || us(e)) {
                return;
            }
            buttonPanel.add(editRel);
            if(supplierEditor != null && (getR(e) & 0b101) > 0) {
                buttonPanel.add(supplierMenu);
            }
            if(customerEditor != null && (getR(e) & 2) == 2) {
                buttonPanel.add(customerMenu);
            }
            if(serviceProviderEditor != null && (getR(e) & 0b1000000) == 0b1000000) {
                buttonPanel.add(serviceProviderMenu);
            }
        }
    }

    private boolean us(Entity entity) {
        SystemEntity se = getTransactionManager().getEntity();
        return se != null && se.getEntityId().equals(entity.getId());
    }

    private String getRS(Entity entity) {
        return us(entity) ? "" : getRS(getR(entity));
    }

    private String getRS(int r) {
        StringBuilder s = new StringBuilder();
        int p = 0, no = 0;
        while(r > 0) {
            if((r & 1) == 1) {
                if(s.length() > 0) {
                    s.append('\n');
                }
                ++no;
                s.append('(').append(no).append(") ").append(REL[p]);
            }
            ++p;
            r >>= 1;
        }
        if(s.length() == 0) {
            s.append("None");
        }
        return s.toString();
    }

    private int getR(Entity entity) {
        if(us(entity)) {
            return 0;
        }
        Integer rel = relCache.get(entity.getId());
        if(rel != null) {
            return rel;
        }
        int r = 0;
        List<InventoryVirtualLocation> locs =
                StoredObject.list(InventoryVirtualLocation.class, "Entity=" + entity.getId()).
                        toList();
        for(InventoryVirtualLocation loc: locs) {
            switch(loc.getType()) {
                case 1 -> // Supplier
                        r |= 1;
                case 2 -> // Customer
                        r |= 2;
                case 3 -> // Repair Org.
                        r |= 4;
                case 17 -> // External owner
                        r |= 8;
                case 8 -> // Rent out
                        r |= 16;
                case 9 -> // Lease in
                        r |= 32;
            }
        }
        if(serviceProviderClass != null && entityRole(serviceProviderClass) != null) {
            r |= 64;
        }
        relCache.put(entity.getId(), r);
        return r;
    }

    private void editRel() {
        if(RFormEditor == null) {
            RFormEditor = new RForm();
        }
        RFormEditor.entity();
    }

    private EntityRole entityRole(ObjectEditor<EntityRole> re) {
        return entityRole(re.getObjectClass());
    }

    private EntityRole entityRole(Class<EntityRole> roleClass) {
        return EntityRole.get(getTransactionManager().getEntity(), roleClass, getObject());
    }

    private void editEntityRole(ObjectEditor<EntityRole> re) {
        EntityRole er = entityRole(re);
        if(er == null) {
            message("Creating details.");
            if(re instanceof EntityRoleEditor<?> ere) {
                er = ere.createObj();
                er.setSystemEntity(getTransactionManager().getEntity());
                er.setOrganization(getObject());
            }
        }
        re.editObject(er, this);
    }

    private void viewEntityRole(ObjectEditor<EntityRole> re) {
        EntityRole er = entityRole(re);
        if(er == null) {
            message("Details not created! Please create it now.");
            return;
        }
        re.viewObject(er, this);
    }

    private void setRel(int r) {
        TransactionManager tm = getTransactionManager();
        Entity e = getObject();
        int p = 0;
        while(r > 0) {
            if((r & 1) != 1) {
                ++p;
                r >>= 1;
                continue;
            }
            switch(p) {
                case 0 -> // Supplier
                        InventoryTransaction.createSupplierLocation(tm, e);
                case 1 -> // Customer
                        InventoryTransaction.createConsumerLocation(tm, e);
                case 2 -> // Repair Org.
                        InventoryTransaction.createRepairLocation(tm, e);
                case 3 -> // External owner
                        InventoryTransaction.createExternalOwnerLocation(tm, e);
                case 4 -> // Rent out
                        InventoryTransaction.createLoanToLocation(tm, e);
                case 5 -> // Rent out
                        InventoryTransaction.createLoanFromLocation(tm, e);
            }
            ++p;
            r >>= 1;
        }
        relCache.remove(e.getId());
        reload();
    }

    private class RForm extends DataForm {

        private final ELabelField entityName = new ELabelField("Entity");
        private final ChoicesField relField = new ChoicesField("Relationships", REL) {
            @Override
            protected HasComponents createContainer() {
                return new GridLayout(1);
            }
        };
        private int relationship;

        public RForm() {
            super("Define Relationships with the Entity");
            ELabelField w = new ELabelField("Warning");
            w.append("Changes once saved can not be reverted!", Application.COLOR_ERROR).update();
            addField(entityName, relField, w);
        }

        void entity() {
            Entity entity = getObject();
            entityName.clearContent().append(entity, Application.COLOR_SUCCESS).update();
            relationship = getR(entity);
            relField.setValueMask(relationship);
            relField.setValueMask(relationship);
            execute(EntityEditor.this);
        }

        @Override
        protected boolean process() {
            int r = relField.getValue();
            if(r == relationship) {
                message("No changes were made.");
                return true;
            }
            close();
            new CRForm(relationship, r & (~relationship)).execute(EntityEditor.this);
            return true;
        }
    }

    private class CRForm extends DataForm {

        private final int next;

        public CRForm(int previous, int next) {
            super("Confirm Changes");
            this.next = next;
            Entity entity = getObject();
            ELabelField en = new ELabelField("Entity");
            en.clearContent().append(entity, Application.COLOR_SUCCESS).update();
            ELabelField p = new ELabelField("Current Relationships");
            p.append(getRS(previous), Application.COLOR_SUCCESS).update();
            ELabelField c = new ELabelField("About to Add the Following Relationships");
            c.append(getRS(next), Application.COLOR_ERROR).update();
            ELabelField w = new ELabelField("Warning");
            w.append("Changes once saved can not be reverted!", Application.COLOR_ERROR).update();
            addField(en, p, c, w);
        }

        @Override
        protected boolean process() {
            close();
            setRel(next);
            return true;
        }
    }

    private EntityRole createEntityRole(ObjectEditor<EntityRole> erEditor) {
        try {
            EntityRole er = erEditor.getObjectClass().getDeclaredConstructor().newInstance();
            er.setSystemEntity(getTransactionManager().getEntity());
            er.setOrganization(getObject());
            return er;
        } catch(Throwable e) {
            error(e);
        }
        return null;
    }
}
