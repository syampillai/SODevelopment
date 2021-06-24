package com.storedobject.ui.tools;

import com.storedobject.common.Array;
import com.storedobject.common.IO;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.tools.ColumnDefinition;
import com.storedobject.ui.Application;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SystemTableDeployer extends View implements Transactional {

    private final Button proceed;
    private final Button menuItem;
    private final PopupButton upload, all;
    private final Button uploadProcess;
    private final Button uploadProcessAndReindex;
    private final Button allProcess;
    private final Button allProcessAndReindex;
    private final Button exit;
    private final ClassNameField className;
    private final TextField tableName;
    private final PasswordField adminPassword;
    private final ELabel status;
    private int action = 0;
    private String currentClassName = "";
    private ClassAttribute<?> ca;
    private ArrayList<String> alterTable;
    private final boolean isSU;
    private Application application;
    private final boolean system;

    public SystemTableDeployer() {
        this("System Data Class Deployer", true);
    }

    SystemTableDeployer(String caption, boolean system) {
        super(caption);
        this.system = system;
        isSU = getTransactionManager().getUser().isAdmin();
        adminPassword = new PasswordField("Administrator Password");
        adminPassword.setMaxLength(30);
        className = new ClassNameField("Name of the Data Class");
        tableName = new TextField("Name of the Data Table");
        tableName.setEnabled(false);
        VerticalLayout layout = new VerticalLayout();
        ButtonLayout buttons = new ButtonLayout();
        buttons.add(proceed = new Button("Proceed", this));
        buttons.add(menuItem = new Button("Create Menu Item", "menu", this));
        menuItem.setVisible(false);
        uploadProcess = new Button("Create / Alter", "", this);
        uploadProcessAndReindex = new Button("Reindex", "", this);
        upload = new PopupButton("Upload Class List", VaadinIcon.UPLOAD);
        upload.add(uploadProcess, uploadProcessAndReindex);
        buttons.add(upload);
        if(system) {
            all = null;
            allProcess = null;
            allProcessAndReindex = null;
        } else {
            allProcess = new Button("Create / Alter", "", this);
            allProcessAndReindex = new Button("Reindex", "", this);
            all = new PopupButton("Process All Classes");
            all.add(allProcess, allProcessAndReindex);
            buttons.add(all);
        }
        buttons.add(exit = new Button("Exit", this));
        layout.add(buttons);
        FormLayout form = new FormLayout();
        form.add(adminPassword);
        form.add(className);
        form.add(tableName);
        form.setWidth(null);
        layout.add(form);
        status = new ELabel();
        layout.add(status);
        layout.setMargin(true);
        setComponent(new Window(layout));
        setFirstFocus(className);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        application = Application.get();
        super.execute(parent, doNotLock);
    }

    private void checkStatus() {
        menuItem.setVisible(false);
        if(action == -1) {
            return;
        }
        tableName.setValue("<Not Set>");
        try {
            setKlass(currentClassName);
        } catch (Exception e) {
            currentClassName = "";
            error(e.getMessage());
            return;
        }
        String tn = ca.getModuleName() + "." + ca.getTableName();
        tableName.setValue(tn);
        if(!Database.get().tableExists(tn)) {
            if(Database.get().schemaExists(ca.getModuleName())) {
                status("Data Table '" + tn + "' does not exist in the database...\nYou may create it now...");
                action = 3;
            } else {
                status("Schema '" + ca.getModuleName() + "' does not exist in the database...\nYou may create it now...");
                action = 2;
            }
            return;
        }
        try {
            alterTable = checkAlterTable(ca);
        } catch (Exception e) {
            error(e);
            currentClassName = "";
            return;
        }
        if(alterTable == null) {
            status("Data Table structure looks fine now... You may reindex the table if needed...");
            action = 5;
            menuItem.setVisible(true);
            return;
        }
        StringBuilder s = new StringBuilder("Table '");
        s.append(tn);
        s.append("' already exists in the database...\nYou may alter it now as follows:\n");
        s.append("(Please make sure that you backed up your database before doing this)");
        for(String t: alterTable) {
            s.append("\n").append(t);
        }
        status(s.toString());
        if(isSU) {
            trace(alterTable);
        }
        action = 4;
    }

    private void trace(Iterable<String> trace) {
        TextView b = new TextView("Trace");
        b.setApplication(application);
        trace.forEach(s -> b.append(s).append(';').newLine());
        b.execute();
    }

    private void status(String message) {
        if(message.length() > 0) {
            message(message);
        }
        status.setValue("<div>" + message.replace("\n", "<br/>") + "</div>");
    }

    @SuppressWarnings("unchecked")
    private void setKlass(String currentClassName) throws Exception {
        if(!JavaClass.checkName(currentClassName)) {
            throw new Exception("Invalid class name");
        }
        Class<? extends StoredObject> objectClass;
        try {
            objectClass = (Class<? extends StoredObject>)JavaClassLoader.getLogic(currentClassName);
        } catch(ClassNotFoundException e) {
            objectClass = null;
        }
        if(objectClass == null) {
            throw new Exception("Class not found: " + currentClassName);
        }
        if(!StoredObject.class.isAssignableFrom(objectClass) || objectClass == StoredObject.class) {
            throw new Exception("Not a Data Class: " + currentClassName + " (If it is really a Data Class, try after restarting the application)");
        }
        ca = StoredObjectUtility.classAttribute(objectClass);
        if(ca == null) {
            throw new Exception("Invalid Data Class: " + currentClassName);
        }
    }

    private String password() {
        String password;
        if((password = adminPassword.getValue()).length() == 0) {
            error("Please enter administrator password");
            return null;
        }
        try {
            if(Database.get().validateSecurityPassword(password)) {
                return password;
            }
            error("Invalid administrator password");
        } catch (Exception e) {
            proceed.setVisible(false);
            upload.setVisible(false);
            all.setVisible(false);
            action = -1;
            error(e);
        }
        return null;
    }

    @SuppressWarnings("resource")
    @Override
    public void clicked(Component c) {
        if(c == exit) {
            close();
            return;
        }
        if(c == uploadProcess || c == uploadProcessAndReindex || c == allProcess || c == allProcessAndReindex) {
            processMulti(c);
            return;
        }
        String ncn = className.getObjectClassName();
        if(ncn == null) {
            menuItem.setVisible(false);
            warning("Class not found!");
            return;
        }
        String newClassName = ncn;
        if(!newClassName.equals(currentClassName) || newClassName.isEmpty()) {
            currentClassName = newClassName;
            checkStatus();
            return;
        }
        if(c == menuItem) {
            Logic logic = Logic.get(Logic.class, "ClassName='E:" + currentClassName + "'");
            if(logic == null) {
                logic = Logic.get(Logic.class, "ClassName='B:" + currentClassName + "'");
            }
            if(logic == null) {
                logic = Logic.get(Logic.class, "ClassName='T:" + currentClassName + "'");
            }
            if(logic == null) {
                logic = Logic.get(Logic.class, "ClassName='F:" + currentClassName + "'");
            }
            if(logic == null) {
                logic = Logic.get(Logic.class, "ClassName='S:" + currentClassName + "'");
            }
            if(logic == null) {
                if((StoredObjectUtility.hints(ca.getObjectClass()) & ObjectHint.SMALL_LIST) == ObjectHint.SMALL_LIST) {
                    newClassName = "B";
                } else {
                    newClassName = "E";
                }
                newClassName += ":" + currentClassName;
                logic = new Logic(newClassName, StringUtility.makeLabel(currentClassName.substring(currentClassName.lastIndexOf('.') + 1)));
                logic.setSingleInstance(true);
            }
            new ObjectEditor<>(Logic.class, EditorAction.ALL).editObject(logic);
            return;
        }
        String password = password();
        if(password == null) {
            return;
        }
        if(c == proceed) {
            switch(action) {
                case 0:
                    return;
                case 2:
                    if(schemaNotCreated(ca, password)) {
                        message("Error creating schema... Please seek help...");
                        action = 0;
                        return;
                    }
                    break;
                case 3:
                    try {
                        if(createTable(ca, password)) {
                            break;
                        }
                        message("Error creating data table... Please seek help...");
                    } catch(Exception e) {
                        error(e);
                    }
                    action = 0;
                    return;
                case 4:
                    try {
                        execCommands(alterTable, password);
                    } catch (Exception e) {
                        message("Error altering data table... Please seek help...\nCommand: " + e.getMessage());
                        action = 0;
                        return;
                    }
                    break;
                case 5:
                    action = 0;
                    try {
                        if(isSU) {
                            trace(StringList.create(StoredObjectUtility.reindex(ca.getObjectClass())));
                        }
                        execCommands(new Array<>(StoredObjectUtility.reindex(ca.getObjectClass())), password);
                    } catch(Exception e) {
                        message("Error reindexing data table... Please seek help...\nCommand: " + e.getMessage());
                        return;
                    }
                    status("Reindexing done... Nothing else to do!");
                    return;
            }
            checkStatus();
        }
    }

    private static boolean schemaNotCreated(ClassAttribute<?> ca, String password) {
        return !Database.get().createSchema(ca.getModuleName(), password);
    }

    private boolean createTable(ClassAttribute<?> ca, String password) throws Exception {
        if(isSU) {
            trace(StringList.create(StoredObjectUtility.createDDL(ca.getObjectClass())));
        }
        return Database.get().createTable(ca.getObjectClass(), password);
    }

    private static void execCommands(Iterable<String> commands, String password) throws Exception {
        for(String comm: commands) {
            if(!Database.get().executeSQL(comm, password)) {
                throw new Exception(comm);
            }
        }
    }

    private static ArrayList<String> checkAlterTable(ClassAttribute<?> ca) throws Exception {
        ArrayList<String> alterTable;
        ClassAttribute<?> pca = ca.getParent();
        String tableName = ca.getModuleName() + "." + ca.getTableName(),
                pTableName = pca == null ? "" : (pca.getModuleName() + "." + pca.getTableName());
        String tableNameH = tableName.replace(".", ".H_"),
                pTableNameH = pTableName.replace(".", ".H_");
        String pre = "ALTER TABLE " + tableName + " ";
        String ppre = "ALTER TABLE " + tableNameH + " ";
        String copy;
        ArrayList<String[]> columns = Database.get().columnDetails(tableName),
                pColumns = Database.get().columnDetails(pTableName);
        while(pca != null) {
            pColumns.forEach(pc -> columns.removeIf(c -> pc[0].equals(c[0])));
            pca = pca.getParent();
            if(pca != null) {
                pColumns = Database.get().columnDetails(pca.getModuleName() + "." + pca.getTableName());
            }
        }
        ColumnDefinitions cds = new ColumnDefinitions();
        Method m = ca.getObjectClass().getMethod("columns", Columns.class);
        m.invoke(null, cds);
        alterTable = new ArrayList<>();
        ArrayList<String[]> dropOuts = new ArrayList<>();
        boolean found;
        int i;
        for(String[] c : columns) {
            found = false;
            for(i = 0; i < cds.size(); i++) {
                if(c[0].equals(cds.getName(i).toLowerCase())) {
                    found = true;
                    if(!c[1].equals(cds.getType(i))) {
                        alterTable.add(copy = pre + "ALTER COLUMN " + cds.getName(i) + " TYPE " + cds.getType(i) +
                                " USING " + ColumnDefinition.getDefaultValue(cds.getType(i)));
                        alterTable.add(copy.replace(pre, ppre));
                    }
                    break;
                }
            }
            if(!found) {
                dropOuts.add(c);
            }
        }
        for(i = 0; i < cds.size(); i++) {
            found = false;
            for(String[] c : columns) {
                if(c[0].equals(cds.getName(i).toLowerCase())) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                for(String[] c : dropOuts) {
                    if(c[1].equals(cds.getType(i))) {
                        found = true;
                        dropOuts.remove(c);
                        alterTable.add(copy = pre + "RENAME COLUMN " + c[0] + " TO " + cds.getName(i));
                        alterTable.add(copy.replace(pre, ppre));
                        break;
                    }
                }
                if(!found) {
                    alterTable.add(copy = pre + "ADD COLUMN " + cds.getName(i) + " " + cds.getType(i) +
                            " NOT NULL DEFAULT " + ColumnDefinition.getDefaultValue(cds.getType(i)));
                    alterTable.add(copy.replace(pre, ppre));
                }
            }
        }
        for(String[] c : dropOuts) {
            alterTable.add(0, copy = pre + "DROP COLUMN " + c[0] + " CASCADE");
            alterTable.add(1, copy.replace(pre, ppre));
        }
        ArrayList<String> list = Database.get().parentTable(tableName);
        if(list.size() == 0 || !list.get(0).equalsIgnoreCase(pTableName)) {
            alterTable.add(pre + "INHERIT " + pTableName);
            if(list.size() > 0) {
                alterTable.add(0, pre + "NO INHERIT " + list.get(0));
            }
        }
        list = Database.get().parentTable(tableNameH);
        if(list.size() == 0 || !list.get(0).equalsIgnoreCase(pTableNameH)) {
            alterTable.add(ppre + "INHERIT " + pTableNameH);
            if(list.size() > 0) {
                alterTable.add(1, ppre + "NO INHERIT " + list.get(0));
            }
        }
        ArrayList<String> cons = Database.get().foreignKeyConstraints(tableName);
        String[] fkeys = StoredObjectUtility.foreignKeysDDL(ca.getObjectClass());
        Optional<String> any;
        for(String fk: fkeys) {
            any = cons.stream().filter(con -> fk.contains(" ADD CONSTRAINT " + con.toLowerCase() + " ")).findAny();
            if(any.isPresent()) {
                cons.remove(any.get());
            } else {
                alterTable.add(fk);
            }
        }
        for(String con: cons) {
            alterTable.add(pre + " DROP CONSTRAINT IF EXISTS " + con);
        }
        if(alterTable.size() == 0) {
            alterTable = null;
        }
        return alterTable;
    }

    private void processMulti(Component c) {
        String p = password();
        if(p == null) {
            return;
        }
        if(c == allProcess || c == allProcessAndReindex) {
            TextView tv = new TextView("All Classes - " +
                    (c == allProcessAndReindex ? "Reindex" : "Create/Update"));
            tv.setProcessor(() ->
                    process(JavaClass.listApplicationClasses(), p,
                            c == allProcessAndReindex, new Message(tv)));
            tv.execute(this);
        } else {
            new MultipleClasses(p, c == uploadProcessAndReindex).execute(this);
        }
    }

    private void process(List<String> list, String password, boolean reindex, Message m) {
        m.m((reindex ? "Re-index" : "Creating/updating/index") + "ing...");
        boolean error;
        for(String klass: list) {
            klass = klass.trim();
            if(klass.isEmpty()) {
                continue;
            }
            if(system &&
                    (!klass.startsWith("com.storedobject.") || klass.equals("com.storedobject.core.StoredObject"))) {
                continue;
            }
            m.p(klass);
            try {
                setKlass(klass);
            } catch (Exception e) {
                m.e(e.getMessage());
                continue;
            }
            error = false;
            String tn = ca.getModuleName() + "." + ca.getTableName();
            if(!Database.get().tableExists(tn)) {
                m.m("Creating " + tn);
                if(!Database.get().schemaExists(ca.getModuleName())) {
                    m.m("Creating schema");
                    if(schemaNotCreated(ca, password)) {
                        m.e("Error creating schema");
                        error = true;
                    }
                }
                if(!error) {
                    try {
                        if(!createTable(ca, password)) {
                            m.e("Error creating data table");
                        }
                    } catch(Exception e) {
                        m.e(e.getMessage());
                    }
                }
            } else {
                try {
                    if((alterTable = checkAlterTable(ca)) != null) {
                        alterTable.forEach(m::m);
                        execCommands(alterTable, password);
                    }
                } catch(Exception e) {
                    m.e(e.getMessage());
                    error = true;
                }
            }
            if(!error && reindex) {
                try {
                    Array<String> a = new Array<>(StoredObjectUtility.reindex(ca.getObjectClass()));
                    a.forEach(m::m);
                    execCommands(a, password);
                } catch (Exception e) {
                    m.e(e.getMessage());
                }
            }
        }
        m.s().blackMessage("[Done]");
    }

    private class MultipleClasses extends UploadProcessorView {

        private final String password;
        private final boolean reindex;

        public MultipleClasses(String password, boolean reindex) {
            super((reindex ? "Re-index" : "Process") + " Multiple Classes",
                    "File containing list of classes");
            this.password = password;
            this.reindex = reindex;
        }

        @Override
        protected void process(InputStream data, String mimeType) {
            try {
                process(data);
            } catch (Exception e) {
                error(e);
            }
        }

        private void process(InputStream data) {
            List<String> list = new ArrayList<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(data, StandardCharsets.UTF_8));
            br.lines().forEach(list::add);
            IO.close(br);
            SystemTableDeployer.this.
            process(list, password, reindex, new Message(this));
        }
    }

    private static class Message {

        private final StyledBuilder sb;

        Message(StyledBuilder sb) {
            this.sb = sb;
        }

        private void p(Object any) {
            sb.blackMessage("Processing " + any);
        }

        private void m(Object any) {
            sb.blueMessage(any);
        }

        private void e(Object any) {
            sb.redMessage(any);
        }

        private StyledBuilder s() {
            return sb;
        }
    }
}
