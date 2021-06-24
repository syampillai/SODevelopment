package com.storedobject.ui;

import com.storedobject.common.Address;
import com.storedobject.common.Country;
import com.storedobject.ui.util.AddressForm;
import com.storedobject.ui.util.XXAddressForm;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextArea;

public class AddressField extends CustomField<String> implements ValueRequired {

    private final CountryField countryField = new CountryField();
    private final TextArea display = new AddressDisplay();
    private final ButtonLayout editor = new ButtonLayout();
    private final ImageButton delete = new ImageButton("Delete", VaadinIcon.CLOSE, e -> delete());
    private Address address;
    private AddressForm form;
    private boolean required = false, deleted = true;

    public AddressField() {
        this(null);
    }

    public AddressField(String label) {
        ImageButton pop = new ImageButton("Edit details", VaadinIcon.ELLIPSIS_DOTS_H, e -> popup());
        style(pop);
        style(delete);
        editor.add(delete, pop, countryField);
        editor.setWidthFull();
        editor.setFlexGrow(1, countryField);
        delete.setVisible(false);
        add(editor, display);
        display.setReadOnly(true);
        display.setWidthFull();
        new Clickable<>(display, e -> {
            if(!isReadOnly() && isEnabled()) {
                popup();
            }
        });
        setLabel(label);
        countryField.addValueChangeListener(e -> {
           if(e.isFromClient() && countryField.getCountry() != null) {
               popup();
           }
        });
        try {
            address(countryField.getCountry());
        } catch (RuntimeException error) {
            Application.get().log(error);
            throw error;
        }
    }

    private void style(ImageButton button) {
        Box box = new Box(button);
        button.setSize("25px");
        box.alignSizing();
        box.grey();
    }

    @Override
    protected String generateModelValue() {
        return getValue();
    }

    @Override
    protected void setPresentationValue(String s) {
        display.setValue(address == null || deleted ? "" : address.toString());
    }

    public void switchAddress(Country country) {
        if(country == null) {
            country = Country.get(Application.getDefaultCountry());
        }
        if(country == null) {
            return;
        }
        countryField.setCountry(country);
        if(address == null) {
            return;
        }
        address(country);
    }

    private void address(Country country) {
        if(country == null) {
            country = getCountry();
        }
        Address a = Address.create(country);
        if (address != null) {
            a.copy(address);
            display.setValue(a.toString());
        } else {
            display.setValue("");
        }
        address = a;
    }

    public Country getCountry() {
        Country c = countryField.getCountry();
        if(c == null && address != null) {
            c = address.getCountry();
        }
        if(c == null) {
            c = Country.get(Application.getDefaultCountry());
        }
        return c;
    }

    @Override
    public void setValue(String value) {
        if(value == null) {
            value = "";
        }
        if(value.isEmpty()) {
            super.setValue(value);
            deleted = true;
            delete.setVisible(false);
            display.setValue("");
            return;
        }
        deleted = false;
        delete.setVisible(true);
        address = Address.create(value);
        if(address != null) {
            countryField.setCountry(address.getCountry());
        } else {
            address(countryField.getCountry());
        }
        super.setValue(address == null ? "" : address.encode());
    }

    @Override
    public String getValue() {
        return deleted ? "" : address.encode();
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        if(address == null) {
            address = Address.create(countryField.getCountry());
        }
        this.address = address;
        countryField.setCountry(address.getCountry());
        super.setValue(address.encode());
        deleted = false;
        delete.setVisible(true);
        setPresentationValue(address.toString());
    }

    @Override
    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        display.setReadOnly(true);
        editor.setVisible(!readOnly);
        delete.setVisible(!readOnly && !required && !deleted);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        editor.setVisible(!isReadOnly() && enabled);
    }

    private void popup() {
        Country country = getCountry();
        if(country == null) {
            return;
        }
        if(address.getCountry() != country) {
            address(country);
        }
        popup2(address);
    }

    private void popup2(Address address) {
        Country country = address.getCountry();
        if(form == null || form.getCountry() != country) {
            try {
                form = (AddressForm) getClass().getClassLoader().
                        loadClass("com.storedobject.ui.util." + country.getShortName() + "AddressForm").
                        getDeclaredConstructor().newInstance();
            } catch (Throwable e) {
                if(form == null || !(form instanceof XXAddressForm) || (address.getExtraLines() != ((XXAddressForm)form).getLineCount())) {
                    form = new XXAddressForm(address);
                }
            }
            form.setAddressField(this, address);
        } else {
            form.setAddress(address);
        }
        Application a = Application.get();
        a.setPostFocus(this);
        form.execute(a.getActiveView());
    }

    private void delete() {
        deleted = true;
        delete.setVisible(false);
        display.setValue("");
    }

    @Override
    public final boolean isRequired() {
        return required;
    }

    @Override
    public void setRequired(boolean required) {
        delete.setVisible(!isReadOnly() && !required);
        this.required = required;
    }

    private static class AddressDisplay extends TextArea {

        private AddressDisplay() {
            setWidthFull();
            setValue("");
        }

        @Override
        public void setValue(String value) {
            if(value == null) {
                value = "";
            } else {
                while (value.endsWith("\n")) {
                    value = value.substring(0, value.length() - 1);
                }
                value = value.replace("\n", ", ");
            }
            super.setValue(value);
        }
    }

    /**
     * Switch the current address to another one with a different country. Note: For internal use only.
     *
     * @param address Address to switch to.
     * @param from Called by.
     */
    public void switchAddress(Address address, AddressForm from) {
        if(this.form != from || address == null) {
            return;
        }
        this.form.close();
        this.form = null;
        popup2(address);
    }
}
