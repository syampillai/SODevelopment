package com.storedobject.ai;

import com.storedobject.common.StringList;
import com.storedobject.core.*;

import java.math.BigInteger;
import java.util.*;

/**
 * The Knowledge class serves as a repository to manage entities and related attributes within the scope of a transactional context.
 * It also provides tools and utility methods for managing stored objects, entities, and their associated attributes.
 *
 * @author Syam
 */
public class Knowledge implements DataRetriever {

    private final TransactionManager tm;
    final Map<String, Class<? extends StoredObject>> entities = new HashMap<>();
    final Map<Class<? extends StoredObject>, StringList> attributes = new HashMap<>();
    final Collection<Object> modules = new ArrayList<>();
    private DataRetriever dataRetriever;
    private Chat chat;
    private boolean logging = false;

    /**
     * Creates an instance of the Knowledge class using the specified Device.
     * The TransactionManager instance required by the Knowledge class is retrieved
     * from the provided Device's server.
     *
     * @param device the Device instance from which the associated TransactionManager
     *               is retrieved to initialize the Knowledge object
     */
    public Knowledge(Device device) {
        this(device.getServer().getTransactionManager());
    }

    /**
     * Creates an instance of the Knowledge class with the specified TransactionManager.
     *
     * @param tm the TransactionManager instance to be used with this Knowledge object
     */
    public Knowledge(TransactionManager tm) {
        this.tm = tm;
    }

    public void setDataRetriever(DataRetriever dataRetriever) {
        this.dataRetriever = dataRetriever;
    }

    /**
     * Retrieves a list of attributes associated with the specified class of type {@code StoredObject}.
     * The attributes are determined based on user roles and access permissions.
     *
     * @param c The class of type {@code StoredObject} for which attributes are to be retrieved.
     * @return A {@code StringList} containing the attributes of the specified class, or {@code null}
     *         if access is restricted.
     */
    public StringList getAttributes(Class<? extends StoredObject> c) {
        StringList attributes = this.attributes.get(c);
        if(attributes != null) {
            return attributes;
        }
        SystemUser su = tm.getUser();
        if(su.isAdmin() || su.isAppAdmin() || entities.containsValue(c)) {
            return StringList.create(ClassAttribute.get(c).getAttributes());
        }
        return null;
    }

    /**
     * Retrieves the TransactionManager instance associated with this Knowledge object.
     *
     * @return The {@code TransactionManager} instance used by this Knowledge object.
     */
    public final TransactionManager getTransactionManager() {
        return tm;
    }

    /**
     * Adds one or more knowledge modules to the knowledge. Modules provide additional
     * functionality or capabilities to enhance the knowledge interaction.
     *
     * @param modules The knowledge modules to be added. Each module can be any non-null object implementing {@link KnowledgeModule}.
     *              If any module is null, it is ignored.
     */
    public final void addModules(KnowledgeModule... modules) {
        if(modules != null) {
            for(KnowledgeModule module : modules) {
                if(module != null) {
                    this.modules.add(module);
                }
            }
        }
    }

    /**
     * Adds a data class to the knowledge base with an optional list of attributes.
     * The data class is associated with a friendly name for easier identification.
     * Optionally, attributes can be specified to define additional metadata.
     *
     * @param friendlyName The user-friendly name to associate with the data class.
     * @param c The class type that extends {@code StoredObject} to add to the knowledge base.
     * @param attributes Optional attributes to associate with the data class.
     */
    public final void addDataClass(String friendlyName, Class<? extends StoredObject> c, String... attributes) {
        entities.put(friendlyName.toLowerCase(), c);
        if(attributes != null) {
            StringList s = StringList.create(attributes);
            if(!s.isEmpty()) {
                this.attributes.put(c, s);
            }
        }
    }

    /**
     * Adds a data class to the internal configuration using the specified class type and optional attributes.
     * This method automatically generates a friendly name for the class by converting its name to a label format
     * without spaces.
     *
     * @param c         The class type to be added, extending {@link StoredObject}.
     * @param attributes Optional attributes associated with the class, represented as an array of strings.
     */
    public final void addDataClass(Class<? extends StoredObject> c, String... attributes) {
        addDataClass(StringUtility.makeLabel(c).toLowerCase(), c, attributes);
    }

    /**
     * Creates a new chat instance using a default memory retention limit of 10 messages.
     * This method is a convenience overload of the {@code createChat(int rememberHistory)} method and
     * delegates to it with a predefined value for {@code rememberHistory}.
     *
     * @return A new {@code Chat} instance with default parameters.
     * @throws Exception If there is an issue, creating the chat instance or accessing required resources.
     */
    public final Chat createChat() throws Exception {
        return createChat(10);
    }

