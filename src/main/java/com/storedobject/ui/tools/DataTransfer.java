package com.storedobject.ui.tools;

import java.io.Writer;

import com.storedobject.core.StoredObject;
import com.storedobject.core.TextContentProducer;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.DataForm;

public abstract class DataTransfer extends DataForm {

    private ChoiceField actionField;
    private Class<? extends StoredObject> objectClass;
    private String where, orderBy;
    private boolean any;

    public DataTransfer() {
        this("Data Transfer");
    }

    public DataTransfer(String caption) {
        super(caption, "Proceed", "Cancel");
    }

    @Override
    protected void buildFields() {
        actionField = new ChoiceField("Select Action", new String[] { "Download", "Upload" });
        addField(actionField);
    }

    @Override
    protected boolean process() {
        if(getObjectClass() == null) {
            return true;
        }
        if(actionField.getValue() == 0) {
            TextContentProducer cp = new TextContentProducer() {
                @Override
                public void generateContent() throws Exception {
                    Writer w = getWriter();
                    for(StoredObject so: StoredObject.list(objectClass, where, orderBy, any)) {
                        so.save(w);
                    }
                }
            };
            ((Application)getApplication()).view(cp);
            return true;
        }
        return true;
    }

    public Class<? extends StoredObject> getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(Class<? extends StoredObject> objectClass) {
        this.objectClass = objectClass;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public boolean isAny() {
        return any;
    }

    public void setAny(boolean any) {
        this.any = any;
    }
}