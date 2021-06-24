package com.storedobject.ui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.core.StoredObjectUtility.Link;
import com.storedobject.vaadin.CloseableView;
import com.vaadin.flow.data.provider.ListDataProvider;

public class ObjectHistoryGrid<T extends StoredObject> extends DataGrid<T> implements CloseableView {

    private final T object;
    private final BiPredicate<T, T> viewFilter;

    public ObjectHistoryGrid(T object) {
        this(object, (StringList)null);
    }

    public ObjectHistoryGrid(T object, BiPredicate<T, T> viewFilter) {
        this(object, cols(object.getClass()), viewFilter);
    }

    public ObjectHistoryGrid(Id id) {
        this(id, null);
    }

    @SuppressWarnings("unchecked")
    public ObjectHistoryGrid(Id id, BiPredicate<T, T> viewFilter) {
        this((T) StoredObject.get(id), viewFilter);
    }

    public ObjectHistoryGrid(T object, StringList browseColumns) {
        this(object, browseColumns, null);
    }

    @SuppressWarnings("unchecked")
    public ObjectHistoryGrid(T object, StringList browseColumns, BiPredicate<T, T> viewFilter) {
        super((Class<T>) object.getClass(), browseColumns);
        this.object = object;
        createColumn("Timestamp");
        createColumn("ChangedBy");
        createColumn("AuditID");
        createColumn("TransactionIP");
        this.viewFilter = viewFilter;
        setHeightFull();
        load();
    }

    public ObjectHistoryGrid(T object, AuditTrailConfiguration atc) {
        this(object, atc, null);
    }

    public ObjectHistoryGrid(T object, AuditTrailConfiguration atc, BiPredicate<T, T> viewFilter) {
        this(object, cols(atc), viewFilter);
        setCaption(atc.getName());
    }

    private static <O extends StoredObject> StringList cols(Class<O> objectClass) {
        StringList cols = cols(AuditTrailConfiguration.getByClass(objectClass));
        return cols == null ? StoredObjectUtility.browseColumns(objectClass) : cols;
    }

    private static StringList cols(AuditTrailConfiguration atc) {
        if(atc == null) {
            return null;
        }
        String dfs = atc.getDisplayFields();
        return StringUtility.isWhite(dfs) ? null : StringList.create(dfs);
    }

    @Override
    public String getColumnCaption(String columnName) {
        switch (columnName) {
            case "Timestamp":
                return "Timestamp (UTC)";
            case "TransactionIP":
                return "IP Address";
        }
        return super.getColumnCaption(columnName);
    }

    public Timestamp getTimestamp(T object) {
        return object.timestamp();
    }

    public Person getChangedBy(T object) {
        return object.person();
    }

    public Id getAuditID(T object) {
        return object.getTransactionId();
    }

    public String getIP() {
        return object.getTransactionIP();
    }

    @SuppressWarnings("unchecked")
    public void load() {
        ObjectIterator<T> objects = (ObjectIterator<T>)object.listHistory();
        if(viewFilter != null) {
            objects = objects.filter(viewFilter);
        }
        List<T> list = new ArrayList<>();
        list.add(object);
        objects.collectAll(list);
        setItems(new ListDataProvider<>(list));
    }

    public void executeAll() {
        execute();
        AuditTrailConfiguration atc = AuditTrailConfiguration.getByClass(object.getClass());
        if(atc == null || atc.getLinks() == 0) {
            ArrayList<Link<?>> links = StoredObjectUtility.linkDetails(object.getClass());
            for(Link<?> link: links) {
                for(StoredObject child: link.list(object)) {
                    new ObjectHistoryGrid<>(child, link.getBrowseColumns()).execute();
                }
            }
        } else {
            if(atc.getLinks() == 2) {
                return;
            }
            Link<?> link;
            for(AuditTrailLinkConfiguration auditTrailLinkConfiguration: atc.listLinks(AuditTrailLinkConfiguration.class)) {
                link = auditTrailLinkConfiguration.createLink(object.getClass());
                if(link == null) {
                    continue;
                }
                for(StoredObject child: link.list(object)) {
                    new ObjectHistoryGrid<>(child, link.getBrowseColumns()).execute();
                }
            }
        }
    }
}