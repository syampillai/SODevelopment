package com.storedobject.ui.tools;

import com.storedobject.common.ContentGenerator;
import com.storedobject.common.JSON;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.JSONField;
import com.storedobject.ui.PasswordField;
import com.storedobject.ui.Transactional;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.*;
import com.storedobject.client.Client;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.io.InputStream;
import java.util.Map;

public class SOClient extends View implements CloseableView, Transactional {

    private final TextField url = new TextField("Host");
    private final TextField app = new TextField("Application");
    private final TextField user = new TextField("User");
    private final PasswordField password = new PasswordField("Password (If not using API Key)");
    private final TextField apiKey = new TextField("API Key (Password not required if provided)");
    private final JSONField request = new JSONField("Request");
    private final TextArea response = new TextArea("Response");
    private final ViewOrDownload viewOrDownload = new ViewOrDownload();
    private Client client;

    public SOClient() {
        super("SO Client");
        ButtonLayout buttons = new ButtonLayout(
                new Button("Reconnect", VaadinIcon.CONNECT_O, e -> { reconnect(); send(); }),
                new Button("View/Download File/Stream", VaadinIcon.EYE, e -> viewOrDownload.execute()),
                new Button("Exit", e -> close())
        );
        Button send = new Button("Send", e -> send());
        FormLayout form = new FormLayout(url, app, user, password, apiKey, request,
                new CompoundField(send), response);
        form.setColumns(4);
        form.setColumnSpan(apiKey, 4);
        form.setColumnSpan(request, 4);
        form.setColumnSpan(response, 4);
        url.addValueChangeListener(e -> reconnect());
        user.addValueChangeListener(e -> reconnect());
        password.addValueChangeListener(e -> reconnect());
        apiKey.addValueChangeListener(e -> reconnect());
        url.setValue(trimURL(SOServlet.getURL()));
        app.setValue(SQLConnector.getDatabaseName());
        user.setValue(getTransactionManager().getUser().getLogin());
        response.setReadOnly(true);
        setComponent(new ContentWithHeader(buttons, form));
    }

    @Override
    public void clean() {
        super.clean();
        reconnect();
    }

    private static String trimURL(String url) {
        if(url == null) {
            return null;
        }
        url = url.trim();
        if(url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if(url.startsWith("http://")) {
            url = url.substring(7);
        }
        if(url.startsWith("https://")) {
            url = url.substring(8);
        }
        return url;
    }

    private void send() {
        if (client == null) {
            String url = trimURL(this.url.getText()),
                    user = this.user.getText().trim(),
                    app = this.app.getText().trim(),
                    password = this.password.getValue().trim(),
                    apiKey = this.apiKey.getText().trim();
            if (url.isEmpty() || app.isEmpty() || user.isEmpty()) {
                message("URL, application and user are required!");
                return;
            }
            if (!password.isEmpty() && !apiKey.isEmpty()) {
                message("Use either password or API Key");
                return;
            }
            client = new Client(url, app, apiKey, true);
            String error = client.login(user, apiKey.isEmpty() ? password : apiKey);
            if (error.isEmpty()) {
                message("Connected successfully!");
            } else {
                warning("Error: " + error);
                reconnect();
                return;
            }
        }
        response.setValue("");
        if(request.isEmpty()) {
            return;
        }
        JSON request = this.request.getValue();
        if (request == null || request.isNull()) {
            message("Invalid request!");
            return;
        }
        Map<String, Object> attributes;
        try {
            attributes = request.toMap();
        } catch (Throwable t) {
            attributes = null;
        }
        if (attributes == null) {
            message("Invalid request!");
            return;
        }
        String command = (String) attributes.get("command");
        if (command == null) {
            command = (String) attributes.get("method");
        }
        if (command == null) {
            message("Command is required!");
            return;
        }
        var result = client.command(command, attributes);
        response.setValue(new JSON(result).toPrettyString());
    }

    private void reconnect() {
        if(client != null) {
            client.close();
            client = null;
        }
    }

    private class ViewOrDownload extends DataForm {

        private final RadioChoiceField file = new RadioChoiceField("File/Stream", StringList.create("File", "Stream"));
        private final TextField fileName = new TextField("File Name or Stream Code");

        public ViewOrDownload() {
            super("Download File/Stream");
            addField(file, fileName);
            setRequired(file);
            setRequired(fileName);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            clearAlerts();
            if(client == null) {
                message("No connection!");
                return;
            }
            super.execute(parent, doNotLock);
        }

        @Override
        protected boolean process() {
            close();
            String fileName = this.fileName.getValue();
            Client.Data data;
            if(file.getValue() == 0) {
                data = client.file(fileName);
            } else {
                data = client.stream(fileName);
            }
            String error = data.error();
            if(error == null) {
                Application.get().view(new Content(data));
            } else {
                error(error);
            }
            return true;
        }
    }

    private record Content(Client.Data data) implements ContentProducer {

        @Override
        public void produce() {
        }

        @Override
        public InputStream getContent() {
            return data.stream();
        }

        @Override
        public String getFileExtension() {
            String ext = data.mimeType() != null ? ContentGenerator.getFileExtension(data.mimeType()) : null;
            return ext == null ? "bin" : ext;
        }

        @Override
        public String getFileName() {
            return null;
        }

        @Override
        public void setTransactionManager(TransactionManager transactionManager) {
        }

        @Override
        public String getContentType() {
            return data.mimeType();
        }
    }
}
