package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * <p>Represents a Journal Voucher (JV). All financial transactions require a JV and this is the only way to create
 * financial ledger entries (referred as "entries" in this documentation) in the system.
 * A JV is owned by an instanceof a {@link StoredObject} and that is the one creating
 * the entries. For example, an "Cash Sales Invoice" object may be creating a "Sales JV" by debiting the "cash
 * account" with the "invoice amount", crediting the "sales account" with the "items total" and crediting the
 * "tax account" with the "tax part" of the invoice.</p>
 * <p>JV entries, once created can not be changed. The only way to change any financial transaction is to
 * pass reversal entries via some reversal JVs. So, a "reversal JV" system needs to be designed separately for such
 * cases.</p>
 *
 * @author Syam
 */
public class JournalVoucher extends StoredObject {

    private Id ownerId;
    private StoredObject owner;
    private Date date;
    private int stage = 0;

    /**
     * Constructor.
     */
    public JournalVoucher() {
        this(null);
    }

    /**
     * Constructor.
     * @param owner Owner.
     */
    public JournalVoucher(StoredObject owner) {
        this.owner = owner;
    }

    /**
     * Column definitions.
     *
     * @param columns Column holder. Column definitions to be added to this.
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
     * @param owner Owner.
     */
    public void setOwner(StoredObject owner) {
        this.owner = owner;
    }

    /**
     * Get the owner Id.
     *
     * @return Id.
     */
    @SetNotAllowed
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
        return owner;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getStage() {
        return stage;
    }

    /**
     * Debit a local currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount.
     * @param entrySerial Entry serial.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, Money amount, int entrySerial, String type, String particulars)
            throws Exception {
        Money r = amount.negate();
        credit(account, r, r, entrySerial, type, particulars);
    }

    /**
     * Debit a local currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount.
     * @param entrySerial Entry serial.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, BigDecimal amount, int entrySerial, String type, String particulars)
            throws Exception {
        BigDecimal r = amount.negate();
        credit(account, r, r, entrySerial, type, particulars);
    }

    /**
     * Debit a foreign currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param entrySerial Entry serial.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, Money amount, Money localCurrencyAmount, int entrySerial,
                            String type, String particulars) throws Exception {
        credit(account, amount.negate(), localCurrencyAmount.negate(), entrySerial, type, particulars);
    }

    /**
     * Debit a foreign currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param entrySerial Entry serial.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, BigDecimal amount, BigDecimal localCurrencyAmount, int entrySerial,
                            String type, String particulars) throws Exception {
        credit(account, amount.negate(), localCurrencyAmount.negate(), entrySerial, type, particulars);
    }

    /**
     * Credit a local currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount.
     * @param entrySerial Entry serial.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, Money amount, int entrySerial, String type, String particulars)
            throws Exception {
        credit(account, amount, amount, entrySerial, type, particulars);
    }

    /**
     * Credit a local currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount.
     * @param entrySerial Entry serial.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, BigDecimal amount, int entrySerial, String type, String particulars)
            throws Exception {
        credit(account, amount, amount, entrySerial, type, particulars);
    }

    /**
     * Credit a foreign currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param entrySerial Entry serial.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, Money amount, Money localCurrencyAmount, int entrySerial,
                             String type, String particulars) throws Exception {
        if(isVirtual() || account.isVirtual()) {
            throw new SOException("Virtual instance");
        }
    }

    /**
     * Credit a foreign currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param entrySerial Entry serial.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, BigDecimal amount, BigDecimal localCurrencyAmount, int entrySerial,
                             String type, String particulars) throws Exception {
        credit(account, account.createAmount(amount), account.createLocalCurrencyAmount(localCurrencyAmount),
                entrySerial, type, particulars);
    }

    /**
     * Debit a local currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, Money amount, String type, String particulars) throws Exception {
        debit(account, amount, 0, type, particulars);
    }

    /**
     * Debit a local currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, BigDecimal amount, String type, String particulars) throws Exception {
        debit(account, amount, 0, type, particulars);
    }

    /**
     * Debit a foreign currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, Money amount, Money localCurrencyAmount, String type, String particulars)
            throws Exception {
        debit(account, amount, localCurrencyAmount, 0, type, particulars);
    }

    /**
     * Debit a foreign currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void debit(Account account, BigDecimal amount, BigDecimal localCurrencyAmount, String type,
                            String particulars) throws Exception {
        debit(account, amount, localCurrencyAmount, 0, type, particulars);
    }

    /**
     * Credit a local currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, Money amount, String type, String particulars) throws Exception {
        credit(account, amount, 0, type, particulars);
    }

    /**
     * Credit a local currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, BigDecimal amount, String type, String particulars) throws Exception {
        credit(account, amount, amount, 0, type, particulars);
    }

    /**
     * Credit a foreign currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, Money amount, Money localCurrencyAmount, String type, String particulars)
            throws Exception {
        credit(account, amount, localCurrencyAmount, 0, type, particulars);
    }

    /**
     * Credit a foreign currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @throws Exception Any exception.
     */
    public final void credit(Account account, BigDecimal amount, BigDecimal localCurrencyAmount, String type,
                             String particulars) throws Exception {
        credit(account, amount, localCurrencyAmount, 0, type, particulars);
    }

