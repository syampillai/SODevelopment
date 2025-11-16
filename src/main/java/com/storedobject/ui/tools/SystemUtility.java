package com.storedobject.ui.tools;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.report.ObjectList;
import com.storedobject.report.ObjectListExcel;
import com.storedobject.ui.Application;
import com.storedobject.ui.*;
import com.storedobject.ui.common.ExcelDataUpload;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.storedobject.core.EditorAction.*;

public class SystemUtility extends View implements CloseableView, Transactional {

    private final TextField select, where, orderBy;
    private final TextField rawCommand;
    private final Checkbox any;
    private final ClassNameField from;
    private final Button executeSQL;
    private final Button pdf;
    private final Button downloadExcelData;
    private final Button downloadData;
    private final Button updateData;
    private final Button edit;
    private final Button editRaw;
    private final Button executeRaw;
    private final LongField rawId;
    private final LongField rawTranId;
    private final boolean isAdmin;
    private Class<? extends StoredObject> objectClass;
    private final IntegerField connectionAge;
    private final IntegerField limit;
    private final TextArea speech;
    private QueryBuilder<?> queryBuilder;

    public SystemUtility() {
        super("Utilities");
        isAdmin = getTransactionManager().getUser().isAdmin();
        select = new TextField("SELECT");
        any = new Checkbox("ANY");
        from = new ClassNameField("FROM");
        from.setWidth("60em");
        where = new TextField("WHERE");
        orderBy = new TextField("ORDER BY");
        limit = new IntegerField("Limit (0 for no limit)");
        FormLayout form = new FormLayout();
        form.setColumns(1);
        form.add(label("Execute SQL"));
        form.add(select);
        form.add(any);
        form.add(from);
        form.add(where);
        form.add(orderBy);
        form.add(limit);
        ButtonLayout buttons = new ButtonLayout();
        form.add(buttons);
        buttons.add(executeSQL = new Button("OK", this));
        buttons.add(new Button("Clear",  e -> clearFields()));
        buttons.add(pdf = new Button("PDF", this));
        buttons.add(downloadExcelData = new Button("Excel", this));
        buttons.add(downloadData = new Button("Download", this));
        buttons.add(updateData = new ConfirmButton("Update", "lightbulb", this));
        buttons.add(edit = new Button("Editor", VaadinIcon.EDIT, this));
        buttons.add(editRaw = new Button("Raw Editor", VaadinIcon.LIFEBUOY, this));
        buttons.add(new Button("View SQL", VaadinIcon.BUG, e -> viewSQL()));
        buttons.add(new Button("Raw Table Details", VaadinIcon.FILE_TABLE, e -> viewTable()));
        buttons.add(new Button("Search Deleted", VaadinIcon.SEARCH, e -> searchDeleted()));
        form.add(label("Execute Logic"));
        form.add(rawCommand = new TextField("Command"));
        buttons = new ButtonLayout();
        buttons.add(executeRaw = new Button("Execute Command", this));
        form.add(buttons);
        form.add(label("View Object"));
        rawId = new LongField(0L, 10);
        buttons = new ButtonLayout();
        buttons.add(new ELabel("Raw Object Id: "), rawId, new Button("View", e -> loadRaw()));
        form.add(buttons);
        form.add(label("View Transaction"));
        rawTranId = new LongField(0L, 10);
        buttons = new ButtonLayout();
        buttons.add(new ELabel("Raw Transaction Id: "),
                rawTranId,
                new Button("View", e -> viewTran()));
        form.add(buttons);
        form.add(label("Upload Data"));
        DataLoader dataLoader = new DataLoader(getApplication());
        UploadField dataUploadField = new UploadField(dataLoader::process);
        dataUploadField.getUploadComponent().setMaxFileSize(100000000);
        buttons = new ButtonLayout();
        buttons.add(new ELabel("Excel Data: "),
                new Button("Excel", e -> new ExcelDataUpload().execute()),
                new ELabel("Raw Data: "),
                dataUploadField);
        form.add(buttons);
        form.add(label("Debug SQL Connections"));
        buttons = new ButtonLayout();
        BooleanField connectionDebug;
        buttons.add(new ELabel("Debug"), connectionDebug = new BooleanField(SQLConnector.debug));
        connectionDebug.addValueChangeListener(e -> SQLConnector.debug = e.getValue());
        buttons.add(new ELabel("Age in Minutes:"), connectionAge = new IntegerField(5, 4));
        buttons.add(new Button("Download", e -> connInfo()));
        form.add(buttons);
        form.add(label("Miscellaneous"));
        speech = new TextArea("Text to Speak out");
        form.add(speech);
        new SpeechRecognition(speech);
        buttons = new ButtonLayout();
        buttons.add(new Button("Speak out", VaadinIcon.VOLUME_DOWN, e -> speakOut()));
        form.add(buttons);
        buttons = new ButtonLayout();
        buttons.add(new Button("Reload CORS", VaadinIcon.SPECIALIST, e -> {
            CORS.clear();
            message("CORS reloaded!");
        }));
        form.add(buttons);
        ELabelField appDetails = new ELabelField("Application Details");
        Application.get().information(appDetails);
        form.add(appDetails);
        setComponent(form);
        setFirstFocus(from);
    }

