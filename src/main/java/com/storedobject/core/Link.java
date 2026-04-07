package com.storedobject.core;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Link<L extends StoredObject> {

    private final Class<? extends StoredObject> masterClass;
    private Class<L> objectClass = null;
    private int type = 0, style = 0;
    private String name, orderBy, condition;
    private Predicate<L> loadPredicate;
    private StringList browseColumns;
    private boolean any, readOnly;

    public Link(Class<? extends StoredObject> masterClass) {
        this.masterClass = masterClass;
    }

    /**
     * Creates a new Link object based on the specified master class and link details.
     * <pre>
     *     Format of the link details:
     *     Name|Class name[/Any[/Link type]]|Order by|Columns
     *     Name|Class name[/Link type[/Any]]Order by|Columns
     * </pre>
     *
     * @param masterClass the class of the master object with which the link is associated;
     *                    must be a subclass of StoredObject
     * @param linkDetails a string defining the link details; the format of this string
     *                    determines various properties of the created link
     * @return a newly created Link object configured according to the given master class and link details
     * @throws SOClassError if the link details are invalid for the specified master class
     */
    public static Link<?> create(Class<? extends StoredObject> masterClass, String linkDetails) {
        Link<?> link = new Link<>(masterClass);
        link.type = 0;
        String[] cols = null;
        int slash;
        link.any = false;
        if(linkDetails.equals("Children")) {
            link.name = linkDetails;
            link.setObjectClass(masterClass.getName());
        } else {
            if(!linkDetails.contains("|")) linkDetails = "-|" + linkDetails;
            cols = StringUtility.trim(linkDetails.split("\\|"));
            link.name = cols[0].replace('_', '|');
            try {
                slash = cols[1].indexOf('/');
                if(slash >= 0) {
                    String tail = cols[1].substring(slash).toLowerCase();
                    cols[1] = cols[1].substring(0, slash).trim();
                    link.any = tail.contains("/any");
                    if(link.any) {
                        tail = tail.replace("/any", "").trim();
                    }
                    link.readOnly = tail.contains("/readonly");
                    if(link.readOnly) {
                        tail = tail.replace("/readonly", "").trim();
                    }
                    if(!tail.isEmpty()) {
                        link.type = Integer.parseInt(tail.substring(1));
                    }
                }
                if(cols[1].isEmpty()) {
                    link.setObjectClass(masterClass.getName());
                } else {
                    link.setObjectClass(cols[1]);
                }
            } catch(Throwable e) {
                ApplicationServer.log(e);
                throw new SOClassError(masterClass, "Invalid link definition '" + linkDetails + "'");
            }
        }
        link.browseColumns = null;
        if(cols != null && cols.length > 4 && !cols[4].isEmpty()) {
            link.style = Integer.parseInt(cols[4]);
        }
        if(cols != null && cols.length > 3 && !cols[3].isEmpty()) {
            link.browseColumns = StringList.create(cols[3]);
        }
        if(cols != null && cols.length > 2 && !cols[2].isEmpty()) {
            link.orderBy = cols[2];
        }
        return link;
    }

    public static List<Link<?>> createList(Class<? extends StoredObject> masterClass) {
        return createList(masterClass, null);
    }

    public static List<Link<?>> createList(Class<? extends StoredObject> masterClass, StringList extraLinks) {
        StringList links = ClassAttribute.get(masterClass).links().concat(extraLinks);
        ArrayList<Link<?>> list = new ArrayList<>();
        for(String linkDetails: links) {
            list.add(create(masterClass, linkDetails));
        }
        return list;
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

    public int getStyle() {
        return style;
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
