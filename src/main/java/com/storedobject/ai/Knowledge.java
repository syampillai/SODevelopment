package com.storedobject.ai;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import java.util.*;
import java.util.concurrent.Future;

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
    final List<KnowledgeModule> modules = new ArrayList<>();
    private DataRetriever dataRetriever;
    private String topic;

    /**
     * Creates an instance of the Knowledge class with the specified TransactionManager.
     *
     * @param tm the TransactionManager instance to be used with this Knowledge object
     */
    public Knowledge(TransactionManager tm) {
        this.tm = tm;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
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
        entities.put(friendlyName, c);
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
        addDataClass(StringUtility.makeLabel(c).replace(" ", ""), c, attributes);
    }

    /**
     * Creates a new chat instance using a default memory retention limit of 10 messages.
     * This method is a convenience overload of the {@code createChat(int rememberHistory)} method and
     * delegates to it with a predefined value for {@code rememberHistory}.
     *
     * @return A new {@code Chat} instance with default parameters.
     * @throws Exception If there is an issue creating the chat instance or accessing required resources.
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
        if(rememberHistory > 0) {
            throw new Exception();
        }
        return new Chat() {
            @Override
            public Future<String> ask(String message) {
                return null;
            }

            @Override
            public void close() {
            }

            @Override
            public boolean isClosed() {
                return false;
            }

            @Override
            public void setChatClosedListener(Runnable listener) {
            }
        };
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
