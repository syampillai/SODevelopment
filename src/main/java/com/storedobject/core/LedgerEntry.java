package com.storedobject.core;

import java.sql.*;
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
     * Get the transaction Id that created this entry.
     *
     * @return Transaction Id.
     */
    default Id getLedgerTran() {
        return Id.ZERO;
    }

    /**
     * Get the voucher for this entry.
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
}
