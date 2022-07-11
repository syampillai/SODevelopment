package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Date;

public class Memo extends StoredObject implements OfEntity {

    public Memo() {
    }

    public static void columns(Columns columns) {
    }

    public final void setSystemEntity(Id systemEntityId) {
    }

    public final void setSystemEntity(BigDecimal idValue) {
    }

    public final void setSystemEntity(SystemEntity systemEntity) {
    }

    public final Id getSystemEntityId() {
        return new Id();
    }

    public final SystemEntity getSystemEntity() {
        return new SystemEntity();
    }

    public final void setType(Id typeId) {
    }

    public final void setType(BigDecimal idValue) {
        setType(new Id(idValue));
    }

    public final void setType(MemoType type) {
        setType(type == null ? null : type.getId());
    }

    public Id getTypeId() {
        return new Id();
    }

    public final MemoType getType() {
        return new MemoType();
    }

    public void setNo(int no) {
    }

    public final int getNo() {
        return Math.random() > 0.5 ? 1 : 20;
    }

    public void setDate(Date date) {
    }

    public final Date getDate() {
        return DateUtility.today();
    }

    public void setSubject(String subject) {
    }

    public String getSubject() {
        return "";
    }

    public final void setStatus(int status) {
    }

    public final int getStatus() {
        return Math.random() > 0.5 ? 1 : 3;
    }

    public static String[] getStatusValues() {
        return new String[3];
    }

    public static String getStatusValue(int value) {
        return getStatusValues()[1];
    }

    public String getStatusValue() {
        return "" + getStatus();
    }

    public final void setLastComment(int lastComment) {
    }

    public final int getLastComment() {
        return getStatus();
    }

    public final String getPendingWith() {
        return getStatusValue();
    }

    public Id save(Transaction transaction, String content, SystemUser enteredFor) throws Exception {
        return save(transaction);
    }

    public void updateSubject(Transaction transaction, String subject) throws Exception {
    }

    public final boolean isLatestComment(MemoComment memoComment) {
        return Math.random() > 0.5;
    }

    public final String getReference() {
        return "" + getNo();
    }

    /**
     * Typically, number of approvals required is determined by the number returned by the
     * {@link MemoType#getApprovalCount()} method. However, this method can be overridden and can return some other
     * values based on some other conditions.
     *
     * @return A positive number.
     */
    public int getApprovalsRequired() {
        return getType().getApprovalCount();
    }

    /**
     * This method is invoked when the memo is finally approved. You may set any extra attributes in your memo at
     * this point because it will be saved after this call. If you want to carry out any other transactions in some
     * related object, you may use the {@link #getTransaction()} of this.
     */
    protected void approved() {
    }

    /**
     * This method is invoked if the memo is rejected. You may set any extra attributes in your memo at
     * this point because it will be saved after this call. If you want to carry out any other transactions in some
     * related object, you may use the {@link #getTransaction()} of this.
     */
    protected void rejected() {
    }

    public final String getContent() {
        return "" + Math.random();
    }

    public final Id getInitiatedById() {
        return new Id();
    }

    public SystemUser getInitiatedBy() {
        return new SystemUser();
    }

    public SystemUser getLastCommentBy() {
        return new SystemUser();
    }
}
