package com.storedobject.ui.tools;

import com.storedobject.common.SOException;
import com.storedobject.core.ObjectTree;
import com.storedobject.core.*;
import com.storedobject.pdf.*;
import com.storedobject.tools.ColumnDefinition;
import com.storedobject.tools.LinkDefinition;
import com.storedobject.tools.TableDefinition;
import com.storedobject.ui.Application;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TableDefinitionEditor extends ObjectEditor<TableDefinition> {

    private Button loadParent, viewChildren, viewSource, editSource, compileSource, deploy, upload, deployAll, download,
            downloadAll, createSourceAll, compileSourceAll, viewAll, reorderFields, uploadUpdate, uploadCompare, report;
    private ClassTreeBrowser classTree = null, fullClassTree = null;
    private String adminPassword;

    public TableDefinitionEditor() {
        this(EditorAction.ALL);
    }

    public TableDefinitionEditor(int actions) {
        super(TableDefinition.class, actions, "Data Class");
        addField("VersionInformation");
    }

    @Override
    public void createExtraButtons() {
        PopupButton d = new PopupButton("Delete");
        delete = d;
        d.add(new ConfirmButton("Delete Data Class Definition", "", e -> doDelete()));
        d.add(new Button("Delete Logic / Table", "", e -> deleteTableOrDC(false)));
        reorderFields = new Button("Reorder Fields", "sort", this);
        loadParent = new Button("Load Parent", "angle-double-up", this);
        viewChildren = new Button("Children", this);
        editSource = new Button("Edit Source", "editor:mode-edit", this);
        viewSource = new Button("View Source", this);
        compileSource = new Button("Compile Source",this);
        deploy = new Button("Deploy", "truck", this);
        upload = new Button("Upload", this);
        download = new Button("Download", e -> download());
        downloadAll = new Button("Download All", "download", e -> new DownloadAll().execute(this));
        createSourceAll = new Button("Create Source Files", "download", this);
        compileSourceAll = new ConfirmButton("Compile All", this);
        deployAll = new ConfirmButton("Deploy All", "truck", this);
        viewAll = new Button("View All", "children", this);
        uploadUpdate = new Button("Bulk Upload & Update", "upload", this);
        uploadCompare = new Button("Bulk Upload & Compare", "upload", this);
        report = new Button("Report", this);
    }

    @Override
    public void addExtraButtons() {
        TableDefinition td = getObject();
        if(td != null && td.getId() != null) {
            buttonPanel.add(reorderFields);
            if(!td.getParentClassName().endsWith("StoredObject")) {
                buttonPanel.add(loadParent);
            }
            buttonPanel.add(viewChildren);
            buttonPanel.add(viewSource);
            buttonPanel.add(editSource);
            buttonPanel.add(compileSource);
            buttonPanel.add(deploy);
            buttonPanel.add(download);
        }
        buttonPanel.add(upload);
        buttonPanel.add(downloadAll);
        buttonPanel.add(createSourceAll);
        buttonPanel.add(compileSourceAll);
        buttonPanel.add(deployAll);
        buttonPanel.add(viewAll);
        buttonPanel.add(uploadUpdate);
        buttonPanel.add(uploadCompare);
        buttonPanel.add(report);
    }

    public String getVersionInformation() {
        TableDefinition td = getObject();
        if(td == null) {
            return "";
        }
        Timestamp t = td.timestamp();
        Person p = td.person();
        StringBuilder s = new StringBuilder();
        if(t != null) {
            s.append("Modified at: ").append(DateUtility.format(t)).append(". ");
        }
        if(p != null) {
            s.append("By: ").append(p.getName());
        }
        return s.toString();
    }

    @Override
    protected int getFieldOrder(String fieldName) {
        if("VersionInformation".equals(fieldName)) {
            return 100000;
        }
        return super.getFieldOrder(fieldName);
    }

    private void reorderFields(int startingNumber, int increment) {
        if(increment == 0) {
            increment = 1;
        }
        TableDefinition td = getObject();
        int n = startingNumber;
        Transaction t = null;
        try (ObjectIterator<ColumnDefinition> icd = td.listLinks(ColumnDefinition.class, null, "DisplayOrder")) {
            t = getTransactionManager().createTransaction();
            for (ColumnDefinition cd : icd) {
                cd.setDisplayOrder(n);
                cd.save(t);
                n += increment;
            }
            t.commit();
            reload();
        } catch (Exception e) {
            if (t != null) {
                t.rollback();
            }
            message(e);
        }
    }

    private void tdv(ArrayList<Id> tds, TableDefinition td) {
        Id id = td.getId();
        if(tds.contains(id)) {
            return;
        }
        TableDefinition ptd = TableDefinition.get(td.getParentClassName());
        if(ptd != null && !tds.contains(ptd.getId())) {
            tdv(tds, ptd);
        }
        tds.add(id);
    }

    private void download(String module) {
        ContentProducer cp = new TextContentProducer() {
            @Override
            public void generateContent() throws Exception {
                StringBuilder filter = new StringBuilder();
                if(module != null && !module.isBlank()) {
                    String[] ms = StringUtility.trim(module.trim().split("\\s"));
                    for(int i = 0; i < ms.length; i++) {
                        if(ms[i].isBlank()) {
                            continue;
                        }
                        if(i > 0) {
                            filter.append(" OR ");
                        }
                        filter.append("ClassName LIKE '%").append(ms[i]).append("%'");
                    }
                }
                Writer w = getWriter();
                for(TableDefinition td: StoredObject.list(TableDefinition.class,
                        filter.isEmpty() ? null : filter.toString())) {
                    td.save(w);
                }
            }
        };
        getApplication().view(cp);
    }

    private void download() {
        ContentProducer cp = new TextContentProducer() {
            @Override
            public void generateContent() throws Exception {
                getObject().save(getWriter());
            }
        };
        getApplication().view(cp);
    }

    @Override
    public void clicked(Component c) {
        if(c == report) {
            new ClassSelector(getObject()).execute();
            return;
        }
        if(c == reorderFields) {
            (new FieldReorderForm(getObject())).execute(this);
            return;
        }
        if(c == deployAll) {
            command(() -> new DeployAll().execute());
            return;
        }
        if(c == upload) {
            new UploadBox().execute(this);
            return;
        }
        if(c == createSourceAll || c == compileSourceAll) {
            final Component pressed = c;
            TextView v = new TextView((pressed == createSourceAll ? "Creat" : "Compil") + "ing Sources");
            v.setProcessor(() -> {
                createCompileSources(v, false, pressed == compileSourceAll);
                if(pressed == compileSourceAll) {
                    v.setProgress(0);
                    createCompileSources(v, true, false);
                    v.setProgress(100);
                }
            });
            v.execute();
            return;
        }
        if(c == editSource || c == viewSource || c == compileSource || c == deploy) {
            TableDefinition td = getObject();
            String title = "Java Class = " + caption(td.getClassName());
            StringWriter sw = new StringWriter();
            try {
                if(c == compileSource || c == deploy) {
                    if(td.generateInterfaceCode()) {
                        warning("Interface code generated... Please see if you want to edit the source...");
                        return;
                    }
                    if(td.isCorrectionNeeded()) {
                        warning("Generated code needs to be corrected... Please edit the source...");
                        return;
                    }
                }
                td.generateJavaCode(sw);
                if(c == editSource) {
                    (new SourceCodeEditor(sw.toString(), title)).execute(this);
                } else if(c == compileSource || c == deploy) {
                    if(compile(sw.toString(), c == compileSource)) {
                        if(c == deploy) {
                            command(() -> new DeployTable(td).execute(this));
                        }
                    } else {
                        if(c == deploy) {
                            error("Please fix compilation errors first...");
                        }
                    }
                } else {
                    TextArea ta = new TextArea();
                    ta.setSpellCheck(false);
                    ta.setWidthFull();
                    ta.setValue(sw.toString());
                    View.createCloseableView(ta, title).invoke(this);
                }
            } catch (Exception e) {
                if(!(e instanceof SOException)) {
                    getTransactionManager().getDevice().log(e);
                }
                error(e);
            }
            return;
        }
        if(c == loadParent) {
            String cName = getObject().getParentClassName();
            TableDefinition so = TableDefinition.get(cName);
            if(so == null) {
                message("Data Class definition not found for '" + cName + "'");
            } else {
                setObject(so);
            }
            return;
        }
        if(c == viewChildren) {
            if(classTree != null) {
                classTree.close();
            }
            classTree = new ClassTreeBrowser(getObject());
            classTree.invoke(TableDefinitionEditor.this);
            return;
        }
        if(c == viewAll) {
            if(fullClassTree != null) {
                fullClassTree.close();
            }
            fullClassTree = new FullClassTreeBrowser();
            fullClassTree.invoke(TableDefinitionEditor.this);
            return;
        }
        if(c == uploadUpdate || c == uploadCompare) {
            UploadProcessorView u = new UploadProcessorView("Data Class Definitions",
                    (c == uploadUpdate ? "Updat" : "Compar") + "ing data class definitions");
            u.setProcessor((data, mime) -> processDefinitions(data, c == uploadUpdate, u));
            u.execute();
            return;
        }
        super.clicked(c);
    }

    private void createCompileSources(TextView v, boolean compile, boolean skipCompletionLabel) {
        v.message((compile ? "Compil" : "Creat") + "ing data classes");
        JavaClass jc;
        StringWriter sw;
        int count = 0, errorCount = 0;
        TableDefinition errorTD;
        errorTD = null;
        for(TableDefinition td: StoredObject.list(TableDefinition.class)) {
            jc = new JavaClass(td.getClassName());
            sw = new StringWriter();
            try {
                try {
                    td.generateJavaCode(sw);
                } catch (Throwable e) {
                    log(e);
                    v.error("Error generating source code for " + td.getClassName());
                    continue;
                }
                jc.setSourceCode(sw.toString());
                ++count;
                if(compile) {
                    if(jc.compile() != null) {
                        v.redMessage("Error in " + jc.getName());
                        errorTD = td;
                        ++errorCount;
                    } else {
                        v.blueMessage(jc.getName());
                    }
                }
            } catch (Exception e) {
                v.error(e);
            }
        }
        if(!compile) {
            v.message("Data class source files created: " + count);
        } else {
            if(errorTD != null && getObject() == null) {
                TableDefinition td = errorTD;
                getApplication().access(() -> setObject(td));
            }
            v.newLine(true).append("Total classes: ").append(count).append(", ");
            v.append("Classes with errors: " + errorCount, errorCount == 0 ? Application.COLOR_SUCCESS
                    : Application.COLOR_ERROR);
            v.update();
        }
        if(!skipCompletionLabel) {
            v.setCaption(compile ? "Compilation Done" : "Sources Created");
        }
    }

    private class DownloadAll extends DataForm {

        private TextField module;

        public DownloadAll() {
            super("Download All Data Classes");
        }

        @Override
        protected void buildFields() {
            addField(module = new TextField("Filter Words (Separated by Space)"));
            module.setHelperText("Leave it empty for all");
            module.setSpellCheck(false);
        }

        @Override
        protected boolean process() {
            close();
            download(module.getValue().trim());
            return true;
        }
    }

    private class DeployAll extends DataForm {

        private TextField filter;

        public DeployAll() {
            super("Deploy All Data Classes");
        }

        @Override
        protected void buildFields() {
            addField(filter = new TextField("Class Name Filter"));
            filter.setHelperText("Leave it empty for all");
            filter.setSpellCheck(false);
        }

        @Override
        protected boolean process() {
            close();
            String f = filter.getValue().trim();
            if(f.isEmpty()) {
                f = null;
            }
            deployAll(f);
            return true;
        }
    }

    private void deployAll(String classNameFilter) {
        if(adminPassword == null) {
            final String filter = classNameFilter;
            command(() -> deployAll(filter));
            return;
        }
        final String caption = "Deploying data classes (" + (classNameFilter == null ? "all" : classNameFilter) +
                "). This may take a while... Round ";
        if (classNameFilter != null) {
            if (!classNameFilter.contains("'")) {
                classNameFilter = "LIKE '%" + classNameFilter + "%'";
            }
            classNameFilter = "lower(ClassName) " + classNameFilter;
        }
        String cnf = classNameFilter;
        TextView view = new TextView("Deploying...");
        view.setProcessor(() -> {
            view.setProgressCaption(caption + "1");
            createCompileSources(view, false, true);
            ArrayList<Id> tds = new ArrayList<>();
            for (TableDefinition td : StoredObject.list(TableDefinition.class, cnf, "ClassName")) {
                tdv(tds, td);
            }
            TransactionManager tm = getTransactionManager();
            TableDefinition td, errorTD;
            int round = 1, allCount = tds.size(), totalCount, count = 0, errors, prevErrors = 0;
            while (true) {
                totalCount = errors = 0;
                errorTD = null;
                for (int i = 0; i < tds.size(); i++) {
                    td = StoredObject.get(TableDefinition.class, tds.get(i));
                    if (td == null) {
                        tds.remove(i);
                        --i;
                        continue;
                    }
                    ++totalCount;
                    try {
                        if (td.deploy(tm, adminPassword, false)) {
                            ++count;
                            view.blueMessage(td.getClassName());
                        }
                        tds.remove(i);
                        --i;
                    } catch (Throwable e) {
                        view.error(td.getClassName(), ", ", e);
                        errors++;
                        errorTD = td;
                        log("Deployed: " + count + "/" + totalCount + "/" + allCount + ", Errors: " + errors +
                                ", Error deploying " + td.getClassName() + "\n" + e.getMessage());
                    }
                    if (count < 3 || totalCount % (errors == 0 ? 10 : 30) == 0) {
                        if(errors == 0) {
                            view.clear();
                        }
                        view.blueMessage("Deployed: " + count + "/" + totalCount + "/" + allCount + ", Errors: "
                                + errors + ", Remaining: " + tds.size());
                        view.setProgressCaption(caption + round + " (Status: Deployed: " + count + "/" + totalCount
                                + "/" + allCount + ", Errors: " + errors + ", Remaining: " + tds.size() + ") ");
                    }
                }
                if (errors == 0) {
                    break;
                }
                if (round > 1 && (errors == prevErrors || errors == 1)) {
                    break;
                }
                prevErrors = errors;
                ++round;
                view.setProgressCaption(caption + round + " ");
            }
            if (errorTD != null) {
                setObject(errorTD);
            }
            log("Total classes deployed: " + count + "/" + allCount + ", Errors: " + errors);
            view.newLine().append("Total classes deployed: " + count + "/" + allCount + ", Errors: " + errors,
                    errors > 0 ? Application.COLOR_ERROR : (count > 0 ? Application.COLOR_NORMAL : Application.COLOR_SUCCESS));
            view.update();
            view.setCaption("Classes Deployed");
        });
        view.execute();
    }

    @Override
    public boolean canDelete() {
        TableDefinition td = getObject();
        JavaClass jc = JavaClass.create(td.getClassName());
        if(jc.getId() != null || tableExists()) {
            deleteTableOrDC(true);
            return false;
        }
        return true;
    }

    private boolean tableExists() {
        String tName = getObject().getTableName(getTransactionManager());
        if(tName.startsWith("<")) {
            return false;
        }
        return Database.get().viewExists(tName) || Database.get().tableExists(tName);
    }

    private void deleteTableOrDC(boolean message) {
        if(adminPassword == null) {
            command(() -> deleteTableOrDC(message));
            return;
        }
        if(message) {
            message("Please delete the generated Data Class and Table first...");
        }
        DeployTable t = new DeployTable();
        t.deleteTable(getObject(), this);
    }

    @Override
    public void validateData() {
        TableDefinition td = getObject();
        String name = td.getClassName();
        if(name.indexOf('.') < 0) {
            name = StringUtility.pack(StringUtility.makeLabel(name, false));
            td.setClassName(name);
        }
        if(!JavaClass.checkName(name)) {
            if(StringUtility.getCharCount(name, '.') < 3) {
                name = ApplicationServer.getPackageName() + "." + name;
            }
            td.setClassName(name);
        }
    }

    private boolean compile(String source) {
        return compile(source, true);
    }

    private boolean compile(String source, boolean showMessages) {
        TableDefinition td = getObject();
        JavaClass jc = new JavaClass(td.getClassName());
        try {
            jc.setSourceCode(source);
        } catch (Exception e) {
            if(showMessages) {
                error(e);
            }
            return false;
        }
        source = jc.compile();
        if(source == null) {
            if(showMessages) {
                message("Compilation successful");
            }
            return true;
        }
        if(showMessages) {
            TextArea ta = new TextArea();
            ta.setSpellCheck(false);
            ta.setWidthFull();
            ta.setValue(source);
            View.createCloseableView(ta, "Errors").execute();
        }
        return false;
    }

    @Override
    public void clean() {
        if(classTree != null) {
            classTree.close();
        }
        if(fullClassTree != null) {
            fullClassTree.close();
        }
    }

    private static String caption(String c) {
        int p, dots = 3;
        while(dots-- > 0) {
            p = c.indexOf('.');
            if(p < 0) {
                return c;
            }
            c = c.substring(p + 1);
        }
        return c;
    }

    private void command(Runnable command) {
        if(adminPassword == null) {
            AdminPasswordForm apf = new AdminPasswordForm();
            apf.command = command;
            apf.execute(this);
        } else {
            command.run();
        }
    }

    class UploadBox extends ProcessView {

        private final TextArea definition;
        private final TableDefinition td;

        public UploadBox() {
            super("Definition Upload");
            definition = new TextArea("Definition");
            definition.setSpellCheck(false);
            definition.setWidthFull();
            td = getObject();
            if(td == null) {
                append("Current definition, if any, will be replaced");
            } else {
                append("Class: ").append(td.getClassName()).newLine();
                append("Current class definition will be deleted and/or replaced with the new definition",
                        Application.COLOR_ERROR);
            }
        }

        @Override
        protected Component getBottomComponent() {
            return definition;
        }

        @Override
        public void process() throws Throwable {
            String d = definition.getValue();
            if(StringUtility.isWhite(d)) {
                getApplication().access(() -> TableDefinitionEditor.this.error("Blank definition"));
                close();
                return;
            }
            if(td != null) {
                Transaction t = getTransactionManager().createTransaction();
                td.delete(t);
                t.commit();
            }
            StoredObject.load(getTransactionManager(), new StringReader(d), null);
            getApplication().access(() -> {
                if(td != null) {
                    setObject(TableDefinition.get(td.getClassName()));
                }
                TableDefinitionEditor.this.message("Processed");
            });
            close();
        }
    }

    private static Map<String, StoredObject> cols(TableDefinition td) {
        Map<String, StoredObject> cols = new HashMap<>();
        td.listLinks(ColumnDefinition.class).forEach(cd -> cols.put(cd.getName(), cd));
        return cols;
    }

    class SourceCodeEditor extends ObjectBlockEditor {

        private Button compile;

        public SourceCodeEditor(String source, String title) throws Exception {
            super(TableDefinitionEditor.this.getObject(), cols(TableDefinitionEditor.this.getObject()), source, title);
        }

        @Override
        public void addExtraButtons() {
            compile = new Button("Compile", this);
            this.buttonPanel.add(compile);
        }

        @Override
        public void clicked(Component c) {
            if(c == compile) {
                compile(getBlockText());
                return;
            }
            super.clicked(c);
        }

        @Override
        public boolean canSave() {
            return compile(getBlockText());
        }

        @Override
        public void clean() {
            super.clean();
            reload();
        }
    }

    class ClassTreeBrowser extends ObjectTreeBrowser<TableDefinition> {

        private Button load;

        public ClassTreeBrowser(TableDefinition root) {
            this("Child Data Classes");
            setRoots(root);
        }

        public ClassTreeBrowser(String caption) {
            super(TableDefinition.class, 0, caption);
            getDataProvider().getTree().setBuilder(new ClassTreeBuilder());
        }

        @Override
        protected void createExtraButtons() {
            load = new Button("Set", this);
        }

        @Override
        protected void addExtraButtons() {
            buttonPanel.add(load);
        }

        @Override
        public void clicked(Component c) {
            if(c == load) {
                TableDefinition td = getSelected();
                if(td != null) {
                    TableDefinitionEditor.this.setObject(td);
                    TableDefinitionEditor.this.select();
                }
                return;
            }
            super.clicked(c);
        }
    }

    class FullClassTreeBrowser extends ClassTreeBrowser {

        public FullClassTreeBrowser() {
            super("All Data Classes");
            load();
        }
    }

    private static class ClassTreeBuilder implements ObjectTree.Builder<TableDefinition> {

        @Override
        public ObjectIterator<TableDefinition> listChildren(ObjectTree<TableDefinition> objectTree, TableDefinition parent) {
            return StoredObject.list(TableDefinition.class, "lower(ParentClassName)='" +
                    parent.getClassName().trim().toLowerCase() + "'");
        }

        @Override
        public TableDefinition getParent(ObjectTree<TableDefinition> objectTree, TableDefinition child) {
            String pcn = child.getParentClassName().trim().toLowerCase();
            return pcn.isEmpty() ? null : StoredObject.get(TableDefinition.class,
                    "lower(ClassName)='" + pcn + "'");
        }
    }

    class FieldReorderForm extends DataForm {

        private final IntegerField startingNumberField;
        private final IntegerField gapField;

        public FieldReorderForm(TableDefinition td) {
            super("Reorder Fields - " + TableDefinitionEditor.this.getCaption());
            addField(startingNumberField = new IntegerField("Starting Number", td.isCoreType("Name") ? 200 : 100, 5));
            addField(gapField = new IntegerField("Gap", 100, 5));
        }

        @Override
        protected boolean process() {
            reorderFields(startingNumberField.getValue(), gapField.getValue());
            return true;
        }
    }

    private void processDefinitions(InputStream data, boolean update, StyledBuilder view) {
        try {
            if (update) {
                TableDefinition.loadDefinitions(getTransactionManager(), data, view);
            } else {
                TableDefinition.compareDefinitions(getTransactionManager(), data, view);
            }
        } catch(Exception error) {
            getApplication().access(() -> error(error));
        }
        view.update();
    }

    static class ClassSelector extends DataForm {

        private RadioChoiceField select;
        private final TableDefinition td;

        public ClassSelector(TableDefinition td) {
            super("Report");
            this.td = td;
        }

        @Override
        protected void buildFields() {
            String[] options;
            if(td != null && td.hasDetailInterface()) {
                options = new String[] { "All", "Current Module" };
            } else {
                options = new String[] { "All", "Current Module", "Current Data Class" };
            }
            select = new RadioChoiceField("Select", options);
            addField(select);
        }

        @Override
        protected boolean process() {
            close();
            ObjectIterator<TableDefinition> tds = null;
            switch(select.getValue()) {
                case 1 -> {
                    String cn = td.getClassName();
                    cn = cn.substring(0, cn.lastIndexOf('.'));
                    cn = cn.substring(cn.lastIndexOf('.') + 1);
                    cn = "ClassName LIKE '%." + cn + ".%'";
                    tds = StoredObject.list(TableDefinition.class, cn);
                }
                case 2 -> tds = ObjectIterator.create(td);
            }
            new Report(tds).view();
            return true;
        }

        @Override
        protected void execute(com.storedobject.vaadin.View parent, boolean doNotLock) {
            if(td == null) {
                //noinspection resource
                new Report(null).view();
                return;
            }
            super.execute(parent, doNotLock);
        }
    }

    static class Report extends PDFReport {

        private final ObjectIterator<TableDefinition> tds;
        private TableDefinition td;
        private int tdmCount = 0, mCount = 0;
        private static final String unknown = "Unknown";

        public Report(ObjectIterator<TableDefinition> tds) {
            super(Application.get());
            setFontSize(8);
            if(tds == null) {
                tds = StoredObject.list(TableDefinition.class, null, "ClassName");
            }
            this.tds = tds;
        }

        @Override
        public PDFRectangle getPageSize() {
            return super.getPageSize().rotate();
        }

        private String project() {
            if(tds == null || !tds.hasNext()) {
                return unknown;
            }
            td = tds.next();
            return name(false);
        }

        private String module() {
            return name(true);
        }

        private String name(boolean module) {
            String s = td.getClassName();
            int p = s.indexOf('.');
            if(p < 0) {
                return unknown;
            }
            s = s.substring(p + 1);
            p = s.indexOf('.');
            if(p < 0) {
                return unknown;
            }
            s = s.substring(p + 1);
            p = s.indexOf('.');
            if(p < 0) {
                return unknown;
            }
            if(module) {
                s = s.substring(p + 1);
                p = s.lastIndexOf('.');
                if (p < 0) {
                    return "None";
                }
            }
            return s.substring(0, p).toUpperCase();
        }

        @Override
        public Object getTitleText() {
            Text t = new Text();
            t.append(14, PDFFont.BOLD).append("Project: ").append(project()).newLine().append(12).append("Design Document");
            t.newLine().append(10).append("(Confidential)");
            return createCenteredCell(t);
        }

        @Override
        public void generateContent() {
            String module = "", m;
            while(true) {
                if(td == null) {
                    if(tds.hasNext()) {
                        td = tds.next();
                    } else {
                        break;
                    }
                }
                if(td.hasDetailInterface()) {
                    td = null;
                    continue;
                }
                m = module();
                if(!module.equals(m)) {
                    ++mCount;
                    tdmCount = 0;
                    module = m;
                    newPage();
                    add(createCenteredCell(createTitleText(mCount + ". " + module)));
                }
                ++tdmCount;
                printTable(td);
                td = null;
            }
        }

        private void printLinks(PDFTable table, TableDefinition td, ArrayList<TableDefinition> detailLinks) {
            String s;
            int order = 0;
            TableDefinition linkTable;
            for(LinkDefinition ld: td.listLinks(LinkDefinition.class, null, "DisplayOrder")) {
                if(ld.getDisplayOrder() > order) {
                    order = ld.getDisplayOrder();
                } else {
                    ++order;
                }
                table.addCell(createCenteredCell("Link: " + order));
                table.addCell(createCell(ld.getName()));
                s = ld.getObjectName().trim();
                table.addCell(createCell(s));
                if(s.contains("/")) {
                    s = s.substring(0, s.indexOf('/'));
                }
                if(s.startsWith("com.storedobject.")) {
                    table.addCell(createCell("Core Reference Link"));
                } else {
                    linkTable = StoredObject.get(TableDefinition.class, "lower(ClassName)='" + s.toLowerCase() + "'");
                    if(linkTable == null) {
                        table.addCell(createCell(createText("Missing!", getFontSize(), PDFColor.RED)));
                    } else {
                        if(linkTable.hasDetailInterface()) {
                            table.addCell(createCell("Detail Link"));
                            if(!linkTable.equals(td) && !detailLinks.contains(linkTable)) {
                                detailLinks.add(linkTable);
                            }
                        } else {
                            table.addCell(createCell("Reference Link"));
                        }
                    }
                }
                printNotes(table, ld.getNotes());
            }
        }

        private void printNotes(PDFTable table, String notes) {
            if(notes.isEmpty()) {
                return;
            }
            PDFCell c = createCenteredCell(new Text().append(6).append("Notes:"));
            c.setBorderWidthLeft(0);
            table.addCell(c);
            c = createCell(notes);
            c.setColumnSpan(table.getNumberOfColumns() - 1);
            table.addCell(c);
        }

        private void printIndices(@SuppressWarnings("unused") TableDefinition td) {
        }

        private void printColumns(TableDefinition td, ArrayList<TableDefinition> detailLinks) {
            PDFTable t = createTable(2, 10, 17, 5);
            addTitles(t, "", "Name", "Type", "Style/Notes");
            String s;
            int order = 0;
            for(ColumnDefinition cd: td.listLinks(ColumnDefinition.class, null, "DisplayOrder")) {
                if(cd.getDisplayOrder() > order) {
                    order = cd.getDisplayOrder();
                } else {
                    ++order;
                }
                t.addCell(createCenteredCell(order));
                s = cd.getCaption();
                if(!s.isEmpty()) {
                    s = s + "\n(" + cd.getName() + ")";
                } else {
                    s = StringUtility.makeLabel(cd.getName());
                }
                t.addCell(createCell(s));
                s = cd.getTypeValue();
                if(s.equals("Object")) {
                    s = cd.getParameters();
                } else if(s.equals("Object Text")) {
                    s = "Text (" + cd.getParameters() + ")";
                }
                t.addCell(createCell(s));
                if(!s.equals("Object") && !s.equals("Object Text")) {
                    s = "";
                } else {
                    s = cd.getParameters();
                }
                if(!cd.getSetAllowed() || cd.getEmptyAllowed()) {
                    if(!s.isEmpty()) {
                        s += "\n";
                    }
                    if(cd.getSetAllowed()) {
                        s += "Can be empty";
                    } else {
                        s += "Set not allowed";
                        if(cd.getEmptyAllowed()) {
                            s += ", Can be empty";
                        }
                    }
                }
                t.addCell(createCell(s));
                printNotes(t, cd.getNotes());
            }
            printLinks(t, td, detailLinks);
            add(t);
        }

        private void printTable(TableDefinition td) {
            printTable(td, 0);
        }

        private void printTable(TableDefinition td, int linkIndex) {
            String s;
            PDFCell c;
            PDFTable t = createTable(15, 80);
            t.addCell(createCenteredCell(createTitleText(mCount + "." + tdmCount + (linkIndex == 0 ? "" : ("." + linkIndex)))));
            s = td.getAbstractClass() ? "Abstract" : "";
            if(td.hasDetailInterface()) {
                if(!s.isEmpty()) {
                    s += " ";
                }
                s += "Detail";
            }
            t.addCell(createTitleText(s + " Class: " + td.getClassName()));
            s = td.getNotes();
            if(!s.isEmpty()) {
                c = createCell(s);
                c.setColumnSpan(2);
                t.addCell(c);
            }
            s = td.getParentClassName();
            if(!s.endsWith(".StoredObject") && !s.equals("StoredObject")) {
                t.addCell(createCell("Parent"));
                t.addCell(createCell(s));
            }
            s = td.getInterfaces();
            s = s.replace("Detail", "").replace(",", ", ").trim();
            if(s.endsWith(",")) {
                s = s.substring(0, s.length() - 1);
            }
            if(s.startsWith(",")) {
                s = s.substring(1).trim();
            }
            while(s.indexOf(",  ") > 0) {
                s = s.replace(",  ", ", ");
            }
            if(!s.isEmpty()) {
                printValue(t, "Implements", s);
            }
            printValue(t, "Display Columns", td.getDisplayColumns());
            printValue(t, "Search Columns", td.getSearchColumns());
            printValue(t, "Browse Columns", td.getBrowseColumns());
            printValue(t, "Protected Columns", td.getProtectedColumns());
            if(td.getSmall() || td.getSmallList()) {
                s = td.getSmall() ? "Small" : "";
                if(td.getSmallList()) {
                    if(!s.isEmpty()) {
                        s += ", ";
                    }
                    s += "Small List";
                }
                printValue(t, "Properties", s);
            }
            s = td.getFormTitle();
            if(!s.isEmpty()) {
                s = "Title: " + s;
            }
            if(td.getFormStyle() > 0) {
                if(!s.isEmpty()) {
                    s += ", ";
                }
                s += " Style: " + td.getFormStyleValue();
                printValue(t, "Form", s);
            }
            add(t);
            ArrayList<TableDefinition> detailLinks = new ArrayList<>();
            printColumns(td, detailLinks);
            printIndices(td);
            int linkCount = 0;
            for(TableDefinition link: detailLinks) {
                printTable(link, ++linkCount);
            }
        }

        private void printValue(PDFTable table, String caption, String value) {
            if(value.isEmpty()) {
                return;
            }
            table.addCell(createCell(caption));
            table.addCell(createCell(value));
        }
    }

    private class AdminPasswordForm extends DataForm {

        private final PasswordField adminPassword = new PasswordField("Administrator Password");
        private Runnable command;

        public AdminPasswordForm() {
            super("Password");
            addField(adminPassword);
            setRequired(adminPassword);
        }

        @Override
        protected boolean process() {
            clearAlerts();
            String password = adminPassword.getValue();
            try {
                if(!Database.get().validateSecurityPassword(password)) {
                    message("Invalid administrator password");
                    return false;
                }
            } catch(Exception e) {
                error(e);
                return true;
            }
            TableDefinitionEditor.this.adminPassword = password;
            close();
            if(command != null) {
                Runnable r = command;
                command = null;
                r.run();
            }
            return true;
        }
    }

    private class DeployTable extends View implements Transactional {

        private final Button proceed;
        private final Button deleteLogic;
        private final Button deleteTable;
        private final Button menuItem;
        private final Button exit;
        private TableDefinition td;
        private final TextField className, tableName;
        private final ELabel status;
        private int action;
        private JavaClass jc;
        private ArrayList<String> alterTable;

        DeployTable() {
            this(null);
        }

        DeployTable(TableDefinition tableDefinition) {
            setCaption("Data Class Deployer");
            className = new TextField("Name of the Data Class");
            className.setSpellCheck(false);
            className.setEnabled(false);
            tableName = new TextField("Name of the Data Table");
            tableName.setSpellCheck(false);
            tableName.setEnabled(false);
            VerticalLayout layout = new VerticalLayout();
            ButtonLayout buttons = new ButtonLayout();
            buttons.add(proceed = new Button("Proceed", this));
            buttons.add(deleteLogic = new ConfirmButton("Delete Logic",this));
            buttons.add(deleteTable = new ConfirmButton("Delete Table",this));
            buttons.add(menuItem = new Button("Create Menu Item", "menu", this));
            buttons.add(exit = new Button("Exit", this));
            layout.add(buttons);
            FormLayout form = new FormLayout();
            form.setColumns(1);
            ELabel sysWarning;
            form.add(sysWarning = new ELabel("Please make sure that System Data Classes are updated",
                    Application.COLOR_ERROR));
            sysWarning.setVisible(false);
            form.add(className);
            form.add(tableName);
            form.setWidth(null);
            layout.add(form);
            status = new ELabel();
            layout.add(status);
            layout.setMargin(true);
            setComponent(layout);
            setTable(tableDefinition);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            if(action < 0) {
                return;
            }
            super.execute(parent, doNotLock);
        }

        private String getTable() {
            if(td == null) {
                return "";
            }
            String t = td.getTableName(getTransactionManager());
            if(t == null) {
                action = -1;
            }
            return t == null ? "<Not Available>" : t;
        }

        private void setTable(TableDefinition tableDefinition) {
            jc = null;
            action = -1;
            proceed.setVisible(false);
            this.td = tableDefinition;
            className.setValue(td == null ? "" : td.getClassName());
            tableName.setValue("<Not set yet>");
            if(td == null) {
                deleteLogic.setVisible(false);
                deleteTable.setVisible(false);
                return;
            }
            deleteLogic.setVisible(true);
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
            deleteLogic.setVisible(true);
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
            JavaClassLoader.clearNoFoundCache();
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
                error("Unable to determine table/view name, please check error logs (include generic information)!");
                close();
                return;
            }
            tableName.setValue(tName);
            if(action == -1) {
                return;
            }
            boolean exists = td.isMasterObject() ? Database.get().viewExists(tName) : Database.get().tableExists(tName);
            if(!exists) {
                TransactionManager tm = getTransactionManager();
                if(Database.get().schemaExists(td.getSchemaName(tm))) {
                    status("Data " + (td.isMasterObject() ? "view" : "table") + " '" + td.getTableName(tm) +
                            "' does not exist in the database...\nYou may create it now...");
                    action(3);
                } else {
                    status("Schema '" + td.getSchemaName(tm) +
                            "' does not exist in the database...\nYou may create it now...");
                    action(2);
                }
                return;
            }
            try {
                alterTable = td.alterTable(getTransactionManager());
            } catch (Exception e) {
                error(e);
                return;
            }
            if(alterTable == null) {
                if(td.isMasterObject()) {
                    status("Data view structure looks fine now... Nothing else to do...",
                            Application.COLOR_SUCCESS);
                    action = 0;
                    proceed.setVisible(false);
                } else {
                    status("Data table structure looks fine now... You may reindex the table if needed...",
                            Application.COLOR_SUCCESS);
                    proceed.setText("Reindex Table");
                    action(5);
                }
                return;
            }
            StringBuilder s = new StringBuilder("Data ");
            if(td.isMasterObject()) {
                s.append("View");
            } else {
                s.append("Table");
            }
            s.append(" '").append(tName);
            s.append("' already exists in the database...\n");
            if(td.isMasterObject()) {
                s.append("You may drop and recreate it as follows:\n");
                s.append("(No risk of losing any data)");
            } else {
                s.append("You may alter it now as follows:\n");
                s.append("(Please make sure that you backed up your database before doing this)");
            }
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
            status(message, Application.COLOR_ERROR);
        }

        private void status(String message, String color) {
            if(!message.isEmpty()) {
                message(message);
            }
            String[] m = message.split("\\n");
            status.clear();
            for(String s: m) {
                if(!status.isEmpty()) {
                    status.newLine();
                }
                status.append(s, color);
            }
            status.update();
        }

        @Override
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
                    logic = new Logic(e + ":" + cName,
                            StringUtility.makeLabel(cName.substring(cName.lastIndexOf('.') + 1),
                                    false));
                    logic.setSingleInstance(true);
                }
                ObjectEditor<Logic> oe = new ObjectEditor<>(Logic.class, EditorAction.ALL);
                oe.setObject(logic);
                oe.editObject(logic);
                return;
            }
            if((c == proceed && action > 1) || (c == deleteLogic) || (c == deleteTable)) {
                if(adminPassword == null) {
                    message("The operation you selected requires administrator password! Please input the password and try again.");
                    command(this::clearAlerts);
                    return;
                }
            }
            if(c == deleteLogic) {
                delLogic();
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
                    case 0 -> {
                        return;
                    }
                    case 1 -> saveClass();
                    case 2 -> {
                        if(!Database.get().createSchema(td.getSchemaName(getTransactionManager()), adminPassword)) {
                            message("Error creating schema... Please seek help...");
                            return;
                        }
                    }
                    case 3 -> {
                        try {
                            @SuppressWarnings("unchecked") Class<? extends StoredObject> objectClass =
                                    (Class<? extends StoredObject>) JavaClassLoader.getLogic(td.getClassName());
                            if(!Database.get().createTable(objectClass, adminPassword)) {
                                message("Error creating data table... Please seek help...");
                                action = -1;
                                return;
                            }
                        } catch(Exception e) {
                            error(e);
                            action = -1;
                            return;
                        }
                    }
                    case 4 -> {
                        for(String command : alterTable) {
                            if(!Database.get().executeSQL(command, adminPassword)) {
                                message("Error altering data table... Please seek help...\nCommand: " + command);
                                action = -1;
                                return;
                            }
                        }
                    }
                    case 5 -> {
                        try {
                            @SuppressWarnings("unchecked") Class<? extends StoredObject> objectClass =
                                    (Class<? extends StoredObject>) JavaClassLoader.getLogic(td.getClassName());
                            commands = StoredObjectUtility.reindex(objectClass);
                        } catch(ClassNotFoundException e) {
                            error(e);
                            action = -1;
                            return;
                        }
                        for(String comm : commands) {
                            if(!Database.get().executeSQL(comm, adminPassword)) {
                                message("Error reindexing data table... Please seek help...\nCommand: " + comm);
                                action = -1;
                                return;
                            }
                        }
                        action = 0;
                        status("Reindexing done... Nothing else to do!", Application.COLOR_SUCCESS);
                        return;
                    }
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

        private void delLogic() {
            try {
                if(jc == null) {
                    jc = JavaClass.create(td.getClassName());
                }
                if(jc.getId() != null) {
                    Transaction t = getTransactionManager().createTransaction();
                    try {
                        jc.delete(t);
                        t.commit();
                        message("Logic dropped");
                    } catch(Exception e) {
                        t.rollback();
                        error(e);
                    }
                } else {
                    message("Logic doesn't exist");
                }
            } catch (Exception e) {
                action = -1;
                error(e);
            }
        }

        @SuppressWarnings("unchecked")
        private void delTable() {
            if(adminPassword == null) {
                command(this::delTable);
                return;
            }
            try {
                String tableName = td.getTableName(getTransactionManager());
                if(Database.get().tableExists(tableName) || Database.get().viewExists(tableName)) {
                    Class<? extends StoredObject> objectClass =
                            (Class<? extends StoredObject>)JavaClassLoader.getLogic(td.getClassName());
                    String[] commands = StoredObjectUtility.dropDDL(objectClass);
                    for(String comm: commands) {
                        if(!Database.get().executeSQL(comm, adminPassword)) {
                            message("Error dropping data table... Please seek help...\nCommand: " + comm);
                            action = -1;
                            return;
                        }
                    }
                    message("Table dropped");
                } else {
                    message("Table doesn't exist");
                }
            } catch(ClassNotFoundException e) {
                error(e);
                action = -1;
            }
        }
    }

}
