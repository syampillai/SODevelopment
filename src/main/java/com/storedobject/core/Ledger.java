package com.storedobject.core;

import javax.annotation.Nonnull;
import java.sql.Date;
import java.util.Iterator;

/**
 * Representation of a set of entries in the "Transaction Ledger". Please note that this class implements both
 * {@link Iterator} and {@link Iterable} interfaces and thus, it is possible to iterate through the entries (instances
 * of {@link LedgerEntry}) using constructs such as "for" loops.
 * <p>Note: Use {@link Account#getLedger(DatePeriod)} to create an instance of the {@link Ledger}.</p>
 *
 * @author Syam
 */
public interface Ledger extends Iterator<LedgerEntry>, Iterable<LedgerEntry> {

    /**
     * Foreign currency balance, including the current entry. Before the iteration starts, this will return
     * the "Opening Balance" for the date-period selected and when the iteration is over, it will be
     * the "Closing Balance" for the date-period selected.
     *
     * @return Foreign currency balance.
     */
    Money getBalance();

    /**
     * Local currency balance, including the current entry. Before the iteration starts, this will return
     * the "Opening Balance" for the date-period selected and when the iteration is over, it will be
     * the "Closing Balance" for the date-period selected.
     *
     * @return Local currency balance.
     */
    Money getLocalCurrencyBalance();

    /**
     * Date of the current {@link LedgerEntry}. Before the iteration starts, this will return
     * the "Opening Date" of the date-period selected and when the iteration is over, it will be
     * the "Closing Date" of the date-period selected.
     *
     * @return Date.
     */
    Date getDate();

    /**
     * The account of this ledger.
     *
     * @return Account.
     */
    Account getAccount();

    /**
     * The date-period selected.
     *
     * @return Date-period selected.
     */
    DatePeriod getPeriod();

    /**
     * Iterator for {@link LedgerEntry}s.
     *
     * @return Ledger entry iterator.
     */
    @Override
    @Nonnull
    default Iterator<LedgerEntry> iterator() {
        return this;
    }

    /**
     * This method exists for API conformance. However, this operation is not supported.
     */
    @Override
    default void remove() {
        throw new UnsupportedOperationException();
    }
}
