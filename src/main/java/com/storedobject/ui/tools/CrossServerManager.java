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

    private final Button connect = new Button("Define Connection", VaadinIcon.CLUSTER, e -> connect(false));
    private String connectTo;
    private ConnectForm connectForm;
    private boolean adding = false;

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
        adding = false;
        warning("Editing server links - Please makes sure that you make " +
                "the respective changes on the other servers too.");
        return true;
    }

    @Override
    protected boolean canAdd() {
        if(connectTo == null) {
            connect(true);
            return false;
        }
        adding = true;
        return true;
    }

    private class Editor extends ObjectEditor<ServerInformation> {

        public Editor() {
            super(ServerInformation.class);
            addConstructedListener(f -> {
                TextField tf = (TextField) getField("Name");
                addValidator(tf, ServerInformation::checkServerName);
            });
        }

        @Override
        protected boolean save() throws Exception {
            if(!adding) {
                return super.save();
            }
            ServerInformation si = getObject();
            ServerInformation.createServer(getTransactionManager(), si.getName(), si.getDescription(),
                    connectTo, ServerLink.trim(Application.get().getURL()));
            return true;
        }
    }

    private void connect(boolean toAdd) {
        if(connectForm == null) {
            connectForm = new ConnectForm();
        }
        connectForm.toAdd = toAdd;
        connectForm.execute();
    }

    private class ConnectForm extends DataForm {

        private final TextField connectToApp = new TextField("Connect to");
        private boolean toAdd;

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
            if(toAdd) {
                doAdd();
            }
            return true;
        }
    }
}
