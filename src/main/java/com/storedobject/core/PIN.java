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
		return null;
	}
	
	public String getType() {
		return null;
	}
	
	public void setType(String type) {
	}
	
	boolean isOTP() {
		return false;
	}
	
	public Id getOwnerId() {
		return null;
	}
	
	public void setOwner(BigDecimal idValue) {
	}
	
	public StoredObject getOwner() {
		return null;
	}

	public void setStatus(int status) {
	}

	public int getStatus() {
		return 0;
	}

	public void setExpiry(Date expiry) {
	}

	public Date getExpiry() {
		return null;
	}

	public boolean isExpired() {
		return false;
	}

	public String getPIN() {
		return null;
	}
	
	public void setPIN(String pin) {
	}

	public void validateNewPIN(String currentPIN, String newPIN) throws SOException {
	}

	public void changePIN(String currentPIN, String newPIN) throws SOException {
	}

	public void resetPIN() throws Exception {
	}

	public boolean verify(String pin) {
		return false;
	}
	
	public static boolean verify(Id ownerId, String type, String pin) {
		return false;
	}
	
	public PasswordPolicy getPolicy() {
		return null;
	}
}