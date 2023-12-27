package com.storedobject.core;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a "window" of entries (instances of {@link LedgerEntry}) of the "Transaction Ledger".
 * A {@link java.util.List} must be provided to
 * keep the entries and this class will maintain that list by adding/removing entries to/from it when the "window" is
 * moved forward/backward. When the instance is initialized with an {@link Account}, it will automatically
 * load entries corresponding to the latest transactions.
 * <p>Note: Please note that this class doesn't support generic ledger entries of external systems unless you override
 * methods: {@link #getLedger(Date, Date)}, {@link #getMaxDate(Date)}, {@link #getMinDate(Date)}. However, there is
 * a provision to append additional entries (could be un-posted ones). Override the {@link #getUnposted()} method for
 * returning the unposted entries. All the entries in the unposted entry list must have the same date, and it will be
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
     * Can we move backward?
     *
     * @return True/false.
     */
    public boolean canMoveBackward() {
        return !isError();
    }

    /**
     * Can we move forward?
     *
     * @return True/false.
     */
    public boolean canMoveForward() {
        return !isError();
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
     * Override this method to return any unposted entries to be appended.
     *
     * @return Unposted entries.
     */
    protected List<LedgerEntry> getUnposted() {
        return null;
    }

    /**
     * Set the error logger to capture the errors while generating the ledger entries.
     *
     * @param errorLogger Error logger.
     */
    public void setErrorLogger(ErrorLogger errorLogger) {
    }


    /**
     * Get the date of the first transaction available for the current account. Override this method if you override
     * {@link #getLedger(Date, Date)}
     * to bring entries from an external system.
     *
     * @param lowerBoundary Lower boundary. If not null, the date returned should be greater than or equal to this.
     *                      Null can be returned if no transactions found.
     * @return Date of the first transaction available.
     */
    public Date getMinDate(Date lowerBoundary) {
        return null;
    }

    /**
     * Get the date of the most recent transaction within the upper boundary. Override this method if you override
     * {@link #getLedger(Date, Date)} to bring entries from an external system.
     *
     * @param upperBoundary Upper boundary. If not null, the date returned should be less than this. Null can be
     *                      returned if no transactions found.
     * @return Most recent transaction date.
     */
    public Date getMaxDate(Date upperBoundary) {
        return null;
    }

    /**
     * Get the ledger entries for the given period for the account. This method is invoked to obtain ledger entries
     * from the system. You may override this method to return entries from external systems.
     *
     * @param from From date.
     * @param to To date.
     * @return Ledger.
     */
    public Ledger getLedger(Date from, Date to) {
        return getAccount().getLedger(DatePeriod.create(from, to));
    }
}
