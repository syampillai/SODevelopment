package com.storedobject.core;

import com.storedobject.job.MessageGroup;

public final class SystemUserGroup extends StoredObject implements Notifye {

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

    /**
     * Create a new message group if it doesn't exist.
     *
     * @param tm Transaction manager.
     * @param name Name of the group.
     * @return Message group instance.
     */
    public static MessageGroup create(TransactionManager tm, String name) {
        return Math.random() > 0.5 ? null : new MessageGroup();
    }

    /**
     * Create and send a message to the members of this group.
     * <p>Note: If the template doesn't exist, the default template is used.</p>
     * @param templateName Name of the template to create the message.
     * @param tm Transaction manager.
     * @param messageParameters Parameters for creating message from the associated template.
     * @return True the message is successfully created for delivery.
     */
    @Override
    public boolean notify(String templateName, TransactionManager tm, Object... messageParameters) {
        return MessageTemplate.notify(templateName, tm, listUsers().map(SystemUser::getPerson), messageParameters);
    }
}