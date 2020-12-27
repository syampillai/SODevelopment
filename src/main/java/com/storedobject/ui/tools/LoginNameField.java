package com.storedobject.ui.tools;

import com.storedobject.common.StringList;
import com.storedobject.core.SQLConnector;
import com.storedobject.core.StringUtility;
import com.storedobject.helper.ID;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class LoginNameField extends ComboBox<String> {

    private final Users users = new Users();
    private final HasValue<?, Boolean> remember;
    private StringList userList;

    public LoginNameField(HasValue<?, Boolean> remember) {
        super("Login");
        this.remember = remember;
        ID.set(this);
        getElement().appendChild(users.getElement());
        setAllowCustomValue(true);
        addCustomValueSetListener(e -> setValue(e.getDetail()));
        addValueChangeListener(e -> {
            if(userList != null) {
                String u = getValue();
                if(u != null) {
                    u = u.trim().toLowerCase();
                    this.remember.setValue(userList.contains(u));
                }
            }
        });
        this.remember.addValueChangeListener(e -> {
            if(e.isFromClient() && !this.remember.getValue() && this.userList != null) {
                removeUser();
            }
        });
    }

    private void removeUser() {
        String u = getValue();
        if(u != null) {
            u = u.trim().toLowerCase();
            if(userList.contains(u)) {
                users.save();
                if(userList.size() == 1) {
                    userList = StringList.EMPTY;
                } else {
                    ArrayList<String> a = new ArrayList<>(userList);
                    a.remove(u);
                    userList = StringList.create(a);
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

    public void save() {
        users.save();
    }

    @Tag("so-users")
    @JsModule("./so/auth/users.js")
    private class Users extends Component {

        private Users() {
            ID.set(this);
            getElement().setProperty("db", SQLConnector.getDatabaseName());
        }

        @ClientCallable
        private void users(String users) {
            if(users == null) {
                userList = StringList.EMPTY;
            } else {
                userList = StringList.create(users);
            }
            LoginNameField.this.set();
        }

        private void save() {
            if(userList == null) {
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
                    u = u + "," + userList.stream().filter(user -> !user.equals(thisU)).collect(Collectors.joining(","));
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
}
