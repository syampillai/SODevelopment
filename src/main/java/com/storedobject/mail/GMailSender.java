package com.storedobject.mail;

import com.storedobject.core.Columns;

public class GMailSender extends Sender {

	public GMailSender() {
	}

	public static void columns(Columns columns) {
	}

	public void setClientId(String clientId) {
	}

	public String getClientId() {
		return null;
	}

	public void setClientSecret(String clientSecret) {
	}

	public String getClientSecret() {
		return null;
	}

	public void setRefreshToken(String refreshToken) {
	}

	public String getRefreshToken() {
		return null;
	}

	public String getApplicationURI() {
		return null;
	}

	public void setApplicationURI(String applicationURI) {
	}

	@Override
	public boolean canSend() {
		return false;
	}
}
