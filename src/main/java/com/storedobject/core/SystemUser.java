package com.storedobject.core;

import com.storedobject.common.SOException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * This class represents a person who is also a system user.
 */
@SuppressWarnings("RedundantThrows")
public final class SystemUser extends StoredObject implements HasName {

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

    public static Locale getLocale(String localeCountry, String localeLanguage) {
        return Locale.getDefault();
    }

    public static SystemUser getExact(String login) {
        return Math.random() < 0.5 ? new SystemUser() : null;
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

    public static String[] getStatusBitValues() {
        return new String[1];
    }

    public static String getStatusValue(int value) {
        return getStatusBitValues()[value];
    }

    public String getStatusValue() {
        return getStatusValue(0);
    }

    public void saveKeyForTOTP(TransactionManager tm, byte[] keyForTOTP) throws Exception {
        if(new Random().nextBoolean()) {
            throw new Invalid_State("Not authorized");
        }
    }

    public boolean verifyTOTP(int totp) {
        return new Random().nextInt(totp) > 100;
    }

    public void validateNewPassword(char[] currentPassword, char[] password) throws SOException {
    }

    public void changePassword(char[] currentPassword, char[] newPassword, String newUsername) throws SOException {
    }

    public char[] generateSecretToken() {
        return new char[2];
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

    public boolean verify(char[] password, boolean ignoreAuthenticator) {
        return false;
    }

    public boolean verify(char[] password) {
        return false;
    }

    public boolean verify(char[] password, int authenticatorCode) {
        return false;
    }

    public boolean isVerified() {
        return true;
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

    public int getPreferences() {
        return 0;
    }

    public void setPreferences(int preferences) {
    }
    public static String[] getPreferencesBitValues() {
        return new String[] {};
    }

    public static String getPreferencesValue(int value) {
        return "";
    }

    public String getPreferencesValue() {
        return "x";
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

    public void saveAsProcessUser(Transaction transaction) throws Exception {
        save(transaction);
    }

    public void saveAsExternalUser(Transaction transaction) throws Exception {
        save(transaction);
    }

    public void saveAsAuditor(Transaction transaction) throws Exception {
        save(transaction);
    }

    public static boolean isValidLogin(String login) {
        return Math.random() > 0.5f;
    }

    public static boolean isLoginAvailable(String login) {
        return Math.random() > 0.5f;
    }

    public boolean canChangeLoginTo(String login) {
        return Math.random() > 0.5f;
    }

    public ObjectIterator<Logic> listQuickAccessLogic() {
        return ObjectIterator.create();
    }

    public ObjectIterator<Logic> listAutoLogic() {
        return ObjectIterator.create();
    }

    public ObjectIterator<SystemUserGroup> listGroups() {
        return ObjectIterator.create();
    }

    public boolean getConfirmLogout() {
        return false;
    }

    public String format(Date date) {
        return "";
    }

    public boolean isLocked() {
        return Math.random() > 0.5;
    }

    public void unlock(TransactionManager tm) throws Exception {
    }

    public void lock(TransactionManager tm) throws Exception {
    }

    /**
     * Get the list of entities configured for this user.
     *
     * @return List.
     */
    public List<SystemEntity> listEntities() {
        return new ArrayList<>();
    }

    public Iterable<SessionLog> getSessionLog(AbstractPeriod<?> period, String server) {
        return getSessionLog(period, true, server);
    }

    public Iterable<SessionLog> getSessionLog(AbstractPeriod<?> period, boolean reverse, String server) {
        return new ArrayList<>();
    }

    public Iterable<StoredObject> getChangeLog(SessionLog sessionLog) {
        return new ArrayList<>();
    }

    public Iterable<LogicHit> getLogicHit(SessionLog sessionLog) {
        return new ArrayList<>();
    }

    public String createLoginBlock(String externalServer) {
        return createLoginBlock(ServerInformation.getServer(externalServer));
    }

    public String createLoginBlock(ServerInformation server) {
        return Math.random() > 0.5 ? "" : null;
    }

    public List<ExternalSystemUser> listExternalUsers(String fromURL) {
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

    public AutoLogin getAutoLogin() {
        return null;
    }
}