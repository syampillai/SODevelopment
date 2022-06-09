package com.storedobject.ui.tools;

import com.storedobject.core.*;
import com.storedobject.ui.*;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.stream.Stream;

public class ManageExternalUsers extends ObjectEditor<SystemUser> {

    private static final String EU = "External Login.l";
    private static final String AEU = "Authorized Remote Server User";
    private Button authUserButton;
    private String authUser;
    private char[] authPassword;

    public ManageExternalUsers() {
        super(SystemUser.class, EditorAction.EDIT | EditorAction.SEARCH | EditorAction.VIEW,
                "Manage External Users");
        setSearchFilter(Application.getUserVisibility("cross"));
    }

    @Override
    public Stream<StoredObjectUtility.Link<?>> extraLinks() {
        StoredObjectUtility.Link<ExternalSystemUser> ess = new StoredObjectUtility.Link<>(SystemUser.class);
        ess.setObjectClass(ExternalSystemUser.class);
        ess.setName(EU.substring(0, EU.lastIndexOf('.')));
        return Stream.of(ess);
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
        if(getObject() != null) {
            buttonPanel.add(authUserButton);
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

    private class ExternalUserGrid extends DetailLinkGrid<ExternalSystemUser> {

        public ExternalUserGrid(ObjectLinkField<ExternalSystemUser> linkField) {
            super(linkField);
            Button verify = new Button("Verify", VaadinIcon.THUMBS_UP_O, e -> verify()).asSmall();
            getButtonPanel().add(verify);
            verify.setVisible(false);
            addItemSelectedListener((c, eu) -> verify.setVisible(eu != null));
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

        private void verify() {
            clearAlerts();
            ExternalSystemUser eu = selected();
            if(eu == null) {
                return;
            }
            if(authUser == null || authUser.isBlank()) {
                new AuthUser(this::verify).execute();
                return;
            }
            try {
                eu.verify(getTransactionManager(), authUser, authPassword, SOServlet.getURL());
                refresh(eu);
                message("Verified successfully");
            } catch(Exception e) {
                warning(e);
            }
        }
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
