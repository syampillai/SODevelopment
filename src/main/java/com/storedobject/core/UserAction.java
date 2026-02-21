package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

/**
 * Represents an action performed by a system user. This class maps the relationship
 * between a {@link SystemUser} and a {@link UIAction}.
 * <p>
 * Each instance of this class corresponds to a unique combination of a user and an
 * action, enforcing that a user can only perform an action once in the context
 * of a specific condition.
 *
 * @author Syam
 */
public class UserAction extends StoredObject {

    private Id systemUserId;
    private Id actionId;

    /**
     * Default constructor for the UserAction class.
     * This constructor creates an instance of UserAction with default attribute values.
     */
    public UserAction() {}

    public static void columns(Columns columns) {
        columns.add("SystemUser", "id");
        columns.add("Action", "id");
    }

    public static void indices(Indices indices) {
        indices.add("SystemUser, Action", true);
    }

    @Override
    public String getUniqueCondition() {
        return "SystemUser=" + getSystemUserId() + " AND " + "Action=" + getActionId();
    }

    /**
     * Sets the system user for this instance.
     * Throws an exception if the system user cannot be changed under the current conditions.
     *
     * @param userId The ID of the system user to be set.
     */
    public void setSystemUser(Id userId) {
        if (!loading() && !Id.equals(this.getSystemUserId(), userId)) {
            throw new Set_Not_Allowed("System User");
        }
        this.systemUserId = userId;
    }

    /**
     * Sets the system user for this UserAction instance using a BigDecimal value.
     *
     * @param idValue The BigDecimal representing the identifier of the system user.
     */
    public void setSystemUser(BigDecimal idValue) {
        setSystemUser(new Id(idValue));
    }

    /**
     * Sets the system user for this UserAction instance.
     * Converts the provided SystemUser object into its associated ID using the `getId` method
     * and assigns it to the corresponding field.
     *
     * @param user the SystemUser instance to set. If null, it clears the associated user ID.
     */
    public void setSystemUser(SystemUser user) {
        setSystemUser(user == null ? null : user.getId());
    }

    /**
     * Retrieves the unique identifier associated with the system user for this action.
     *
     * @return The unique identifier representing the system user.
     */
    @SetNotAllowed
    @Column(order = 100)
    public Id getSystemUserId() {
        return systemUserId;
    }

    /**
     * Retrieves the related SystemUser instance associated with this UserAction.
     * This method fetches the SystemUser object linked via the systemUserId field.
     *
     * @return The related SystemUser instance, or null if no association exists.
     */
    public SystemUser getSystemUser() {
        return getRelated(SystemUser.class, systemUserId);
    }

    /**
     * Sets the action for the UserAction instance.
     * This method assigns the provided action ID to the actionId field if the object
     * is not in a loading state and the given action ID differs from the current one.
     * If the set operation is not allowed, it throws a Set_Not_Allowed exception.
     *
     * @param actionId The ID of the action to be set. This must be a valid {@link Id} instance.
     *                 If the action ID cannot be changed due to current constraints, an exception is thrown.
     * @throws Set_Not_Allowed if attempting to set the action while not allowed.
     */
    public void setAction(Id actionId) {
        if (!loading() && !Id.equals(this.getActionId(), actionId)) {
            throw new Set_Not_Allowed("Action");
        }
        this.actionId = actionId;
    }

    /**
     * Sets the action associated with this object using the provided BigDecimal value.
     * This method internally creates an Id object from the given BigDecimal value and
     * updates the action.
     *
     * @param idValue The BigDecimal value representing the action's unique identifier.
     */
    public void setAction(BigDecimal idValue) {
        setAction(new Id(idValue));
    }

    /**
     * Sets the action for this instance using the provided {@link UIAction}.
     * If the action is null, the current action is cleared.
     *
     * @param action the {@link UIAction} instance to set; if null, the current action will be set to null
     */
    public void setAction(UIAction action) {
        setAction(action == null ? null : action.getId());
    }

    /**
     * Retrieves the unique identifier associated with the action.
     *
     * @return the Id representing the action's unique identifier
     */
    @SetNotAllowed
    @Column(order = 200)
    public Id getActionId() {
        return actionId;
    }

    /**
     * Retrieves the UIAction object associated with the current entity.
     *
     * @return the UIAction instance linked to this entity through the actionId, or null if no association exists.
     */
    public UIAction getAction() {
        return getRelated(UIAction.class, actionId);
    }

    /**
     * Validates the data for the current object by performing type checks and ensuring no duplicates.
     *
     * @param tm the TransactionManager instance used to perform type checks and validation.
     * @throws Exception if any data validation fails, including type mismatches or duplicate entries.
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        systemUserId = tm.checkType(this, systemUserId, SystemUser.class, false);
        actionId = tm.checkType(this, actionId, UIAction.class, false);
        checkForDuplicate("SystemUser", "Action");
        super.validateData(tm);
    }

    /**
     * Retrieves a UserAction based on the given SystemUser and action.
     *
     * @param user the SystemUser for whom the action is being retrieved
     * @param action the action identifier as a string
     * @return the UserAction corresponding to the specified user and action
     */
    public static UserAction get(SystemUser user, String action) {
        return get(user, UIAction.getForAction(action));
    }

