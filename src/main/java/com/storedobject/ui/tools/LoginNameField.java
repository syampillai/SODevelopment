package com.storedobject.ui.tools;

import com.storedobject.common.StringList;
import com.storedobject.core.SQLConnector;
import com.storedobject.core.StringUtility;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.shared.Registration;
import elemental.json.JsonType;
import elemental.json.JsonValue;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class LoginNameField extends ComboBox<String> {

    private HasValue<?, Boolean> remember;
    private HasValue<?, ?> passwordField;
    private StringList userList;
    private Registration registrationRemember;

    public LoginNameField() {
        super();
        setAllowCustomValue(true);
        addCustomValueSetListener(e -> setValue(e.getDetail()));
        addValueChangeListener(e -> userChanged());
    }

    public void setRemember(HasValue<?, Boolean> remember) {
        if(registrationRemember != null) {
            registrationRemember.remove();
        }
        if(remember == null) {
            this.remember = null;
            return;
        }
        this.remember = remember;
        registrationRemember = this.remember.addValueChangeListener(e -> {
            if(e.isFromClient() && !this.remember.getValue() && this.userList != null) {
                removeUser();
            }
        });
        userChanged();
    }

    public void setPasswordField(HasValue<?, ?> passwordField) {
        this.passwordField = passwordField;
    }

    private void userChanged() {
        if(userList != null && this.remember != null) {
            String u = getValue();
            if(u != null) {
                u = u.trim().toLowerCase();
                this.remember.setValue(userList.contains(u));
            }
        }
    }

    private void removeUser() {
        String u = getValue();
        if(u != null) {
            u = u.trim().toLowerCase();
            if(userList.contains(u)) {
                save();
                if(userList.size() == 1) {
                    userList = StringList.EMPTY;
                } else {
                    ArrayList<String> a = new ArrayList<>(userList);
                    a.remove(u);
                    userList = StringList.create(a);
                }
                if(passwordField != null) {
                    passwordField.clear();
                }
                set();
            }
        }
    }

    private void set() {
        setItems(userList);
        if(!userList.isEmpty()) {
            setValue(userList.get(0));
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(userList == null) {
            UI.getCurrent().getPage().executeJs("return window.localStorage.getItem('users."
                    + SQLConnector.getDatabaseName() + "')").then(this::users);
        }
    }

    private void users(JsonValue users) {
        if(users != null && users.getType() == JsonType.STRING) {
            userList = StringList.create(users.asString());
        } else {
            userList = StringList.EMPTY;
        }
        set();
    }

    public void save() {
        if(userList == null || remember == null) {
            return;
        }
        String u = getValue().trim().toLowerCase();
        if(u.isEmpty() || StringUtility.isDigit(u)) {
            return;
        }
        if(remember.getValue()) {
            if(userList.size() > 0) {
                if(userList.get(0).equals(u)) {
                    return;
                }
                String thisU = u;
                u = u + "," + userList.stream().filter(user -> !user.equals(thisU))
                        .collect(Collectors.joining(","));
            }
        } else {
            if(!userList.contains(u)) {
                return;
            }
            if(userList.size() == 1) {
                u = null;
            } else {
                String thisUser = u;
                u = userList.stream().filter(user -> !user.equals(thisUser)).collect(Collectors.joining(","));
            }
        }
        String key = "'users." + SQLConnector.getDatabaseName() + "'";
        String js = "window.localStorage.";
        if(u == null) {
            js += "removeItem(" + key;
        } else {
            js += "setItem(" + key + ",'" + u + "'";
        }
        js += ");";
        UI.getCurrent().getPage().executeJs(js);
    }
}
