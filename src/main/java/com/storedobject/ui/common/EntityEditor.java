package com.storedobject.ui.common;

import com.storedobject.core.*;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityEditor extends ObjectEditor<Entity> {

    private static final String[] REL = new String[] {
            "Supplies items to us",
            "We sell items to them",
            "Does repair/maintenance work for us",
            "Occasionally keeps their stock with us",
            "We rent items out to them",
            "We lease items from them",
    };
    private final Map<Id, Integer> relCache;
    private Button editRel;
    private PopupButton supplierMenu;
    private RForm RFormEditor;
    private ObjectEditor<EntityRole> supplierEditor;

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
            editRel = new Button("Define Relationships", VaadinIcon.CLUSTER, e -> editRel());
            String scName = GlobalProperty.get("SUPPLIER-CLASS");
            if(scName != null) {
                try {
                    Class<?> scc = JavaClassLoader.getLogic(scName);
                    if(EntityRole.class.isAssignableFrom(scc)) {
                        //noinspection unchecked
                        supplierEditor = ObjectEditor.create((Class<EntityRole>)scc);
                        supplierEditor.setFieldReadOnly("SystemEntity", "Organization");
                        supplierEditor.setNewObjectGenerator(this::createSupplier);
                        supplierMenu = new PopupButton("Supplier", VaadinIcon.INVOICE);
                        supplierMenu.add(new Button("View Details", e -> viewSupp()));
                        supplierMenu.add(new Button("Edit Details", e -> editSupp()));
                    }
                } catch(ClassNotFoundException ignored) {
                }
            }
        } else {
            relCache = null;
        }
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
            if(supplierEditor != null && (getR(e) & 1) == 1) {
                buttonPanel.add(supplierMenu);
            }
        }
    }

    private boolean us(Entity entity) {
        return getTransactionManager().getEntity().getEntityId().equals(entity.getId());
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
                case 1: // Supplier
                    r |= 1;
                    break;
                case 2: // Customer
                    r |= 2;
                    break;
                case 3: // Repair Org.
                    r |= 4;
                    break;
                case 17: // External owner
                    r |= 8;
                    break;
                case 8: // Rent out
                    r |= 16;
                    break;
                case 9: // Lease in
                    r |= 32;
                    break;
            }
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

    private EntityRole supplier() {
        return EntityRole.get(getTransactionManager().getEntity(), supplierEditor.getObjectClass(), getObject());
    }

    private void editSupp() {
        EntityRole er = supplier();
        if(er == null) {
            message("Creating new supplier details.");
            supplierEditor.addObject(this);
        } else {
            supplierEditor.editObject(er, this);
        }
    }

    private void viewSupp() {
        EntityRole er = supplier();
        if(er == null) {
            message("Supplier details not created! Please create it now.");
            return;
        }
        supplierEditor.viewObject(er, this);
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
                case 0: // Supplier
                    InventoryTransaction.createSupplierLocation(tm, e);
                    break;
                case 1: // Customer
                    InventoryTransaction.createConsumerLocation(tm, e);
                    break;
                case 2: // Repair Org.
                    InventoryTransaction.createRepairLocation(tm, e);
                    break;
                case 3: // External owner
                    InventoryTransaction.createExternalOwnerLocation(tm, e);
                    break;
                case 4: // Rent out
                    InventoryTransaction.createRentOutLocation(tm, e);
                    break;
                case 5: // Rent out
                    InventoryTransaction.createRentInLocation(tm, e);
                    break;
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
            w.append("Changes once saved can not be reverted!", "red").update();
            addField(entityName, relField, w);
        }

        void entity() {
            Entity entity = getObject();
            entityName.clearContent().append(entity, "blue").update();
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
            en.clearContent().append(entity, "blue").update();
            ELabelField p = new ELabelField("Current Relationships");
            p.append(getRS(previous), "blue").update();
            ELabelField c = new ELabelField("About to Add the Following Relationships");
            c.append(getRS(next), "red").update();
            ELabelField w = new ELabelField("Warning");
            w.append("Changes once saved can not be reverted!", "red").update();
            addField(en, p, c, w);
        }

        @Override
        protected boolean process() {
            close();
            setRel(next);
            return true;
        }
    }

    private EntityRole createSupplier() {
        try {
            EntityRole er = supplierEditor.getObjectClass().getDeclaredConstructor().newInstance();
            er.setSystemEntity(getTransactionManager().getEntity());
            er.setOrganization(getObject());
            return er;
        } catch(Throwable e) {
            error(e);
        }
        return null;
    }
}
