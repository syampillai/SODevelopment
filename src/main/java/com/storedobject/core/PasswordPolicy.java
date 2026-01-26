package com.storedobject.core;

import com.storedobject.core.annotation.*;

/**
 * The PasswordPolicy class represents a set of rules and constraints that define password requirements
 * for a system. It includes parameters such as allowed character types, length constraints,
 * expiration policies, and reuse restrictions.
 * This class is designed to ensure passwords comply with specified security standards
 * and can be applied to validate password input.
 <p>
 * Attributes:
 * - Defines the minimum and maximum allowed lengths for passwords.
 * - Specifies character requirements, such as alphanumeric, special characters, or numeric-only.
 * - Allows toggling case sensitivity and restriction on repeated characters.
 * - Includes functionality for password expiry and reuse history.
 * - Supports initial default password configuration.
 * </p>
 */
public class PasswordPolicy extends StoredObject {

    private static final String[] requirementValues = new String[] {
            "Alphanumeric with Special Characters",
            "Alphanumeric",
            "Alphanumeric only",
            "Alphabetic",
            "Alphabetic only",
            "Numeric",
            "Only numeric",
    };
    private String dataClass = "";
    private int minimumLength = 10;
    private int maximumLength = 99;
    private int requirement = 0;
    private boolean requireMixedcase = true;
    private boolean allowRepeatCharacters = false;
    private int expiryDays = 0;
    private int reuseHistory = 5;
    private String initialPassword = "Welcome2System$";

    /**
     * Constructs a new instance of the PasswordPolicy class.
     * This class is typically used for defining and enforcing rules
     * and constraints on passwords to ensure they comply with security standards.
     */
    public PasswordPolicy() {
    }

    /**
     * Configures the specified columns with predefined attributes and their respective data types.
     *
     * @param columns The Columns object to which the specified attributes and data types will be added.
     */
    public static void columns(Columns columns) {
        columns.add("DataClass", "text");
        columns.add("MinimumLength", "int");
        columns.add("MaximumLength", "int");
        columns.add("Requirement", "int");
        columns.add("RequireMixedcase", "boolean");
        columns.add("AllowRepeatCharacters", "boolean");
        columns.add("ExpiryDays", "int");
        columns.add("ReuseHistory", "int");
        columns.add("InitialPassword", "text");
    }

    /**
     * Provides a hint or constant value representing a specific object state,
     * characteristic, or configuration. In this implementation, it returns
     * the constant value for a small list hint.
     *
     * @return an integer constant representing the hint for a small list
     */
    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    /**
     * Adds an index to the `indices` object with predefined parameters.
     *
     * @param indices the `Indices` object where the index will be added
     */
    public static void indices(Indices indices) {
        indices.add("DataClass", true);
    }

    /**
     * Constructs and returns a unique condition string based on the `dataClass` value.
     *
     * @return a string representing a unique condition in the format "DataClass='value'".
     */
    public String getUniqueCondition() {
        return "DataClass='" + dataClass + "'";
    }

    /**
     * Sets the value of the dataClass field.
     *
     * @param dataClass the new value to assign to the dataClass field
     */
    public void setDataClass(String dataClass) {
        this.dataClass = dataClass;
    }

    /**
     * Retrieves the associated data class.
     *
     * @return The name of the associated data class as a string. If no data class
     *         is associated, it returns null.
     */
    @Column(required = false, caption = "Associated Class", order = 100)
    public String getDataClass() {
        return dataClass;
    }

    /**
     * Sets the minimum length for passwords in the policy.
     *
     * @param minimumLength the minimum number of characters required for a password
     */
    public void setMinimumLength(int minimumLength) {
        this.minimumLength = minimumLength;
    }

    /**
     * Retrieves the minimum length value.
     *
     * @return the minimum length as an integer.
     */
    @Column(order = 200)
    public int getMinimumLength() {
        return minimumLength;
    }

    /**
     * Sets the maximum length for the password.
     *
     * @param maximumLength the maximum number of characters allowed for the password
     */
    public void setMaximumLength(int maximumLength) {
        this.maximumLength = maximumLength;
    }

