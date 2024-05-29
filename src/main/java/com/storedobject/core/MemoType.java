package com.storedobject.core;

import com.storedobject.common.ArrayListSet;
import com.storedobject.core.annotation.Column;

import java.util.*;

public final class MemoType extends StoredObject {

    private static final Map<Id, MemoType> cache = new Hashtable<>();
    private String name, shortPrefix;
    private String dataClass = Memo.class.getName();
    private int approvalCount = 1;
    private boolean special, crossEntity = true, inactive;
    private String contentTemplate;
    private List<SystemUser> approvers;
    private List<SystemUser> commenters;

    public MemoType() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("ShortPrefix", "text");
        columns.add("DataClass", "text");
        columns.add("ApprovalCount", "int");
        columns.add("Special", "boolean");
        columns.add("CrossEntity", "boolean");
        columns.add("ContentTemplate", "text");
        columns.add("Inactive", "boolean");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name)", true);
        indices.add("ShortPrefix", true);
    }

    public static String[] links() {
        return new String[] {
                "Commenters|SystemUserGroup",
                "Approvers|SystemUserGroup/1",
        };
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(order = 100)
    public String getName() {
        return name;
    }

    public void setShortPrefix(String shortPrefix) {
        this.shortPrefix = shortPrefix;
    }

    @Column(order = 200, style = "(upper)")
    public String getShortPrefix() {
        return shortPrefix;
    }

    public void setDataClass(String dataClass) {
        this.dataClass = dataClass;
    }

    @Column(order = 300)
    public String getDataClass() {
        return dataClass;
    }

    public void setApprovalCount(int approvalCount) {
        this.approvalCount = approvalCount;
    }

    @Column(order = 400, caption = "Approvals Required")
    public int getApprovalCount() {
        return approvalCount;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    @Column(order = 500)
    public boolean getSpecial() {
        return special;
    }

    public void setCrossEntity(boolean crossEntity) {
        this.crossEntity = crossEntity;
    }

    @Column(order = 600)
    public boolean getCrossEntity() {
        return crossEntity;
    }

    public void setContentTemplate(String contentTemplate) {
        this.contentTemplate = contentTemplate;
    }

    @Column(style = "(large)", order = 700, required = false)
    public String getContentTemplate() {
        return contentTemplate;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    @Column(order = 800)
    public boolean getInactive() {
        return inactive;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(approvalCount <= 0) {
            approvalCount = 1;
        }
        if (StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        shortPrefix = toCode(shortPrefix);
        if (StringUtility.isWhite(shortPrefix)) {
            throw new Invalid_Value("Short Prefix");
        }
        checkForDuplicate("ShortPrefix");
        if (getMemoClass() == null) {
            throw new Invalid_Value("Data Class");
        }
        super.validateData(tm);
    }

    @Override
    public void validateUpdate() throws Exception {
        MemoType old = get(MemoType.class, getId());
        if(!old.getShortPrefix().equals(shortPrefix)) {
            String memoUI = "com.storedobject.ui.common.MemoSystem|";
            for(Logic logic: list(Logic.class, "lower(ClassName)='"
                    + (memoUI + old.getShortPrefix()).toLowerCase() + "'")) {
                logic.setClassName(memoUI + shortPrefix);
                logic.save(getTransaction());
            }
        }
        super.validateUpdate();
    }

    @Override
    void savedCore() throws Exception {
        approvers = commenters = null;
        cache.put(getId(), this);
        super.savedCore();
    }

    public Class<? extends Memo> getMemoClass() {
        try {
            //noinspection unchecked
            return (Class<? extends Memo>) JavaClassLoader.getLogic(dataClass);
        } catch (Throwable ignored) {
        }
        return null;
    }

    static MemoType get(Memo memo) {
        Id tid = memo.getTypeId();
        MemoType memoType = cache.get(tid);
        if(memoType == null) {
            memoType = memo.getRelated(MemoType.class, tid);
            if(!memo.old()) {
                cache.put(tid, memoType);
            }
        }
        return memoType;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String toDisplay() {
        return name;
    }

    List<SystemUser> approvers() {
        if(approvers == null) {
            approvers = new ArrayListSet<>();
            listLinks(1, SystemUserGroup.class).forEach(sg -> sg.listUsers().forEach(approvers::add));
        }
        return approvers;
    }

    List<SystemUser> commenters() {
        if(commenters == null) {
            commenters = new ArrayListSet<>();
            listLinks(SystemUserGroup.class).forEach(sg -> sg.listUsers().forEach(commenters::add));
        }
        return commenters;
    }
}
