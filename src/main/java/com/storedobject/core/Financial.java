package com.storedobject.core;

import java.util.List;

/**
 * The Financial interface represents a financial entity that can post ledger transactions.
 * It provides methods to check if the ledger is already posted and to post a ledger transaction.
 * It also provides a default method to post a ledger transaction using a TransactionManager.
 *
 * @author Syam
 */
public interface Financial {

    /**
     * Checks if the ledger is already posted.
     *
     * @return true if the ledger is already posted, false otherwise.
     */
    boolean isLedgerPosted();

    /**
     * Checks whether the ledger is reversed.
     *
     * @return {@code true} if the ledger is reversed, {@code false} otherwise.
     */
    default boolean isLedgerReversed() {
        return false;
    }

    /**
     * Posts ledger transactions using the provided TransactionManager.
     *
     * @param transactionManager the TransactionManager used to post the ledger transaction
     * @throws Exception if the ledger is already posted
     */
    void postLedger(TransactionManager transactionManager) throws Exception;

    /**
     * Determines whether the ledger can be reversed.
     * This method provides a default implementation that specifies
     * the ledger cannot be reversed and always returns false.
     * Override this method to customize the reversal behavior
     * based on specific conditions.
     *
     * @return {@code true} if the ledger can be reversed; otherwise {@code false}.
     */
    default boolean canReverseLedger() {
        return false;
    }

    /**
     * Reverses the ledger transactions using the provided transaction manager.
     * This method delegates the reversal process to another method that
     * works on a per-transaction basis.
     *
     * @param transactionManager the transaction manager used to handle the reversal process
     * @param reversalReason a string specifying the reason for the reversal
     * @param appendToNarration a boolean indicating whether to append the reversal reason to the narration
     * @throws Exception if an error occurs during the reversal process
     */
    default void reverseLedger(TransactionManager transactionManager, String reversalReason, boolean appendToNarration) throws Exception {
        transactionManager.transact(t -> reverseLedger(t, reversalReason, appendToNarration));
    }

    /**
     * Reverses a ledger by creating and saving a reversal journal voucher for the provided transaction.
     *
     * @param transaction the transaction to be reversed
     * @param reversalReason the reason for reversing the transaction
     * @param appendToNarration whether to append the reversal reason to the narration
     * @throws Exception if an error occurs during the reversal process
     */
    default void reverseLedger(Transaction transaction, String reversalReason, boolean appendToNarration) throws Exception {
        getReversalJournalVoucher(reversalReason, appendToNarration).save(transaction);
    }

    /**
     * Retrieves a reversal journal voucher based on the provided reversal reason
     * and whether to append the reason to the narration.
     * <p></p>
     * This method performs the following checks:
     * - Verifies that the ledger has been posted.
     * - Confirms that the ledger supports reversal operations.
     * - Ensures there is at least one journal voucher to reverse.
     * <p></p>
     * If any of these conditions are not met, an exception is thrown.
     *
     * @param reversalReason the reason for reversing the journal voucher
     * @param appendToNarration a flag indicating whether the reversal reason should
     *                          be appended to the voucher narration
     * @return the reversal journal voucher for the first available journal voucher
     * @throws Exception if:
     *         - the ledger is not posted,
     *         - the ledger does not support reversal, or
     *         - there are no journal vouchers to reverse
     */
    default JournalVoucher getReversalJournalVoucher(String reversalReason, boolean appendToNarration) throws Exception {
        if(!isLedgerPosted()) throw new Invalid_State("Ledger not posted");
        if(!canReverseLedger()) throw new Invalid_State("Reversal not supported");
        List<JournalVoucher> vouchers = listJournalVouchers();
        if(vouchers.isEmpty()) throw new Invalid_State("No vouchers to reverse");
        return vouchers.getLast().reverseVoucher(reversalReason, appendToNarration);
    }

    /**
     * Retrieves a list of journal vouchers associated with the current financial entity.
     * If the current object is an instance of {@code StoredObject}, it queries the
     * relevant journal vouchers based on the owner's identifier. Otherwise, it returns an
     * empty list.
     *
     * @return a list of {@code JournalVoucher} instances tied to the financial entity,
     * or an empty list if the object is not a {@code StoredObject}.
     */
    default List<JournalVoucher> listJournalVouchers() {
        if(this instanceof StoredObject so) {
            return StoredObject.list(JournalVoucher.class, "Owner=" + so.getId(), "T.Id", true)
                    .toList();
        }
        return List.of();
    }

    /**
     * Calculates and retrieves the category of the provided account based on its account status.
     *
     * @param account the account whose category is to be determined
     * @return an integer representing the category, determined by shifting the account's status bits and applying a mask
     */
    static int getCategory(Account account) {
        return (account.getAccountStatus() >> 5) & 3;
    }

