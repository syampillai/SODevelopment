package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.ObjectComboField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Location field for accepting inventory locations.
 *
 * @author Syam
 */
public class LocationField extends ObjectComboField<InventoryLocation> {

    private static final String NO_LOC = "Unable to determine location";
    private final List<InventoryLocation> locations;

    /**
     * Constructor.
     *
     * @param types Types of locations to be allowed. See {@link InventoryLocation#getType()}.
     */
    public LocationField(int... types) {
        this(null, types);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param types Types of locations to be allowed. See {@link InventoryLocation#getType()}.
     */
    public LocationField(String label, int... types) {
        this(label, locations(types));
    }

    public LocationField(List<InventoryLocation> list) {
        this(null, list);
    }

    /**
     * Constructor.
     *
     * @param label Label.
     * @param list List of allowed locations.
     */
    public LocationField(String label, List<InventoryLocation> list) {
        super(label, InventoryLocation.class, list);
        locations = list;
        if(!list.isEmpty()) {
            setValue(list.get(0));
        }
    }

    /**
     * Create a location field.
     *
     * @param types Types of locations to be allowed. See {@link InventoryLocation#getType()}.
     */
    public static LocationField create(int... types) {
        return create(null, types);
    }

    /**
     * Create a location field.
     *
     * @param label Label.
     * @param types Types of locations to be allowed. See {@link InventoryLocation#getType()}.
     */
    public static LocationField create(String label, int... types) {
        try {
            LocationField locationField = new LocationField(label, types);
            if(locationField.getLocations().isEmpty()) {
                throw new Exception();
            }
            return locationField;
        } catch(Throwable error) {
            StringBuilder m = new StringBuilder("Unable to determine stock locations. Check your configuration of ");
            if(types.length == 0) {
                m.append("stores.");
            } else {
                for(int i = 0; i < types.length; i++) {
                    if(i > 0) {
                        m.append(", ");
                    }
                    m.append('"');
                    m.append(InventoryLocation.getTypeValue(types[i]));
                    m.append('"');
                }
                if(types.length > 1) {
                    m.append(" etc.");
                } else {
                    m.append('.');
                }
            }
            throw new SORuntimeException(m.toString());
        }
    }

    /**
     * Create a location field for a given location.
     *
     * @param label Label.
     * @param locationName Name of the location.
     * @param types Types of locations to be allowed. See {@link InventoryLocation#getType()}.
     */
    public static LocationField create(String label, String locationName, int... types) {
        return create(label, getLocation(locationName,false, types));
    }

    /**
     * Create a location field for a given location.
     *
     * @param location Location.
     */
    public static LocationField create(InventoryLocation location) {
        return create(null, location);
    }

    /**
     * Create a location field for a given location.
     *
     * @param label Label.
     * @param location Location.
     */
    public static LocationField create(String label, InventoryLocation location) {
        List<InventoryLocation> locationList = new ArrayList<>();
        locationList.add(location);
        return new LocationField(label, locationList);
    }

    private static List<InventoryLocation> locations(int... types) {
        ArrayList<InventoryLocation> locations = new ArrayList<>();
        StringBuilder stypes = new StringBuilder("Type IN (");
        boolean includeZero = false, includeCustodians = false;
        if(types == null || types.length == 0) {
            includeZero = true;
            includeCustodians = true;
            stypes.append("10,14");
        } else if(types.length == 1 && types[0] == 0) {
            includeZero = true;
            stypes = null;
        } else if(types.length == 1 && types[0] == 18) {
            includeCustodians = true;
            stypes = null;
        } else {
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
        if(includeCustodians) {
            StoredObject.list(InventoryCustodyLocation.class).forEach(locations::add);
        }
        if(stypes == null) {
            return locations;
        }
        stypes.append(')');
        StoredObject.list(InventoryVirtualLocation.class, stypes.toString()).forEach(locations::add);
        return locations;
    }

    /**
     * Get the list of locations allowed in this field instance.
     *
     * @return List of allowed locations.
     */
    public List<InventoryLocation> getLocations() {
        return Collections.unmodifiableList(locations);
    }

    /**
     * Get the number of locations allowed in this field.
     *
     * @return Location count.
     */
    public int getLocationCount() {
        return locations.size();
    }

    /**
     * Remove a location from the list of allowed locations.
     *
     * @param location Location to be removed.
     * @return Self reference.
     */
    public LocationField remove(InventoryLocation location) {
        if(location == null) {
            return this;
        }
        boolean sameValue = location.equals(getValue());
        getObjectLoader().deleted(location);
        if(sameValue) {
            setValue(locations.isEmpty() ? null : locations.get(0));
        }
        return this;
    }

    /**
     * Get the store for the given store name.
     *
     * @param storeName Name of the store.
     * @return Store or null if store can't be identified from the name.
     */
    public static InventoryStore getStore(String storeName) {
        if(storeName == null) {
            return null;
        }
        storeName = storeName.trim().replace('_', ' ');
        int p = storeName.indexOf('|');
        if(p >= 0) {
            storeName = storeName.substring(0, p).trim();
        }
        String nam = storeName;
        if(nam.isEmpty()) {
            return null;
        }
        LocationField f = new LocationField(0);
        return f.getLocations().stream().filter(loc -> loc instanceof InventoryStoreBin).
                map(loc -> ((InventoryStoreBin)loc).getStore()).
                filter(s -> s.getName().equalsIgnoreCase(nam)).
                findAny().orElse(null);
    }

    /**
     * Get the location for the given location name. A run-time exception is raised if the location can't be found.
     *
     * @param locationName Name of the location.
     * @return Location or null if empty location name is passed.
     */
    public static InventoryLocation getLocation(String locationName, int... types) {
        return getLocation(locationName, true, types);
    }

    /**
     * Get the location for the given location name. A run-time exception is raised if the location can't be found.
     *
     * @param locationName Name of the location.
     * @param allowEmptyName If true, null will be returned if the location name passed is null or empty.
     * @return Location or null if empty location name is passed and the parameter allowEmptyName is true.
     */
    public static InventoryLocation getLocation(String locationName, boolean allowEmptyName, int... types) {
        return location(allowEmptyName, locationName, types);
    }

    private static InventoryLocation location(boolean allowEmptyName, String locationName, int... types) {
        locationName = ParameterParser.name(locationName);
        if(locationName == null) {
            if(allowEmptyName) {
                return null;
            }
            throw new SORuntimeException(NO_LOC);
        }
        String nam = locationName;
        if(nam.isEmpty()) {
            if(allowEmptyName) {
                return null;
            }
            throw new SORuntimeException(NO_LOC);
        }
        LocationField f = new LocationField(types);
        InventoryLocation location = f.getLocations().stream().
                filter(loc -> loc.getName().equalsIgnoreCase(nam)).
                findAny().orElse(null);
        if(location == null) {
            throw new SORuntimeException(NO_LOC + " - " + locationName);
        }
        return location;
    }
}
