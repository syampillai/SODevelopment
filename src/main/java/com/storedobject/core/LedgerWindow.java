package com.storedobject.core;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representation of a "window" of entries (instances of {@link LedgerEntry}) of the "Transaction Ledger".
 * A {@link java.util.List} must be provided to
 * keep the entries and this class will maintain that list by adding/removing entries to/from it when the "window" is
 * moved forward/backward. When the instance is initialized with an {@link Account}, it will automatically
 * load entries corresponding to the latest transactions.
 * <p>Note: Please note that this class doesn't support generic ledger entries of external systems. However, there is
 * a provision to append additional entries (could be un-posted ones). Override the {@link #getTail()} method for
 * returning the tail entries. The date in the tail must be the same for all the entries in the tail and it will be
 * automatically fixed as the upper date boundary.</p>
 *
 * @author Syam
 */
public class LedgerWindow {

    /**
     * Constructor. Default window size is 10.
     */
    public LedgerWindow() {
        this(0, null);
    }

    /**
     * Constructor. Default window size is 10.
     *
     * @param entries List where ledger entries need to be maintained. If <code>null</code> is passed,
     *                a new list will be created.
     */
    public LedgerWindow(List<LedgerEntry> entries) {
        this(0, entries);
    }

    /**
     * Constructor.
     *
     * @param windowSize Window size of the ledger entries.
     */
    public LedgerWindow(int windowSize) {
        this(windowSize, null);
    }

    /**
     * Constructor.
     *
     * @param windowSize Window size of the ledger entries.
     * @param entries List where ledger entries need to be maintained. If <code>null</code> is passed,
     *                a new list will be created.
     */
    public LedgerWindow(int windowSize, List<LedgerEntry> entries) {
    }

    /**
     * Get the list containing the ledger entries. The list returned by this method should not be altered externally.
     * If altered, further method calls may not work properly.
     *
     * @return List.
     */
    public List<LedgerEntry> getEntries() {
        return new ArrayList<>();
    }

    /**
     * Get the account for which entries are loaded.
     *
     * @return Account.
     */
    public Account getAccount() {
        return new Account();
    }

    /**
     * Set the account for which ledger entries need to be loaded. Ledger entries for the most recent transactions
     * will be loaded.
     *
     * @param account Account.
     */
    public void setAccount(Account account) {
    }

    /**
     * Set the account for which ledger entries need to be loaded.
     *
     * @param account Account.
     * @param date Date for which entries need to be loaded. If passed <code>null</code>, most recent
     *             entries will be loaded.
     */
    public void setAccount(Account account, Date date) {
    }

    /**
     * Set the starting date from which ledger entries to be loaded.
     * If enough entries are not found to fill up the window-size, it will try to load more entries from the
     * following dates until window-size can be full-filled. If there are no entries in the following dates
     * to full-fill the window-size, it will try load entries from the
     * preceding dates to full-fill the window-size.
     * If <code>null</code> is passed, the entries corresponding to the latest transactions are loaded.
     *
     * @param date Starting date.
     */
    public void setDate(Date date) {
    }

    /**
     * Move window to the beginning.
     */
    public void moveToBeginning() {
    }

    /**
     * Move window to the end.
     */
    public void moveToEnd() {
    }

    /**
     * Move the window backward.
     *
     * @return True if moved and false if no further ledger entries found.
     */
    public boolean moveBackward() {
        return false;
    }

    /**
     * Move the window forward.
     *
     * @return True if moved and false if no further ledger entries found.
     */
    public boolean moveForward() {
        return false;
    }

    /**
     * Check if any DB error occurred while loading entries or not.
     *
     * @return True or false.
     */
    public boolean isError() {
        return false;
    }

    /**
     * Get the most recent transaction date of this account.
     *
     * @return Most recent transaction date. This method returns <code>null</code> if transactions are not yet loaded.
     */
    public Date getMostRecentDate() {
        return new Random().nextBoolean() ? DateUtility.today() : null;
    }

    /**
     * Get the earliest transaction date of this account.
     *
     * @return Earliest transaction date. This method returns <code>null</code> if transactions are not fully loaded.
     */
    public Date getEarliestDate() {
        return new Random().nextBoolean() ? DateUtility.today() : null;
    }

    /**
     * Get the period for which ledger entries are loaded.
     *
     * @return Period. This method returns <code>null</code> if transactions are not loaded.
     */
    public DatePeriod getPeriod() {
        return DatePeriod.thisMonth();
    }

    /**
     * Override this method to return the extra tail entries.
     *
     * @return Extra trail entries.
     */
    protected List<LedgerEntry> getTail() {
        return null;
    }

    /**
     * Set the error logger to capture the errors while generating the ledger entries.
     *
     * @param errorLogger Error logger.
     */
    public void setErrorLogger(ErrorLogger errorLogger) {
    }
}
