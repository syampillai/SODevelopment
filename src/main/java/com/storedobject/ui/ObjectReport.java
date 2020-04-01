package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.StoredObject;
import com.storedobject.report.ObjectData;
import com.storedobject.report.ObjectPDF;
import com.storedobject.vaadin.DataForm;
import com.vaadin.flow.component.HasValue;

public class ObjectReport<T extends StoredObject> extends DataForm {

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
        this(report.save(new StringBuilder()).toString().substring(2));
    }

    private ObjectReport(Class<T> objectClass, boolean any, StringList columns, int blockNumber) {
        super("Report");
    }

    @SuppressWarnings("unchecked")
    public ObjectReport(String className) throws Exception {
        this((Class<T>)createClass(className), false, null, 0);
    }

    private static Class<?> createClass(String className) throws Exception {
        return null;
    }

    public ObjectPDF<T> getReport() {
        return null;
    }

    @Override
    public void setCaption(String caption) {
    }

    @Override
    protected void buildFields() {
    }

    @Override
    protected boolean process() {
        return true;
    }

    public static HasValue<?, ?> getFieldEditor(ObjectData<?> data) {
        return null;
    }
}