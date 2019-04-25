package com.storedobject.core;

public final class Transaction {

	public Exception getError() {
		return null;
	}

	public Id getId() {
		return null;
	}

	public Id getUserId() {
		return null;
	}

	public TransactionManager getManager() {
		return null;
	}

	public <T extends StoredObject> T get(T object) {
		return null;
	}

	public <T extends StoredObject> T get(Class<T> objectClass, Id objectId) {
		return null;
	}

	public boolean isInvolved(Id id) {
		return false;
	}

	public boolean isInvolved(StoredObject object) {
		return false;
	}

	public void debit(Account account, Money amount, String narration) throws Exception {
	}

	public void debit(Account account, Money amount, Money localCurrencyAmount, String narration) throws Exception {
	}

	public void credit(Account account, Money amount, String narration) throws Exception {
	}

	public void credit(Account account, Money amount, Money localCurrencyAmount, String narration) throws Exception {
	}

	public void commit() throws Exception {
	}

	public void rollback() {
	}

	public boolean isActive() {
		return false;
	}

	@SuppressWarnings("serial")
	static class Transaction_Closed extends Exception {
	}
}
