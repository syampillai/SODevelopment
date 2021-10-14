package com.storedobject.core;

import com.storedobject.common.SOException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
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

    public static String[] getMenuStyleValues() {
        return null;
    }

    public static String getMenuStyleValue(int value) {
        return null;
    }

    public static String[] getPreferencesBitValues() {
        return null;
    }

    public static String getPreferencesValue(int value) {
        return null;
    }

    public static Locale getLocale(String localeCountry, String localeLanguage) {
        return Locale.getDefault();
    }

    public static SystemUser get(String login) {
        return null;
    }

    public static ObjectIterator<SystemUser> list(String login) {
        return null;
    }

    public String getName() {
        return null;
    }

    public Id getPersonId() {
        return null;
    }

    public Person getPerson() {
        return new Person();
    }

    public void setPerson(Id id) {
    }

    public void setPerson(BigDecimal idValue) {
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

    public int getMenuStyle() {
        return 0;
    }

    public void setMenuStyle(int menuStyle) {
    }

    public String getMenuStyleValue() {
        return null;
    }

    public int getPreferences() {
        return 0;
    }

    public void setPreferences(int preferences) {
    }

    public String getPreferencesValue() {
        return null;
    }

    public String getLocaleLanguage() {
        return "en";
    }

    public void setLocaleLanguage(String localeLanguage) {
    }

    public String getLocaleCountry() {
        return "IN";
    }

    public void setLocaleCountry(String localeCountry) {
    }

    public Locale getLocale() {
        return Locale.getDefault();
    }

    public void setLocale(Locale locale) {
    }

    public ObjectIterator<Logic> listQuickAccessLogic() {
        return ObjectIterator.create();
    }

    public ObjectIterator<Logic> listAutoLogic() {
        return ObjectIterator.create();
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

    public Iterable<SessionLog> getSessionLog(AbstractPeriod<?> period) {
        return getSessionLog(period, true);
    }

    public Iterable<SessionLog> getSessionLog(AbstractPeriod<?> period, boolean reverse) {
        return new ArrayList<>();
    }

    public Iterable<StoredObject> getChangeLog(SessionLog sessionLog) {
        return new ArrayList<>();
    }

    public Iterable<LogicHit> getLogicHit(SessionLog sessionLog) {
        return new ArrayList<>();
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

    public static final class LogicHit {

        private Logic logic;
        private Timestamp hitTime;
        private boolean executed;

        private LogicHit(ResultSet rs) {
            try {
                this.logic = get(Logic.class, new Id(rs.getBigDecimal(1)));
                this.hitTime = rs.getTimestamp(2);
                this.executed = rs.getBoolean(3);
            } catch(SQLException ignored) {
            }
        }

        public Logic getLogic() {
            return logic;
        }

        public Timestamp getHitTime() {
            return hitTime;
        }

        public boolean isExecuted() {
            return executed;
        }
    }
}