package com.storedobject.ui;

import com.storedobject.core.Utility;
import com.vaadin.flow.component.combobox.ComboBox;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TimeZoneField extends ComboBox<String> {

    private static final List<String> timeZones = new ArrayList<>(ZoneId.getAvailableZoneIds());
    static {
        timeZones.sort((a, b) -> Utility.nameGMT(a).compareToIgnoreCase(Utility.nameGMT(b)));
    }

    public TimeZoneField() {
        this(null);
    }

    public TimeZoneField(String label) {
        super(label, timeZones);
        setValue("Etc/GMT");
        setItemLabelGenerator(name -> Utility.nameGMT(name) + "  " + name);
    }

    public ZoneId getZoneId() {
        return ZoneId.of(getValue());
    }
}
