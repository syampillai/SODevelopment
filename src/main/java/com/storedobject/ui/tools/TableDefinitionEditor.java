package com.storedobject.ui.tools;

import com.storedobject.common.SOException;
import com.storedobject.core.*;
import com.storedobject.pdf.*;
import com.storedobject.tools.ColumnDefinition;
import com.storedobject.tools.LinkDefinition;
import com.storedobject.tools.TableDefinition;
import com.storedobject.ui.Application;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextArea;

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
    private String password;
    private TableDeployer deployer;

    public TableDefinitionEditor() {
        this(EditorAction.ALL);
    }

    public TableDefinitionEditor(int actions) {
        super(TableDefinition.class, actions, "Data Class");
        addField("VersionInformation");
    }

    @Override
    public void createExtraButtons() {
        reorderFields = new Button("Reorder Fields", "sort", this);
        loadParent = new Button("Load Parent", "angle-double-up", this);
        viewChildren = new Button("Children", this);
        editSource = new Button("Edit Source", "editor:mode-edit", this);
        viewSource = new Button("View Source", this);
        compileSource = new Button("Compile Source",this);
        deploy = new Button("Deploy", "truck", this);
        upload = new Button("Upload", this);
        download = new Button("Download", this);
        downloadAll = new Button("Download All", "download", this);
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

    private boolean notAdmin() {
        if(password == null) {
            if(deployer == null) {
                deployer = new TableDeployer();
            }
            try {
                password = deployer.getAdminPassword();
            } catch (Exception e) {
                password = null;
            }
            if(password == null) {
                message("Please enter 'Administrator password' first and try again...");
                deployer.acceptPassword(this);
            }
        }
        return password == null;
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

    @SuppressWarnings("resource")
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
            if(notAdmin()) {
                return;
            }
            new DeployAll().execute();
            return;
        }
        if(c == upload) {
            new UploadBox().execute(this);
            return;
        }
        if(c == download || c == downloadAll) {
            final Component button = c;
            ContentProducer cp = new TextContentProducer() {
                @Override
                public void generateContent() throws Exception {
                    if(button == download) {
                        getObject().save(getWriter());
                    } else {
                        Writer w = getWriter();
                        for(TableDefinition td: StoredObject.list(TableDefinition.class)) {
                            td.save(w);
                        }
                    }
                }
            };
            getApplication().view(cp);
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
                            if(password == null && deployer != null) {
                                try {
                                    password = deployer.getAdminPassword();
                                } catch(Exception ignore) {
                                }
                            }
                            deployer = new TableDeployer(td);
                            deployer.setAdminPassword(password);
                            deployer.execute(this);
                        }
                    } else {
                        if(c == deploy) {
                            error("Please fix compilation errors first...");
                        }
                    }
                } else {
                    TextArea ta = new TextArea();
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
            v.append("Classes with errors: " + errorCount, errorCount == 0 ? "blue" : "red");
            v.update();
        }
        if(!skipCompletionLabel) {
            v.setCaption(compile ? "Compilation Done" : "Sources Created");
        }
    }

    private class DeployAll extends DataForm {

        private TextField filter;

        public DeployAll() {
            super("Deploy All Data Classes");
        }

        @Override
        protected void buildFields() {
            addField(filter = new TextField("Class Name Filter:"));
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
        if (notAdmin()) {
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
                        if (td.deploy(tm, password, false)) {
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
                    if (totalCount % 10 == 0) {
                        if (count == 0 && errors == 0) {
                            view.clear();
                        }
                        view.setProgressCaption(caption + round + " (Status: Deployed: " + count + "/" + totalCount + "/" + allCount +
                                ", Errors: " + errors + ", Remaining: " + tds.size() + ") ");
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
                    errors > 0 ? "red" : (count > 0 ? "black" : "blue"));
            view.update();
            view.setCaption("Classes Deployed");
        });
        view.execute();
    }

    @SuppressWarnings("resource")
    @Override
    public boolean canDelete() {
        TableDefinition td = getObject();
        JavaClass jc = JavaClass.create(td.getClassName());
        if(jc.getId() != null) {
            if(notAdmin()) {
                return false;
            }
            message("Please delete the generated Java Class first...");
            TableDeployer t = new TableDeployer();
            t.setAdminPassword(password);
            t.deleteTable(td, this);
            return false;
        }
        return true;
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

    protected static boolean isDetail(TableDefinition td) {
        String s = td.getInterfaces().trim();
        return s.equals("Detail") || s.endsWith(" Detail") || s.contains(" Detail ");
    }

    class UploadBox extends ProcessView {

        private final TextArea definition;
        private final TableDefinition td;

        public UploadBox() {
            super("Definition Upload");
            definition = new TextArea("Definition");
            definition.setWidthFull();
            td = getObject();
            if(td == null) {
                append("Current definition, if any, will be replaced");
            } else {
                append("Class: ").append(td.getClassName()).newLine();
                append("Current class definition will be deleted and/or replaced with the new definition", "red");
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
            //noinspection ResultOfMethodCallIgnored
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
            load(root);
        }

        public ClassTreeBrowser(String caption) {
            super(TableDefinition.class, new ClassTreeBuilder(), caption);
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

    private static class ClassTreeBuilder implements ObjectTreeBuilder {

        @SuppressWarnings("unchecked")
        @Override
        public <O extends StoredObject> ObjectIterator<O> listChildren(O parent) {
            return (ObjectIterator<O>) StoredObject.list(TableDefinition.class, "lower(ParentClassName)='" +
                    ((TableDefinition)parent).getClassName().trim().toLowerCase() + "'");
        }

        @SuppressWarnings("unchecked")
        @Override
        public <O extends StoredObject> O getParent(O child) {
            String pcn = ((TableDefinition)child).getParentClassName().trim().toLowerCase();
            return pcn.isEmpty() ? null : (O)StoredObject.get(TableDefinition.class, "lower(ClassName)='" + pcn + "'");
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
        protected void buildFields() {
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
            if(td != null && isDetail(td)) {
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
            switch (select.getValue()) {
                case 1:
                    String cn = td.getClassName();
                    cn = cn.substring(0, cn.lastIndexOf('.'));
                    cn = cn.substring(cn.lastIndexOf('.') + 1);
                    cn = "ClassName LIKE '%." + cn + ".%'";
                    tds = StoredObject.list(TableDefinition.class, cn);
                    break;
                case 2:
                    tds = ObjectIterator.create(td);
                    break;
            }
            new Report(tds).view();
            return true;
        }

        @Override
        protected void execute(com.storedobject.vaadin.View parent, boolean doNotLock) {
            if(td == null) {
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
            return s.substring(0, p).toUpperCase();
        }

        private String module() {
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
            s = s.substring(p + 1);
            p = s.lastIndexOf('.');
            if(p < 0) {
                return "None";
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
                if(isDetail(td)) {
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
                        if(isDetail(linkTable)) {
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

        private void printIndices(TableDefinition td) {
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
            if(isDetail(td)) {
                if(s.length() > 0) {
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
}
