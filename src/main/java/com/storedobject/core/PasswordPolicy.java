package com.storedobject.core;

public class PasswordPolicy extends StoredObject {

    public PasswordPolicy() {
    }

    public static void columns(Columns columns) {
    }

    public void setDataClass(String dataClass) {
    }

    public String getDataClass() {
        return "";
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
        return new String[0];
    }

    public static String getRequirementValue(int value) {
        return "";
    }

    public String getRequirementValue() {
        return "";
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

    public void setReuseHistory(int reuseHistory) {
    }

    public int getReuseHistory() {
        return 0;
    }

    public void setInitialPassword(String initialPassword) {
    }

    public String getInitialPassword() {
        return "";
    }

    public String describe() {
        return "";
    }

    public String apply(char[] password) {
        return "";
    }

    public static PasswordPolicy get(Id owner) {
        return new PasswordPolicy();
    }

    public static <T extends StoredObject> PasswordPolicy get(T owner) {
        return new PasswordPolicy();
    }

    public static <T extends StoredObject> PasswordPolicy getForClass(Class<T> owner) {
        return new PasswordPolicy();
    }
}
