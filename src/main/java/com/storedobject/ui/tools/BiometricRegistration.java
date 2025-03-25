package com.storedobject.ui.tools;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.InvisibleComponent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.dom.Element;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Tag("so-auth-reg")
@JsModule("./so/auth/auth-register.js")
public class BiometricRegistration extends Component implements InvisibleComponent {

    private boolean progress;
    private final Consumer<Boolean> consumer;
    private final WebBiometric biometric;
    private boolean available = false;

    public BiometricRegistration() {
        throw new LogicRedirected(new ManageBiometric(Application.get()));
    }

    public BiometricRegistration(SystemUser user, Consumer<Boolean> consumer) {
        this.consumer = consumer;
        biometric = new WebBiometric();
        biometric.setWebURL(SOServlet.getURL());
        biometric.setLogin(user);
        Collection<WebBiometric> list = StoredObject.list(WebBiometric.class, "Login=" + user.getId()).toList();
        if(list.isEmpty()) {
            biometric.setDeviceName("My Device");
        } else {
            final AtomicInteger i = new AtomicInteger(1);
            while (list.stream().anyMatch(wb -> wb.getDeviceName().toLowerCase().equals("my device " + i.get()))) {
                i.incrementAndGet();
            }
            biometric.setDeviceName("My Device " + i.get());
        }
        Element e = getElement();
        e.setProperty("challenge", biometric.getChallenge());
        e.setProperty("site", SOServlet.getDomain());
        e.setProperty("userId", biometric.getUserID());
        e.setProperty("userName", biometric.getUserName());
        e.setProperty("userDisplay", user.getPerson().getName());
    }

    public void register() {
        if(!progress) {
            progress = true;
            getElement().setProperty("run", true);
        }
    }

    public boolean isAvailable() {
        return available;
    }

    @ClientCallable
    private void biometric(boolean available) {
        this.available = available;
    }

    @ClientCallable
    private void failed() {
        consumer.accept(false);
    }

    @ClientCallable
    private void registered(String id, String type, String attestationObject, String clientDataJSON) {
        boolean registered = biometric.register(Application.get().getTransactionManager(), id, type, attestationObject, clientDataJSON);
        if(registered) {
            String key = biometric.getLogin().getLogin() + "@" + SQLConnector.getDatabaseName() + "@" + SOServlet.getDomain();
            String js = "window.localStorage.setItem('" + key + "','" + biometric.getId() + "');";
            UI.getCurrent().getPage().executeJs(js);
        }
        consumer.accept(registered);
    }

    @ClientCallable
    private void debug(String debug) {
        Application.get().log("From " + getClass() + " => " + debug);
    }
}
