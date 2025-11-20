package com.storedobject.core;

import com.storedobject.core.annotation.*;

/**
 * A class representing a user interface action. This class extends {@code StoredObject}
 * and is designed to manage actions within a UI system. It provides functionality
 * to define actions, manage descriptions, and handle persistence via columns and indices.
 * Actions are uniquely identified by their {@code action} property, and utility
 * methods are provided for retrieving and listing actions.
 <p></p>
 * This class enforces restrictions on the {@code action} property to ensure valid data and
 * prevent duplicates.
 *
 * @author Syam
 */
public final class UIAction extends StoredObject {

    private String action = "";
    private String description;

    /**
     * Default constructor for the UIAction class.
     */
    public UIAction() {
    }

    public static void columns(Columns columns) {
        columns.add("Action", "text");
        columns.add("Description", "text");
    }

    public static void indices(Indices indices) {
        indices.add("Action", true);
    }

    public String getUniqueCondition() {
        return "Action='" + action.replace("'", "''") + "'";
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    /**
     * Sets the action for the current object. This method assigns a new value to the
     * action property, but it ensures that the operation is only performed when loading
     * is allowed. If loading is not allowed, it throws a Set_Not_Allowed exception.
     *
     * @param action the new action to be set for the object
     * @throws Set_Not_Allowed if the action setting is not allowed due to the loading state
     */
    public void setAction(String action) {
        if (!loading()) {
            throw new Set_Not_Allowed("Action");
        }
        this.action = action;
    }

    /**
     * Retrieves the action value associated with this UIAction instance.
     *
     * @return The action value as a String.
     */
    @SetNotAllowed
    @Column(style = "(code)", order = 100)
    public String getAction() {
        return action;
    }

    /**
     * Sets the description for this UIAction.
     *
     * @param description the description to be assigned to this UIAction
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the description associated with this UIAction.
     *
     * @return The description of the action, or null if not set.
     */
    @Column(required = false, order = 200)
    public String getDescription() {
        return description;
    }

    /**
     * Validates the data for the current UIAction instance.
     * Performs checks on the 'action' field to ensure it is not empty or invalid,
     * converts it to its code representation, and verifies there are no duplicate entries.
     *
     * @param tm The {@code TransactionManager} instance used to handle the transaction context.
     * @throws Exception If validation fails due to invalid or duplicate data.
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(action)) {
            throw new Invalid_Value("Action");
        }
        action = toCode(action);
        checkForDuplicate("Action");
        super.validateData(tm);
    }

    /**
     * Provides a string representation of the UIAction object by returning its action field.
     *
     * @return the action field as a string representation of the object
     */
    @Override
    public String toString() {
        return action;
    }

    /**
     * Constructs a displayable string representation of the UIAction, combining the action and description fields.
     *
     * @return A string combining the action and description separated by a space.
     */
    @Override
    public String toDisplay() {
        return action + " " + description;
    }

    /**
     * Returns a UIAction corresponding to the specified action string.
     *
     * @param action the action identifier as a String
     * @return the corresponding UIAction for the given action string
     */
    public static UIAction getForAction(String action) {
        return getFor(toCode(action));
    }

    /**
     * Retrieves the UIAction corresponding to the specified action name.
     *
     * @param action the name of the action as a String for which the corresponding UIAction is to be retrieved
     * @return the UIAction corresponding to the specified action name
     */
    static UIAction getFor(String action) {
        return get(UIAction.class, "Action='" + action + "'");
    }

    /**
     * Retrieves a UIAction based on the given action name.
     *
     * @param action the name of the action to retrieve
     * @return a UIAction object that matches the given action name, or null if no match is found
     */
    public static UIAction get(String action) {
        action = toCode(action);
        UIAction a = getFor(action);
        if(a != null) {
            return a;
        }
        return list(UIAction.class, "Action LIKE '" + action + "%'").single(false);
    }

    /**
     * Retrieves an iterator of UIAction objects whose 'Action' property matches the specified action code pattern.
     *
     * @param action the action code pattern to filter the results. This pattern is transformed into a format suitable for querying.
     * @return an ObjectIterator of UIAction objects that match the specified action pattern.
     */
    public static ObjectIterator<UIAction> list(String action) {
        action = toCode(action);
        return list(UIAction.class, "Action LIKE '" + action + "%'");
    }
}
