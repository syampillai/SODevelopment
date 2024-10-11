package com.storedobject.core;

public class Invalid_Account_Status extends Database_Id_Message {
	
	public Invalid_Account_Status(String id) {
		super(id);
	}

	@Override
	protected String displayTail(String tail) {
		Account account = getAccount();
		return account == null ? tail : ("Status: " + account.getStatusDescription());
	}
}