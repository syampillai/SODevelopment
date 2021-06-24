package com.storedobject.ui.util;

import com.storedobject.common.Address;
import com.storedobject.common.MYAddress;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.TextField;

public class MYAddressForm extends AddressForm {

    private final ChoiceField statesField = new ChoiceField("State/Federal Territory", MYAddress.getStates());
    private final TextField poField = new TextField("Postal Town");

    @Override
    protected void buildFields() {
        super.buildFields();
        addField(poField);
        addField(statesField);
    }

    @Override
    protected void loadAddress(Address address) {
        super.loadAddress(address);
        MYAddress a = (MYAddress) address;
        a.setPostalTown(poField.getValue());
        a.setState(statesField.getValue());
    }

    @Override
    public void setAddress(Address address) {
        super.setAddress(address);
        try {
            MYAddress a = (MYAddress) address;
            poField.setValue(a.getPostalTown());
            statesField.setValue(a.getState());
        } catch (Throwable ignored) {
        }
    }
}