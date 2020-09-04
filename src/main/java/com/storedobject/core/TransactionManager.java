package com.storedobject.core;

import java.util.Currency;
import java.util.Date;
import java.util.Properties;

public final class TransactionManager {

    public TransactionManager(Device device, String login) {
    }

    public void reinit(char[] password) throws Exception {
    }

    /**
     * Gets Session Id.
     *
     * @return Session Id.
     */
    public Id getSession() {
        return null;
    }

    public Device getDevice() {
        return null;
    }

    public boolean needsApprovals() {
        return false;
    }

    public static TransactionManager create(Device device, Properties loginProperties) {
        return null;
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
        return null;
    }

    public boolean verify(char[] password) {
        return false;
    }

    public SystemUser getUser() {
        return null;
    }

    public void setEntity(SystemEntity entity) {
    }

    public SystemEntity getEntity() {
        return null;
    }

    public Currency getCurrency() {
        return null;
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
        return null;
    }

    public Id checkType(StoredObject host, Id id, Class<? extends StoredObject> objectClass) throws Exception {
        return null;
    }

    public Id checkType(StoredObject host, Id id, Class<? extends StoredObject> objectClass, boolean allowEmpty) throws Exception {
        return null;
    }

    public Id checkTypeAny(StoredObject host, Id id, Class<? extends StoredObject> objectClass) throws Exception {
        return null;
    }

    public Id checkTypeAny(StoredObject host, Id id, Class<? extends StoredObject> objectClass, boolean allowEmpty) throws Exception {
        return null;
    }
}