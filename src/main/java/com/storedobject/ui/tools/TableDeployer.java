package com.storedobject.ui.tools;

import java.io.StringWriter;
import java.util.ArrayList;

import com.storedobject.core.Database;
import com.storedobject.core.EditorAction;
import com.storedobject.core.JavaClass;
import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.Logic;
import com.storedobject.common.SOException;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.core.StringUtility;
import com.storedobject.core.Transaction;
import com.storedobject.tools.TableDefinition;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;

public class TableDeployer extends View implements Transactional {

    private final Button proceed;
    private final Button delete;
    private final Button deleteTable;
    private final Button menuItem;
    private final Button exit;
    private TableDefinition td;
    private final TextField className, tableName;
    private final PasswordField adminPassword;
    private final ELabel status;
    private int action;
    private JavaClass jc;
    private ArrayList<String> alterTable;
    private final ELabel sysWarning;

    public TableDeployer() {
        this(null);
    }

    public TableDeployer(TableDefinition tableDefinition) {
        setCaption("Data Class Deployer");
        adminPassword = new PasswordField("Administrator Password");
        adminPassword.setMaxLength(30);
        className = new TextField("Name of the Data Class");
        className.setEnabled(false);
        tableName = new TextField("Name of the Data Table");
        tableName.setEnabled(false);
        VerticalLayout layout = new VerticalLayout();
        ButtonLayout buttons = new ButtonLayout();
        buttons.add(proceed = new Button("Proceed", this));
        buttons.add(delete = new ConfirmButton("Delete", this));
        buttons.add(deleteTable = new ConfirmButton("Delete Table", "delete", this));
        buttons.add(menuItem = new Button("Create Menu Item", "menu", this));
        buttons.add(exit = new Button("Exit", this));
        layout.add(buttons);
        FormLayout form = new FormLayout();
        form.setColumns(1);
        form.add(sysWarning = new ELabel("Please make sure that System Data Classes are updated", "red"));
        sysWarning.setVisible(false);
        form.add(className);
        form.add(tableName);
        form.add(adminPassword);
        form.setWidth(null);
        layout.add(form);
        status = new ELabel();
        layout.add(status);
        layout.setMargin(true);
        setComponent(layout);
        setTable(tableDefinition, true);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(action < 0) {
            return;
        }
        super.execute(parent, doNotLock);
    }

    public void acceptPassword(View caller) {
        status("");
        sysWarning.setVisible(true);
        proceed.setVisible(false);
        delete.setVisible(false);
        deleteTable.setVisible(false);
        menuItem.setVisible(false);
        className.setVisible(false);
        tableName.setVisible(false);
        int a = action;
        action = 0;
        execute(caller);
        action = a;
    }

    private String getTable() {
        if(td == null) {
            return "";
        }
        String t = td.getTableName();
        if(t == null) {
            action = -1;
        }
        return t == null ? "<Not Available>" : t;
    }

    public void setTable(TableDefinition tableDefinition) {
        setTable(tableDefinition, false);
    }

    private void setTable(TableDefinition tableDefinition, boolean nullAllowed) {
        jc = null;
        action = -1;
        proceed.setVisible(false);
        this.td = tableDefinition;
        className.setValue(td == null ? "" : td.getClassName());
        tableName.setValue("<Not set yet>");
        if(td == null) {
            if(!nullAllowed) {
                status("No Data Class definition set");
            }
            delete.setVisible(false);
            deleteTable.setVisible(false);
            return;
        }
        delete.setVisible(true);
        deleteTable.setVisible(true);
        if(!td.compile()) {
            status("Compilation errors exist");
            action = 0;
            proceed.setVisible(false);
            return;
        }
        action = 0;
        checkStatus();
    }

    public void deleteTable(TableDefinition tableDefinition, View caller) {
        jc = null;
        action = -1;
        proceed.setVisible(false);
        menuItem.setVisible(false);
        this.td = tableDefinition;
        className.setValue(td == null ? "" : td.getClassName());
        tableName.setValue(getTable());
        if(td == null) {
            status("No Data Class definition set");
            return;
        }
        delete.setVisible(true);
        deleteTable.setVisible(true);
        action = 0;
        execute(caller);
    }

