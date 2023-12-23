package com.storedobject.core;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a single entry in the {@link Ledger}.
 *
 * @author Syam
 */
public interface LedgerEntry {

    /**
     * Date of this entry.
     *
     * @return Date of the entry.
     */
    Date getDate();

    /**
     * Foreign currency amount of this entry.
     *
     * @return Foreign currency amount.
     */
    Money getAmount();

    /**
     * Local currency amount of this entry.
     *
     * @return Local currency amount.
     */
    Money getLocalCurrencyAmount();

    /**
     * Get the balance in the account after this entry is counted.
     *
     * @return Balance.
     */
    Money getBalance();

    /**
     * Get the local currency balance in the account after this entry is counted.
     *
     * @return Local currency balance.
     */
    Money getLocalCurrencyBalance();

    /**
     * Get the opening balance in the account at this entry (before effecting transaction of
     * this entry).
     *
     * @return Opening balance.
     */
    default Money getOpeningBalance() {
        return getBalance().subtract(getAmount());
    }

    /**
     * Get the opening balance in local currency in the account at this entry (before effecting transaction of
     * this entry).
     *
     * @return Local currency opening balance.
     */
    default Money getLocalCurrencyOpeningBalance() {
        return getLocalCurrencyBalance().subtract(getLocalCurrencyAmount());
    }

    /**
     * Transaction particulars (narration) of this entry.
     *
     * @return Transaction particulars.
     */
    String getParticulars();

    /**
     * Get the entry serial number.
     *
     * @return Entry serial number.
     */
    default int getEntrySerial() {
        return -1;
    }

    /**
     * Get the transaction Id that created this entry. (This is not available in external systems).
     *
     * @return Transaction Id.
     */
    default Id getLedgerTran() {
        return Id.ZERO;
    }

    /**
     * Get the voucher for this entry. (This is not available in external systems).
     *
     * @return Voucher.
     */
    default JournalVoucher getVoucher() {
        return null;
    }

    /**
     * Get the vouchers for this transaction. The first entry in the list will be the voucher for this entry.
     * <p>Note: This could return an empty list if the voucher is not available in the current implementation.</p>
     *
     * @return List of journal vouchers.
     */
    default List<JournalVoucher> getVouchers() {
        return new ArrayList<>();
    }

    /**
     * String representation of this entry.
     *
     * @return A string representation suitable for human-friendly display.
     */
    default String toDisplay() {
        return DateUtility.formatDate(getDate()) + ' ' + getOpeningBalance() + " => " + getAmount() + " => "
                + getBalance() + '\n' + getParticulars();
    }

    /**
     * Check if this entry is already posted on the ledger in the DB. This is always true in the case of SO platform.
     * However, if you are abstracting away entries from external systems, it may contain un-posted entries.
     *
     * @return The default implementation always returns <code>true</code>.
     */
    default boolean isPosted() {
        return true;
    }

    /**
     * Return a short-string representing the type of the entry. In SO platform, this is available only if a
     * {@link TransactionType} is set while posting the entry.
     * <p>In some external system, it could be numeric value but the String representation will be returned by this
     * method even though it is a bit inefficient.</p>
     *
     * @return A short-string representing the transaction. Otherwise, <code>null</code> is returned.
     */
    default String getType() {
        return null;
    }

    /**
     * Certain external systems still follows batch processing and the entry may have a batch number. This method
     * returns that number.
     * <p>SO platform is not batch-based and returns -1 from this method. </p>
     *
     * @return Batch number for batch-based systems and -1 if not batch-based or not available.
     */
    default int getBatchNumber() {
        return -1;
    }
}
