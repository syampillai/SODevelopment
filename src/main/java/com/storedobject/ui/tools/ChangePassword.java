package com.storedobject.ui.tools;

import com.storedobject.common.SOException;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class ChangePassword extends DataForm implements Transactional {

    private PasswordField password, newPassword, repeatNewPassword;
    private final SystemUser su;
    private int attempts = 0;
    private boolean forgot;
    private boolean expired = false;
    private boolean aborting = false;

    public ChangePassword() {
        this(false, true);
    }

    public ChangePassword(boolean expired, boolean windowMode) {
        super("Change Password", windowMode);
        this.expired = expired;
        su = StoredObject.get(SystemUser.class, getTransactionManager().getUser().getId());
        addConstructedListener(o -> fConstructed());
    }

    public ChangePassword(SystemUser su) {
        super("Set New Password");
        this.su = su;
        this.forgot = true;
        addConstructedListener(o -> fConstructed());
    }

    private void fConstructed() {
        setColumns(1);
        ((HasSize)getContent()).setMinWidth((Application.get().getDeviceWidth() / 3) + "px");
    }

    private PasswordField createPasswordField(String caption) {
        PasswordField f = new PasswordField(caption);
        f.setMaxLength(30);
        return f;
    }

    private String value(HasValue<?, String> f) {
        return f.getValue().trim();
    }

    private char[] valueChars(HasValue<?, String> f) {
        return value(f).toCharArray();
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(!forgot) {
            if(getTransactionManager().getUser().getLogin().equals("guest") && ApplicationServer.runMode().equals("demo")) {
                warning("Password change not allowed for 'guest' user in demo mode");
                return;
            }
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected void buildFields() {
        String voice;
        if(expired) {
            voice = "You password has expired, please change it now!";
            speak(voice);
            add(new ELabel(voice, "red"));
        }
        TextField user;
        addField(user = new TextField("Login ID"));
        user.setReadOnly(true);
        user.setTabIndex(-1);
        user.setValue(su.getId() + ":" + su.getLogin());
        password = createPasswordField("Current Password");
        if(!forgot) {
            addField(password);
        }
        addField(newPassword = createPasswordField("New Password"));
        addField(repeatNewPassword = createPasswordField("Repeat New Password"));
        TextArea condition = new TextArea();
        voice = PasswordPolicy.getForClass(SystemUser.class).describe();
        condition.setValue(voice);
        condition.setReadOnly(true);
        condition.setTabIndex(-1);
        add(condition);
        speak(voice);
    }

    @Override
    protected boolean process() {
        if(!proc()) {
            if(++attempts > 2) {
                abort();
            }
            return false;
        }
        return true;
    }

    @Override
    public void abort() {
        if(aborting) {
            super.abort();
        } else {
            aborting = true;
            getApplication().close();
        }
    }

    private boolean proc() {
        String p = value(newPassword);
        char[] pc = p.toCharArray();
        if(!su.isAdmin()) {
            try {
                su.validateNewPassword(valueChars(password), pc);
            } catch (SOException e) {
                warning(e);
                return false;
            }
        }
        if(!p.equals(value(repeatNewPassword))) {
            warning("Please correctly type the new password in both 'New Password' and 'Repeat New Password'");
            return false;
        }
        attempts = 0;
        if(forgot) {
            close();
            try {
                getTransactionManager().forgotPassword(pc);
            } catch(Exception e) {
                error(e);
            }
            getApplication().close();
            return true;
        }
        Transaction t = null;
        try {
            t = getTransactionManager().createTransaction();
            su.setTransaction(t);
            su.changePassword(valueChars(password), pc);
            t.commit();
            if(!su.verifyPasswordUpdate()) {
                throw new SOException("Password change failed, please report to Technical Support!");
            }
            message("Password changed successfully");
            getTransactionManager().reinit(pc);
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
