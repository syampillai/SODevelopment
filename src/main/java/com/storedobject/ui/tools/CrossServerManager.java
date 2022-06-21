package com.storedobject.ui.tools;

import com.storedobject.core.SQLConnector;
import com.storedobject.core.ServerInformation;
import com.storedobject.core.ServerLink;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.icon.VaadinIcon;

public class CrossServerManager extends ObjectBrowser<ServerInformation> {

    private final Button connect = new Button("Define Connection", VaadinIcon.CLUSTER, e -> connect());
    private String connectTo;
    private ConnectForm connectForm;

    public CrossServerManager() {
        super(ServerInformation.class);
        setFixedFilter("lower(Name)<>'" + SQLConnector.getDatabaseName().toLowerCase() + "'", true);
        setCaption("External Servers");
    }

    @Override
    protected ObjectEditor<ServerInformation> createObjectEditor() {
        return new Editor();
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        buttonPanel.add(connect);
    }

    @Override
    public boolean canEdit(ServerInformation object) {
        if(connectTo == null) {
            connect(object);
            return false;
        }
        return true;
    }

    @Override
    protected boolean canAdd() {
        if(connectTo == null) {
            connect(null);
            return false;
        }
        return true;
    }

    private class Editor extends ObjectEditor<ServerInformation> {

        public Editor() {
            super(ServerInformation.class);
            addConstructedListener(f -> {
                TextField tf = (TextField) getField("Name");
                addValidator(tf, ServerInformation::checkServerName);
                setFieldReadOnly("Links.l");
            });
        }

        @Override
        protected boolean save() throws Exception {
            ServerInformation si = getObject();
            ServerInformation.createServer(getTransactionManager(), si.getName(), si.getDescription(),
                    connectTo, ServerLink.trim(Application.get().getURL()));
            return true;
        }
    }

    private void connect() {
        if(connectForm == null) {
            connectForm = new ConnectForm();
        }
        connectForm.process = false;
        connectForm.execute();
    }

    private void connect(ServerInformation server) {
        if(connectForm == null) {
            connectForm = new ConnectForm();
        }
        connectForm.process = true;
        connectForm.server = server;
        connectForm.execute();
    }

    private class ConnectForm extends DataForm {

        private final TextField connectToApp = new TextField("Connect to");
        private ServerInformation server;
        private boolean process;

        public ConnectForm() {
            super("Connection Information");
            TextField thisApp = new TextField("This Application");
            addField(thisApp, connectToApp);
            thisApp.setValue(ServerLink.trim(Application.get().getURL()));
            setFieldReadOnly(thisApp);
            setRequired(connectToApp);
            connectToApp.setValue(SOServlet.getURL());
        }

        @Override
        protected boolean process() {
            connectTo = ServerLink.trim(connectToApp.getValue());
            if(connectTo == null) {
                return false;
            }
            close();
            if(process) {
                if(server == null) {
                    doAdd();
                } else {
                    doEdit(server);
                }
            }
            return true;
        }
    }
}
