package com.storedobject.core;

/**
 * The Financial interface represents a financial entity that can post ledger transactions.
 * It provides methods to check if the ledger is already posted, and to post a ledger transaction.
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
     * Posts ledger transactions using the provided TransactionManager.
     *
     * @param transactionManager the TransactionManager used to post the ledger transaction
     * @throws Exception if the ledger is already posted
     */
    void postLedger(TransactionManager transactionManager) throws Exception;

    static int getCategory(Account account) {
        return (account.getAccountStatus() >> 5) & 3;
    }

    static String getCategoryValue(Account account) {
        return AccountChart.getCategoryValue(getCategory(account));
    }

    static int getBalanceType(Account account) {
        return account.getAccountStatus() >> 4;
    }

    static String getBalanceTypeValue(Account account) {
        return AccountChart.getBalanceTypeValue(getBalanceType(account));
    }

    static int getTransactionType(Account account) {
        int status = (account.getAccountStatus() >> 1) & 3;
        return status == 0 ? ((account.getAccountStatus() >> 9) & 3) : status;
    }

    static String getTransactionTypeValue(Account account) {
        return AccountChart.getTransactionTypeValue(getTransactionType(account));
    }

    static boolean hasStrictBalanceControl(Account account) {
        return ((account.getAccountStatus() >> 3) & 1) == 1;
    }

    static boolean hasLimitCheck(Account account) {
        return ((account.getAccountStatus() >> 8) & 1) == 1;
    }

    static boolean isDeepFrozen(Account account) {
        return ((account.getAccountStatus() >> 7) & 1) == 1;
    }

    static boolean isSpecial(Account account) {
        return account instanceof AccountTitle || account instanceof BranchAccount || account instanceof OffsetAccount;
    }

    static boolean isAsset(Account account) {
        return getCategory(account) == 0 && getBalanceType(account) == 0;
    }

    static boolean isLiability(Account account) {
        return getCategory(account) == 1 && getBalanceType(account) == 1;
    }

    /**
     * Is financial system active for this entity?
     *
     * @return True/false.
     */
    static boolean isActive(SystemEntity systemEntity) {
        return StoredObject.exists(Account.class, systemEntity == null ? null : ("SystemEntity=" + systemEntity.getId()),
                true);
    }

    /**
     * Is financial system active for this transaction manager?
     *
     * @return True/false.
     */
    static boolean isActive(TransactionManager tm) {
        return isActive(tm == null ? null : tm.getEntity());
    }
}
