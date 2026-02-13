package com.storedobject.core;

import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Memo extends StoredObject implements OfEntity {

    final static String ILLEGAL = "Illegal modification attempt";
    private static final String[] statusValues =
            new String[] {
                    "Initiated", "Forwarded", "Returned", "Being Approved", "Approved", "Rejected"
            };
    private Id typeId;
    private MemoType type;
    private int no = 0;
    int lastComment = 0;
    final Date date = DateUtility.today();
    private String subject;
    int status = 0;
    boolean internal = false;
    private Id systemEntityId;
    private SystemUser initiatedBy;
    private Id assistedById = Id.ZERO;

    public Memo() {
    }

    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
        columns.add("Type", "id");
        columns.add("No", "int");
        columns.add("Date", "date");
        columns.add("Subject", "text");
        columns.add("Status", "int");
        columns.add("LastComment", "int");
        columns.add("AssistedBy", "id");
    }

    public static void indices(Indices indices) {
        indices.add("SystemEntity,No", "Status<10", false);
        indices.add("SystemEntity,No",false);
    }

    public static String[] browseColumns() {
        return new String[] {
                "Reference", "Date", "Type.Name AS Type", "Subject AS Subject / Short Description", "Status",
                "PendingWith",
        };
    }

    public static String[] protectedColumns() {
        return new String[] {
                "LastComment",
        };
    }

    public final void setSystemEntity(Id systemEntityId) {
        if(!loading()) {
            throw new Set_Not_Allowed("System Entity");
        }
        this.systemEntityId = systemEntityId;
    }

    public final void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    public final void setSystemEntity(SystemEntity systemEntity) {
        setSystemEntity(systemEntity == null ? null : systemEntity.getId());
    }

    @SetNotAllowed
    @Column(order = 10, caption = "Of")
    public final Id getSystemEntityId() {
        return systemEntityId;
    }

    public final SystemEntity getSystemEntity() {
        return SystemEntity.getCached(systemEntityId);
    }

    public final void setType(Id typeId) {
        if (!loading() && !Id.equals(this.typeId, typeId)) {
            throw new Set_Not_Allowed("Type");
        }
        this.typeId = typeId;
    }

    public final void setType(BigDecimal idValue) {
        setType(new Id(idValue));
    }

    public final void setType(MemoType type) {
        setType(type == null ? null : type.getId());
    }

    @SetNotAllowed
    @Column(order = 100)
    public Id getTypeId() {
        return typeId;
    }

    public final MemoType getType() {
        if(type == null) {
            type = MemoType.get(this);
        }
        return type;
    }

    public void setNo(int no) {
        if (!loading()) {
            throw new Set_Not_Allowed("No");
        }
        this.no = no;
    }

    @SetNotAllowed
    @Column(style = "(serial)", order = 200)
    public final int getNo() {
        if (no == 0) {
            Transaction t = getTransaction();
            no = SerialGenerator.generate(t, "MEMO-" + systemEntityId + "-" + typeId).intValue();
        }
        return no;
    }

    public void setDate(Date date) {
        if (!loading() && !DateUtility.isSameDate(date, this.date)) {
            throw new Set_Not_Allowed("Date");
        }
        this.date.setTime(date.getTime());
    }

    @SetNotAllowed
    @Column(order = 300)
    public final Date getDate() {
        return new Date(date.getTime());
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Column(order = 500, caption = "Subject / Short Description")
    public String getSubject() {
        return subject;
    }

    public final void setStatus(int status) {
        if(!loading() && status != this.status) {
            throw new Set_Not_Allowed("Status");
        }
        this.status = status;
    }

    @SetNotAllowed
    @Column(order = 400, readOnly = true)
    public final int getStatus() {
        return status;
    }

    public static String[] getStatusValues() {
        return statusValues;
    }

    public static String getStatusValue(int value) {
        if(value >= 10) value -= 10;
        String[] s = getStatusValues();
        return s[value % s.length];
    }

    public String getStatusValue() {
        return renameAction(getStatusValue(status)) + (status >= 10 ? " - Closed" : "");
    }

    protected  String getMemoStatus() {
        return getStatusValue();
    }

    public String getApproveLabel() {
        return "Approve";
    }

    public String renameAction(String action) {
        return action;
    }

    public String renameCommentAction(String action) {
        return action;
    }

    public String renameActionVerb(String action) {
        return action;
    }

    public final void setLastComment(int lastComment) {
        if (!loading() && this.lastComment != lastComment) {
            throw new Set_Not_Allowed("Last Comment");
        }
        this.lastComment = lastComment;
    }

    @SetNotAllowed
    @Column(order = 600)
    public final int getLastComment() {
        return lastComment;
    }

    public void setAssistedBy(Id assistedById) {
        this.assistedById = assistedById;
    }

    public void setAssistedBy(BigDecimal idValue) {
        setAssistedBy(new Id(idValue));
    }

    public void setAssistedBy(SystemUser assistedBy) {
        setAssistedBy(assistedBy == null ? null : assistedBy.getId());
    }

    @Column(order = 700, required = false, readOnly = true)
    public Id getAssistedById() {
        return assistedById;
    }

    public SystemUser getAssistedBy() {
        return getRelated(SystemUser.class, assistedById);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        systemEntityId = check(tm, systemEntityId);
        typeId = tm.checkType(this, typeId, MemoType.class, false);
        assistedById = tm.checkType(this, assistedById, SystemUser.class, true);
        if(StringUtility.isWhite(subject)) {
            throw new Invalid_Value("Empty subject");
        }
        super.validateData(tm);
    }

    public final String getPendingWith() {
        if(status >= 4 || (lastComment == 0 && status == 0)) {
            return "None";
        }
        return pendingWith(getLatestComment());
    }

    String pendingWith(MemoComment mc) {
        if(status >= 4 || (lastComment == 0 && status == 0)) {
            return "None";
        }
        if(mc == null) {
            return "Unknown";
        }
        if(lastComment != mc.commentCount) {
            return pendingWith(getLatestComment());
        }
        return mc.getCommentedBy().getName();
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        throw new SOException("Delete not allowed");
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        if(!internal) {
            throw new SOException(ILLEGAL);
        }
    }

    public MemoComment getLatestComment() {
        MemoComment mc = get(MemoComment.class, "Memo=" + getId() + " AND CommentCount=" + lastComment);
        if(mc != null) mc.memo = this;
        return mc;
    }

    /**
     * Determines if the assistant can assist the boss based on their relationship.
     * This method checks if both users are not null and if a link exists between them.
     *
     * @param boss The SystemUser who requires assistance.
     * @param assistant The SystemUser who might assist the boss.
     * @return {@code true} if the assistant can assist the boss, {@code false} otherwise.
     */
    protected boolean canAssist(SystemUser boss, SystemUser assistant) {
        return boss != null && assistant != null && boss.existsLink(assistant);
    }

    public Id save(Transaction transaction, String content, SystemUser enteredFor, Memo parent) throws Exception {
        boolean created = created();
        if(content == null || content.isBlank()) {
            if(created) {
                content = subject;
            } else {
                throw new SOException("Empty content");
            }
        }
        if(enteredFor == null) {
            throw new SOException("Owner");
        }
        Id uid = transaction.getUserId();
        if(!uid.equals(enteredFor.getId()) && !canAssist(transaction.getManager().getUser(), enteredFor)) {
            throw new Invalid_State(enteredFor.getName() + " can't assist " + transaction.getManager().getUser().getName());
        }
        MemoComment mc;
        internal = true;
        assistedById = uid.equals(enteredFor.getId()) ? Id.ZERO : uid;
        Id id = save(transaction);
        if(created) {
            mc = new MemoComment();
            mc.memoId = id;
            mc.commentedById = enteredFor.getId();
            mc.status = 1;
        } else {
            if(lastComment != 0 || status > 1) {
                throw new SOException(ILLEGAL);
            }
            mc = get(MemoComment.class, "Memo=" + getId() + " AND CommentCount=0");
            if(!mc.commentedById.equals(enteredFor.getId())) {
                throw new SOException(ILLEGAL);
            }
        }
        mc.comment = content;
        mc.save(transaction);
        if(parent != null) {
            parent.addLink(transaction, this);
        }
        return id;
    }

    public void updateSubject(Transaction transaction, String subject) throws Exception {
        if(status >= 4) {
            throw new SOException("Can't change subject when status is '" + getStatusValue() + "'");
        }
        Id owner = getInitiatedById();
        if(!owner.equals(transaction.getUserId())) {
            MemoComment mc = get(MemoComment.class, "Memo=" + getId() + " AND CommentCount=" + lastComment);
            if(mc == null || !mc.getCommentedById().equals(owner) || !transaction.getUserId().equals(assistedById)) {
                throw new SOException(ILLEGAL);
            }
        }
        internal = true;
        this.subject = subject;
        save(transaction);
    }

    @Override
    void savedCore() throws Exception {
        internal = false;
        super.savedCore();
    }

    public final boolean isLatestComment(MemoComment memoComment) {
        return memoComment.getMemoId().equals(getId()) && memoComment.getCommentCount() == lastComment;
    }

    public final String getReference() {
        return getType().getShortPrefix() + "-" + (no == 0 ? "___" : no);
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

    @Override
    public String toDisplay() {
        return getReference() + " [" + getStatusValue() + "] " + subject;
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

    /**
     * Escalates the memo to the next level.
     * <p>Note: This is not used by the basic memo system because there is no concept of "escalation" in the memo system.
     * However, it could be implemented in a derived class.</p>
     *
     * @param transaction The transaction object.
     * @throws Exception If an error occurs during the saving or escalation process.
     */
    protected void escalate(Transaction transaction) throws Exception {
        save(transaction);
    }

    /**
     * This is invoked just before the memo is reopened. You may set/change attributes of the memo at this stage. The
     * memo will be saved after this call.
     * <p>Note: This is not used by the basic memo system because there is no concept of reopening a memo.
     * However, it could be implemented in derived classes.</p>
     */
    protected void reopening() {
    }

    /**
     * This is invoked just before the memo is returned. You may set/change attributes of the memo at this stage. The
     * memo will be saved after this call.
     *
     * @param comment Comment entry for the receiver of the memo.
     */
    protected void returning(MemoComment comment) {
    }

    public final String getContent() {
        MemoComment mc = getInitialComment();
        return mc == null ? "" : mc.getComment();
    }

    public MemoComment getInitialComment() {
        return get(MemoComment.class, "Memo=" + getId() + " AND CommentCount=0");
    }

    public final Id getInitiatedById() {
        getInitiatedBy();
        return initiatedBy == null ? Id.ZERO : initiatedBy.getId();
    }

    public SystemUser getInitiatedBy() {
        if(initiatedBy == null) {
            MemoComment mc = getInitialComment();
            initiatedBy = mc == null ? null : mc.getCommentedBy();
        }
        return initiatedBy;
    }

    public SystemUser getLastCommentBy() {
        if(lastComment == 0) {
            return null;
        }
        MemoComment mc = getLatestComment();
        if(mc == null) {
            return null;
        }
        if(mc.status > 0) {
            return mc.getCommentedBy();
        }
        if(lastComment == 1) {
            return null;
        }
        mc = get(MemoComment.class, "Memo=" + getId() + " AND CommentCount=" + (lastComment - 1));
        return mc == null ? null : mc.getCommentedBy();
    }

    public boolean isMine(SystemUser su) {
        return getInitialComment().isMine(su);
    }

    public boolean isMyMemo(SystemUser su) {
        return getInitialComment().getCommentedById().equals(su.getId());
    }

    public List<SystemUser> listApprovers() {
        return getType().approvers();
    }

    public List<SystemUser> listNextLevelApprovers() {
        return listApprovers();
    }

    public List<SystemUser> listCommenters() {
        return getType().commenters();
    }

    public final Set<SystemUser> listCommenters(SystemEntity forEntity) {
        Set<SystemUser> set = listFor(forEntity, listCommenters());
        set.addAll(listApprovers(forEntity));
        return set;
    }

    public final Set<SystemUser> listApprovers(SystemEntity forEntity) {
        return listFor(forEntity, listApprovers());
    }

    private Set<SystemUser> listFor(SystemEntity forEntity, List<SystemUser> users) {
        HashSet<SystemUser> set = new HashSet<>();
        users.stream().filter(u -> {
            if(getType().getCrossEntity() || forEntity == null) {
                return true;
            }
            if(set.contains(u)) {
                return false;
            }
            return u.listEntities().contains(forEntity);
        }).forEach(set::add);
        return set;
    }

    public boolean canReturnToInitiator(MemoComment latestComment) {
        if(lastComment == 0 || latestComment == null || latestComment.commentCount != lastComment) {
            return false;
        }
        if(latestComment.commentedById.equals(getInitialComment().commentedById)) {
            return false;
        }
        return switch (status) {
            case 1, 2 -> true;
            default -> false;
        };
    }

    public boolean canReopen(SystemUser su) {
        return false;
    }

    public boolean canEscalate(SystemUser su) {
        return false;
    }

    public String whyNoTakers() {
        if(!listApprovers().isEmpty()) {
            return null;
        }
        if(listCommenters().isEmpty()) {
            return "Problem: No commenters/approvers";
        }
        return null;
    }

    protected long getAutocloseTime() {
        return 2592000L; // 30 days = 30 * 24 * 60 * 60 * 1000L;
    }

    /**
     * Determines whether this memo can have predecessors associated with it.
     * <p>Note: The basic memos don't have predecessors. However, other derived memos may have predecessors.</p>
     *
     * @return {@code true} if there is a predecessor, {@code false} otherwise.
     */
    public boolean canHavePredecessors() {
        return false;
    }
}
