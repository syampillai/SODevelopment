package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * <p>Represents a Journal Voucher (JV). All financial transactions require a JV and this is the only way to create
 * financial ledger entries (referred as "entries" in this documentation) in the system.
 * A JV is owned by a {@link StoredObject} and that is the one creating
 * the entries. For example, an "Cash Sales Invoice" object may be creating a "Sales JV" by debiting the "cash
 * account" with the "invoice amount", crediting the "sales account" with the "items total" and crediting the
 * "tax account" with the "tax part" of the invoice.</p>
 * <p>A JV, once created can not be changed. The only way is to change any financial transaction is to
 * pass reversal entries via some reversal JVs. So, a "reversal JV" system needs to be designed separately for such
 * cases.</p>
 *
 * @author Syam
 */
public abstract class JournalVoucher extends StoredObject {

    private Id ownerId;
    private StoredObject owner;
    private Date date;

    /**
     * Constructor.
     */
    public JournalVoucher() {
    }

    /**
     * Column definitions.
     *
     * @param columns Column holder.
     */
    public static void columns(Columns columns) {
    }

    /**
     * Set the owner of this JV.
     *
     * @param ownerId Owner Id.
     */
    public void setOwner(Id ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Set the owner of this JV.
     *
     * @param idValue Owner.
     */
    public void setOwner(BigDecimal idValue) {
        setOwner(new Id(idValue));
    }

    /**
     * Set the owner of this JV.
     *
     * @param owner Owner.
     */
    public void setOwner(StoredObject owner) {
        setOwner(owner == null ? null : owner.getId());
    }

    /**
     * Get the owner Id.
     *
     * @return Id.
     */
    @Column(style = "(any)")
    public Id getOwnerId() {
        return ownerId;
    }

    /**
     * Get the owner.
     *
     * @return Owner.
     */
    public StoredObject getOwner() {
        if(owner == null) {
            owner = get(StoredObject.class, ownerId, true);
        }
        return owner;
    }

    /**
     * Override this method and generate all financial
     * transactions here. You may invoke debit() or credit() methods as many
     * times as needed to generate financial transactions.
     * However, please make sure that super.generateTransactions() is
     * called from the overridden method. Otherwise, a {@link Design_Error} will occur.
     * Also, please note that this method should not be invoked directly from any logic. It will be automatically
     * invoked by the system when the object is saved to the database.
     *
     * @exception Exception Transaction will be rolled back if any exception is thrown.
     */
    public void generateTransactions() throws Exception {
    }

    /**
     * Debit a local currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount.
     * @param entrySerial Entry serial.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, Money amount, int entrySerial, String particulars) throws Exception {
    }

    /**
     * Debit a local currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount.
     * @param entrySerial Entry serial.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, BigDecimal amount, int entrySerial, String particulars) throws Exception {
    }

    /**
     * Debit a foreign currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param entrySerial Entry serial.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, Money amount, Money localCurrencyAmount, int entrySerial, String particulars) throws Exception {
    }

    /**
     * Debit a foreign currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param entrySerial Entry serial.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, BigDecimal amount, BigDecimal localCurrencyAmount, int entrySerial, String particulars) throws Exception {
    }

    /**
     * Credit a local currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount.
     * @param entrySerial Entry serial.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, Money amount, int entrySerial, String particulars) throws Exception {
    }

    /**
     * Credit a local currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount.
     * @param entrySerial Entry serial.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, BigDecimal amount, int entrySerial, String particulars) throws Exception {
    }

    /**
     * Credit a foreign currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param entrySerial Entry serial.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, Money amount, Money localCurrencyAmount, int entrySerial, String particulars) throws Exception {
    }

    /**
     * Credit a foreign currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param entrySerial Entry serial.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, BigDecimal amount, BigDecimal localCurrencyAmount, int entrySerial, String particulars) throws Exception {
    }

    /**
     * Debit a local currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, Money amount, String particulars) throws Exception {
    }

    /**
     * Debit a local currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, BigDecimal amount, String particulars) throws Exception {
    }

    /**
     * Debit a foreign currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, Money amount, Money localCurrencyAmount, String particulars) throws Exception {
    }

    /**
     * Debit a foreign currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, BigDecimal amount, BigDecimal localCurrencyAmount, String particulars) throws Exception {
    }

    /**
     * Credit a local currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, Money amount, String particulars) throws Exception {
    }

    /**
     * Credit a local currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, BigDecimal amount, String particulars) throws Exception {
    }

    /**
     * Credit a foreign currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, Money amount, Money localCurrencyAmount, String particulars) throws Exception {
    }

    /**
     * Credit a foreign currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, BigDecimal amount, BigDecimal localCurrencyAmount, String particulars) throws Exception {
    }

    /**
     * Get the date of this JV.
     *
     * @return Date.
     */
    public Date getDate() {
        return (Date) date.clone();
    }

    /**
     * Set the date of this JV. (Should not call this method for a JV that is already saved).
     *
     * @param date Date to set.
     */
    public void setDate(Date date) {
    }

    /**
     * Get the entries of this JV. (This will be available only for JVs that are already saved to the DB).
     *
     * @return Stream of entries.
     */
    public Stream<Entry> entries() {
        return Stream.of();
    }

    /**
     * Get the entry count of this JV. (This will be available only for JVs that are already saved to the DB).
     *
     * @return Entry count.
     */
    public int getEntryCount() {
        return 0;
    }

    /**
     * Get the entry at a specific index. (This will be available only for JVs that are already saved to the DB).
     *
     * @param index Index of the entry.
     * @return Entry at the index (<code>null</code> will be returned for out-of-range index values).
     */
    public Entry getEntry(int index) {
        return new Entry();
    }

    /**
     * Get the list of all vouchers in this transaction. The first entry in the list will be this voucher itself.
     *
     * @return List of journal vouchers.
     */
    public List<JournalVoucher> getVouchers() {
        return new ArrayList<>();
    }

    /**
     * Represents a ledger entry of the JV.
     *
     * @author Syam
     */
    public static class Entry {

        private Entry() {
        }

        /**
         * Get the account of this entry.
         *
         * @return Account
         */
        public Account getAccount() {
            return new Account();
        }

        /**
         * Get the (foreign currency) amount of this entry.
         *
         * @return Amount.
         */
        public Money getAmount() {
            return new Money();
        }

        /**
         * Get the amount of this entry (in accounting currency).
         *
         * @return Amount.
         */
        public Money getLocalCurrencyAmount() {
            return new Money();
        }

        /**
         * Get the serial number of this entry in the JV.
         *
         * @return Serial number.
         */
        public int getEntrySerial() {
            return 0;
        }

        /**
         * Get the particulars/narration of this entry.
         *
         * @return Particulars/narration.
         */
        public String getParticulars() {
            return "";
        }
    }
}
