package com.storedobject.ui.util;

import com.storedobject.common.Address;
import com.storedobject.common.PKAddress;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.TextField;

public class PKAddressForm extends AddressForm {

    private final ChoiceField provincesField = new ChoiceField("Province", PKAddress.getProvinces());
    private final ChoiceField districtsField = new ChoiceField("District", PKAddress.getDistricts(0));
    private final TextField placeField = new TextField("Place");

    @Override
    protected void buildFields() {
        super.buildFields();
        remove(poBoxField, postalCodeField);
        addField(placeField);
        add(poBoxField, postalCodeField);
        addField(provincesField);
        addField(districtsField);
        provincesField.addValueChangeListener(l -> {
            districtsField.setChoices(PKAddress.getDistricts(l.getValue()));
            if(l.isFromClient()) {
                districtsField.focus();
            }
        });
    }

    @Override
    protected void loadAddress(Address address) {
        super.loadAddress(address);
        PKAddress a = (PKAddress) address;
        a.setPlaceName(placeField.getValue());
        a.setProvince(provincesField.getValue());
        a.setDistrict(districtsField.getValue());
    }

    @Override
    public void setAddress(Address address) {
        super.setAddress(address);
        try {
            PKAddress a = (PKAddress) address;
            placeField.setValue(a.getPlaceName());
            provincesField.setValue(a.getProvince());
            districtsField.setValue(a.getDistrict());
        } catch (Throwable ignored) {
        }
    }
}