    /**
     * Creates a new chat instance with a specified memory retention limit for recent chat messages.
     * This method delegates the creation of the chat instance to the AI associated with the current
     * session of the TransactionManager.
     *
     * @param rememberHistory The number of recent chat messages to retain in memory.
     *                         Values below 1 default to 10, and values above 100 are capped at 100.
     * @return A new {@code Chat} instance configured with the current knowledge and memory parameters.
     * @throws Exception If an error occurs during the creation of the chat instance.
     */
    public final Chat createChat(int rememberHistory) throws Exception {
        if(chat != null && !chat.isClosed()) {
            throw new Exception("Chat already exists");
        }
        return chat;
    }

    /**
     * Logs the specified object if logging is enabled in the current configuration.
     * This method uses the associated TransactionManager's logging mechanism.
     *
     * @param anything the object to be logged. Can represent any information or data
     *                 the caller wishes to record in the log.
     */
    public void log(Object anything) {
        if(logging) {
            tm.log(anything);
        }
    }

    /**
     * Sets the logging state for the Knowledge object.
     *
     * @param logging a boolean value indicating whether logging should be enabled (true) or disabled (false)
     */
    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    /**
     * Indicates whether logging is currently enabled for the Knowledge instance.
     *
     * @return {@code true} if logging is enabled, {@code false} otherwise.
     */
    public boolean isLogging() {
        return logging;
    }

    /**
     * Retrieves an {@code Id} object based on the provided string representation of the identifier.
     * If the string is invalid, logs an error in the given {@code JSONMap} and returns {@code null}.
     *
     * @param map      The {@code JSONMap} instance to store error details in case of an invalid ID.
     * @param idString The string representation of the identifier to be converted into an {@code Id}.
     * @return An {@code Id} object constructed from the provided string, or {@code null} if the string is invalid.
     */
    public final Id getId(JSONMap map, String idString) {
        Id id;
        try {
            id = new Id(new BigInteger(idString));
        } catch (Throwable ignored) {
            map.put("error", "Invalid ID - " + idString);
            id = null;
        }
        return id;
    }

    /**
     * Retrieves the class of a specified entity by its name.
     * If the entity name is invalid or not found in the available entities,
     * an error message is added to the provided JSONMap and {@code null} is returned.
     *
     * @param <T> the type of the class that extends StoredObject
     * @param map the JSONMap used to store error messages if the entity name is invalid
     * @param entityName the name of the entity for which the class is to be retrieved
     * @return the class of the specified entity if found; otherwise, {@code null}
     */
    public final <T extends StoredObject> Class<T> getClass(JSONMap map, String entityName) {
        if(entityName == null || entityName.isEmpty()) {
            map.put("error", "Invalid entity name - " + entityName);
            return null;
        }
        @SuppressWarnings("unchecked") Class<T> c = (Class<T>) entities.get(entityName);
        if(c == null) {
            map.put("error", "Invalid entity name - " + entityName);
            return null;
        }
        return c;
    }

    /**
     * Saves a collection of {@code StoredObject} instances to a specified {@code JSONMap} under
     * the given name. Each object in the collection is individually processed and saved.
     * Errors during the save process for individual objects are ignored.
     *
     * @param map     The {@code JSONMap} in which the objects are to be saved.
     * @param name    The name of the key under which the array of objects will be saved in the map.
     * @param objects An {@code ObjectIterator} containing the objects to be saved. Each object
     *                must extend {@code StoredObject}.
     * @param <T>     The type of the {@code StoredObject}.
     */
    public final  <T extends StoredObject> void save(JSONMap map, String name, ObjectIterator<T> objects) {
        JSONMap.Array a = map.array(name);
        for(T o: objects) {
            try {
                o.save(a.map(), null, false, true, this::getAttributes);
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Saves the provided object using the specified parameters and updates the given JSONMap.
     * If the object is null, the JSONMap will report an error with the provided name.
     *
     * @param map    the {@code JSONMap} where the save operation results or errors will be recorded.
     * @param name   the name or identifier used to associate with the saved object or error message.
     * @param object the {@code StoredObject} to be saved. If null, an error message is added to the map.
     * @param <T>    the type of the object extending {@code StoredObject} to be saved.
     */
    public final <T extends StoredObject> void save(JSONMap map, String name, T object) {
        if(object == null) {
            map.put("error", name + " not found");
        } else {
            try {
                object.save(map, name, false, true, this::getAttributes);
            } catch (Throwable ignored) {
            }
        }
    }

    private DataRetriever dr() {
        if(dataRetriever == null) {
            dataRetriever = new DataRetriever() {};
        }
        return dataRetriever;
    }

    @Override
    public <T extends StoredObject> ObjectIterator<T> list(Class<T> c) {
        return dr().list(c);
    }

    @Override
    public <T extends StoredObject> int count(Class<T> c) {
        return dr().count(c);
    }

    @Override
    public <T extends StoredObject> T get(Class<T> c, String purpose) {
        return dr().get(c, purpose);
    }
}
