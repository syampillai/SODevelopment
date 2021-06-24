package com.storedobject.ui.util;

import com.storedobject.common.Address;
import com.storedobject.common.Country;
import com.storedobject.common.XXAddress;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.HasValue;

public class XXAddressForm extends AddressForm {

    private final TextField[] lineField;

    public XXAddressForm(String countryCode) {
        this(Address.create(Country.get(countryCode)));
    }

    public XXAddressForm(Address address) {
        lineField = new TextField[address.getExtraLines()];
        for(int i = 0; i < lineField.length; i++) {
            lineField[i] = new TextField();
        }
        setLabels(address);
    }

    void setLabels(Address address) {
        if(address == null) {
            address = getAddress();
        }
        if(address == null) {
            return;
        }
        if(address instanceof XXAddress a) {
            for(int i = 0; i < lineField.length; i++) {
                lineField[i].setLabel(a.getLineCaption(i));
            }
        } else {
            for(int i = 0; i < lineField.length; i++) {
                lineField[i].setLabel("Address Line " + (i + 1));
            }
        }
    }

    @Override
    protected void buildFields() {
        super.buildFields();
        for (TextField textField: lineField) {
            addField(textField);
        }
    }

    @Override
    protected void loadAddress(Address address) {
        super.loadAddress(address);
        for (int i = 0; i < lineField.length; i++) {
            address.setLine(i, lineField[i].getValue().trim());
        }
    }

    @Override
    public void setAddress(Address address) {
        super.setAddress(address);
        for(int i = 0; i < lineField.length; i++) {
            lineField[i].setValue(address.getLine(i));
            if(address instanceof XXAddress && ((XXAddress)address).getLineCaption(i) == null) {
                lineField[i].setVisible(false);
            }
        }
    }

    public int getLineCount() {
        return lineField.length;
    }

    @Override
    public boolean isFieldVisible(HasValue<?, ?> field) {
        if(field instanceof TextField) {
            Address address = getAddress();
            if(address instanceof XXAddress) {
                XXAddress a = (XXAddress) getAddress();
                if(a != null) {
                    for(int i = 0; i < lineField.length; i++) {
                        if(field == lineField[i]) {
                            return a.getLineCaption(i) != null;
                        }
                    }
                }
            }
        }
        return super.isFieldVisible(field);
    }

    String getFieldValue(int index) {
        return lineField[index].getValue();
    }
}