    /**
     * Retrieves a UserAction object based on the specified SystemUser and UIAction.
     * If either the user or action is null, the method returns null.
     *
     * @param user the SystemUser whose associated action is to be retrieved
     * @param action the UIAction to be associated with the given SystemUser
     * @return the corresponding UserAction object if both user and action are valid; otherwise, null
     */
    public static UserAction get(SystemUser user, UIAction action) {
        return user == null || action == null ? null
                : get(UserAction.class, "SystemUser=" + user.getId() + " AND Action=" + action.getId());
    }

    /**
     * Retrieves a {@code UserAction} object associated with a specific stored object and action.
     * The method constructs the action identifier by combining the action prefix of the given
     * stored object with the specified action string, and then retrieves the corresponding {@code UserAction}.
     *
     * @param tm the {@code TransactionManager} used for managing the transaction
     *           during the retrieval operation
     * @param object the {@code StoredObject} whose action prefix is used to construct
     *               the combined action identifier
     * @param action the action string to append to the action prefix of the stored object
     * @return the {@code UserAction} corresponding to the constructed action identifier
     */
    public static UserAction get(TransactionManager tm, StoredObject object, String action) {
        return get(tm, ClassAttribute.get(object).getActionPrefix() + "-" + action);
    }

    /**
     * Retrieves a UserAction object associated with a given action. If the action does not exist,
     * it will be created, and associated UserAction will be initialized and saved.
     *
     * @param tm the TransactionManager instance used for transaction operations and user retrieval
     * @param action the action code or description to find or create the associated UserAction
     * @return the UserAction associated with the given action, or null if the save operation fails
     */
    public static UserAction get(TransactionManager tm, String action) {
        action = toCode(action);
        UIAction a = UIAction.getFor(action);
        if(a == null) {
            tm.actionAllowed(action); // This will create the action if not exists.
            a = UIAction.getFor(action);
        }
        UserAction ua = get(tm.getUser(), a);
        if(ua == null) {
            ua = new UserAction();
            ua.setSystemUser(tm.getUser());
            ua.setAction(a);
            try {
                if(tm.transact(ua::save) != 0) {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return ua;
    }

    /**
     * Retrieves the system user who is the actor associated with a specific action for a stored object.
     *
     * @param so the stored object for which the actor is to be retrieved
     * @param action the action identifier used to determine the associated actor
     * @return the system user associated with the specified action for the given stored object, or null if no actor is identified
     */
    public static SystemUser getActor(StoredObject so, String action) {
        UIAction a = UIAction.getForAction(action);
        if(a == null) return null;
        return so.listLinks(UserAction.class, "Action=" + a.getId()).map(UserAction::getSystemUser).findFirst();
    }

    /**
     * Saves a user action as a link.
     *
     * @param object the {@link StoredObject} to be linked with the user action
     * @param action the action identifier used to retrieve or create the associated {@link UserAction}
     * @throws Exception if an error occurs during the transaction, retrieval, or linking process
     */
    public static void save(StoredObject object, String action) throws Exception {
        Transaction t = object.getTransaction();
        UserAction ua = UserAction.get(t.getManager(), object, action);
        boolean found = false;
        for(UserAction a: object.listLinks(UserAction.class, "Action=" + ua.getActionId())) {
            if(ua.getId().equals(a.getId())) {
                found = true;
                continue;
            }
            object.removeLink(t, a);
        }
        if(!found) object.addLink(t, ua);
        UserActionLog log = new UserActionLog();
        log.setObject(object);
        log.setUserAction(ua);
        log.save(t);
    }

    /**
     * Note: for internal use only.
     * <p></p>
     * Saves a user action and associates it with a stored object and a transaction.
     * This method ensures that a {@code UserAction} is created (if it doesn't already exist)
     * for the given system user and action. It then links the specified stored object
     * with the user action and logs the interaction by saving a {@code UserActionLog}.
     *
     * @param t       The {@code Transaction} instance used to manage database interactions.
     * @param object  The {@code StoredObject} to be linked with the user action.
     * @param action  The {@code UIAction} representing the action to be associated with the user.
     * @throws Exception If an error occurs during the process, such as database issues or validation failures.
     */
    static void save(Transaction t, StoredObject object, UIAction action) throws Exception {
        UserAction ua = get(UserAction.class, "Action=" + action.getId() + " AND SystemUser=" + t.getManager().getUser().getId());
        if(ua == null) {
            ua = new UserAction();
            ua.setAction(action);
            ua.setSystemUser(t.getManager().getUser());
            ua.save(t);
        }
        object.addLink(t, ua);
        UserActionLog log = new UserActionLog();
        log.setObject(object);
        log.setUserAction(ua);
        log.save(t);
    }
}
