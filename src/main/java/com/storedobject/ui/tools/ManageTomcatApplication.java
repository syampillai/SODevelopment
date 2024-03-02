package com.storedobject.ui.tools;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.ApplicationServer;
import com.storedobject.core.Secret;
import com.storedobject.core.StringUtility;
import com.storedobject.core.SystemUser;
import com.storedobject.ui.Transactional;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.RadioField;
import com.storedobject.vaadin.TextArea;
import com.storedobject.vaadin.TextField;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.time.Duration;

public class ManageTomcatApplication extends DataForm implements Transactional {

    private TextField server;
    private RadioField<String> action;
    private final TextArea warning = new TextArea("Please Note:");
    private boolean warned = false;

    public ManageTomcatApplication() {
        super("Manage Applications", "Proceed", "Exit");
        SystemUser su = getTransactionManager().getUser();
        if(su.isAdmin() || su.isAppAdmin()) {
            return;
        }
        throw new SORuntimeException("You don't have enough privilege to carry out this operation!");
    }

    @Override
    protected void buildFields() {
        server = new TextField("Application");
        server.setRequired(true);
        addField(server);
        action = new RadioField<>("Action", new String[] { "Reload", "Stop", "Start" });
        addField(action);
        warning.setMaxHeight("50vh");
        warning.setText("Your action may affect other live users!");
        addField(warning);
        setFieldReadOnly(warning);
    }

    @Override
    protected boolean process() {
        System.setProperty("jdk.httpclient.allowRestrictedHeaders", "host");
        String a = server.getValue().trim().toLowerCase();
        if(a.isEmpty() || !StringUtility.isLetterOrDigit(a.replace('_', '0'))) {
            warning.setValue("Invalid application name");
            warned = false;
            return false;
        }
        if(a.equals(getApplication().getLinkName()) && action.getIndex() == 1 && !warned) {
            warning.setValue("You are about to stop this application! Press the 'Proceed' button again.");
            warned = true;
            return false;
        }
        try {
            SSLContext sc = null;
            String manager = "http";
            if(SOServlet.getURL().startsWith("https://")) {
                manager += "s";
                sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[] { new TrustAllX509TrustManager() },
                        new java.security.SecureRandom());
            }
            manager += "://localhost:8";
            manager += (sc != null ? "443" : "080") + "/manager/text/" + action.getValue().toLowerCase() + "?path=/" + a;
            HttpRequest request = HttpRequest.newBuilder(URI.create(manager)).header("Host", SOServlet.getServer())
                    .timeout(Duration.ofSeconds(20)).GET().build();
            String user = ApplicationServer.getGlobalProperty("application.manager.user", "soengine");
            String password = ApplicationServer.getGlobalProperty("application.manager.password",
                    "testme!always");
            HttpClient.Builder builder = HttpClient.newBuilder().authenticator(Secret.authenticator(user, password))
                    .connectTimeout(Duration.ofSeconds(20));
            if(sc != null) {
                builder.sslContext(sc);
            }
            HttpResponse<String> response = builder.build().send(request, HttpResponse.BodyHandlers.ofString());
            String result = null;
            if(response.statusCode() == HttpURLConnection.HTTP_OK) {
                result = response.body();
            }
            warning.setValue(result);
        } catch (Exception e) {
            error(e);
            warning.setValue("Error executing the requested action");
        }
        warned = false;
        return false;
    }

    private static class TrustAllX509TrustManager extends X509ExtendedTrustManager {

        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
        }
    }
}