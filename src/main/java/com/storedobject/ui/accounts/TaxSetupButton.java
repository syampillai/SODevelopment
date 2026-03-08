package com.storedobject.ui.accounts;

import com.storedobject.accounts.*;
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

    private ObjectEditor<BusinessEntity> taxEditorBusiness;
    private ObjectEditor<PersonalEntity> taxEditorPersonal;

    /**
     * Constructor for TaxSetupButton.
     */
    public TaxSetupButton() {
        super(StoredObject.class, "Tax Setup", VaadinIcon.BOOK_DOLLAR);
    }

    @Override
    public void accept(StoredObject role, Object source) {
        if(!(role instanceof EntityAccountFinder f) || !(source instanceof Transactional t)) {
            warn("Unable to find account of " + role.toDisplay());
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
            error(e);
            return;
        }
        View v = source instanceof View ? (View)source: (source instanceof HasColumns<?> hc ? hc.getView(): null);
        AccountEntity<?> ae = account.getEntity();
        if(ae instanceof PersonalEntity pe) {
            if (taxEditorPersonal == null) taxEditorPersonal = ObjectEditor.create(PersonalEntity.class);
            taxEditorPersonal.editObject(pe, v);
        } else if(ae instanceof BusinessEntity be) {
            if (taxEditorBusiness == null) taxEditorBusiness = ObjectEditor.create(BusinessEntity.class);
            taxEditorBusiness.editObject(be, v);
        }
    }

    private void warn(Object message) {
        Application.warning(message);
    }

    private void error(Object message) {
        Application.error(message);
    }
}
