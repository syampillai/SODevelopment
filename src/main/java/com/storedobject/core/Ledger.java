package com.storedobject.core;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.sql.Date;
import java.util.Iterator;

/**
 * Representation of a a set of entries in the "Transaction Ledger". Please note that this class implements both
 * {@link Iterator} and {@link Iterable} interfaces and thus, it is possible to interate through the entries (instances
 * of {@link LedgerEntry}) using constructs such as "for" loops.
 *
 * @author Syam
 */
public final class Ledger implements Iterator<LedgerEntry>, Iterable<LedgerEntry>, Closeable {

    /**
     * Create a Ledger.
     *
     * @param account Account for which ledger needs to be created.
     * @param datePeriod Date period.
     */
    public Ledger(Account account, DatePeriod datePeriod) {
    }

    /**
     * Foreign currency balance, including the current entry. Before the iteration starts, this will return
     * the "Opening Balance" for the date-period selected and when the iteration is over, it will be
     * the "Closing Balance" for the date-period selected.
     *
     * @return Foreign currency balance.
     */
    public Money getBalance() {
        return new Money();
    }

    /**
     * Local currency balance, including the current entry. Before the iteration starts, this will return
     * the "Opening Balance" for the date-period selected and when the iteration is over, it will be
     * the "Closing Balance" for the date-period selected.
     *
     * @return Local currency balance.
     */
    public Money getLocalCurrencyBalance() {
        return new Money();
    }

    /**
     * Date of the current {@link LedgerEntry}. Before the iteration starts, this will return
     * the "Opening Date" of the date-period selected and when the iteration is over, it will be
     * the "Closing Date" of the date-period selected.
     *
     * @return Date.
     */
    public Date getDate() {
        return DateUtility.today();
    }

    /**
     * The account of this ledger.
     *
     * @return Account.
     */
    public Account getAccount() {
        return new Account();
    }

    /**
     * The date-period selected.
     *
     * @return Date-period selected.
     */
    public DatePeriod getPeriod() {
        return new DatePeriod((Date)null, null);
    }

    /**
     * Closes the iterator. This will be automatically invoked if the entries are fully iterated through. Otherwise,
     * it is advisable to call this to release the resources.
     */
    @Override
    public void close() {
    }

    /**
     * Iterator for {@link LedgerEntry}s.
     *
     * @return Ledger entry iterator.
     */
    @Override
    @Nonnull
    public Iterator<LedgerEntry> iterator() {
        return this;
    }

    /**
     * Get the next {@link LedgerEntry}.
     *
     * @return Next ledger entry.
     */
    @Override
    public LedgerEntry next() {
        return new LedgerEntry();
    }

    /**
     * Check if the next {@link LedgerEntry} exists or not.
     *
     * @return True or false.
     */
    @Override
    public boolean hasNext() {
        return false;
    }

    /**
     * This method exists for API conformance. However, this operation is not supported.
     */
    @Override
    public void remove() {
    }
}