package com.storedobject.ui.support;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.common.MemoSystem;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.RadioChoiceField;
import com.storedobject.vaadin.View;
import com.storedobjects.support.*;

import java.util.HashSet;
import java.util.Set;

public class SupportSystemView extends MemoSystem {

    private OrganizationGroup group;
    private final Set<Id> organizationIds = new HashSet<>();
    private Organization organization;
    private SelectOrganization selector;
    private boolean user;
    private View parent;
    private final ObjectComboField<Product> productFilterField = new ObjectComboField<>(Product.class);
    private Id productFilterId;
    private boolean init = false;

    public SupportSystemView() {
        this("SS");
    }

    public SupportSystemView(String typeShortName) {
        this(memoType(typeShortName));
    }

    public SupportSystemView(MemoType type) {
        super(type, StringList.concat(StoredObjectUtility.browseColumns(MemoComment.class), StringList.create("Product", "Organization")), false);
        productFilterField.setMinWidth("300px");
        productFilterField.setClearButtonVisible(true);
        productFilterField.addValueChangeListener(e -> productChanged());
    }

    @Override
    protected String memoLoadFilter() {
        return null;
    }

    @Override
    protected <M extends Memo> void loadMemos() {
        super.loadMemos();
        setColumnVisible("Organization", organization == null);
    }

    private void productChanged() {
        Product product = productFilterField.getValue();
        productFilterId = product == null ? null : product.getId();
        loadMemos();
    }

    @Override
    protected boolean filter(Memo m) {
        if (!(m instanceof Issue issue)) return false;
        if (group != null) {
            if (!organizationIds.contains(issue.getOrganizationId())) return false;
        } else if (organization != null) {
            if (!issue.getOrganizationId().equals(organization.getId())) return false;
        }
        return productFilterId == null || issue.getProductId().equals(productFilterId);
    }

    @Override
    protected void addToHeader(ButtonLayout buttonLayout) {
        buttonLayout.add(new ELabel("|", Application.COLOR_INFO), new ELabel("Product Filter"),
                productFilterField);
    }

    @Override
    protected void checkUser(SystemUser who) {
    }

    @Override
    protected String whoName(SystemUser who) {
        return group != null ? ("Group: " + group.getName())
                : (organization == null ? "All Organizations" : organization.getName());
    }

    @Override
    protected boolean isViewMode() {
        return true;
    }

    @Override
    protected boolean canAssignAssistant() {
        return false;
    }

    @Override
    protected String assistantMessage(SystemUser who) {
        return "Select an organization or group";
    }

    @Override
    protected boolean canCreateNew() {
        return false;
    }

    public String getProduct(MemoComment mc) {
        return ((Issue)mc.getMemo()).getProduct().toDisplay();
    }

    public String getOrganization(MemoComment mc) {
        return ((Issue)mc.getMemo()).getOrganization().getName();
    }

    @Override
    public void execute(View lock) {
        parent = lock;
        if(!init) {
            SupportUser supportUser = StoredObject.get(SupportUser.class, "SupportUser="
                    + getTransactionManager().getUser().getId());
            if(supportUser == null) {
                user = false;
                selector().execute(lock);
                return;
            } else {
                init = true;
                user = true;
                organization = supportUser.getOrganization();
            }
        }
        super.execute(lock);
        whoButton();
        loadMemos();
    }

    @Override
    protected void selectWho() {
        if(user) {
            clearAlerts();
            message("You don't have access to organizations");
            return;
        }
        selector().execute(parent);
    }

    private SelectOrganization selector() {
        if(selector == null) {
            selector = new SelectOrganization();
        }
        return selector;
    }

    private class SelectOrganization extends DataForm {

        private final RadioChoiceField choiceField = new RadioChoiceField("Choose", new String[] { "Organization", "Group" });
        private final ObjectComboField<OrganizationGroup> groupField = new ObjectComboField<>("Group", OrganizationGroup.class);
        private final ObjectComboField<Organization> organizationField = new ObjectComboField<>("Organization", Organization.class);

        public SelectOrganization() {
            super("Select Organization");
            groupField.setVisible(false);
            organizationField.setPlaceholder("Leave blank for showing all organizations");
            organizationField.setClearButtonVisible(true);
            addField(choiceField, organizationField, groupField);
            choiceField.addValueChangeListener(e -> {
                if(choiceField.getValue() == 0) {
                    setFieldVisible(organizationField);
                    setFieldHidden(groupField);
                    organizationField.focus();
                } else {
                    setFieldVisible(groupField);
                    setFieldHidden(organizationField);
                    groupField.focus();
                }
            });
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            super.execute(parent, doNotLock);
            if(choiceField.getValue() == 0) {
                organizationField.focus();
            } else {
                groupField.focus();
            }
        }

        @Override
        protected boolean process() {
            clearAlerts();
            if(choiceField.getValue() == 0) {
                organization = organizationField.getObject();
                group = null;
                organizationIds.clear();
            } else {
                group = groupField.getObject();
                if(group == null) {
                    groupField.focus();
                    return false;
                }
                group.listLinks(Organization.class).forEach(o -> organizationIds.add(o.getId()));
                if(organizationIds.isEmpty()) {
                    message("No organization found in this group");
                    groupField.focus();
                    return false;
                }
            }
            init = true;
            close();
            SupportSystemView.this.execute(parent);
            return true;
        }

        @Override
        public int getMinimumContentWidth() {
            return 40;
        }
    }
}
