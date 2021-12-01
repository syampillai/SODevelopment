package com.storedobject.ui;

import com.storedobject.common.ConvertedIterable;
import com.storedobject.common.Email;
import com.storedobject.common.SOException;
import com.storedobject.core.*;
import com.storedobject.mail.Mail;
import com.storedobject.mail.Sender;
import com.storedobject.mail.SenderGroup;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class MailForm extends DataForm implements Transactional {

    private ObjectField<SenderGroup> senderField;
    private TokensField<Address> addressField, ccField;
    private TextField subjectField;
    private TextArea contentField;
    private Attachments attachments;
    private boolean allowAttachments = true;
    private SenderGroup senderGroup;

    public MailForm() {
        this("Mail");
    }

    public MailForm(String caption) {
        super(caption, "Send", "Cancel", false);
        setButtonsAtTop(true);
        setScrollable(true);
        setColumns(1);
    }

    public void setAllowAttachments(boolean allow) {
        allowAttachments = allow;
        if(allow) {
            if(attachments == null) {
                attachments = new Attachments();
                addField(attachments);
            }
        } else {
            if(attachments != null) {
                remove(attachments);
                attachments = null;
            }
        }
    }

    @Override
    protected void buildFields() {
        String userEmail = getTransactionManager().getUser().getPerson().getContact("email");
        senderField = new ObjectField<>("From", SenderGroup.class, ObjectField.Type.CHOICE);
        senderField.setValue((Id)null);
        senderField.setFilter(g -> {
            for(Sender s: StoredObject.list(Sender.class, "SenderGroup=" + g.getId(), true)) {
                if(s.getFromAddress().equalsIgnoreCase(userEmail) || s.getReplyToAddress().equalsIgnoreCase(userEmail)) {
                    senderGroup = g;
                    return true;
                }
            }
            return false;
        });
        addField(senderField);
        if(senderGroup != null) {
            senderField.setValue(senderGroup);
            senderGroup = null;
        }
        addressField = new TokensField<>("To");
        addField(addressField);
        ccField = new TokensField<>("CC");
        addField(ccField);
        subjectField = new TextField("Subject");
        addField(subjectField);
        setAllowAttachments(allowAttachments);
        contentField = new TextArea("Content");
        addField(contentField);
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        ok.setIcon(new Icon(VaadinIcon.ENVELOPE_O));
    }

    private static String emails(TokensField<Address> field) {
        Set<Address> as = field.getValue();
        StringBuilder s = new StringBuilder();
        as.forEach(a -> {
            if(s.length() > 0) {
                s.append(",");
            }
            s.append(a.email);
        });
        return s.toString();
    }

    @Override
    protected boolean process() {
        String s = emails(addressField);
        if(s.isEmpty()) {
            warning("Please select the address to send to");
            return false;
        }
        Mail m = new Mail();
        SenderGroup sg = getSenderGroup();
        if(sg != null) {
            m.setSenderGroup(sg);
        } else {
            m.setSenderGroup(senderField.getObject());
        }
        m.setToAddress(s);
        s = emails(ccField);
        if(!s.isEmpty()) {
            m.setCCAddress(s);
        }
        m.setSubject(subjectField.getValue());
        m.setMessage(contentField.getValue());
        FileData[] a = null;
        if(attachments != null) {
            try {
                a = attachments.files();
            } catch (SOException e) {
                warning(e);
                return false;
            }
        }
        if(a == null) {
            if(!transact(m::save)) {
                return false;
            }
        } else {
            if(!m.attach(getTransactionManager(), a)) {
                error("Error while attaching files");
                return false;
            }
        }
        try {
            m.reload();
            addOtherAttachments(m);
            m.ready();
        } catch (Throwable e) {
            error(e);
            return false;
        }
        transact(m::save);
        warning("Mail created successfully for sending...");
        return true;
    }

    public void addOtherAttachments(@SuppressWarnings("unused") Mail mail) {
    }

    public <T extends StoredObject> void setAllowedAddresses(Iterable<T> persons) {
        addressField.setItems(toSet(persons));
    }

    public <T extends StoredObject> void addAllowedAddresses(Iterable<T> persons) {
        addressField.addItems(toSet(persons));
    }

    public <T extends StoredObject> void setAllowedAddresses(Class<T> personClass) {
        setAllowedAddresses(StoredObject.list(personClass));
    }

    public <T extends StoredObject> void addAllowedAddresses(Class<T> personClass) {
        addAllowedAddresses(StoredObject.list(personClass));
    }

    public <T extends StoredObject> void setAddress(Iterable<T> persons) {
        addressField.setValue(toSet(persons));
    }

    public <T extends StoredObject> void addAddress(Iterable<T> persons) {
        addressField.addValue(toSet(persons));
    }

    public <T extends StoredObject> void setAddress(Class<T> personClass) {
        setAddress(StoredObject.list(personClass));
    }

    public <T extends StoredObject> void addAddress(Class<T> personClass) {
        addAddress(StoredObject.list(personClass));
    }

    public void setAddress(Person person) {
        setAddress(ObjectIterator.create(person));
    }

    public void addAddress(Person person) {
        addAddress(ObjectIterator.create(person));
    }

    public void setAddress(PersonRole person) {
        setAddress(ObjectIterator.create(person));
    }

    public void addAddress(PersonRole person) {
        addAddress(ObjectIterator.create(person));
    }

    public void setAllowedAddress(String email) {
        addressField.setItems(toSet(email));
    }

    public void addAllowedAddress(String email) {
        addressField.addItems(toSet(email));
    }

    public void setAddress(String email) {
        addressField.setValue(toSet(email));
    }

    public void addAddress(String email) {
        addressField.addValue(toSet(email));
    }

    public <T extends StoredObject> void setAllowedCCs(Iterable<T> persons) {
        ccField.setItems(toSet(persons));
    }

    public <T extends StoredObject> void addAllowedCCs(Iterable<T> persons) {
        ccField.addItems(toSet(persons));
    }

    public <T extends StoredObject> void setAllowedCCs(Class<T> personClass) {
        setAllowedCCs(StoredObject.list(personClass));
    }

    public <T extends StoredObject> void addAllowedCCs(Class<T> personClass) {
        addAllowedCCs(StoredObject.list(personClass));
    }

    public <T extends StoredObject> void setCC(Iterable<T> persons) {
        ccField.setValue(toSet(persons));
    }

    public <T extends StoredObject> void addCC(Iterable<T> persons) {
        ccField.addValue(toSet(persons));
    }

    public <T extends StoredObject> void setCC(Class<T> personClass) {
        setCC(StoredObject.list(personClass));
    }

    public <T extends StoredObject> void addCC(Class<T> personClass) {
        addCC(StoredObject.list(personClass));
    }

    public void setCC(Person person) {
        setCC(ObjectIterator.create(person));
    }

    public void addCC(Person person) {
        addCC(ObjectIterator.create(person));
    }

    public void setCC(PersonRole person) {
        setCC(ObjectIterator.create(person));
    }

    public void addCC(PersonRole person) {
        addCC(ObjectIterator.create(person));
    }

    public void setAllowedCC(String email) {
        ccField.setItems(toSet(email));
    }

    public void addAllowedCC(String email) {
        ccField.addItems(toSet(email));
    }

    public void setCC(String email) {
        ccField.setValue(toSet(email));
    }

    public void addCC(String email) {
        ccField.addValue(toSet(email));
    }

    public void setSubject(String subject) {
        subjectField.setValue(subject);
    }

    public void setContent(String content) {
        contentField.setValue(content);
    }

    public SenderGroup getSenderGroup() {
        return senderGroup;
    }

    public void setSenderGroup(SenderGroup senderGroup) {
        senderField.setVisible(senderGroup == null);
        this.senderGroup = senderGroup;
    }

    private <T extends StoredObject> Set<Address> toSet(Iterable<T> persons) {
        HashSet<Address> set = new HashSet<>();
        new ConvertedIterable<>(persons, new SOtoAddress<>()).forEach(a -> {
            if(a != null) {
                set.add(a);
            }
        });
        return set;
    }

    private Set<Address> toSet(String email) {
        Address a = new Address(email);
        HashSet<Address> set = new HashSet<>();
        if(a.isValid()) {
            set.add(a);
        }
        return set;
    }

    private static class SOtoAddress<T extends StoredObject> implements Function<T, Address> {

        @Override
        public Address apply(T so) {
            if(so != null) {
                Address a;
                if(so instanceof Person) {
                    a = new Address((Person) so);
                } else if(so instanceof PersonRole) {
                    a = new Address((PersonRole) so);
                } else if(so instanceof Contact) {
                    a = new Address((Contact) so);
                } else {
                    a = new Address(so);
                }
                if(a.isValid()) {
                    return a;
                }
            }
            return null;
        }
    }

    public static class Address {

        private String name;
        private String email;

        private Address(Person p) {
            this.email = p == null ? null : p.getContact("email");
            if(p != null) {
                this.name = p.getFirstName() + " " + p.getLastName();
            }
        }

        private Address(PersonRole p) {
            this(p.getPerson());
        }


        private Address(Contact contact) {
            this(contact.getValue());
        }

        private Address(StoredObject so) {
            if(so == null) {
                return;
            }
            Contact a = so.listLinks(Contact.class, true).filter(c -> c.getType().getName().trim().equalsIgnoreCase("email")).findFirst();
            if(a != null) {
                this.email = a.getValue();
                this.name = so.toDisplay();
            }
        }

        private Address(String email) {
            this.email = email;
        }

        public String toString() {
            return name == null ? email : name;
        }

        public boolean isValid() {
            if(email == null) {
                return false;
            }
            try {
                Email.check(email);
                return true;
            } catch (Exception ignore) {
            }
            return false;
        }
    }

    private static class Attachments extends CompoundField implements ClickHandler {

        private final VerticalLayout layout;
        private final ImageButton add;

        private Attachments() {
            super("Attachments");
            layout = new VerticalLayout();
            add = new ImageButton("Add", VaadinIcon.PLUS_CIRCLE, this);
            layout.add(add);
            add(layout);
        }

        @Override
        public void clicked(Component c) {
            if(c == add) {
                layout.remove(add);
                layout.add(new Attachment());
                layout.add(add);
            }
        }

        public FileData[] files() throws SOException {
            ArrayList<FileData> files = new ArrayList<>();
            ArrayList<SOException> errors = new ArrayList<>();
            layout.getChildren().forEach(c -> {
                try {
                    if(c instanceof Attachment) {
                        ((Attachment) c).check();
                        files.add(((Attachment)c).getFile());
                    }
                } catch (SOException e) {
                    errors.add(e);
                }
            });
            if(!errors.isEmpty()) {
                throw errors.get(0);
            }
            if(files.isEmpty()) {
                return null;
            }
            return files.toArray(new FileData[0]);
        }
    }

    private static class Attachment extends HorizontalLayout implements ClickHandler {

        private final ImageButton remove;
        private final FileField upload;

        private Attachment() {
            remove = new ImageButton("Remove", VaadinIcon.MINUS_CIRCLE, this);
            add(remove);
            add(new ELabel("  "));
            upload = new FileField();
            upload.setRequired(false);
            upload.disallowLinking();
            add(upload);
        }

        public FileData getFile() {
            FileData fd = new FileData();
            fd.setName(upload.getFileName());
            fd.setFile(upload.getValue());
            return fd;
        }

        @Override
        public void clicked(Component c) {
            if(c == remove) {
                Optional<Component> parent = getParent();
                if(parent.isPresent() && parent.get() instanceof HasComponents) {
                    ((HasComponents)parent.get()).remove(this);
                }
            }
        }

        public void check() throws SOException {
            if(upload.getValue() == null) {
                throw new SOException("Attachment not uploaded");
            }
        }
    }
}
