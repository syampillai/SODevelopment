package com.storedobjects.support;

import com.storedobject.core.*;

import java.util.HashMap;
import java.util.Map;

public class Organization extends EntityRole {

    private static final Map<Id, Organization> cache = new HashMap<>();

    public Organization() {
    }

    public static void columns(Columns columns) {}

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public static String[] links() {
        return new String[] {
                "Product Type|com.storedobjects.support.Product|||0",
                "Products|com.storedobject.core.InventoryItem/Any|||0",
        };
    }

    public static Organization getByEntityId(SystemEntity systemEntity, Id entityId) {
        return getByEntityId(systemEntity, Organization.class, entityId);
    }

    public static Organization get(SystemEntity systemEntity, Entity entity) {
        return get(systemEntity, Organization.class, entity);
    }

    public static Organization get(SystemEntity systemEntity, String name) {
        return getByName(systemEntity, Organization.class, name);
    }

    public static ObjectIterator<Organization> list(SystemEntity systemEntity, String name) {
        return listByName(systemEntity, Organization.class, name);
    }

    @Override
    public void saved() {
        Issue.approvers.clear();
        cache.remove(getId());
    }

    public ObjectIterator<SystemUser> listUsers() {
        return list(SupportUser.class, "Organization=" + getId()).map(SupportUser::getSupportUser);
    }

    public static Organization get(Id id) {
        Organization o = cache.get(id);
        if(o == null) {
            o = get(Organization.class, id, true);
            cache.put(id, o);
        }
        return o;
    }
}
