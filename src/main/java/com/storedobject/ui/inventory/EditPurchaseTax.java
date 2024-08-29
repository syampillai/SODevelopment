package com.storedobject.ui.inventory;

import com.storedobject.vaadin.DataForm;

public class EditPurchaseTax extends DataForm {

    public EditPurchaseTax(String caption) {
        super("Purchase Tax");
    }

    @Override
    protected boolean process() {
        return false;
    }
}
