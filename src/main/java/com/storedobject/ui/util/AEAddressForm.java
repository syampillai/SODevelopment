package com.storedobject.ui.util;

import com.storedobject.common.AEAddress;
import com.storedobject.common.Address;
import com.storedobject.vaadin.ChoiceField;

public class AEAddressForm extends AddressForm {

    private final ChoiceField emiratesField = new ChoiceField("Emirate", AEAddress.getEmirates());

    @Override
    protected void buildFields() {
        super.buildFields();
        addField(emiratesField);
    }

    @Override
    protected void loadAddress(Address address) {
        super.loadAddress(address);
        ((AEAddress)address).setEmirate(emiratesField.getValue());
    }

    @Override
    public void setAddress(Address address) {
        super.setAddress(address);
        try {
            emiratesField.setValue(((AEAddress)address).getEmirate());
        } catch (Throwable ignored) {
        }
    }
}
