package com.storedobject.ui.util;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Quantity;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.CustomTextField;
import com.storedobject.vaadin.RequiredField;
import com.storedobject.vaadin.util.HasTextValue;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;
import com.vaadin.flow.component.textfield.TextField;

import java.math.BigDecimal;
import java.util.*;

public class AbstractQuantityField<T extends Quantity> extends CustomTextField<T> implements RequiredField {

    private static final String HELP = "Click on the unit to change it";
    private Span unit;
    private Collection<MeasurementUnit> allowedUnits;
    private ItemContextMenu<MeasurementUnit> popup;
    private final Class<T> quantityClass;
    private final int decimals;
    private boolean required = false;
    private boolean changed = false;
    private T maxAllowed;

    @SuppressWarnings("unchecked")
    public AbstractQuantityField(String label, int width, int decimals, Class<T> quantityClass, MeasurementUnit unit) {
        super(Quantity.create(quantityClass));
        this.quantityClass = quantityClass;
        if (width < 4 || width > 20) {
            width = 20;
        }
        if (decimals < 1 || decimals > (width - 2)) {
            decimals = 0;
        }
        this.decimals = decimals;
        getField().setMaxLength(width);
        if (unit != null && MeasurementUnit.get(unit.getUnit(), quantityClass) != null) {
            this.setValue((T)Quantity.create(0.0D, unit));
        }
        this.setPresentationValue(this.getValue());
        this.setLabel(label);
        if(quantityClass != Quantity.class) {
            setAllowedUnits(MeasurementUnit.list(quantityClass));
        }
        unit(false);
    }

    @Override
    protected void customizeTextField(HasTextValue textField) {
        if (this.unit == null) {
            this.unit = new Span();
            this.unit.getStyle().set("background", "var(--lumo-error-color-10pct)").set("margin-right", "3px");
        }
        ((HasPrefixAndSuffix)textField).setPrefixComponent(this.unit);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        Application a = Application.get();
        if(a != null) {
            ((TextField) getField()).setAutoselect(!a.getWebBrowser().isAndroid());
        }
    }

    public MeasurementUnit getUnit() {
        return MeasurementUnit.get(this.unit.getText(), this.quantityClass);
    }

    private T v() {
        return v(getValue());
    }

    private T v(T v) {
        return v == null ? Quantity.create(quantityClass) : v;
    }

    private void unitChanged(MeasurementUnit unit) {
        if(unit == null || isReadOnly()) {
            return;
        }
        T q = v();
        MeasurementUnit u = q.getUnit();
        if(u == unit) {
            return;
        }
        T q1;
        try {
            //noinspection unchecked
            q1 = (T) q.convert(unit);
            if(q1 != null) {
                changed = true;
                setValue(q1);
                return;
            }
        } catch(Throwable ignored) {
        }
        //noinspection unchecked
        setValue((T)Quantity.create(q.getValue(), unit));
    }

    @Override
    public void setValue(T value) {
        value = convert(value);
        T v = getValue();
        if(value.isZero() && v.isZero() && !value.getUnit().equals(v.getUnit())) {
            setPresentationValue(value);
        }
        super.setValue(convert(value));
    }

    private T convert(T value) {
        try {
            return convertX(value);
        } catch(Throwable e) {
            //noinspection unchecked
            return (T) Quantity.create(value.getValue(), allowedUnits.stream().findAny().orElse(null));
        }
    }

    private T convertX(T value) {
        value = v(value);
        if(allowedUnits != null && !allowedUnits.contains(value.getUnit())) {
            MeasurementUnit unit = value.getUnit();
            MeasurementUnit mu = allowedUnits.stream().filter(u -> u.getType() == unit.getType()).findFirst().
                    orElse(null);
            if(mu == null) {
                if(value.isZero()) {
                    mu = allowedUnits.stream().findFirst().orElseThrow();
                } else {
                    throw new SORuntimeException();
                }
            }
            //noinspection unchecked
            value = (T) value.convert(mu);
        }
        return value;
    }

