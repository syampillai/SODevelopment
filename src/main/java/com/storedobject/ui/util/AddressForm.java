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

public abstract class AddressForm extends DataForm {

    private static final String B_NAME = "Building Number/Name", A_NAME = "Apartment Number/Name";
    private AddressField addressField;
    CountryField countryField = new CountryField("Country");
    ChoiceField typeField = new ChoiceField("Type", new String[] { "Apartment", "Villa", "House", "Office" });
    TextField apartmentField = new TextField(A_NAME);
    TextField buildingField = new TextField(B_NAME);
    TextField streetField = new TextField("Street Number & Name");
    TextField areaField = new TextField("Area");
    IntegerField poBoxField = new IntegerField("");
    IntegerField postalCodeField = new IntegerField("");

    public AddressForm() {
        super("",false);
        setButtonsAtTop(true);
    }

    @Override
    protected void buildFields() {
        addField(countryField);
        setFieldReadOnly(countryField);
        addField(typeField, apartmentField, buildingField, streetField, areaField, poBoxField, postalCodeField);
        typeField.addValueChangeListener(e -> {
            int v = e.getValue();
            switch (v) {
                case 0:
                    apartmentField.setLabel(A_NAME);
                    break;
                case 1:
                    apartmentField.setLabel("Villa Number/Name");
                    break;
                case 2:
                    apartmentField.setLabel("House Number/Name");
                    break;
                case 3:
                    apartmentField.setLabel("Office Name");
                    break;
            }
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

    @Override
    protected boolean process() {
        Address a;
        try {
            a = addressField.getAddress();
            loadAddress(a);
            a.validate();
        } catch(Throwable e) {
            warning(e);
            return false;
        }
        addressField.setAddress(a);
        return true;
    }

    protected void loadAddress(Address address) {
        address.setApartmentCode((char)('0' + typeField.getValue()));
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

    public final void setAddressField(AddressField addressField) {
        this.addressField = addressField;
        setCaption(addressField.getLabel());
        setAddress(addressField.getAddress());
    }

    public void setAddress(Address address) {
        countryField.setCountry(address.getCountry());
        typeField.setValue(address.getApartmentCode() - '0');
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
            } else {
                System.err.println("Here");
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