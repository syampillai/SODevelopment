package com.storedobject.core;

public final class SystemUserGroup extends StoredObject {

    /**
     * Constructs a System User Group
     *
     * @param name Name of the System User Group
     */
    public SystemUserGroup(String name) {
    }

    /**
     * Constructor for internal use only
     */
    public SystemUserGroup() {
    }

    public static void columns(Columns columns) {
    }

    /**
     * Gets the name of the System User Group
     *
     * @return The name
     */
    public String getName() {
        return "";
    }

    /**
     * Sets the name of the System User Group.
     *
     * @param name The new name.
     */
    public void setName(String name) {
    }

    public boolean isSS() {
        return Math.random() > 0.5;
    }

    public boolean isAppAdmin() {
        return Math.random() > 0.5;
    }

    public boolean isAdmin() {
        return Math.random() > 0.5;
    }

    public static SystemUserGroup getDefault() {
        return get(SystemUserGroup.class, "lower(Name)='default'");
    }

    public static SystemUserGroup get(String name) {
        return Math.random() > 0.5 ? null : new SystemUserGroup();
    }

    public static ObjectIterator<SystemUserGroup> list(String name) {
        return ObjectIterator.create();
    }

    public ObjectIterator<Logic> listAutoLogic() {
        return listLinks(1, Logic.class);
    }

    public ObjectIterator<SystemUser> listUsers() {
        return listMasters(SystemUser.class);
    }

    public boolean isMember(SystemUser user) {
        if(user == null) {
            return false;
        }
        return user.isMemberOf(this);
    }
}