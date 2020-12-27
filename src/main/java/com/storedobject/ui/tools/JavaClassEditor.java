package com.storedobject.ui.tools;

import com.storedobject.core.*;
import com.storedobject.tools.JavaTool;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.TextView;
import com.storedobject.ui.UploadProcessorView;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ConfirmButton;
import com.storedobject.vaadin.PopupButton;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.io.*;
import java.sql.Timestamp;

public class JavaClassEditor extends ObjectEditor<JavaClass> {

    private TextArea source;
    private final TextArea error;
    private TextField name;
    private Button compileSource, compileSourceAll, format, createSourceAll, downloadAll, uploadAll, uploadCompile, uploadCompare;
    private PopupButton uploadMenu;
    private final View errors;

    public JavaClassEditor() {
        super(JavaClass.class);
        error = new TextArea();
        errors = createCloseableView(error, "Compilation Errors");
        addField("VersionInformation", "Source");
        addConstructedListener(o -> setFieldReadOnly("Generated"));
    }

    @Override
    protected boolean includeField(String fieldName) {
        if(fieldName.equals("SourceData") || fieldName.equals("ClassData") || fieldName.equals("Version")) {
            return false;
        }
        return super.includeField(fieldName);
    }

    @Override
    protected int getFieldOrder(String fieldName) {
        switch (fieldName) {
            case "Source":
                return 100001;
            case "VersionInformation":
                return 100000;
        }
        return super.getFieldOrder(fieldName);
    }

    @Override
    protected HasValue<?, ?> createField(String fieldName, Class<?> fieldType, String label) {
        if("Source".equals(fieldName)) {
            source = new TextArea(label);
            setColumnSpan(source, 2);
            return source;
        }
        return super.createField(fieldName, fieldType, label);
    }

    @Override
    protected void customizeField(String fieldName, HasValue<?, ?> field) {
        if("Name".equals(fieldName)) {
            name = (TextField) field;
        }
        super.customizeField(fieldName, field);
    }

    public String getVersionInformation() {
        JavaClass jc = getObject();
        if(jc == null) {
            return "";
        }
        Timestamp t = jc.timestamp();
        Person p = jc.person();
        StringBuilder s = new StringBuilder();
        s.append(jc.getVersion());
        if(t != null) {
            s.append(" / Modified at ").append(DateUtility.format(t));
        }
        if(p != null) {
            s.append(" by ").append(p.getName());
        }
        return s.toString();
    }

    @Override
    protected void createExtraButtons() {
        compileSource = new Button("Compile", this);
        compileSourceAll = new ConfirmButton("Compile All", this);
        format = new Button("Format", this);
        createSourceAll = new Button("Create Source Files", "download", this);
        downloadAll = new Button("Download All", "download", this);
        uploadAll = new Button("Bulk Upload", "upload", this);
        uploadCompile = new Button("Bulk Upload & Deploy", "upload", this);
        uploadCompare = new Button("Bulk Upload & Compare", "upload", this);
        uploadMenu = new PopupButton("Upload");
        uploadMenu.add(uploadAll, uploadCompare, uploadCompile);
    }

    @Override
    protected void addExtraButtons() {
        buttonPanel.add(compileSource, compileSourceAll, format, createSourceAll, downloadAll, uploadMenu);
    }

