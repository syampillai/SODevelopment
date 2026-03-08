package com.storedobject.ui.accounts;

import com.storedobject.accounts.AccountFinder;
import com.storedobject.accounts.BusinessEntity;
import com.storedobject.accounts.EntityAccount;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectLogicButton;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.HasColumns;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Button for setting up tax-related configurations for business entities.
 *
 * @author Syam
 */
public class TaxSetupButton extends ObjectLogicButton<StoredObject> {

    private ObjectEditor<BusinessEntity> taxEditor;

    /**
     * Constructor for TaxSetupButton.
     */
    public TaxSetupButton() {
        super(StoredObject.class, "Tax Setup", VaadinIcon.BOOK_DOLLAR);
    }

    @Override
    public void accept(StoredObject role, Object source) {
        if(!(role instanceof AccountFinder f) || !(source instanceof Transactional t)) {
            warn("Cannot find account of " + role.toDisplay());
            return;
        }
        EntityAccount account;
        try {
            account = f.getAccount(t.getTransactionManager());
            if(account == null) {
                warn("No account found for " + role.toDisplay());
                return;
            }
        } catch (Exception e) {
            warn(e);
            return;
        }
        if (taxEditor == null) taxEditor = ObjectEditor.create(BusinessEntity.class);
        View v = source instanceof View ? (View)source: (source instanceof HasColumns<?> hc ? hc.getView(): null);
        taxEditor.editObject((BusinessEntity) account.getEntity(), v);
    }

    private void warn(Object message) {
        Application.warning(message);
    }
}
