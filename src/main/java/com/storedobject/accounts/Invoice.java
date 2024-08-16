package com.storedobject.accounts;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Currency;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Invoice extends StoredObject implements OfEntity, Financial, TradeType {

    private static final String[] paymentStatusValues =
            new String[] {
                    "Pending", "Partially Paid", "Settled",
            };
    private Id systemEntityId;
    private int type;
    private final Date date = DateUtility.today();
    private Id partyId;
    private Money amount = new Money(), total = new Money();
    private boolean posted;
    private Money payment = new Money();
    private String paymentDetail;
    private int paymentStatus = 0;
    private boolean fromInventory, internal = false;
    private Rate exchangeRate = new Rate(6);

    public Invoice() {}

    public static void columns(Columns columns) {
        columns.add("SystemEntity", "id");
        columns.add("Type", "int");
        columns.add("Date", "date");
        columns.add("Party", "id");
        columns.add("Amount", "money");
        columns.add("Total", "money");
        columns.add("Posted", "boolean");
        columns.add("Payment", "money");
        columns.add("PaymentDetail", "text");
        columns.add("PaymentStatus", "int");
        columns.add("FromInventory", "boolean");
        columns.add("ExchangeRate", "numeric(14,6)");
    }

    public static String[] protectedColumns() {
        return new String[] { "Type", "Payment" };
    }

    public static String[] browseColumns() {
        return new String[] {
                "Date", "InvoiceNo", "Party", "Amount", "Total", "Payment", "PaymentStatus", "Posted",
                "FromInventory", "PaymentDetail",
        };
    }

    public static void readOnlyColumns(ColumnNames columnNames) {
        columnNames.add("SystemEntity");
        columnNames.add("Posted");
        columnNames.add("Total");
        columnNames.add("PaymentStatus");
        columnNames.add("FromInventory");
    }

    public void setSystemEntity(Id systemEntityId) {
        if (!loading() && !Id.equals(this.getSystemEntityId(), systemEntityId)) {
            throw new Set_Not_Allowed("System Entity");
        }
        this.systemEntityId = systemEntityId;
    }

    public void setSystemEntity(BigDecimal idValue) {
        setSystemEntity(new Id(idValue));
    }

    public void setSystemEntity(SystemEntity systemEntity) {
        setSystemEntity(systemEntity == null ? null : systemEntity.getId());
    }

    @SetNotAllowed
    @Column(order = 100)
    public Id getSystemEntityId() {
        return systemEntityId;
    }

    public SystemEntity getSystemEntity() {
        return getRelated(SystemEntity.class, systemEntityId);
    }

    public void setType(int type) {
        if (!loading()) {
            throw new Set_Not_Allowed("Type");
        }
        this.type = type;
    }

    /**
     * Retrieves the type of the invoice.
     * <p>Note: The type should match with the PO type for proper linkage.</p>
     *
     * @return The type of the invoice as an integer.
     */
    @SetNotAllowed
    @Column(order = 150)
    public int getType() {
        return type;
    }

    public void setDate(Date date) {
        this.date.setTime(date.getTime());
    }

    @Column(order = 200)
    public Date getDate() {
        return new Date(date.getTime());
    }

    public void setParty(Id partyId) {
        this.partyId = partyId;
    }

    public void setParty(BigDecimal idValue) {
        setParty(new Id(idValue));
    }

    public void setParty(EntityAccount party) {
        setParty(party == null ? null : party.getId());
    }

    @Column(style = "(any)", order = 350, caption = "Party Account")
    public Id getPartyId() {
        return partyId;
    }

    public EntityAccount getParty() {
        return getRelated(EntityAccount.class, partyId, true);
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public void setAmount(Object moneyValue) {
        setAmount(Money.create(moneyValue));
    }

    @Column(order = 400)
    public Money getAmount() {
        return amount;
    }

    public void setTotal(Money total) {
        this.total = total;
    }

    public void setTotal(Object moneyValue) {
        setTotal(Money.create(moneyValue));
    }

    @Column(order = 500)
    public Money getTotal() {
        return total;
    }

    public void setPosted(boolean posted) {
        if (!loading()) {
            throw new Set_Not_Allowed("Posted");
        }
        this.posted = posted;
    }

    @SetNotAllowed
    @Column(order = 600)
    public boolean getPosted() {
        return posted;
    }

    public void setPayment(Money payment) {
        if (!loading()) {
            throw new Set_Not_Allowed("Payment");
        }
        this.payment = payment;
    }

    public void setPayment(Object moneyValue) {
        setPayment(Money.create(moneyValue));
    }

    @SetNotAllowed
    @Column(required = false, order = 700)
    public Money getPayment() {
        return payment;
    }

    public void setPaymentDetail(String paymentDetail) {
        this.paymentDetail = paymentDetail;
    }

    @Column(required = false, order = 800)
    public String getPaymentDetail() {
        return paymentDetail;
    }

    public void setPaymentStatus(int paymentStatus) {
        if (!loading()) {
            throw new Set_Not_Allowed("Payment Status");
        }
        this.paymentStatus = paymentStatus;
    }

    @SetNotAllowed
    @Column(order = 900)
    public int getPaymentStatus() {
        return paymentStatus;
    }

    public static String[] getPaymentStatusValues() {
        return paymentStatusValues;
    }

    public static String getPaymentStatusValue(int value) {
        String[] s = getPaymentStatusValues();
        return s[value % s.length];
    }

    public String getPaymentStatusValue() {
        return getPaymentStatusValue(paymentStatus);
    }

    public void setFromInventory(boolean fromInventory) {
        if(!loading()) {
            throw new Set_Not_Allowed("From Inventory");
        }
        this.fromInventory = fromInventory;
    }

    @SetNotAllowed
    @Column(order = 1000)
    public boolean getFromInventory() {
        return fromInventory;
    }

    public void setExchangeRate(Rate exchangeRate) {
        this.exchangeRate = new Rate(exchangeRate.getValue(), 6);
    }

    public void setExchangeRate(Object value) {
        setExchangeRate(Rate.create(value, 6));
    }

    @Column(style = "(d:14,6)", order = 1100)
    public Rate getExchangeRate() {
        return exchangeRate;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        systemEntityId = check(tm, systemEntityId);
        if (Utility.isEmpty(date)) {
            throw new Invalid_Value("Date");
        }
        partyId = tm.checkTypeAny(this, partyId, EntityAccount.class, false);
        validateAccount();
        if(total.getCurrency() == tm.getCurrency()) {
            exchangeRate = Rate.ONE;
        } else {
            exchangeRate.checkLimit("Exchange Rate", 14);
            if(exchangeRate.isZero()) {
                throw new Invalid_Value("Exchange Rate");
            }
        }
        super.validateData(tm);
    }

    @Override
    public void saved() throws Exception {
        internal = false;
    }

    @Override
    public void validateUpdate() throws Exception {
        super.validateUpdate();
        if(posted && !internal) {
            throw new Invalid_State("Can't update invoice - already posted");
        }
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        if(posted && !internal) {
            throw new Invalid_State("Can't delete invoice - already posted");
        }
    }

    private EntityAccount validateAccount() throws Exception {
        AccountConfiguration ac = getConfiguration();
        if(ac == null) {
            throw new Invalid_State("Account configuration not created");
        }
        EntityAccount ea = getParty();
        if(!ea.getSystemEntityId().equals(systemEntityId)) {
            throw new Invalid_Value("Party account doesn't belong to this organization");
        }
        int allow = ac.getAllow();
        if(ea instanceof CashAccount && (allow & 2) != 2) {
            throw new Invalid_State("Can't accept cash payment");
        } else if(ea instanceof AbstractCardAccount && (allow & 4) != 4) {
            throw new Invalid_State("Can't accept card payment");
        } else if(ea instanceof DigitalPaymentAccount && (allow & 8) != 8) {
            throw new Invalid_State("Can't accept digital payment");
        } else {
            if((allow & 1) == 0) {
                if(ac.getEntityAccountClass() == ea.getClass()) {
                    allow = 1;
                } else {
                    allow = 0;
                }
            } else {
                if(ac.getEntityAccountClass().isAssignableFrom(ea.getClass())) {
                    allow = 1;
                } else {
                    allow = 0;
                }
            }
            if(allow == 0) {
                throw new Invalid_State("Can't accept party accounts of type " + StringUtility.makeLabel(ea.getClass()));
            }
        }
        return ea;
    }

    public final AccountConfiguration getConfiguration() {
        return AccountConfiguration.getFor(systemEntityId, this instanceof SupplierInvoice ? 0 : 1, type);
    }

    @Override
    public final boolean isLedgerPosted() {
        return posted;
    }

    @Override
    public final void postLedger(TransactionManager transactionManager) throws Exception {
        if(posted) {
            throw new Invalid_State("Already posted");
        }
        if(created()) {
            throw new Invalid_State("New invoice");
        }
        Date wd = transactionManager.getWorkingDate();
        if(date.after(wd)) {
            throw new Invalid_State("Invoice date " + DateUtility.formatDate(date) + " > " + " working date "
                    + DateUtility.formatDate(wd));
        }
        AccountConfiguration ac = getConfiguration();
        if(ac == null) {
            throw new Invalid_State("Account configuration not created");
        }
        EntityAccount ea = validateAccount();
        AtomicReference<JournalVoucher> jvr = new AtomicReference<>();
        transactionManager.transact(t -> jvr.set(postLedger(ac, ea, wd, t)));
        JournalVoucher jv = jvr.get();
        if(jv != null && !DateUtility.isSameDate(jv.getDate(), date)) {
            jv.predateTransactions(transactionManager, date, "Invoice dated " + DateUtility.formatDate(date)
                    + " was posted on " + DateUtility.formatDate(DateUtility.today()));
        }
    }

    private JournalVoucher postLedger(AccountConfiguration ac, EntityAccount ea, Date wd, Transaction transaction)
            throws Exception {
        Account accountGL = ac.getAccount();
        Money m = total;
        Currency lc = accountGL.getCurrency();
        if(ea.getCurrency() != lc) {
            m = m.convert(exchangeRate.reverse(), lc);
        }
        if(this instanceof SupplierInvoice) {
            m = m.negate();
        }
        String particulars = particulars();
        JournalVoucher jv = new JournalVoucher();
        jv.setDate(wd);
        jv.setTransaction(transaction);
        jv.setOwner(this);
        if(ea.getCurrency() == lc) {
            jv.debit(ea, m, null, particulars);
        } else {
            jv.debit(ea, this instanceof SupplierInvoice ? total.negate() : total, m, null, particulars);
        }
        setTransaction(transaction);
        postTax(jv);
        jv.credit(accountGL, jv.getOffsetAmount(), null, particulars);
        posted = true;
        jv.save(transaction);
        internal = true;
        if(ea instanceof InstantaneousAccount) {
            payment = total;
            paymentStatus = 2;
        }
        jv.entries().forEach(e -> System.err.println(e.getAccount() + ": " + e.getAmount() + ", " + e.getLocalCurrencyAmount()));
        save(transaction);
        return jv;
    }

    protected void postTax(JournalVoucher journalVoucher) throws Exception {}

    protected String particulars() {
        return "Invoice " + getInvoiceNo();
    }

    public void addPayment(Money amount, TransactionManager tm) throws Exception {
        setPayment(payment.add(amount), tm);
    }

    public void addPayment(Money amount, Transaction transaction) throws Exception {
        setPayment(payment.add(amount), transaction);
    }

    public void setPayment(Money amount, TransactionManager tm) throws Exception {
        tm.transact(t -> setPayment(amount, t));
    }

    public void setPayment(Money amount, Transaction transaction) throws Exception {
        payment = amount;
        if(payment.isLessThan(total)) {
            paymentStatus = payment.isZero() ? 0 : 1;
        } else {
            paymentStatus = 2;
        }
        internal = true;
        save(transaction);
    }

    public abstract String getInvoiceNo();
}
