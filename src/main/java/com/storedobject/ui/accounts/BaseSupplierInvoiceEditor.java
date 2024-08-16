package com.storedobject.ui.accounts;

import com.storedobject.accounts.EntityAccount;
import com.storedobject.accounts.SupplierInvoice;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.inventory.GRN;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;

import java.util.ArrayList;
import java.util.List;

public class BaseSupplierInvoiceEditor<I extends SupplierInvoice> extends AbstractInvoiceEditor<I> {

    private CreateNew createNew;
    private final Button viewGRN = new Button("View GRN", e -> viewGRN(getObject()));
    private InventoryGRN grn;

    public BaseSupplierInvoiceEditor(Class<I> objectClass) {
        super(objectClass);
    }

    public BaseSupplierInvoiceEditor(Class<I> objectClass, int actions) {
        super(objectClass, actions);
    }

    public BaseSupplierInvoiceEditor(Class<I> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public BaseSupplierInvoiceEditor(String className) throws Exception {
        super(className);
    }

    protected BaseSupplierInvoiceEditor(Class<I> objectClass, int actions, String caption, String allowedActions) {
        super(objectClass, actions, caption, allowedActions);
    }

    @Override
    public void doEdit() {
        I invoice = getObject();
        if(invoice == null) {
            return;
        }
        setFieldReadOnly(invoice.getTotal().getCurrency() == getTransactionManager().getCurrency(),
                "ExchangeRate");
        setFieldReadOnly(invoice.getFromInventory(), "Amount", "Total", "InvoiceNo", "Date", "Party");
        super.doEdit();
    }

    @Override
    public void doAdd() {
        grn = null;
        if(accountOnly) {
            doAddSuper();
            return;
        }
        if(createNew == null) {
            setFieldReadOnly("Amount", "Total", "InvoiceNo", "Date", "Party");
            try {
                createNew = new CreateNew();
            } catch (SORuntimeException e) {
                warning(e);
                return;
            }
        }
        createNew.execute();
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        SupplierInvoice si = getObject();
        if(si != null && si.getFromInventory()) {
            buttonPanel.add(viewGRN);
        }
    }

    private int getGRNType() {
        return configuration.getType() == 1001 ? 3 : 0; // 1001 is for RO, otherwise POs
    }

    void viewGRN(SupplierInvoice supplierInvoice) {
        if(supplierInvoice == null) {
            return;
        }
        clearAlerts();
        if(!supplierInvoice.getFromInventory()) {
            message("Not created from a GRN");
            return;
        }
        InventoryGRN grn = supplierInvoice.listLinks(InventoryGRN.class).single(false);
        if (grn == null) {
            message("No GRN found");
            return;
        }
        GRN grnView = new GRN(grn.getType(), grn.getStore(), EditorAction.VIEW);
        grnView.viewGRN(grn);
    }

    private class CreateNew extends DataForm {

        private final ObjectField<InventoryStore> storeField;
        private final ObjectField<Entity> supplierField;
        private final ObjectComboField<InventoryGRN> grnField;

        public CreateNew() {
            super("New Supplier Invoice - Select GRN");
            storeField = new ObjectField<>("Store", InventoryStore.class, true);
            addField(storeField);
            setRequired(storeField);
            InventoryGRN grn = StoredObject.get(InventoryGRN.class, "Type=0");
            if(grn != null) {
                storeField.setValue(grn.getStore());
            }
            List<Entity> suppliers = GRN.suppliers(getGRNType());
            if(suppliers.isEmpty()) {
                throw new SORuntimeException("No suppliers found!");
            }
            supplierField = new ObjectField<>("Supplier", Entity.class,  suppliers, false);
            addField(supplierField);
            setRequired(supplierField);
            grnField = new ObjectComboField<>("GRN", InventoryGRN.class, new ArrayList<>());
            grnField.setItemLabelGenerator(g -> g.toDisplay() + " - Invoice " + g.getInvoiceNumber());
            addField(grnField);
            setRequired(grnField);
            storeField.addValueChangeListener(e -> changed());
            supplierField.addValueChangeListener(e -> changed());
        }

        private void changed() {
            clearAlerts();
            Id storedId = storeField.getValue(), entityId = supplierField.getValue();
            if(Id.isNull(storedId) || Id.isNull(entityId)) {
                grnField.load(ObjectIterator.create());
                return;
            }
            List<InventoryGRN> grns = StoredObject.list(InventoryGRN.class,
                    "Store=" + storedId + " AND Supplier=" + entityId + " AND Type=" + getGRNType()
                            + " AND Status>0")
                    .filter(g -> g.getSupplierInvoice() == null)
                    .toList();
            grnField.load(ObjectIterator.create(grns));
            if(grns.isEmpty()) {
                warning("No GRNs found for the supplier selected");
            }
        }

        @Override
        protected boolean process() {
            grn = grnField.getValue();
            if(grn.getSupplierInvoice() != null) {
                warning("Invoice already created for that GRN");
                changed();
                return false;
            }
            clearAlerts();
            List<EntityAccount> accounts = partyAccounts(grn.getSupplierId(), grn.getTotal().getCurrency());
            if(accounts.isEmpty()) {
                warning("No accounts found for the supplier selected");
                return false;
            }
            close();
            if(accounts.size() == 1) {
                partyAccount = accounts.get(0);
                doAddSuper();
                return true;
            }
            new SelectAccount(accounts).execute();
            return true;
        }
    }

    private class SelectAccount extends DataForm {

        private final ObjectComboField<EntityAccount> accountField;

        public SelectAccount(List<EntityAccount> accounts) {
            super("Select Account");
            accountField = new ObjectComboField<>("Account", EntityAccount.class, accounts);
            addField(accountField);
            setRequired(accountField);
        }

        @Override
        protected boolean process() {
            partyAccount = accountField.getValue();
            close();
            doAddSuper();
            return true;
        }
    }

    @Override
    protected I createObjectInstance() {
        if(grn == null) {
            return super.createObjectInstance();
        }
        try {
            return SupplierInvoice.createFrom(getObjectClass(), grn, partyAccount);
        } catch (Exception e) {
            warning(e);
            return null;
        }
    }

    @Override
    protected void saveObject(Transaction t, I object) throws Exception {
        super.saveObject(t, object);
        if(grn != null) {
            object.addLink(t, grn);
        }
    }
}
