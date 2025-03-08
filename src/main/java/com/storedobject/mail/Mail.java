package com.storedobject.mail;

import com.storedobject.common.Array;
import com.storedobject.common.Email;
import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import com.storedobject.job.MessageGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

/**
 * Represents a Mail providing capabilities to manage and send emails with
 * support for handling addresses, CC, sender information, message types,
 * attachments, and error handling.
 *
 * @author Syam
 */
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

	/**
	 * Initializes a new instance of the Mail class with default settings.
	 * This constructor sets the initial error status to 3.
	 */
	public Mail() {
		error = 3;
	}

	/**
	 * Adds the necessary columns for the Mail table.
	 *
	 * @param columns The Columns object used to define the column structure. This method
	 *                adds the following columns:
	 *                <pre>
	 *                - "SenderGroup" with type "Id"
	 *                - "ToAddress" with type "text"
	 *                - "CCAddress" with type "text"
	 *                - "ReplyToAddress" with type "text"
	 *                - "Subject" with type "text"
	 *                - "MessageType" with type "text"
	 *                - "Sender" with type "Id"
	 *                </pre>
	 */
	public static void columns(Columns columns) {
		columns.add("SenderGroup", "Id");
        columns.add("ToAddress", "text");
        columns.add("CCAddress", "text");
        columns.add("ReplyToAddress", "text");
        columns.add("Subject", "text");
        columns.add("MessageType", "text");
		columns.add("Sender", "Id");
	}

	/**
	 * Defines and adds various index configurations for a mail table.
	 *
	 * @param indices The {@link Indices} object used to specify the column combinations and constraints
	 *                for table indexing.
	 */
	public static void indices(Indices indices) {
		indices.add("ToAddress,CreatedAt", false);
		indices.add("ToAddress,Sent", "NOT Sent", false);
		indices.add("CreatedAt,Sent", "NOT Sent", false);
	}

    /**
	 * Provides links to associated classes or objects related to 'Mail'.
	 *
	 * @return An array of strings, each representing a link in the format:
	 *         "Label|QualifiedClassName|AdditionalInfo|".
	 *         Example elements: "Attachments|com.storedobject.mail.Attachment||",
	 *         "Errors|com.storedobject.mail.Error||".
	 */
	public static String[] links() {
        return new String[] {
            "Attachments|com.storedobject.mail.Attachment||",
            "Errors|com.storedobject.mail.Error||",
        };
    }
    
    /**
	 * Customizes the metadata for a specified field name by applying specific configurations.
	 *
	 * @param fieldName the name of the field whose metadata needs to be customized
	 * @param md the UIFieldMetadata object that represents the metadata of the field
	 * @return true if the metadata was successfully customized for the specified field, false otherwise
	 */
	public static boolean customizeMetadata(String fieldName, UIFieldMetadata md) {
        if(fieldName.equals("Error")) {
            md.setCaption("Status");
            return true;
        }
        return false;
    }
    
    /**
	 * Sets the sender group for the mail based on the given sender group name.
	 * This method retrieves the {@link SenderGroup} object associated with the
	 * specified name and updates the sender group for the mail.
	 *
	 * @param senderGroupName the name of the sender group to be set
	 */
	public void setSenderGroup(String senderGroupName) {
    	setSenderGroup(SenderGroup.get(senderGroupName));
    }
    
    /**
	 * Sets the sender group for the mail.
	 *
	 * @param senderGroupId The ID of the sender group to be set.
	 */
	public void setSenderGroup(Id senderGroupId) {
        this.senderGroupId = senderGroupId;
    }

    /**
	 * Sets the sender group using the given identifier value.
	 *
	 * @param idValue The identifier value of the sender group.
	 */
	@Column
    public void setSenderGroup(BigDecimal idValue) {
        setSenderGroup(new Id(idValue));
    }

    /**
	 * Sets the sender group for the mail.
	 *
	 * @param senderGroup The sender group to be set. If null, the sender group ID is set to null.
	 */
	public void setSenderGroup(SenderGroup senderGroup) {
        setSenderGroup(senderGroup == null ? null : senderGroup.getId());
    }

    /**
	 * Retrieves the identifier of the sender group associated with the mail.
	 *
	 * @return The identifier of the sender group as an {@code Id}.
	 */
	public Id getSenderGroupId() {
        return senderGroupId;
    }

    /**
	 * Retrieves the SenderGroup instance associated with the current senderGroupId.
	 *
	 * @return The SenderGroup instance corresponding to senderGroupId, or null if it is not found.
	 */
	public SenderGroup getSenderGroup() {
    	return get(SenderGroup.class, senderGroupId);
    }

	/**
	 * Retrieves the "from address" associated with the sender of the mail.
	 * If the sender is not defined, an empty string will be returned.
	 *
	 * @return The "from address" as a {@code String}, or an empty string if the sender is not set.
	 */
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

	/**
	 * Retrieves the "to address" associated with the mail.
	 *
	 * @return The "to address" as a String. This typically contains comma-separated email addresses.
	 */
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

	/**
	 * Retrieves the "CC address" associated with the mail.
	 *
	 * @return The "CC address" as a String. This typically contains comma-separated email addresses
	 *         or may return null if no CC address is set.
	 */
	@Column(required = false)
    public String getCCAddress() {
        return cCAddress;
    }

    /**
	 * Sets the "Reply-To" address for the email. This address specifies where replies
	 * to the email should be sent.
	 *
	 * @param replyToAddress The "Reply-To" email address to be set. Typically, this is
	 *                       a valid email address in string format.
	 */
	public void setReplyToAddress(String replyToAddress) {
		this.replyToAddress = replyToAddress;
	}

    /**
	 * Retrieves the "reply-to address" associated with the mail.
	 * This value may be used to indicate the address that should
	 * receive replies to the mail if different from the "from address".
	 *
	 * @return The "reply-to address" as a String, or null if not set.
	 */
	@Column(required = false)
	public String getReplyToAddress() {
		return replyToAddress;
	}

    /**
	 * Sets the subject of the mail.
	 *
	 * @param subject The subject of the mail as a string.
	 */
	public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
	 * Retrieves the subject of the mail.
	 *
	 * @return The subject of the mail as a String.
	 */
	public String getSubject() {
        return subject;
    }

    /**
	 * Sets the message type for the mail.
	 *
	 * @param messageType The type of the message to be set.
	 */
	public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    /**
	 * Retrieves the message type associated with this mail.
	 *
	 * @return The message type as a String.
	 */
	public String getMessageType() {
        return messageType;
    }

	/**
	 * Sets the error value for the Mail instance. The method determines the error code
	 * corresponding to the specified error value from the predefined errorValues array.
	 * If the provided error value is not found, a default error code is assigned.
	 *
	 * @param errorValue The error description string to set as the current error value.
	 *                   This string is compared against the elements in the errorValues array.
	 */
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

	/**
	 * Retrieves the array of error values associated with the mail.
	 *
	 * @return An array of strings representing error values.
	 */
	public static String[] getErrorValues() {
		return errorValues;
	}

	/**
	 * Retrieves a specific error value from the predefined error values array based on the provided input value.
	 *
	 * @param value the input value used to determine the appropriate error value. This value is used as an
	 *              index after being adjusted by the length of the error values array.
	 * @return the error value as a String corresponding to the adjusted index derived from the input value.
	 */
	public static String getErrorValue(int value) {
		String[] s = getErrorValues();
		return s[value % s.length];
	}

	/**
	 * Retrieves the error value associated with the current object's error property.
	 *
	 * @return The error value as a String, derived from the error property.
	 */
	public String getErrorValue() {
		return getErrorValue(error);
	}

	/**
	 * Sets the sender for the mail using the specified sender ID.
	 *
	 * @param senderId The identifier of the sender to be set.
	 */
	public void setSender(Id senderId) {
		this.senderId = senderId;
	}

	/**
	 * Sets the sender information for the mail using the provided identifier value.
	 * The identifier value is wrapped into an {@link Id} object and assigned to the sender.
	 *
	 * @param idValue The identifier value of the sender as a {@code BigDecimal}.
	 */
	@Column
	public void setSender(BigDecimal idValue) {
		setSender(new Id(idValue));
	}

	/**
	 * Sets the sender for the mail instance using the provided {@link Sender} object.
	 * If the sender is null, the sender ID is set to null.
	 *
	 * @param sender The {@link Sender} object representing the sender to be set.
	 *               If null, the sender ID associated with the mail will also be set to null.
	 */
	public void setSender(Sender sender) {
		setSender(sender == null ? null : sender.getId());
	}

	/**
	 * Retrieves the identifier of the sender associated with the mail.
	 *
	 * @return The sender identifier as an {@code Id} instance, or null if not set.
	 */
	@Column(style = "(any)", required = false)
	public Id getSenderId() {
		return senderId;
	}

	/**
	 * Retrieves the sender associated with the mail.
	 *
	 * @return The {@link Sender} instance corresponding to the senderId. If the sender is not found, the method may return null.
	 */
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

	/**
	 * Attaches one or more content items to the mail using the provided transaction manager.
	 * This method saves the current context if not already saved, processes the content,
	 * and creates attachments by linking the content to mail.
	 *
	 * @param tm the transaction manager used to handle database transactions
	 * @param content one or more content producers that generate the content to be attached
	 * @return true if the attachment process is successful, false otherwise
	 */
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

	/**
	 * Attaches the provided files to the specified transaction manager.
	 *
	 * @param tm the transaction manager to which the files should be attached
	 * @param files the array of file data objects to be attached
	 * @return true if the files were successfully attached, false otherwise
	 */
	public boolean attach(TransactionManager tm, FileData... files) {
		return attach(tm, new Array<>(files));
	}

	/**
	 * Attaches a collection of files to the current object using the provided TransactionManager.
	 *
	 * @param tm the TransactionManager to handle the transactional operations
	 * @param files an Iterable collection of FileData objects to be attached
	 * @return true if the files were successfully attached; false otherwise
	 */
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
	 * Attach attachments from another mail. When doing this, the actual contents are not duplicated. Instead, they
	 * are linked internally.
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

	/**
	 * Creates an alert and returns it as a Mail object.
	 * This method may throw an SOException in case of errors
	 * during the alert creation process.
	 *
	 * @return A Mail object representing the created alert.
	 * @throws SOException If an error occurs during the alert creation process.
	 */
	public static Mail createAlert() throws SOException {
		return createAlert(null);
	}

	/**
	 * Creates and prepares an alert mail based on the provided TransactionManager instance.
	 * If no sender group with the name "Alert" exists, it attempts to create and save one
	 * using the transaction manager. The sender group ID is then assigned to the mail.
	 * If no TransactionManager is provided or the sender group creation fails,
	 * the method throws an SOException.
	 *
	 * @param tm the TransactionManager instance used to handle database transactions
	 *           for creating a new sender group if required. It can be null.
	 * @return a Mail object initialized with sender group details for the alert.
	 * @throws SOException if no sender groups are defined for mail alerts, and
	 *                     the sender group creation fails or cannot be performed.
	 */
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

	/**
	 * Sends an alert for the given transaction with a specified message and person information.
	 * <p>Note: The person object could be an instance of {@link Person}, {@link SystemUser},
	 * {@link HasContacts} or {@link Contact}.</p>
	 *
	 * @param transaction the transaction object associated with the alert
	 * @param message the message to be included in the alert
	 * @param person the person object related to the alert
	 * @throws Exception if an error occurs while processing the alert
	 */
	public static void alert(Transaction transaction, String message, StoredObject person) throws Exception {
		alert(transaction, message, ObjectIterator.create(person));
	}

	/**
	 * Sends an alert with a specified message to a group of persons in the context of a given transaction.
	 * <p>Note: The person iterator could contain instances of {@link Person}, {@link SystemUser},
	 * {@link HasContacts} or {@link Contact}.</p>
	 *
	 * @param transaction The transaction within which the alert is being generated.
	 * @param message The message content for the alert.
	 * @param persons An iterable collection of recipients (StoredObject instances) to whom the alert is sent.
	 * @throws Exception If an error occurs while processing the alert.
	 */
	public static void alert(Transaction transaction, String message, Iterable<? extends StoredObject> persons)
			throws Exception {
		alert(transaction, message, persons, null);
	}

	/**
	 * Sends an alert with the specified details.
	 * <p>Note: The person object could be an instance of {@link Person}, {@link SystemUser},
	 * {@link HasContacts} or {@link Contact}.</p>
	 *
	 * @param transaction The transaction context in which the alert will be executed.
	 * @param message The message content of the alert.
	 * @param person The stored object representing the person to whom the alert is related.
	 * @param subject The subject of the alert.
	 * @throws Exception If an error occurs while sending the alert.
	 */
	public static void alert(Transaction transaction, String message, StoredObject person, String subject)
			throws Exception {
		alert(transaction, message, ObjectIterator.create(person), subject);
	}

	/**
	 * Sends an alert email to the specified recipients with the provided message and subject.
	 * <p>Note: The person object could be an instance of {@link Person}, {@link SystemUser},
	 * {@link HasContacts} or {@link Contact}.</p>
	 *
	 * @param transaction The transaction object used to save the email alert.
	 * @param message The message content of the alert email.
	 * @param persons An iterable collection of recipients represented as StoredObject instances.
	 * @param subject The subject line of the alert email. If null, the default subject "Alert" is used.
	 * @throws Exception If an error occurs during alert creation, email preparation, or saving the alert.
	 */
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

	/**
	 * Sets the recipient of the message.
	 *
	 * @param sentTo The person to whom the message is being sent.
	 */
	@Override
	public void setSentTo(Person sentTo) {
		super.setSentTo(sentTo);
		if(toAddress == null || toAddress.isBlank()) {
			setToAddress(sentTo);
		}
	}
}
