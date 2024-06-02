package com.storedobject.core;

import com.storedobject.common.Email;
import com.storedobject.common.IO;
import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;
import com.storedobject.job.MessageSender;
import com.storedobject.mail.Mail;
import com.storedobject.mail.SenderGroup;
import com.storedobject.sms.SMSMessage;
import com.storedobject.telegram.Telegram;

import java.math.BigDecimal;
import java.util.*;

public final class MessageTemplate extends StoredObject {

    private static MessageTemplate defaultTemplate;
    private static final String[] deliveryValues = new String[] {
            "SMS",
            "Email",
            "Application",
            "Other",
            "Telegram",
    };
    private String code;
    private Id contactTypeId;
    private String template;
    private int delivery = 0;

    public MessageTemplate() {
    }

    public static void columns(Columns columns) {
        columns.add("Code", "text");
        columns.add("ContactType", "id");
        columns.add("Template", "text");
        columns.add("Delivery", "int");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Code),Delivery", true);
    }

    @Override
    public String getUniqueCondition() {
        return "lower(Code)='" + getCode().trim().toLowerCase().replace("'", "''")
                + "' AND Delivery=" + delivery;
    }

    public void setCode(String code) {
        if(!loading()) {
            throw new Set_Not_Allowed("Code");
        }
        this.code = code;
    }

    @SetNotAllowed
    @Column(style = "(code)", order = 1)
    public String getCode() {
        return code;
    }

    public void setContactType(Id contactTypeId) {
        this.contactTypeId = contactTypeId;
    }

    public void setContactType(BigDecimal idValue) {
        setContactType(new Id(idValue));
    }

    public void setContactType(ContactType contactType) {
        setContactType(contactType == null ? null : contactType.getId());
    }

    @Column(order = 2)
    public Id getContactTypeId() {
        return contactTypeId;
    }

    public ContactType getContactType() {
        return get(ContactType.class, contactTypeId);
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @Column(style = "(large)", order = 3)
    public String getTemplate() {
        return template;
    }

    public void setDelivery(int delivery) {
        this.delivery = delivery;
    }

    @Column(order = 300)
    public int getDelivery() {
        return delivery;
    }

    public static String[] getDeliveryValues() {
        return deliveryValues;
    }

    public static String getDeliveryValue(int value) {
        String[] s = getDeliveryValues();
        return s[value % s.length];
    }

    public String getDeliveryValue() {
        return getDeliveryValue(delivery);
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        if(delivery == 3) {
            throw new Invalid_Value("Unsupported delivery type");
        }
        if(StringUtility.isWhite(code)) {
            throw new Invalid_Value("Code");
        }
        code = name(code);
        contactTypeId = tm.checkType(this, contactTypeId, ContactType.class, false);
        if(StringUtility.isWhite(template)) {
            throw new Invalid_Value("Template");
        }
        if(delivery != 2) {
            ContactType ct = getContactType();
            if(delivery != ct.getType()) {
                throw new Invalid_State("Contact type (" + ct.getTypeValue() + ") doesn't match with delivery type (" +
                        getDeliveryValue() + ")");
            }
        }
        super.validateData(tm);
    }

    private static String name(String name) {
        return toCode(name).replace('-', '_');
    }

    static String toString(Object parameter) {
        if(parameter == null) {
            return "";
        }
        if(parameter instanceof Date) {
            return DateUtility.format((Date)parameter);
        }
        return parameter.toString();
    }

    @Override
    public String toString() {
        return code;
    }

    public Contact getContact(Person person) {
        Contact c = person.listLinks(Contact.class, "Type=" + contactTypeId).single(false);
        if(c == null) {
            try(ObjectIterator<PersonRole> roles
                        = list(PersonRole.class, "Person=" + person.getId(), true)) {
                for(PersonRole role: roles) {
                    c = role.listLinks(Contact.class, "Type=" + contactTypeId).single(false);
                    if(c != null) {
                        break;
                    }
                }
            }
        }
        return c;
    }

    public static MessageTemplate getFor(String code) {
        code = name(code);
        String finalCode = code;
        return list(code).filter(t -> t.code.equals(finalCode)).findFirst();
    }

    public static MessageTemplate get(String code) {
        code = name(code);
        code = code.replace("'", "''").toLowerCase();
        MessageTemplate m = get(MessageTemplate.class, "lower(Code)='" + code + "'");
        return m == null ? list(code).single(false) : m;
    }

    public static ObjectIterator<MessageTemplate> list(String code) {
        code = name(code);
        code = code.replace("'", "''").toLowerCase();
        if(code.isEmpty()) {
            return ObjectIterator.create();
        }
        return list(MessageTemplate.class, "lower(Code) LIKE '" + code + "%'");
    }

    public String createMessage(Object... parameters) {
        String s = template, index;
        int i = 1;
        for(Object p: parameters) {
            index = "<" + i + ">";
            if(s.contains(index)) {
                s = s.replace(index, toString(p));
            }
            ++i;
        }
        return s;
    }

    public String createSubject(Object... parameters) {
        for(int i = 0; i < parameters.length; i++) {
            if(template.contains("<" + (i + 1) + ">")) {
                continue;
            }
            if(parameters[i] instanceof String p && p.toLowerCase().startsWith("subject:")) {
                return p.substring("subject:".length()).strip();
            }
        }
        return "-";
    }

    public String createEmailAddress(Object... parameters) {
        for(int i = 0; i < parameters.length; i++) {
            if(template.contains("<" + (i + 1) + ">")) {
                continue;
            }
            if(parameters[i] instanceof String p) {
                try {
                    Email.check(p);
                    return p;
                } catch (Exception ignored) {
                }
            }
        }
        return "";
    }

    public Class<?> createProcessorLogic(Object... parameters) {
        for(int i = 0; i < parameters.length; i++) {
            if(template.contains("<" + (i + 1) + ">")) {
                continue;
            }
            if(parameters[i] instanceof Class) {
                return (Class<?>)parameters[i];
            }
        }
        return null;
    }

    public StoredObject createGeneratedBy(Object... parameters) {
        StoredObject so = null;
        for(int i = 0; i < parameters.length; i++) {
            if(template.contains("<" + (i + 1) + ">")) {
                continue;
            }
            if(parameters[i] instanceof Id) {
                so = get((Id)parameters[i]);
            } else if(parameters[i] instanceof StoredObject) {
                so = (StoredObject)parameters[i];
            }
            if(so instanceof SenderGroup || so instanceof FileData || so instanceof StreamData) {
                so = null;
            }
            if(so != null) {
                return so;
            }
        }
        return null;
    }

    public SenderGroup createSenderGroup(Object... parameters) {
        SenderGroup sg = null;
        for(int i = 0; i < parameters.length; i++) {
            if(template.contains("<" + (i + 1) + ">")) {
                continue;
            }
            if(parameters[i] instanceof Id id) {
                sg = get(SenderGroup.class, id);
            } else if(parameters[i] instanceof SenderGroup) {
                sg = (SenderGroup) parameters[i];
            }
            if(sg != null) {
                return sg;
            }
        }
        return null;
    }

    public int createValidity(Object... parameters) {
        for(int i = 0; i < parameters.length; i++) {
            if(template.contains("<" + (i + 1) + ">")) {
                continue;
            }
            if(parameters[i] instanceof Integer) {
                return ((Integer)parameters[i]);
            }
        }
        return 7;
    }

    public List<MessageTemplate> listAll(int deliveryType) {
        return listAll(code, deliveryType);
    }

    public List<MessageTemplate> listAll() {
        return listAll(code, -1);
    }

    public static List<MessageTemplate> listAll(String templateName) {
        return listAll(templateName, -1);
    }

    public static List<MessageTemplate> listAll(String templateName, int deliveryType) {
        String c = "lower(Code)='" + name(templateName).toLowerCase().replace("'", "''") + "'";
        if(deliveryType >= 0) {
            c += " AND Delivery=" + deliveryType;
        }
        return list(MessageTemplate.class, c).toList();
    }

    public static List<Id> send(String templateName, TransactionControl tc, Person person,
                                Object... messageParameters) throws Throwable {
        return send(templateName, tc, ObjectIterator.create(person), messageParameters);
    }

    public static List<Id> send(String templateName, TransactionControl tc, Iterable<Person> persons,
                                Object... messageParameters) throws Throwable {
        List<Id> mIds = new ArrayList<>();
        boolean any;
        Map<Id, LoginMessage> mm = new HashMap<>();
        LoginMessage m;
        templateName = name(templateName);
        List<MessageTemplate> templates = listAll(templateName);
        List<Message> toAttach = new ArrayList<>();
        try {
            if(templates.isEmpty()) {
                throw new SOException("No such template - " + templateName);
            }
            for(Person person : persons) {
                any = false;
                StringBuilder channels = new StringBuilder();
                for(MessageTemplate template : templates) {
                    if(template.delivery == 2) { // Application
                        m = mm.get(template.getId());
                        if(m == null) {
                            m = LoginMessage.alert(tc.getTransaction(), template.createMessage(messageParameters),
                                    person, template.createProcessorLogic(messageParameters),
                                    template.createGeneratedBy(messageParameters), 7);
                            mm.put(template.getId(), m);
                            mIds.add(m.getId());
                        } else {
                            m.addPersons(tc.getTransaction(), ObjectIterator.create(person));
                        }
                        tc.commit();
                        any = true;
                    } else {
                        if(template.sendOne(tc, person, toAttach, messageParameters)) {
                            any = true;
                        } else {
                            if(!channels.isEmpty()) {
                                channels.append(", ");
                            }
                            channels.append(template.getDeliveryValue());
                        }
                    }
                }
                if(tc.isError()) {
                    tc.getManager().log(tc.getError());
                    tc.clear();
                }
                if(!any) {
                    tc.getManager().log("No message sent to " + person + " for Template " + templateName
                            + " (Channels: " + channels + ")");
                }
            }
        } finally {
            if(persons instanceof AutoCloseable) {
                IO.close((AutoCloseable) persons);
            }
            any = false;
            Mail first = null;
            for(Message message: toAttach) {
                if(!(message instanceof Mail mail)) {
                    continue;
                }
                if(first != null) {
                    if(any) {
                        mail.attachFrom(tc.getManager(), first);
                    }
                    mail.ready();
                    tc.getManager().transact(mail::save);
                    continue;
                }
                List<FileData> files = new ArrayList<>();
                for(Object o: messageParameters) {
                    if(o instanceof FileData fd) {
                        files.add(fd);
                    } else if (o instanceof Id id) {
                        FileData fd = StoredObject.get(FileData.class, id, true);
                        if(fd != null) {
                            files.add(fd);
                        }
                    }
                }
                if(!files.isEmpty()) {
                    mail.attach(tc.getManager(), files);
                }
                for(Object o: messageParameters) {
                    if(o instanceof ContentProducer cp) {
                        mail.attach(tc.getManager(), cp);
                        any = true;
                    }
                }
                if(!any) {
                    any = !files.isEmpty();
                }
                mail.ready();
                tc.getManager().transact(mail::save);
                first = mail;
            }
            MessageSender.kick();
        }
        return mIds;
    }

    private boolean sendOne(TransactionControl tc, Person person, List<Message> toAttach, Object... messageParameters)
            throws Throwable {
        try {
            if(person == null) {
                tc.rollback("Person not specified");
                return false;
            }
            Message message;
            switch(delivery) {
                case 0 -> // SMS
                    message = new SMSMessage();
                case 1 -> // Email
                    message = Mail.createAlert();
                case 2 -> { // Application
                    LoginMessage.alert(tc.getTransaction(), createMessage(messageParameters), person,
                            createProcessorLogic(messageParameters), createGeneratedBy(messageParameters),
                            createValidity(messageParameters));
                    return true;
                }
                case 4 -> // Telegram
                    message = new Telegram();
                default -> {
                    tc.rollback("Don't know how to handle messages of type '" + getDeliveryValue() + "'");
                    return false;
                }
            }
            Contact contact = getContact(person);
            if(contact == null) {
                return false;
            }
            StringBuilder m = new StringBuilder();
            for(Object p: messageParameters) {
                if(!m.isEmpty()) {
                    m.append('|');
                }
                m.append(MessageTemplate.toString(p));
            }
            MessageStore ms = new MessageStore();
            ms.setPerson(person);
            ms.setContact(contact);
            ms.setCode(getCode());
            ms.setMessage(m.toString());
            ms.validateData(tc.getManager());
            if(exists(tc.useTransaction(), MessageStore.class, ms.getUniqueCondition())) {
                return true;
            }
            if(!tc.save(ms)) {
                tc.rollback("Error creating Message Store - " + StringUtility.toString(tc.getError()));
            }
            message.setMessage(createMessage(messageParameters));
            switch(delivery) {
                case 0 -> { // SMS
                    long mobile = 0;
                    try {
                        mobile = Long.parseLong(contact.getContactValue().replace(" ", "").trim());
                    } catch(Throwable error) {
                        tc.rollback("Invalid mobile number '" + contact.getValue() +
                                "' configured to send SMS for " + person.toDisplay());
                    }
                    //noinspection ConstantConditions
                    ((SMSMessage) message).setMobileNumber(mobile);
                }
                case 1 -> { // Email
                    @SuppressWarnings("ConstantConditions") Mail mail = (Mail) message;
                    mail.setToAddress(contact.getContactValue());
                    mail.setSubject(createSubject(messageParameters));
                    mail.setReplyToAddress(createEmailAddress(messageParameters));
                    SenderGroup sg = createSenderGroup(messageParameters);
                    if(sg != null) {
                        mail.setSenderGroup(sg);
                    }
                    toAttach.add(mail);
                }
                case 4 -> { // Telegram
                    String tn = contact.getContactValue();
                    if("Telegram".equals(tn)) {
                        return false;
                    }
                    int p = tn.indexOf('/');
                    if(p > 0) {
                        tn = tn.substring(0, p);
                        if(!StringUtility.isDigit(tn)) {
                            p = -1;
                        }
                    }
                    if(p <= 0) {
                        tc.rollback("Invalid telegram contact for " + person.toDisplay());
                    }
                    //noinspection DataFlowIssue
                    ((Telegram)message).setTelegramNumber(Long.parseLong(tn));
                }
            }
            if(!message.save(tc) || !tc.commit()) {
                tc.throwError();
            }
            return true;
        } catch(Throwable error) {
            tc.rollback();
            throw error;
        }
    }

    /**
     * Create and send messages to the given list of persons.
     * <p>Note: If the template doesn't exist, the default template is used.</p>
     * @param templateName Template name.
     * @param tm Transaction manager.
     * @param persons List of persons.
     * @param messageParameters Parameters for creating message from the associated template.
     * @return True if the message is successfully created for delivery.
     */
    public static boolean notify(String templateName, TransactionManager tm, Iterable<Person> persons,
                                Object... messageParameters) {
        TransactionControl tc = new TransactionControl(tm);
        try {
            templateName = name(templateName);
            MessageTemplate mt = MessageTemplate.getFor(templateName);
            if(mt == null) {
                templateName = createDefault(tm).code;
            }
            send(templateName, tc, persons, messageParameters);
        } catch (Throwable ignored) {
        }
        return tc.commit();
    }

    /**
     * Create the default template if it doesn't exist.
     * @param tm Transaction manager
     * @return Default template.
     */
    public static MessageTemplate createDefault(TransactionManager tm) {
        if(defaultTemplate == null) {
            defaultTemplate = create("DEFAULT", tm);
        }
        return defaultTemplate;
    }

    /**
     * Create a template if it doesn't exist.
     * @param tm Transaction manager
     * @return The template that is created or retrieved.
     */
    public static MessageTemplate create(String name, TransactionManager tm) {
        name = name.trim();
        MessageTemplate t = getFor(name);
        if(t != null) {
            return t;
        }
        MessageTemplate mt = new MessageTemplate();
        mt.setCode(name);
        mt.setDelivery(2);
        mt.setTemplate("<1>");
        mt.setContactType(ContactType.createForTelegram(tm));
        try {
            tm.transact(mt::save);
            return mt;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    void savedCore() throws Exception {
        defaultTemplate = null;
    }
}
