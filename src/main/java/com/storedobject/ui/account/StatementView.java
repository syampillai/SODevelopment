package com.storedobject.ui.account;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.sql.Date;
import java.util.List;

public class StatementView extends ListGrid<LedgerEntry> implements CloseableView {

    private static final String NO_ENTRIES = "No entries";
    private final ObjectField<Account> accountField = new ObjectField<>(Account.class, true);
    private final DateField dateField = new DateField();
    private final Button forward, backward, begin, end, voucher;
    private final LedgerWindow ledger = new LedgerWindow(60, this);
    private Account account;
    private final ELabel openingBalance = new ELabel(NO_ENTRIES);
    private JournalVoucherView voucherView;

    public StatementView() {
        super(LedgerEntry.class, StringList.create("Date", "Particulars", "Debit", "Credit", "Balance"));
        setCaption("Statement View");
        forward = new Button("Next", this);
        backward = new Button("Previous", this);
        begin = new Button("First Page", VaadinIcon.SIGN_IN_ALT, this);
        end = new Button("Last Page", VaadinIcon.SIGN_OUT_ALT, this);
        voucher = new Button("Voucher", VaadinIcon.FILE_TEXT_O, this);
        trackValueChange(dateField);
        trackValueChange(accountField);
        new Box(openingBalance);
    }

    public StatementView(Account account) {
        this();
        if(account != null) {
            accountField.setValue(account);
        }
    }

    @Override
    public Component createHeader() {
        Div h = new Div();
        ButtonLayout b = new ButtonLayout();
        b.add(new ELabel("Account:"), accountField);
        h.add(b);
        b = new ButtonLayout();
        b.add(new ELabel("From Date:"), dateField, forward, backward, begin, end, voucher);
        b.addFiller();
        b.add(openingBalance);
        forward.setEnabled(false);
        backward.setEnabled(false);
        begin.setEnabled(false);
        end.setEnabled(false);
        voucher.setEnabled(false);
        h.add(b);
        return h;
    }

    @Override
    public ColumnTextAlign getTextAlign(String columnName) {
        if("Debit".equals(columnName) || "Credit".equals(columnName) || "Balance".equals(columnName)) {
            return ColumnTextAlign.END;
        }
        return super.getTextAlign(columnName);
    }

    @Override
    public boolean isColumnSortable(String columnName) {
        return false;
    }

    public String getDebit(LedgerEntry le) {
        Money a = le.getAmount();
        if(a.isDebit()) {
            return a.negate().toString(false);
        }
        return "";
    }

    public String getCredit(LedgerEntry le) {
        Money a = le.getAmount();
        if(a.isCredit()) {
            return a.toString(false);
        }
        return "";
    }

    public String getBalance(LedgerEntry le) {
        Money b = le.getBalance();
        if(b.isDebit()) {
            return "DB " + b.negate().toString(false);
        }
        return b.toString(false);
    }

    @Override
    public void clicked(Component c) {
        if(c == forward) {
            if(ledger.moveForward()) {
                begin.setEnabled(true);
                setOB();
            } else {
                forward.setEnabled(false);
            }
            return;
        }
        if(c == backward) {
            if(ledger.moveBackward()) {
                end.setEnabled(true);
                setOB();
            } else {
                backward.setEnabled(false);
            }
            return;
        }
        if(c == begin) {
            ledger.moveToBeginning();
            begin.setEnabled(false);
            setOB();
            return;
        }
        if(c == end) {
            ledger.moveToEnd();
            end.setEnabled(false);
            setOB();
            return;
        }
        if(c == voucher) {
            LedgerEntry le = getSelected();
            if(le == null) {
                message("Please select an entry");
                return;
            }
            List<JournalVoucher> jv = le.getVouchers();
            if(jv.isEmpty()) {
                warning("Voucher for this entry is missing!");
                return;
            }
            if(voucherView == null) {
                voucherView = new JournalVoucherView(jv);
            } else {
                voucherView.setVouchers(jv);
            }
            voucherView.execute();
            return;
        }
        super.clicked(c);
    }

    @Override
    public void valueChanged(ChangedValues changedValues) {
        if(changedValues.isChanged(accountField)) {
            account = accountField.getObject();
            if(account == null) {
                setCaption("Statement View");
            } else {
                ledger.setAccount(account);
                enableButtons();
                setCaption("Statement: " + account);
            }
            return;
        }
        if(account != null && changedValues.isChanged(dateField) && changedValues.isFromClient()) {
            ledger.setDate(dateField.getValue());
            enableButtons();
            return;
        }
        super.valueChanged(changedValues);
    }

    private void setOB() {
        openingBalance.clear();
        if(isEmpty()) {
            openingBalance.append(NO_ENTRIES);
        } else {
            LedgerEntry le = get(0);
            dateField.setValue(le.getDate());
            openingBalance.append("Opening balance as of ").append(le.getDate()).append(" is ");
            Money b = le.getOpeningBalance();
            if(b.isDebit()) {
                openingBalance.append("DB ");
                b = b.negate();
            }
            openingBalance.append(b.toString(false));
        }
        openingBalance.update();
        recalculateColumnWidths();
    }

    private void enableButtons() {
        setOB();
        if(isEmpty()) {
            forward.setEnabled(false);
            backward.setEnabled(false);
            begin.setEnabled(false);
            end.setEnabled(false);
            voucher.setEnabled(false);
            return;
        }
        voucher.setEnabled(true);
        DatePeriod p = ledger.getPeriod();
        Date m = ledger.getMostRecentDate();
        if(m == null) {
            end.setEnabled(true);
        } else {
            end.setEnabled(p.getTo().before(m));
        }
        m = ledger.getEarliestDate();
        if(m == null) {
            begin.setEnabled(true);
        } else {
            begin.setEnabled(p.getFrom().after(m));
        }
        forward.setEnabled(end.isEnabled());
        backward.setEnabled(begin.isEnabled());
    }
}
