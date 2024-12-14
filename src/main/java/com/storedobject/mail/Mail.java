package com.storedobject.mail;

import com.storedobject.common.Array;
import com.storedobject.common.Email;
import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.job.MessageGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

public class Mail extends Message {

	static final String CHAR_SET_TAG = ";charset=UTF-8";
	static final String PLAIN_TEXT = "text/plain" + CHAR_SET_TAG;
	private static final String[] errorValues = new String[] {
		"None",
		"Error - Retry",
		"N/A",
		"Draft",
		"Invalid Address",
	};
	private Id senderId = Id.ZERO;
	private Id senderGroupId;
    private String toAddress;
    private String cCAddress;
    private String replyToAddress;
    private String subject;
    private String messageType = PLAIN_TEXT;

	public Mail() {
		error = 3;
	}

	public static void columns(Columns columns) {
		columns.add("SenderGroup", "Id");
        columns.add("ToAddress", "text");
        columns.add("CCAddress", "text");
        columns.add("ReplyToAddress", "text");
        columns.add("Subject", "text");
        columns.add("MessageType", "text");
		columns.add("Sender", "Id");
	}

	public static void indices(Indices indices) {
		indices.add("ToAddress,CreatedAt", false);
		indices.add("ToAddress,Sent", "NOT Sent", false);
		indices.add("CreatedAt,Sent", "NOT Sent", false);
	}

    public static String[] links() {
        return new String[] {
            "Attachments|com.storedobject.mail.Attachment||",
            "Errors|com.storedobject.mail.Error||",
        };
    }
    
    public static boolean customizeMetadata(String fieldName, UIFieldMetadata md) {
        if(fieldName.equals("Error")) {
            md.setCaption("Status");
            return true;
        }
        return false;
    }
    
    public void setSenderGroup(String senderGroupName) {
    	setSenderGroup(SenderGroup.get(senderGroupName));
    }
    
    public void setSenderGroup(Id senderGroupId) {
        this.senderGroupId = senderGroupId;
    }

    @Column
    public void setSenderGroup(BigDecimal idValue) {
        setSenderGroup(new Id(idValue));
    }

    public void setSenderGroup(SenderGroup senderGroup) {
        setSenderGroup(senderGroup == null ? null : senderGroup.getId());
    }

    public Id getSenderGroupId() {
        return senderGroupId;
    }

    public SenderGroup getSenderGroup() {
    	return get(SenderGroup.class, senderGroupId);
    }

	public String getFromAddress() {
		Sender sender = getSender();
		return sender == null ? "" : getSender().getFromAddress();
	}

	/**
	 * Set "to address". Comma delimited email addresses.
	 *
	 * @param toAddress Addresses.
	 */
    public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	/**
	 * Set the "to address".
	 *
	 * @param personId Id of the {@link Person}, {@link SystemUser}, {@link SystemUserGroup}, {@link Contact},
	 * {@link PersonRole} or {@link MessageGroup}.
	 */
	public void setToAddress(Id personId) {
		setToAddress(StoredObject.get(personId));
	}

	/**
	 * Set the "to address".
	 *
	 * @param person {@link Person}, {@link SystemUser}, {@link SystemUserGroup}, {@link Contact},
	 * {@link PersonRole} or {@link MessageGroup}.
	 */
	public void setToAddress(StoredObject person) {
		if(person == null) {
			return;
		}
		StringBuilder email = new StringBuilder();
		collectEmail(ObjectIterator.create(person), email);
		if(!email.isEmpty()) {
			setToAddress(email.toString());
		}
	}

	public String getToAddress() {
		return toAddress;
	}

	/**
	 * Set "CC address". Comma delimited email addresses.
	 *
	 * @param ccAddress Addresses.
	 */
    public void setCCAddress(String ccAddress) {
        this.cCAddress = ccAddress;
    }

	/**
	 * Set the "CC address".
	 *
	 * @param personId Id of the {@link Person}, {@link SystemUser}, {@link SystemUserGroup}, {@link Contact},
	 * {@link PersonRole} or {@link MessageGroup}.
	 */
	public void setCCAddress(Id personId) {
		setToAddress(StoredObject.get(personId));
	}

