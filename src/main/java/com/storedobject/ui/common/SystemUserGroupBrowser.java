package com.storedobject.ui.common;

import com.storedobject.common.StringList;
import com.storedobject.core.SystemUser;
import com.storedobject.core.SystemUserGroup;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ViewGrid;
import com.vaadin.flow.component.icon.VaadinIcon;

public class SystemUserGroupBrowser extends ObjectBrowser<SystemUserGroup> {

    public SystemUserGroupBrowser() {
        super(SystemUserGroup.class);
    }

    public SystemUserGroupBrowser(String className) {
        this();
    }

    public SystemUserGroupBrowser(Iterable<String> browseColumns) {
        super(SystemUserGroup.class, browseColumns);
    }

    public SystemUserGroupBrowser(Iterable<String> browseColumns, Iterable<String> filterColumns) {
        super(SystemUserGroup.class, browseColumns, filterColumns);
    }

    public SystemUserGroupBrowser(int actions) {
        super(SystemUserGroup.class, actions);
    }

    public SystemUserGroupBrowser(int actions, String caption) {
        super(SystemUserGroup.class, actions, caption);
    }

    public SystemUserGroupBrowser(Iterable<String> browseColumns, int actions) {
        super(SystemUserGroup.class, browseColumns, actions);
    }

    public SystemUserGroupBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns) {
        super(SystemUserGroup.class, browseColumns, actions, filterColumns);
    }

    public SystemUserGroupBrowser(Iterable<String> browseColumns, int actions, String caption) {
        super(SystemUserGroup.class, browseColumns, actions, caption);
    }

    public SystemUserGroupBrowser(Iterable<String> browseColumns, int actions, Iterable<String> filterColumns, String caption) {
        super(SystemUserGroup.class, browseColumns, actions, filterColumns, caption);
    }

    @Override
    protected void addExtraButtons() {
        buttonPanel.add(new Button("Members", VaadinIcon.GROUP, e -> viewMembers()));
    }

    private void viewMembers() {
        clearAlerts();
        SystemUserGroup g = selected();
        if(g == null) {
            return;
        }
        if(g.getName().equalsIgnoreCase("default")) {
            message("Every user is a member of this group");
            return;
        }
        ViewGrid<SystemUser> viewGrid = new ViewGrid<>(SystemUser.class, g.listUsers().toList(),
                StringList.create("Login", "Name", "Status"), "Members - " + g.getName());
        viewGrid.setEmptyRowsMessage("No members found in this group");
        viewGrid.execute(SystemUserGroupBrowser.this.getView());
    }
}
