package com.storedobject.core;

import com.storedobject.common.SOException;

import java.math.BigDecimal;
import java.sql.Date;

public final class PIN extends StoredObject {

	public PIN(Id ownerId, String type) {
	}
	
	public PIN() {
	}
	
	public static void columns(Columns columns) {
	}
	
	public static PIN get(Id owner, String type) {
		return new PIN();
	}
	
	public String getType() {
		return "";
	}
	
	public void setType(String type) {
	}
	
	boolean isOTP() {
		return false;
	}
	
	public Id getOwnerId() {
		return new Id();
	}
	
	public void setOwner(BigDecimal idValue) {
	}
	
	public StoredObject getOwner() {
		return new SystemUser();
	}

	public void setStatus(int status) {
	}

	public int getStatus() {
		return 0;
	}

	public void setExpiry(Date expiry) {
	}

	public Date getExpiry() {
		return DateUtility.today();
	}

	public boolean isExpired() {
		return false;
	}

	public void validateNewPIN(char[] currentPIN, char[] newPIN) throws SOException {
	}

	public void changePIN(char[] currentPIN, char[] newPIN) throws SOException {
	}

	public void resetPIN() throws Exception {
	}

	public boolean verify(char[] pin, boolean ignoreAuthenticator) {
		return false;
	}

	public boolean verify(char[] pin, int authenticatorCode) {
		return false;
	}

	public static boolean verify(Id ownerId, String type, char[] pin) {
		return false;
	}
	
	public PasswordPolicy getPolicy() {
		return new PasswordPolicy();
	}
}