 package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public final class MemoComment extends StoredObject {

    private static final String[] statusValues =
            new String[] {
                    "Not Seen", "Being Reviewed", "Returned", "Approved", "Rejected", "On Hold", "Reopened", "Escalated"
            };
    Id memoId;
    Memo memo;
    String comment = "";
    Timestamp commentedAt = DateUtility.now();
    Id commentedById;
    int commentCount = 0;
    int status = 0;

    public MemoComment() {
    }

    public static void columns(Columns columns) {
        columns.add("Memo", "id");
        columns.add("CommentCount", "int");
        columns.add("Comment", "text");
        columns.add("CommentedAt", "timestamp");
        columns.add("CommentedBy", "id");
        columns.add("Status", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Memo,CommentCount", true);
        indices.add("Memo,CommentedBy");
    }

    public static String[] browseColumns() {
        return new String[] {
                "Memo.Type.Name AS Type", "Reference", "Memo.Subject AS Subject / Short Description",
                "Memo.InitiatedBy.Person.Name AS Initiated by", "PendingWith",
                "CommentedAt AS Time", "MemoStatus AS Status",
        };
    }

    public static String[] protectedColumns() {
        return new String[] {
                "CommentedAt", "CommentCount", "Status",
        };
    }

    public static String[] links() {
        return new String[] {
                "Attachments|com.storedobject.core.MemoAttachment|||0",
        };
    }

    public void setMemo(Id memoId) {
        this.memoId = memoId;
    }

    public void setMemo(BigDecimal idValue) {
        setMemo(new Id(idValue));
    }

    public void setMemo(Memo memo) {
        setMemo(memo == null ? null : memo.getId());
    }

    @Column(order = 100, style = "(any)")
    public Id getMemoId() {
        return memoId;
    }

    public Memo getMemo() {
        if(memo == null) {
            memo = getRelated(Memo.class, memoId, true);
        }
        return memo;
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        memo = null;
    }

    public void setCommentCount(int commentCount) {
        if(commentCount < 0) {
            throw new Set_Not_Allowed("Comment Count");
        }
        this.commentCount = commentCount;
    }

    @Column(order = 200)
    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentedAt(Timestamp commentedAt) {
        this.commentedAt = new Timestamp(commentedAt.getTime());
        this.commentedAt.setNanos(commentedAt.getNanos());
    }

    @Column(order = 400)
    public Timestamp getCommentedAt() {
        return new Timestamp(commentedAt.getTime());
    }

    public void setCommentedBy(Id commentedById) {
        this.commentedById = commentedById;
    }

    public void setCommentedBy(BigDecimal idValue) {
        setCommentedBy(new Id(idValue));
    }

    public void setCommentedBy(SystemUser commentedBy) {
        setCommentedBy(commentedBy == null ? null : commentedBy.getId());
    }

    @Column(order = 500)
    public Id getCommentedById() {
        return commentedById;
    }

    public SystemUser getCommentedBy() {
        return getRelated(SystemUser.class, commentedById);
    }

    public void setStatus(int status) {
        if(!loading() && status != this.status) {
            throw new Set_Not_Allowed("Status");
        }
        this.status = status;
    }

    @Column(order = 800, readOnly = true)
    public int getStatus() {
        return status;
    }

    public static String[] getStatusValues() {
        return statusValues;
    }

    public static String getStatusValue(int value) {
        String[] s = getStatusValues();
        return s[value % s.length];
    }

    public String getStatusValue() {
        return getMemo().renameCommentAction(getStatusValue(status));
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column(style = "(large)", order = 100000)
    public String getComment() {
        return comment;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        memoId = tm.checkTypeAny(this, memoId, Memo.class, false);
        super.validateData(tm);
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        if(commentCount > 0) {
            throw new SOException(Memo.ILLEGAL);
        }
        if(commentCount == 0) {
            if(exists(MemoComment.class, "Memo=" + getId() + " AND CommentCount>0")) {
                if(status != 5) { // Closing by the original owner?
                    throw new SOException(Memo.ILLEGAL);
                }
            }
        }
        commentCount = -commentCount;
    }

    public String getPendingWith() {
        return getMemo().pendingWith(this);
    }

    public boolean isLatest() {
        return getMemo().isLatestComment(this);
    }

    private void saveInt(Transaction t) throws Exception {
        commentCount = -commentCount;
        save(t);
    }

    private void deleteAlert(Transaction transaction) throws Exception {
        for(LoginMessage m : getMemo().listMasters(5, LoginMessage.class)) {
            if(m.existsLinks(SystemUser.class, "Id=" + commentedById)) {
                m.delete(transaction);
            }
        }
    }

    private void alert(Transaction transaction, String message) throws Exception {
        alert(transaction, message, getCommentedBy());
    }

    private void alert(Transaction transaction, String message, SystemUser to) throws Exception {
        SystemUser sendTo = to == null ? getMemo().getInitiatedBy() : to;
        if((sendTo.getStatus() & 0b110) > 0) {
            return; // System/process users
        }
        LoginMessage.alert(transaction, getMemo().getReference() + " (Subject: " + getMemo().getSubject() + ") "
                + " is " + message, sendTo, to == null ? null : getMemo());
    }

    public void assignAssistant(Transaction transaction, SystemUser assistant) throws Exception {
        Id uid = transaction.getUserId();
        if(!uid.equals(commentedById) && !uid.equals(getMemo().getInitiatedById())) {
            throw new SOException(Memo.ILLEGAL);
        }
        if(getMemo().getStatus() >= 4) {
            throw new SOException("Can't assign, status is '" + getMemoStatus() + "'");
        }
        if(memo.getAssistedById().equals(assistant.getId())) {
            return;
        }
        if(!assistant.existsMaster(uid)) {
            throw new SOException(assistant.getName() + " can't assist " + getCommentedBy().getName());
        }
        memo.internal = true;
        memo.setAssistedBy(assistant);
        memo.save(transaction);
    }

    public void recallMemo(Transaction transaction) throws Exception {
        MemoComment mc = getPrevious();
        if(mc == null || !canRecall(transaction.getManager().getUser())) {
            throw new SOException(Memo.ILLEGAL);
        }
        Memo m = getMemo();
        m.lastComment = mc.commentCount;
        m.internal = true;
        m.save(transaction);
        commentCount = -commentCount;
        deleteAlert(transaction);
        delete(transaction);
        mc.status = 1; // Being reviewed
        mc.saveInt(transaction);
    }

    public void returnMemoToInitiator(Transaction transaction, String reason) throws Exception {
        if(!canReturnToInitiator(transaction.getManager().getUser())) {
            throw new SOException("Can't return to the initiator");
        }
        returnMemo(transaction, reason, true);
    }

    public void returnMemo(Transaction transaction, String reason) throws Exception {
        if(status == 2) { // Was returned to me
            throw new SOException(Memo.ILLEGAL);
        }
        returnMemo(transaction, reason, false);
    }

    private void returnMemo(Transaction transaction, String reason, boolean toInitiator) throws Exception {
        preprocess(transaction, true);
        if(reason.isBlank()) {
            throw new SOException("Empty reason");
        }
        Memo m = getMemo();
        m.lastComment = commentCount + 1;
        MemoComment pre = toInitiator ? getFirst() : getPrevious();
        if(pre == null) {
            throw new SOException(Memo.ILLEGAL);
        }
        if(pre.getCommentedBy().getStatus() != 0) {
            throw new SOException("Can't return to " + pre.getCommentedBy().getPerson().getName());
        }
        m.returning(pre);
        m.status = 2; // Returned
        m.internal = true;
        m.save(transaction);
        pre.makeNew();
        pre.commentedAt = DateUtility.now();
        pre.comment = "";
        pre.status = 2; // Returned
        pre.commentCount = m.lastComment;
        comment = reason;
        status = 2; // Returned for me too
        saveInt(transaction);
        deleteAlert(transaction);
        pre.saveInt(transaction);
        pre.alert(transaction, "returned to you");
    }

    public void forwardMemo(Transaction transaction, String comment, SystemUser to) throws Exception {
        preprocess(transaction, true);
        if(status >= 3) {
            throw new SOException("Can't forward, status is '" + getStatusValue() + "'");
        }
        forwardMemoInt(transaction, comment, to, false);
    }

    private void forwardMemoInt(Transaction transaction, String comment, SystemUser to, boolean escalate) throws Exception {
        if(comment == null || comment.isBlank()) {
            throw new SOException("Empty comment");
        }
        status = escalate ? 7 : 1;
        this.comment = comment;
        Memo m = getMemo();
        if(m.status == 0 || m.status == 2) {
            m.status = 1;
        }
        m.lastComment = commentCount + 1;
        MemoComment mc = createFor(to);
        mc.commentCount = m.lastComment;
        mc.status = 0; // Not seen initially
        saveInt(transaction);
        deleteAlert(transaction);
        m.internal = true;
        m.save(transaction);
        mc.saveInt(transaction);
        mc.alert(transaction, (escalate ? "escalated" : "forwarded") + " to you");
    }

    private MemoComment createFor(SystemUser su) {
        MemoComment mc = list(MemoComment.class, "Memo=" + memoId + " AND CommentedBy=" + su.getId(),
                "CommentCount DESC").findFirst();
        if(mc == null) {
            mc = new MemoComment();
            mc.memoId = memoId;
            mc.commentedById = su.getId();
        } else {
            mc.makeNew();
            mc.commentedAt = DateUtility.now();
            mc.comment = "";
        }
        return mc;
    }

    public void reopenMemo(Transaction transaction, String reason) throws Exception {
        SystemUser su = transaction.getManager().getUser();
        if(!canReopen(su)) {
            throw new SOException(Memo.ILLEGAL);
        }
        if(reason == null || reason.isBlank()) {
            throw new SOException("Empty reason");
        }
        MemoComment mc = createFor(su);
        mc.comment = reason;
        mc.status = 6;
        Memo m = getMemo();
        m.reopening();
        mc.commentCount = m.lastComment + 1;
        mc.saveInt(transaction);
        mc = createFor(su);
        mc.status = 0;
        mc.commentCount = m.lastComment + 2;
        m.status = 0;
        m.lastComment = mc.commentCount;
        m.internal = true;
        m.save(transaction);
        mc.saveInt(transaction);
    }

    public void escalateMemo(Transaction transaction, String reason) throws Exception {
        if(!canEscalate(transaction.getManager().getUser())) {
            throw new SOException(Memo.ILLEGAL);
        }
        SystemUser nextUser = getMemo().escalating();
        if(nextUser == null) {
            throw new SOException("No one to escalate to");
        }
        preprocess(transaction, true);
        String m = comment;
        if(m.isBlank()) {
            m = reason;
        } else {
            m += "\nReason for escalation: " + reason;
        }
        forwardMemoInt(transaction, m, nextUser, true);
    }

    public void approveMemo(Transaction transaction, String approvalText) throws Exception {
        approveMemo(transaction, approvalText, null);
    }

    public void approveMemo(Transaction transaction, String approvalText, SystemUser forwardTo) throws Exception {
        Memo m = getMemo();
        preprocess(transaction, false);
        if(status >= 3) {
            throw new SOException("Can't " + m.renameActionVerb("approve") + ", status is '" + getStatusValue() + "'");
        }
        SystemUser me = transaction.getManager().getUser();
        if(!m.listApprovers(transaction.getManager().getEntity()).contains(me)
                || getFirst().commentedById.equals(me.getId())) {
            throw new SOException("No authority");
        }
        if(exists(MemoComment.class, "Memo=" + memoId + " AND Status=3 AND CommentedBy=" + me.getId())) {
            MemoComment reopened = list(MemoComment.class, "Memo=" + memoId + " AND Status=6", "CommentCount DESC")
                    .findFirst(); // Was reopened?
            if(reopened != null) { // Yes, it was reopened
                if(exists(MemoComment.class, "Memo=" + memoId + " AND Status=3 AND CommentedBy="
                        + me.getId() + " AND CommentCount>" + reopened.commentCount)) {
                    // It was approved by this guy
                    reopened = null;
                }
            }
            if(reopened == null) throw new SOException("Was already " + m.renameActionVerb("approved") + " by " + me.getName());
        }
        int approvals = count(MemoComment.class, "Memo=" + memoId + " AND Status=3") + 1;
        int approvalsRequired = m.getApprovalsRequired();
        if(forwardTo == null) {
            if(approvalsRequired > approvals) {
                throw new SOException("Further " + m.renameActionVerb("approvals") + " required");
            }
        } else {
            if(approvalsRequired == approvals) {
                throw new SOException("No further " + m.renameActionVerb("approvals") + " required");
            }
        }
        if(approvalText == null || approvalText.isBlank()) {
            comment = m.renameActionVerb("Approved");
        } else {
            comment = approvalText;
        }
        status = 3; // Approved
        m.status = forwardTo == null ? 4 : 3;
        if(forwardTo != null) {
            m.lastComment = commentCount + 1;
            MemoComment mc = createFor(forwardTo);
            mc.commentCount = m.lastComment;
            mc.status = 0; // Not seen initially
            mc.saveInt(transaction);
            mc.alert(transaction, "forwarded for further " + m.renameActionVerb("approval"));
        }
        m.internal = true;
        if(forwardTo == null) { // Final approval
            m.approved();
            alert(transaction, m.renameActionVerb("approved"), null);
        }
        m.save(transaction);
        saveInt(transaction);
        deleteAlert(transaction);
    }

    public void rejectMemo(Transaction transaction, String reason) throws Exception {
        preprocess(transaction, false);
        if(status >= 3) {
            throw new SOException("Can't reject, status is '" + getStatusValue() + "'");
        }
        SystemUser me = transaction.getManager().getUser();
        Memo m = getMemo();
        if(!m.listApprovers(transaction.getManager().getEntity()).contains(me)
                || getFirst().commentedById.equals(me.getId())) {
            throw new SOException("No authority");
        }
        if(reason == null || reason.isBlank()) {
            throw new SOException("Empty reason");
        }
        comment = reason;
        status = 4; // Rejected
        m.status = 5;
        m.internal = true;
        m.setTransaction(transaction);
        m.rejected();
        m.save();
        saveInt(transaction);
        deleteAlert(transaction);
        alert(transaction, m.renameActionVerb("rejected"), null);
    }

    public void commentMemo(Transaction transaction, String comment) throws Exception {
        preprocess(transaction, true);
        if(status >= 3) {
            throw new SOException("Can't comment, status is '" + getStatusValue() + "'");
        }
        this.comment = comment == null ? "" : comment;
        if(status != 2) { // Returned memo should not change the status
            status = this.comment.isBlank() ? 0 : 1;
        }
        saveInt(transaction);
        deleteAlert(transaction);
    }

    public boolean canClose(SystemUser su) {
        Memo m = getMemo();
        if(m.status < 10 && m.getInitiatedById().equals(su.getId())) return true;
        switch(m.status) {
            case 4, 5 -> {
                // Approved/Rejected
            }
            default -> {
                return false;
            }
        }
        MemoComment mc = m.getLatestComment();
        return mc.getCommentedById().equals(su.getId())
                && DateUtility.now().getTime() - mc.getCommentedAt().getTime() > m.getAutocloseTime();
    }

    public void closeMemo(Transaction transaction) throws  Exception {
        if(!canClose(transaction.getManager().getUser())) {
            throw new SOException(Memo.ILLEGAL);
        }
        Memo m = getMemo();
        m.status += 10; // Closed
        m.internal = true;
        m.save(transaction);
    }

    private void preprocess(Transaction transaction, boolean checkEnteredBy) throws Exception {
        Id uid = transaction.getUserId();
        if(getMemo().lastComment != commentCount
                || (!commentedById.equals(uid)) && (!checkEnteredBy || !getMemo().getAssistedById().equals(uid))) {
            throw new SOException(Memo.ILLEGAL);
        }
    }

    public MemoComment getPrevious() {
        if(commentCount == 0) {
            return null;
        }
        return mc(commentCount - 1);
    }

    private MemoComment mc(int count) {
        return get(MemoComment.class, "Memo=" + memoId + " AND CommentCount=" + count);
    }

    public MemoComment getNext() {
        return mc(commentCount + 1);
    }

    public MemoComment getFirst() {
        if(commentCount == 0) {
            return this;
        }
        return mc(0);
    }

    public MemoComment getLatest() {
        if(commentCount == getMemo().lastComment) {
            return this;
        }
        return mc(getMemo().lastComment);
    }

    public String getReference() {
        return getMemo().getReference();
    }

    public String getMemoStatus() {
        return getMemo().getMemoStatus();
    }

    public String getContent() {
        return commentCount == 0 ? comment : getMemo().getContent();
    }

    public boolean canEdit(SystemUser su) {
        if(commentCount != 0 || !isMine(su)) {
            return false;
        }
        Memo memo = getMemo();
        return memo.getLastComment() == 0 && memo.status <= 1;
    }

    public boolean canEditSubject(SystemUser su) {
        if(!isMine(su)) {
            return false;
        }
        Memo memo = getMemo();
        if(memo.status >= 3) {
            return false;
        }
        if(commentCount == 0) {
            return true;
        }
        return memo.getInitiatedById().equals(su.getId());
    }

    public boolean canApprove(SystemUser su) {
        if(commentCount == 0 || commentCount != getMemo().getLastComment() || getMemo().status >= 4 || isMyMemo(su)
                || !commentedById.equals(su.getId())) {
            return false;
        }
        List<SystemUser> set = getMemo().listApprovers();
        if(set.isEmpty() || !set.contains(su)) return false;
        boolean approvedBefore = exists(MemoComment.class, "Memo=" + memoId + " AND Status=3 AND CommentedBy="
                + su.getId());
        if(!approvedBefore) return true;
        MemoComment reopened = list(MemoComment.class, "Memo=" + memoId + " AND Status=6", "CommentCount DESC")
                .findFirst(); // Was reopened?
        if(reopened == null) return false;
        return !exists(MemoComment.class, "Memo=" + memoId + " AND Status=3 AND CommentedBy=" + su.getId()
                + " AND CommentCount>" + reopened.commentCount);
    }

    public boolean canForward(SystemUser su) {
        if(commentCount != getMemo().getLastComment() || status >= 3 || !isMine(su)) {
            return false;
        }
        Memo m = getMemo();
        return !m.listCommenters().isEmpty() || !m.listApprovers().isEmpty();
    }

    public boolean canReturn(SystemUser su) {
        if(isMyMemo(su)) {
            return false;
        }
        return commentCount > 0 && switch(status) {
            case 0, 1 -> true;
            default -> false;
        } && isMine(su);
    }

    public boolean canReturnToInitiator(SystemUser su) {
        Memo m = getMemo();
        if(commentCount != m.getLastComment() || su.getId().equals(getFirst().commentedById)) {
            return false;
        }
        return m.canReturnToInitiator(this);
    }

    public boolean canRecall(SystemUser su) {
        if(status != 0) return false;
        MemoComment previous = getPrevious();
        return previous != null && previous.status != 6 && previous.isMine(su); // status != 6 means not reopened
    }

    public boolean canComment(SystemUser su) {
        if(commentCount != getMemo().getLastComment() || !isMine(su)) {
            return false;
        }
        return switch(status) {
            case 0, 1, 2 -> true;
            default -> false;
        };
    }

    public boolean canReopen(SystemUser su) {
        return getMemo().canReopen(su);
    }

    public boolean canEscalate(SystemUser su) {
        return getMemo().canEscalate(su);
    }

    public boolean isMine(SystemUser su) {
        return commentedById.equals(su.getId()) ||
                (getMemo().getAssistedById().equals(su.getId())) && getLatest().getCommentedById().equals(su.getId());
    }

    public boolean isMyMemo(SystemUser su) {
        return commentCount == 0 ? commentedById.equals(su.getId()) : getMemo().isMyMemo(su);
    }
}
