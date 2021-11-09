package com.storedobject.ui.common;

import com.storedobject.core.*;
import com.storedobject.common.SOException;
import com.storedobject.ui.*;
import com.storedobject.vaadin.ConfirmButton;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;

public class ChangeObjectPassword<T extends StoredObject> extends DataForm implements Transactional {

    private final ObjectField<T> objectField;
    private PasswordField password, newPassword, repeatNewPassword;
    private final String pinType;
    private int attempts = 0;
    private PIN pin;
    private ConfirmButton deletePassword;

    public ChangeObjectPassword(T object) {
        this(null, object);
    }

    @SuppressWarnings("unchecked")
    public ChangeObjectPassword(String passwordType, T object) {
        this(passwordType, (Class<T>)object.getClass());
        objectField.setValue(object);
        objectField.setReadOnly(true);
    }

    public ChangeObjectPassword(Class<T> objectClass) {
        this(null, objectClass);
    }

    public ChangeObjectPassword(String passwordType, Class<T> objectClass) {
        this(passwordType, new ObjectField<>(StringUtility.makeLabel(objectClass), objectClass, true));
    }

    public ChangeObjectPassword(ObjectField<T> objectField) {
        this(null, objectField);
    }

    public ChangeObjectPassword(String passwordType, ObjectField<T> objectField) {
        super("Set Password");
        this.objectField = objectField;
        if(StringUtility.isWhite(passwordType)) {
            passwordType = "adhoc";
        } else {
            passwordType = passwordType.trim();
        }
        this.pinType = passwordType;
    }

    private PasswordField createPasswordField(String caption) {
        return new PasswordField(caption);
    }

    private String value(HasValue<?, String> f) {
        return f.getValue().trim();
    }

    @Override
    protected void buildFields() {
        addField(objectField);
        setRequired(objectField);
        trackValueChange(objectField);
        addField(password = createPasswordField("Current Password"));
        password.setVisible(false);
        addField(newPassword = createPasswordField("New Password"));
        addField(repeatNewPassword = createPasswordField("Repeat New Password"));
        TextArea conditon = new TextArea();
        conditon.setValue(PasswordPolicy.getForClass(objectField.getObjectClass()).describe());
        conditon.setReadOnly(true);
        add(conditon);
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        deletePassword = new ConfirmButton("Delete Password", VaadinIcon.TRASH, this);
        buttonPanel.add(deletePassword);
        deletePassword.setVisible(false);
        ok.setText("Set Password");
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        getComponent();
        clicked(objectField);
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        if(!change()) {
            if(++attempts > 2) {
                abort();
            }
            return false;
        }
        return true;
    }

    public void setObject(T object) {
        getComponent();
        objectField.setReadOnly(false);
        objectField.setValue(object);
        objectField.setReadOnly(true);
        clicked(objectField);
    }

    @Override
    public void clicked(Component c) {
        if(c == cancel) {
            abort();
            return;
        }
        if(c == objectField) {
            if(!objectField.isEmpty()) {
                pin = PIN.get(objectField.getObjectId(), pinType);
            } else {
                pin = null;
            }
            deletePassword.setVisible(pin != null);
            password.setVisible(pin != null);
            return;
        }
        if(c == deletePassword) {
            if(!delete()) {
                error("Password removal failed, please contact Technical Support!");
                abort();
            }
            pin = null;
            message("Password removed successfully");
            close();
            return;
        }
        super.clicked(c);
    }

    private boolean isCurrentInvalid() {
        if(pin != null && !pin.verify(value(password).toCharArray(), false)) {
            error("Current password is incorrect!");
            return true;
        }
        return false;
    }

    private boolean delete() {
        if(isCurrentInvalid()) {
            return false;
        }
        if(!transact(t -> {
            pin.setTransaction(t);
            pin.resetPIN();
        })) {
            return false;
        }
        return pin.getStatus() == 0;
    }

    protected boolean change() {
        PIN tpin = pin;
        if(tpin == null) {
            tpin = new PIN(objectField.getObjectId(), pinType);
        }
        String p = value(newPassword);
        try {
            tpin.validateNewPIN(value(password).toCharArray(), p.toCharArray());
        } catch (SOException e) {
            warning(e);
            return false;
        }
        if(!p.equals(value(repeatNewPassword))) {
            warning("Please correctly type the new password in both 'New Password' and 'Repeat New Password'");
            return false;
        }
        attempts = 0;
        Transaction t = null;
        try {
            t = getTransactionManager().createTransaction();
            if(pin == null) {
                tpin.setTransaction(t);
            } else {
                tpin.setTransaction(t);
                tpin.changePIN(value(password).toCharArray(), p.toCharArray());
            }
            tpin.save(t);
            t.commit();
            if(tpin.getStatus() != 0) {
                throw new SOException("Password update failed, please contact Technical Support!");
            }
            message("Password set successfully");
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