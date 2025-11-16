package com.storedobject.ui.support;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.common.MemoSystem;
import com.storedobject.vaadin.View;
import com.storedobjects.support.*;
import com.vaadin.flow.component.HasValue;

import java.util.ArrayList;
import java.util.List;

public class SupportSystem extends MemoSystem {

    private List<Organization> organizations;
    private boolean user = false;
    private ObjectField<Product> productField;

    public SupportSystem() {
        this("SS");
    }

    public SupportSystem(MemoType type) {
        super(type);
    }

    public SupportSystem(String typeShortName) {
        super(typeShortName);
    }

    public SupportSystem(MemoType type, boolean load) {
        super(type, load);
    }

    @Override
    protected void checkMemoType(MemoType type) {
        if(type == null || !type.getSpecial()) {
            throw new SORuntimeException("Not a support type");
        }
    }

    @Override
    protected String whoName(SystemUser who) {
        return user ? (who.getName() + " (" + organizations.getFirst().getName() + ")") : super.whoName(who);
    }

    @Override
    protected boolean filterWho(SystemUser who) {
        if(!user) {
            return false;
        }
        SupportUser supportUser = StoredObject.get(SupportUser.class, "SupportUser=" + who.getId());
        return supportUser != null && supportUser.getOrganizationId().equals(organizations.getFirst().getId());
    }

    @Override
    protected boolean canAssignAssistant() {
        return user;
    }

    @Override
    protected boolean canAssist() {
        return user;
    }

    @Override
    protected boolean canCreateNew() {
        return user;
    }

    @Override
    protected boolean canReject() {
        return false;
    }

    @Override
    protected String getApproveLabel() {
        return "Mark as Resolved";
    }

    @Override
    public void execute(View lock) {
        if(organizations == null) {
            clearAlerts();
            organizations = new ArrayList<>();
            SystemUser su = getTransactionManager().getUser();
            SupportUser supportUser = StoredObject.get(SupportUser.class, "SupportUser=" + su.getId());
            if(supportUser != null) {
                organizations.add(supportUser.getOrganization());
                user = true;
                setProductField(productField);
            }
            if(!user) {
                SupportPerson supportPerson = StoredObject.get(SupportPerson.class, "Person=" + su.getId());
                if (supportPerson == null) {
                    warning("You are not part of the support system");
                    return;
                }
                supportPerson.listLinks(Organization.class).collectAll(organizations);
                if (organizations.isEmpty()) {
                    warning("You are not assigned to any organization to provide support");
                }
            }
        }
        super.execute(lock);
        Application.get().closeMenu();
    }

    private void setProductField(ObjectField<Product> field) {
        if(field == null) {
            return;
        }
        this.productField = field;
        if(organizations != null && user) {
            Organization organization = organizations.getFirst();
            productField.setLoadFilter(p -> organization.existsLinks(Product.class, "Id=" + p.getId()));
        }
    }

    @Override
    protected String getMemoLabel() {
        return "Issue";
    }

    @Override
    protected String getCreateLabel() {
        return "Log";
    }

    @Override
    protected <M extends Memo> MemoEditor<M> createMemoEditor(Class<M> memoClass) {
        if(Issue.class.isAssignableFrom(memoClass)) {
            return createIssueEditor(memoClass);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <M extends Memo, I extends Issue> MemoEditor<M> createIssueEditor(Class<M> memoClass) {
        Class<I> issueClass = (Class<I>) memoClass;
        return (MemoEditor<M>) new IssueEditor<>(issueClass);
    }

    private static boolean checkAlertHandler = true;

    private class IssueEditor<I extends Issue> extends MemoEditor<I> {

        protected IssueEditor(Class<I> objectClass) {
            super(objectClass);
            if(checkAlertHandler) {
                checkAlertHandler = false;
                if (!StoredObject.exists(ApplicationAlertHandler.class, "DataClassName='com.storedobjects.support.Issue")) {
                    ApplicationAlertHandler aah = new ApplicationAlertHandler();
                    aah.setDataClassName(Issue.class.getName());
                    aah.setLogicClassName(SupportSystem.class.getName());
                    try {
                        getTransactionManager().transact(aah::save);
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        @Override
        public boolean isFieldVisible(String fieldName) {
            return !"SystemEntity".equals(fieldName) && super.isFieldEditable(fieldName);
        }

        @Override
        protected void customizeField(String fieldName, HasValue<?, ?> field) {
            if("Product".equals(fieldName) && field instanceof ObjectField<?>) {
                //noinspection unchecked
                setProductField((ObjectField<Product>) field);
            }
            super.customizeField(fieldName, field);
        }
    }
}
