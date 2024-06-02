package com.storedobject.core;

import com.storedobject.core.annotation.*;

import java.math.BigDecimal;

public abstract class EntityRole extends StoredObject implements OfEntity, HasContacts, HasName {

    private Id organizationId;
    private Id systemEntityId;
    private Entity organization;
    private SystemEntity systemEntity;

    public EntityRole() {
    }

    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
        columns.add("Organization", "id");
    }

    public static void indices(Indices indices) {
        indices.add("Organization, SystemEntity, T_Family", true);
    }

    @Override
	public String getUniqueCondition() {
        return "Organization=" + getOrganizationId() + " AND T_Family=" + family() + " AND SystemEntity=" + getSystemEntityId();
    }

	public static String[] displayColumns() {
		return new String[] {
			"Organization.Name as Organization",
			"Organization.Location as Location",
			"Organization.Country AS Country"
		};
	}

	public static String[] searchColumns() {
		return new String[] {
			"Organization.Name as Organization",
		};
	}

    public void setOrganization(Id organizationId) {
        if(!loading() && !Id.equals(this.organizationId, organizationId)) {
            throw new Set_Not_Allowed("Organization");
        }
        this.organizationId = organizationId;
    }

    public void setOrganization(BigDecimal idValue) {
        setOrganization(new Id(idValue));
    }

    public void setOrganization(Entity organization) {
        setOrganization(organization == null ? null : organization.getId());
    }

    @SetNotAllowed
    @Column(order = 200)
    public Id getOrganizationId() {
        return organizationId;
    }

    public Entity getOrganization() {
    	if(organization == null || Id.isNull(organization.getId())) {
    		organization = get(Entity.class, organizationId);
    	}
    	return organization;
    }
    
    public void setSystemEntity(Id systemEntityId) {
        if(!loading()) {
            throw new Set_Not_Allowed("System Entity");
        }
        this.systemEntityId = systemEntityId;
    }

    public void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    public void setSystemEntity(SystemEntity systemEntity) {
        setSystemEntity(systemEntity == null ? null : systemEntity.getId());
    }

    @SetNotAllowed
    @Column(order = 100, caption = "Of")
    public Id getSystemEntityId() {
        return systemEntityId;
    }

    public SystemEntity getSystemEntity() {
        if(systemEntity == null || Id.isNull(systemEntity.getId())) {
            systemEntity = get(SystemEntity.class, systemEntityId);
        }
        return systemEntity;
    }

    @Override
	public void validateData(TransactionManager tm) throws Exception {
        if(!deleted()) {
            systemEntityId = check(tm, systemEntityId);
            if(this instanceof OfEntitySelf && Id.isNull(organizationId)) {
                organizationId = getSystemEntity().getEntityId();
            }
            organizationId = tm.checkType(this, organizationId, Entity.class, false);
            if(this instanceof OfEntitySelf && !getSystemEntity().getEntityId().equals(organizationId)) {
                throw new Invalid_State("Invalid entity!");
            }
        }
        super.validateData(tm);
    }

    @Override
	public String toString() {
        return getOrganization().toString();
    }

    @Override
    public String toDisplay() {
        return getOrganization().toDisplay();
    }

    public static <T extends EntityRole> T get(SystemEntity systemEntity, Class<T> roleClass, Entity entity) {
    	return get(systemEntity, roleClass, entity, false);
    }
    
    public static <T extends EntityRole> T get(SystemEntity systemEntity, Class<T> roleClass, Entity entity, boolean any) {
    	return entity == null ? null : getByEntityId(systemEntity, roleClass, entity.getId(), any);
    }
    
    public static <T extends EntityRole> T getByEntityId(SystemEntity systemEntity, Class<T> roleClass, Id entityId) {
    	return getByEntityId(systemEntity, roleClass, entityId, false);
    }
    
    public static <T extends EntityRole> T getByEntityId(SystemEntity systemEntity, Class<T> roleClass, Id entityId, boolean any) {
    	return entityId == null || roleClass == null ? null :
    		get(roleClass, "Organization=" + entityId + " AND SystemEntity=" + systemEntity.getId(), any);
    }

    public static <T extends EntityRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name) {
    	return getByName(systemEntity, roleClass, name, false);
    }

    public static <T extends EntityRole> T getByName(SystemEntity systemEntity, Class<T> roleClass, String name, boolean any) {
    	T role = get(systemEntity, roleClass, Entity.get(name), any);
    	return role == null ? listByName(systemEntity, roleClass, name, any).single(false) : role;
    }

    public static <T extends EntityRole> ObjectIterator<T> listByName(SystemEntity systemEntity, Class<T> roleClass, String name) {
    	return listByName(systemEntity, roleClass, name, false);
    }
    
    public static <T extends EntityRole> ObjectIterator<T> listByName(final SystemEntity systemEntity, final Class<T> roleClass, String name, final boolean any) {
		return Entity.list(name).convert(e -> get(systemEntity, roleClass, e, any));
    }

    @Override
    public Id getContactOwnerId() {
        return getContactGroupingCode() == 0 ? organizationId : getId();
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
        return organizationId;
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
        return getOrganization().getName();
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
