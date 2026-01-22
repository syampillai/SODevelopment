package com.storedobject.ui.support;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.common.MemoSystem;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;
import com.storedobjects.support.Issue;
import com.storedobjects.support.Organization;
import com.storedobjects.support.Product;
import com.storedobjects.support.SupportUser;

public class SupportSystemView extends MemoSystem {

    private Organization organization;
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
        if(!(m instanceof Issue issue)) return false;
        if(organization != null && !issue.getOrganizationId().equals(organization.getId())) return false;
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
        return organization == null ? "All Organizations" : organization.getName();
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
        return "Select an organization";
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
                new SelectOrganization().execute(lock);
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
        new SelectOrganization().execute(parent);
    }

    private class SelectOrganization extends DataForm {

        private final ObjectComboField<Organization> organizationField = new ObjectComboField<>("Organization", Organization.class);

        public SelectOrganization() {
            super("Select Organization");
            organizationField.setPlaceholder("Leave blank for showing all organizations");
            organizationField.setClearButtonVisible(true);
            addField(organizationField);
        }

        @Override
        protected boolean process() {
            init = true;
            organization = organizationField.getObject();
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