    /**
     * Retrieves the category value of the given account.
     *
     * @param account the account object from which the category value is to be obtained
     * @return the category value associated with the specified account
     */
    static String getCategoryValue(Account account) {
        return AccountChart.getCategoryValue(getCategory(account));
    }

    /**
     * Retrieves the balance type of the given account.
     *
     * @param account the account for which the balance type is to be determined
     * @return an integer representing the balance type, where the type is derived from the account's status
     */
    static int getBalanceType(Account account) {
        return (account.getAccountStatus() >> 4) & 1;
    }

    /**
     * Retrieves the balance type value associated with the given account.
     *
     * @param account The account for which the balance type value is to be retrieved.
     * @return The balance type value as a string.
     */
    static String getBalanceTypeValue(Account account) {
        return AccountChart.getBalanceTypeValue(getBalanceType(account));
    }

    /**
     * Determines the transaction type based on the account's status.
     *
     * @param account the account object whose status is used to determine the transaction type
     * @return an integer representing the calculated transaction type
     */
    static int getTransactionType(Account account) {
        int status = (account.getAccountStatus() >> 1) & 3;
        return status == 0 ? ((account.getAccountStatus() >> 9) & 3) : status;
    }

    /**
     * Retrieves the transaction type value for a given account.
     *
     * @param account The account for which the transaction type value is to be determined.
     * @return The corresponding transaction type value as a string.
     */
    static String getTransactionTypeValue(Account account) {
        return AccountChart.getTransactionTypeValue(getTransactionType(account));
    }

    /**
     * Determines if the given account has strict balance control enabled.
     *
     * @param account the account to evaluate
     * @return {@code true} if the account has strict balance control enabled, {@code false} otherwise
     */
    static boolean hasStrictBalanceControl(Account account) {
        return ((account.getAccountStatus() >> 3) & 1) == 1;
    }

    /**
     * Checks whether the given account has a limit check flag enabled.
     * This method examines the account's status by performing a bitwise operation
     * to determine if the specific limit check flag is set.
     *
     * @param account the account to be checked
     * @return true if the account has the limit check flag enabled, false otherwise
     */
    static boolean hasLimitCheck(Account account) {
        return ((account.getAccountStatus() >> 8) & 1) == 1;
    }

    /**
     * Determines whether the provided account is deeply frozen.
     *
     * @param account The account to check for the deep-frozen status.
     * @return {@code true} if the account is deeply frozen, {@code false} otherwise.
     */
    static boolean isDeepFrozen(Account account) {
        return ((account.getAccountStatus() >> 7) & 1) == 1;
    }

    /**
     * Determines if the given account is a special type of account.
     *
     * @param account the account to check; can be an instance of AccountTitle, BranchAccount, or OffsetAccount
     * @return true if the account is an instance of AccountTitle, BranchAccount, or OffsetAccount; false otherwise
     */
    static boolean isSpecial(Account account) {
        return account instanceof AccountTitle || account instanceof BranchAccount || account instanceof OffsetAccount;
    }

    /**
     * Determines whether the provided account is classified as an asset.
     *
     * @param account the account to be evaluated
     * @return true if the account is categorized as an asset, otherwise false
     */
    static boolean isAsset(Account account) {
        return getCategory(account) == 0 && getBalanceType(account) == 0;
    }

    /**
     * Determines if the given account qualifies as a liability based on its category and balance type.
     *
     * @param account the account to be evaluated
     * @return true if the account is classified as a liability, false otherwise
     */
    static boolean isLiability(Account account) {
        return getCategory(account) == 1 && getBalanceType(account) == 1;
    }

    /**
     * Determines whether the given account qualifies as an expense.
     *
     * @param account the account to be checked.
     * @return true if the account's category is 1 and its balance type is 0, false otherwise.
     */
    static boolean isExpense(Account account) {
        return getCategory(account) == 1 && getBalanceType(account) == 0;
    }

    /**
     * Determines if the provided account qualifies as a revenue account.
     *
     * @param account The account object to be evaluated.
     * @return {@code true} if the account's category is 0 and the balance type is 1, otherwise {@code false}.
     */
    static boolean isRevenue(Account account) {
        return getCategory(account) == 0 && getBalanceType(account) == 1;
    }

    /**
     * Is a financial system active for this entity?
     *
     * @return True/false.
     */
    static boolean isActive(SystemEntity systemEntity) {
        return StoredObject.exists(Account.class, systemEntity == null ? null : ("SystemEntity=" + systemEntity.getId()),
                true);
    }

    /**
     * Is a financial system active for this transaction manager?
     *
     * @return True/false.
     */
    static boolean isActive(TransactionManager tm) {
        return isActive(tm == null ? null : tm.getEntity());
    }
}