    /**
     * Retrieves the maximum length allowed by the password policy.
     *
     * @return The maximum allowable length for passwords.
     */
    @Column(order = 300)
    public int getMaximumLength() {
        return maximumLength;
    }

    /**
     * Sets the requirement level for the password policy.
     *
     * @param requirement the requirement level to be set. It determines the password policy's specific constraints or rules.
     */
    public void setRequirement(int requirement) {
        this.requirement = requirement;
    }

    /**
     * Retrieves the requirement value for the password policy.
     *
     * @return The requirement value as an integer, representing specific conditions or rules
     *         associated with the password policy.
     */
    @Column(order = 400)
    public int getRequirement() {
        return requirement;
    }

    /**
     * Retrieves the array of requirement values associated with the password policy.
     *
     * @return An array of strings representing the requirement values.
     */
    public static String[] getRequirementValues() {
        return requirementValues;
    }

    /**
     * Retrieves a requirement value based on the given index.
     * The value is determined from a predefined array of requirement values,
     * cycling through the array if the index exceeds its length.
     *
     * @param value The index to retrieve the requirement value. If the index exceeds
     *              the length of the requirement values array, it wraps around using modulo operation.
     * @return A string representing the requirement value corresponding to the provided index.
     */
    public static String getRequirementValue(int value) {
        String[] s = getRequirementValues();
        return s[value % s.length];
    }

    /**
     * Retrieves the requirement value associated with the current requirement.
     *
     * @return A string representing the requirement value mapped to the current requirement.
     */
    public String getRequirementValue() {
        return getRequirementValue(requirement);
    }

    /**
     * Sets whether the password policy requires passwords to include a mix of uppercase and lowercase letters.
     *
     * @param requireMixedcase a boolean indicating if mixed case is required;
     *                         true if passwords must include both uppercase and lowercase letters,
     *                         false otherwise.
     */
    public void setRequireMixedcase(boolean requireMixedcase) {
        this.requireMixedcase = requireMixedcase;
    }

    /**
     * Retrieves the status of whether mixed-case characters are required.
     *
     * @return True if mixed-case characters are required; otherwise, false.
     */
    @Column(order = 500)
    public boolean getRequireMixedcase() {
        return requireMixedcase;
    }

    /**
     * Sets whether repeated characters are allowed in the password.
     *
     * @param allowRepeatCharacters a boolean value where {@code true} allows repeated characters
     *                              in the password, and {@code false} disallows them.
     */
    public void setAllowRepeatCharacters(boolean allowRepeatCharacters) {
        this.allowRepeatCharacters = allowRepeatCharacters;
    }

    /**
     * Retrieves the value indicating whether repeated characters are allowed.
     *
     * @return true if repeated characters are allowed, false otherwise.
     */
    @Column(order = 600)
    public boolean getAllowRepeatCharacters() {
        return allowRepeatCharacters;
    }

    /**
     * Sets the number of days until expiration.
     *
     * @param expiryDays the number of days to set for expiration
     */
    public void setExpiryDays(int expiryDays) {
        this.expiryDays = expiryDays;
    }

    /**
     * Retrieves the number of days until the item expires.
     *
     * @return the number of expiry days as an integer.
     */
    @Column(order = 700, required = false)
    public int getExpiryDays() {
        return expiryDays;
    }

    /**
     * Calculates and returns the number of expiry days.
     * If the value of expiryDays is less than or equal to 0, a default value of 14600 is returned.
     *
     * @return the number of expiry days, or a default value of 14600 if expiryDays is less than or equal to 0
     */
    int expiryDays() {
        return expiryDays <= 0 ? 14600 : expiryDays;
    }

    /**
     * Sets the reuse history value.
     *
     * @param reuseHistory the number of times an object or action has been reused
     */
    public void setReuseHistory(int reuseHistory) {
        this.reuseHistory = reuseHistory;
    }

