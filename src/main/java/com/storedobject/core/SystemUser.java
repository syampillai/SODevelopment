package com.storedobject.core;

import com.storedobject.common.SOException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

/**
 * This class represents an person who is also a system user.
 */
public final class SystemUser extends StoredObject implements RequiresApproval {

    public SystemUser(String login, Id personId) {
        this();
    }

    public SystemUser() {
    }

    public static void columns(Columns columns) {
    }

    public String getName() {
        return null;
    }

    public Id getPersonId() {
        return null;
    }

    public void setPerson(BigDecimal idValue) {
    }

    public Person getPerson() {
        return new Person();
    }

    public String getLogin() {
        return "";
    }

    public int getStatus() {
        return 0;
    }

    public void setStatus(int status) {
    }

    public void validateNewPassword(char[] currentPassword, char[] password) throws SOException {
    }

    public void changePassword(char[] currentPassword, char[] newPassword) throws SOException {
    }


    public java.sql.Date getPasswordExpiry() {
        return null;
    }

    public boolean isPasswordExpired() {
        return false;
    }

    public boolean verifyPasswordUpdate() {
        return false;
    }

    public boolean verify(char[] password) {
        return false;
    }

    public boolean verify(char[] password, int authenticatorCode) {
        return false;
    }

    public void resetPassword() throws Exception {
    }

    public boolean isSS() {
        return false;
    }

    public boolean isAdmin() {
        return false;
    }

    public boolean isAppAdmin() {
        return false;
    }

    public void setMenuStyle(int menuStyle) {
    }

    public int getMenuStyle() {
        return 0;
    }

    public static String[] getMenuStyleValues() {
        return null;
    }

    public static String getMenuStyleValue(int value) {
        return null;
    }

    public String getMenuStyleValue() {
        return null;
    }

    public void setPreferences(int preferences) {
    }

    public int getPreferences() {
        return 0;
    }

    public static String[] getPreferencesBitValues() {
        return null;
    }

    public static String getPreferencesValue(int value) {
        return null;
    }

    public String getPreferencesValue() {
        return null;
    }

    public void setLocaleLanguage(String localeLanguage) {
    }

    public String getLocaleLanguage() {
        return null;
    }

    public void setLocaleCountry(String localeCountry) {
    }

    public String getLocaleCountry() {
        return null;
    }

    public void setLocale(Locale locale) {
    }

    public Locale getLocale() {
        return null;
    }

    public static Locale getLocale(String localeCountry, String localeLanguage) {
        return null;
    }

    public ObjectIterator<Logic> listQuickAccessLogic() {
        return null;
    }

    public ObjectIterator<Logic> listAutoLogic() {
        return null;
    }

    public static SystemUser get(String login) {
        return null;
    }

    public static ObjectIterator<SystemUser> list(String login) {
        return null;
    }

    public ObjectIterator<SystemUserGroup> listGroups() {
        return null;
    }

    public boolean getConfirmLogout() {
        return false;
    }

    public String format(Date date) {
        return null;
    }

    public void unlock(TransactionManager tm) throws Exception {
    }
}