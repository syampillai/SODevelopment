package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;

import java.util.function.Predicate;

public class Link<L extends StoredObject> {

    private final Class<? extends StoredObject> masterClass;
    private Class<L> objectClass = null;
    private int type = 0;
    private String name, orderBy, condition;
    private Predicate<L> loadPredicate;
    private StringList browseColumns;
    private boolean any, readOnly;

    public Link(Class<? extends StoredObject> masterClass) {
        this.masterClass = masterClass;
    }

    public Class<L> getObjectClass() {
        return objectClass;
    }

    public Class<? extends StoredObject> getMasterClass() {
        return masterClass;
    }

    public void setObjectClass(Class<L> objectClass) {
        this.objectClass = objectClass;
    }

    @SuppressWarnings("unchecked")
    public void setObjectClass(String className) {
        try {
            objectClass = (Class<L>)StoredObjectUtility.getObjectClass(className);
            if(objectClass == null) {
                throw new SOException("Invalid link class - " + className);
            }
        } catch (Throwable e) {
            throw new SORuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.replace('_', '|');
    }

    public void setBrowserColumns(String columns) {
        if(StringUtility.isWhite(columns)) {
            browseColumns = null;
        } else {
            setBrowserColumns(StringList.create(columns));
        }
    }

    public void setBrowserColumns(StringList columns) {
        browseColumns = columns;
    }

    public StringList getBrowseColumns() {
        if(browseColumns == null && objectClass != null) {
            browseColumns = ClassAttribute.get(objectClass).browseColumns();
        }
        return browseColumns;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderBy() {
        if(orderBy == null && objectClass != null) {
            orderBy = ClassAttribute.get(objectClass).browseOrder();
        }
        return orderBy;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setLoadPredicate(Predicate<L> loadPredicate) {
        if(loadPredicate == null) {
            loadPredicate = o -> true;
        }
        this.loadPredicate = loadPredicate;
    }

    public Predicate<L> getLoadPredicate() {
        return loadPredicate;
    }

    public void setAny() {
        any = true;
    }

    public boolean isAny() {
        return any;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isDetail() {
        return isDetailOf(masterClass);
    }

    public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
        if(!Detail.class.isAssignableFrom(getObjectClass())) {
            return false;
        }
        try {
            return ((Detail) getObjectClass().getDeclaredConstructor().newInstance()).isDetailOf(masterClass);
        } catch(Exception ignored) {
        }
        return false;
    }

    public ObjectIterator<L> list(Id master) {
        return links(master,null);
    }

    public ObjectIterator<L> list(StoredObject master) {
        if(Id.isNull(master)) {
            return ObjectIterator.create();
        }
        Transaction t = master.getTransaction();
        if(t != null && !t.isActive()) {
            t = null;
        }
        return links(master.getId(), t).filter(loadPredicate);
    }

    private ObjectIterator<L> links(Id master, Transaction t) {
        return master.listLinks(t, type, objectClass, getCondition(), getOrderBy(), any)
                .filter(loadPredicate);
    }

    public Query query(StoredObject master) {
        Transaction t = master.getTransaction();
        if(t != null && !t.isActive()) {
            t = null;
        }
        return master.queryLinks(t, type, objectClass, getBrowseColumns().toString(), getCondition(),
                getOrderBy(), any);
    }

    @Override
    public boolean equals(Object another) {
        if(!(another instanceof Link<?> a)) {
            return false;
        }
        return objectClass == a.objectClass && type == a.type;
    }
}
