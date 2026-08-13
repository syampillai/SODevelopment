package com.storedobject.ui.accounts;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.helper.ID;
import com.storedobject.ui.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.ColumnTextAlign;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

public class JournalVoucherEditor extends ObjectEditor<JournalVoucher> {

    private final boolean multiEntity;
    private HasValue<?, Id> systemEntityField;
    private final Entries entries = new Entries();
    private final EntryForm entryForm = new EntryForm();

    public JournalVoucherEditor() {
        this(false);
    }

    public JournalVoucherEditor(boolean multiEntity) {
        super(JournalVoucher.class, EditorAction.NEW);
        this.multiEntity = multiEntity;
        addConstructedListener(e -> {
            add(entries);
            setColumnSpan(entries, 2);
        });
    }

    @Override
    protected void customizeField(String fieldName, HasValue<?, ?> field) {
        if("SystemEntity".equals(fieldName)) {
            //noinspection unchecked
            systemEntityField = (HasValue<?, Id>) field;
        }
        super.customizeField(fieldName, field);
    }

    @Override
    protected JournalVoucher createObjectInstance() {
        JournalVoucher jv = super.createObjectInstance();
        jv.setDate(getTransactionManager().getWorkingDate());
        jv.setOwner(jv);
        return jv;
    }

    @Override
    public void validateData() throws Exception {
        if(entries.isEmpty()) {
            throw new SOException("Journal voucher must have at least 2 entries");
        }
        if(!entries.getTotal().isZero()) {
            throw new SOException("Total of debit and credit amounts must be zero");
        }
        if(entries.size() == 2 && entries.get(0).account.equals(entries.get(1).account)) {
            throw new SOException("Same account is repeated");
        }
    }

    @Override
    protected void saveObject(Transaction t, JournalVoucher object) throws Exception {
        String type;
        for(Entry e : entries) {
            type = e.type == null ? null : e.type.getShortName();
            object.credit(e.account, e.fcAmount, e.lcAmount, type, e.particulars);
        }
        super.saveObject(t, object);
        if(getGrid() instanceof JournalVoucherBrowser b) {
            b.load();
            b.select(object);
        }
        entries.clear();
    }

    private class Entries extends ListGrid<Entry> {

        private final Button add, edit, delete, deleteAll;
        private GridRow.Cell total;

        public Entries() {
            super(Entry.class, StringList.create("Account", "Particulars", "Type", "FCAmount AS Amount", "LCAmount AS Amount in "
                    + getTransactionManager().getCurrency().getCurrencyCode()));
            setWidthFull();
            setMinHeight("200px");
            setMaxHeight("60vh");
            add = new Button("Add", e -> doAdd()).asSmall();
            edit = new Button("Edit", e -> doEdit()).asSmall();
            delete = new Button("Delete", e -> doDelete()).asSmall();
            deleteAll = new Button("Delete All", e -> clear()).asSmall();
        }

        @Override
        public ColumnTextAlign getTextAlign(String columnName) {
            return switch (columnName) {
                case "LCAmount", "FCAmount" -> ColumnTextAlign.END;
                default -> super.getTextAlign(columnName);
            };
        }

        @Override
        public int getRelativeColumnWidth(String columnName) {
            return switch (columnName) {
                case "Account" -> 35;
                case "Particulars" -> 50;
                case "LCAmount", "FCAmount" -> 15;
                default -> 10;
            };
        }

        @Override
        public Component createHeader() {
            return new ButtonLayout(add, edit, delete, deleteAll);
        }

        @Override
        public void createFooters() {
            GridRow gr = appendFooter();
            total = gr.getCell("LCAmount");
            updateTotal();
        }

        private void doAdd() {
            entryForm.setEntry(null);
            entryForm.account.clear();
            if(entries.isEmpty()) {
                entryForm.fcAmount.clear();
                entryForm.lcAmount.clear();
            } else {
                Money m = getTotal().negate();
                entryForm.debitCredit.setValue(m.isDebit() ? 0 : 1);
                m = m.absolute();
                entryForm.fcAmount.setValue(m);
                entryForm.lcAmount.setValue(m);
                entryForm.rate.setValue(Rate.ONE);
            }
            entryForm.systemEntityId = systemEntityField.getValue();
            entryForm.execute();
        }

        private void doEdit() {
            Entry e = selected();
            if(e == null) {
                return;
            }
            entryForm.setEntry(e);
            entryForm.systemEntityId = systemEntityField.getValue();
            entryForm.execute();
        }

        private void doDelete() {
            Entry e = selected();
            if(e == null) {
                return;
            }
            remove(e);
            updateTotal();
        }

        private Entry selected() {
            clearAlerts();
            Entry e = getSelected();
            if(e == null && size() == 1) {
                select(get(0));
                return getSelected();
            }
            if(e == null) {
                warning(isEmpty() ? "No entries" : "Please select an entry");
            }
            return e;
        }

        void updateTotal() {
            total.setText("Total: " + getTotal(), ColumnTextAlign.END);
        }

