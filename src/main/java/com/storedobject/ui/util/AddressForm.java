package com.storedobject.ui.util;

import com.storedobject.common.Address;
import com.storedobject.common.Country;
import com.storedobject.ui.AddressField;
import com.storedobject.ui.CountryField;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.IntegerField;
import com.storedobject.vaadin.TextField;
import org.vaadin.textfieldformatter.CustomStringBlockFormatter;

public abstract class AddressForm extends DataForm {

    private static final String B_NAME = "Building Number/Name";
    private static final String AREA_LABEL = "Area";
    private AddressField addressField;
    private Address address;
    final CountryField countryField = new CountryField("Country");
    final ChoiceField typeField = new ChoiceField("Type", Address.getTypeValues());
    final TextField apartmentField = new TextField(Address.getTypeValue('0') + " Number/Name");
    final TextField buildingField = new TextField(B_NAME);
    final TextField streetField = new TextField("Street Number & Name");
    final TextField areaField = new TextField(AREA_LABEL);
    final IntegerField poBoxField = new IntegerField("");
    final TextField postalCodeField = new TextField("");
    private CustomStringBlockFormatter formatter;

    public AddressForm() {
        super("",true);
        setButtonsAtTop(true);
        setFirstFocus(typeField);
    }

    @Override
    public int getMaximumContentWidth() {
        return 30;
    }

    @Override
    protected void buildFields() {
        addField(countryField);
        setRequired(countryField);
        countryField.addValueChangeListener(e -> switchCountry());
        addField(typeField, apartmentField, buildingField, streetField, areaField, poBoxField, postalCodeField);
        typeField.addValueChangeListener(e -> {
            int v = e.getValue();
            apartmentField.setLabel(Address.getTypeValue((char)('0' + v)) + " Number/Name");
            if(v == 1 || v == 2) {
                buildingField.setLabel("Community Name");
                buildingField.setVisible(hasCommunityName());
            } else {
                buildingField.setLabel(B_NAME);
                buildingField.setVisible(true);
            }
            areaField.setLabel(v == 3 ? areaLabelForOffice() : AREA_LABEL);
            if(e.isFromClient()) {
                firstFocus();
            }
        });
    }

    String areaLabelForOffice() {
        return AREA_LABEL;
    }

    boolean hasCommunityName() {
        return true;
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        ok.setText("Save");
    }

    void firstFocus() {
        apartmentField.focus();
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
        address.setApartmentName(apartmentField.getValue());
        address.setType((char)('0' + typeField.getValue()));
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
        setFieldVisible(address.isPOBoxAddress(), poBoxField);
        postalCodeField.setLabel(address.getPostalCodeCaption());
        postalCodeField.setValue(address.getPostalCode());
        setFieldVisible(address.isPostalCodeAddress(), postalCodeField);
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

    Address getAddress() {
        Address a = addressField.getAddress();
        if(a == null) {
            Country c = countryField.getCountry();
            if(c == null) {
                String sn = getClass().getName();
                sn = sn.substring(sn.lastIndexOf('.') + 1);
                sn = sn.substring(0, 2);
                c = Country.get(sn);
            }
            a = Address.create(c);
        }
        return a;
    }
}