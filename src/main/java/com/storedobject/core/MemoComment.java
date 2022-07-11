package com.storedobject.core;

import java.sql.Timestamp;
import java.math.BigDecimal;

public final class MemoComment extends StoredObject {

    public MemoComment() {
    }

    public static void columns(Columns columns) {
    }

    public void setMemo(Id memoId) {
    }

    public void setMemo(BigDecimal idValue) {
        setMemo(new Id(idValue));
    }

    public void setMemo(Memo memo) {
        setMemo(memo == null ? null : memo.getId());
    }

    public Id getMemoId() {
        return new Id();
    }

    public Memo getMemo() {
        return new Memo();
    }

    public void setCommentCount(int commentCount) {
    }

    public int getCommentCount() {
        return getStatus();
    }

    public void setCommentedAt(Timestamp commentedAt) {
    }

    public Timestamp getCommentedAt() {
        return new Timestamp(0);
    }

    public void setCommentedBy(Id commentedById) {
    }

    public void setCommentedBy(BigDecimal idValue) {
        setCommentedBy(new Id(idValue));
    }

    public void setCommentedBy(SystemUser commentedBy) {
        setCommentedBy(commentedBy == null ? null : commentedBy.getId());
    }

    public Id getCommentedById() {
        return new Id();
    }

    public SystemUser getCommentedBy() {
        return new SystemUser();
    }

    public void setEnteredBy(Id enteredById) {
    }

    public void setEnteredBy(BigDecimal idValue) {
        setEnteredBy(new Id(idValue));
    }

    public void setEnteredBy(SystemUser enteredBy) {
        setEnteredBy(enteredBy == null ? null : enteredBy.getId());
    }

    public Id getEnteredById() {
        return new Id();
    }

    public SystemUser getEnteredBy() {
        return new SystemUser();
    }

    public void setStatus(int status) {
    }

    public int getStatus() {
        return Math.random() > 0.5 ? 4 : 1;
    }

    public static String[] getStatusValues() {
        return new String[3];
    }

    public static String getStatusValue(int value) {
        return "" + value;
    }

    public String getStatusValue() {
        return "" + Math.random();
    }

    public void setComment(String comment) {
    }

    public String getComment() {
        return "" + Math.random();
    }

    public String getPendingWith() {
        return "" + Math.random();
    }

    public boolean isLatest() {
        return getMemo().isLatestComment(this);
    }

    public void assignAssistant(Transaction transaction, SystemUser assistant) throws Exception {
        if(Math.random() > 0.5) {
            throw new SOException();
        }
    }

    public void recallMemo(Transaction transaction) throws Exception {
        if(Math.random() > 0.5) {
            throw new SOException();
        }
    }

    public void returnMemo(Transaction transaction, String reason) throws Exception {
        if(Math.random() > 0.5) {
            throw new SOException();
        }
    }

    public void forwardMemo(Transaction transaction, String comment, SystemUser to) throws Exception {
        if(Math.random() > 0.5) {
            throw new SOException();
        }
    }

    public void approveMemo(Transaction transaction, String approvalText) throws Exception {
        if(Math.random() > 0.5) {
            throw new SOException();
        }
    }

    public void approveMemo(Transaction transaction, String approvalText, SystemUser forwardTo) throws Exception {
        if(Math.random() > 0.5) {
            throw new SOException();
        }
    }

    public void rejectMemo(Transaction transaction, String reason) throws Exception {
        if(Math.random() > 0.5) {
            throw new SOException();
        }
    }

    public void commentMemo(Transaction transaction, String comment) throws Exception {
        if(Math.random() > 0.5) {
            throw new SOException();
        }
    }

    public void closeMemo(Transaction transaction) throws  Exception {
        if(Math.random() > 0.5) {
            throw new SOException();
        }
    }

    public MemoComment getNext() {
        return Math.random() > 0.5 ? new MemoComment() : null;
    }

    public MemoComment getFirst() {
        return new MemoComment();
    }

    public MemoComment getCurrent() {
        return new MemoComment();
    }

    public String getReference() {
        return getMemo().getReference();
    }

    public String getMemoStatus() {
        return getMemo().getStatusValue();
    }

    public String getContent() {
        return getMemo().getContent();
    }
}
