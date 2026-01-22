package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.StoredObject;
import com.storedobject.core.SystemUser;

import java.util.List;
import java.util.function.Predicate;

/**
 * Field to select a normal user. Unless a list or iterator is provided, it will use the default filter criteria to
 * filter out system users, auditors, external users, and locked users.
 *
 * @author Syam
 */
public class UserField extends ObjectField<SystemUser> {

    /**
     * Default constructor for creating a new instance of UserField.
     * Acts as shorthand for initializing the UserField with a default label.
     * The label is set to "Username" if no specific label is provided.
     */
    public UserField() {
        this((String)null);
    }

    /**
     * Constructs a UserField with a specified type.
     *
     * @param type The type of the field to be created. This determines the nature of the user field, such as whether it is
     *             a choice field, a search field, or another type from the predefined {@code Type} enumeration.
     */
    public UserField(Type type) {
        this(null, type);
    }

    /**
     * Constructor for the UserField class, used to create a labeled field for user input.
     *
     * @param label The label for the field. If null, the default label "Username" will be used.
     */
    public UserField(String label) {
        super(label == null ? "Username" : label, SystemUser.class);
        setup(true);
    }

    /**
     * Constructs a new {@code UserField} with the specified label and type.
     * If the label is {@code null}, a default label of "Username" is used.
     * Additionally, sets up the field with specific filters for validity.
     *
     * @param label The label for the field. If {@code null}, defaults to "Username".
     * @param type The desired type of the field. Determines the behavior and constraints of the field.
     */
    public UserField(String label, Type type) {
        super(label == null ? "Username" : label, SystemUser.class, type);
        setup(true);
    }

    /**
     * Constructs a UserField object with a specified list of SystemUser objects.
     *
     * @param list a list of SystemUser instances to be associated with the UserField object
     */
    public UserField(List<SystemUser> list) {
        this(null, list);
    }

    /**
     * Constructs a UserField object with the given list of SystemUser objects.
     *
     * @param list an iterable collection of SystemUser objects to initialize the UserField.
     */
    public UserField(Iterable<SystemUser> list) {
        this(null, list);
    }

    /**
     * Constructs a `UserField` instance with the specified label and a list of `SystemUser` objects.
     *
     * @param label Label associated with the field.
     * @param list An iterable collection of `SystemUser` objects that will be used to create the field's list.
     */
    public UserField(String label, Iterable<SystemUser> list) {
        this(label, list(list));
    }

    /**
     * Constructs a UserField with a specified label and a list of valid SystemUser instances.
     *
     * @param label Label for the field. If null, the default label "Username" will be used.
     * @param list List of SystemUser objects that will be allowed for selection in this field.
     */
    public UserField(String label, List<SystemUser> list) {
        super(label == null ? "Username" : label, SystemUser.class, list, false);
        setup(false);
    }

    /**
     * Constructs a UserField instance with the specified input field.
     *
     * @param field The input field of type {@code ObjectInput<SystemUser>} to define the behavior of the UserField.
     */
    public UserField(ObjectInput<SystemUser> field) {
        this(null, field);
    }

    /**
     * Constructs a {@code UserField} with the specified label and field.
     * If the label is {@code null}, a default label of "Username" is used.
     *
     * @param label The label to display for the field.
     *               If {@code null}, "Username" is used as the default value.
     * @param field An {@link ObjectInput} instance for handling input of {@link SystemUser} objects.
     */
    public UserField(String label, ObjectInput<SystemUser> field) {
        super(label == null ? "Username" : label, field);
        setup(false);
    }

    /**
     * Retrieves a UserField associated with the given master object for handling linked entities.
     *
     * @param master the master StoredObject to associate the UserField with; must not be null.
     * @return a UserField instance configured for linked entity handling.
     */
    public static UserField forLinks(StoredObject master) {
        return forLinks(null, master);
    }

    /**
     * Creates a UserField using the specified label and linked SystemUser objects from the provided master object.
     *
     * @param label the label to be used for the UserField.
     * @param master the StoredObject instance whose linked SystemUser objects are used to populate the UserField.
     * @return a new instance of UserField initialized with the provided label and linked SystemUser objects.
     */
    public static UserField forLinks(String label, StoredObject master) {
        return forLinks(label, master, null);
    }

