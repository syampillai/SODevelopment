package com.storedobject.ui.tools;

import com.storedobject.common.StringList;
import com.storedobject.core.EditorAction;
import com.storedobject.core.SystemUser;
import com.storedobject.core.WebBiometric;
import com.storedobject.ui.Application;
import com.storedobject.ui.HTMLText;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.HTMLGenerator;

public class ManageBiometric extends ObjectBrowser<WebBiometric> {

    private final Button registrationButton;
    private final BiometricRegistration registration;
    private final Application application;

    public ManageBiometric(Application application) {
        super(WebBiometric.class, StringList.create("This", "DeviceName", "Disabled"),
                EditorAction.EDIT | EditorAction.DELETE, "Manage Biometric");
        createHTMLColumn("This", this::getThis);
        this.application = application;
        SystemUser su = application.getTransactionManager().getUser();
        setFilter("Login=" + su.getId());
        if(application.isBiometricAvailable() && !application.isBiometricRegistered()) {
            registration = new BiometricRegistration(su, this::registered);
            registrationButton = new Button("Register This Device", "icons:fingerprint", e -> {
                application.startPolling(this);
                registration.register();
            });
        } else {
            registration = null;
            registrationButton = null;
        }
        addConstructedListener(o -> con());
    }

    private void con() {
        load();
        setObjectEditor(new Editor());
    }

    @Override
    protected void addExtraButtons() {
        super.addExtraButtons();
        if(registration != null) {
            buttonPanel.add(registrationButton);
            buttonPanel.add(registration);
        }
    }

    @Override
    public String getColumnCaption(String columnName) {
        if("This".equals(columnName)) {
            return "";
        }
        return super.getColumnCaption(columnName);
    }

    private HTMLGenerator getThis(WebBiometric biometric) {
        HTMLText h = new HTMLText();
        if(biometric.getId().equals(getApplication().getBiometricDeviceId())) {
            h.append("This Device", Application.COLOR_SUCCESS).update();
        }
        return h;
    }

    private void registered(boolean success) {
        application.access(() -> {
            if(success) {
                application.disableBiometric();
                registrationButton.setVisible(false);
                load();
                message("Biometric login has been enabled for this device!");
            } else {
                error("Unable to register biometric login!");
            }
            close();
            application.stopPolling(this);
        });
    }

    private static class Editor extends ObjectEditor<WebBiometric> {


        public Editor() {
            super(WebBiometric.class);
        }

        @Override
        protected void formConstructed() {
            super.formConstructed();
            addValidator(getField("DeviceName"),
                    name -> !getObject().duplicateDeviceName((String)name), "Name already exists");
        }
    }
}