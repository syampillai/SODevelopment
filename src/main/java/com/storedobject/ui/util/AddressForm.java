package com.storedobject.ui.util;

import com.storedobject.common.Address;
import com.storedobject.common.Country;
import com.storedobject.ui.AddressField;
import com.storedobject.ui.CountryField;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.IntegerField;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.HasValue;
import org.vaadin.textfieldformatter.CustomStringBlockFormatter;

public abstract class AddressForm extends DataForm {

    private static final String B_NAME = "Building Number/Name";
    private AddressField addressField;
    private Address address;
    CountryField countryField = new CountryField("Country");
    ChoiceField typeField = new ChoiceField("Type", Address.getTypeValues());
    TextField apartmentField = new TextField(Address.getTypeValue('0') + " Number/Name");
    TextField buildingField = new TextField(B_NAME);
    TextField streetField = new TextField("Street Number & Name");
    TextField areaField = new TextField("Area");
    IntegerField poBoxField = new IntegerField("");
    TextField postalCodeField = new TextField("");
    private CustomStringBlockFormatter formatter;

    public AddressForm() {
        super("",false);
        setButtonsAtTop(true);
        setFirstFocus(typeField);
    }

    @Override
    protected void buildFields() {
        addField(countryField);
        countryField.addValueChangeListener(e -> switchCountry());
        addField(typeField, apartmentField, buildingField, streetField, areaField, poBoxField, postalCodeField);
        typeField.addValueChangeListener(e -> {
            int v = e.getValue();
            apartmentField.setLabel(Address.getTypeValue((char)('0' + v)) + " Number/Name");
            if(v == 1 || v == 2) {
                buildingField.setLabel("Community Name");
            } else {
                buildingField.setLabel(B_NAME);
            }
            if(e.isFromClient()) {
                apartmentField.focus();
            }
        });
    }

    private void switchCountry() {
        Country c = countryField.getCountry();
        if(c == null) {
            return;
        }
        Address a = Address.create(c);
        try {
            loadAddress(a);
            a.validate();
        } catch(Throwable ignored) {
        }
        addressField.switchAddress(a, this);
    }

    @Override
    protected boolean process() {
        clearAlerts();
        try {
            loadAddress(address);
            address.validate();
        } catch(Throwable e) {
            warning(e);
            return false;
        }
        addressField.setAddress(address);
        return true;
    }

    protected void loadAddress(Address address) {
        address.setType((char)('0' + typeField.getValue()));
        address.setApartmentName(apartmentField.getValue());
        address.setBuildingName(buildingField.getValue());
        address.setStreetName(streetField.getValue());
        address.setAreaName(areaField.getValue());
        address.setPOBox(poBoxField.getValue());
        address.setPostalCode(postalCodeField.getValue());
    }

    public final Country getCountry() {
        return countryField.getCountry();
    }

    public final void setAddressField(AddressField addressField, Address address) {
        this.addressField = addressField;
        setCaption(addressField.getLabel());
        setAddress(address);
    }

    public void setAddress(Address address) {
        this.address = address;
        countryField.setCountry(address.getCountry());
        typeField.setValue(address.getType() - '0');
        apartmentField.setValue(address.getApartmentName());
        buildingField.setValue(address.getBuildingName());
        streetField.setValue(address.getStreetName());
        areaField.setValue(address.getAreaName());
        areaField.setLabel(address.getAreaCaption());
        poBoxField.setLabel(address.getPOBoxName());
        poBoxField.setValue(address.getPOBox());
        postalCodeField.setLabel(address.getPostalCodeCaption());
        postalCodeField.setValue(address.getPostalCode());
        poBoxField.setVisible(address.isPOBoxAddress());
        postalCodeField.setVisible(address.isPostalCodeAddress());
        setPCFormatter();
    }

    private void setPCFormatter() {
        if(formatter != null || !address.isPostalCodeAddress()) {
            return;
        }
        int block = address.getPostalCodeMaxLength();
        if(block == Integer.MAX_VALUE || block != address.getPostalCodeMinLength()) {
            block = -1;
        }
        formatter = new CustomStringBlockFormatter(new int[] { block <= 0 ? 1 : block }, new String[] { },
                CustomStringBlockFormatter.ForceCase.UPPER, null, true);
        if(block > 0) {
            formatter.extend(postalCodeField);
        }
    }

    @Override
    public boolean isFieldVisible(HasValue<?, ?> field) {
        if(field == poBoxField || field == postalCodeField) {
            Address a = getAddress();
            if(a != null) {
                if(field == poBoxField) {
                    return a.isPOBoxAddress();
                }
                if(field == postalCodeField) {
                    return a.isPostalCodeAddress();
                }
            }
        }
        return super.isFieldVisible(field);
    }

    Address getAddress() {
        Address a = addressField.getAddress();
        if(a == null) {
            Country c = countryField.getCountry();
            if(c != null) {
                a = Address.create(c);
            }
        }
        return a;
    }
}