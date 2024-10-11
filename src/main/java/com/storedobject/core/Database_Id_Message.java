package com.storedobject.core;

public abstract class Database_Id_Message extends Database_Message {

	private final String id;
	private final String tail;
	private Account account;
	
	public Database_Id_Message(String id) {
		int p = id.indexOf(',');
		if(p > 0) {
			tail = id.substring(p + 1).trim();
			id = id.substring(0, p);
		} else {
			tail = null;
		}
		this.id = id;
	}
	
	protected String getCustomMessage() {
		return tail == null ? displayId(getId()) : (displayId(getId()) + ", " + displayTail(tail));
	}

	public Id getId() {
		return new Id(id);
	}

	String getTail() {
		return tail;
	}

	protected String displayId(Id id) {
		Account a = getAccount();
		return a == null ? id.toString() : a.toDisplay();
	}

	protected String displayTail(String tail) {
		return tail;
	}

	protected Account getAccount() {
		Id id = getId();
		if(account == null || !account.getId().equals(id)) {
			account = StoredObject.get(Account.class, id, true);
		}
		return account;
	}
}
