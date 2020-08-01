package com.storedobject.ui.inventory;

import com.storedobject.core.InventoryLocation;
import com.storedobject.ui.ObjectComboField;

import java.util.ArrayList;
import java.util.List;

public class LocationField extends ObjectComboField<InventoryLocation> {

    public LocationField(int... types) {
        this(null, types);
    }

    public LocationField(String label, int... types) {
        super(label, locations(types));
    }

    private static List<InventoryLocation> locations(int... types) {
        return new ArrayList<>();
    }
}