    /**
     * Get the date of this JV.
     *
     * @return Date.
     */
    @SetNotAllowed
    public Date getDate() {
        return date == null ? null : new Date(date.getTime());
    }

    /**
     * Set the date of this JV. (Should not call this method for a JV that is already saved).
     *
     * @param date Date to set.
     */
    public void setDate(Date date) {
        if(loading()) {
            this.date = date == null ? null : new Date(date.getTime());
            return;
        }
        throw new Set_Not_Allowed("Date");
    }

    /**
     * Get the entries of this JV. (This will be available only for JVs that are already saved to the DB).
     *
     * @return Stream of entries.
     */
    public Stream<Entry> entries() {
        return Stream.empty();
    }

    /**
     * Get the entry count of this JV. (This will be available only for JVs that are already saved to the DB).
     *
     * @return Entry count.
     */
    public int getEntryCount() {
        return new Random().nextInt();
    }

    /**
     * Get the entry at a specific index. (This will be available only for JVs that are already saved to the DB).
     *
     * @param index Index of the entry.
     * @return Entry at the index (<code>null</code> will be returned for out-of-range index values).
     */
    public Entry getEntry(int index) {
        return new Random().nextBoolean() ? null :
                new Entry(this,null, null, null, 0, null, null);
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

        private static final AtomicInteger ID = new AtomicInteger(0);
        private final int id = ID.incrementAndGet();
        private final JournalVoucher journalVoucher;
        private final Account account;
        private final Money amount;
        private final Money localCurrencyAmount;
        private final int entrySerial;
        private final Id type;
        private final String particulars;

        private Entry(JournalVoucher journalVoucher, Account account, Money amount, Money localCurrencyAmount,
                      int entrySerial, Id type, String particulars) {
            this.journalVoucher = journalVoucher;
            this.account = account;
            this.amount = amount;
            this.localCurrencyAmount = localCurrencyAmount;
            this.entrySerial = entrySerial;
            this.type = type;
            this.particulars = particulars;
        }

        /**
         * Get the account of this entry.
         *
         * @return Account
         */
        public Account getAccount() {
            return account;
        }

        /**
         * Get the (foreign currency) amount of this entry.
         *
         * @return Amount.
         */
        public Money getAmount() {
            return amount;
        }

        /**
         * Get the amount of this entry (in accounting currency).
         *
         * @return Amount.
         */
        public Money getLocalCurrencyAmount() {
            return localCurrencyAmount;
        }

        /**
         * Get the serial number of this entry in the JV.
         *
         * @return Serial number.
         */
        public int getEntrySerial() {
            return entrySerial;
        }

        /**
         * Get the particulars/narration of this entry.
         *
         * @return Particulars/narration.
         */
        public String getParticulars() {
            return particulars;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this;
        }

        @Override
        public int hashCode() {
            return id;
        }

        /**
         * Get transaction type.
         *
         * @return Transaction type.
         */
        public TransactionType getType() {
            return get(TransactionType.class, type);
        }

        @Override
        public String toString() {
            return account.toDisplay() + " [" + account.getSystemEntity().getName() + "]"
                    + ", Amount " + amount + ", LC Amount " + localCurrencyAmount + ", " + particulars;
        }

        /**
         * Get the voucher associated with this entry.
         *
         * @return Associated journal voucher.
         */
        public JournalVoucher getVoucher() {
            return journalVoucher;
        }
    }


    /**
     * Allow excess in an account participating in this transaction. (The user should have the authority to set this).
     *
     * @param account Account.
     * @param excess Excess amount (on top of the allowed limit).
     */
    public void allowExcess(Account account, Money excess) {
    }

    /**
     * Change the date of the associated transactions.
     * <p>Note: This method is useful only for data pick up of old transaction during implementation.</p>
     * @param transactionManager Transaction manager.
     * @param date New date.
     * @exception Exception if changed can't be carried out.
     */
    public void predateTransactions(TransactionManager transactionManager, Date date, String remarks) throws Exception {
        if(new Random().nextBoolean()) {
            throw new Exception();
        }
    }
}
