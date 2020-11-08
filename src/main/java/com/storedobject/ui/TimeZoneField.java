package com.storedobject.ui;

import com.vaadin.flow.component.combobox.ComboBox;

import java.time.ZoneId;
import java.util.ArrayList;

public class TimeZoneField extends ComboBox<String> {

    public TimeZoneField() {
        this(null);
    }

    public TimeZoneField(String label) {
        super();
        ArrayList<String> list = new ArrayList<>(ZoneId.getAvailableZoneIds());
        list.sort(String::compareToIgnoreCase);
        setItems(list);
        if(label != null) {
            setLabel(label);
        }
        setValue("GMT");
    }

    public ZoneId getZoneId() {
        return ZoneId.of(getValue());
    }
}
