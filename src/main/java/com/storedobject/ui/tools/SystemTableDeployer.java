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

    private final Button create;
    private final Button drop;
    private final Button menuItem;
    private final PopupButton upload, all;
    private final Button uploadProcess;
    private final Button uploadProcessAndReindex;
    private final Button allProcess, allProcessSystem;
    private final Button allProcessAndReindex, allProcessAndReindexSystem;
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
        buttons.add(create = new Button("Create/Alter/Reindex", "ok", this));
        buttons.add(drop = new ConfirmButton("Drop", "delete", this));
        buttons.add(menuItem = new Button("Create Menu Item", "menu", this));
        menuItem.setVisible(false);
        uploadProcess = new Button("Create/Alter", "", this);
        uploadProcessAndReindex = new Button("Reindex", "", this);
        upload = new PopupButton("Upload Class List", VaadinIcon.UPLOAD);
        upload.add(uploadProcess, uploadProcessAndReindex);
        buttons.add(upload);
        allProcess = new Button("Create/Alter Application Classes", "", this);
        allProcessAndReindex = new Button("Reindex Application Classes", "", this);
        allProcessSystem = new Button("Create/Alter System Classes", "", this);
        allProcessAndReindexSystem = new Button("Reindex System Classes", "", this);
        all = new PopupButton("Process All Classes");
        all.add(allProcess, allProcessAndReindex, allProcessSystem, allProcessAndReindexSystem);
        buttons.add(all);
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
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        application = Application.get();
        super.execute(parent, doNotLock);
    }

    private void checkStatus(boolean drop) {
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
        if(drop) {
            return;
        }
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
        if(isSU && system) {
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
        if(!message.isEmpty()) {
            message(message);
        }
        status.setValue("<div>" + message.replace("\n", "<br/>") + "</div>");
    }

    @SuppressWarnings("unchecked")
    private void setKlass(String currClassName) throws Exception {
        if(!JavaClass.checkName(currClassName)) {
            throw new Exception("Invalid class name");
        }
        Class<? extends StoredObject> objectClass;
        try {
            objectClass = (Class<? extends StoredObject>)JavaClassLoader.getLogic(currClassName);
        } catch(ClassNotFoundException e) {
            objectClass = null;
        }
        if(objectClass == null) {
            throw new Exception("Class not found: " + currClassName);
        }
        if(!StoredObject.class.isAssignableFrom(objectClass) || objectClass == StoredObject.class) {
            throw new Exception("Not a Data Class: " + currClassName + " (If it is really a Data Class, try after restarting the application)");
        }
        ca = StoredObjectUtility.classAttribute(objectClass);
        if(ca == null) {
            throw new Exception("Invalid Data Class: " + currClassName);
        }
    }

    private String password() {
        String password;
        if((password = adminPassword.getValue()).isEmpty()) {
            error("Please enter administrator password");
            return null;
        }
        try {
            if(Database.get().validateSecurityPassword(password)) {
                return password;
            }
            error("Invalid administrator password");
        } catch (Exception e) {
            create.setVisible(false);
            upload.setVisible(false);
            all.setVisible(false);
            action = -1;
            error(e);
        }
        return null;
    }

    @Override
    public void clicked(Component c) {
        if(c == exit) {
            close();
            return;
        }
        if(c == uploadProcess || c == uploadProcessAndReindex || c == allProcess || c == allProcessAndReindex
                || c == allProcessSystem || c == allProcessAndReindexSystem) {
            processMulti(c);
            return;
        }
        String ncn = className.getObjectClassName();
        if(ncn == null) {
            menuItem.setVisible(false);
            warning("Class not found!");
            return;
        }
        if(!ncn.equals(currentClassName) || ncn.isEmpty()) {
            currentClassName = ncn;
            checkStatus(c == drop);
            if(c != drop) {
                return;
            }
        }
        if(c == menuItem) {
            Logic logic = Logic.get(Logic.class, "ClassName='" + currentClassName + "'");
            if(logic == null) {
                logic = Logic.get(Logic.class, "ClassName='E:" + currentClassName + "'");
            }
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
                logic = new Logic(currentClassName, StringUtility.makeLabel(
                        currentClassName.substring(currentClassName.lastIndexOf('.') + 1)));
                logic.setSingleInstance(true);
            }
            new ObjectEditor<>(Logic.class, EditorAction.ALL).editObject(logic);
            return;
        }
        String password = password();
        if(password == null) {
            return;
        }
        if(c == drop) {
            action = 0;
            clearAlerts();
            if(!isSU && !getTransactionManager().getUser().isAppAdmin()) {
                warning("Not authorized!");
                return;
            }
            try {
                dropTable(ca, password);
            } catch(Exception e) {
                error(e);
                close();
            }
            return;
        }
        if(c == create) {
            switch(action) {
                case 0 -> {
                    return;
                }
                case 2 -> {
                    if(schemaNotCreated(ca, password)) {
                        message("Error creating schema... Please seek help...");
                        action = 0;
                        return;
                    }
                }
                case 3 -> {
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
                }
                case 4 -> {
                    try {
                        execCommands(alterTable, password);
                    } catch(Exception e) {
                        message("Error altering data table... Please seek help...\nCommand: " + e.getMessage());
                        action = 0;
                        return;
                    }
                }
                case 5 -> {
                    action = 0;
                    try {
                        if(isSU && system) {
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
            }
            checkStatus(false);
        }
    }

    private static boolean schemaNotCreated(ClassAttribute<?> ca, String password) {
        return !Database.get().createSchema(ca.getModuleName(), password);
    }

    private void dropTable(ClassAttribute<?> ca, String password) throws Exception {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("DROP TABLE " + ca.getModuleName() + "." + ca.getTableName());
        commands.add("DROP TABLE " + ca.getModuleName() + ".H_" + ca.getTableName());
        execCommands(commands, password);
        message("Data class dropped: " + ca.getObjectClass().getName());
    }

    private boolean createTable(ClassAttribute<?> ca, String password) throws Exception {
        if(isSU && system) {
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
        ArrayList<String> alterTable = new ArrayList<>();
        checkAlterTable(ca, false, alterTable);
        checkAlterTable(ca, true, alterTable);
        if(alterTable.isEmpty()) {
            alterTable = null;
        }
        return alterTable;
    }

    private static void checkAlterTable(ClassAttribute<?> ca, boolean history, ArrayList<String> alterTable) throws Exception {
        String h = history ? "H_" : "";
        ClassAttribute<?> pca = ca.getParent();
        String tableName = ca.getModuleName() + "." + h + ca.getTableName(),
                pTableName = pca == null ? "" : (pca.getModuleName() + "." + h + pca.getTableName());
        String pre = "ALTER TABLE " + tableName + " ";
        List<String[]> columns = Database.get().columnDetails(tableName),
                pColumns = Database.get().columnDetails(pTableName);
        while(pca != null) {
            pColumns.forEach(pc -> columns.removeIf(c -> pc[0].equals(c[0])));
            pca = pca.getParent();
            if(pca != null) {
                pColumns = Database.get().columnDetails(pca.getModuleName() + "." + h + pca.getTableName());
            }
        }
        ColumnDefinitions cds = new ColumnDefinitions();
        Method m = ca.getObjectClass().getMethod("columns", Columns.class);
        m.invoke(null, cds);
        ArrayList<String[]> dropOuts = new ArrayList<>();
        boolean found;
        int i;
        for(String[] c : columns) {
            found = false;
            for(i = 0; i < cds.size(); i++) {
                if(c[0].equals(cds.getName(i).toLowerCase())) {
                    found = true;
                    if(!c[1].equals(cds.getType(i))) {
                        alterTable.add(pre + "ALTER COLUMN " + cds.getName(i) + " TYPE " + cds.getType(i) +
                                " USING " + ColumnDefinition.getDefaultValue(cds.getType(i)));
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
                        alterTable.add(pre + "RENAME COLUMN " + c[0] + " TO " + cds.getName(i));
                        break;
                    }
                }
                if(!found) {
                    alterTable.add(pre + "ADD COLUMN " + cds.getName(i) + " " + cds.getType(i) +
                            " NOT NULL DEFAULT " + ColumnDefinition.getDefaultValue(cds.getType(i)));
                }
            }
        }
        for(String[] c : dropOuts) {
            alterTable.add(0, pre + "DROP COLUMN " + c[0] + " CASCADE");
        }
        List<String> list = Database.get().parentTable(tableName);
        if(list.isEmpty() || !list.get(0).equalsIgnoreCase(pTableName)) {
            alterTable.add(pre + "INHERIT " + pTableName);
            if(!list.isEmpty()) {
                alterTable.add(0, pre + "NO INHERIT " + list.get(0));
            }
        }
        if(history) {
            return;
        }
        List<String> cons = Database.get().foreignKeyConstraintNames(tableName);
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
    }

    private void processMulti(Component c) {
        String p = password();
        if(p == null) {
            return;
        }
        if(c == allProcess || c == allProcessAndReindex || c == allProcessSystem || c == allProcessAndReindexSystem) {
            TextView tv = new TextView("All "
                    + (c == allProcess || c == allProcessAndReindex ? "Application" : "System") + " Classes - " +
                    (c == allProcessAndReindex || c == allProcessAndReindexSystem ? "Reindex" : "Create/Update"));
            tv.setProcessor(() ->
                    process(c == allProcess || c == allProcessAndReindex ? JavaClass.listApplicationClasses()
                                    : JavaClass.listSystemClasses(p), p,
                            c == allProcessAndReindex || c == allProcessAndReindexSystem, new Message(tv),
                            c == allProcessSystem || c == allProcessAndReindexSystem));
            tv.execute(this);
        } else {
            new MultipleClasses(p, c == uploadProcessAndReindex, system).execute(this);
        }
    }

    private void process(List<String> list, String password, boolean reindex, Message m, boolean system) {
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
        m.done();
    }

    private class MultipleClasses extends UploadProcessorView {

        private final String password;
        private final boolean reindex;
        private final boolean system;

        public MultipleClasses(String password, boolean reindex, boolean system) {
            super((reindex ? "Re-index" : "Process") + " Multiple Classes",
                    "File containing list of classes");
            this.password = password;
            this.reindex = reindex;
            this.system = system;
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
            process(list, password, reindex, new Message(this), system);
        }
    }

    private static class Message {

        private final List<M> list = new ArrayList<>();
        private final StyledBuilder sb;

        Message(StyledBuilder sb) {
            this.sb = sb;
        }

        private void p(Object any) {
            list.add(new M("Processing " + any, 0));
            render();
        }

        private void m(Object any) {
            list.add(new M(any, 1));
            render();
        }

        private void e(Object any) {
            list.add(new M(any, 2));
            render();
        }

        private void done() {
            list.add(new M("[Done]", 0));
            renderAll();
        }

        private void render() {
            if(list.size() < 200) {
                render(list.get(list.size() - 1));
                sb.update();
                return;
            }
            remove(0);
            remove(1);
            remove(2);
            renderAll();
        }

        private void renderAll() {
            sb.clear();
            list.forEach(this::render);
            sb.update();
        }

        private void render(M m) {
            switch(m.color) {
                case 1 -> sb.append(m.m, Application.COLOR_SUCCESS);
                case 2 -> sb.append(m.m, Application.COLOR_ERROR);
                default -> sb.append(m.m, Application.COLOR_NORMAL);
            }
            if(m.color == 2) {
                sb.newLine();
            }
            sb.newLine();
        }

        private void remove(int color) {
            int i = 0;
            M m;
            while(list.size() > 100) {
                if(i >= list.size()) {
                    break;
                }
                m = list.get(i);
                if(m.color == color) {
                    list.remove(i);
                    continue;
                }
                ++i;
            }
        }

        private record M(Object m, int color) {}
    }
}
