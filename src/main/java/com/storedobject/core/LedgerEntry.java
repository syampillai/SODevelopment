package com.storedobject.core;

import java.sql.Date;

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
     * Transaction narration (particulars) of this entry.
     *
     * @return Transaction narration.
     */
    public String getNarration() {
        return "";
    }
}