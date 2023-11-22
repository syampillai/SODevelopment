package com.storedobject.core;

import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;

@SuppressWarnings("RedundantThrows")
public final class BranchAccount extends Account {

    private Id branchId;

    public BranchAccount() {
    }

    public static void columns(Columns columns) {
        columns.add("Branch", "id");
    }

    public static void indices(Indices indices) {
        indices.add("SystemEntity,Branch", true);
    }

    public static String[] protectedColumns() {
        return new String[] { "Name" };
    }

    public void setBranch(Id branchId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Branch");
        }
        this.branchId = branchId;
    }

    public void setBranch(BigDecimal idValue) {
        setBranch(new Id(idValue));
    }

    public void setBranch(SystemEntity branch) {
        setBranch(branch.getId());
    }

    @SetNotAllowed
    public Id getBranchId() {
        return branchId;
    }

    public SystemEntity getBranch() {
        return get(SystemEntity.class, branchId);
    }

    public String getTitle() {
        return getBranch().toString();
    }

    public static BranchAccount createTo(TransactionManager tm, SystemEntity branch) throws Exception {
        return new BranchAccount();
    }

    public static BranchAccount createFrom(TransactionManager tm, SystemEntity master) throws Exception {
        return new BranchAccount();
    }
}
