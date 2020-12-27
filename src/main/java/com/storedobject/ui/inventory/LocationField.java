package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.InventoryLocation;
import com.storedobject.core.InventoryStoreBin;
import com.storedobject.core.InventoryVirtualLocation;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.util.ObjectListProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocationField extends ObjectComboField<InventoryLocation> {

    private final List<InventoryLocation> locations;

    public LocationField(int... types) {
        this(null, types);
    }

    public LocationField(String label, int... types) {
        this(label, locations(types));
    }

    public LocationField(List<InventoryLocation> list) {
        this(null, list);
    }

    public LocationField(String label, List<InventoryLocation> list) {
        super(label, InventoryLocation.class, list);
        locations = list;
        if(!list.isEmpty()) {
            setValue(list.get(0));
        }
    }

    public static LocationField create(int... types) {
        return create(null, types);
    }

    public static LocationField create(String label, int... types) {
        try {
            LocationField locationField = new LocationField(label, types);
            if(locationField.getLocations().isEmpty()) {
                throw new Exception();
            }
            return locationField;
        } catch(Throwable error) {
            throw new SORuntimeException("Unable to determine stock locations. Have you configured any stores yet?");
        }
    }

    private static List<InventoryLocation> locations(int... types) {
        ArrayList<InventoryLocation> locations = new ArrayList<>();
        StringBuilder stypes = new StringBuilder("Type IN (");
        boolean includeZero;
        if(types == null || types.length == 0) {
            includeZero = true;
            stypes.append("10,14");
        } else if(types.length == 1 && types[0] == 0) {
            includeZero = true;
            stypes = null;
        } else {
            includeZero = false;
            int len = stypes.length();
            for(int t: types) {
                if(!includeZero) {
                    includeZero = t == 0;
                }
                if(t != 0) {
                    if(stypes.length() > len) {
                        stypes.append(',');
                    }
                    stypes.append(t);
                }
            }
        }
        if(includeZero) {
            StoredObject.list(InventoryStoreBin.class).forEach(locations::add);
        }
        if(stypes == null) {
            return locations;
        }
        stypes.append(')');
        StoredObject.list(InventoryVirtualLocation.class, stypes.toString()).forEach(locations::add);
        return locations;
    }

    public List<InventoryLocation> getLocations() {
        return Collections.unmodifiableList(locations);
    }

    public void remove(InventoryLocation location) {
        //noinspection unchecked
        ((ObjectListProvider<InventoryLocation, ?>)getDataProvider()).delete(location);
    }
}
