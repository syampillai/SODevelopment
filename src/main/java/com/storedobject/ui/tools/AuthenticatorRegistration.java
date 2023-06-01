package com.storedobject.ui.tools;

import com.storedobject.common.TOTP;
import com.storedobject.core.ApplicationServer;
import com.storedobject.core.PIN;
import com.storedobject.core.SQLConnector;
import com.storedobject.core.SystemUser;
import com.storedobject.ui.BarcodeImage;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocomplete;
import org.apache.commons.codec.binary.Base32;

import java.util.Timer;
import java.util.TimerTask;

public class AuthenticatorRegistration extends DataForm implements Transactional {

    private static final String Authenticator_Registration = "Authenticator Registration";
    private final byte[] secret = TOTP.generateKey();
    private final TOTP totp = new TOTP(secret);
    private final IntegerField code = new IntegerField("Verify Code ", 0, 6);
    private final SystemUser user;
    private final boolean exists;

    public AuthenticatorRegistration() {
        super(Authenticator_Registration);
        user = getTransactionManager().getUser();
        exists = PIN.get(user.getId(), "totp") != null;
        code.setAutocomplete(Autocomplete.OFF);
        code.setLength(6);
        code.setEmptyDisplay("");
        code.setWidth("6em");
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(exists) {
            new ActionForm(Authenticator_Registration,
                    "An authenticator already exists!\nDo you really want to replace that?",
                    () -> new Create().execute()).execute();
        } else {
            new Create().execute();
        }
    }

    @Override
    protected boolean process() {
        return true;
    }

    private class Create extends DataForm {

        private final TimerComponent timer = new TimerComponent();

        private Create() {
            super(Authenticator_Registration);
            code.addValueChangeListener(e -> process());
            String app = ApplicationServer.getApplicationName();
            BarcodeImage bcImage =
                    new BarcodeImage("otpauth://totp/" + app + ":" + user.getLogin() + "@" +
                            SQLConnector.getDatabaseName() + "?secret=" + base32() +
                            "&issuer=" + app);
            bcImage.setImageWidth(200);
            bcImage.setWidth("200px");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    getApplication().access(bcImage::redraw);
                }
            }, 3000);
            VerticalLayout imageLayout = new VerticalLayout(
                    new ELabel("Set up your authenticator using the following QR code"), bcImage);
            imageLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            imageLayout.setSizeFull();
            add(imageLayout);
            setColumnSpan(imageLayout, 2);
            addField(code);
            timer.setPrefix("Cancel automatically in ");
            timer.setSuffix(" seconds");
            timer.addListener(e -> cancel());
            add(timer);
        }

        private String base32() {
            String s = new Base32().encodeAsString(secret);
            while(s.endsWith("=")) {
                s = s.substring(0, s.length() - 1);
            }
            return s;
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            super.execute(parent, doNotLock);
            getApplication().startPolling(this);
            timer.countDown(180);
        }

        @Override
        public void clean() {
            super.clean();
            getApplication().stopPolling(this);
            timer.abort();
        }

        @Override
        protected void cancel() {
            clearAlerts();
            super.cancel();
        }

        @Override
        protected boolean process() {
            clearAlerts();
            if(totp.verify(code.getValue())) {
                try {
                    user.saveKeyForTOTP(getTransactionManager(), secret);
                    message("Authenticator is set up successfully");
                    close();
                    return true;
                } catch(Exception e) {
                    error(e);
                }
            } else {
                warning("Code not valid, please try again");
            }
            return false;
        }
    }
}
