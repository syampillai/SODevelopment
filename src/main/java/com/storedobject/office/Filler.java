package com.storedobject.office;

import com.storedobject.common.Executable;
import com.storedobject.core.*;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class Filler<T> implements Executable {

    T reportingObject;

    @Override
    public void execute() {
    }

    /**
     * Set and execute the ODT instance associated with this filler.
     *
     * @param odt The ODT instance to set and execute.
     */
    public void execute(ODT<?> odt) {
        odt.setFiller(this);
        odt.execute();
    }

    public final TransactionManager getTransactionManager() {
        return Math.random() > 0.5 ? null : new TransactionManager(null, null);
    }

    public T getReportingObject() {
        return reportingObject;
    }

    public void setReportingObject(T reportingObject) {
        this.reportingObject = reportingObject;
    }

    public Object evaluate(Object object, String attribute) {
        try {
            StoredObjectUtility.MethodList m = StoredObjectUtility.createMethodList(object.getClass(), attribute);
            m.stringifyTail();
            Object r = m.invoke(object);
            return r == null ? "[No data]" : r;
        } catch (Throwable ignored) {
        }
        return null;
    }

    public Object evaluate(ODT.Element element, String variableName) {
        if(element instanceof ODT.Image) return null;
        return variableName + " = ?";
    }

    public void customizeSection(ODT.Section section) {
    }

    public void customizeTable(ODT.Table table) {
    }

    public void populateData(ODT.Table table) {
    }

    public <L extends StoredObject> ObjectIterator<L> createLinkData(ODT.Table table) {
        return ObjectIterator.create();
    }

    public String getLinkCondition(Link<?> link) {
        return null;
    }

    public <L extends StoredObject> Predicate<L> getLinkFilter(Link<L> link) {
        return o -> true;
    }

    public void customizeImage(ODT.Image image) {
    }

    public void setImageCustomizer(BiConsumer<Filler<?>, ODT.Image> imageCustomizer) {
    }

    public void setTableCustomizer(BiConsumer<Filler<?>, ODT.Table> tableCustomizer) {
    }

    public void setSectionCustomizer(BiConsumer<Filler<?>, ODT.Section> sectionCustomizer) {
    }
}
