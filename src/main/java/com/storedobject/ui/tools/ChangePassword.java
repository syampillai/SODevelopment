package com.storedobject.ui.tools;

import com.storedobject.common.SOException;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.TextField;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;

public class ChangePassword extends DataForm implements Transactional {

    private final TextField newUsername = new TextField("New Username (change if desired)");
    private PasswordField password, newPassword, repeatNewPassword;
    private final SystemUser su;
    private int attempts = 0;
    private final boolean forgot;
    private boolean expired = false;
    private boolean changed = false;

    public ChangePassword() {
        this(false, true);
    }

    public ChangePassword(boolean expired, boolean windowMode) {
        super("Change Password", windowMode);
        this.forgot = false;
        this.expired = expired;
        su = StoredObject.get(SystemUser.class, getTransactionManager().getUser().getId());
        addConstructedListener(o -> fConstructed());
    }

    protected ChangePassword(SystemUser su) {
        this(su, null);
    }

    protected ChangePassword(SystemUser su, String caption) {
        super(caption == null || caption.isBlank() ? "Set Password" : caption);
        this.su = su;
        this.forgot = true;
        addConstructedListener(o -> fConstructed());
    }

    private void fConstructed() {
        setColumns(1);
        setCloseable(false);
    }

    @Override
    protected void sizeSet() {
        getContent().getElement().getStyle().set("max-width", "500px");
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
            if(getTransactionManager().getUser().getLogin().equals("guest")
                    && ApplicationServer.runMode().equals("demo")) {
                warning("Password change not allowed for 'guest' user in demo mode");
                return;
            }
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        ok.setText("Confirm");
    }

    @Override
    protected void buildFields() {
        newUsername.setMaxLength(30);
        newUsername.lowercase();
        newUsername.setValue(su.getLogin());
        String voice;
        if(expired) {
            voice = "You password has expired, please change it now!";
            speak(voice);
            add(new ELabel(voice, "red"));
        }
        TextField user;
        addField(user = new TextField("Username"));
        user.setReadOnly(true);
        user.setTabIndex(-1);
        user.setValue(su.getId() + ":" + su.getLogin());
        addField(newUsername);
        password = createPasswordField("Current Password");
        if(!forgot) {
            addField(password);
        }
        addField(newPassword = createPasswordField((forgot ? "" : "New ") + "Password"));
        addField(repeatNewPassword = createPasswordField("Repeat " + (forgot ? "" : "New ") + "Password"));
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

    private boolean proc() {
        String newUser = newUsername.getValue().trim().toLowerCase();
        if(newUser.equals(su.getLogin())) {
            newUser = null;
        }
        if(newUser != null) {
            if(!SystemUser.isValidLogin(newUser)) {
                warning("Not a valid username, please try another one");
                return false;
            }
            if(!SystemUser.isLoginAvailable(newUser)) {
                warning("Username you selected is not available, please try another one");
                return false;
            }
        }
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
                getTransactionManager().forgotPassword(pc, newUser);
                changed = true;
            } catch(Throwable e) {
                error(e);
            }
            return true;
        }
        Transaction t = null;
        try {
            t = getTransactionManager().createTransaction();
            su.setTransaction(t);
            su.changePassword(valueChars(password), pc, newUser);
            t.commit();
            if(!su.verifyPasswordUpdate()) {
                throw new SOException("Password change failed, please report to Technical Support!");
            }
            changed = true;
            message("Password changed successfully");
            getTransactionManager().reinit(pc);
        } catch(Throwable e) {
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

    public boolean isChanged() {
        return changed;
    }
}
