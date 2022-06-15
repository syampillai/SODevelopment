package com.storedobject.ui.tools;

import com.storedobject.common.TOTP;
import com.storedobject.core.ApplicationServer;
import com.storedobject.core.SQLConnector;
import com.storedobject.core.SystemUser;
import com.storedobject.ui.Application;
import com.storedobject.ui.BarcodeImage;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;
import org.apache.commons.codec.binary.Base32;

public class AuthenticatorRegistration extends DataForm implements Transactional {

    private final ELabel result = new ELabel();
    private final TOTP totp;
    private final TextField code = new TextField("Code");
    private final SystemUser user;

    public AuthenticatorRegistration() {
        super("Activate Authenticator Login");
        user = getTransactionManager().getUser();
        add(code);
        code.addValueChangeListener(e -> process());
        add(result);
        byte[] secret = TOTP.generateKey();
        totp = new TOTP(secret);
        String app = ApplicationServer.getApplicationName();
        BarcodeImage bcImage =
                new BarcodeImage("otpauth://totp/" + app + ":" + user.getLogin() + "@" +
                        SQLConnector.getDatabaseName() + "?secret=" + new Base32().encodeAsString(secret) +
                        "&issuer=" + app);
        add(bcImage);
    }

    @Override
    protected boolean process() {
        result.clearContent();
        if(totp.verify(code.getValue())) {
            result.append(user.getPerson().getName()).append(": ").
                    append("Created successfully").update();
        } else {
            result.append("Invalid Code!", Application.COLOR_ERROR).update();
        }
        return false;
    }
}