    public String getSource() {
        JavaClass javaClass = getObject();
        if(javaClass == null) {
            return "";
        }
        try {
            javaClass.download();
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(javaClass.getSourceStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            br.close();
            return sb.toString();
        } catch (NullPointerException ignored) {
        } catch(Exception e) {
            message(e);
        }
        return "";
    }

    public void setSource(String source) {
    }

    @SuppressWarnings("resource")
    @Override
    public void clicked(Component c) {
        if(c == format) {
            JavaSourceWriter w = null;
            try {
                StringWriter sw = new StringWriter();
                w = new JavaSourceWriter(sw);
                w.write(source.getValue());
                w.close();
                source.setValue(sw.toString());
            } catch(Exception e) {
                message(e);
            } finally {
                try {
                    if (w != null) {
                        w.close();
                    }
                } catch(Exception ignore) {
                }
            }
            return;
        }
        if(c == compileSource) {
            String s = name.getValue().trim();
            if(!JavaClass.checkName(s)) {
                error("Invalid class name");
                return;
            }
            JavaClass javaClass = new JavaClass(s);
            try {
                javaClass.setSourceCode(source.getValue());
            } catch(Exception e) {
                message(e);
                return;
            }
            s = javaClass.compile();
            if(s == null) {
                message("Compilation successful");
                errors.close();
            } else {
                error.setValue(s);
                errors.execute();
            }
            return;
        }
        if(c == compileSourceAll) {
            clicked(createSourceAll);
        }
        if(c == createSourceAll || c == compileSourceAll) {
            final Component pressed = c;
            TextView v = new TextView((pressed == createSourceAll ? "Creat" : "Compil") + "ing classes");
            v.setProcessor(() -> {
                int total = 0, count = 0, errorCount = 0;
                JavaClass errorJC = null;
                for(JavaClass jc: StoredObject.list(JavaClass.class)) {
                    ++total;
                    try {
                        if(jc.getGenerated()) {
                            continue;
                        }
                        jc.download();
                        ++count;
                        if(pressed == compileSourceAll) {
                            if(jc.compile() != null) {
                                v.redMessage(jc.getName());
                                errorJC = jc;
                                ++errorCount;
                            } else {
                                v.blueMessage(jc.getName());
                            }
                        }
                    } catch (Exception e) {
                        error(e);
                    }
                }
                if(pressed == createSourceAll) {
                    v.message("Source files created: " + count + "/" + total);
                    v.setCaption("Sources Created");
                } else {
                    if(errorJC != null && getObject() == null) {
                        JavaClass jc = errorJC;
                        getApplication().access(() -> setObject(jc));
                    }
                    v.newLine(true).append("Total classes: ").append(count).append(", ");
                    v.append("Classes with errors: " + errorCount, errorCount == 0 ? "blue" : "red");
                    v.update();
                    v.setCaption("Compilation Done");
                }
            });
            v.execute();
            return;
        }
        if(c == downloadAll) {
            ContentProducer cp = new TextContentProducer() {
                @Override
                public void generateContent() throws Exception {
                    Writer w = getWriter();
                    for(JavaClass jc: StoredObject.list(JavaClass.class, "NOT Generated")) {
                        jc.save(w);
                    }
                }
            };
            getApplication().view(cp);
            return;
        }
        if(c == uploadAll || c == uploadCompile || c == uploadCompare) {
            new UploadClasses(c == uploadAll ? 0 : (c == uploadCompile ? 2 : 1)).execute();
            return;
        }
        super.clicked(c);
    }

    @Override
    protected boolean save() throws Exception {
        Transaction tran = null;
        try {
            tran = getTransactionManager().createTransaction();
            JavaClass javaClass = getObject();
            javaClass.setSourceCode(source.getValue());
            javaClass.setTransaction(tran);
            String m = javaClass.upload();
            if(m != null) {
                tran.rollback();
                error.setValue(m);
                errors.execute();
                return false;
            }
            tran.commit();
            message("Save successful");
            errors.close();
            return true;
        } catch(Exception e) {
            if(tran != null) {
                try {
                    tran.rollback();
                } catch(Exception ignore) {
                }
            }
            throw e;
        }
    }

    @Override
    public boolean canAdd() {
        source.setValue("");
        return true;
    }

    @Override
    public boolean canEdit() {
        JavaClass javaClass = getObject();
        if(javaClass.getGenerated()) {
            warning("This code was generated by the tools... Can not be edited...");
            return false;
        }
        return true;
    }

    private static class UploadClasses extends UploadProcessorView {

        private final int action; // 0: Upload only, 1: Upload & compare, 2: Upload & deploy

        public UploadClasses(int action) {
            super((action == 0 ? "Upload" : (action == 2 ? "Deploy" : "Analyz")) + "ing classes", "Upload Source");
            this.action = action;
        }

        @Override
        public void process(InputStream data, String dataType) {
            try {
                switch (action) {
                    case 0:
                        JavaTool.loadDefinitions(getTransactionManager(), data, this);
                        break;
                    case 1:
                        JavaTool.compareDefinitions(getTransactionManager(), data, this);
                        break;
                    case 2:
                        JavaTool.updateDefinitions(getTransactionManager(), data, this);
                        break;
                }
            } catch (Throwable error) {
                redMessage(error);
            }
        }
    }
}
