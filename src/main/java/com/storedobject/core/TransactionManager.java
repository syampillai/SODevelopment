package com.storedobject.core;

import java.util.Currency;
import java.util.Date;
import java.util.Properties;

public final class TransactionManager {

    public TransactionManager(Device device, String login) {
    }

    public void reinit(String password) throws Exception {
    }

    public final Device getDevice() {
        return null;
    }

    public static TransactionManager create(Device device, Properties loginProperties) {
        return null;
    }

    public Id getSession() {
        return null;
    }

    /**
     * Start a new transaction.
     *
     * @return Newly created transaction.
     * @throws Exception Any exception.
     */
    public Transaction createTransaction() throws Exception {
        return null;
    }

    public PseudoTransaction getPseudoTransaction(Object key, boolean create) {
        return null;
    }

    public boolean verify(String password) {
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

    public void transact(Transact transact) throws Exception {
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