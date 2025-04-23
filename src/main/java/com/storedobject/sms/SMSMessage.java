package com.storedobject.sms;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;

public class SMSMessage extends Message {

	private static final String[] errorValues = new String[] {
		"None", "Error - Retry", "Insufficient Balance", "Unknown Error", "Invalid Number",
	};
	private long mobileNumber;
	private boolean delivered;

	public SMSMessage() {
	}

	public SMSMessage(long mobileNumber, String message) {
		super(message);
		this.mobileNumber = mobileNumber;
	}

	public static void columns(Columns columns) {
		columns.add("MobileNumber", "long");
		columns.add("Delivered", "boolean");
	}

	public static void indices(Indices indices) {
		indices.add("MobileNumber,CreatedAt", false);
		indices.add("MobileNumber,Sent", "NOT Sent", false);
		indices.add("CreatedAt,Sent", "NOT Sent", false);
		indices.add("CreatedAt,Delivered", "Sent AND NOT Delivered AND Error IN (0,1)", false);
	}

	public void setMobileNumber(long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public long getMobileNumber() {
		return mobileNumber;
	}
	
	public static String[] getErrorValues() {
		return errorValues;
	}

	public static String getErrorValue(int value) {
		String[] s = getErrorValues();
		return s[value % s.length];
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
		if(delivered) {
			error = 0;
		}
	}

	@Column(order = 600)
	public boolean getDelivered() {
		return delivered;
	}

	public String getErrorValue() {
		return getErrorValue(error);
	}
	
	@Override
	public String toString() {
		return mobileNumber + " \"" + getMessage() + "\" /" + getMessageID() + "("
				+ DateUtility.format(getCreatedAt()) + ")";
	}

	protected int getMaxLength() {
		return 160;
	}

	@Override
	public void validateData(TransactionManager tm) throws Exception {
		if(mobileNumber <= 0L) {
			throw new Invalid_Value("Mobile Number");
		}
		String m = getMessage();
		if(StringUtility.isWhite(m) || m.length() > getMaxLength()) {
			throw new Invalid_Value("Message");
		}
		super.validateData(tm);
	}
}
