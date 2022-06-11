package com.storedobject.ui.tools;

import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.List;
import java.util.stream.Stream;

public class ManageExternalUsers extends ObjectEditor<SystemUser> {

    private static final String EU = "External Username.l";
    private static final String AEU = "Authorized Remote Server User";
    private Button authUserButton;
    private String authUser;
    private char[] authPassword;
    private final Button verify;

    public ManageExternalUsers() {
        super(SystemUser.class, EditorAction.EDIT | EditorAction.SEARCH | EditorAction.VIEW,
                "Manage External Users");
        setSearchFilter(Application.getUserVisibility("cross"));
        verify = new Button("Verify", VaadinIcon.THUMBS_UP_O, e -> verify());
    }

    @Override
    public Stream<StoredObjectUtility.Link<?>> extraLinks() {
        StoredObjectUtility.Link<ExternalSystemUser> ess = new StoredObjectUtility.Link<>(SystemUser.class);
        ess.setObjectClass(ExternalSystemUser.class);
        ess.setName(EU.substring(0, EU.lastIndexOf('.')));
        return Stream.of(ess);
    }

    @Override
    public void setTab(String tabName) {
    }

    @Override
    protected int getFieldOrder(String fieldName) {
        if(EU.equals(fieldName)) {
            return Integer.MAX_VALUE - 2;
        }
        if("Groups.l".equals(fieldName)) {
            return Integer.MAX_VALUE - 1;
        }
        if("Entities.l".equals(fieldName)) {
            return Integer.MAX_VALUE;
        }
        return super.getFieldOrder(fieldName);
    }

    @Override
    protected boolean includeField(String fieldName) {
        if(fieldName.endsWith(".l")) {
            return (EU).equals(fieldName) || "Groups.l".equals(fieldName) || "Entities.l".equals(fieldName);
        }
        return super.includeField(fieldName);
    }

    @Override
    public boolean isFieldEditable(String fieldName) {
        return (EU).equals(fieldName);
    }

    @Override
    protected void createExtraButtons() {
        authUserButton = new Button(AEU, VaadinIcon.KEY_O, e -> new AuthUser(null).execute());
    }

    @Override
    protected void addExtraButtons() {
        SystemUser su = getObject();
        if(su != null) {
            buttonPanel.add(authUserButton);
        }
        if(su != null && su.existsLinks(ExternalSystemUser.class, "NOT Verified")) {
            buttonPanel.add(verify);
        }
    }

    @Override
    protected LinkGrid<?> createLinkFieldGrid(String fieldName, ObjectLinkField<?> field) {
        if(EU.equals(fieldName)) {
            //noinspection unchecked
            return new ExternalUserGrid((ObjectLinkField<ExternalSystemUser>) field);
        }
        return super.createLinkFieldGrid(fieldName, field);
    }

    private static class ExternalUserGrid extends DetailLinkGrid<ExternalSystemUser> {

        public ExternalUserGrid(ObjectLinkField<ExternalSystemUser> linkField) {
            super(linkField);
            addConstructedListener(f -> (((ObjectField<?>)getObjectEditor().getField("Server")))
                    .setFilter("lower(Name)<>'" + SQLConnector.getDatabaseName().toLowerCase() + "'",
                            true));
        }

        @Override
        public boolean canEdit(ExternalSystemUser eu) {
            if(eu.getVerified()) {
                message("Can't edit, already verified");
                return false;
            }
            return true;
        }
    }

    private void verify() {
        clearAlerts();
        SystemUser su = getObject();
        if(su == null) {
            return;
        }
        if(authUser == null || authUser.isBlank()) {
            new AuthUser(this::verify).execute();
            return;
        }
        List<ExternalSystemUser> esus = su.listLinks(ExternalSystemUser.class, "NOT Verified").toList();
        if(esus.isEmpty()) {
            return;
        }
        for(ExternalSystemUser esu: esus) {
            try {
                esu.verify(getTransactionManager(), authUser, authPassword, SOServlet.getURL());
                message("Verification successful on the server - " + esu.getServer().getDescription());
            } catch(Exception e) {
                warning("Verification failed on the server - " + esu.getServer().getDescription());
                warning(e);
            }
        }
        ManageExternalUsers.this.reload();
    }

    private class AuthUser extends DataForm {

        private final TextField authUser = new TextField(AEU);
        private final PasswordField authPassword = new PasswordField("Password on the Remove Server");
        private final Runnable action;

        public AuthUser(Runnable action) {
            super("Credentials");
            this.action = action;
            addField(authUser, authPassword);
            if(ManageExternalUsers.this.authUser != null) {
                authUser.setValue(ManageExternalUsers.this.authUser);
                authPassword.setValue(new String(ManageExternalUsers.this.authPassword));
            }
        }

        @Override
        protected boolean process() {
            close();
            ManageExternalUsers.this.authUser = authUser.getValue().trim();
            ManageExternalUsers.this.authPassword = authPassword.getValue().toCharArray();
            if(action != null) {
                Application.get().access(action::run);
            }
            return true;
        }
    }
}
