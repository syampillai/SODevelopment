package com.storedobject.core;

import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * Audit trail..
 *
 * @author Syam
 */
public final class AuditTrail {

    private final Id tranId, sessionId, userId;
    private final int status;
    private final Timestamp timestamp;
    private Timestamp loginTime, logoutTime;
    private String ip, application;

    private AuditTrail(Id tranId) throws Exception {
        this.tranId = tranId;
        RawSQL sql = new RawSQL();
        try {
            if(!sql.execute("SELECT SessionId,SystemUser,TranTime,Status FROM core.TranLog WHERE Id=" + tranId)
                    || sql.eoq()) {
                throw new Exception();
            }
            ResultSet rs = sql.getResult();
            assert rs != null;
            sessionId = new Id(rs.getBigDecimal(1));
            userId = new Id(rs.getBigDecimal(2));
            timestamp = rs.getTimestamp(3);
            status = rs.getInt(4);
        } finally {
            sql.close();
        }
    }

    /**
     * Create audit trail for the given instance.
     *
     * @param object Object instance.
     * @return Audit trail instance.
     */
    public static AuditTrail create(StoredObject object) {
        if(object == null) {
            return null;
        }
        return create(object.getTransactionId());
    }


    /**
     * Create audit trail for the given transaction ID.
     *
     * @param transactionId Transaction ID.
     * @return Audit trail instance.
     */
    public static AuditTrail create(Id transactionId) {
        if(Id.isNull(transactionId)) {
            return null;
        }
        try {
            return new AuditTrail(transactionId);
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Get transaction Id.
     *
     * @return Transaction ID.
     */
    public Id getId() {
        return tranId;
    }

    /**
     * Get timestamp.
     *
     * @return Timestamp.
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Get the user who created this transaction.
     *
     * @return System User.
     */
    public SystemUser getUser() {
        return StoredObject.get(SystemUser.class, userId);
    }

    /**
     * Get the login time of the user.
     *
     * @return Log in time.
     */
    public Timestamp getLoginTime() {
        if(loginTime == null) {
            session();
        }
        return loginTime;
    }

    /**
     * Get the logout time of the user.
     * <p>Note: Sometimes the logout time may not be recorded correctly dur to communication error. In that case,
     * this method returns <code>null</code>.</p>
     *
     * @return Log out time.
     */
    public Timestamp getLogoutTime() {
        if(logoutTime == null) {
            session();
        }
        return loginTime != null && loginTime.getTime() < logoutTime.getTime() ? logoutTime : null;
    }

    private void session() {
        RawSQL sql = new RawSQL();
        try {
            if(!sql.execute("SELECT Application,IPAddress,LoginTime,LogoutTime FROM core.TranSession WHERE Id="
                    + sessionId)
                    || sql.eoq()) {
                return;
            }
            ResultSet rs = sql.getResult();
            assert rs != null;
            application = rs.getString(1);
            ip = rs.getString(2);
            loginTime = rs.getTimestamp(3);
            logoutTime = rs.getTimestamp(4);
        } catch(Exception ignored) {
        } finally {
            sql.close();
        }
    }

    /**
     * Get the application client environment. If it was executed from a browser, browser details will be included.
     *
     * @return Application client environment.
     */
    public String getApplicationClient() {
        if(application == null) {
            session();
        }
        return application;
    }

    /**
     * Get the IP address from which this transaction was carried out.
     *
     * @return IP address string.
     */
    public String getIPAddress() {
        if(ip == null) {
            session();
        }
        return ip;
    }

    /**
     * Get the status of the transaction.
     *
     * @return Status description.
     */
    public String getStatus() {
        return switch(status) {
            case 0 -> "Initiated/created";
            case 3 -> "Rolled back";
            case 7, 31 -> "Too long (more than 5 minutes)";
            case 13, 32 -> "Illegal";
            case 33 -> "Superseded class (No save allowed)";
            case 23, 34 -> "Design error";
            default -> "Unknown";
        };
    }
}
