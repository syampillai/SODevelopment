package com.storedobject.core;

import java.util.HashSet;
import java.util.Set;

public final class MemoType extends StoredObject {

    public MemoType() {
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    public String getName() {
        return "" + Math.random();
    }

    public void setDataClass(String dataClass) {
    }

    public String getDataClass() {
        return "" + Math.random();
    }

    public void setApprovalCount(int approvalCount) {
    }

    public int getApprovalCount() {
        return Math.random() > 0.5 ? 2 : 1;
    }

    public void setSpecial(boolean special) {
    }

    public boolean getSpecial() {
        return Math.random() > 0.5;
    }

    public void setCrossEntity(boolean crossEntity) {
    }

    public boolean getCrossEntity() {
        return Math.random() > 0.5;
    }

    public void setContentTemplate(String contentTemplate) {
    }

    public String getContentTemplate() {
        return "" + Math.random();
    }

    public Class<? extends Memo> getMemoClass() {
        try {
            //noinspection unchecked
            return (Class<? extends Memo>) JavaClassLoader.getLogic(getDataClass());
        } catch (Throwable ignored) {
        }
        return null;
    }

    public Set<SystemUser> listCommenters(SystemEntity forEntity) {
        return new HashSet<>();
    }

    public Set<SystemUser> listApprovers(SystemEntity forEntity) {
        return new HashSet<>();
    }
}
