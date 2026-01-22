package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Represents a log of actions performed by users on specific objects.
 *
 * @author Syam
 */
public class UserActionLog extends StoredObject {

    private Id objectId;
    private Id userActionId;
    private final Timestamp actedAt = DateUtility.now();

    /**
     * Constructor.
     */
    public UserActionLog() {
    }

    /**
     * Configures the column definitions for the associated entity by adding
     * mappings of property names to their database column types.
     *
     * @param columns the Columns object to which the column definitions
     *                are to be added
     */
    public static void columns(Columns columns) {
        columns.add("Object", "id");
        columns.add("UserAction", "id");
        columns.add("ActedAt", "timestamp");
    }

    /**
     * Adds index configurations to the specified {@code Indices} by including the columns "Object"
     * and "UserAction" in the index.
     *
     * @param indices An instance of {@code Indices} to which index configurations will be added.
     */
    public static void indices(Indices indices) {
        indices.add("Object, UserAction");
    }

    /**
     * Updates the object identifier while ensuring certain conditions are met.
     *
     * @param objectId The new identifier to set for the object.
     *                 Must be a valid Id and meet the restrictions imposed by the method.
     *                 If the object is currently loading or the new identifier is the same
     *                 as the existing one, the operation will proceed. Otherwise, an exception
     *                 is thrown.
     * @throws Set_Not_Allowed If the object is not in a valid state to allow the update.
     */
    public void setObject(Id objectId) {
        if (!loading() && !Id.equals(this.getObjectId(), objectId)) {
            throw new Set_Not_Allowed("Object");
        }
        this.objectId = objectId;
    }

    /**
     * Sets the object ID by wrapping the provided BigDecimal value into an {@link Id} instance.
     *
     * @param idValue The BigDecimal value representing the object ID to be set.
     */
    public void setObject(BigDecimal idValue) {
        setObject(new Id(idValue));
    }

    /**
     * Sets the object associated with this log entry. If the provided object is null,
     * the associated object ID is set to null. Otherwise, the object ID is set
     * to the ID of the provided object.
     *
     * @param object The {@code StoredObject} to associate with this log entry, or
     *               {@code null} to disassociate any previously associated object.
     */
    public void setObject(StoredObject object) {
        setObject(object == null ? null : object.getId());
    }

    /**
     * Retrieves the identifier of the referenced object associated with this instance.
     *
     * @return the identifier of the referenced object as an {@code Id}.
     */
    @SetNotAllowed
    @Column(style = "(any)", order = 100)
    public Id getObjectId() {
        return objectId;
    }

    /**
     * Retrieves the associated StoredObject instance related to the objectId field.
     *
     * @return The StoredObject instance corresponding to the objectId, or null if no such association exists.
     */
    public StoredObject getObject() {
        return getRelated(StoredObject.class, objectId, true);
    }

    /**
     * Sets the user action identifier for the current instance.
     * This method updates the user action ID only if the instance is not in a loading state and
     * the given user action ID differs from the current one. If the conditions are not met, an
     * exception is thrown to indicate the operation is not allowed.
     *
     * @param userActionId the identifier of the user action to be set
     * @throws Set_Not_Allowed if the user action ID cannot be updated due to invalid conditions
     */
    public void setUserAction(Id userActionId) {
        if (!loading() && !Id.equals(this.getUserActionId(), userActionId)) {
            throw new Set_Not_Allowed("User Action");
        }
        this.userActionId = userActionId;
    }

    /**
     * Sets the user action by creating and assigning an instance of Id
     * based on the provided id value.
     *
     * @param idValue the unique identifier value used to represent a user action, as a BigDecimal
     */
    public void setUserAction(BigDecimal idValue) {
        setUserAction(new Id(idValue));
    }

    /**
     * Sets the user action for the current object by extracting the ID from the provided UserAction instance.
     *
     * @param userAction An instance of UserAction. If null, the user action will be set to null. If not null, the ID of the provided UserAction will be used.
     */
    public void setUserAction(UserAction userAction) {
        setUserAction(userAction == null ? null : userAction.getId());
    }

    /**
     * Retrieves the unique identifier associated with the user's action.
     *
     * @return the ID representing the user's action as an instance of {@link Id}.
     */
    @SetNotAllowed
    @Column(order = 200)
    public Id getUserActionId() {
        return userActionId;
    }

    /**
     * Retrieves the UserAction associated with this UserActionLog.
     *
     * @return the UserAction object linked to this UserActionLog, based on the userActionId.
     */
    public UserAction getUserAction() {
        return getRelated(UserAction.class, userActionId);
    }

    /**
     * Updates the timestamp for when an action was performed.
     *
     * @param actedAt the Timestamp object representing the new "acted at" time.
     *                Cannot be set if the provided timestamp matches the existing one,
     *                unless the object is in a loading state.
     * @throws Set_Not_Allowed if the new timestamp is identical to the current timestamp
     *                          and the object is not in a loading state.
     */
    public void setActedAt(Timestamp actedAt) {
        if (!loading()) {
            if (actedAt.getTime() == this.actedAt.getTime() && actedAt.getNanos() == this.actedAt.getNanos()) return;
            throw new Set_Not_Allowed("Acted at");
        }
        this.actedAt.setTime(actedAt.getTime());
        this.actedAt.setNanos(actedAt.getNanos());
    }

    /**
     * Retrieves the timestamp indicating when the action was performed.
     *
     * @return a {@code Timestamp} object representing the date and time the action occurred.
     */
    @SetNotAllowed
    @Column(order = 300, readOnly = true)
    public Timestamp getActedAt() {
        return new Timestamp(actedAt.getTime());
    }

    /**
     * Validates the data of the current object and its properties.
     *
     * @param tm the transaction manager used to validate and check the data.
     * @throws Exception if validation fails due to invalid or missing values.
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        objectId = tm.checkTypeAny(this, objectId, StoredObject.class, false);
        userActionId = tm.checkType(this, userActionId, UserAction.class, false);
        if (Utility.isEmpty(actedAt)) {
            throw new Invalid_Value("Acted at");
        }
        super.validateData(tm);
    }

    /**
     * Saves the provided stored object with the specified action.
     *
     * @param object the stored object to be saved, which contains the data that needs to be persisted
     * @param action the action describing the type of save operation to be performed
     * @throws Exception if an error occurs during the save operation
     */
    public static void save(StoredObject object, String action) throws Exception {
        UserAction.save(object, action);
    }
}