package com.storedobject.core;

import com.storedobject.common.SOException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Stream;

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

    public void setPerson(Id id) {
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
        return "en";
    }

    public void setLocaleCountry(String localeCountry) {
    }

    public String getLocaleCountry() {
        return "IN";
    }

    public void setLocale(Locale locale) {
    }

    public Locale getLocale() {
        return Locale.getDefault();
    }

    public static Locale getLocale(String localeCountry, String localeLanguage) {
        return Locale.getDefault();
    }

    public ObjectIterator<Logic> listQuickAccessLogic() {
        return ObjectIterator.create();
    }

    public ObjectIterator<Logic> listAutoLogic() {
        return ObjectIterator.create();
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

    public Stream<SessionLog> getSessionLog(AbstractPeriod<?> period) {
        return getSessionLog(period, true);
    }

    public Stream<SessionLog> getSessionLog(AbstractPeriod<?> period, boolean reverse) {
        return Stream.empty();
    }

    public static final class SessionLog {

        private String application;
        private String ipAddress;
        private Timestamp inTime, outTime;
        private boolean out;

        private SessionLog(ResultSet rs) {
            try {
                this.application = rs.getString(1);
                this.ipAddress = rs.getString(2);
                this.inTime = rs.getTimestamp(3);
                this.outTime = rs.getTimestamp(4);
                this.out = rs.getInt(5) == 0;
            } catch(SQLException ignored) {
            }
        }

        public String getApplication() {
            return application;
        }

        public String getIPAddress() {
            return ipAddress;
        }

        public Timestamp getInTime() {
            return inTime;
        }

        public Timestamp getOutTime() {
            return out ? outTime : null;
        }
    }
}