package com.storedobject.ui.common;

import com.storedobject.common.Email;
import com.storedobject.core.*;
import com.storedobject.mail.Mail;
import com.storedobject.mail.MailSender;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.PasswordField;

import java.util.ArrayList;

public class SetMailSenderPassword extends DataForm implements Transactional {

    private ArrayList<MailSender> senders = new ArrayList<>();
    private String email;
    private PasswordField password, newPassword, repeatNewPassword;
    private final Person person;
    private int attempts = 0;

    public SetMailSenderPassword() {
        super("Set Mail Sender Password");
        person = getTransactionManager().getUser().getPerson();
        email = person.getContact("email");
        if(email != null && !email.isEmpty()) {
            StoredObject.list(MailSender.class, "lower(FromAddress)='" + email.toLowerCase() + "'").collectAll(senders);
        }
    }

    private PasswordField createPasswordField(String caption) {
        PasswordField f = new PasswordField(caption);
        f.setMaxLength(30);
        return f;
    }

    private String value(HasValue<?, String> f) {
        return f.getValue().trim();
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(getTransactionManager().getUser().getLogin().equals("guest") && ApplicationServer.runMode().equals("demo")) {
            warning("Password change not allowed for 'guest' user in demo mode");
            return;
        }
        if(StringUtility.isWhite(email)) {
            warning("No email found in your name!");
            return;
        }
        try {
            Email.check(email);
        } catch (Exception e) {
            warning("You email doesn't seem to be correct! Please correct it.");
            return;
        }
        if(senders.isEmpty()) {
            warning("No email sender configured with your email address! Your email address is " + email);
            return;
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected void buildFields() {
        ELabelField user;
        addField(user = new ELabelField("Your Email"));
        user.setValue(person.getName() + " <" + email + ">");
        addField(password = createPasswordField("Current Password"));
        setFirstFocus(password);
        addField(newPassword = createPasswordField("New Password"));
        addField(repeatNewPassword = createPasswordField("Repeat New Password"));
    }

    @Override
    protected boolean process() {
        if(!proc()) {
            if(++attempts > 2) {
                close();
                getApplication().close();
            }
            return false;
        }
        return true;
    }

    protected boolean proc() {
        for(MailSender ms: senders) {
            if(ms.getPassword().equals(value(password))) {
                error("Current password is incorrect!");
                return false;
            }
        }
        String p = value(newPassword);
        if(!p.equals(value(repeatNewPassword))) {
            warning("Please correctly type the new password in both 'New Password' and 'Repeat New Password'");
            return false;
        }
        attempts = 0;
        Transaction t = null;
        try {
            t = getTransactionManager().createTransaction();
            for(MailSender ms: senders) {
                ms.setPassword(p);
                ms.setUserName(email);
                ms.save(t);
            }
            t.commit();
            message("Password changed successfully");
        } catch(Exception e) {
            try {
                if(t != null) {
                    t.rollback();
                }
            } catch(Exception ignore) {
            }
            error(e);
        }
        return true;
    }
}