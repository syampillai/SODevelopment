package com.storedobject.job;

import com.storedobject.common.ArrayList;
import com.storedobject.common.ArrayListSet;
import com.storedobject.common.EndUserMessage;
import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;

import java.math.BigDecimal;
import java.util.List;

public final class MessageGroup extends StoredObject implements RequiresApproval{

    private String name;
    private Id templateId;

    public MessageGroup() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("Template", "id");
    }

    public static int hints() {
        return ObjectHint.SMALL | ObjectHint.SMALL_LIST;
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name)", true);
        indices.add("Template");
    }

    @Override
    public String getUniqueCondition() {
        return "lower(Name)='" + getName().trim().toLowerCase().replace("'", "''") + "'";
    }

    public static MessageGroup getFor(String name) {
        MessageGroup mg = get(name);
        return mg != null && mg.getName().equals(name(name)) ? mg : null;
    }

    public static MessageGroup get(String name) {
        name = name(name);
        MessageGroup mg = StoredObjectUtility.get(MessageGroup.class, "Name", name, false);
        return mg == null ? list(name).single(false) : mg;
    }

    public static ObjectIterator < MessageGroup > list(String name) {
        name = name(name);
        return StoredObjectUtility.list(MessageGroup.class, "Name", name, false);
    }

    public static String[] links() {
        return new String[] {
                "Members (Persons)|com.storedobject.core.Person|||0",
                "Members (System Groups)|com.storedobject.core.SystemUserGroup|||0",
                "Escalation|com.storedobject.job.MessageEscalation|||0",
        };
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(order = 1, style = "(code)")
    public String getName() {
        return name;
    }

    public void setTemplate(Id templateId) {
        this.templateId = templateId;
    }

    public void setTemplate(BigDecimal idValue) {
        setTemplate(new Id(idValue));
    }

    public void setTemplate(MessageTemplate template) {
        setTemplate(template == null ? null : template.getId());
    }

    @Column(order = 2)
    public Id getTemplateId() {
        return templateId;
    }

    public MessageTemplate getTemplate() {
        return get(MessageTemplate.class, templateId);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        name = name(name);
        templateId = tm.checkType(this, templateId, MessageTemplate.class, false);
        super.validateData(tm);
    }

    /**
     * Create a new message group if it doesn't exist.
     *
     * @param name Name of the group.
     * @param tm Transaction manager.
     * @return Message group instance.
     */
    public static MessageGroup create(String name, TransactionManager tm) {
        name = name(name);
        MessageGroup mg = getFor(name);
        if(mg != null) {
            return mg;
        }
        mg = new MessageGroup();
        mg.setName(name);
        MessageTemplate mt = MessageTemplate.create(name, tm);
        mg.setTemplate(mt);
        try {
            if(tm.transact(mg::save) == 0) {
                return mg;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static String name(String name) {
        return toCode(name).replace('-', '_');
    }

    private void escalate(List<Id> messageIds, TransactionControl tc) {
        ArrayList<LoginMessage> ms = new ArrayList<>();
        LoginMessage m;
        for(Id id: messageIds) {
            m = get(LoginMessage.class, id);
            if(m != null) {
                ms.add(m);
            }
        }
        if(!ms.isEmpty()) {
            escalate(ms, tc);
        }
    }

    private void escalate(ArrayList<LoginMessage> messages, TransactionControl tc) {
        ArrayList<LoginMessage> created = new ArrayList<>();
        ObjectIterator<Person> persons = null;
        try(ObjectIterator<MessageEscalation> list = listLinks(MessageEscalation.class, "Days>0", "Days")) {
            int days = 0;
            for(MessageEscalation me: list) {
                if(days == 0) {
                    persons = me.getEscalateTo().listMembers();
                    days = me.getDays();
                } else if(days == me.getDays()) {
                    persons.add(me.getEscalateTo().listMembers());
                } else {
                    escalate(messages, persons, tc, days, created);
                    days = 0;
                    messages = created;
                    created = new ArrayList<>();
                }
            }
            if(days> 0) {
                escalate(messages, persons, tc, days, created);
            }
        }
        if(persons != null) {
            persons.close();
        }
    }

    private void escalate(ArrayList<LoginMessage> messages, ObjectIterator<Person> persons, TransactionControl tc,
                          int days, ArrayList<LoginMessage> collect) {
        LoginMessage nm;
        for(LoginMessage m: messages) {
            nm = m.escalate(tc, persons, days);
            persons.close();
            if(nm == null) {
                return;
            }
            collect.add(nm);
        }
    }

    public ObjectIterator<Person> listMembers() {
        return listMembers(null);
    }

    private ObjectIterator<Person> listMembers(Person person) {
        ArrayListSet<Person> m = new ArrayListSet<>();
        if(person != null) {
            m.add(person);
        }
        listLinks(Person.class).collectAll(m);
        for(SystemUserGroup g: listLinks(SystemUserGroup.class)) {
            for(SystemUser su: g.listUsers()) {
                m.add(su.getPerson());
            }
        }
        return ObjectIterator.create(m);
    }

    /**
     * Get the list of contacts belonging to this message group.
     *
     * @param contactType Type of contact (0: SMS, 1: Email, 2: Application)
     * @param <P> Contact role type.
     * @return List.
     */
    public <P extends PersonRole> List<Contact> listContacts(int contactType) {
        List<MessageTemplate> templates = getTemplate().listAll(contactType);
        ArrayListSet<Contact> list = new ArrayListSet<>();
        if(templates.isEmpty()) {
            return list;
        }
        listMembers().forEach(p -> {
            for(MessageTemplate mt: templates) {
                ContactType ct = mt.getContactType();
                Class<? extends HasContacts> contactClass = mt.getContactType().getContactClass();
                Contact c;
                if(PersonRole.class.isAssignableFrom(contactClass)) {
                    //noinspection unchecked
                    P pr = get((Class<P>)contactClass, "Person=" + p.getId(), true);
                    if(pr != null) {
                        c = pr.getContactObject(ct);
                        if(c != null) {
                            list.add(c);
                        }
                    }
                } else {
                    c = p.getContactObject(ct);
                    if(c != null) {
                        list.add(c);
                    }
                }
            }
        });
        return list;
    }

    public List<SystemUser> listUsers() {
        ArrayListSet<SystemUser> sus = new ArrayListSet<>();
        listLinks(Person.class).forEach(p -> list(SystemUser.class, "Person=" + p.getId()).collectAll(sus));
        for(SystemUserGroup g: listLinks(SystemUserGroup.class)) {
            g.listUsers().collectAll(sus);
        }
        return sus;
    }

    /**
     * Create and send a message to all members of this group.
     * @param tm Transaction Manager.
     * @param messageParameters Parameters for creating message from the associated template.
     * @throws Throwable If message can not be created.
     */
    public void send(TransactionManager tm, Object... messageParameters) throws Throwable {
        send((Person)null, tm, messageParameters);
    }

    /**
     * Create and send a message to all members of this group and to an additional person.
     * @param person Additional person to receive the message
     * @param tm Transaction Manager.
     * @param messageParameters Parameters for creating message from the associated template.
     * @throws Throwable If message can not be created.
     */
    public void send(Person person, TransactionManager tm, Object... messageParameters) throws Throwable {
        TransactionControl tc;
        send(person, tc = new TransactionControl(tm), messageParameters);
        if(tc.isError()) {
            tc.throwError();
        } else {
            tc.commit();
        }
    }

    /**
     * Create and send a message to all members of this group.
     * @param tc Transaction Control.
     * @param messageParameters Parameters for creating message from the associated template.
     * @throws Throwable If message can not be created.
     */

    public void send(TransactionControl tc, Object... messageParameters) throws Throwable {
        send((Person)null, tc, messageParameters);
    }

    /**
     * Create and send a message to all members of this group and to an additional person.
     * @param person Additional person to receive the message
     * @param tc Transaction Control.
     * @param messageParameters Parameters for creating message from the associated template.
     * @throws Throwable If message can not be created.
     */
    public void send(Person person, TransactionControl tc, Object... messageParameters) throws Throwable {
        MessageTemplate messageTemplate = getTemplate();
        if(messageTemplate == null) {
            return;
        }
        List<Id> ms = MessageTemplate.send(messageTemplate.getCode(), tc, listMembers(person), messageParameters);
        escalate(ms, tc);
    }

    /**
     * Create and send a message to all members of this group. No message will be sent if the message
     * group does not exist.
     * @param groupName Name of the message group.
     * @param tm Transaction Manager.
     * @param messageParameters Parameters for creating message from the associated template.
     * @throws Throwable If message can not be created.
     */
    public static void send(String groupName, TransactionManager tm, Object... messageParameters) throws Throwable {
        send(groupName, null, tm, messageParameters);
    }

    /**
     * Create and send a message to all members of this group. No message will be sent if the message
     * group does not exist.
     * @param groupName Name of the message group.
     * @param tc Transaction Control.
     * @param messageParameters Parameters for creating message from the associated template.
     * @throws Throwable If message can not be created.
     */
    public static void send(String groupName, TransactionControl tc, Object... messageParameters) throws Throwable {
        send(groupName, null, tc, messageParameters);
    }

    /**
     * Create and send a message to all members of this group and to an additional person. No message will be sent if the message
     * group does not exist.
     * @param groupName Name of the message group.
     * @param person Additional person to receive the message
     * @param tm Transaction Manager.
     * @param messageParameters Parameters for creating message from the associated template.
     * @throws Throwable If message can not be created.
     */
    public static void send(String groupName, Person person, TransactionManager tm, Object... messageParameters) throws Throwable {
        MessageGroup mg = get(groupName);
        if(mg == null) {
            throw new NOT_FOUND(groupName);
        } else {
            mg.send(person, tm, messageParameters);
        }
    }

    /**
     * Create and send a message to all members of this group and to an additional person. No message will be sent
     * if the message group does not exist.
     * @param groupName Name of the message group.
     * @param person Additional person to receive the message (Could be null).
     * @param tc Transaction Control.
     * @param messageParameters Parameters for creating message from the associated template.
     * @throws Throwable If message can not be created.
     */
    public static void send(String groupName, Person person, TransactionControl tc, Object... messageParameters)
            throws Throwable {
        MessageGroup mg = get(groupName);
        if(mg == null) {
            throw new NOT_FOUND(groupName);
        } else {
            mg.send(person, tc, messageParameters);
        }
    }

    public static class NOT_FOUND extends RuntimeException implements EndUserMessage {

        public NOT_FOUND(String name) {
            super(name);
        }

        @Override
        public String getEndUserMessage() {
            return "Message Group '" + getMessage() + "' not found";
        }
    }

    @Override
    public String toString() {
        MessageTemplate mt = getTemplate();
        return name + (mt.getCode().equals(name) ? "" : (" (" + mt.getCode() + ")"));
    }

    /**
     * Create and send a message to all members of this group.
     * <p>Note: If the message group does not exist a new one will be created.</p>
     * @param groupName Name of the message group.
     * @param tm Transaction manager.
     * @param messageParameters Parameters for creating message from the associated template.
     * @return True if the message is successfully created for delivery.
     */
    public static boolean notify(String groupName, TransactionManager tm, Object... messageParameters) {
        try {
            MessageGroup mg = create(groupName, tm);
            if(mg != null) {
                mg.send(tm, messageParameters);
                return true;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }
}
