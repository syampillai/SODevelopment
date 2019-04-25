package com.storedobject.sms;

import com.storedobject.core.Columns;
import com.storedobject.core.Indices;
import com.storedobject.core.Message;

public class SMSMessage extends Message {

	public SMSMessage() {
	}

	public SMSMessage(long mobileNumber, String message) {
	}

	public static void columns(Columns columns) {
	}

	public static void indices(Indices indices) {
	}

	public void setMobileNumber(long mobileNumber) {
	}

	public long getMobileNumber() {
		return 0;
	}

	public void setErrorValue(String errorValue) {
	}
	
	public static String[] getErrorValues() {
        return null;
	}

	public static String getErrorValue(int value) {
        return null;
	}

	public String getErrorValue() {
        return null;
	}
	
	public String toString() {
        return null;
	}

	public void validateData() throws Exception {
	}
}
