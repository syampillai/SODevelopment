package com.storedobject.core;

public interface MessageViewer {
	public TransactionManager getTransactionManager();
    public void alert(String message);
	public void message(LoginMessage message);
}