package com.storedobject.core;

import com.storedobject.core.annotation.*;

import java.util.List;

/**
 * Represents an API token object used for managing and validating access tokens
 * associated with different purposes and clients in the system.
 * The class extends {@link StoredObject}, allowing it to be persisted and
 * manipulated within the system's data model.
 *
 * @author Syam
 */
public class APIToken extends StoredObject {

    private String purpose;
    private String clientID;
    private String projectID;
    private String token;

    /**
     * Default constructor for the APIToken class.
     * Initializes a new instance of the APIToken object.
     * This constructor does not set any fields or properties and is
     * primarily intended for creating an empty token object.
     */
    public APIToken() {}

    /**
     * Defines the columns for the APIToken class.
     *
     * @param columns the column object used to define and add the required columns for the APIToken class.
     */
    public static void columns(Columns columns) {
        columns.add("Purpose", "text");
        columns.add("ClientID", "text");
        columns.add("ProjectID", "text");
        columns.add("Token", "text");
    }

    /**
     * Adds an index to the specified {@code Indices} collection. The index is based on the column list
     * "lower(Purpose)".
     *
     * @param indices the {@code Indices} collection to which the index will be added
     */
    public static void indices(Indices indices) {
        indices.add("lower(Purpose)");
    }

    /**
     * Provides the hint value by combining multiple hint constants that indicate certain
     * characteristics of the object. Combines ObjectHint.SMALL and ObjectHint.SMALL_LIST.
     *
     * @return An integer representing the bitwise combination of the hint constants
     *         ObjectHint.SMALL and ObjectHint.SMALL_LIST.
     */
    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    /**
     * Retrieves the link definitions associated with this class.
     * Each definition indicates the relationships or connections
     * between this object and other entities or groups.
     *
     * @return An array of strings, where each string represents a link definition
     *         in the format: "EntityName|FullyQualifiedClassName|||AttributesDescriptor".
     */
    public static String[] links() {
        return new String[] {
                "Users|com.storedobject.core.SystemUser|||0",
                "Groups|com.storedobject.core.SystemUserGroup|||0",
        };
    }

    /**
     * Sets the purpose of the API token.
     *
     * @param purpose The purpose to associate with the API token.
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * Retrieves the purpose associated with the API token.
     *
     * @return The purpose as a {@code String}.
     */
    @Column(style = "(code)", order = 100)
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the client ID associated with this API token.
     *
     * @param clientID The client identifier to be set.
     */
    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    /**
     * Retrieves the client ID associated with this instance, if applicable.
     *
     * @return The client ID as a {@code String}, or empty if not applicable.
     */
    @Column(required = false, caption = "Client ID (If applicable)", order = 200)
    public String getClientID() {
        return clientID;
    }

    /**
     * Sets the token for the API.
     *
     * @param token the token to be set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Retrieves the project ID associated with this instance, if applicable.
     *
     * @return The project ID as a {@code String}, or empty if not applicable.
     */
    @Column(required = false, order = 300, caption = "Project ID (If applicable)")
    public String getProjectID() {
        return projectID;
    }

    /**
     * Sets the project ID associated with this API token instance.
     *
     * @param projectID The project identifier to be set.
     */
    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    /**
     * Retrieves the token associated with this API token instance.
     *
     * @return A string representing the token.
     */
    @Column(order = 400)
    public String getToken() {
        return token;
    }

    /**
     * Validates the data for the current instance of APIToken.
     * Ensures that the required fields have appropriate non-empty values
     * and perform any necessary validation logic.
     *
     * @param tm The transaction manager used to handle the validation process.
     * @throws Invalid_Value If the value of the "purpose" or "token" field is empty or invalid.
     * @throws Exception If an error occurs during the validation process.
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if (StringUtility.isWhite(purpose)) {
            throw new Invalid_Value("Purpose");
        }
        purpose = toCode(purpose);
        if (StringUtility.isWhite(token)) {
            throw new Invalid_Value("Token");
        }
        super.validateData(tm);
    }

    /**
     * Checks if the given user has access to this API token.
     *
     * @param user The user to check for access.
     * @return True if the user has access, false otherwise.
     */
    public boolean canAccess(SystemUser user) {
        if(existsLink(user) || existsLink(SystemUserGroup.getDefault())) {
            return true;
        }
        List<SystemUserGroup> groups = user.listGroups().toList();
        for(SystemUserGroup group : groups) {
            if(existsLink(group)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return purpose;
    }

    @Override
    public String toDisplay() {
        return purpose;
    }
}