        private Money getTotal() {
            Money m = new Money();
            for (Entry entry : this) {
                m = m.add(entry.lcAmount);
            }
            return m;
        }

        @Override
        public void clear() {
            super.clear();
            updateTotal();
        }
    }

    static final class Entry {

        private final long id = ID.newID();
        private final Account account;
        private final String particulars;
        private final TransactionType type;
        private final Money lcAmount, fcAmount;

        Entry(Account account, String particulars, TransactionType type, Money lcAmount, Money fcAmount) {
            this.account = account;
            this.particulars = particulars;
            this.type = type;
            this.lcAmount = lcAmount;
            this.fcAmount = fcAmount;
        }

        public Account getAccount() {
            return account;
        }

        public String getParticulars() {
            return particulars;
        }

        public TransactionType getType() {
            return type;
        }

        public Money getLCAmount() {
            return lcAmount;
        }

        public Money getFCAmount() {
            return fcAmount;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Entry e && e.id == id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private class EntryForm extends DataForm {

        private Id systemEntityId = Id.ZERO;
        private final Currency localCurrency;
        private final AccountField<Account> account = new AccountField<>("Account");
        private final TextArea particulars = new TextArea("Particulars");
        private final ObjectField<TransactionType> type = new ObjectField<>("Transaction Type", TransactionType.class);
        private final ChoiceField debitCredit = new ChoiceField("Debit/Credit", "Debit, Credit");
        private final MoneyField fcAmount = new MoneyField("Amount");
        private final RateField rate = new RateField("Exchange Rate");
        private final MoneyField lcAmount = new MoneyField("Amount in Local Currency");
        private Entry entry;

        public EntryForm() {
            super("Journal Entry");
            if(!multiEntity) {
                account.setFilter(() -> "SystemEntity=" + systemEntityId, false);
            }
            type.setFilter("NOT Inactive", false);
            if(multiEntity) {
                type.setLoadFilter(t -> {
                    Account a = account.getAccount();
                    return t.isApplicableTo(a == null ? systemEntityId : a.getSystemEntityId());
                }, false);
            } else {
                type.setLoadFilter(t -> t.isApplicableTo(systemEntityId), false);
            }
            localCurrency = getTransactionManager().getCurrency();
            addField(account, particulars, type, debitCredit, fcAmount, rate, lcAmount);
            setRequired(account);
            setRequired(particulars);
            setRequired(rate);
            setRequired(fcAmount);
            setRequired(lcAmount);
            lcAmount.setAllowedCurrencies(localCurrency);
            fcAmount.addValueChangeListener(e -> {
                if(e.isFromClient()) {
                    computeLCA();
                }
            });
            rate.addValueChangeListener(e -> {
                if(e.isFromClient()) {
                    computeLCA();
                }
            });
            account.addValueChangeListener(e -> {
                if(e.isFromClient()) {
                    accountChanged();
                }
            });
        }

        private void accountChanged() {
            Account a = account.getAccount();
            if(a != null) {
                fcAmount.setAllowedCurrencies(a.getCurrency());
                if(a.getCurrency().equals(localCurrency)) {
                    rate.setValue(Rate.ONE);
                    setFieldVisible(false, rate, lcAmount);
                    fcAmount.setValue(lcAmount.getValue());
                } else {
                    setFieldVisible(true, rate, lcAmount);
                    Rate r = Money.getExchangeRate(localCurrency, a.getCurrency());
                    rate.setValue(r);
                    fcAmount.setValue(lcAmount.getValue().divide(r));
                }
            }
        }

        private void computeLCA() {
            lcAmount.setValue(fcAmount.getValue().multiply(rate.getValue()));
        }

        @Override
        protected boolean process() {
            if(entry == null) {
                entries.add(entry());
            } else {
                Entry e = entry();
                List<Entry> list = new ArrayList<>();
                entries.forEach(ee -> {
                    if(ee.equals(entry)) {
                        list.add(e);
                    } else {
                        list.add(ee);
                    }
                });
                entries.clear();
                entries.addAll(list);
            }
            entries.updateTotal();
            return true;
        }

        private Entry entry() {
            Money mFC = fcAmount.getValue(), mLC = lcAmount.getValue();
            if(debitCredit.getValue() == 0) {
                mFC = mFC.negate();
                mLC = mLC.negate();
            }
            return new Entry(account.getAccount(), particulars.getValue(), type.getObject(), mLC, mFC);
        }

        void setEntry(Entry entry) {
            this.entry = entry;
            if(entry == null) {
                return;
            }
            account.setValue(entry.account);
            particulars.setValue(entry.particulars);
            type.setValue(entry.type);
            debitCredit.setValue(entry.lcAmount.isNegative() ? 0 : 1);
            fcAmount.setAllowedCurrencies(entry.account.getCurrency());
            fcAmount.setValue(entry.fcAmount.absolute());
            lcAmount.setValue(entry.lcAmount.absolute());
            if(!entry.fcAmount.isZero()) {
                rate.setValue(new Rate(entry.lcAmount, entry.fcAmount));
            }
        }
    }
}
