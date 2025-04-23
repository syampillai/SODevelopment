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

public class ApplicationManager extends DataForm implements Transactional {

    private final ComboField<String> host = new ComboField<>("Server", Application.listHostNames());
    private final ComboField<Application> application = new ComboField<>("Application");
    private final ELabelField status = new ELabelField("Status");
    private final RadioChoiceField action = new RadioChoiceField("Action",
            StringList.create("Reload", "Start", "Stop"));

    public ApplicationManager() {
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
        SystemUser su = getTransactionManager().getUser();
        String  by = a + " - " + action.getValue() + " - " + su.getId()
                + " - " + su.getName() + " - " + SOServlet.getServer() + " - " + SQLConnector.getDatabaseName();
        try {
            switch (action.getValue()) {
                case 0 -> {
                    close();
                    if (a.isRunning()) {
                        if(act(a, true, by)) {
                            message(a.getName() + " - reloaded successfully.");
                        }
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
                    if(act(a, false, by)) {
                        message(a.getName() + " - stopped successfully.");
                    }
                }
            }
        } catch (Exception e) {
            error(e);
        }
        return true;
    }

    private static boolean ours(Application a) {
        return a.toString().equals(SOServlet.getServer() + "/" + SQLConnector.getDatabaseName());
    }

    private boolean act(Application a, boolean reload, String by) throws Exception {
        if(ours(a)) {
            com.storedobject.ui.Application app = com.storedobject.ui.Application.get();
            Thread.startVirtualThread(() -> {
                try {
                    Thread.sleep(2000);
                    String result = doAct(a, reload, by);
                    if(result != null) {
                        app.access(() -> warning(result));
                    }
                } catch (Exception ignored) {
                }
            });
            message("Action: " + (reload ? "Reload" : "Stop") + "\nYour screen may stop responding if successful");
            return false;
        }
        String result = doAct(a, reload, by);
        if(result == null) {
            return true;
        }
        warning(result);
        return false;
    }

    private String doAct(Application a, boolean reload, String by) throws Exception {
        return reload ? a.reload(by) : a.stop(by);
    }
}