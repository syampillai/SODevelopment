package com.storedobject.ui.support;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.MemoType;
import com.storedobject.core.StoredObject;
import com.storedobject.core.SystemUser;
import com.storedobject.ui.common.MemoSystem;
import com.storedobject.vaadin.View;
import com.storedobjects.support.Organization;
import com.storedobjects.support.SupportPerson;
import com.storedobjects.support.SupportUser;

import java.util.ArrayList;
import java.util.List;

public class SupportSystem extends MemoSystem {

    private List<Organization> organizations;
    private boolean user = false;

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
        return user ? (who.getName() + " (" + organizations.get(0).getName() + ")") : super.whoName(who);
    }

    @Override
    protected boolean filterWho(SystemUser who) {
        if(!user) {
            return false;
        }
        SupportUser supportUser = StoredObject.get(SupportUser.class, "SupportUser=" + who.getId());
        return supportUser != null && supportUser.getOrganizationId().equals(organizations.get(0).getId());
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
    }

    @Override
    protected String getMemoLabel() {
        return "Issue";
    }

    @Override
    protected String getCreateLabel() {
        return "Log";
    }
}
