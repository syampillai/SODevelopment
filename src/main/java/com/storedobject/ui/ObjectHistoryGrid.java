package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.core.StoredObjectUtility.Link;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.function.BiPredicate;

public class ObjectHistoryGrid<T extends StoredObject> extends DataGrid<T> implements CloseableView {

    private T object;
    private final BiPredicate<T, T> viewFilter;
    private AuditTrailConfiguration atc;
    private final StoredObject master;

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

    public ObjectHistoryGrid(T object, StringList browseColumns, BiPredicate<T, T> viewFilter) {
        this(object, browseColumns, viewFilter, null);
    }

    @SuppressWarnings("unchecked")
    private ObjectHistoryGrid(T object, StringList browseColumns, BiPredicate<T, T> viewFilter, StoredObject master) {
        super((Class<T>) object.getClass(), browseColumns);
        this.master = master;
        this.object = object;
        createColumn("Timestamp");
        createColumn("ChangedBy");
        createColumn("AuditID");
        createColumn("TransactionIP");
        this.viewFilter = viewFilter;
        setHeightFull();
        setObject(object);
    }

    public void setObject(T object) {
        this.object = object;
        load();
    }

    public ObjectHistoryGrid(T object, AuditTrailConfiguration atc) {
        this(object, atc, null);
    }

    public ObjectHistoryGrid(T object, AuditTrailConfiguration atc, BiPredicate<T, T> viewFilter) {
        this(object, cols(atc), viewFilter);
        this.atc = atc;
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
        return StringUtility.isWhite(dfs) ? null : StringList.create(dfs).map(StringUtility::pack);
    }

    @Override
    public String getColumnCaption(String columnName) {
        return switch(columnName) {
            case "TransactionIP" -> "I Address";
            case "Timestamp" -> "Timestamp (" + getTransactionManager().getEntity().getTimeZone() + ")";
            default -> super.getColumnCaption(columnName);
        };
    }

    public Timestamp getTimestamp(T object) {
        return getTransactionManager().date(object.timestamp());
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

    private void load() {
        deselectAll();
        clear();
        @SuppressWarnings("unchecked") ObjectIterator<T> objects = (ObjectIterator<T>)object.listHistory();
        objects = ObjectIterator.create(object).add(objects);
        if(viewFilter != null) {
            objects = objects.filter(viewFilter);
        }
        objects.collectAll(this);
    }

    @Override
    public void execute() {
        if(master != null && isEmpty()) {
            return;
        }
        super.execute();
    }

    @Override
    public void execute(View lock) {
        if(master != null && isEmpty()) {
            return;
        }
        super.execute(lock);
    }

    public void executeAll() {
        if(master != null && isEmpty()) {
            return;
        }
        getATC();
        if(atc == null || atc.getLinks() == 0) {
            ArrayList<Link<?>> links = StoredObjectUtility.linkDetails(object.getClass());
            for(Link<?> link: links) {
                if(!link.isDetail()) {
                    continue;
                }
                for(StoredObject child: link.list(object)) {
                    new ObjectHistoryGrid<>(child, link.getBrowseColumns(), null, object).execute();
                }
            }
        } else if(atc.getLinks() == 1) {
            Link<?> link;
            for(AuditTrailLinkConfiguration auditTrailLinkConfiguration:
                    atc.listLinks(AuditTrailLinkConfiguration.class)) {
                link = auditTrailLinkConfiguration.createLink(object.getClass());
                if(link == null || !link.isDetail()) {
                    continue;
                }
                for(StoredObject child: link.list(object)) {
                    new ObjectHistoryGrid<>(child, link.getBrowseColumns(), null, object).execute();
                }
            }
        }
        execute();
    }

    @Override
    public Component createHeader() {
        ButtonLayout buttons = new ButtonLayout();
        if(master != null) {
            buttons.add(new ELabel("Detail of: " + master.toDisplay()));
        }
        buttons.add(new Button("View Changes", VaadinIcon.PENCIL, e -> viewChanges()));
        if(master == null) {
            buttons.add(new Button("Load Another (Here)", e -> loadAnother(this::setObject)),
                    new Button("Load Another (New View)",
                            e -> loadAnother(o -> new ObjectHistoryGrid<>(o, getATC()).executeAll())));
        }
        buttons.add(new Button("User Details", e -> showMore()));
        buttons.add(new Button("Exit", e -> close()));
        return buttons;
    }

    private AuditTrailConfiguration getATC() {
        if(atc == null) {
            atc = AuditTrailConfiguration.getByClass(object.getClass());
        }
        return atc;
    }

    private void loadAnother(ObjectSetter<T> viewer) {
        getATC();
        if(atc == null) {
            return;
        }
        Class<T> objectClass = getObjectClass();
        ObjectBrowser<T> ob = new ObjectBrowser<>(objectClass, StoredObjectUtility.browseColumns(objectClass),
                EditorAction.SEARCH, StringList.create(atc.getSearchFields()));
        ob.search(null, viewer);
    }

    private void viewChanges() {
        clearAlerts();
        T current  = selected();
        if(current == null) {
            return;
        }
        int n = indexOf(current) + 1;
        if(n >= size()) {
            message("That is the first entry");
            return;
        }
        T previous = get(n);
        new ObjectComparisonGrid<>(current, previous).execute();
    }

    private void showMore() {
        clearAlerts();
        T current  = selected();
        if(current == null) {
            return;
        }
        showTrail(AuditTrail.create(current), getTransactionManager()).popup();
    }

    static TextView showTrail(AuditTrail auditTrail, TransactionManager tm) {
        TextView tv = new TextView("User Details");
        String tz = " " + tm.getEntity().getTimeZone();
        tv.append("Person: " + auditTrail.getUser().getPerson()).newLine()
                .append("Username: " + auditTrail.getUser().getLogin()).newLine()
                .append("Timestamp: " + DateUtility.formatWithTimeHHMM(tm.date(auditTrail.getTimestamp())) + tz)
                .newLine()
                .append("IP Address: ").append(auditTrail.getIPAddress()).newLine()
                .append("Connected from: " + auditTrail.getApplicationClient()).newLine()
                .append("Logged in at: " + DateUtility.formatWithTimeHHMM(tm.date(auditTrail.getLoginTime())) + tz)
                .update();
        return tv;
    }
}