package com.storedobject.ui.util;

import com.storedobject.common.Address;
import com.storedobject.common.NPAddress;
import com.storedobject.vaadin.IntegerField;
import com.storedobject.vaadin.TextField;

public class NPAddressForm extends AddressForm {

    private final IntegerField wardField = new IntegerField("Ward");
    private final TextField districtField = new TextField("District");

    @Override
    protected void buildFields() {
        super.buildFields();
        removeField(areaField);
        removeField(postalCodeField);
        addField(wardField, areaField, postalCodeField, districtField);
        setRequired(wardField, areaField, postalCodeField, buildingField);
    }

    @Override
    protected void loadAddress(Address address) {
        super.loadAddress(address);
        NPAddress a = (NPAddress) address;
        a.setWard(wardField.getValue());
        a.setDistrict(districtField.getValue());
    }

    @Override
    public void setAddress(Address address) {
        super.setAddress(address);
        NPAddress a = (NPAddress) address;
        wardField.setValue(a.getWard());
        districtField.setValue(a.getDistrict());
    }
}
