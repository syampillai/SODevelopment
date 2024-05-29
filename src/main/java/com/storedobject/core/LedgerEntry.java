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
     * Value-date of this entry. Normally, value-date is the same as the{@link #getDate()}. However, for
     * the purpose of interest calculations, the effective date of any entry may have a different date.
     *
     * @return Value-date of the entry.
     */
    default Date getValueDate() {
        return getDate();
    }

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
     * Transaction particulars (narration) of this entry.
     *
     * @param includeValueDatedDetails Whether to include value-dated details or not.
     * @return Transaction particulars.
     */
    default String getParticulars(boolean includeValueDatedDetails) {
        String p = getParticulars();
        if(isUnposted()) {
            p = "[Not Posted] " + p;
        }
        if(!includeValueDatedDetails) {
            return p;
        }
        Date d = getDate(), vd = getValueDate();
        if(DateUtility.isSameDate(d, vd)) {
            return p;
        }
        return "Value Dated: " + DateUtility.formatDate(vd) + '\n' + p;
    }

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
        StringBuilder s = new StringBuilder(DateUtility.formatDate(getDate())).append('\n');
        Money m = getOpeningBalance();
        if(m.isNegative()) {
            s.append('-');
            m = m.negate();
        }
        s.append(m).append(' ');
        m = getAmount();
        if(m.isNegative()) {
            s.append('-');
            m = m.negate();
        } else {
            s.append('+');
        }
        s.append(' ').append(m).append(" = ");
        m = getBalance();
        if(m.isNegative()) {
            s.append('-');
            m = m.negate();
        }
        s.append(m);
        s.append('\n').append(getParticulars(true));
        int bn = getBatchNumber();
        String type = getType();
        if(bn > 0 || type != null) {
            s.append('\n');
            if(bn > 0) {
                s.append("Batch Number: ").append(bn);
                if(type != null) {
                    s.append(", ");
                }
            }
            if(type != null) {
                s.append("Type: ").append(type);
            }
        }
        return s.toString();
    }

    /**
     * Check if this entry is not yet posted on the ledger in the DB. This is always false in the case of SO platform.
     * However, if you are abstracting away entries from external systems, it may contain un-posted entries.
     *
     * @return The default implementation always returns <code>false</code>.
     */
    default boolean isUnposted() {
        return false;
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

    /**
     * Check if this entry is created by the given foreign financial system.
     *
     * @param ffs Foreign financial system. If null, we are checking if it is created by the SO platform.
     * @return True if created by the given foreign financial system.
     */
    default boolean isCreatedBy(ForeignFinancialSystem ffs) {
        JournalVoucher jv = getVoucher();
        if(ffs == null) {
            return jv == null || Id.isNull(jv.getOriginId());
        }
        return jv != null && jv.getOriginId().equals(ffs.getId());
    }

    /**
     * Returns the reference of the entry.
     *
     * @return The reference.
     */
    default String getReference() {
        return null;
    }
}