    public int getDecimals() {
        return this.decimals;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T getModelValue(String string) {
        T q;
        string = string.trim().replace(",", "").replace("+", "");
        try {
            Double.parseDouble(string);
            q = (T)Quantity.create(new BigDecimal(string), this.getUnit());
        } catch (Throwable notANumber) {
            try {
                q = Quantity.create(string, this.quantityClass);
            } catch(Throwable unitError) {
                q = (T) Quantity.create(this.getUnit());
            }
        }
        q = convert(q);
        if(maxAllowed != null) {
            try {
                if(q.isGreaterThan(maxAllowed)) {
                    throw new Exception();
                }
            } catch(Throwable unitError) {
                q = maxAllowed;
                setHelperText("Maximum allowed is " + maxAllowed);
                focus();
            }
        }
        setPresentationValue(q);
        return q;
    }

    @Override
    protected String format(T value) {
        value = convert(value);
        return required && (value.isZero()) ? "" : value.toString(false);
    }

    @Override
    protected void setPresentationValue(T value) {
        value = convert(value);
        getField().setValue(format(value));
        this.unit.setText(value.getUnit().getUnit());
    }

    @Override
    public void setRequired(boolean required) {
        setRequiredIndicatorVisible(required);
        this.required = required;
        setPresentationValue(getValue());
    }

    @Override
    protected boolean valueEquals(T value1, T value2) {
        if(changed) {
            changed = false;
            return false;
        }
        if(value1.equals(value2)) {
            return value1.isZero() || value1.getUnit().equals(value2.getUnit());
        }
        return false;
    }

    public void setAllowedUnits(Collection<MeasurementUnit> allowedUnits) {
        if(allowedUnits != null) {
            allowedUnits = new ArrayList<>(allowedUnits);
            allowedUnits.removeIf(Objects::isNull);
            if(quantityClass != Quantity.class) {
                int type = v().getUnit().getType();
                allowedUnits.removeIf(u -> u.getType() != type);
            }

        }
        if(allowedUnits == null || allowedUnits.isEmpty()) {
            this.allowedUnits = null;
        } else {
            this.allowedUnits = allowedUnits;
        }
        checkUnit();
    }

    public void setAllowedUnits(MeasurementUnit... units) {
        if(units == null) {
            setAllowedUnits((Collection<MeasurementUnit>) null);
        } else {
            setAllowedUnits(Arrays.asList(units));
        }
    }

    public void setAllowedUnits(String... units) {
        if(units == null) {
            setAllowedUnits((Collection<MeasurementUnit>) null);
        } else {
            List<MeasurementUnit> list = new ArrayList<>();
            for(String u: units) {
                list.add(MeasurementUnit.get(u, quantityClass));
            }
            setAllowedUnits(list);
        }
    }

    private void checkUnit() {
        if(allowedUnits == null || allowedUnits.size() == 1) {
            setHelperText(null);
            if(popup != null) {
                popup.setTarget(null);
                popup = null;
            }
        } else {
            if(popup == null) {
                popup = new ItemContextMenu<>(this.unit, this::unitChanged);
            }
            popup.setItems(allowedUnits);
            if(isReadOnly()) {
                setHelperText(null);
            } else {
                setHelperText(HELP);
            }
        }
        unit(isReadOnly());
        if(allowedUnits == null) {
            return;
        }
        T q = getValue();
        if(!allowedUnits.contains(q.getUnit())) {
            setValue(getValue());
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        unit(readOnly);
        setHelperText(allowedUnits != null && allowedUnits.size() > 1 && !readOnly ? HELP : null);
        if(popup != null) {
            if(readOnly) {
                popup.setTarget(null);
            } else {
                popup.setTarget(unit);
            }
        }
        getField().setValue(getValue().toString(readOnly));
        unit.setVisible(!readOnly);
    }

    private void unit(boolean readOnly) {
        unit.getStyle().set("cursor", "pointer");
        unit.getElement().
                setProperty("title",
                        allowedUnits != null && allowedUnits.size() > 1 && !readOnly ?
                                "Click to change" : "Unit");
    }

    public void setMaximumAllowed(T maximumAllowedQuantity) {
        this.maxAllowed = maximumAllowedQuantity;
    }
}
