package com.storedobject.core;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTransaction implements Transaction {

    private boolean closed = false;
    private final TransactionManager tm;
    Id tranId;
    Exception error;
    private List<CommitListener> listeners;

    // For internal use only.
    AbstractTransaction(TransactionManager tm) {
        this.tm = tm;
    }

    @Override
    public void addCommitListener(CommitListener listener) {
        if(listener != null) {
            if (listeners == null) {
                listeners = new ArrayList<>();
            }
            listeners.add(listener);
        }
    }

    @Override
    public void removeCommitListener(CommitListener listener) {
        if(listener != null && listeners != null) {
            listeners.remove(listener);
        }
    }

    // For internal use only.
    boolean errorWhileCommitting() {
        if(listeners != null) {
            for(CommitListener listener: listeners) {
                try {
                    listener.committing(this);
                } catch (Throwable throwable) {
                    setError(throwable);
                    rollback();
                    return true;
                }
            }
        }
        return false;
    }

    // For internal use only.
    void fireCommitted() {
        if(listeners != null) {
            listeners.forEach(listener -> listener.committed(this));
        }
    }

    // For internal use only.
    void fireRolledBack() {
        if(listeners != null) {
            listeners.forEach(listener -> listener.rolledback(this));
        }
    }

    // For internal use only.
    RawSQL getSQL() {
        return new RawSQL();
    }

    /**
     * Gets the current error in Transaction.
     * A transaction that was closed normally (committed) does not return any error.
     *
     * @return Error if any, otherwise null.
     */
    @Override
    public final Exception getError() {
        return error;
    }

    /**
     * Gets the Id of this transaction.
     *
     * @return Id
     */
    @Override
    public final Id getId() {
        return tranId;
    }

    @Override
    public final int hashCode() {
        return tranId.hashCode();
    }

    /**
     * Gets the TransactionManager associated with this Transaction.
     *
     * @return TransactionManager
     */
    @Override
    public final TransactionManager getManager() {
        return tm;
    }

    // For internal use only.
    final void setError(Throwable error) {
        if(this.error != null) {
            return;
        }
        if(error instanceof Exception) {
            this.error = (Exception)error;
        } else {
            this.error = new Exception(error);
        }
    }

    // For internal use only.
    void close() {
        closed = true;
    }

    // For internal use only.
    void error() throws Exception {
        if(error == null) {
            if(closed) {
                throw new Transaction_Closed();
            }
            return;
        }
        rollback();
        if(error != null) {
            throw error;
        }
        noService();
    }

    void noService() throws Transaction_Error {
        throw new Transaction_Error(null, "Service not available");
    }

    private static class Transaction_Closed extends Exception {
    }

    @Override
    public String toString() {
        return "Tran = " + tranId;
    }

    /**
     * Log something.
     * @param anything Anything, including exceptions, to be logged.
     */
    public void log(Object anything) {
        getManager().log(anything);
    }
}
