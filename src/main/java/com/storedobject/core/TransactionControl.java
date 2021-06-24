package com.storedobject.core;

import com.storedobject.common.ErrorText;

public class TransactionControl extends ErrorText {

    public TransactionControl(com.storedobject.core.TransactionManager p1) {
        this();
    }

    private TransactionControl() {
    }

    public com.storedobject.core.TransactionControl clear() {
        return null;
    }

    public boolean save(com.storedobject.core.StoredObject p1) {
        return false;
    }

    public boolean delete(com.storedobject.core.StoredObject p1) {
        return false;
    }

    public boolean isError() {
        return false;
    }
    
	public Transaction useTransaction() {
		return null;
	}
	
    public com.storedobject.core.Transaction getTransaction() {
        return null;
    }

    public com.storedobject.core.TransactionManager getManager() {
        return null;
    }

    public void setTransaction(com.storedobject.core.Transaction p1) {
    }

	public void rollback(String error) throws Exception {
	}
	
	public void rollback(Exception error) throws Exception {
	}
	
    public void rollback() {
    }

    public boolean commit() {
        return false;
    }

    public boolean addLink(com.storedobject.core.StoredObject p1, com.storedobject.core.Id p2) {
        return false;
    }

    public boolean addLink(com.storedobject.core.StoredObject p1, com.storedobject.core.StoredObject p2) {
        return false;
    }

    public boolean addLink(com.storedobject.core.StoredObject p1, com.storedobject.core.Id p2, int p3) {
        return false;
    }

    public boolean addLink(com.storedobject.core.StoredObject p1, com.storedobject.core.StoredObject p2, int p3) {
        return false;
    }

    public boolean removeLink(com.storedobject.core.StoredObject p1, com.storedobject.core.StoredObject p2) {
        return false;
    }

    public boolean removeLink(com.storedobject.core.StoredObject p1, com.storedobject.core.Id p2) {
        return false;
    }

    public boolean removeLink(com.storedobject.core.StoredObject p1, com.storedobject.core.StoredObject p2, int p3) {
        return false;
    }

    public boolean removeLink(com.storedobject.core.StoredObject p1, com.storedobject.core.Id p2, int p3) {
        return false;
    }
    
	public boolean removeAllLinks(StoredObject parent, Class<? extends StoredObject> linkClass) {
		return false;
	}
	
	public boolean removeAllLinks(StoredObject parent, Class<? extends StoredObject> linkClass, int linkType) {
		return false;
	}

    public boolean isActive() {
        return false;
    }

    public void addListener(CommitListener p1) {
    }

    public void removeListener(CommitListener p1) {
    }
    public interface CommitListener {

        public void rolledback(com.storedobject.core.TransactionControl p1);

        public void committing(com.storedobject.core.TransactionControl p1) throws java.lang.Throwable;

        public void committed(com.storedobject.core.TransactionControl p1);
    }
}
