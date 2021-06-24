package com.storedobject.ui;

import com.storedobject.common.Geolocation;
import com.storedobject.common.SOException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.report.ObjectData;
import com.storedobject.report.ObjectPDF;
import com.storedobject.report.ReportColumn;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

import java.util.HashMap;
import java.util.List;

public class ObjectReport<T extends StoredObject> extends DataForm {

    private final ObjectPDF<T> report;
    private final HashMap<ReportColumn<?>, HasValue<?, ?>> fields = new HashMap<>();

    public ObjectReport(Class<T> objectClass) {
        this(objectClass, (StringList)null);
    }

    public ObjectReport(Class<T> objectClass, StringList columns) {
        this(objectClass, false, columns, -1);
    }

    public ObjectReport(Class<T> objectClass, String columns) {
        this(objectClass, StringList.create(columns));
    }

    public ObjectReport(Class<T> objectClass, boolean any) {
        this(objectClass, any, (StringList)null);
    }

    public ObjectReport(Class<T> objectClass, boolean any, StringList columns) {
        this(objectClass, any, columns, -1);
    }

    public ObjectReport(Class<T> objectClass, boolean any, String columns) {
        this(objectClass, any, StringList.create(columns));
    }

    public ObjectReport(ObjectPDF<T> report) throws Exception {
        this(report.save(new StringBuilder()).substring(2));
    }

    private ObjectReport(Class<T> objectClass, boolean any, StringList columns, int blockNumber) {
        super("Report");
        report = new ObjectPDF<>(Application.get());
        report.createBlock(blockNumber, objectClass, any, columns);
        setCaption(report.getTitleText());
    }

    @SuppressWarnings("unchecked")
    public ObjectReport(String className) throws Exception {
        this((Class<T>)createClass(className), createAny(className), null, createBlockNumber(className));
        if(createPosition(className) != null) {
            throw new SOException("Invalid block number - " + className);
        }
        int p = className.indexOf('|');
        if(p <= 0) {
            return;
        }
        ObjectPDF<T>.PDFBlock<?> block = report.getBlocks().get(0);
        String s;
        className = className.substring(p + 1);
        //noinspection LoopStatementThatDoesntLoop
        while (true) {
            if (className.startsWith("L:") || className.startsWith("(")) {
                break;
            }
            p = className.indexOf('|');
            if (p < 0) {
                setCaption(className);
                return;
            }
            if (p > 1) {
                setCaption(className.substring(0, p));
            }
            className = className.substring(p + 1);
            if (className.startsWith("L:") || className.startsWith("(")) {
                break;
            }
            p = className.indexOf('|');
            if (p < 0) {
                s = className;
                className = "";
            } else {
                s = className.substring(0, p);
                className = className.substring(p + 1);
            }
            if (!s.isEmpty()) {
                block.setColumns(StringList.create(s));
            }
            break;
        }
        while(className.length() > 0) {
            if(className.startsWith("(")) {
                if(createPosition(className) != null) {
                    throw new SOException("Invalid block number - " + className);
                }
                block = report.createBlock(createBlockNumber(className), (Class<? extends StoredObject>)createClass(className), createAny(className));
                p = className.indexOf('|');
                if(p <= 0) {
                    return;
                }
                className = className.substring(p + 1);
                if(className.startsWith("L:") || className.startsWith("(")) {
                    continue;
                }
                p = className.indexOf('|');
                if(p < 0) {
                    block.setTitleText(className);
                    return;
                }
                if(p > 1) {
                    block.setTitleText(className.substring(0, p));
                }
                className = className.substring(p + 1);
                if(className.startsWith("L:") || className.startsWith("(")) {
                    continue;
                }
                p = className.indexOf('|');
                if(p < 0) {
                    s = className;
                    className = "";
                } else {
                    s = className.substring(0, p);
                    className = className.substring(p + 1);
                }
                if(!s.isEmpty()) {
                    block.setColumns(StringList.create(s));
                }
                continue;
            }
            if(!className.startsWith("L:")) {
                throw new SOException("Invalid Report Definition - " + className);
            }
            className = className.substring(2);
            block = report.createBlock(createBlockNumber(className), (Class<? extends StoredObject>)createClass(className), createAny(className),
                    createLink(className), null, createPosition(className));
            if(block == null) {
                throw new SOException("Invalid Report Link Position - " + className);
            }
            p = className.indexOf('|');
            if(p < 0) {
                return;
            }
            className = className.substring(p + 1);
            p = className.indexOf('|');
            if(p < 0) {
                block.setTitleText(className);
                return;
            }
            if(p > 1) {
                block.setTitleText(className.substring(0, p));
            }
            className = className.substring(p + 1);
            if(className.startsWith("L:") || className.startsWith("(")) {
                continue;
            }
            p = className.indexOf('|');
            if(p < 0) {
                s = className;
                className = "";
            } else {
                s = className.substring(0, p);
                className = className.substring(p + 1);
            }
            if(!s.isEmpty()) {
                block.setColumns(StringList.create(s));
            }
        }
    }

