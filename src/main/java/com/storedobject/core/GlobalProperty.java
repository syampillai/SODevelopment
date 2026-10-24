package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a globally stored key-value property associated with a specific system entity.
 * This class allows properties to be retrieved, updated, and cached for efficient access.
 * It also includes functionality for storing metadata such as description and supports
 * custom constraints through unique conditions and validations.
 *
 * @author Syam
 */
public final class GlobalProperty extends StoredObject {

    private static final Map<String, Map<Id, String>> cache = new HashMap<>();
    private Id systemEntityId = Id.ZERO;
    private String name;
    private String value;
    private String description;

    public GlobalProperty() {
    }

    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
        columns.add("Name", "text");
        columns.add("Value", "text");
        columns.add("Description", "text");
    }

    public static void indices(Indices indices) {
        indices.add("Name,SystemEntity", true);
    }

    public String getUniqueCondition() {
        return "SystemEntity="
                + getSystemEntityId()
                + " AND Name='"
                + name
                + "'";
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setSystemEntity(Id systemEntityId) {
        this.systemEntityId = systemEntityId;
    }

    public void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    public void setSystemEntity(SystemEntity systemEntity) {
        setSystemEntity(systemEntity == null ? null : systemEntity.getId());
    }

    @Column(required = false, order = 100)
    public Id getSystemEntityId() {
        return systemEntityId;
    }

    public SystemEntity getSystemEntity() {
        return get(SystemEntity.class, systemEntityId);
    }

    public void setName(String name) {
        this.name = name == null ? "" : name.trim();
    }

    @Column(order = 200, style = "(code)")
    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(style = "(large)", order = 300)
    public String getValue() {
        return value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(style = "(large)", order = 400)
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        systemEntityId = tm.checkType(this, systemEntityId, SystemEntity.class, true);
        if (StringUtility.isWhite(name) || name.contains("'")) {
            throw new Invalid_Value("Name");
        }
        name = toCode(name);
        if (StringUtility.isWhite(value)) {
            throw new Invalid_Value("Value");
        }
        if (StringUtility.isWhite(description)) {
            throw new Invalid_Value("Description");
        }
        super.validateData(tm);
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        cache.remove(name);
    }

    private static long toLong(String value) {
        try {
            return Long.parseLong(value);
        } catch(Throwable ignored) {
        }
        return 0L;
    }

    private static int toInteger(String value) {
        return (int) toLong(value);
    }

    /**
     * Retrieves the value corresponding to the specified name from the given SystemEntity.
     * If no value is found, a default empty string is returned.
     *
     * @param systemEntity the SystemEntity containing the data
     * @param name the key for which the value needs to be retrieved
     * @return the value associated with the specified name, or an empty string if not found
     */
    public static String get(SystemEntity systemEntity, String name) {
        return get(systemEntity, name, "");
    }

    /**
     * Retrieves a value based on the provided system entity, name, and default value.
     *
     * @param systemEntity the system entity from which the value will be fetched; if null, a default ID is used
     * @param name the name of the property or key to retrieve
     * @param defaultValue the value to return if the specified name does not exist or cannot be found
     * @return the retrieved value, or the default value if the specified name cannot be found
     */
    public static String get(SystemEntity systemEntity, String name, String defaultValue) {
        return get(systemEntity == null ? Id.ZERO : systemEntity.getId(), name, defaultValue);
    }

    /**
     * Retrieves a value based on the provided system entity ID and name.
     *
     * @param systemEntityId the ID of the system entity used to locate the value
     * @param name the name associated with the value to retrieve
     * @return the retrieved value as a string
     */
    public static String get(Id systemEntityId, String name) {
        return get(systemEntityId, name, "");
    }

    /**
     * Retrieves a value associated with a given system entity ID and name from the cache or other sources.
     * If the value is not found, it returns a default value and updates the cache.
     *
     * @param systemEntityId the ID of the system entity for which the value is retrieved; can be {@code null}, in which case a default ID is used
     * @param name the name of the property to retrieve; must not be {@code null} or contain invalid characters such as an apostrophe
     * @param defaultValue the default value to return if no value is found; if {@code null}, it defaults to an empty string
     * @return the retrieved value if found, or the provided default value if no value exists
     */
    public static String get(Id systemEntityId, String name, String defaultValue) {
        if(defaultValue == null) {
            defaultValue = "";
        }
        String originalName = name;
        if(name == null || name.contains("'")) {
            return defaultValue;
        }
        name = toCode(name);
        if(systemEntityId == null) {
            systemEntityId = Id.ZERO;
        }
        Map<Id, String> vMap = cache.get(name);
        if(vMap == null) {
            Map<Id, String> vReadMap = new HashMap<>();
            list(GlobalProperty.class, "Name='" + name + "'")
                    .forEach(gp -> vReadMap.put(gp.systemEntityId, gp.value));
            cache.put(name, vReadMap);
            vMap = vReadMap;
        }
        String v = vMap.get(systemEntityId);
        if(v == null) {
            if(systemEntityId.equals(Id.ZERO)) {
                if(vMap.size() == 1) {
                    v = vMap.values().stream().findAny().orElse(null);
                }
            } else {
                v = vMap.get(Id.ZERO);
            }
        }
        if(v == null) {
            v = ApplicationServer.getGlobalProperty(originalName, false);
        }
        if(v == null) {
            v = defaultValue;
            vMap.put(systemEntityId, defaultValue);
        }
        return v;
    }

    /**
     * Retrieves the value associated with the specified name. If no value is found,
     * it defaults to an empty string.
     *
     * @param name the name whose associated value is to be returned
     * @return the value associated with the specified name, or an empty string if no value is found
     */
    public static String get(String name) {
        return get(name, "");
    }

    /**
     * Retrieves the value of a global property associated with the given name and system entity ID.
     * If no value is found, the specified default value is returned.
     *
     * @param name          the name of the global property to retrieve
     * @param defaultValue  the default value to return if no property value is found
     * @return              the value of the global property for the specified name; if no value is found, the provided default value is returned
     */
    public static String get(String name, String defaultValue) {
        return get(Id.ZERO, name, defaultValue);
    }

    /**
     * Retrieves a value using the specified TransactionManager, name, and an optional default value.
     *
     * @param tm The TransactionManager instance used to fetch the value.
     * @param name The name or key associated with the value.
     * @return The value associated with the provided name, or an empty string if no value is found.
     */
    public static String get(TransactionManager tm, String name) {
        return get(tm, name, "");
    }

    /**
     * Retrieves a value based on the provided transaction manager, name, and default value.
     *
     * @param tm the transaction manager used to retrieve the entity; may be null
     * @param name the name of the value to retrieve
     * @param defaultValue the value to return if no value is found for the specified name
     * @return the retrieved value if found, otherwise the provided default value
     */
    public static String get(TransactionManager tm, String name, String defaultValue) {
        return get(tm == null ? null : tm.getEntity(), name, defaultValue);
    }

    /**
     * Retrieves the boolean value associated with the specified name from the given SystemEntity.
     * If the value is not found, a default value of {@code false} is returned.
     *
     * @param systemEntity the SystemEntity object from which the boolean value is retrieved
     * @param name the name of the property for which the boolean value is to be retrieved
     * @return the boolean value associated with the specified name, or {@code false} if not found
     */
    public static boolean getBoolean(SystemEntity systemEntity, String name) {
        return getBoolean(systemEntity, name, false);
    }

    /**
     * Retrieves a boolean value based on the provided system entity, parameter name,
     * and default value. If the system entity is null, a default identifier is used.
     *
     * @param systemEntity the system entity containing the context for the parameter lookup; may be null
     * @param name the name of the parameter to be retrieved
     * @param defaultValue the default boolean value to be returned if the parameter is not found
     * @return the boolean value associated with the given parameter name, or the default value if not found
     */
    public static boolean getBoolean(SystemEntity systemEntity, String name, boolean defaultValue) {
        return getBoolean(systemEntity == null ? Id.ZERO : systemEntity.getId(), name, defaultValue);
    }

    /**
     * Retrieves a boolean value based on the provided system entity ID and name.
     *
     * @param systemEntityId the identifier of the system entity for which the boolean value is to be retrieved
     * @param name the name associated with the boolean value to be retrieved
     * @return the boolean value associated with the specified system entity ID and name, or a default value if not found
     */
    public static boolean getBoolean(Id systemEntityId, String name) {
        return getBoolean(systemEntityId, name, false);
    }

    /**
     * Retrieves a boolean property value for the specified system entity and property name.
     * If the property value is not found, the specified default value is returned.
     *
     * @param systemEntityId The ID of the system entity for which the property value is being retrieved.
     * @param name The name of the property to retrieve.
     * @param defaultValue The default value to return if the property is not found or cannot be converted to a boolean.
     * @return The boolean value of the property, or the specified default value if the property does not exist
     *         or cannot be converted to a boolean.
     */
    public static boolean getBoolean(Id systemEntityId, String name, boolean defaultValue) {
        return ApplicationServer.toBoolean(get(systemEntityId, name, defaultValue ? "1" : "0"));
    }

    /**
     * Retrieves the boolean value of a configuration property based on its name.
     *
     * @param name the name of the configuration property to be retrieved
     * @return the boolean value of the specified configuration property; returns false if the property is not found or is set to "0"
     */
    public static boolean getBoolean(String name) {
        return ApplicationServer.toBoolean(get(name, "0"));
    }

    /**
     * Retrieves a boolean value associated with the given name. If the value is not found,
     * the default value is returned.
     *
     * @param name         the name of the property to retrieve
     * @param defaultValue the default boolean value to return if the property is not found
     * @return the boolean value associated with the given name, or the provided default value if not found
     */
    public static boolean getBoolean(String name, boolean defaultValue) {
        return ApplicationServer.toBoolean(get(name, defaultValue ? "1" : "0"));
    }

    /**
     * Retrieves a boolean property by its name from a transaction-managed context.
     *
     * @param tm The TransactionManager instance to use for retrieving the property.
     * @param name The name of the property to retrieve.
     * @return The boolean value of the specified property, or {@code false} if it is not found or invalid.
     */
    public static boolean getBoolean(TransactionManager tm, String name) {
        return getBoolean(tm, name, false);
    }

    /**
     * Retrieves a boolean value associated with a given property name, using a fallback default value if the property is not defined.
     *
     * @param tm The transaction manager to use for accessing the property value.
     * @param name The name of the property.
     * @param defaultValue The default boolean value to return if the property is not found.
     * @return The boolean value associated with the property name, or the defaultValue if the property is not defined.
     */
    public static boolean getBoolean(TransactionManager tm, String name, boolean defaultValue) {
        return ApplicationServer.toBoolean(get(tm, name, defaultValue ? "1" : "0"));
    }

    /**
     * Retrieves an integer value associated with a specific property name and system entity.
     * If the provided system entity is null, the default system entity ID is used.
     *
     * @param systemEntity The system entity from which to retrieve the property value. If null, a default ID is used.
     * @param name The name of the property whose value should be retrieved.
     * @return The integer value of the specified property. If the property does not exist, a default value of 0 is returned.
     */
    public static int getInteger(SystemEntity systemEntity, String name) {
        return getInteger(systemEntity == null ? Id.ZERO : systemEntity.getId(), name, 0);
    }

    /**
     * Retrieves an integer value associated with the specified name from the given SystemEntity.
     * If the value is not found or cannot be converted to an integer, the provided default value is returned.
     *
     * @param systemEntity the SystemEntity instance from which the property is being retrieved
     * @param name the name of the property to retrieve
     * @param defaultValue the default value to return if the property is not found or cannot be converted to an integer
     * @return the integer value of the property, or the default value if it is not found or invalid
     */
    public static int getInteger(SystemEntity systemEntity, String name, int defaultValue) {
        return toInteger(get(systemEntity, name, Integer.toString(defaultValue)));
    }

    /**
     * Retrieves an integer property value associated with the specified system entity ID and name.
     * If the property does not exist, a default value of 0 is returned.
     *
     * @param systemEntityId the ID of the system entity to retrieve the property from
     * @param name the name of the property
     * @return the integer value of the specified property, or 0 if the property is not found
     */
    public static int getInteger(Id systemEntityId, String name) {
        return getInteger(systemEntityId, name, 0);
    }

    /**
     * Retrieves the integer value of a property identified by the given system entity ID and name.
     * If the property is not found, returns the specified default value.
     *
     * @param systemEntityId The ID of the system entity associated with the property.
     * @param name The name of the property to retrieve.
     * @param defaultValue The default integer value to return if the property does not exist.
     * @return The integer value of the property or the default value if the property is not found.
     */
    public static int getInteger(Id systemEntityId, String name, int defaultValue) {
        return toInteger(get(systemEntityId, name, Integer.toString(defaultValue)));
    }

    /**
     * Retrieves the integer value associated with the specified property name.
     * If the property is not found, a default value of 0 is returned.
     *
     * @param name the name of the property whose integer value is to be retrieved
     * @return the integer value of the specified property, or 0 if the property is not found
     */
    public static int getInteger(String name) {
        return toInteger(get(name, "0"));
    }

    /**
     *
     */
    public static int getInteger(String name, int defaultValue) {
        return toInteger(get(name, Integer.toString(defaultValue)));
    }

    /**
     * Retrieves an integer value associated with a given name using the provided TransactionManager.
     * If no value is found, a default value of 0 is returned.
     *
     * @param tm   the TransactionManager instance to use for the retrieval
     * @param name the name of the property to retrieve
     * @return the integer value associated with the specified name, or 0 if not found
     */
    public static int getInteger(TransactionManager tm, String name) {
        return getInteger(tm, name, 0);
    }

    /**
     * Retrieves the integer value associated with the specified name in the given transaction manager.
     * If the value is not found, the specified default value is returned.
     *
     * @param tm the transaction manager to fetch the value from
     * @param name the name of the property whose value is to be retrieved
     * @param defaultValue the default integer value to return if the property is not found
     * @return the integer value of the property, or the default value if the property does not exist
     */
    public static int getInteger(TransactionManager tm, String name, int defaultValue) {
        return toInteger(get(tm, name, Integer.toString(defaultValue)));
    }

    /**
     * Retrieves the long value associated with a specified name and system entity.
     * If no specific value is found, a default value of 0 is returned.
     *
     * @param systemEntity the system entity to which the property is associated
     * @param name the name of the property whose value is to be retrieved
     * @return the long value associated with the specified name, or 0 if not found
     */
    public static long getLong(SystemEntity systemEntity, String name) {
        return getLong(systemEntity, name, 0);
    }

    /**
     * Retrieves a long value associated with the specified system entity ID and property name.
     * If the value is not set, the method will return the default value of 0.
     *
     * @param systemEntityId the unique identifier of the system entity
     * @param name the name of the property
     * @return the long value associated with the specified system entity ID and property name,
     *         or 0 if the value is not set
     */
    public static long getLong(Id systemEntityId, String name) {
        return getLong(systemEntityId, name, 0);
    }

    /**
     * Retrieves the long value associated with the specified name.
     * If the value cannot be found or is not a valid long, a default value of 0 is returned.
     *
     * @param name the name of the property or key to retrieve the long value for
     * @return the long value associated with the specified name, or 0 if not found or invalid
     */
    public static long getLong(String name) {
        return getLong(name, 0);
    }

    /**
     * Retrieves the long value of a specific property identified by its name within the given transaction context.
     * If the property cannot be found, a default value of 0 is returned.
     *
     * @param tm the TransactionManager instance, providing the transaction context for the property retrieval
     * @param name the name of the property whose long value is to be retrieved
     * @return the long value of the specified property, or 0 if the property is not found
     */
    public static long getLong(TransactionManager tm, String name) {
        return getLong(tm, name, 0);
    }

    /**
     * Retrieves a long value associated with a given name from the specified system entity. If the value is not found or invalid,
     * returns the provided default value.
     *
     * @param systemEntity the system entity from which to retrieve the value
     * @param name the name of the key for which the value is to be retrieved
     * @param defaultValue the default value to return if the key is not found or cannot be converted to a long
     * @return the long value associated with the specified name, or the default value if not found or invalid
     */
    public static long getLong(SystemEntity systemEntity, String name, long defaultValue) {
        return toLong(get(systemEntity, name, Long.toString(defaultValue)));
    }

    /**
     * Retrieves the value associated with the specified name and system entity ID as a long.
     * If the value does not exist or cannot be converted to a long, the specified default value is returned.
     *
     * @param systemEntityId the identifier of the system entity from which the value is retrieved
     * @param name the name of the parameter to retrieve
     * @param defaultValue the default value to return if the value does not exist or cannot be converted to a long
     * @return the value associated with the specified name as a long, or the default value if not found or invalid
     */
    public static long getLong(Id systemEntityId, String name, long defaultValue) {
        return toLong(get(systemEntityId, name, Long.toString(defaultValue)));
    }

    /**
     * Retrieves the long value associated with the specified name. If the value
     * cannot be found or converted to a long, the provided default value is returned.
     *
     * @param name the name of the property to retrieve
     * @param defaultValue the default value to return if the property cannot be found or converted
     * @return the long value associated with the specified name, or the default value if not found or invalid
     */
    public static long getLong(String name, long defaultValue) {
        return toLong(get(name, Long.toString(defaultValue)));
    }

    /**
     * Retrieves a long value associated with the specified name from the given TransactionManager.
     * If the value is not found or cannot be converted to a long, the provided default value is returned.
     *
     * @param tm           the TransactionManager instance to retrieve the value from
     * @param name         the name of the key whose associated value is to be retrieved
     * @param defaultValue the default value to return if the key is not found or cannot be converted
     * @return the long value associated with the specified name, or the default value if not found or invalid
     */
    public static long getLong(TransactionManager tm, String name, long defaultValue) {
        return toLong(get(tm, name, Long.toString(defaultValue)));
    }
}
