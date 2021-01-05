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
        return new Id();
    }

    public Entity getOrganization() {
        return new Entity();
    }
    
    public void setSystemEntity(Id systemEntityId) {
    }

    public void setSystemEntity(BigDecimal idValue) {
    }

    public void setSystemEntity(SystemEntity systemEntity) {
    }

    public Id getSystemEntityId() {
        return new Id();
    }

    public SystemEntity getSystemEntity() {
        return new SystemEntity();
    }

    public static <T extends EntityRole> T get(SystemEntity systemEntity, Class<T> roleClass, Entity entity) {
        //noinspection unchecked
        return (T) new EntityRole() {};
    }
    
    public static <T extends EntityRole> T get(SystemEntity systemEntity, Class<T> roleClass, Entity entity, boolean any) {
        //noinspection unchecked
        return (T) new EntityRole() {};
    }
    
    public static <T extends EntityRole> T getByEntityId(SystemEntity systemEntity, Class<T> roleClass, Id entityId) {
        //noinspection unchecked
        return (T) new EntityRole() {};
    }
    
    public static <T extends EntityRole> T getByEntityId(SystemEntity systemEntity, Class<T> roleClass, Id entityId, boolean any) {
        //noinspection unchecked
        return (T) new EntityRole() {};
    }

    public static <T extends EntityRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name) {
        //noinspection unchecked
        return (T) new EntityRole() {};
    }

    public static <T extends EntityRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name, boolean any) {
        //noinspection unchecked
        return (T) new EntityRole() {};
    }

    public static <T extends EntityRole> ObjectIterator<T> listByName(SystemEntity systemEntity, Class<T> roleClass, String name) {
        return ObjectIterator.create();
    }
    
    public static <T extends EntityRole> ObjectIterator<T> listByName(final SystemEntity systemEntity, final Class<T> roleClass, String name, final boolean any) {
        return ObjectIterator.create();
    }

    public Id getContactOwnerId() {
        return new Id();
    }
}