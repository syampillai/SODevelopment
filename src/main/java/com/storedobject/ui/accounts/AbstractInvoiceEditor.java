package com.storedobject.ui.accounts;

import com.storedobject.accounts.*;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public abstract class AbstractInvoiceEditor<I extends Invoice> extends ObjectEditor<I> {

    final static boolean accountOnly = !StoredObject.exists(InventoryStore.class, "true", true);
    AccountConfiguration configuration;
    EntityAccount partyAccount;

    public AbstractInvoiceEditor(Class<I> objectClass) {
        super(objectClass);
    }

    public AbstractInvoiceEditor(Class<I> objectClass, int actions) {
        super(objectClass, actions);
    }

    public AbstractInvoiceEditor(Class<I> objectClass, int actions, String caption) {
        super(objectClass, actions, caption);
    }

    public AbstractInvoiceEditor(String className) throws Exception {
        super(className);
    }

    protected AbstractInvoiceEditor(Class<I> objectClass, int actions, String caption, String allowedActions) {
        super(objectClass, actions, caption, allowedActions);
    }

    protected void doAddSuper() {
        super.doAdd();
    }

    public void setConfiguration(AccountConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean canEdit() {
        I invoice = getObject();
        if(invoice != null && invoice.isLedgerPosted()) {
            clearAlerts();
            warning("Can't edit, already posted.");
            return false;
        }
        return super.canEdit();
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(configuration == null) {
            selectConfiguration(() -> super.execute(parent, doNotLock));
            return;
        }
        super.execute(parent, doNotLock);
    }

    void selectConfiguration(Runnable runnable) {
        new SelectConfiguration(runnable).execute();
    }

    private class SelectConfiguration extends DataForm {

        private final Runnable runnable;
        private final ObjectComboField<AccountConfiguration> configurationField;

        public SelectConfiguration(Runnable runnable) {
            super("Select");
            this.runnable = runnable;
            configurationField = new ObjectComboField<>("Configuration", AccountConfiguration.class,
                    "SystemEntity=" + getTransactionManager().getEntity().getId() +
                    " AND Category=" + (SupplierInvoice.class.isAssignableFrom(getDataClass()) ? 0 : 1));
            addField(configurationField);
            setRequired(configurationField);
            //noinspection SizeReplaceableByIsEmpty
            if(configurationField.size() == 0) {
                throw new SORuntimeException("Invoice configuration is empty");
            }
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            if(configurationField.size() == 1) {
                config(configurationField.get(0));
                return;
            }
            super.execute(parent, doNotLock);
        }

        @Override
        protected boolean process() {
            close();
            config(configurationField.getValue());
            return true;
        }

        private void config(AccountConfiguration ac) {
            setConfiguration(ac);
            if(runnable != null) {
                runnable.run();
            }
        }
    }

    protected List<EntityAccount> partyAccounts(Id entityId, Currency currency) {
        TransactionManager tm = getTransactionManager();
        BusinessEntity be;
        try {
            be = BusinessEntity.createFor(tm, entityId);
        } catch (Exception e) {
            error(e);
            return new ArrayList<>();
        }
        Id sysId = getTransactionManager().getEntity().getId();
        List<EntityAccount> list = StoredObject.list(configuration.getEntityAccountClass(),
                "Entity=" + be.getId(), (configuration.getAllow() & 1) == 1)
                .filter(a -> a.getSystemEntityId().equals(sysId) && a.getCurrency() == currency)
                .map(a -> (EntityAccount)a).toList();
        if(list.isEmpty()) {
            try {
                EntityAccount ea = EntityAccount.createFor(tm, be, currency, configuration.getEntityAccountClass());
                if(ea != null) {
                    list.add(ea);
                }
            } catch (Exception e) {
                error(e);
            }
        }
        return list;
    }
}
