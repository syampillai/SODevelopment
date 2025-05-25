package com.storedobject.mail;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.storedobject.core.ApplicationServer;
import com.storedobject.common.JSON;
import com.storedobject.core.ParameterService;
import com.storedobject.common.SOException;
import com.storedobject.core.SQLConnector;
import com.storedobject.core.StoredObject;
import com.storedobject.common.StringList;
import com.storedobject.core.TransactionManager;

public class GMailRegistrationService implements ParameterService {
	
	private TransactionManager tm;
	private static final StringList parameters = StringList.create("state", "code");
	
	@Override
	public String getName() {
		return "GMail Registration Service";
	}

	@Override
	public void setTransactionManager(TransactionManager tm) {
		this.tm = tm;
	}

	@Override
	public StringList getMandatoryParameters() {
		return parameters;
	}

	@Override
	public boolean requiresPOST(ApplicationServer applicationServer) {
		return false;
	}

	private String param(Map<String, String[]> parameters, String key) {
		String[] p = parameters.get(key);
		return (p == null || p.length != 1) ? "" : p[0];
	}

	@Override
	public String serve(Map<String, String[]> parameters) throws Exception {
		String code = param(parameters, GMailRegistrationService.parameters.get(1));
		if(code.isEmpty()) {
			return UNAUTHORIZED;
		}
		String state = param(parameters, GMailRegistrationService.parameters.get(0));
		List<GMailSender> gmss = StoredObject.list(GMailSender.class, "RefreshToken='State/" + state + "'")
				.toList();
		if(gmss.isEmpty()) {
			return UNAUTHORIZED;
		}
		String u = "client_id=" + URLEncoder.encode(gmss.getFirst().getClientId(), StandardCharsets.UTF_8) +
				"&client_secret=" + URLEncoder.encode(gmss.getFirst().getClientSecret(), StandardCharsets.UTF_8) +
				"&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
				"&redirect_uri=" +
				URLEncoder.encode(gmss.getFirst().getApplicationURI() + "/" + SQLConnector.getDatabaseName()
						+ "/SCHEDULER", StandardCharsets.UTF_8) +
				"&grant_type=authorization_code";
		JSON json = getJson(u);
		StoredObject.logger.info(json.toString());
		String token = json.getString("refresh_token");
		gmss.forEach(s -> s.setRefreshToken(token));
		for(GMailSender gms: gmss) {
			tm.transact(gms::save);
		}
		return OK;
	}

	static JSON getJson(String u) throws IOException, SOException, URISyntaxException {
		byte[] ub = u.getBytes(StandardCharsets.UTF_8);
		HttpURLConnection uc = (HttpURLConnection) (new URI("https://accounts.google.com/o/oauth2/token")
				.toURL().openConnection());
		uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		uc.setRequestProperty("Content-Length", "" + ub.length);
		uc.setDoInput(true);
		uc.setDoOutput(true);
		uc.setRequestMethod("POST");
		OutputStream out = uc.getOutputStream();
		out.write(ub);
		out.flush();
		if(uc.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new SOException(uc.getResponseMessage());
		}
		return new JSON(uc.getInputStream());
	}
}
