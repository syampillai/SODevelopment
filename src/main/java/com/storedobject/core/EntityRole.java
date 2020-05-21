package com.storedobject.core;

import java.math.BigDecimal;

public abstract class EntityRole extends StoredObject implements OfEntity, HasContacts, RequiresApproval {

    public EntityRole() {
    }

    public static void columns(Columns columns) {
    }

    public void setOrganization(Id organizationId) {
    }

    public void setOrganization(BigDecimal idValue) {
    }

    public void setOrganization(Entity organization) {
    }

    public Id getOrganizationId() {
        return null;
    }

    public Entity getOrganization() {
        return null;
    }
    
    public void setSystemEntity(Id systemEntityId) {
    }

    public void setSystemEntity(BigDecimal idValue) {
    }

    public void setSystemEntity(SystemEntity systemEntity) {
    }

    public Id getSystemEntityId() {
        return null;
    }

    public SystemEntity getSystemEntity() {
        return null;
    }

    public static <T extends EntityRole> T get(SystemEntity systemEntity, Class<T> roleClass, Entity entity) {
        return null;
    }
    
    public static <T extends EntityRole> T get(SystemEntity systemEntity, Class<T> roleClass, Entity entity, boolean any) {
        return null;
    }
    
    public static <T extends EntityRole> T getByEntityId(SystemEntity systemEntity, Class<T> roleClass, Id entityId) {
        return null;
    }
    
    public static <T extends EntityRole> T getByEntityId(SystemEntity systemEntity, Class<T> roleClass, Id entityId, boolean any) {
        return null;
    }

    public static <T extends EntityRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name) {
        return null;
    }

    public static <T extends EntityRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name, boolean any) {
        return null;
    }

    public static <T extends EntityRole> ObjectIterator<T> listByName(SystemEntity systemEntity, Class<T> roleClass, String name) {
        return null;
    }
    
    public static <T extends EntityRole> ObjectIterator<T> listByName(final SystemEntity systemEntity, final Class<T> roleClass, String name, final boolean any) {
        return null;
    }

    public Id getContactOwnerId() {
        return null;
    }
}