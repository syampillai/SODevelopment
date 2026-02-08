package com.storedobjects.support;

import com.storedobject.core.*;

import java.util.HashMap;
import java.util.Map;

public class OrganizationGroup extends Name {

    private static final Map<Id, OrganizationGroup> cache = new HashMap<>();

    public OrganizationGroup() {}

    public static void columns(Columns columns) {}

    public static OrganizationGroup get(String name) {
        return StoredObjectUtility.get(OrganizationGroup.class, "Name", name, false);
    }

    public static ObjectIterator<OrganizationGroup> list(String name) {
        return StoredObjectUtility.list(OrganizationGroup.class, "Name", name, false);
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public static String[] links() {
        return new String[] {
                "Organizations|com.storedobjects.support.Organization|||0",
        };
    }

    @Override
    public void saved() {
        cache.remove(getId());
    }

    public static OrganizationGroup get(Id id) {
        OrganizationGroup og = cache.get(id);
        if(og == null) {
            og = get(OrganizationGroup.class, id);
            cache.put(id, og);
        }
        return og;
    }
}
