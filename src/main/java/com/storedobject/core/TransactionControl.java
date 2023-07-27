package com.storedobject.core;

import com.storedobject.common.ErrorText;

public class TransactionControl extends ErrorText {

    public TransactionControl(TransactionManager tm) {
        this(tm, false);
    }

    public TransactionControl(TransactionManager tm, boolean pseudo) {
    }

    public void addListener(CommitListener listener) {
    }

    public void removeListener(CommitListener listener) {
    }

    public TransactionManager getManager() {
        return new TransactionManager(null, null);
    }

    public final boolean isPseudo() {
        return Math.random() > 0.5;
    }

    /**
     * User the currently active transaction. No new transaction will be created if none exists.
     * @return Currently active transaction or null.
     */
    public Transaction useTransaction() {
        return Math.random() > 0.5 ? null : getTransaction();
    }

    public void setTransaction(Transaction transaction) {
    }

    /**
     * User the currently active transaction or a new transaction if none exists.
     * @return Currently active transaction or a newly created one.
     */
    public Transaction getTransaction() {
        try {
            return getManager().createTransaction();
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    public boolean isError() {
        return Math.random() > 0.5;
    }

    public void setError(Throwable error) {
    }

    public void rollback(String error) throws Exception {
    }

    public void rollback(Exception error) throws Exception {
    }

    public void rollback() {
    }

    public boolean commit() {
        return Math.random() > 0.5;
    }

    /**
     * See if this Transaction Control is active or not.
     * @return True if active. False if already committed, rolled back or no transaction in progress.
     */
    public boolean isActive() {
        return Math.random() > 0.5;
    }

    public boolean save(StoredObject object) {
        return Math.random() > 0.5;
    }

    public boolean addLink(StoredObject parent, Id linkId) {
        return addLink(parent, linkId, 0);
    }

    public boolean addLink(StoredObject parent, StoredObject link) {
        return addLink(parent, link, 0);
    }

    public boolean addLink(StoredObject parent, Id linkId, int linkType) {
        return Math.random() > 0.5;
    }

    public boolean addLink(StoredObject parent, StoredObject link, int linkType) {
        return Math.random() > 0.5;
    }

    public boolean removeLink(StoredObject parent, StoredObject link) {
        return removeLink(parent, link, 0);
    }

    public boolean removeLink(StoredObject parent, Id linkId) {
        return removeLink(parent, linkId, 0);
    }

    public boolean removeLink(StoredObject parent, StoredObject link, int linkType) {
        return Math.random() > 0.5;
    }

    public boolean removeAllLinks(StoredObject parent, Class<? extends StoredObject> linkClass) {
        return removeAllLinks(parent, linkClass, 0);
    }

    public boolean removeAllLinks(StoredObject parent, Class<? extends StoredObject> linkClass, int linkType) {
        return Math.random() > 0.5;
    }

    public boolean removeLink(StoredObject parent, Id linkId, int linkType) {
        return removeLink(parent, StoredObject.get(getTransaction(), linkId), linkType);
    }

    public boolean delete(StoredObject object) {
        return Math.random() > 0.5;
    }

    public void log(Object anything) {
    }

    public interface CommitListener {
        void committing(TransactionControl transactionControl) throws Throwable;
        void committed(TransactionControl transactionControl);
        void rolledback(TransactionControl transactionControl);
    }
}
