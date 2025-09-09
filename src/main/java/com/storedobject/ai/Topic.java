package com.storedobject.ai;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

import java.lang.reflect.Modifier;

/**
 * Represents a specific topic characterized by its name and associated logic.
 * This class extends the functionality of the {@link Name} class and provides additional behavior
 * for managing and validating knowledge logic associated with the topic.
 * It is immutable and provides static utility methods for working with topic-related data.
 *
 * @author Syam
 */
public final class Topic extends Name {

    private String logic;
    private Class<? extends KnowledgeLogic> knowledgeClass;

    /**
     * Constructs a new, empty instance of the Topic class.
     * This default constructor is used to create an uninitialized object of Topic.
     */
    public Topic() {}

    /**
     * Adds required column definitions for the `Topic` class to the given {@code Columns} object.
     *
     * @param columns the {@code Columns} object to which the column definitions will be added
     */
    public static void columns(Columns columns) {
        columns.add("Logic", "text");
    }

    /**
     * Retrieves an array of column definitions for the topic entity.
     *
     * @return An array of strings representing the column definitions,
     *         mapping database column names to user-friendly names.
     *         For example, "Name AS Topic" and "Logic AS Knowledge Logic".
     */
    public static String[] browseColumns() {
        return new String[] {"Name AS Topic", "Logic AS Knowledge Logic"};
    }

    /**
     * Retrieves a {@code Topic} instance by its name.
     *
     * @param name the name of the topic to retrieve
     * @return the {@code Topic} instance with the specified name, or {@code null} if not found
     */
    public static Topic get(String name) {
        return StoredObjectUtility.get(Topic.class, "Name", name);
    }

    /**
     * Retrieves an iterator of {@code Topic} objects that match the specified name.
     *
     * @param name the name of the topics to be listed
     * @return an iterator of {@code Topic} objects that match the specified name
     */
    public static ObjectIterator<Topic> list(String name) {
        return StoredObjectUtility.list(Topic.class, "Name", name);
    }

    /**
     * Retrieves the name of the topic.
     *
     * @return The name of the topic as a String.
     */
    public String getTopicName() {
        return name;
    }

    /**
     * Sets the logic associated with the topic.
     *
     * @param logic the logic to be associated with this topic; typically represents a class name
     *              or identifier related to knowledge logic functionality.
     */
    public void setLogic(String logic) {
        this.logic = logic;
    }

    /**
     * Retrieves the logic associated with this topic.
     *
     * @return the logic as a String, representing the associated knowledge logic for this topic.
     */
    @Column(order = 200, caption = "Knowledge Logic")
    public String getLogic() {
        return logic;
    }

    /**
     * Validates the data integrity and ensures all required properties of the {@code Topic} are properly set.
     * If the associated knowledge logic class cannot be resolved, an exception is thrown.
     *
     * @param tm the {@code TransactionManager} instance used for handling transactional operations
     * @throws Exception if the {@code KnowledgeLogic} class is not set or valid
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (getKnowledgeLogicClass() == null) {
            throw new Invalid_Value("Knowledge Logic");
        }
        super.validateData(tm);
    }

    /**
     * Returns the {@code Class} object representing the type of {@link KnowledgeLogic} associated with the topic.
     * This method attempts to resolve the class based on the logic string stored in the topic.
     * If the class has already been resolved and cached, it will return the cached value.
     * Otherwise, it will dynamically resolve the class using a class loader.
     * If the resolved class is abstract or an error occurs during class resolution, it returns null.
     *
     * @return the {@code Class} object representing the {@link KnowledgeLogic} type, or null if the logic class is abstract
     *         or cannot be resolved.
     */
    public <KL extends KnowledgeLogic> Class<KL> getKnowledgeLogicClass() {
        if (this.knowledgeClass != null) {
            //noinspection unchecked
            return (Class<KL>) this.knowledgeClass;
        } else {
            try {
                @SuppressWarnings("unchecked") Class<KL> kc = (Class<KL>) JavaClassLoader.getLogic(this.logic);
                if (Modifier.isAbstract(kc.getModifiers())) {
                    return null;
                } else {
                    this.knowledgeClass = kc;
                    return kc;
                }
            } catch (Throwable var2) {
                return null;
            }
        }
    }

    /**
     * Customizes the metadata of a UI field.
     * Updates the caption of the metadata if the field name matches specific criteria.
     *
     * @param md the metadata of the UI field to be customized
     */
    public static void customizeMetadata(UIFieldMetadata md) {
        if (md.getFieldName().equals("Name")) {
            md.setCaption("Topic");
        }
    }
}
