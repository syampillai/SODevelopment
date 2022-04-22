package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;

import java.util.ArrayList;
import java.util.List;

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
            if(StringUtility.getCharCount(p, '.') == 1) {
                p = ApplicationServer.getPackageName() + "." + p;
            }
            if(!isClass(p)) {
                continue;
            }
            try {
                Class<?> c;
                if("*".equals(p)) {
                    c = InventoryItemType.class;
                } else {
                    c = JavaClassLoader.getLogic(p);
                }
                if(!InventoryItemType.class.isAssignableFrom(c)) {
                    continue;
                }
                if(skip <= 0) {
                    //noinspection unchecked
                    return (Class<? extends InventoryItemType>) c;
                }
                --skip;
            } catch(Throwable e) {
                throw new SORuntimeException("Unable to determine item type from '" + parameters + "'");
            }
        }
        return defaultClass;
    }

    static <T extends InventoryItemType> Class<? extends T>[] itemTypeClasses(Class<T> baseClass, int skip,
                                                                              String parameters) {
        List<Class<? extends T>> classes = new ArrayList<>();
        Class<? extends InventoryItemType> c;
        while(true) {
            c = itemTypeClass(skip, parameters, null);
            if(c == null) {
                break;
            }
            if(baseClass.isAssignableFrom(c)) {
                //noinspection unchecked
                classes.add((Class<? extends T>) c);
            }
            ++skip;
        }
        @SuppressWarnings("unchecked") Class<? extends T>[] classArray = new Class[0];
        return classes.toArray(classArray);
    }

    static Class<? extends InventoryItem> itemClass(String parameters) {
        return itemClass(parameters, InventoryItem.class);
    }

    static Class<? extends InventoryItem> itemClass(int skip, String parameters) {
        return itemClass(skip, parameters, InventoryItem.class);
    }

    static Class<? extends InventoryItem> itemClass(String parameters, Class<? extends InventoryItem> defaultClass) {
        return itemClass(0, parameters, defaultClass);
    }

    static Class<? extends InventoryItem> itemClass(int skip, String parameters,
                                                            Class<? extends InventoryItem> defaultClass) {
        if(parameters == null) {
            return defaultClass;
        }
        String[] ps = parameters.split("\\|");
        for(String p: ps) {
            p = p.trim();
            if(StringUtility.getCharCount(p, '.') == 1) {
                p = ApplicationServer.getPackageName() + "." + p;
            }
            if(!isClass(p)) {
                continue;
            }
            try {
                Class<?> c;
                if("*".equals(p)) {
                    c = InventoryItem.class;
                } else {
                    c = JavaClassLoader.getLogic(p);
                }
                if(!InventoryItem.class.isAssignableFrom(c)) {
                    continue;
                }
                if(skip <= 0) {
                    //noinspection unchecked
                    return (Class<? extends InventoryItem>) c;
                }
                --skip;
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
        return "*".equals(parameter) || (StringUtility.getCharCount(parameter, '.') > 1 && !parameter.contains(" "));
    }
}
