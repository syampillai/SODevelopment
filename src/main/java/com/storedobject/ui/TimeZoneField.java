package com.storedobject.ui;

import com.vaadin.flow.component.combobox.ComboBox;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TimeZoneField extends ComboBox<String> {

    private static final List<String> timeZones = new ArrayList<>(ZoneId.getAvailableZoneIds());
    static {
        timeZones.sort(String::compareToIgnoreCase);
    }

    public TimeZoneField() {
        this(null);
    }

    public TimeZoneField(String label) {
        super(label, timeZones);
        setValue("Etc/GMT");
    }

    public ZoneId getZoneId() {
        return ZoneId.of(getValue());
    }
}
