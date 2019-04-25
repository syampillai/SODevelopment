package com.storedobject.core;

import java.util.Date;

public final class TransactionManager {

    public TransactionManager(java.lang.String p1) {
        this();
    }

    private TransactionManager() {
    }

    public static com.storedobject.core.TransactionManager create(java.util.Properties p1) {
        return null;
    }

    public boolean verify(java.lang.String p1) {
        return false;
    }

    public java.util.Currency getCurrency() {
        return null;
    }

    public com.storedobject.core.SystemEntity getEntity() {
        return null;
    }

    public com.storedobject.core.Transaction createTransaction() throws java.lang.Exception {
        return null;
    }

    public void setEntity(com.storedobject.core.SystemEntity p1) {
    }

    public com.storedobject.core.SystemUser getUser() {
        return null;
    }

    public void reinit(java.lang.String p1) throws java.lang.Exception {
    }
    
	public static interface Transact {
		public void transact(Transaction transaction) throws Exception;
	}
	
	public void transact(Transact transact) throws Exception {
	}
	
	public String format(Date date) {
		return null;
	}
}
