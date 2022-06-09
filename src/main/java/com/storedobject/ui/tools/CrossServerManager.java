package com.storedobject.ui.tools;

import com.storedobject.core.SQLConnector;
import com.storedobject.core.ServerInformation;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.TextField;

public class CrossServerManager extends ObjectBrowser<ServerInformation> {

    public CrossServerManager() {
        super(ServerInformation.class);
        setFixedFilter("lower(Name)<>'" + SQLConnector.getDatabaseName().toLowerCase() + "'", true);
        setCaption("External Servers");
    }

    @Override
    protected ObjectEditor<ServerInformation> createObjectEditor() {
        return new Editor();
    }

    private static class Editor extends ObjectEditor<ServerInformation> {

        public Editor() {
            super(ServerInformation.class);
            addConstructedListener(f -> {
                TextField tf = (TextField) getField("Name");
                addValidator(tf, ServerInformation::checkServerName);
            });
        }

        @Override
        protected boolean save() throws Exception {
            ServerInformation si = getObject();
            ServerInformation.createServer(getTransactionManager(), si.getName(), si.getDescription(),
                    si.created() ? SOServlet.getURL() : null);
            return true;
        }
    }
}