    /**
     * Retrieves the reuse history count for the associated entity.
     *
     * @return the number of times the entity has been reused.
     */
    @Column(required = false, order = 800)
    public int getReuseHistory() {
        return reuseHistory;
    }

    /**
     * Sets the initial password for this password policy.
     *
     * @param initialPassword the initial password to be set
     */
    public void setInitialPassword(String initialPassword) {
        this.initialPassword = initialPassword;
    }

    /**
     * Retrieves the initial password assigned to a user or system entity.
     *
     * @return the initial password as a String.
     */
    @Column(order = 900)
    public String getInitialPassword() {
        return initialPassword;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(minimumLength < 4) {
            throw new Invalid_Value(("Minimum Length"));
        }
        if(maximumLength < minimumLength) {
            throw new Invalid_Value(("Maximum Length"));
        }
        if(requirement == 6) {
            requireMixedcase = false;
        }
        dataClass = dataClass == null ? "" : dataClass.trim();
        if(!dataClass.isEmpty()) {
            Class<?> dClass = null;
            try {
                dClass = JavaClassLoader.getLogic(dataClass);
                if(dClass != null && !StoredObject.class.isAssignableFrom(dClass)) {
                    dClass = null;
                }
            } catch (Throwable ignored) {
            }
            if(dClass == null) {
                throw new Invalid_Value(("Associated Class"));
            }
        }
        if(StringUtility.isWhite(initialPassword)) {
            throw new Invalid_Value("Initial Password");
        }
        String a = apply(initialPassword.toCharArray());
        if(a != null) {
            throw new Invalid_State("Initial Password - " + a);
        }
        super.validateData(tm);
    }

    /**
     * Generates a descriptive string about the password requirements.
     * The description includes:
     * - The type of characters allowed or required (e.g., alphanumeric, numeric, etc.).
     * - Whether mixed case is required (upper and lowercase letters).
     * - Whether repeating characters are allowed.
     * - The history of previously used passwords that cannot be reused.
     * - The minimum length required for the password.
     *
     * @return A string describing the password requirements based on the current configuration.
     */
    public String describe() {
        StringBuilder s = new StringBuilder("Password $ contain ");
        String should = "should";
        switch(requirement) {
            case 0 -> s.append("alphanumeric and special characters");
            case 1 -> {
                should = "can";
                s.append("any characters but include at least one alphabetic character and one digit");
            }
            case 2 -> s.append("at least one alphabetic character and one digit");
            case 3 -> s.append("at least one alphabetic character");
            case 4 -> s.append("alphabetic characters only");
            case 5 -> {
                should = "can";
                s.append("any characters but include at least one digit");
            }
            case 6 -> s.append("numeric digits only");
        }
        s.append('.');
        if(requireMixedcase) {
            s.append(" At least one alphabetic character must be in uppercase and at least one must be in lowercase.");
        }
        if(!allowRepeatCharacters) {
            s.append(" Repeat characters are not allowed.");
        }
        if(reuseHistory > 0) {
            s.append(" Password should not be the same as ");
            if(reuseHistory == 1) {
                s.append("the previous one.");
            } else {
                s.append("one of the ").append(reuseHistory).append(" previously used passwords.");
            }
        }
        s.append(" Minimum length required is ").append(minimumLength).append('.');
        return s.toString().replace("$", should);
    }

