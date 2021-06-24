package com.storedobject.ui.tools;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.InvisibleComponent;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.dom.Element;

import java.math.BigInteger;
import java.util.function.Consumer;

@Tag("so-auth")
@JsModule("./so/auth/auth.js")
public class BiometricButton extends Component implements InvisibleComponent {

    private boolean progress = false;
    private SystemUser user;
    private WebBiometric biometric;
    private final Button button;
    private boolean available = false, registered = false;
    private final Consumer<BiometricButton> inform;
    private final Login login;

    public BiometricButton(Consumer<BiometricButton> inform, Login login) {
        this.inform = inform;
        button = new Button("Use Biometric", "icons:fingerprint", e -> authenticate());
        this.login = login;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        Component c = getParent().orElse(null);
        if(c instanceof HasComponents) {
            ((HasComponents) c).add(button);
        }
    }

    @ClientCallable
    private void biometric(boolean available) {
        this.available = available;
        SystemUser u = this.user;
        this.user = null;
        setUser(u);
    }

    @ClientCallable
    private void authenticated(String id, String type, String authenticatorData, String clientDataJSON, String signature, String userHandle) {
        progress = false;
        registered = login.login(biometric, id, type, authenticatorData, clientDataJSON, signature, userHandle, false);
        if(registered) {
            inform.accept(this);
        } else {
            button.setVisible(false);
        }
    }

    @ClientCallable
    private void failed() {
        progress = false;
        button.setVisible(false);
        registered = false;
    }

    @ClientCallable
    private void debug(String debug) {
        Application.get().log("From " + getClass() + " => " + debug);
    }


    public boolean isAvailable() {
        return available;
    }

    public boolean isRegistered() {
        return registered;
    }

    public WebBiometric getBiometric() {
        return biometric;
    }

    public void setText(String text) {
        button.setText(text);
    }

    public void setUser(SystemUser user) {
        if(!available || user == null) {
            this.user = user;
            biometric = null;
            button.setVisible(false);
            return;
        }
        if(user.equals(this.user)) {
            return;
        }
        button.setVisible(false);
        this.user = user;
        getElement().setProperty("findDevice", user.getLogin() + "@" + SQLConnector.getDatabaseName() + "@" +
                SOServlet.getDomain());
    }

    @ClientCallable
    private void device(String credentialId) {
        try {
            biometric = WebBiometric.get(user, new Id(new BigInteger(credentialId)));
        } catch(Throwable error) {
            biometric = null;
        }
        if(biometric != null && !biometric.getDisabled()) {
            button.setVisible(true);
            Element e = getElement();
            e.setProperty("challenge", biometric.getChallenge());
            e.setProperty("site", SOServlet.getDomain());
            e.setProperty("credentialId", biometric.getBiometricID());
        } else {
            if(biometric != null) { // Disabled device
                registered = true;
                available = false;
            }
        }
    }

    private void authenticate() {
        if(progress || biometric == null) {
            return;
        }
        biometric.setWebURL(SOServlet.getURL());
        progress = true;
        getElement().setProperty("run", true);
    }
}