package com.storedobject.core;

import java.math.BigInteger;

/**
 * SO transaction interface.
 *
 * @author Syam
 */
public interface Transaction {

	/**
	 * Transaction commit listener.
	 */
	@FunctionalInterface
	interface CommitListener {
		/**
		 * Will be invoked jus before committing the transaction. (Default implementation does nothing).
		 *
		 * @param transaction Transaction
		 * @throws Throwable If any exception is thrown, transaction will be rolled back.
		 */
		default void committing(Transaction transaction) throws Throwable {
		}

		/**
		 * Will be invoked after the transaction is committed.
		 *
		 * @param transaction Transaction
		 */
		void committed(Transaction transaction);

		/**
		 * Will be invoked after the transaction is rolled back. (Default implementation does nothing).
		 *
		 * @param transaction Transaction
		 */
		default void rolledback(Transaction transaction) {
		}
	}

	/**
	 * Add a commit listener for this transaction.
	 *
	 * @param listener Listener
	 */
	void addCommitListener(CommitListener listener);

	/**
	 * Remove a commit listener for this transaction.
	 *
	 * @param listener Listener
	 */
	void removeCommitListener(CommitListener listener);

	/**
	 * Gets the current error in Transaction.
	 * A transaction that was closed normally (committed) does not return any error.
	 *
	 * @return Error if any, otherwise null.
	 */
	Exception getError();

	/**
	 * Gets the Id of this transaction.
	 *
	 * @return Id
	 */
	Id getId();

	/**
	 * Gets the TransactionManager associated with this Transaction.
	 *
	 * @return TransactionManager
	 */
	TransactionManager getManager();

	default Id getUserId() {
		return null;
	}

	/**
	 * Gets Session Id of the transaction.
	 *
	 * @return Session Id.
	 */
	default Id getSession() {
		return null;
	}

	<T extends StoredObject> T get(T object);

	<T extends StoredObject> T get(Class<T> objectClass, Id objectId);

	default StoredObject get(Id objectId) {
		return get(StoredObject.class, objectId);
	}

	/**
	 * See if this Id is involved in this transaction or not.
	 *
	 * @param id Id to be checked.
	 * @return True if involved.
	 */
	boolean isInvolved(Id id);

	/**
	 * See if this object is involved in this transaction or not.
	 *
	 * @param object Object to be checked.
	 * @return True if involved.
	 */
	default boolean isInvolved(StoredObject object) {
		if(object == null) {
			return false;
		}
		return isInvolved(object.getId());
	}
	/**
	 * Commit the transaction.
	 * @throws Exception Any
	 */
	void commit() throws Exception;

	/**
	 * Rollback the transaction.
	 */
	void rollback();

	/**
	 * See if this transaction is active or not.
	 * @return True if active. False if already committed or rolled back.
	 */
	boolean isActive();

	/**
	 * Debit a local currency account.
	 *
	 * @param account Account to be debited.
	 * @param amount Amount.
	 * @param narration Transaction narration.
	 * @throws Exception Any
	 */
	void debit(Account account, Money amount, String narration) throws Exception;

	/**
	 * Debit a foreign currency account.
	 *
	 * @param account Account to be debited.
	 * @param amount Amount in account currency.
	 * @param localCurrencyAmount Amount in local currency.
	 * @throws Exception Any
	 * @param narration Transaction narration.
	 */
	void debit(Account account, Money amount, Money localCurrencyAmount, String narration) throws Exception;

	/**
	 * Credit a local currency account.
	 *
	 * @param account Account to be credited.
	 * @param amount Amount.
	 * @param narration Transaction narration.
	 * @throws Exception Any
	 */
	void credit(Account account, Money amount, String narration) throws Exception;

	/**
	 * Credit a foreign currency account.
	 *
	 * @param account Account to be credited.
	 * @param amount Amount in account currency.
	 * @param localCurrencyAmount Amount in local currency.
	 * @param narration Transaction narration.
	 * @throws Exception Any
	 */
	void credit(Account account, Money amount, Money localCurrencyAmount, String narration) throws Exception;
}