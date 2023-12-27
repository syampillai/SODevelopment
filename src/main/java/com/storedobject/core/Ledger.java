package com.storedobject.core;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * Representation of a set of entries in the "Transaction Ledger". Please note that this class implements both
 * {@link Iterator} and {@link Iterable} interfaces and thus, it is possible to iterate through the entries (instances
 * of {@link LedgerEntry}) using constructs such as "for" loops.
 * <p>Note: Use {@link Account#getLedger(DatePeriod)} to create an instance of the {@link Ledger}.</p>
 *
 * @author Syam
 */
public interface Ledger extends Iterator<LedgerEntry>, Iterable<LedgerEntry>, AutoCloseable {

    /**
     * Foreign currency opening balance.
     *
     * @return Foreign currency balance.
     */
    Money getOpeningBalance();

    /**
     * Local currency opening balance.
     *
     * @return Local currency balance.
     */
    Money getLocalCurrencyOpeningBalance();

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