    private void action(int action) {
        this.action = action;
        proceed.setVisible(true);
    }

    private void checkStatus() {
        proceed.setVisible(false);
        if(action == -1) {
            return;
        }
        if(!JavaClassLoader.loaded(td.getClassName())) {
            jc = JavaClass.create(td.getClassName());
            if(jc.created()) {
                action(1);
                status("This data class was never deployed...\nYou may deploy it now...");
                return;
            }
            if(changed()) {
                action(1);
                status("The deployed data class differs from this...\nYou may redeploy it now...");
                return;
            }
            if(action == -1) {
                return;
            }
            action = -2;
        } else {
            if(changed()) {
                status("Old version of this data class is already loaded by the application...\nYou have to restart the application...");
                return;
            }
            action = -3;
        }
        String tName = getTable();
        if(tName.startsWith("<")) {
            error("Unable to determine table name, please check error logs (include generic information)!");
            close();
            return;
        }
        tableName.setValue(tName);
        if(action == -1) {
            return;
        }
        if(!Database.get().tableExists(tName)) {
            if(Database.get().schemaExists(td.getSchemaName())) {
                status("Data Table '" + td.getTableName() + "' does not exist in the database...\nYou may create it now...");
                action(3);
            } else {
                status("Schema '" + td.getSchemaName() + "' does not exist in the database...\nYou may create it now...");
                action(2);
            }
            return;
        }
        try {
            alterTable = td.alterTable();
        } catch (Exception e) {
            error(e);
            return;
        }
        if(alterTable == null) {
            status("Data Table structure looks fine now... You may reindex the table if needed...");
            proceed.setText("Reindex Table");
            action(5);
            return;
        }
        StringBuilder s = new StringBuilder("Data Table '");
        s.append(tName);
        s.append("' already exists in the database...\nYou may alter it now as follows:\n");
        s.append("(Please make sure that you backed up your database before doing this)");
        for(String t: alterTable) {
            s.append("\n").append(t);
        }
        status(s.toString());
        error(s.toString());
        action(4);
    }

    private boolean changed() {
        if(jc == null) {
            jc = JavaClass.create(td.getClassName());
        }
        try {
            return td.classChanged(jc);
        } catch(Exception e) {
            action = -1;
            error(e);
        }
        return true;
    }

    private void status(String message) {
        if(message.length() > 0) {
            message(message);
        }
        String[] m = message.split("\\n");
        status.clear();
        for(String s: m) {
            if(!status.isEmpty()) {
                status.newLine();
            }
            status.append(s, "red");
        }
        status.update();
    }

    public TableDefinition getTableDefinition() {
        return td;
    }

    public String getAdminPassword() throws Exception {
        String password = adminPassword.getValue();
        if(password.length() == 0) {
            throw new SOException("Please enter administrator password");
        }
        if(!Database.get().validateSecurityPassword(password)) {
            throw new SOException("Invalid administrator password");
        }
        return password;
    }

    public void setAdminPassword(String password) {
        if(password == null) {
            return;
        }
        adminPassword.setValue(password);
    }

