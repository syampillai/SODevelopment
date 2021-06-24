package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;

/**
 * Utility class. For internal use only.
 *
 * @author Syam
 */
class ParameterParser {

    static Class<? extends InventoryItemType> itemTypeClass(String parameters) {
        return itemTypeClass(parameters, InventoryItemType.class);
    }

    static Class<? extends InventoryItemType> itemTypeClass(int skip, String parameters) {
        return itemTypeClass(skip, parameters, InventoryItemType.class);
    }

    static Class<? extends InventoryItemType> itemTypeClass(String parameters,
                                                            Class<? extends InventoryItemType> defaultClass) {
        return itemTypeClass(0, parameters, defaultClass);
    }

    static Class<? extends InventoryItemType> itemTypeClass(int skip, String parameters,
                                                            Class<? extends InventoryItemType> defaultClass) {
        if(parameters == null) {
            return defaultClass;
        }
        String[] ps = parameters.split("\\|");
        for(String p: ps) {
            p = p.trim();
            if(!isClass(p)) {
                continue;
            }
            try {
                //noinspection unchecked
                Class<? extends InventoryItemType> c = (Class<? extends InventoryItemType>) JavaClassLoader.getLogic(p);
                if(skip <= 0) {
                    return c;
                }
                --skip;
            } catch(Throwable e) {
                throw new SORuntimeException("Unable to determine item type from '" + parameters + "'");
            }
        }
        return defaultClass;
    }

    static Class<? extends InventoryItem> itemClass(String parameters) {
        return itemClass(parameters, InventoryItem.class);
    }

    static Class<? extends InventoryItem> itemClass(String parameters, Class<? extends InventoryItem> defaultClass) {
        if(parameters == null) {
            return defaultClass;
        }
        String[] ps = parameters.split("\\|");
        for(String p: ps) {
            p = p.trim();
            if(!isClass(p)) {
                continue;
            }
            try {
                //noinspection unchecked
                return (Class<? extends InventoryItem>) JavaClassLoader.getLogic(p);
            } catch(Throwable e) {
                throw new SORuntimeException("Unable to determine item class from '" + parameters + "'");
            }
        }
        return defaultClass;
    }

    static InventoryStore store(String parameters) {
        if(parameters == null) {
            return null;
        }
        String[] ps = parameters.split("\\|");
        for(String p: ps) {
            p = p.trim();
            if(isClass(p) || StringUtility.isDigit(p)) {
                continue;
            }
            InventoryStore store = LocationField.getStore(p);
            if(store == null) {
                throw new SORuntimeException("Unable to determine store from '" + parameters + "'");
            }
            return store;
        }
        return null;
    }

    static InventoryLocation location(String parameters, int... types) {
        return location(parameters, true, types);
    }

    static InventoryLocation location(String parameters, boolean allowEmptyName, int... types) {
        return location(0, parameters, allowEmptyName, types);
    }

    static InventoryLocation location(int skip, String parameters, boolean allowEmptyName, int... types) {
        String name = name(skip, parameters);
        InventoryLocation location = LocationField.getLocation(name, allowEmptyName, types);
        if(location == null) {
            if(allowEmptyName && (name == null || name.isEmpty())) {
                return null;
            }
            throw new SORuntimeException("Unable to determine location from '" + parameters + "'");
        }
        return location;
    }

    static int number(String parameters) {
        if(parameters == null) {
            return Integer.MIN_VALUE;
        }
        String[] ps = parameters.split("\\|");
        for(String p: ps) {
            p = p.trim();
            if(!StringUtility.isDigit(p)) {
                continue;
            }
            return Integer.parseInt(p);
        }
        return Integer.MIN_VALUE;
    }

    static String name(String parameters) {
        return name(0, parameters);
    }

    static String name(int skip, String parameters) {
        if(parameters == null) {
            return null;
        }
        if(!parameters.contains("|")) {
            return skip < 1 ? parameters.replace('_', ' ').trim() : null;
        }
        String[] ps = parameters.split("\\|");
        for(String p: ps) {
            p = p.trim();
            if(StringUtility.isDigit(p) || isClass(p)) {
                continue;
            }
            --skip;
            if(skip >= 0) {
                continue;
            }
            return p.replace('_', ' ').trim();
        }
        return null;
    }

    static boolean isClass(String parameter) {
        return StringUtility.getCharCount(parameter, '.') > 1 && !parameter.contains(" ");
    }
}
