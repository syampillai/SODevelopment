package com.storedobject.core;

import com.storedobject.common.SOException;

import java.math.BigDecimal;

public final class PIN extends StoredObject {
	
	public PIN(Id ownerId, String type, String pin) {
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
	
	public String getPIN() {
		return null;
	}
	
	public void setPIN(String pin) {
	}
	
	public void changePIN(String currentPIN, String newPIN, int minLength) throws SOException {
	}
	
	public void changePIN(String currentPIN, String newPIN) throws SOException {
	}
	
	public boolean verify(String pin) {
		return false;
	}
	
	public static boolean verify(Id ownerId, String type, String pin) {
		return false;
	}
	
	public static String getPasswordCondition() {
		return null;
	}
}