    private static Class<?> createClass(String className) throws Exception {
        className = className.trim();
        if(className.startsWith("(")) {
            className = className.substring(className.indexOf(')') + 1).trim();
        }
        int p = className.indexOf('|');
        if(p >= 0) {
            className = className.substring(0, p);
        }
        p = className.indexOf('/');
        if(p >= 0) {
            className = className.substring(0, p);
        }
        return JavaClassLoader.getLogic(className);
    }

    private static boolean createAny(String className) {
        className = className.trim();
        if(className.startsWith("(")) {
            className = className.substring(className.indexOf(')') + 1).trim();
        }
        int p = className.indexOf('|');
        if(p >= 0) {
            className = className.substring(0, p);
        }
        p = className.indexOf('/');
        if(p < 0) {
            return false;
        }
        className = className.substring(p + 1);
        return className.toUpperCase().contains("ANY");
    }

    private static int createBlockNumber(String className) {
        className = className.trim();
        if(!className.startsWith("(")) {
            return -1;
        }
        className = className.substring(1, className.indexOf(')'));
        int p = className.lastIndexOf('.');
        return Integer.parseInt(p < 0 ? className : className.substring(p + 1));
    }

    private static int[] createPosition(String className) {
        className = className.trim();
        if(!className.startsWith("(")) {
            return null;
        }
        className = className.substring(1, className.indexOf(')'));
        int p = className.lastIndexOf('.');
        if(p < 0) {
            return null;
        }
        className = className.substring(0, p);
        String[] s = className.split("\\.");
        int[] pos = new int[s.length];
        for(int i = 0; i < pos.length; i++) {
            pos[i] = Integer.parseInt(s[i]);
        }
        return pos;
    }

    private static int createLink(String className) {
        className = className.trim();
        if(className.startsWith("(")) {
            className = className.substring(className.indexOf(')') + 1).trim();
        }
        int p = className.indexOf('|');
        if(p >= 0) {
            className = className.substring(0, p);
        }
        p = className.indexOf('/');
        if(p < 0) {
            return 0;
        }
        className = className.substring(p + 1).toUpperCase().replace("ANY", "").replace("/", "");
        return className.length() > 0 ? Integer.parseInt(className) : 0;
    }

    public ObjectPDF<T> getReport() {
        return report;
    }

    @Override
    public void setCaption(String caption) {
        super.setCaption(caption);
        if(report != null) {
            report.getBlocks().get(0).setTitleText(caption);
        }
    }

    @Override
    protected void buildFields() {
        for(ObjectPDF<T>.PDFBlock<?> b: report.getBlocks()) {
            buildFields(b);
        }
    }

    private void buildFields(ObjectPDF<T>.PDFBlock<?> block) {
        HasValue<?, ?> f;
        for(ReportColumn<?> rc: block.getColumns()) {
            if(rc.getSelection() == 0) {
                continue;
            }
            f = getFieldEditor(rc);
            if(f == null) {
                continue;
            }
            if(rc.getSelection() == 7) {
                String t = getLabel(f);
                HasValue<?, ?> f2 = getFieldEditor(rc);
                setLabel(f, null);
                setLabel(f2, null);
                ELabel between = new ELabel();
                between.append(" and ").update();
                f = new CompoundField(t, (Component)f, between, (Component)f2);
            }
            form.addField(rc.getName(), f);
            fields.put(rc, f);
        }
        for(ObjectPDF<T>.PDFBlock<?> b: block.getBlocks()) {
            buildFields(b);
        }
    }

