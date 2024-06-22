package com.storedobject.ui.accounts;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.helper.ID;
import com.storedobject.ui.MoneyField;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JournalVoucherEditor extends ObjectEditor<JournalVoucher> {

    private final Entries entries = new Entries();
    private final EntryForm entryForm = new EntryForm();

    public JournalVoucherEditor() {
        super(JournalVoucher.class, EditorAction.NEW);
        addConstructedListener(e -> {
            add(entries);
            setColumnSpan(entries, 2);
        });
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
            throw new SOException("Same account is repeatedly");
        }
    }

    @Override
    protected void saveObject(Transaction t, JournalVoucher object) throws Exception {
        for(Entry e : entries) {
            object.credit(e.account, e.amount, null, e.particulars);
        }
        super.saveObject(t, object);
    }

    private class Entries extends ListGrid<Entry> {

        private final Button add, edit, delete;
        private GridRow.Cell total;

        public Entries() {
            super(Entry.class, StringList.create("Account", "Particulars", "Amount"));
            setWidthFull();
            setMinHeight("200px");
            setMaxHeight("60vh");
            add = new Button("Add", e -> doAdd()).asSmall();
            edit = new Button("Edit", e -> doEdit()).asSmall();
            delete = new Button("Delete", e -> doDelete()).asSmall();
        }

        @Override
        public ColumnTextAlign getTextAlign(String columnName) {
            if("Amount".equals(columnName)) {
                return ColumnTextAlign.END;
            }
            return super.getTextAlign(columnName);
        }

        @Override
        public int getRelativeColumnWidth(String columnName) {
            return switch (columnName) {
                case "Account" -> 35;
                case "Particulars" -> 50;
                case "Amount" -> 15;
                default -> 10;
            };
        }

        @Override
        public Component createHeader() {
            return new ButtonLayout(add, edit, delete);
        }

        @Override
        public void createFooters() {
            GridRow gr = appendFooter();
            total = gr.getCell("Amount");
            updateTotal();
        }

        private void doAdd() {
            entryForm.setEntry(null);
            if(entries.isEmpty()) {
                entryForm.amount.clear();
            } else {
                Money m = getTotal().negate();
                entryForm.amount.setValue(m.absolute());
                entryForm.debitCredit.setValue(m.isDebit() ? 0 : 1);
            }
            entryForm.execute();
        }

        private void doEdit() {
            Entry e = selected();
            if(e == null) {
                return;
            }
            entryForm.setEntry(e);
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
                m = m.add(entry.amount);
            }
            return m;
        }
    }

    static final class Entry {

        private final long id = ID.newID();
        private final Account account;
        private final String particulars;
        private final Money amount;

        Entry(Account account, String particulars, Money amount) {
            this.account = account;
            this.particulars = particulars;
            this.amount = amount;
        }

        public Account getAccount() {
            return account;
        }

        public String getParticulars() {
            return particulars;
        }

        public Money getAmount() {
            return amount;
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

        private final AccountField<Account> account = new AccountField<>("Account");
        private final TextArea particulars = new TextArea("Particulars");
        private final ChoiceField debitCredit = new ChoiceField("Debit/Credit", "Debit, Credit");
        private final MoneyField amount = new MoneyField("Amount");
        private Entry entry;

        public EntryForm() {
            super("Journal Entry");
            addField(account, particulars, debitCredit, amount);
            setRequired(account);
            setRequired(particulars);
            setRequired(amount);
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
            Money m = amount.getValue();
            if(debitCredit.getValue() == 0) {
                m = m.negate();
            }
            return new Entry(account.getAccount(), particulars.getValue(), m);
        }

        void setEntry(Entry entry) {
            this.entry = entry;
            if(entry == null) {
                return;
            }
            account.setValue(entry.account);
            particulars.setValue(entry.particulars);
            debitCredit.setValue(entry.amount.isNegative() ? 0 : 1);
            amount.setValue(entry.amount.absolute());
        }
    }
}
