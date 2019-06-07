package com.storedobject.ui.util;

import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Quantity;
import com.storedobject.vaadin.CustomTextField;
import com.storedobject.vaadin.util.HasTextValue;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.HasPrefixAndSuffix;

import java.math.BigDecimal;

public class AbstractQuantityField<T extends Quantity> extends CustomTextField<T> {

    private Span unit;
    private Class<T> quantityClass;
    private int decimals;

    @SuppressWarnings("unchecked")
    public AbstractQuantityField(String label, int width, int decimals, Class<T> quantityClass, MeasurementUnit unit) {
        super(Quantity.create(quantityClass));
        this.quantityClass = quantityClass;
        if (width < 4 || width > 20) {
            width = 12;
        }

        if (decimals < 1 || decimals > width - 2) {
            decimals = 0;
        }
        this.decimals = decimals;
        getField().setMaxLength(width);
        if (unit != null && MeasurementUnit.get(unit.getUnit(), quantityClass) != null) {
            this.setValue((T)Quantity.create(0.0D, unit));
        }
        this.setPresentationValue(this.getValue());
        this.setLabel(label);
    }

    @Override
    protected void customizeTextField(HasTextValue textField) {
        if (this.unit == null) {
            this.unit = new Span();
        }
        ((HasPrefixAndSuffix)textField).setSuffixComponent(this.unit);
    }

    public MeasurementUnit getUnit() {
        return MeasurementUnit.get(this.unit.getText(), this.quantityClass);
    }

    public int getDecimals() {
        return this.decimals;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T getModelValue(String string) {
        T q;
        string = string.trim().replace(",", "");
        try {
            Double.parseDouble(string);
            q = (T)Quantity.create(new BigDecimal(string), this.getUnit());
        } catch (Throwable var3) {
            if (string.isEmpty() || !Character.isDigit(string.charAt(0))) {
                string = "0" + string;
            }
            q = Quantity.create(string, this.quantityClass);
        }
        setPresentationValue(q);
        return q;
    }

    @Override
    protected String format(T value) {
        return value.toString(false);
    }

    @Override
    protected void setPresentationValue(T value) {
        getField().setValue(format(value));
        this.unit.setText(value.getUnit().getUnit());
    }
}