	/**
	 * Set the "CC address".
	 *
	 * @param person {@link Person}, {@link SystemUser}, {@link SystemUserGroup}, {@link Contact}, {@link PersonRole}
	 *                               or {@link MessageGroup}.
	 */
	public void setCCAddress(StoredObject person) {
		if(person == null) {
			return;
		}
		StringBuilder email = new StringBuilder();
		collectEmail(ObjectIterator.create(person), email);
		if(!email.isEmpty()) {
			setCCAddress(email.toString());
		}
	}

	@Column(required = false)
    public String getCCAddress() {
        return cCAddress;
    }

    public void setReplyToAddress(String replyToAddress) {
		this.replyToAddress = replyToAddress;
	}

    @Column(required = false)
	public String getReplyToAddress() {
		return replyToAddress;
	}

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }

	public void setErrorValue(String errorValue) {
		for(int i = 0; i < errorValues.length; i++) {
			if(errorValue.equalsIgnoreCase(errorValues[i])) {
				error = i;
				return;
			}
		}
		logger.info("EMail Error Value: " + errorValue);
		error = 1;
	}

	public static String[] getErrorValues() {
		return errorValues;
	}

	public static String getErrorValue(int value) {
		String[] s = getErrorValues();
		return s[value % s.length];
	}

	public String getErrorValue() {
		return getErrorValue(error);
	}

	public void setSender(Id senderId) {
		this.senderId = senderId;
	}

	@Column
	public void setSender(BigDecimal idValue) {
		setSender(new Id(idValue));
	}

	public void setSender(Sender sender) {
		setSender(sender == null ? null : sender.getId());
	}

	@Column(style = "(any)", required = false)
	public Id getSenderId() {
		return senderId;
	}

	public Sender getSender() {
		return get(Sender.class, senderId, true);
	}

	@Override
	public String toString() {
		return toAddress + " \"" + getMessage() + "\" /" + getMessageID() + "(" + DateUtility.format(getCreatedAt())+ ")";
	}

	@Override
	public void validateData(TransactionManager tm) throws Exception {
		if(Id.isNull(senderGroupId)) {
			ArrayList<SenderGroup> groups = new ArrayList<>();
			list(SenderGroup.class).collectAll(groups);
			if(groups.size() > 1) {
				groups.removeIf(g -> !g.getAlert());
			}
			if(!groups.isEmpty()) {
				senderGroupId = groups.get(new Random().nextInt(groups.size())).getId();
			}
		}
		String[] as = toAddress == null ? new String[] { "" } : toAddress.split(",");
		for(String a: as) {
			Email.check(a.trim());
		}
		if(!StringUtility.isWhite(cCAddress)) {
			as = cCAddress.split(",");
			for(String a: as) {
				Email.check(a.trim());
			}
		}
		if(!StringUtility.isWhite(replyToAddress)) {
			replyToAddress = replyToAddress.trim();
			Email.check(replyToAddress);
		}
        senderGroupId = tm.checkType(this, senderGroupId, SenderGroup.class, false);
		if(Id.isNull(sentToId)) {
			Contact contact = get(Contact.class, "Contact='" + as[0].trim() + "'");
			if(contact != null) {
				Person person = contact.listMasters(Person.class).single(false);
				if(person == null) {
					PersonRole pr = contact.listMasters(PersonRole.class, true).single(false);
					if(pr != null) {
						sentToId = pr.getPersonId();
					}
				} else {
					sentToId = person.getId();
				}
			}
		}
		super.validateData(tm);
	}

	private boolean cantAttach() {
		if(error != 3) {
			return false;
		}
		Transaction t = getTransaction();
		return t != null && t.isActive();
	}

	public boolean attach(TransactionManager tm, ContentProducer... content) {
		if(cantAttach()) {
			return false;
		}
		try {
			tm.transact(t -> {
				if(Id.isNull(getId())) {
					save(t);
				}
				StreamData sd;
				Attachment a;
				for(ContentProducer c: content) {
					c.setTransactionManager(tm);
					sd = c.getStreamData();
					a = new Attachment();
					sd.save(t);
					a.setFile(sd);
					a.setFileName(c.getFileName());
					a.save(t);
					addLink(t, a);
				}
			});
		} catch (Exception e) {
			tm.getDevice().log(e);
			return false;
		}
		return true;
	}

	public boolean attach(TransactionManager tm, FileData... files) {
		return attach(tm, new Array<>(files));
	}

	public boolean attach(TransactionManager tm, Iterable<FileData> files) {
		if(cantAttach()) {
			return false;
		}
		try {
			tm.transact(t -> {
				if(Id.isNull(getId())) {
					save(t);
				}
				Attachment a;
				for(FileData file: files) {
					a = new Attachment();
					a.setFile(file.getFileId());
					a.setFileName(file.getName());
					a.save(t);
					addLink(t, a);
				}
			});
		} catch (Exception e) {
			tm.getDevice().log(e);
			return false;
		}
		return true;
	}

	/**
	 * Attach attachments from another mail.
	 * @param tm Transaction manager
	 * @param another Another mail.
	 * @return True if successful.
	 */
	public boolean attachFrom(TransactionManager tm, Mail another) {
		if(cantAttach()) {
			return false;
		}
		try {
			tm.transact(t -> {
				if(Id.isNull(getId())) {
					save(t);
				}
				Attachment a;
				for(Attachment attachment: another.listLinks(Attachment.class)) {
					a = new Attachment();
					a.setFile(attachment.getFileId());
					a.setFileName(attachment.getFileName());
					a.save(t);
					addLink(t, a);
				}
			});
		} catch (Exception e) {
			tm.getDevice().log(e);
			return false;
		}
		return true;
	}

	public static Mail createAlert() throws SOException {
		return createAlert(null);
	}

	public static Mail createAlert(TransactionManager tm) throws SOException {
		Mail m = new Mail();
		if(tm == null) {
			m.ready();
		}
		SenderGroup group = list(SenderGroup.class, "Alert").findFirst();
		if(group == null && tm != null) {
			group = new SenderGroup();
			group.setName("ALERT");
			group.setAlert(true);
            try {
                tm.transact(group::save);
            } catch (Exception e) {
				group = null;
            }
        }
		if(group != null) {
			m.senderGroupId = group.getId();
		} else {
			throw new SOException("No senders defined for mail alerts");
		}
		return m;
	}

	public static void alert(Transaction transaction, String message, StoredObject person) throws Exception {
		alert(transaction, message, ObjectIterator.create(person));
	}

	public static void alert(Transaction transaction, String message, Iterable<? extends StoredObject> persons)
			throws Exception {
		alert(transaction, message, persons, null);
	}

	public static void alert(Transaction transaction, String message, StoredObject person, String subject)
			throws Exception {
		alert(transaction, message, ObjectIterator.create(person), subject);
	}

	public static void alert(Transaction transaction, String message, Iterable<? extends StoredObject> persons,
							 String subject) throws Exception {
		Mail m;
		try {
			m = createAlert();
		} catch(Exception ignore) {
			return;
		}
		m.setMessage(message);
		if(subject != null) {
			m.setSubject(subject);
		} else {
			m.setSubject("Alert");
		}
		StringBuilder email = new StringBuilder();
		collectEmail(persons, email);
		if(email.isEmpty()) {
			return;
		}
		m.setToAddress(email.toString());
		m.ready();
		m.save(transaction);
	}

	private static void collectEmail(Iterable<? extends StoredObject> persons, StringBuilder emails) {
		StoredObject person;
		for(StoredObject so: persons) {
			person = person(so);
			if(person != null) {
				collectEmail(person, emails);
				continue;
			}
			if(so instanceof SystemUserGroup) {
				so.listMasters(SystemUser.class).forEach(su -> collectEmail(su.getPerson(), emails));
			} else if(so instanceof MessageGroup mg) {
				mg.listContacts(1).forEach(p -> collectEmail(p, emails));
			}
		}
	}

	private static void collectEmail(StoredObject person, StringBuilder emails) {
		if(person instanceof HasContacts hc) {
			collectEmail(emails, hc.getContact("email"));
		} else if(person instanceof Contact c) {
			collectEmail(emails, c.getContact());
		}
	}

	private static void collectEmail(StringBuilder emails, String email) {
		if(email != null) {
			if(!emails.isEmpty()) {
				emails.append(',');
			}
			emails.append(email);
		}
	}

	// Returns HasContact or Contact
	private static StoredObject person(StoredObject so) {
		if(so instanceof SystemUser) {
			return ((SystemUser)so).getPerson();
		}
		if(so instanceof HasContacts || so instanceof Contact) {
			return so;
		}
		return null;
	}

	@Override
	public void setSentTo(Person sentTo) {
		super.setSentTo(sentTo);
		if(toAddress == null || toAddress.isBlank()) {
			setToAddress(sentTo);
		}
	}
}