    /**
     * Creates a UserField instance configured to handle linked entities associated with the specified master object.
     * The SystemUser instances are filtered based on the provided predicate.
     *
     * @param master the master StoredObject from which linked entities of type SystemUser are retrieved; must not be null
     * @param filter a predicate to filter the SystemUser instances; if null, no filtering is applied
     * @return a UserField instance populated with the filtered list of linked SystemUser objects
     */
    public static UserField forLinks(StoredObject master, Predicate<SystemUser> filter) {
        return forLinks(null, master, filter);
    }

    /**
     * Creates a {@code UserField} instance populated with linked {@code SystemUser} objects
     * from the provided master object, filtered by the specified {@code Predicate}.
     *
     * @param label the label to be used for the {@code UserField}.
     * @param master the {@code StoredObject} instance whose linked {@code SystemUser} objects
     *               are used to populate the {@code UserField}; must not be null.
     * @param filter a {@code Predicate} to filter the {@code SystemUser} objects. If the filter
     *               is {@code null}, all linked {@code SystemUser} objects are included.
     * @return a new {@code UserField} instance initialized with the specified label,
     *         linked {@code SystemUser} objects, and filter.
     */
    public static UserField forLinks(String label, StoredObject master, Predicate<SystemUser> filter) {
        if(filter == null) filter = u -> true;
        List<SystemUser> list = master.listLinks(SystemUser.class, SystemUser.userFilter() + " AND Id<>"
                        + master.getId())
                .filter(filter).toList();
        return new UserField(label, list);
    }

    /**
     * Creates a UserField instance for handling master-related operations.
     *
     * @param link the StoredObject instance representing the association or link needed for the master operation
     * @return a UserField instance configured for master-related functionality
     */
    public static UserField forMasters(StoredObject link) {
        return forMasters(null, link);
    }

    /**
     * Creates a UserField instance for masters based on the specified label and stored object link.
     *
     * @param label the label to be displayed for the UserField.
     * @param link a reference to the stored object used to retrieve the list of master SystemUsers.
     * @return a UserField instance populated with the list of master SystemUsers.
     */
    public static UserField forMasters(String label, StoredObject link) {
        return forMasters(label, link, null);
    }

    /**
     * Creates a UserField instance configured for master-related functionality.
     * This method initializes the UserField with a stored object link and filters SystemUsers
     * based on the provided predicate.
     *
     * @param link the StoredObject instance representing the linked association for the master operation
     * @param filter a Predicate to filter the SystemUser instances; if null, all SystemUsers are included
     * @return a UserField instance populated with the filtered list of master SystemUsers
     */
    public static UserField forMasters(StoredObject link, Predicate<SystemUser> filter) {
        return forMasters(null, link, filter);
    }

    /**
     * Creates a UserField instance for handling master-related entities, using the specified label,
     * stored object link, and an optional filter for filtering SystemUser objects.
     *
     * @param label the label to be displayed for the UserField.
     * @param link the StoredObject instance used to retrieve the list of master SystemUsers.
     * @param filter a predicate to filter the list of SystemUser objects; if null, no filtering is applied.
     * @return a new UserField instance populated with the filtered list of master SystemUsers.
     */
    public static UserField forMasters(String label, StoredObject link, Predicate<SystemUser> filter) {
        if(filter == null) filter = u -> true;
        List<SystemUser> list = link.listMasters(SystemUser.class, SystemUser.userFilter() + " AND Id<>"
                        + link.getId())
                .filter(filter).toList();
        return new UserField(label, list);
    }

    private void setup(boolean filter) {
        setItemLabelGenerator(SystemUser::getName);
        var field = getField();
        if(field instanceof AbstractObjectField<SystemUser> of) {
            StringList columns = StringList.create("Person.FirstName as First Name", "Person.LastName as Last Name");
            of.setBrowseColumns(columns);
            of.setSearchColumns(columns);
        }
        if(filter) {
            setFilter(SystemUser.userFilter(), false);
        }
    }
}
