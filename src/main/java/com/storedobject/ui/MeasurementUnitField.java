package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.ComboField;
import com.storedobject.vaadin.CustomField;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A field for inputting {@link MeasurementUnit}s.
 *
 * @author Syam
 */
public class MeasurementUnitField extends CustomField<MeasurementUnit> {

    private final static List<Class<?>> classList = Quantity.types().collect(Collectors.toList());
    private final static List<Class<?>> qClassList = Quantity.quantityTypes().collect(Collectors.toList());
    private final ComboField<Class<?>> classes;
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
        this(label, true);
    }

    /**
     * Constructor.
     *
     * @param all Whether all types should be allowed or not. If <code>false</code> is passed,
     *            units corresponding to only real quantities are allowed.
     */
    public MeasurementUnitField(boolean all) {
        this(null, all);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param all Whether all types should be allowed or not. If <code>false</code> is passed,
     *            units corresponding to only real quantities are allowed.
     */
    public MeasurementUnitField(String label, boolean all) {
        super(null);
        classes = new ComboField<>(all ? classList : qClassList);
        classes.setItemLabelGenerator(c -> c == Distance.class ? "Length/Distance" : StringUtility.makeLabel(c));
        units.setWidth("7em");
        add(new ButtonLayout(classes, units));
        classes.addValueChangeListener(e -> {
            @SuppressWarnings("unchecked")
            Class<? extends Quantity> qClass = (Class<? extends Quantity>) e.getValue();
            List<MeasurementUnit> us = MeasurementUnit.list(qClass);
            if(us == null) {
                classes.focus();
            } else {
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