    /**
     * Applies a password validation check based on predefined rules such as length limits,
     * character composition, and prohibitions against specific sequences or repetitions.
     *
     * @param password the input array of characters representing the password to be validated.
     * @return a string describing the validation result. Returns null if the password satisfies
     *         all rules, or an error message indicating the validation failure reason otherwise.
     */
    public String apply(char[] password) {
        int n = password.length;
        if(n < minimumLength || n > maximumLength) {
            return "Invalid length";
        }
        if(n == 0 && minimumLength == 0 && maximumLength == 0) {
            return null;
        }
        boolean capital = false, small = false, alpha = false, digit = false, special = false;
        int i;
        char c;
        for(i = 0; i < n; i++) {
            c = password[i];
            if(!allowRepeatCharacters && i > 0 && c == password[i - 1]) {
                return "Contains repeat characters";
            }
            if(c >= 'A' && c <= 'Z') {
                capital = alpha = true;
                continue;
            }
            if(c >= 'a' && c <= 'z') {
                small = alpha = true;
                continue;
            }
            if(c >= '0' && c <= '9') {
                digit = true;
                continue;
            }
            special = true;
        }
        if(requireMixedcase && (!small || !capital)) {
            return "Doesn't contain mixed case alphabets";
        }
        switch (requirement) {
            case 0: // Alphanumeric with Special Characters
                if(alpha && digit && special) {
                    return null;
                }
                break;
            case 1: // Alphanumeric
                if(alpha && digit) {
                    return null;
                }
                special = true;
                break;
            case 2: // Alphanumeric only
                if(alpha && digit && !special) {
                    return null;
                }
                if(special) {
                    return "Special characters are not allowed";
                }
                break;
            case 3: // Alphabets
                if(alpha) {
                    return null;
                }
                digit = special = true;
                break;
            case 4: // Alphabets only
                if(alpha && !digit && !special) {
                    return null;
                }
                return "Only alphabets are allowed";
            case 5: // Numeric
                if(digit) {
                    return null;
                }
                alpha = special = true;
                break;
            case 6: // Numeric only
                if(!alpha && digit && !special) {
                    return null;
                }
                return "Only numeric digits are allowed";
        }
        if(!special) {
            if(!digit) {
                return "At least one digit and one special character are required";
            }
            if(!alpha) {
                return "At least one alphabet and one special character are required";
            }
            return "At least one special character is required";
        }
        if(!digit) {
            if(!alpha) {
                return "At least one alphabet and one digit are required";
            }
            return "At least one digit is required";
        }
        return "At least one alphabet is required";
    }

    /**
     * Retrieves the password policy associated with the specified owner.
     * If the owner is null, it returns a default password policy.
     *
     * @param owner the identifier of the owner whose password policy is to be retrieved
     * @return the password policy associated with the given owner, or a default password policy if the owner is null
     */
    public static PasswordPolicy get(Id owner) {
        return owner == null ? new PasswordPolicy() : get(get(owner));
    }

    /**
     * Retrieves a PasswordPolicy associated with the specified owner object.
     * If the owner is null, a default PasswordPolicy is returned.
     *
     * @param <T>   the type of the owner, which must extend StoredObject
     * @param owner the owner object for which the PasswordPolicy is to be retrieved;
     *              if null, a default PasswordPolicy is returned
     * @return the PasswordPolicy associated with the owner's class, or a default
     *         PasswordPolicy if the owner is null
     */
    public static <T extends StoredObject> PasswordPolicy get(T owner) {
        return owner == null ? new PasswordPolicy() : getForClass(owner.getClass());
    }

    /**
     * Retrieves the {@link PasswordPolicy} for the specified class.
     *
     * @param <T>   The type parameter extending {@link StoredObject}.
     * @param owner The class for which the {@link PasswordPolicy} is to be retrieved.
     *              If null, a default {@link PasswordPolicy} is returned.
     * @return The {@link PasswordPolicy} associated with the specified class.
     *         If no policy is found, a default {@link PasswordPolicy} is returned.
     */
    public static <T extends StoredObject> PasswordPolicy getForClass(Class<T> owner) {
        if(owner == null) {
            return new PasswordPolicy();
        }
        PasswordPolicy policy = get(PasswordPolicy.class, "DataClass='" + owner.getName() + "'");
        if(policy == null) {
            policy = get(PasswordPolicy.class, "DataClass=''");
        }
        if(policy == null && owner != SystemUser.class) {
            return getForClass(SystemUser.class);
        }
        return policy == null ? new PasswordPolicy() : policy;
    }

    /**
     * Generates a new password based on the current password policy.
     *
     * @return a string representing the generated password.
     */
    public String generatePassword() {
        return Secret.generatePassword(this);
    }
}
