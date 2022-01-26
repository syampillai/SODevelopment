package com.storedobject.ui.util;

import com.storedobject.common.Address;
import com.storedobject.common.SOException;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.common.USAddress;

public class USAddressForm extends AddressForm {

    private final ChoiceField stateField;

    public USAddressForm() {
        stateField = new ChoiceField("State", USAddress.getStates().toList());
        setFirstFocus(streetField);
    }

    @Override
    protected void buildFields() {
        super.buildFields();
        remove(typeField, apartmentField, buildingField, streetField, areaField, poBoxField, postalCodeField);
        add(streetField, typeField, apartmentField, buildingField, areaField, poBoxField);
        addField(stateField);
        add(postalCodeField);
        setRequired(streetField);
        setRequired(areaField);
    }

    @Override
    void firstFocus() {
        streetField.focus();
    }

    @Override
    protected void loadAddress(Address address) {
        super.loadAddress(address);
        ((USAddress)address).setState(stateField.getValue());
    }

    @Override
    public void setAddress(Address address) {
        super.setAddress(address);
        try {
            stateField.setValue(((USAddress)address).getState());
        } catch(SOException ignored) {
        }
    }
}
