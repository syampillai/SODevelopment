package com.storedobject.ui.tools;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.StringUtility;
import com.storedobject.core.SystemUser;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.CompoundField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;

import java.io.File;
import java.io.IOException;

public class ManageJettyApplication extends DataForm implements Transactional {

    private TextField server;

    public ManageJettyApplication() {
        super("Manage Applications", "Reload", "Exit");
        SystemUser su = getTransactionManager().getUser();
        if(su.isAdmin() || su.isAppAdmin()) {
            return;
        }
        throw new SORuntimeException("You don't have enough privilege to carry out this operation!");
    }

    @Override
    protected void buildFields() {
        server = new TextField("Application");
        setRequired(server);
        addField(server);
        ELabel warning = new ELabel("Your action may affect other live users!", "red");
        warning.update();
        addField(new CompoundField("Warning", warning));
    }

    @Override
    protected boolean process() {
        String a = server.getValue().trim().toLowerCase();
        if(a.isEmpty() || !StringUtility.isLetterOrDigit(a)) {
            warning("Invalid application name");
            return false;
        }
        String cwd = System.getProperty("user.dir") + File.separator;
        File f = new File(cwd + "webapps");
        if(!f.isDirectory() || !cwd.endsWith("/")) {
            warning("Configuration error, unable to determine application folders!");
            return false;
        }
        String af = cwd + "webapps" + File.separator + a;
        f = new File(af);
        if(!f.exists() || !f.isDirectory()) {
            warning("Application not found: " + a);
            return false;
        }
        f = new File(cwd + "moved");
        if(!f.exists() && !f.mkdir()) {
            warning("Unable to create shutdown hook, please contact Technical Support!");
            return false;
        }
        if(!f.isDirectory()) {
            warning("Configuration error, shutdown hook is not configured correctly!");
            return false;
        }
        String command = "/bin/mv " + cwd + "webapps/" + a + " " + cwd + "moved/;sleep 3;/bin/mv " +
                cwd + "moved/" + a + " " + cwd + "webapps";
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);
        try {
            Process p = pb.start();
            if(p.waitFor() == 0) {
                message("Application reloaded: " + a);
            } else {
                warning("Application reload process failed with a code: " + p.exitValue());
            }
        } catch(InterruptedException | IOException e) {
            error(e);
            return false;
        }
        return true;
    }
}