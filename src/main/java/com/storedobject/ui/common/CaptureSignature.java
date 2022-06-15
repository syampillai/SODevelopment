package com.storedobject.ui.common;

import com.storedobject.core.Person;
import com.storedobject.core.Signature;
import com.storedobject.helper.ID;
import com.storedobject.helper.LitComponent;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.icon.VaadinIcon;

public final class CaptureSignature extends DataForm implements Transactional {

    private final Sign sign;
    private final GridLayout layout = new GridLayout(1);
    private Application application;
    private Signature signature;
    private final Person person;

    public CaptureSignature() {
        this(null);
    }

    public CaptureSignature(Person person) {
        super("Sign");
        this.person = person == null ? getTransactionManager().getUser().getPerson() : person;
        sign = new Sign();
        add(sign);
        RadioChoiceField ink = new RadioChoiceField(new String[] { "Black", "Blue" });
        add(new CompoundField(new ELabel("Ink Color: "), ink));
        ink.addValueChangeListener(e -> {
            boolean black = ink.getValue() == 0;
            sign.color(black ? Application.COLOR_NORMAL : Application.COLOR_SUCCESS, black ? Application.COLOR_SUCCESS : Application.COLOR_NORMAL);
        });
    }

    @Override
    protected HasComponents createFieldContainer() {
        layout.getStyle().set("justify-items", "center");
        return layout;
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        buttonPanel.removeAll();
        ok.setText("Save");
        buttonPanel.add(ok, new Button("Clear", VaadinIcon.SUN_O, e -> sign.clear()), cancel);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        super.execute(parent, doNotLock);
        buttonPanel.getElement().removeFromParent();
        layout.add((Component) buttonPanel);
        application = Application.get();
        signature = Signature.get(person.getId());
        if(signature == null) {
            signature = new Signature();
            signature.setPerson(person);
        } else {
            sign.load(signature.getSignature());
        }
    }

    @Override
    protected boolean process() {
        sign.read();
        ((Component)buttonPanel).setVisible(false);
        application.startPolling(this);
        return false;
    }

    private void save() {
        signature.setSignature(sign.signature);
        if(transact(signature::save)) {
            message("Signature saved");
        }
        close();
    }

    @NpmPackage(value = "signature_pad", version = "3.0.0-beta.4")
    @Tag("so-sign")
    @JsModule("./so/sign/so-sign.js")
    private class Sign extends LitComponent {

        private String signature = null;

        public Sign() {
            getElement().setProperty("idSign", "sosign" + ID.newID());
            getElement().setProperty("person", person.getName());
        }

        @ClientCallable
        private void read(String signature) {
            if(signature.length() > 5 && signature.startsWith("data:")) {
                this.signature = signature;
                application.access(CaptureSignature.this::save);
            } else {
                this.signature = null;
                application.access(() -> {
                    ((Component)buttonPanel).setVisible(true);
                    warning("Signature is blank!");
                });
            }
            application.stopPolling(CaptureSignature.this);
        }

        void read() {
            executeJS("read");
        }

        void clear() {
            executeJS("clear");
        }

        void color(String penColor, String borderColor) {
            executeJS("color", penColor, borderColor);
        }

        void load(String signature) {
            executeJS("load", signature);
        }
    }
}
