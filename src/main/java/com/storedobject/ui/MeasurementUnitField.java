package com.storedobject.ui;

import com.storedobject.core.Count;
import com.storedobject.core.MeasurementUnit;
import com.storedobject.core.Quantity;
import com.storedobject.core.StringUtility;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.ComboField;
import com.storedobject.vaadin.CustomField;

import java.util.List;
import java.util.stream.Collectors;

public class MeasurementUnitField extends CustomField<MeasurementUnit> {

    private final static List<Class<?>> classList = Quantity.types().collect(Collectors.toList());
    private final ComboField<Class<?>> classes = new ComboField<>(classList);
    private final ComboField<MeasurementUnit> units = new ComboField<>(MeasurementUnit.list(Count.class));

    /**
     * Constructor.
     */
    public MeasurementUnitField() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     */
    public MeasurementUnitField(String label) {
        super(null);
        classes.setItemLabelGenerator(StringUtility::makeLabel);
        units.setWidth("7em");
        add(new ButtonLayout(classes, units));
        classes.addValueChangeListener(e -> {
            if(e.isFromClient()) {
                @SuppressWarnings("unchecked")
                Class<? extends Quantity> qClass = (Class<? extends Quantity>) e.getValue();
                List<MeasurementUnit> us = MeasurementUnit.list(qClass);
                units.setItems(us);
                units.setValue(us.get(0));
            }
        });
        if(label != null) {
            setLabel(label);
        }
        setValue(Count.defaultUnit);
    }

    @Override
    protected MeasurementUnit generateModelValue() {
        return units.getValue();
    }

    @Override
    protected void setPresentationValue(MeasurementUnit mu) {
        if(mu == null) {
            mu = Count.defaultUnit;
        }
        Class<?> qc = mu.getQuantityClass();
        if(classes.getValue() != qc) {
            classes.setValue(qc);
        }
        MeasurementUnit u = units.getValue();
        if(u != mu) {
            units.setValue(mu);
        }
    }

    @Override
    public void setValue(MeasurementUnit value) {
        if(value == null) {
            value = Count.defaultUnit;
        }
        super.setValue(value);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        classes.setReadOnly(readOnly);
        units.setReadOnly(readOnly);
    }
}
