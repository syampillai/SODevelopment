package com.storedobject.core;

public class PasswordPolicy extends StoredObject {

    public PasswordPolicy() {
    }

    public static void columns(Columns columns) {
    }

    public void setDataClass(String dataClass) {
    }

    public String getDataClass() {
        return null;
    }

    public void setMinimumLength(int minimumLength) {
    }

    public int getMinimumLength() {
        return 0;
    }

    public void setMaximumLength(int maximumLength) {
    }

    public int getMaximumLength() {
        return 0;
    }

    public void setRequirement(int requirement) {
    }

    public int getRequirement() {
        return 0;
    }

    public static String[] getRequirementValues() {
        return null;
    }

    public static String getRequirementValue(int value) {
        return null;
    }

    public String getRequirementValue() {
        return null;
    }

    public void setRequireMixedcase(boolean requireMixedcase) {
    }

    public boolean getRequireMixedcase() {
        return false;
    }

    public void setAllowRepeatCharacters(boolean allowRepeatCharacters) {
    }

    public boolean getAllowRepeatCharacters() {
        return false;
    }

    public void setExpiryDays(int expiryDays) {
    }

    public int getExpiryDays() {
        return 0;
    }

    public String describe() {
        return null;
    }

    public String apply(String password) {
        return null;
    }

    public static PasswordPolicy get(Id owner) {
        return null;
    }

    public static <T extends StoredObject> PasswordPolicy get(T owner) {
        return null;
    }

    public static <T extends StoredObject> PasswordPolicy getForClass(Class<T> owner) {
        return null;
    }
}
