package com.storedobjects.support;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Issue extends Memo {

    static final Map<String, List<SystemUser>> approvers = new HashMap<>();
    private int level = 0;
    private Id productId, productModuleId = Id.ZERO, organizationId;

    public Issue() {
    }

    public static void columns(Columns columns) {
        columns.add("Level", "int");
        columns.add("Product", "id");
        columns.add("ProductModule", "id");
    }

    public static String[] protectedColumns() {
        return new String[] {
                "Level"
        };
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Column(order = 450)
    public int getLevel() {
        return level;
    }

    public static String[] getLevelValues() {
        return ProductSkill.getSkillLevelValues();
    }

    public static String getLevelValue(int value) {
        String[] s = getLevelValues();
        return s[value % s.length];
    }

    public String getLevelValue() {
        return getLevelValue(level);
    }

    public void setProduct(Id productId) {
        if (!loading() && !Id.equals(this.getProductId(), productId)) {
            throw new Set_Not_Allowed("Product");
        }
        this.productId = productId;
    }

    public void setProduct(BigDecimal idValue) {
        setProduct(new Id(idValue));
    }

    public void setProduct(Product product) {
        setProduct(product == null ? null : product.getId());
    }

    @SetNotAllowed
    @Column(order = 100, caption = "Product/Service")
    public Id getProductId() {
        return productId;
    }

    public Product getProduct() {
        return Product.get(productId);
    }

    public void setProductModule(Id productModuleId) {
        if (!Id.equals(this.getProductModuleId(), productModuleId)) {
            throw new SORuntimeException("Product Module doesn't belong to '" + getProduct().toDisplay() + "'");
        }
        this.productModuleId = productModuleId;
    }

    public void setProductModule(BigDecimal idValue) {
        setProductModule(new Id(idValue));
    }

    public void setProductModule(ProductModule productModule) {
        setProductModule(productModule == null ? null : productModule.getId());
    }

    @Column(order = 110, required = false)
    public Id getProductModuleId() {
        return productModuleId;
    }

    public ProductModule getProductModule() {
        return ProductModule.get(productModuleId);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        productId = tm.checkType(this, productId, Product.class, false);
        productModuleId = tm.checkType(this, productModuleId, ProductModule.class, true);
        ProductModule pm = getProductModule();
        if(pm != null && !productId.existsLink(pm)) {
            throw new Invalid_State("'" + pm.getName() + "' is not a module of '" + getProduct().getName() + "'");
        }
        super.validateData(tm);
    }

    @Override
    public String getApproveLabel() {
        return "Mark as " + (getProduct().getInternal() ? "Completed" : "Resolved");
    }

    @Override
    public String renameAction(String action) {
        return switch (action) {
            case "Forwarded" -> "Open";
            case "Being Approved" -> "Working on";
            case "Approved" -> getProduct().getInternal() ? "Completed" : "Resolved";
            case "Rejected" -> "On Hold";
            case "Abandoned" -> "Closed";
            default -> action;
        };
    }

    @Override
    public String renameCommentAction(String action) {
        return switch (action) {
            case "Approved" -> getProduct().getInternal() ? "Completed" : "Resolved";
            case "Rejected" -> "On Hold";
            case "Abandoned" -> "Closed";
            default -> action;
        };
    }

    @Override
    public String renameActionVerb(String action) {
        return switch (action) {
            case "approve" -> "resolve";
            case "approved" -> getProduct().getInternal() ? "completed" : "resolved";
            case "Approved" -> getProduct().getInternal() ? "Completed" : "Resolved";
            case "approval", "approvals" -> "investigation";
            case "rejected" -> "on hold";
            default -> action;
        };
    }

    @Override
    protected String getMemoStatus() {
        if(level == 0) {
            return super.getMemoStatus();
        }
        return getStatusValue() + " (" + getLevelValue() + ")";
    }

    @Override
    public int getApprovalsRequired() {
        return 1;
    }

    public Id getOrganizationId() {
        if(organizationId == null) {
            SupportUser su = get(SupportUser.class, "SupportUser=" + getInitiatedById());
            organizationId = su == null ? Id.ZERO : su.getOrganizationId();
        }
        return organizationId;
    }

    public Organization getOrganization() {
        return Organization.get(getOrganizationId());
    }

    public Entity getEntity() {
        Organization org = getOrganization();
        return org == null ? getSystemEntity().getEntity() : org.getEntity();
    }

    @Override
    protected void escalate(Transaction transaction) throws Exception {
        ++level;
        super.escalate(transaction);
    }

    @Override
    protected void returning(MemoComment comment) {
        if(level == 0) {
            return;
        }
        SupportPerson sp = SupportPerson.getFor(comment.getCommentedBy());
        if(sp == null) {
            level = 0;
            return;
        }
        List<ProductSkill> skills = sp.listLinks(ProductSkill.class, "Product=" + productId).toList();
        for(ProductSkill skill : skills) {
            if(skill.getSkillLevel() < level) {
                level = skill.getSkillLevel();
                return;
            }
        }
    }

    @Override
    protected void reopening() {
        level = 0;
    }

    @Override
    public boolean canReturnToInitiator(MemoComment latestComment) {
        return level == 0 && super.canReturnToInitiator(latestComment);
    }

    private List<SystemUser> listApprovers(int level) {
        String key = productId + "/" + getOrganizationId() + "/" + level;
        List<SystemUser> approvers = Issue.approvers.get(key);
        if(approvers == null) {
            approvers = new ArrayList<>();
            List<SystemUser> finalApprovers = approvers;
            ObjectIterator<SupportPerson> sps = list(SupportPerson.class)
                    .filter(sp -> sp.existsLinks(ProductSkill.class, skillMatch()));
            if(!Id.isNull(organizationId)) {
                sps = sps.filter(sp -> !sp.existsLinks(Organization.class) // Not assigned to any specific organization, so take as all
                        || sp.existsLinks(Organization.class, "Id=" + organizationId));
            }
            sps.forEach(sp -> finalApprovers.add(sp.getPerson()));
            Issue.approvers.put(key, approvers);
        }
        return approvers;
    }

    private String skillMatch() {
        return "Product=" + productId + " AND SkillLevel=" + level;
    }

    @Override
    public List<SystemUser> listApprovers() {
        return listApprovers(level);
    }

    @Override
    public List<SystemUser> listNextLevelApprovers() {
        return listApprovers(level + 1);
    }

    @Override
    public List<SystemUser> listCommenters() {
        return listApprovers();
    }

    @Override
    public String whyNoTakers() {
        if(listApprovers().isEmpty()) {
            StringBuilder sb = new StringBuilder("Product: ");
            sb.append(getProduct().toDisplay()).append(", Organization: ")
                    .append(getEntity().getName())
                    .append("\nSkill Level: ")
                    .append(level + 1)
                    .append(", Problem: No one found to ");
            if(list(SupportPerson.class)
                    .filter(sp -> sp.existsLinks(ProductSkill.class, skillMatch())).findFirst() == null) {
                sb.append("handle this skill level");
            } else {
                sb.append("support this organization");
            }
            return sb.toString();
        }
        return null;
    }

    @Override
    public boolean canReopen(SystemUser su) {
        int status = getStatus();
        if(status < 10) {
            switch (status) {
                case 4, 5 -> { // Approved / Rejected
                }
                default -> {
                    return false;
                }
            }
        }
        if(!isMine(su) && !su.getId().equals(getAssistedById())) { // Not mine and not helped by me
            return false;
        }
        return switch (getLatestComment().getStatus()) {
            case 3, 4, 5 -> true; // Approved / Rejected / Closed
            default -> false;
        };
    }

    @Override
    public boolean canEscalate(SystemUser su) {
        if(level == 2) {
            return false;
        }
        int status = getStatus();
        if(status >= 10) {
            return false;
        }
        switch (status) {
            case 1, 2, 3, 5 -> { // Forwarded / Returned / Being Approved / Rejected
            }
            default -> {
                return false;
            }
        }
        MemoComment mc = getLatestComment();
        if(!mc.isMine(su)) {
            return false;
        }
        return switch (getLatestComment().getStatus()) {
            case 3, 5, 6, 7 -> false; // Approved / Closed / Reopened / Escalated
            default -> true;
        };
    }

    @Override
    protected boolean canAssist(SystemUser boss, SystemUser assistant) {
        if(super.canAssist(boss, assistant)) return true;
        if(boss == null || assistant == null) return false;
        return exists(SupportPerson.class, "Person=" + boss.getId()); // A support person can help anyone
    }
}
