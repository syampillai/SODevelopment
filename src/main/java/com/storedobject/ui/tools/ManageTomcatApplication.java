package com.storedobject.ui.tools;

import com.storedobject.common.HTTP;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.ApplicationServer;
import com.storedobject.core.Secret;
import com.storedobject.core.StringUtility;
import com.storedobject.core.SystemUser;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.CompoundField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.RadioField;
import com.storedobject.vaadin.TextField;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.security.cert.X509Certificate;

public class ManageTomcatApplication extends DataForm implements Transactional {

    private TextField server;
    private RadioField<String> action;
    private ELabel warning;
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
        warning = new ELabel("Your action may affect other live users!", "red");
        warning.update();
        addField(new CompoundField("Warning", warning));
    }

    @Override
    protected boolean process() {
        String a = server.getValue().trim().toLowerCase();
        if(a.isEmpty() || !StringUtility.isLetterOrDigit(a)) {
            warning.clearContent().append("Invalid application name", "red").update();
            warned = false;
            return false;
        }
        if(a.equals(getApplication().getLinkName()) && action.getIndex() == 1 && !warned) {
            warning.clearContent().append(
                    "You are about to stop this application! Press the 'Proceed' button again.", "red")
                    .update();
            warned = true;
            return false;
        }
        try {
            String manager = ApplicationServer.getGlobalProperty("application.manager",
                    "https://localhost:8443/manager");
            SSLContext sc = null;
            if(manager.startsWith("https://")) {
                sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[]{new TrustAllX509TrustManager()}, new java.security.SecureRandom());
            }
            HTTP http = new HTTP(manager + "/text/" + action.getValue().toLowerCase() + "?path=/" + a);
            http.setAllowHTTPErrors(true);
            if(sc != null) {
                http.setHostnameVerifier((host, session) -> true);
                http.setSSLSocketFactory(sc.getSocketFactory());
            }
            Secret.authenticate(http, ApplicationServer.getGlobalProperty("application.manager.user",
                    "soengine"), ApplicationServer.getGlobalProperty("application.manager.password",
                    "testme!always"));
            BufferedReader r = http.getReader();
            String ok = r.readLine();
            r.close();
            warning.clearContent().append(ok, ok.startsWith("OK") ? "blue" : "red").update();
        } catch (Exception e) {
            error(e);
            warning.clearContent().append("Error executing the requested action", "red").update();
        }
        warned = false;
        return false;
    }

    private static class TrustAllX509TrustManager implements X509TrustManager {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }
    }
}