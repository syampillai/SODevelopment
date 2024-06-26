package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;
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
public class JournalVoucher extends StoredObject implements OfEntity, HasReference {

    private static final ReferencePattern<JournalVoucher> ref = new ReferencePattern<>();
    private static final Map<String, Id> types = new HashMap<>();
    private Id ownerId = Id.ZERO;
    private StoredObject owner;
    private Date date;
    private int entrySerial = 0;
    private final List<Entry> entries = new ArrayList<>();
    private Id systemEntityId, originId = Id.ZERO, ignoreFSId = Id.ZERO;
    int no = 0;
    private String reference, foreignReference = "";
    private DecimalNumber ledgerTran = DecimalNumber.zero(0);

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
        columns.add("SystemEntity", "id");
        columns.add("No", "int");
        columns.add("Owner", "id");
        columns.add("Date", "date");
        columns.add("LedgerTran", "numeric(30,0)");
        columns.add("Origin", "id");
        columns.add("ForeignReference", "text");
    }

    public static void indices(Indices indices) {
        indices.add("SystemEntity,No,T_Family", true);
        indices.add("SystemEntity,Date,No");
        indices.add("ForeignReference,Origin", "ForeignReference != ''", true);
    }

    public static String[] protectedColumns() {
        return new String[] { "No", "LedgerTran", "Origin", "ForeignReference" };
    }

    public static String[] browseColumns() {
        return new String[] {
                "Date",
                "Reference",
                "GeneratedBy",
                "OriginatedFrom"
        };
    }

    /**
     * Set the system entity of this JV.
     * @param systemEntityId System entity Id.
     */
    public void setSystemEntity(Id systemEntityId) {
        if(!loading()) {
            throw new Set_Not_Allowed("System Entity");
        }
        this.systemEntityId = systemEntityId;
    }

    /**
     * Set the system entity of this JV.
     *
     * @param idValue Id of the system entity.
     */
    public void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    /**
     * Set the system entity of this JV.
     *
     * @param systemEntity System entity.
     */
    public void setSystemEntity(SystemEntity systemEntity) {
        setSystemEntity(systemEntity == null ? null : systemEntity.getId());
    }

    /**
     * Get the system entity Id of this JV.
     *
     * @return System entity Id.
     */
    @SetNotAllowed
    @Column(order = 10, caption = "Of")
    public Id getSystemEntityId() {
        return systemEntityId;
    }

    /**
     * Get the system entity of this JV.
     * @return  System entity.
     */
    public SystemEntity getSystemEntity() {
        return SystemEntity.getCached(systemEntityId);
    }

    /**
     * Set the serial number of this JV.
     * @param no Serial number.
     */
    public void setNo(int no) {
        if (!loading()) {
            throw new Set_Not_Allowed("No");
        }
        this.no = no;
    }

    /**
     * Get the serial number of this JV.
     * @return Serial number.
     */
    @Override
    @SetNotAllowed
    @Column(style = "(serial)", order = 200)
    public int getNo() {
        if (no == 0) {
            Transaction t = getTransaction();
            no = SerialGenerator.generate(t, SerialConfigurator.getFor(getClass()).getYearPrefix(t)
                    + getTagPrefix() + ref.getTag(this)).intValue();
        }
        return no;
    }

    @Override
    public String getTagPrefix() {
        return "JV-";
    }

    /**
     * Get the reference of this JV.
     *
     * @return Reference.
     */
    @Override
    public final String getReference() {
        if(reference == null) {
            reference = ref.get(this);
        }
        return reference == null ? "" : reference;
    }

    /**
     * Get the owner who generated this JV.
     *
     * @return Name of the owner of this JV.
     */
    public String getGeneratedBy() {
        if(ownerId == null || ownerId.equals(getId())) {
            return "JV Creation";
        }
        return StringUtility.makeLabel(getOwner().getClass());
    }

    /**
     * Get the origin of this JV.
     *
     * @return Name the origin
     */
    public String getOriginatedFrom() {
        if(Id.isNull(originId)) {
            return "Current System";
        }
        return getOrigin().getName() + (StringUtility.isWhite(foreignReference) ? "" : ( " - " + foreignReference));
    }

    /**
     * Set the owner of this JV.
     *
     * @param ownerId Owner Id.
     */
    public void setOwner(Id ownerId) {
        if(!loading() || owner != null) {
            throw new Set_Not_Allowed("JV Owner");
        }
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
        if(owner == this) {
            if (this.owner == null) {
                this.owner = this;
                return;
            }
            throw new Set_Not_Allowed("JV Owner");
        }
        setOwner(owner == null ? null : owner.getId());
    }

    /**
     * Get the owner Id.
     *
     * @return Id.
     */
    @SetNotAllowed
    @Column(style = "(any)", order = 400)
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
            owner = Objects.equals(getId(), ownerId) ? this : get(StoredObject.class, ownerId, true);
        }
        return owner;
    }

    /**
     * Set the original transaction Id of this voucher for which ledger entries are created.
     * <p>Note: For internal use only.</p>
     *
     * @param ledgerTran An instance of the {@link DecimalNumber} that embeds the transaction Id.
     */
    public void setLedgerTran(DecimalNumber ledgerTran) {
        if (!loading()) {
            throw new Set_Not_Allowed("Ledger Tran");
        }
        this.ledgerTran = new DecimalNumber(ledgerTran.getValue(), 0);
    }


    /**
     * Set the original transaction Id of this voucher for which ledger entries are created.
     * <p>Note: For internal use only.</p>
     *
     * @param value Value that embeds the transaction Id.
     */
    public void setLedgerTran(Object value) {
        setLedgerTran(DecimalNumber.create(value, 0));
    }

    /**
     * Get the original transaction Id of this voucher for which ledger entries are created.
     *
     * @return An instance of the {@link DecimalNumber} that embeds the transaction Id.
     */
    @SetNotAllowed
    public DecimalNumber getLedgerTran() {
        return ledgerTran;
    }

    /**
     * Set the origin of this JV.
     * @param originId Id of the origin.
     */
    public void setOrigin(Id originId) {
        if(!loading()) {
            throw new Set_Not_Allowed("Origin");
        }
        this.originId = originId;
    }

    /**
     * Set the origin of this JV.
     * @param idValue Id of the origin.
     */
    public void setOrigin(BigDecimal idValue) {
        setOrigin(new Id(idValue));
    }

    /**
     * Set the origin of this JV.
     * @param foreignSystem Origin.
     */
    public void setOrigin(ForeignFinancialSystem foreignSystem) {
        setOrigin(foreignSystem == null ? Id.ZERO : foreignSystem.getId());
    }

    /**
     * Get the origin Id of this JV.
     * @return Origin Id.
     */
    @SetNotAllowed
    @Column(order = 200, required = false)
    public Id getOriginId() {
        return originId;
    }

    /**
     * Get the origin of this JV.
     * @return Origin.
     */
    public ForeignFinancialSystem getOrigin() {
        return Id.isNull(originId) ? null : getRelated(ForeignFinancialSystem.class, originId);
    }

    /**
     * Ignore a foreign system so that this JV will not be sent to that system.
     * @param ffs Foreign system to be ignored.
     */
    public void ignoreForeignSystem(ForeignFinancialSystem ffs) {
        ignoreFSId = ffs == null ? Id.ZERO : ffs.getId();
    }

    /**
     * Ignore a foreign system so that this JV will not be sent to that system.
     * @param ffsId Id of the foreign system to be ignored.
     */
    public void ignoreForeignSystem(Id ffsId) {
        ignoreFSId = ffsId == null ? Id.ZERO : ffsId;
    }

    /**
     * Set the foreign reference of this JV (to be used by the foreign system).
     *
     * @param foreignReference Reference.
     */
    public void setForeignReference(String foreignReference) {
        this.foreignReference = foreignReference.toUpperCase().strip();
    }

    /**
     * Get the foreign reference of this JV (to be used by the foreign system).
     * @return Reference.
     */
    public String getForeignReference() {
        return foreignReference;
    }

    /**
     * Get the offset amount of this JV. The offset amount is the amount that is required to balance this JV.
     *
     * @return Offset amount.
     */
    public Money getOffsetAmount() {
        if(entries.isEmpty()) {
            return new Money();
        }
        Money a = entries.get(0).amount.zero();
        for(Entry e: entries) {
            a = a.add(e.amount);
        }
        return a.negate();
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        SystemEntity se = tm.getEntity();
        if(se == null) {
            throw new Invalid_State("System not set up for any entity");
        }
        systemEntityId = check(tm, systemEntityId);
        if(owner == null && ownerId != null) {
            ownerId = tm.checkTypeAny(this, ownerId, StoredObject.class, false);
        }
        boolean dateSet = date != null;
        if(date == null) {
            date = se.getWorkingDate();
        } else if(date.after(se.getWorkingDate())) {
            throw new Invalid_State("Voucher date can't be greater than the working date - "
                    + DateUtility.formatDate(se.getWorkingDate()));
        }
        if(!(dateSet && DateUtility.isSameDate(date, se.getWorkingDate()))) {
            Date d = DateUtility.today();
            int n = DateUtility.getPeriodInDays(date, d);
            if (n > 0 && !GlobalProperty.getBoolean(se, "ALLOW-OLD-VOUCHERS")) {
                throw new Invalid_State("Working date of '" + se.getName() + "' - "
                        + DateUtility.formatDate(se.getWorkingDate()) + " - seems to be wrong. Today is "
                        + DateUtility.formatDate(d) + " and the voucher date is " + DateUtility.formatDate(date) + ".");
            }
        }
        originId = tm.checkType(this, originId, ForeignFinancialSystem.class, true);
        super.validateData(tm);
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        if(inserted()) {
            ledgerTran = new DecimalNumber(new BigDecimal(getTransactionId().get()));
        }
        if(deleted() || undeleted()) {
            throw new SOException("Not allowed");
        }
        if(owner != null) {
            ownerId = owner.getId();
        }
        if(ownerId == null) {
            ownerId = getId();
        }
        if(ownerId == null) {
            throw new Invalid_Value("Owner");
        }
    }

    @Override
    public void validateUpdate() throws Exception {
        JournalVoucher jv = get(getClass(), getId());
        if(!jv.date.equals(date)) {
            throw new Invalid_State("Date can't be updated");
        }
        super.validateUpdate();
    }

    @Override
    void savedCore() throws Exception {
        super.savedCore();
        if(!inserted()) {
            return;
        }
        Transaction t = getTransaction();
        boolean found = false;
        for(ForeignFinancialSystem ffs: list(ForeignFinancialSystem.class, "Active")) {
            if(ffs.getId().equals(originId)) {
                found = ffs.getActive();
                continue;
            }
            if(ffs.getId().equals(ignoreFSId)) {
                continue;
            }
            JournalVoucherStage jvs = new JournalVoucherStage();
            jvs.setVoucher(this);
            jvs.setForeignSystem(ffs);
            jvs.save(t);
        }
        if(!found && !Id.isNull(originId)) {
            throw new Invalid_State("Origin is not active");
        }
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
        debit(account, amount, entrySerial, type, particulars, null);
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
        debit(account, amount, entrySerial, type, particulars, null);
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
        debit(account, amount, localCurrencyAmount, entrySerial, type, particulars, null);
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
        debit(account, amount, localCurrencyAmount, entrySerial, type, particulars, null);
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
        credit(account, amount, localCurrencyAmount, entrySerial, type, particulars, null);
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
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void credit(Account account, Money amount, Money localCurrencyAmount, int entrySerial,
                             String type, String particulars, Date valueDate) throws Exception {
        if(account instanceof AccountTitle at) {
            account = at.getAccount();
        }
        if(isVirtual() || account.isVirtual()) {
            throw new SOException("Virtual instance");
        }
        if(getId() != null) {
            throw new SOException("JV was already saved");
        }
        if(account instanceof BranchAccount) {
            throw new SOException("Branch Account - " + account.toDisplay());
        }
        if(localCurrencyAmount == null) {
            localCurrencyAmount = amount;
        }
        if(amount == null || localCurrencyAmount.isZero()) {
            throw new SOException("Amount is zero");
        }
        Currency c = account.getCurrency(), lc = account.getLocalCurrency();
        if(c == lc) {
            if(!amount.equals(localCurrencyAmount)) {
                throw new SOException("Local currency amount mismatch: " + amount + ", " + localCurrencyAmount);
            }
        } else {
            if(c != amount.getCurrency()) {
                throw new SOException("Currency amount mismatch: " + amount + ", Expected currency: "
                        + Money.getSymbol(c));
            }
        }
        if(StringUtility.isWhite(particulars)) {
            throw new SOException("Transaction narration/particulars can't be empty");
        }
        if(entrySerial >= 1000000000) { // Conflict with IB transactions (Check in DBTransaction class)
            throw new SOException("Entry serial should be less than 1000000000");
        }
        creditInt(account, amount, localCurrencyAmount, entrySerial, typeId(type), particulars, valueDate);
    }

    private void creditInt(Account account, Money amount, Money localCurrencyAmount, int entrySerial, Id type,
                           String particulars, Date valueDate) {
        if(entrySerial <= 0) {
            entrySerial = this.entrySerial + 1;
        }
        this.entrySerial = entrySerial;
        entries.add(new Entry(this, account, amount, localCurrencyAmount, entrySerial,
                type, particulars, valueDate));
        account.addBalance(amount);
        account.addLocalCurrencyBalance(localCurrencyAmount);
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
     * Debit a local currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount.
     * @param entrySerial Entry serial.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void debit(Account account, Money amount, int entrySerial, String type, String particulars,
                            Date valueDate)
            throws Exception {
        Money r = amount.negate();
        credit(account, r, r, entrySerial, type, particulars, valueDate);
    }

    /**
     * Debit a local currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount.
     * @param entrySerial Entry serial.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void debit(Account account, BigDecimal amount, int entrySerial, String type, String particulars,
                            Date valueDate) throws Exception {
        BigDecimal r = amount.negate();
        credit(account, r, r, entrySerial, type, particulars, valueDate);
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
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void debit(Account account, Money amount, Money localCurrencyAmount, int entrySerial,
                            String type, String particulars, Date valueDate) throws Exception {
        credit(account, amount.negate(), localCurrencyAmount.negate(), entrySerial, type, particulars, valueDate);
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
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void debit(Account account, BigDecimal amount, BigDecimal localCurrencyAmount, int entrySerial,
                            String type, String particulars, Date valueDate) throws Exception {
        credit(account, amount.negate(), localCurrencyAmount.negate(), entrySerial, type, particulars, valueDate);
    }

    /**
     * Credit a local currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount.
     * @param entrySerial Entry serial.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void credit(Account account, Money amount, int entrySerial, String type, String particulars,
                             Date valueDate) throws Exception {
        credit(account, amount, amount, entrySerial, type, particulars, valueDate);
    }

    /**
     * Credit a local currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount.
     * @param entrySerial Entry serial.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void credit(Account account, BigDecimal amount, int entrySerial, String type, String particulars,
                             Date valueDate) throws Exception {
        credit(account, amount, amount, entrySerial, type, particulars, valueDate);
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
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void credit(Account account, BigDecimal amount, BigDecimal localCurrencyAmount, int entrySerial,
                             String type, String particulars, Date valueDate) throws Exception {
        credit(account, account.createAmount(amount), account.createLocalCurrencyAmount(localCurrencyAmount),
                entrySerial, type, particulars, valueDate);
    }

    /**
     * Debit a local currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void debit(Account account, Money amount, String type, String particulars, Date valueDate)
            throws Exception {
        debit(account, amount, 0, type, particulars, valueDate);
    }

    /**
     * Debit a local currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void debit(Account account, BigDecimal amount, String type, String particulars, Date valueDate)
            throws Exception {
        debit(account, amount, 0, type, particulars, valueDate);
    }

    /**
     * Debit a foreign currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void debit(Account account, Money amount, Money localCurrencyAmount, String type, String particulars,
                            Date valueDate) throws Exception {
        debit(account, amount, localCurrencyAmount, 0, type, particulars, valueDate);
    }

    /**
     * Debit a foreign currency account.
     *
     * @param account Account to be debited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void debit(Account account, BigDecimal amount, BigDecimal localCurrencyAmount, String type,
                            String particulars, Date valueDate) throws Exception {
        debit(account, amount, localCurrencyAmount, 0, type, particulars, valueDate);
    }

    /**
     * Credit a local currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void credit(Account account, Money amount, String type, String particulars, Date valueDate)
            throws Exception {
        credit(account, amount, 0, type, particulars, valueDate);
    }

    /**
     * Credit a local currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void credit(Account account, BigDecimal amount, String type, String particulars, Date valueDate)
            throws Exception {
        credit(account, amount, amount, 0, type, particulars, valueDate);
    }

    /**
     * Credit a foreign currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void credit(Account account, Money amount, Money localCurrencyAmount, String type, String particulars,
                             Date valueDate) throws Exception {
        credit(account, amount, localCurrencyAmount, 0, type, particulars, valueDate);
    }

    /**
     * Credit a foreign currency account.
     *
     * @param account Account to be credited.
     * @param amount Amount in account currency.
     * @param localCurrencyAmount Amount in local currency.
     * @param type Transaction type (As defined in {@link TransactionType}).
     * @param particulars Particulars (narration) of the transaction entry. (Can not be empty or <code>null</code>).
     * @param valueDate Value-date.
     * @throws Exception Any exception.
     */
    public final void credit(Account account, BigDecimal amount, BigDecimal localCurrencyAmount, String type,
                             String particulars, Date valueDate) throws Exception {
        credit(account, amount, localCurrencyAmount, 0, type, particulars, valueDate);
    }

    private Id typeId(String type) throws Invalid_State {
        if(type == null || type.isBlank()) {
            return Id.ZERO;
        }
        type = StoredObject.toCode(type);
        Id tid = types.get(type);
        if(tid == null) {
            TransactionType tt = TransactionType.getFor(type);
            if(tt == null) {
                throw new Invalid_State("Unknown transaction type: " + type);
            }
            tid = tt.getId();
            types.put(type, tid);
        }
        return tid;
    }

    /**
     * Get the date of this JV.
     *
     * @return Date.
     */
    @Override
    @SetNotAllowed
    @Column(order = 300)
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
        return entries.stream();
    }

    /**
     * Get the entry count of this JV. (This will be available only for JVs that are already saved to the DB).
     *
     * @return Entry count.
     */
    public int getEntryCount() {
        return entries.size();
    }

    /**
     * Get the entry at a specific index. (This will be available only for JVs that are already saved to the DB).
     *
     * @param index Index of the entry.
     * @return Entry at the index (<code>null</code> will be returned for out-of-range index values).
     */
    public Entry getEntry(int index) {
        return index >= 0 && index < entries.size() ? entries.get(index) : null;
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
    public final static class Entry {

        private static final AtomicInteger ID = new AtomicInteger(0);
        private final int id = ID.incrementAndGet();
        final JournalVoucher journalVoucher;
        final Account account;
        final Money amount;
        final Money localCurrencyAmount;
        final int entrySerial;
        final Id type;
        final String particulars;
        Date date, valueDate;
        private String extraData;

        Entry(JournalVoucher journalVoucher, Account account, Money amount, Money localCurrencyAmount,
              int entrySerial, Id type, String particulars, Date valueDate) {
            this.journalVoucher = journalVoucher;
            this.account = account;
            this.amount = amount;
            this.localCurrencyAmount = localCurrencyAmount;
            this.entrySerial = entrySerial;
            this.type = type;
            this.particulars = particulars;
            this.date = journalVoucher.date;
            this.valueDate = valueDate == null ? new Date(Utility.BLANK_TIME) : valueDate;
        }

        private Entry(JournalVoucher journalVoucher, Account account, Money amount, Money localCurrencyAmount,
                      int entrySerial, Id type, String particulars, Date valueDate, String extraData) {
            this(journalVoucher, account, amount, localCurrencyAmount, entrySerial, type, particulars, valueDate);
            setExtraData(extraData);
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

        /**
         * Get transaction type.
         *
         * @return Transaction type.
         */
        public TransactionType getType() {
            return Id.isNull(type) ? null : get(TransactionType.class, type);
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return account.toDisplay() + " [" + account.getSystemEntity().getName() + "]"
                    + ", Amount " + amount + ", LC Amount " + localCurrencyAmount + ", " + particulars;
        }

        /**
         * Sets the extra data for this entry.
         *
         * @param extraData The extra data to be set.
         */
        public void setExtraData(String  extraData) {
            this.extraData = extraData;
        }

        /**
         * Returns the extra data associated with this entry.
         *
         * @return The extra data as an Object.
         */
        public String getExtraData() {
            return extraData;
        }

        /**
         * Get the voucher associated with this entry.
         *
         * @return Associated journal voucher.
         */
        public JournalVoucher getVoucher() {
            return journalVoucher;
        }

        /**
         * Get the value-date of this entry.
         *
         * @return Value-date.
         */
        public Date getValueDate() {
            return Utility.isEmpty(valueDate) ? null : valueDate;
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
        if(Math.random() > 0.5) {
            throw new Exception();
        }
    }

    @Override
    public String toDisplay() {
        return DateUtility.formatDate(date) + ", " + getReference() + ", From: " + getGeneratedBy();
    }

    @Override
    public String toString() {
        return toDisplay();
    }

    /**
     * Save this JV as an unposted journal.
     *
     * @param tm Transaction manager.
     * @return Instance of the {@link UnpostedJournal} created.
     * @throws Exception If any error occurs.
     */
    public UnpostedJournal saveAsUnposted(TransactionManager tm) throws Exception {
        if(Math.random() > 0.5) {
            throw new Exception();
        }
        return new UnpostedJournal();
    }

    /**
     * Create an instance of the {@link JournalVoucher} from the given {@link UnpostedJournal}.
     *
     * @param unpostedJournal Unposted journal.
     * @return JV created.
     * @throws Exception If any error occurs.
     */
    public static JournalVoucher createFrom(UnpostedJournal unpostedJournal) throws Exception {
        JournalVoucher jv = (JournalVoucher) JavaClassLoader.getLogic(unpostedJournal.getJVClassName())
                .getConstructor().newInstance();
        if(jv.getClass() != JournalVoucher.class) {
            jv.load(new LineNumberReader(new StringReader(unpostedJournal.getExtraInformation())));
        }
        Account a;
        Money amount, lcAmount;
        for(UnpostedJournalEntry e: unpostedJournal.listLinks(UnpostedJournalEntry.class, null, "DisplayOrder")) {
            a = e.getAccount();
            amount = e.getAmount();
            lcAmount = e.getLocalCurrencyAmount();
            jv.entries.add(new Entry(jv, a, amount, lcAmount, e.getEntrySerial(), e.getTypeId(), e.getParticulars(),
                    e.getValueDate(), e.getExtraData()));
            jv.entrySerial = e.getEntrySerial();
            a.addBalance(amount);
            a.addLocalCurrencyBalance(lcAmount);
        }
        return jv;
    }
}
