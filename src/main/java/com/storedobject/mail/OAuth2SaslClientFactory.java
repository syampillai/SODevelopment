package com.storedobject.mail;

import com.storedobject.core.StoredObject;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;
import java.io.IOException;
import java.util.Map;

public class OAuth2SaslClientFactory implements SaslClientFactory {

	public static final String OAUTH_TOKEN_PROP = "mail.smtp.sasl.mechanisms.oauth2.oauthToken";
	private static final String AUTH_MECHANISM = "XOAUTH2";

	public SaslClient createSaslClient(String[] mechanisms, String authorizationId, String protocol, String serverName,
			Map<String, ?> props, CallbackHandler callbackHandler) {
		boolean matchedMechanism = false;
		for(String mechanism : mechanisms) {
			if(AUTH_MECHANISM.equalsIgnoreCase(mechanism)) {
				matchedMechanism = true;
				break;
			}
		}
		if(!matchedMechanism) {
			StoredObject.logger.info("OAuth2 Sasl Client Factory: Failed to match any mechanisms");
			return null;
		}
		return new OAuth2SaslClient((String) props.get(OAUTH_TOKEN_PROP), callbackHandler);
	}

	public String[] getMechanismNames(Map<String, ?> props) {
		return new String[] { AUTH_MECHANISM };
	}
	
	private static class OAuth2SaslClient implements SaslClient {

		private final String oauthToken;
		private final CallbackHandler callbackHandler;
		private boolean isComplete = false;

		/**
		 * Creates a new instance of the OAuth2SaslClient. This will ordinarily only
		 * be called from OAuth2SaslClientFactory.
		 */
		public OAuth2SaslClient(String oauthToken, CallbackHandler callbackHandler) {
			this.oauthToken = oauthToken;
			this.callbackHandler = callbackHandler;
		}

		public String getMechanismName() {
			return AUTH_MECHANISM;
		}

		public boolean hasInitialResponse() {
			return true;
		}

		public byte[] evaluateChallenge(byte[] challenge) throws SaslException {
			if(isComplete) {
				// Empty final response from server, just ignore it.
				return new byte[] { };
			}
			NameCallback nameCallback = new NameCallback("Enter name");
			Callback[] callbacks = new Callback[] { nameCallback };
			try {
				callbackHandler.handle(callbacks);
			} catch (UnsupportedCallbackException e) {
				throw new SaslException("Unsupported callback: " + e);
			} catch (IOException e) {
				throw new SaslException("Failed to execute callback: " + e);
			}
			String email = nameCallback.getName();
			byte[] response = String.format("user=%s\1auth=Bearer %s\1\1", email, oauthToken).getBytes();
			isComplete = true;
			return response;
		}

		public boolean isComplete() {
			return isComplete;
		}

		public byte[] unwrap(byte[] incoming, int offset, int len) {
			throw new IllegalStateException();
		}

		public byte[] wrap(byte[] outgoing, int offset, int len) {
			throw new IllegalStateException();
		}

		public Object getNegotiatedProperty(String propName) {
			if (!isComplete()) {
				throw new IllegalStateException();
			}
			return null;
		}

		public void dispose() {
		}
	}
}