    @Override
    protected void execute(com.storedobject.vaadin.View parent, boolean doNotLock) {
        getComponent();
        if(fields.size() == 0) {
            report.view();
        } else {
            super.execute(parent, doNotLock);
        }
    }

    @Override
    protected boolean process() {
        for(ObjectPDF<T>.PDFBlock<?> b: report.getBlocks()) {
            process(b);
        }
        report.view();
        return true;
    }

    private void process(ObjectPDF<T>.PDFBlock<?> block) {
        HasValue<?, ?> f;
        for(ReportColumn<?> rc: block.getColumns()) {
            f = fields.get(rc);
            if(f == null) {
                continue;
            }
            if(f instanceof CompoundField) {
                CompoundField cf = (CompoundField)f;
                rc.setLowerValue(cf.getField(0).getValue());
                rc.setUpperValue(cf.getField(1).getValue());
            } else if(f instanceof ChoicesField) {
                rc.setLowerValue(((ChoicesField)f).getValue());
            } else if(f instanceof RadioField) {
                rc.setLowerValue(((RadioField<?>)f).getValue());
            } else {
                rc.setLowerValue(f.getValue());
            }
        }
        for(ObjectPDF<T>.PDFBlock<?> b: block.getBlocks()) {
            process(b);
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static HasValue<?, ?> getFieldEditor(ObjectData<?> data) {
        if(!data.isConditionValid()) {
            return null;
        }
        HasValue<?, ?> f = null;
        Class<?> c = data.getType();
        if(java.sql.Date.class == c || ComputedDate.class == c) {
            f = new DateField();
        } else if(ComputedMinute.class == c) {
            f = new ComputedMinutesField();
        } else if(Geolocation.class == c) {
            f = new GeolocationField();
        } else if(java.sql.Time.class == c) {
            f = new TimeField();
        } else if(java.sql.Timestamp.class == c) {
            f = new TimestampField();
        } else if(String.class == c) {
            f = new TextField();
        } else if(Money.class == c) {
            f = new MoneyField();
        } else if(boolean.class == c || Boolean.class == c) {
            f = new RadioChoiceField(new String[] { "No", "Yes", "Any" });
            ((RadioChoiceField)f).setValue(2);
        } else if(float.class == c || double.class == c || Float.class == c || Double.class == c || ComputedDouble.class == c) {
            f = new DoubleField();
        } else if(ComputedInteger.class == c) {
            f = new IntegerField();
        } else if(ComputedLong.class == c) {
            f = new LongField();
        } else if(DecimalNumber.class == c) {
            f = new DecimalNumberField();
        } else if(Rate.class == c) {
            f = new RateField();
        } else if((Quantity.class).isAssignableFrom(c)) {
            f = new QuantityField(5);
        } else if(StoredObject.class.isAssignableFrom(c)) {
            try {
                Class<? extends StoredObject> oc = (Class<? extends StoredObject>) data.getType();
                f = new ObjectField<>(oc);
            } catch(Throwable t) {
                f = null;
            }
        } else if(int.class == c || long.class == c || short.class == c || (Number.class).isAssignableFrom(c)) {
            if(int.class == c) {
                List<String> list = data.getChoiceValues();
                if(list != null) {
                    f = new ChoicesField(null, list);
                }
            }
            if(f == null && (int.class == c || long.class == c)) {
                List<String> list = data.getChoiceBitValues();
                if(list != null) {
                    f = new ChoicesField(null, list);
                }
            }
            if(f == null) {
                f = new LongField();
            }
        }
        if(f != null) {
            setLabel(f,data.getTitle() + " (" + data.getSelectionValue() + ")");
        }
        return f;
    }

    private static void setLabel(HasValue<?, ?> field, String label) {
        try {
            field.getClass().getMethod("setLabel", String.class).invoke(field, label);
        } catch (Throwable ignored) {
        }
    }

    private static String getLabel(HasValue<?, ?> field) {
        try {
            return (String)field.getClass().getMethod("getLabel").invoke(field);
        } catch (Throwable ignored) {
        }
        return null;
    }
}