package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class TransactionType extends ShortName {

    private static final Map<String, TransactionType> cache = new HashMap<>();
    private boolean inactive;
    private Set<Id> applicableTo;

    public TransactionType() {}

    public static void columns(Columns columns) {
        columns.add("Inactive", "boolean");
    }

    public static TransactionType get(String name) {
        var _instance = getFor(TransactionType.class, name);
        return (_instance != null) ? _instance : list(name).single(false);
    }

    public static ObjectIterator<TransactionType> list(String name) {
        return StoredObjectUtility.list(TransactionType.class, "ShortName", name, false)
                .add(StoredObjectUtility.list(TransactionType.class, "Name", name, false));
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public static String[] browseColumns() {
        return new String[] {
                "ShortName AS Name", "Name AS Description",
        };
    }

    public static String[] links() {
        return new String[] {
                "Applicable to|com.storedobject.core.SystemEntity|||0",
        };
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public void setInactive(Boolean inactive) {
        setInactive(inactive != null && inactive);
    }

    @Column(order = 300)
    public boolean getInactive() {
        return inactive;
    }

    @Override
    protected boolean isCode() {
        return true;
    }

    @Override
    void savedCore() throws Exception {
        cache.remove(shortName);
        super.savedCore();
    }

    public static TransactionType getFor(Id id) {
        if(Id.isNull(id)) return null;
        TransactionType tt = cache.values().stream()
                .filter(transactionType -> transactionType.getId().equals(id))
                .findFirst()
                .orElse(null);
        if(tt != null) {
            return tt;
        }
        tt = get(TransactionType.class, id);
        if(tt != null) {
            cache.put(tt.getShortName(), tt);
        }
        return tt;
    }

    public static TransactionType getFor(String shortName) {
        shortName = toCode(shortName);
        TransactionType tt = cache.get(shortName);
        if(tt != null) {
            return tt;
        }
        tt = get(TransactionType.class, "lower(ShortName)='" + shortName.toLowerCase() + "'");
        if(tt != null) {
            cache.put(shortName, tt);
        }
        return tt;
    }

    public static TransactionType create(TransactionManager transactionManager, String shortName) throws Exception {
        TransactionType tt = getFor(shortName);
        if(tt != null || transactionManager == null) {
            return tt;
        }
        tt = new TransactionType();
        tt.setShortName(shortName);
        tt.setName(shortName);
        transactionManager.transact(tt::save);
        cache.put(tt.shortName, tt);
        return tt;
    }

    private Set<Id> applicableTo() {
        if(applicableTo == null) {
            applicableTo = new HashSet<>();
            listLinks(SystemEntity.class).forEach(link -> applicableTo.add(link.getId()));
        }
        return applicableTo;
    }

    void checkEntity(Id systemEntityId) throws Invalid_State {
        if(!isApplicableTo(systemEntityId)) {
            SystemEntity se = get(SystemEntity.class, systemEntityId);
            throw new Invalid_State(se.toDisplay() + " - Transaction type " + shortName
                    + " is not allowed for this organization.");
        }
    }

    public boolean isApplicableTo(Id systemEntityId) {
        return applicableTo().isEmpty() || applicableTo.contains(systemEntityId);
    }
}
