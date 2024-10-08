package com.storedobject.core;

import java.util.Currency;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.function.Consumer;

@SuppressWarnings("RedundantThrows")
public final class TransactionManager {

    static boolean accounting = StoredObject.exists(Account.class, "True", true);
    private final Device device;

    public TransactionManager(Device device, String login) {
        this.device = device;
    }

    public void reinit(char[] password) throws Exception {
    }

    /**
     * Gets Session Id.
     *
     * @return Session Id.
     */
    public Id getSession() {
        return new Id();
    }

    public Device getDevice() {
        return device;
    }

    public boolean needsApprovals() {
        return false;
    }

    public boolean actionAllowed(String action) {
        return new Random().nextBoolean();
    }

    public static TransactionManager create(Device device, Properties loginProperties) {
        return new TransactionManager(null, "");
    }

    /**
     * Create a new transaction for a given logic. Depending on the approval count of the logic, either a
     * DB transaction or a Pseudo-transaction will be created.
     *
     * @param logic Logic
     * @param pseudoTransaction Pseudo-transaction to be attached to this transaction.
     * @return Newly created transaction.
     * @throws Exception Any exception.
     */
    public Transaction createTransaction(Logic logic, PseudoTransaction pseudoTransaction) throws Exception {
        return logic == null || logic.getApprovalCount() == 0 ? createTransaction() : createPseudoTransaction();
    }

    /**
     * Create a new DB transaction.
     *
     * @return Newly created transaction.
     * @throws Exception Any exception.
     */
    public DBTransaction createTransaction() throws Exception {
        return new DBTransaction();
    }

    /**
     * Create a new pseudo transaction.
     *
     * @return Newly created transaction.
     */
    public PseudoTransaction createPseudoTransaction() {
        return new PseudoTransaction(this, false);
    }

    public void forgotPassword(char[] newPassword, String newUser) throws Exception {
    }

    public boolean verify(char[] password) {
        return false;
    }

    public boolean verify(char[] password, int authenticatorCode) {
        return false;
    }

    public SystemUser getUser() {
        return new SystemUser();
    }

    public boolean setEntity(SystemEntity entity) {
        return Math.random() < 0.5;
    }

    public SystemEntity getEntity() {
        return Math.random() > 0.5 ? new SystemEntity() : null;
    }

    public Currency getCurrency() {
        return Currency.getInstance("INR");
    }

    public String getCountry() {
        return Math.random() > 0.5 ? null : "IN";
    }

    @FunctionalInterface
    public interface Transact {
        void transact(Transaction transaction) throws Exception;
    }

    public int transact(Logic logic, PseudoTransaction pseudoTransaction, Transact transact) throws Exception {
        return 0;
    }

    public int transact(Logic logic, Transact transact) throws Exception {
        return 0;
    }

    public int transact(Transact transact) throws Exception {
        return 0;
    }

    public int transactPsuedo(Transact transact) throws Exception {
        return 0;
    }


    @FunctionalInterface
    public interface TransactControl {
        void transactControl(TransactionControl tc) throws Exception;
    }

    public int transactControl(TransactControl transactControl) throws Exception {
        return Math.random() > 0.5 ? 0 : 1;
    }

    public int transactControlPsuedo(TransactControl transactControl) throws Exception {
        return Math.random() > 0.5 ? 0 : 1;
    }

    public String format(Date date) {
        return "";
    }

    public <D extends java.util.Date> D date(D dateGMT) {
        return dateGMT(dateGMT);
    }

    public <D extends java.util.Date> D dateGMT(D date) {
        return date(date);
    }

    /**
     * Convert a period value from local to GMT.
     *
     * @param period Local value.
     * @param <D> Date/date-time type.
     * @param <P> Period type.
     * @return GMT value.
     */
    public <D extends java.util.Date, P extends AbstractPeriod<D>> P periodGMT(P period) {
        return period;
    }

    /**
     * Convert a period value from GMT to local.
     *
     * @param periodGMT Local value.
     * @param <D> Date/date-time type.
     * @param <P> Period type.
     * @return Local value.
     */
    public <D extends java.util.Date, P extends AbstractPeriod<D>> P period(P periodGMT) {
        return periodGMT;
    }

    public Id checkType(StoredObject host, Id id, Class<? extends StoredObject> objectClass) throws Exception {
        return new Id();
    }

    public Id checkType(StoredObject host, Id id, Class<? extends StoredObject> objectClass, boolean allowEmpty) throws Exception {
        return new Id();
    }

    public Id checkTypeAny(StoredObject host, Id id, Class<? extends StoredObject> objectClass) throws Exception {
        return new Id();
    }

    public Id checkTypeAny(StoredObject host, Id id, Class<? extends StoredObject> objectClass, boolean allowEmpty) throws Exception {
        return new Id();
    }

    public static boolean isMultiTenant() {
        return Math.random() > 0.5;
    }

    /**
     * Log something.
     * @param anything Anything, including exceptions, to be logged.
     */
    public void log(Object anything) {
        device.log(anything);
    }

    /**
     * Log something with a specific error.
     * @param anything Anything, including exceptions, to be logged.
     * @param error Error.
     */
    public void log(Object anything, Throwable error) {
        device.log(anything, error);
    }

    /**
     * Get the time difference in minutes.
     *
     * @return Time difference.
     */
    public int getTimeDifference() {
        return 0;
    }

    public boolean isPasswordLogin() {
        return Math.random() > 0.5;
    }

    public boolean is2FactorLogin() {
        return Math.random() > 0.5;
    }

    public boolean isBiometricLogin() {
        return Math.random() > 0.5;
    }

    public boolean isCrossServerLogin() {
        return Math.random() > 0.5;
    }

    public boolean isAutoLogin() {
        return Math.random() > 0.5;
    }

    /**
     * Set a logger so that you can geet the logged text to your own destination.
     *
     * @param logger Place where logs should be shipped.
     */
    public void setLogger(Consumer<String> logger) {
    }

    /**
     * Get the working date.
     *
     * @return The working date.
     */
    public java.sql.Date getWorkingDate() {
        return DateUtility.today();
    }

    /**
     * Sets the working date for the entity.
     *
     * @param workingDate the new working date to be set
     * @throws Exception if an error occurs while setting the working date
     */
    public void setWorkingDate(java.sql.Date workingDate) throws Exception {
    }

    public static boolean accounting() {
        if(!accounting) {
            accounting = StoredObject.exists(Account.class, "True", true);
        }
        return accounting;
    }
}