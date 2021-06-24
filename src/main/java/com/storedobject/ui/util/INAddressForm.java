package com.storedobject.ui.util;

import com.storedobject.common.Address;
import com.storedobject.common.INAddress;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.TextField;

public class INAddressForm extends AddressForm {

    private final ChoiceField statesField = new ChoiceField("State", INAddress.getStates());
    private final ChoiceField districtsField = new ChoiceField("District", INAddress.getDistricts(0));
    private final TextField poField = new TextField("Post Office");

    @Override
    protected void buildFields() {
        super.buildFields();
        remove(poBoxField, postalCodeField);
        addField(poField);
        add(poBoxField, postalCodeField);
        addField(statesField);
        addField(districtsField);
        statesField.addValueChangeListener(l -> {
            districtsField.setChoices(INAddress.getDistricts(l.getValue()));
           if(l.isFromClient()) {
               districtsField.focus();
           }
        });
    }

    @Override
    protected void loadAddress(Address address) {
        super.loadAddress(address);
        INAddress a = (INAddress) address;
        a.setPostOfficeName(poField.getValue());
        a.setState(statesField.getValue());
        a.setDistrict(districtsField.getValue());
    }

    @Override
    public void setAddress(Address address) {
        super.setAddress(address);
        try {
            INAddress a = (INAddress) address;
            poField.setValue(a.getPostOfficeName());
            statesField.setValue(a.getState());
            districtsField.setValue(a.getDistrict());
        } catch (Throwable ignored) {
        }
    }
}