    @Override
    @SuppressWarnings({ "unchecked", "resource" })
    public void clicked(Component c) {
        if(c == exit) {
            close();
            return;
        }
        if(c == menuItem) {
            String cName = className.getValue().trim();
            Logic logic = StoredObject.get(Logic.class, "ClassName='E:" + cName + "'");
            if(logic == null) {
                logic = StoredObject.get(Logic.class, "ClassName='B:" + cName + "'");
            }
            if(logic == null) {
                logic = StoredObject.get(Logic.class, "ClassName='T:" + cName + "'");
            }
            if(logic == null) {
                logic = StoredObject.get(Logic.class, "ClassName='F:" + cName + "'");
            }
            if(logic == null) {
                logic = StoredObject.get(Logic.class, "ClassName='S:" + cName + "'");
            }
            if(logic == null) {
                char e = 'E';
                if(td.getSmallList()) {
                    e = 'B';
                }
                logic = new Logic(e + ":" + cName, StringUtility.makeLabel(cName.substring(cName.lastIndexOf('.') + 1), false));
                logic.setSingleInstance(true);
            }
            ObjectEditor<Logic> oe = new ObjectEditor<>(Logic.class, EditorAction.ALL);
            oe.setObject(logic);
            oe.editObject(logic);
            return;
        }
        String password = "";
        if((c == proceed && action > 1) || (c == delete) || (c == deleteTable)) {
            try {
                password = getAdminPassword();
            } catch (Exception e) {
                proceed.setVisible(false);
                action = -1;
                error(e);
                return;
            }
        }
        if(c == delete) {
            del();
            return;
        }
        if(c == deleteTable) {
            delTable();
            return;
        }
        if(c == proceed) {
            proceed.setVisible(false);
            String[] commands;
            switch(action) {
                case 0:
                    return;
                case 1:
                    saveClass();
                    break;
                case 2:
                    if(!Database.get().createSchema(td.getSchemaName(), password)) {
                        message("Error creating schema... Please seek help...");
                        return;
                    }
                    break;
                case 3:
                    try {
                        Class<? extends StoredObject> objectClass = (Class<? extends StoredObject>)JavaClassLoader.getLogic(td.getClassName());
                        if(!Database.get().createTable(objectClass, password)) {
                            message("Error creating data table... Please seek help...");
                            action = -1;
                            return;
                        }
                    } catch(Exception e) {
                        error(e);
                        action = -1;
                        return;
                    }
                    break;
                case 4:
                    for(String command: alterTable) {
                        if(!Database.get().executeSQL(command, password)) {
                            message("Error altering data table... Please seek help...\nCommand: " + command);
                            action = -1;
                            return;
                        }
                    }
                    break;
                case 5:
                    try {
                        Class<? extends StoredObject> objectClass = (Class<? extends StoredObject>)JavaClassLoader.getLogic(td.getClassName());
                        commands = StoredObjectUtility.reindex(objectClass);
                    } catch(ClassNotFoundException e) {
                        error(e);
                        action = -1;
                        return;
                    }
                    for(String comm: commands) {
                        if(!Database.get().executeSQL(comm, password)) {
                            message("Error reindexing data table... Please seek help...\nCommand: " + comm);
                            action = -1;
                            return;
                        }
                    }
                    action = 0;
                    status("Reindexing done... Nothing else to do!");
                    return;
            }
            checkStatus();
        }
    }

    private void saveClass() {
        try {
            if(jc == null) {
                jc = JavaClass.create(td.getClassName());
            }
            StringWriter sw = new StringWriter();
            td.generateJavaCode(sw);
            jc.setSourceCode(sw.toString());
            jc.setGenerated(true);
            Transaction t = getTransactionManager().createTransaction();
            try {
                //noinspection ResultOfMethodCallIgnored
                jc.upload(t);
                t.commit();
            } catch(Exception e) {
                t.rollback();
                throw e;
            }
        } catch (Exception e) {
            action = -1;
            error(e);
        }
    }

    private void del() {
        try {
            if(jc == null) {
                jc = JavaClass.create(td.getClassName());
            }
            if(jc.getId() != null) {
                Transaction t = getTransactionManager().createTransaction();
                try {
                    jc.delete(t);
                    t.commit();
                } catch(Exception e) {
                    t.rollback();
                }
            }
        } catch (Exception e) {
            action = -1;
            error(e);
            return;
        }
        delTable();
    }

    @SuppressWarnings("unchecked")
    private void delTable() {
        try {
            if(Database.get().tableExists(td.getTableName())) {
                Class<? extends StoredObject> objectClass = (Class<? extends StoredObject>)JavaClassLoader.getLogic(td.getClassName());
                String[] commands = StoredObjectUtility.dropDDL(objectClass);
                String password = adminPassword.getValue();
                for(String comm: commands) {
                    if(!Database.get().executeSQL(comm, password)) {
                        message("Error dropping data table... Please seek help...\nCommand: " + comm);
                        action = -1;
                        return;
                    }
                }
            }
        } catch(ClassNotFoundException e) {
            error(e);
            action = -1;
            return;
        }
        close();
    }
}
