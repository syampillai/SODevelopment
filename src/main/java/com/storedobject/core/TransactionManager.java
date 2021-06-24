package com.storedobject.core;

import java.util.Currency;
import java.util.Date;
import java.util.Properties;

public final class TransactionManager {

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

    public static TransactionManager create(Device device, Properties loginProperties) {
        return new TransactionManager(null, "");
    }

    /**
     * Create a new transaction for a given logic. Depending on the approval count of the logic, either a
     * DB transaction or a Pseudo-transaction will be created.
     *
     * @param logic Logic
     * @return Newly created transaction.
     * @throws Exception Any exception.
     */
    public Transaction createTransaction(Logic logic) throws Exception {
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
        return new PseudoTransaction(this);
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

    public int transact(Logic logic, Transact transact) throws Exception {
        return 0;
    }

    public int transact(Transact transact) throws Exception {
        return 0;
    }

    public int transactPsuedo(Transact transact) throws Exception {
        return 0;
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
}