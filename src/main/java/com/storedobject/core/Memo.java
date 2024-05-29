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
                    "Initiated", "Forwarded", "Returned", "Being Approved", "Approved", "Rejected", "Abandoned"
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
    @Column(order = 400)
    public final int getStatus() {
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
        return renameAction(getStatusValue(status));
    }

    protected  String getMemoStatus() {
        return getStatusValue();
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

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        systemEntityId = check(tm, systemEntityId);
        typeId = tm.checkType(this, typeId, MemoType.class, false);
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
        return get(MemoComment.class, "Memo=" + getId() + " AND CommentCount=" + lastComment);
    }

    public Id save(Transaction transaction, String content, SystemUser enteredFor) throws Exception {
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
        MemoComment mc;
        internal = true;
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
        mc.enteredById = transaction.getUserId();
        mc.comment = content;
        mc.save(transaction);
        return id;
    }

    public void updateSubject(Transaction transaction, String subject) throws Exception {
        if(status >= 4) {
            throw new SOException("Can't change subject when status is '" + getStatusValue() + "'");
        }
        Id owner = getInitiatedById();
        if(!owner.equals(transaction.getUserId())) {
            MemoComment mc = get(MemoComment.class, "Memo=" + getId() + " AND CommentCount=" + lastComment);
            if(mc == null || !mc.getCommentedById().equals(owner)
                    || mc.getEnteredById().equals(transaction.getUserId())) {
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
        return getReference() + " ("+ getType().getName() + ")";
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
     * This method is invoked when a memo is escalated to another level. This should return a system user at next level.
     * <p>Note: This is not used by the basic memo system because there is no concept of "escalation" in memo system.
     * However, it could be implemented in a derived class.</p>
     *
     * @return A system user at the next level.
     */
    protected SystemUser escalating() {
        return null;
    }

    /**
     * This is invoked just before the memo is reopened. You may set/change attributes of the memo at this stage. The
     * memo will be saved after this call.
     * <p>Note: This is not used by the basic memo system because there is no concept of reopening a memo.
     * However, it could be implemented in derived classes.</p>
     */
    protected void reopening() {
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
        MemoComment mc = get(MemoComment.class, "Memo=" + getId() + " AND CommentCount=" + lastComment);
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

    public List<SystemUser> listApprovers() {
        return getType().approvers();
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

    public boolean canReopen(SystemUser su) {
        return false;
    }

    public boolean canEscalate(SystemUser su) {
        return false;
    }
}
