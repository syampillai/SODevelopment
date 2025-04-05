package com.storedobject.ui.tools;

import com.storedobject.common.StringList;
import com.storedobject.core.SQLConnector;
import com.storedobject.core.SystemUser;
import com.storedobject.platform.Application;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.Transactional;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.ComboField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.RadioChoiceField;

public class ManageApplication extends DataForm implements Transactional {

    private final ComboField<String> host = new ComboField<>("Server", Application.listHostNames());
    private final ComboField<Application> application = new ComboField<>("Application");
    private final ELabelField status = new ELabelField("Status");
    private final RadioChoiceField action = new RadioChoiceField("Action",
            StringList.create("Reload", "Start", "Stop"));

    public ManageApplication() {
        super("Manage Applications");
        setRequired(host);
        host.addValueChangeListener(e -> application.setItems(Application.list(host.getValue())));
        setRequired(application);
        SystemUser su = getTransactionManager().getUser();
        application.addValueChangeListener(e -> {
            Application a = application.getValue();
            status.clearContent();
            if(a != null) {
                status.append(a.isRunning() ? "Running" : "Not running");
            }
            status.update();
        });
        addField(host, application, status, action);
        if(su.isAdmin()) {
            host.setValue(SOServlet.getServer());
            host.setEnabled(false);
        }
    }

    @Override
    protected boolean process() {
        clearAlerts();
        Application a = application.getValue();
        try {
            switch (action.getValue()) {
                case 0 -> {
                    close();
                    if (a.isRunning()) {
                        act(a, true);
                        message(a.getName() + " - reloaded successfully.");
                    } else {
                        a.start();
                        message(a.getName() + " - started successfully.");
                    }
                }
                case 1 -> {
                    if (a.isRunning()) {
                        message(a.getName() + " - already running.");
                        return false;
                    }
                    close();
                    a.start();
                    message(a.getName() + " - started successfully.");
                }
                case 2 -> {
                    if (!a.isRunning()) {
                        message(a.getName() + " - already stopped.");
                        return false;
                    }
                    close();
                    act(a, false);
                    message(a.getName() + " - stopped successfully.");
                }
            }
        } catch (Exception e) {
            error(e);
        }
        return true;
    }

    private static boolean ours(Application a) {
        return a.getName().equals(SOServlet.getServer() + "/" + SQLConnector.getDatabaseName());
    }

    private void act(Application a, boolean reload) throws Exception {
        if(ours(a)) {
            Thread.startVirtualThread(() -> {
                try {
                    Thread.sleep(2000);
                    if(reload) {
                        a.reload();
                    } else {
                        a.stop();
                    }
                } catch (Exception ignored) {
                }
            });
            com.storedobject.ui.Application.get().close(200);
        } else {
            if(reload) {
                a.reload();
            } else {
                a.stop();
            }
        }
    }
}