    private void speakOut() {
        if(!getApplication().isSpeakerOn()) {
            warning("Please toggle speaker output");
            return;
        }
        speak(speech.getValue());
    }

    private H3 label(String label) {
        return new H3(label);
    }

    private int browserActions() {
        return (isAdmin ? DELETE : 0) | VIEW | PDF | AUDIT | (any.getValue() ? ALLOW_ANY : 0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Application getApplication() {
        return super.getApplication();
    }

    private void clearFields() {
        select.clear();
        where.clear();
        orderBy.clear();
        limit.clear();
        from.focus();
    }

    @SuppressWarnings({"rawtypes", "unchecked", "resource", "DuplicatedCode"})
    @Override
    public void clicked(Component c) {
        clearAlerts();
        if(c == executeRaw) {
            String command = rawCommand.getValue().trim();
            getApplication().getServer().execute(command);
            return;
        }
        if(objectClass() == null) {
            return;
        }
        if(c == edit) {
            ObjectEditor.create(objectClass).execute();
            return;
        }
        if(c == editRaw) {
            //noinspection rawtypes
            new ObjectEditor(objectClass).execute();
            return;
        }
        if(queryBuilder() == null) {
            warning("Unable to build query");
            return;
        }
        if(c == downloadData) {
            TextContentProducer cp = new TextContentProducer() {
                @Override
                public void generateContent() throws Exception {
                    Writer w = getWriter();
                    ObjectIterator<? extends StoredObject> objects;
                    objects = queryBuilder.list();
                    objects = objects(objects);
                    for(StoredObject so: objects) {
                        so.save(w);
                    }
                }
            };
            getApplication().view(cp);
            return;
        }
        if(c == updateData) {
            new Update().execute();
            return;
        }
        long rowsRequired = limit.getValue();
        String cols = select.getValue().trim();
        if(c == executeSQL) {
            StringList columns;
            if(cols.startsWith("/")) {
                Query q = StoredObject.query(objectClass, cols, where.getValue(), orderBy.getValue());
                if(rowsRequired > 0) {
                    q = q.limit(rowsRequired);
                }
                new QueryGrid(q).execute();
                return;
            }
            columns = StringList.create(cols);
            Browser b = new Browser(objectClass, columns);
            b.execute();
            b.load(objects(queryBuilder.list()));
            return;
        }
        if(c == pdf) {
            new ObjectList(getApplication(), objectClass, any.getValue(),
                    cols.startsWith("/") ? StringList.EMPTY : StringList.create(cols)) {

                @Override
                public String getOrderBy() {
                    return orderBy.getValue().trim();
                }

                @Override
                public String getExtraCondition() {
                    return where.getValue();
                }

                @Override
                public QueryBuilder customizeQueryBuilder(QueryBuilder queryBuilder) {
                    return queryBuilder.limit(limit.getValue());
                }

                @Override
                public ObjectIterator customizeList(ObjectIterator objectList) {
                    return objects(objectList);
                }
            }.execute();
            return;
        }
        if(c == downloadExcelData) {
            new ObjectListExcel(getApplication(), objectClass, any.getValue(),
                    cols.startsWith("/") ? StringList.EMPTY : StringList.create(cols)) {

                @Override
                public String getOrderBy() {
                    return orderBy.getValue().trim();
                }

                @Override
                public String getExtraCondition() {
                    return where.getValue();
                }

                @Override
                public QueryBuilder customizeQueryBuilder(QueryBuilder queryBuilder) {
                    return queryBuilder.limit(limit.getValue());
                }

                @Override
                public ObjectIterator customizeList(ObjectIterator objectList) {
                    return objects(objectList);
                }
            }.execute();
        }
    }

    private void connInfo() {
        TextContentProducer cp = new TextContentProducer() {
            @Override
            public void generateContent() {
                Writer w = getWriter();
                SQLConnector.getDebugInfo(connectionAge.getValue()).forEach(d -> {
                    try {
                        w.write(d);
                        w.write("\n\n");
                    } catch (IOException ignored) {
                    }
                });
            }
        };
        getApplication().view(cp);
    }

    private void viewTran() {
        long t = rawTranId.getValue();
        if(t == 0) {
            return;
        }
        TransactionInformation ti = TransactionInformation.get(new BigInteger("" + t));
        if(ti == null) {
            warning("Transaction not found: " + t);
            return;
        }
        StringBuilder s = new StringBuilder("Transaction: ");
        s.append(t).append("\n");
        ti.dump(s);
        TextArea ta = new TextArea();
        ta.setWidthFull();
        ta.setValue(s.toString());
        ta.setReadOnly(true);
        View.createCloseableView(ta, "Transaction " + t).execute();
    }

    private void searchDeleted() {
        if(objectClass() == null) {
            return;
        }
        clearAlerts();
        String condition = where.getValue().trim();
        if(condition.isEmpty()) {
            warning("No condition specified!");
            return;
        }
        List<Id> ids = StoredObject.listDeletedIds(ClassAttribute.get(objectClass), condition, 0);
        if(ids.isEmpty()) {
            message("No deleted entries found!");
            return;
        }
        TextView tv = new TextView("Deleted Entries");
        tv.blueMessage("Class: " + objectClass.getName()).newLine();
        tv.blueMessage("Deleted entries matching the condition: " + condition).newLine();
        for(Id id: ids) {
            tv.newLine(true);
            tv.append("Id = " + id);
        }
        tv.update();
        tv.setWindowMode(false);
        tv.execute();
    }

    @SuppressWarnings("rawtypes")
    private ObjectIterator objects(ObjectIterator objectList) {
        long rowsRequired = limit.getValue();
        if(rowsRequired > 0) {
            return objectList.limit(rowsRequired);
        }
        return objectList;
    }

    private void loadRaw() {
        Id id = new Id(new BigInteger("" + rawId.getValue()));
        StoredObject so = StoredObject.get(id);
        if(so == null) {
            clearAlerts();
            so = StoredObject.getDeleted(id);
            if(so == null) {
                warning("No object found for Id = " + id);
                return;
            }
            warning("This is a deleted entry!");
        }
        if(so instanceof StreamData) {
            getApplication().view("Id " + so.getId() + ", Transaction " + so.getTransactionId(), (StreamData)so);
            return;
        }
        getApplication().view(StringUtility.makeLabel(so.getClass()) + " (Id " + so.getId()
                        + ", Transaction " + so.getTransactionId() + ")" + (so.undeleted() ? " - DELETED" : ""), so,
                so.undeleted() ? new ObjectViewerButton<>("Undelete", (oe, o) -> {
                    oe.close();
                    undelete(o);
                }) : null,
                new ObjectViewerButton<>("Audit Trail", (oe, o) -> new ObjectHistoryGrid<>(o).execute()));
    }
    
    private void undelete(StoredObject so) {
        if(transact(so::undelete)) {
            message("Undeleted successfully");
        }
    }

    private Class<? extends StoredObject> objectClass() {
        objectClass = from.getObjectClass();
        if(objectClass == null) {
            warning("Class not found: " + from.getValue());
            return null;
        }
        clearAlerts();
        return objectClass;
    }

    private QueryBuilder<?> queryBuilder() {
        if(objectClass() == null) {
            return null;
        }
        if(queryBuilder == null || queryBuilder.getObjectClass() != objectClass) {
            queryBuilder = QueryBuilder.from(objectClass);
        }
        String cols = select.getValue().trim();
        if(cols.isEmpty()) {
            cols = StoredObjectUtility.browseColumns(objectClass).toString(", ");
            select.setValue(cols);
        } else {
            if(!cols.startsWith("/")) {
                cols = StringList.create(cols).toString(", ");
                select.setValue(cols);
            }
        }
        return queryBuilder.columns(cols).where(where.getValue().trim()).orderBy(orderBy.getValue().trim())
                .any(any.getValue()).limit(limit.getValue());
    }

    private void viewSQL() {
        if(queryBuilder() == null) {
            return;
        }
        String q;
        try {
            q = queryBuilder.querySQL();
        } catch(Throwable e) {
            QueryBuilder<?> qb = QueryBuilder.from(objectClass);
            q = qb.querySQL();
        }
        TextView tv = new TextView("SQL");
        tv.append(q).update();
        tv.execute();
    }

    private void viewTable() {
        if(objectClass() == null) {
            return;
        }
        ClassAttribute<?> ca = ClassAttribute.get(objectClass);
        String tn = ca.getModuleName() + "." + ca.getTableName();
        List<String[]> details = Database.get().columnDetails(tn);
        if(details == null) {
            warning("Unable to obtain table details of " + objectClass.getName());
            return;
        }
        TextView tv = new TextView("Table Details");
        tv.blueMessage("Table details of " + objectClass.getName()).newLine();
        for(String[] ss: details) {
            tv.newLine(true);
            for(String s: ss) {
                tv.append(s).append(' ');
            }
        }
        details = Database.get().foreignKeyConstraints(tn);
        tv.newLine(true).blueMessage("Foreign Key Constraints:");
        if(details.isEmpty()) {
            tv.newLine(true).append("None");
        } else {
            for(String[] d: details) {
                tv.newLine(true).append('"').append(d[11]).append("\" FOREIGN KEY (").append(d[7])
                        .append(") REFERENCES ").append(d[1]).append('.').append(d[2]);
            }
        }
        details = Database.get().dependentConstraints(tn);
        if(details.isEmpty()) {
            tv.newLine(true).append("None");
        } else {
            tv.newLine(true).blueMessage("Dependents (Referencing this table):");
            for(String[] d: details) {
                tv.newLine(true).append('"').append(d[11]).append("\" FOREIGN KEY ").append(d[5]).append('.')
                        .append(d[6]).append('(').append(d[7]).append(')');
            }
        }
        tv.newLine().update();
        tv.execute();
    }

    class DataLoader implements Comparator<CharSequence> {

        private ObjectViewer view = null;
        private final Application a;

        private DataLoader(Application a) {
            this.a = a;
        }

        public void process(InputStream in, @SuppressWarnings("unused") String mimeType) {
            try {
                int count = StoredObject.load(getApplication().getTransactionManager(), in, this);
                a.access(() -> Application.message("Entries created: " + count));
            } catch (Exception e) {
                a.access(() -> Application.error(e));
            }
        }

        @Override
        public int compare(CharSequence o1, CharSequence o2) {
            if(view == null) {
                a.access(() -> {
                   view = new ObjectViewer();
                   view.execute();
                });
            }
            view.setValues(o1, o2);
            while(view.getDecision() < 0) {
                try {
                    view.wait();
                } catch (InterruptedException ignored) {
                }
            }
            return view.getDecision();
        }
    }

    static class ObjectViewer extends View {

        private final TextArea left, right;
        private final Button yes;
        private final Button no;
        private int decision = -1;

        public ObjectViewer() {
            super("Verify");
            left = new TextArea();
            left.setEnabled(false);
            right = new TextArea();
            right.setEnabled(false);
            Div g = new Div();
            ButtonLayout b = new ButtonLayout();
            b.add(new ELabel("Are these same?"));
            b.add(no = new Button("No", this));
            no.setEnabled(false);
            b.add(yes = new Button("Yes", this));
            yes.setEnabled(false);
            g.add(b);
            SplitLayout hp = new SplitLayout(left, right);
            hp.setOrientation(SplitLayout.Orientation.HORIZONTAL);
            g.add(hp);
            setComponent(g);
        }

        @Override
        public void clicked(Component c) {
            if(c == no) {
                decision = 1;
            } else {
                decision = 0;
            }
            no.setEnabled(false);
            yes.setEnabled(false);
            this.notifyAll();
        }

        protected void setValues(CharSequence o1, CharSequence o2) {
            decision = -1;
            left.setValue(o1.toString());
            right.setValue(o2.toString());
            yes.setEnabled(true);
            no.setEnabled(true);
        }

        protected int getDecision() {
            return decision;
        }
    }

    private static String className(Object object) {
        return className(object.getClass());
    }

    private static String className(Class<?> klass) {
        String s = klass.getName();
        boolean core = s.startsWith("com.storedobject.");
        if(!core && StringUtility.getCharCount(s, '.') <= 3) {
            return s;
        }
        s = s.substring(s.indexOf('.') + 1);
        s = s.substring(s.indexOf('.') + 1);
        if(!core) {
            s = s.substring(s.indexOf('.') + 1);
        }
        return s;
    }

    private class Browser<T extends StoredObject> extends ObjectBrowser<T> {

        private Button links, masters, count;

        private Browser(Class<T> objectClass, StringList columns) {
            super(objectClass, columns, browserActions(), className(objectClass));
        }

        private Browser(Class<T> objectClass) {
            super(objectClass, (isAdmin ? DELETE : 0) | VIEW | PDF | AUDIT, className(objectClass));
        }

        @Override
        protected void createExtraButtons() {
            links = new Button("Links", "children", this);
            masters = new Button("Masters", "angle-double-up", this);
            count = new Button("Entry Count", "hash", e -> message("Entries: " + size()));
        }

        @Override
        protected void addExtraButtons() {
            buttonPanel.add(links);
            buttonPanel.add(masters);
            buttonPanel.add(count);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public void clicked(Component c) {
            if(c == links || c == masters || c == view) {
                T selected = selected();
                if(selected == null) {
                    return;
                }
                if(c == view) {
                    getObjectEditor().setCaption(className(selected) + " Id " + selected.getId() + " Transaction "
                            + selected.getTransactionId());
                    super.clicked(c);
                    return;
                }
                ArrayList<Integer> types = new ArrayList<>();
                ObjectIterator<StoredObject> objects;
                if(c == links) {
                    objects = selected.listLinks(StoredObject.class, true);
                } else {
                    objects = selected.listMasters(StoredObject.class, true);
                }
                Browser<StoredObject> b;
                int family, count = 0;
                for(StoredObject so: objects) {
                    ++count;
                    family = StoredObjectUtility.family(so.getClass());
                    if(types.contains(family)) {
                        continue;
                    }
                    types.add(family);
                    b = new Browser(so.getClass());
                    b.setCaption(className(so) + " - " + (c == links ? "Link" : "Master") + "s of " + selected.getId()
                            + "/" + selected.getTransactionId() + " " + className(getObjectClass()));
                    if(c == links) {
                        b.setObjects((Iterable<StoredObject>)selected.listLinks(so.getClass()));
                    } else {
                        b.setObjects((Iterable<StoredObject>)selected.listMasters(so.getClass()));
                    }
                    b.execute();
                }
                if(count == 0) {
                    warning("No " + (c == links ? "links" : "masters") + " found");
                }
                return;
            }
            super.clicked(c);
        }
    }

    private class Update extends DataForm {

        private RadioChoiceField batch;

        public Update() {
            super("Update Entries");
        }

        @Override
        protected void buildFields() {
            batch = new RadioChoiceField("Update Mode", new String[] { "Batch", "Entry by entry" });
            addField(batch);
            ok.setText("Proceed");
        }

        @Override
        protected boolean process() {
            boolean batch = this.batch.getValue() == 0;
            int count = 0;
            Transaction t = null;
            try (ObjectIterator<?> list = queryBuilder.list()) {
                if (batch) {
                    t = getTransactionManager().createTransaction();
                }
                for (StoredObject so : list) {
                    if (!batch) {
                        t = getTransactionManager().createTransaction();
                    }
                    so.save(t);
                    if (!batch) {
                        t.commit();
                    }
                    ++count;
                }
                if (batch) {
                    t.commit();
                }
            } catch (Exception e) {
                if (t != null) {
                    t.rollback();
                }
                error(e);
            }
            tray("Entries updated: " + count);
            return true;
        }
    }
}
