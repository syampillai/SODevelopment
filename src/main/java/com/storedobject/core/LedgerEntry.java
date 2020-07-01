package com.storedobject.core;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a single entry in the {@link Ledger}.
 *
 * @author Syam
 */
public final class LedgerEntry {

    /**
     * Constructor.
     */
    LedgerEntry() {
    }

    /**
     * Date of this entry.
     *
     * @return Date of the entry.
     */
    public Date getDate() {
        return DateUtility.today();
    }

    /**
     * Foreign currency amount of this entry.
     *
     * @return Foreign currency amount.
     */
    public Money getAmount() {
        return new Money();
    }

    /**
     * Local currency amount of this entry.
     *
     * @return Local currency amount.
     */
    public Money getLocalCurrencyAmount() {
        return new Money();
    }
    /**
     * Get the balance in the account after this entry is counted.
     *
     * @return Balance.
     */
    public Money getBalance() {
        return new Money();
    }

    /**
     * Get the local currency balance in the account after this entry is counted.
     *
     * @return Local currency balance.
     */
    public Money getLocalCurrencyBalance() {
        return new Money();
    }

    /**
     * Get the opening balance in the account at this entry (before effecting transaction of
     * this entry).
     *
     * @return Opening balance.
     */
    public Money getOpeningBalance() {
        return new Money();
    }

    /**
     * Get the opening balance in local currency in the account at this entry (before effecting transaction of
     * this entry).
     *
     * @return Local currency opening balance.
     */
    public Money getLocalCurrencyOpeningBalance() {
        return new Money();
    }

    /**
     * Transaction particulars (narration) of this entry.
     *
     * @return Transaction particulars.
     */
    public String getParticulars() {
        return "";
    }

    /**
     * Get the vouchers for this transaction. The first entry in the list will be the voucher for this entry.
     *
     * @return List of journal vouchers.
     */
    public List<JournalVoucher> getVouchers() {
        return new ArrayList<>();
    }
}