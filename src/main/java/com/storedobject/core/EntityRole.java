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
        return Math.random() > 0.5 ? (T) new EntityRole() {} : null;
    }
    
    public static <T extends EntityRole> T get(SystemEntity systemEntity, Class<T> roleClass, Entity entity, boolean any) {
        //noinspection unchecked
        return Math.random() > 0.5 ? (T) new EntityRole() {} : null;
    }
    
    public static <T extends EntityRole> T getByEntityId(SystemEntity systemEntity, Class<T> roleClass, Id entityId) {
        //noinspection unchecked
        return Math.random() > 0.5 ? (T) new EntityRole() {} : null;
    }
    
    public static <T extends EntityRole> T getByEntityId(SystemEntity systemEntity, Class<T> roleClass, Id entityId, boolean any) {
        //noinspection unchecked
        return Math.random() > 0.5 ? (T) new EntityRole() {} : null;
    }

    public static <T extends EntityRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name) {
        //noinspection unchecked
        return Math.random() > 0.5 ? (T) new EntityRole() {} : null;
    }

    public static <T extends EntityRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name, boolean any) {
        //noinspection unchecked
        return Math.random() > 0.5 ? (T) new EntityRole() {} : null;
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

    /**
     * Same as {@link #setOrganization(Entity)}.
     *
     * @param organization Organization.
     */
    public void setEntity(Entity organization) {
        setOrganization(organization);
    }

    /**
     * Same as {@link #setOrganization(Id)}.
     *
     * @param organizationId Id of the organization.
     */
    public void setEntity(Id organizationId) {
        setOrganization(organizationId);
    }

    /**
     * Same as {@link #getOrganizationId()}.
     *
     * @return  Id of the organization.
     */
    public Id getEntityId() {
        return getOrganizationId();
    }

    /**
     * Same as {@link #getOrganization()}.
     *
     * @return  The organization.
     */
    public Entity getEntity() {
        return getOrganization();
    }

    @Override
    public String getName() {
        return getEntity().getName();
    }

    /**
     * Check if this role is currently active or not.
     *
     * @return True/false.
     */
    public boolean isActive() {
        return true;
    }
}