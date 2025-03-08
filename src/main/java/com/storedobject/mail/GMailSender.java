package com.storedobject.mail;

import com.storedobject.common.JSON;
import com.storedobject.common.SOException;
import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.security.Security;
import java.util.Properties;

public class GMailSender extends Sender {

	static {
		Security.addProvider(new OAuth2Provider());
	}
	private String clientId, clientSecret, refeshToken, applicationURI;
	private String token = null;
	private long tokenExpiry = -1;

	public GMailSender() {
		refeshToken = "State/";
	}

	public static void columns(Columns columns) {
		columns.add("ClientId", "text");
		columns.add("ClientSecret", "text");
		columns.add("RefreshToken", "text");
		columns.add("ApplicationURI", "text");
	}

	public static int hints() {
		return ObjectHint.SMALL_LIST;
	}
	
    public static String[] protectedColumns() {
        return new String[] { "ClientId", "ClientSecret", "RefreshToken" };
    }

	public static String[] links() {
		return new String[] {
			"Errors|com.storedobject.mail.Error||",
		};
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Column(order = 1300, style = "(secret)")
	public String getClientId() {
		return clientId;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	@Column(order = 1400, style = "(secret)")
	public String getClientSecret() {
		return clientSecret;
	}

	public void setRefreshToken(String refreshToken) {
		this.refeshToken = refreshToken;
	}

	@Column(order = 1500)
	public String getRefreshToken() {
		return refeshToken;
	}

	@Column(order = 1600)
	public String getApplicationURI() {
		return applicationURI;
	}

	public void setApplicationURI(String applicationURI) {
		this.applicationURI = applicationURI;
	}

	@Override
	public void validateData(TransactionManager tm) throws Exception {
		if(StringUtility.isWhite(clientId)) {
			throw new Invalid_Value("Client Id");
		}
		if(StringUtility.isWhite(clientSecret)) {
			throw new Invalid_Value("Client Secret");
		}
		if(StringUtility.isWhite(refeshToken)) {
			throw new Invalid_Value("Refresh Token");
		}
		applicationURI = StringUtility.pack(applicationURI);
		if(!applicationURI.startsWith("https://") || applicationURI.startsWith("https://.")
				|| applicationURI.endsWith(".") || StringUtility.getCharCount(applicationURI, '.') > 2
				|| applicationURI.contains("..") ||
				!StringUtility.isLetterOrDigit(applicationURI.substring(8).replace(".", ""))) {
			throw new Invalid_Value("Application URI");
		}
		super.validateData(tm);
	}
	
	@Override
	public boolean canSend() {
		return super.canSend() && !refeshToken.startsWith("State/");
	}

	@Override
	protected void createTransport() throws MessagingException {
		if(System.currentTimeMillis() > tokenExpiry || token == null) {
			if(transport != null) {
				try {
					transport.close();
				} catch(Exception ignored) {
				}
				transport = null;
			}
			StringBuilder u = new StringBuilder();
			try {
				u.append("client_id=").append(URLEncoder.encode(clientId, StandardCharsets.UTF_8));
				u.append("&client_secret=").append(URLEncoder.encode(clientSecret, StandardCharsets.UTF_8));
				u.append("&refresh_token=").append(URLEncoder.encode(refeshToken, StandardCharsets.UTF_8));
				u.append("&grant_type=refresh_token");
				JSON json = getJson(u);
				token = json.getString("access_token");
				tokenExpiry = System.currentTimeMillis() + ((json.getNumber("expires_in").intValue() - 10) * 1000L);
			} catch (Exception e) {
				ApplicationServer.log(e);
				throw new MessagingException("Unable to generate Access Token", e);
			}
		}
		if(transport != null) {
			return;
		}
		Properties props = new Properties();
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.starttls.required", "true");
	    props.put("mail.smtp.sasl.enable", "true");
	    props.put("mail.smtp.sasl.mechanisms", "XOAUTH2");
	    props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, token);
	    Session session = Session.getInstance(props);
	    transport = session.getTransport("smtp");
	    transport.connect("smtp.gmail.com", 587, getFromAddress(), "");
	}

	private static JSON getJson(StringBuilder u) throws IOException, SOException, URISyntaxException {
		return GMailRegistrationService.getJson(u.toString());
	}

	private static final class OAuth2Provider extends Provider {

		public OAuth2Provider() {
			super("Google OAuth2 Provider", "1.0", "Provides the XOAUTH2 SASL Mechanism");
			put("SaslClientFactory.XOAUTH2", "com.storedobject.mail.OAuth2SaslClientFactory");
		}
	}
}
