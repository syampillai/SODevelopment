package com.storedobject.ui.util;

import com.storedobject.core.ExtraInfoDefinition;
import com.storedobject.core.StoredObject;

public class ExtraInfo<T extends StoredObject> {

    ExtraInfoField<T> field;
    final Class<T> infoClass;
    StoredObject master;
    private final int displayOrder;

    public ExtraInfo(ExtraInfoDefinition infoDefinition) {
        //noinspection unchecked
        this.infoClass = (Class<T>) infoDefinition.getExtraInfoClass();
        displayOrder = infoDefinition.getDisplayOrder();
    }

    public void setMaster(StoredObject master) {
        this.master = master;
        field.setValue(new ExtraInfoValue<>(this));
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public ExtraInfoValue<T> getValue() {
        return field.getValue();
    }

    public String getLabel() {
        return getName();
    }

    public static String getName() {
        return "Extra Info";
    }
}
