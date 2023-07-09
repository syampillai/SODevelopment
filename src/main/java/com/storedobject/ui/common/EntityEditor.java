package com.storedobject.ui.common;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class EntityEditor extends ObjectEditor<Entity> {

    private static final String[] REL = new String[] {
            "Supplies items to us",
            "We sell items/services to them",
            "Does repair/maintenance work for us",
            "Occasionally, they keep stock with us",
            "We rent items out to them",
            "We lease items from them",
            "Provides services to us"
    };
    private final Map<Id, Integer> relCache, activeCache;
    private PopupButton relMenu, supplierMenu, customerMenu, serviceProviderMenu;
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
            activeCache = new HashMap<>();
            addField(new TextArea("Relationships"), this::getRS);
            Class<?> rClass;
            relMenu = new PopupButton("Relationships", VaadinIcon.CLUSTER);
            relMenu.add(new Button("Define New", VaadinIcon.PLUS, e -> editRel()));
            relMenu.add(new Button("Deactivate", VaadinIcon.UNLINK, e -> switchRel(false)));
            relMenu.add(new Button("Activate", VaadinIcon.LINK, e -> switchRel(true)));
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
                    } else {
                        serviceProviderEditor = supplierEditor;
                    }
                }
            } catch(SOException ignored) {
            }
        } else {
            relCache = null;
            activeCache = null;
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
        if(relMenu != null) {
            Entity e = getObject();
            if(e == null || us(e)) {
                return;
            }
            buttonPanel.add(relMenu);
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
        Integer active = activeCache.get(getObjectId());
        int a = active == null ? 0 : active;
        StringBuilder s = new StringBuilder();
        int p = 0, no = 0;
        while(r > 0) {
            if((r & 1) == 1) {
                if(s.length() > 0) {
                    s.append('\n');
                }
                ++no;
                s.append('(').append(no).append(") ").append(REL[p]).append(' ');
                if(p < 6) {
                    s.append((a & 1) == 1 ? '✓' : '×');
                }
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
        int r = 0, a = 0;
        boolean active;
        List<InventoryVirtualLocation> locs =
                StoredObject.list(InventoryVirtualLocation.class, "Entity=" + entity.getId()).
                        toList();
        for(InventoryVirtualLocation loc: locs) {
            active = loc.isActive();
            switch(loc.getType()) {
                case 1 -> { // Supplier
                    r |= 1;
                    if(active) {
                        a |= 1;
                    }
                }
                case 2 -> { // Customer
                    r |= 2;
                    if(active) {
                        a |= 2;
                    }
                }
                case 3 -> { // Repair Org.
                    r |= 4;
                    if(active) {
                        a |= 4;
                    }
                }
                case 17 -> { // External owner
                    r |= 8;
                    if(active) {
                        a |= 8;
                    }
                }
                case 8 -> { // Rent out
                    r |= 16;
                    if(active) {
                        a |= 16;
                    }
                }
                case 9 -> { // Lease in
                    r |= 32;
                    if(active) {
                        a |= 32;
                    }
                }
            }
        }
        if(serviceProviderClass != null && entityRole(serviceProviderClass) != null) {
            r |= 64;
        }
        relCache.put(entity.getId(), r);
        activeCache.put(entity.getId(), a);
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
        boolean addSP = false;
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
                case 6 -> // Add Service Provider
                        addSP = serviceProviderEditor != null;
            }
            ++p;
            r >>= 1;
        }
        reload();
        if(addSP) {
            EntityRole sp = entityRole(serviceProviderClass);
            if(sp == null) {
                editEntityRole(serviceProviderEditor);
            }
        }
    }

    @Override
    public void reload() {
        Entity e = getObject();
        relCache.remove(e.getId());
        activeCache.remove(e.getId());
        super.reload();
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

    private void switchRel(boolean activate) {
        Entity entity = getObject();
        if(entity == null) {
            return;
        }
        List<InventoryVirtualLocation> locs = StoredObject.list(InventoryVirtualLocation.class,
                "Entity=" + entity.getId() + " AND Status=" + (activate ? 1 : 0)).toList();
        if(locs.isEmpty()) {
            clearAlerts();
            message("No relationship found to " + (activate ? "" : "de") + "activate");
            return;
        }
        new RelGrid(entity, locs, activate,
                items -> switchRel(items, activate))
                .execute(EntityEditor.this);
    }

    private void switchRel(Set<InventoryVirtualLocation> locations, boolean activate) {
        if(locations.isEmpty()) {
            return;
        }
        transact(t -> {
            for(InventoryVirtualLocation loc: locations) {
                loc.setStatus(activate ? 0 : 1);
                loc.save(t);
            }
        });
        reload();
    }

    private static class RelGrid extends MultiSelectGrid<InventoryVirtualLocation> {

        private final Entity entity;

        public RelGrid(Entity entity, List<InventoryVirtualLocation> items, boolean activate,
                       Consumer<Set<InventoryVirtualLocation>> rels) {
            super(InventoryVirtualLocation.class, items, StringList.create("Relationship", "Status"), rels);
            setCaption((activate ? "A" : "Dea") + "ctivate Relationships");
            this.entity = entity;
        }

        @SuppressWarnings("unused")
        public String getRelationship(InventoryVirtualLocation location) {
            return switch(location.getType()) {
                case 1 -> REL[0];
                case 2 -> REL[1];
                case 3 -> REL[2];
                case 17 -> REL[3];
                case 8 -> REL[4];
                case 9 -> REL[5];
                default -> "Unknown";
            };
        }

        @Override
        public void createHeaders() {
            ELabel e = new ELabel("Entity: ").append(entity.toDisplay(), Application.COLOR_SUCCESS).update();
            prependHeader().join().setComponent(e);
        }
    }
}
