package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public class AccessControl extends StoredObject {

	public static final String MESSAGE = "Access restricted, please contact your Service Provider";
    private Id organizationId = Id.ZERO;
    private boolean blockPDF;
    private boolean blockODT;
    private boolean blockODS;
    private boolean blockExcel;

    public AccessControl() {
    }

    public static void columns(Columns columns) {
        columns.add("Organization", "id");
        columns.add("BlockPDF", "boolean");
        columns.add("BlockODT", "boolean");
        columns.add("BlockODS", "boolean");
        columns.add("BlockExcel", "boolean");
    }
    
    public static void indices(Indices indices) {
        indices.add("Organization", true);
    }

    public String getUniqueCondition() {
        return "Organization=" + getOrganizationId();
    }

    public void setOrganization(Id organizationId) {
        this.organizationId = organizationId;
    }

    public void setOrganization(BigDecimal idValue) {
        setOrganization(new Id(idValue));
    }

    public void setOrganization(SystemEntity organization) {
        setOrganization(organization == null ? null : organization.getId());
    }

    @Column(required = false, order = 100)
    public Id getOrganizationId() {
        return organizationId;
    }

    public SystemEntity getOrganization() {
        return get(SystemEntity.class, organizationId);
    }

    public void setBlockPDF(boolean blockPDF) {
        this.blockPDF = blockPDF;
    }

    @Column(caption = "Block PDF", order = 200)
    public boolean getBlockPDF() {
        return blockPDF;
    }

    public void setBlockODT(boolean blockODT) {
        this.blockODT = blockODT;
    }

    @Column(caption = "Block ODT", order = 300)
    public boolean getBlockODT() {
        return blockODT;
    }

    public void setBlockODS(boolean blockODS) {
        this.blockODS = blockODS;
    }

    @Column(caption = "Block ODS", order = 400)
    public boolean getBlockODS() {
        return blockODS;
    }

    public void setBlockExcel(boolean blockExcel) {
        this.blockExcel = blockExcel;
    }

    @Column(order = 500)
    public boolean getBlockExcel() {
        return blockExcel;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        organizationId = tm.checkType(this, organizationId, SystemEntity.class, true);
        super.validateData(tm);
    }
    
    public static AccessControl get(SystemEntity entity) {
    	AccessControl ac = null;
    	if(entity == null) {
    		ac = get(AccessControl.class, "Organization=0");
    		if(ac == null) {
    			ac = list(AccessControl.class).findFirst();
    		}
    	} else {
    		ac = get(AccessControl.class, "Organization=" + entity.getId());
    		if(ac == null) {
    			ac = get(AccessControl.class, "Organization=0");
    		}
    	}
    	return ac == null ? new AccessControl() : ac;
    }
    
    public static AccessControl get(Entity entity) {
    	if(entity == null) {
    		return get((SystemEntity)null);
    	}
    	return get(get(SystemEntity.class, "Entity=" + entity.getId()));
    }
}