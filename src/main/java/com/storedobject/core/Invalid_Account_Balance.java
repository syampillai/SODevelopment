package com.storedobject.core;

public class Invalid_Account_Balance extends Database_Id_Message {
	
	public Invalid_Account_Balance(String id) {
		super(id);
	}

	@Override
	protected String displayTail(String tail) {
		return "Balance: " + tail;
	}
}

