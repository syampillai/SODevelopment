package com.storedobject.ui.util;

import com.storedobject.common.Address;
import com.storedobject.common.GBAddress;
import com.storedobject.vaadin.TextField;

public class GBAddressForm extends AddressForm {

    private final TextField postTown = new TextField("Post Town");

    @Override
    protected void buildFields() {
        super.buildFields();
        remove(postalCodeField);
        add(postTown, postalCodeField);
    }

    @Override
    boolean hasCommunityName() {
        return false;
    }

    @Override
    protected void loadAddress(Address address) {
        super.loadAddress(address);
        GBAddress a = (GBAddress) address;
        a.setPostTown(postTown.getValue());
    }

    @Override
    public void setAddress(Address address) {
        super.setAddress(address);
        try {
            GBAddress a = (GBAddress) address;
            postTown.setValue(a.getPostTown());
        } catch (Throwable ignored) {
        }
    }
}
