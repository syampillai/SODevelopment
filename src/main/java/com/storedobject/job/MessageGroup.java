package com.storedobject.job;

import com.storedobject.common.EndUserMessage;
import com.storedobject.core.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class MessageGroup extends StoredObject implements RequiresApproval{

	public MessageGroup() {
	}

	public static MessageGroup get(String name) {
		return Math.random() > 0.5f ? null : new MessageGroup();
	}

	public static ObjectIterator < MessageGroup > list(String name) {
		return ObjectIterator.create();
	}

	public void setName(String name) {
	}

	public String getName() {
		return "";
	}

	public void setTemplate(Id templateId) {
	}

	public void setTemplate(BigDecimal idValue) {
	}

	public void setTemplate(MessageTemplate template) {
	}

	public Id getTemplateId() {
		return new Id();
	}

	public MessageTemplate getTemplate() {
		return new MessageTemplate();
	}

	public ObjectIterator<Person> listMembers() {
		return ObjectIterator.create();
	}


	/**
	 * Get the list of contacts belonging to this message group.
	 *
	 * @param contactType Type of contact (0: SMS, 1: Email, 2: Application)
	 * @return List.
	 */
	public List<Contact> listContacts(int contactType) {
		return new ArrayList<>();
	}

	public List<SystemUser> listUsers() {
		return new ArrayList<>();
	}

	/**
	 * Create and send a message to all members of this group.
	 * @param tm Transaction Manager.
	 * @param messageParameters Parameters for creating message from the associated template.
	 * @throws Throwable If message can not be created.
	 */
	public void send(TransactionManager tm, Object... messageParameters) throws Throwable {
	}

	/**
	 * Create and send a message to all members of this group and to an additional person.
	 * @param person Additional person to receive the message
	 * @param tm Transaction Manager.
	 * @param messageParameters Parameters for creating message from the associated template.
	 * @throws Throwable If message can not be created.
	 */
	public void send(Person person, TransactionManager tm, Object... messageParameters) throws Throwable {
	}

	/**
	 * Create and send a message to all members of this group.
	 * @param tc Transaction Control.
	 * @param messageParameters Parameters for creating message from the associated template.
	 * @throws Throwable If message can not be created.
	 */

	public void send(TransactionControl tc, Object... messageParameters) throws Throwable {
	}

	/**
	 * Create and send a message to all members of this group and to an additional person.
	 * @param person Additional person to receive the message
	 * @param tc Transaction Control.
	 * @param messageParameters Parameters for creating message from the associated template.
	 * @throws Throwable If message can not be created.
	 */
	public void send(Person person, TransactionControl tc, Object... messageParameters) throws Throwable {
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
	}

	/**
	 * Create and send a message to all members of this group and to an additional person. No message will be sent if the message
	 * group does not exist.
	 * @param groupName Name of the message group.
	 * @param person Additional person to receive the message
	 * @param tc Transaction Control.
	 * @param messageParameters Parameters for creating message from the associated template.
	 * @throws Throwable If message can not be created.
	 */
	public static void send(String groupName, Person person, TransactionControl tc, Object... messageParameters) throws Throwable {
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

	/**
	 * Create a new message group if it doesn't exist.
	 *
	 * @param tm Transaction manager.
	 * @param name Name of the group.
	 * @return Message group instance.
	 */
	public static MessageGroup create(TransactionManager tm, String name) {
		return new MessageGroup();
	}
}
