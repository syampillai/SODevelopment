package com.storedobject.ui.account;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.List;

public class StatementView extends ListGrid<LedgerEntry> implements CloseableView {

    private static final String NO_ENTRIES = "No entries";
    private final AccountField<Account> accountField;
    private final DateField dateField = new DateField();
    private final Button forward, backward, begin, end, voucher;
    private final LedgerWindow ledger = new LedgerWindow(20, this) {
        @Override
        protected List<LedgerEntry> getUnposted() {
            return StatementView.this.getTail(getAccount());
        }
    };
    private Account account;
    private final ELabel openingBalance = new ELabel(NO_ENTRIES), accountTitle = new ELabel();
    private JournalVoucherView voucherView;

    public StatementView() {
        super(LedgerEntry.class, StringList.create("Date", "Particulars", "Debit", "Credit", "Balance"));
        ledger.setErrorLogger((m, e) -> {
            error(m);
            error(e);
        });
        accountField = createAccountField();
        accountField.setDisplayDetail(t -> {});
        setCaption("Statement View");
        forward = new Button("Next", this);
        backward = new Button("Previous", this);
        begin = new Button("First Page", VaadinIcon.SIGN_IN_ALT, this);
        end = new Button("Last Page", VaadinIcon.SIGN_OUT_ALT, this);
        voucher = new Button("Voucher", VaadinIcon.FILE_TEXT_O, this);
        forward.setEnabled(false);
        backward.setEnabled(false);
        begin.setEnabled(false);
        end.setEnabled(false);
        voucher.setEnabled(false);
        trackValueChange(dateField);
        trackValueChange(accountField);
        new Box(openingBalance);
    }

    public StatementView(Account account) {
        this(account, true);
    }

    public StatementView(Account account, boolean lockAccountField) {
        this();
        if(account != null) {
            accountField.setValue(account);
            if(lockAccountField) {
                accountField.setReadOnly(true);
            }
        }
    }

    protected AccountField<Account> createAccountField() {
        return new AccountField<>();
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        accountField.setValue((Id)null);
        accountField.setValue(account);
    }

    @Override
    public Component createHeader() {
        Div h = new Div();
        ButtonLayout b = new ButtonLayout();
        b.add(new ELabel("Account:"), accountField, accountTitle);
        h.add(b);
        b = new ButtonLayout();
        b.add(new ELabel("From Date:"), dateField, forward, backward, begin, end, voucher);
        addExtraButtons(b);
        b.add(new Button("Exit", e -> close()));
        b.addFiller();
        b.add(openingBalance);
        h.add(b);
        accountField.focus();
        return h;
    }

    protected void addExtraButtons(ButtonLayout buttonLayout) {
    }

    @Override
    public ColumnTextAlign getTextAlign(String columnName) {
        return switch (columnName) {
          case "Debit", "Credit", "Balance" -> ColumnTextAlign.END;
            default -> super.getTextAlign(columnName);
        };
    }

    @Override
    public boolean isColumnSortable(String columnName) {
        return false;
    }

    public String getParticulars(LedgerEntry le) {
        return le.getParticulars(true);
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
                enableButtons();
            } else {
                forward.setEnabled(false);
            }
            return;
        }
        if(c == backward) {
            if(ledger.moveBackward()) {
                enableButtons();
            } else {
                backward.setEnabled(false);
            }
            return;
        }
        if(c == begin) {
            ledger.moveToBeginning();
            enableButtons();
            return;
        }
        if(c == end) {
            ledger.moveToEnd();
            enableButtons();
            return;
        }
        if(c == voucher) {
            LedgerEntry le = size() == 1 ? get(0) : getSelected();
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
            account = accountField.getAccount();
            if(account == null) {
                setCaption("Statement View");
                accountTitle.clearContent().append("<Account Not Selected>");
            } else {
                loadingAccont(account);
                ledger.setAccount(account);
                accountTitle.clearContent().append(account.toDisplay(), Application.COLOR_SUCCESS).update();
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

    protected void loadingAccont(Account account) {
    }

    private void setOB() {
        openingBalance.clear();
        if(isEmpty()) {
            openingBalance.append(NO_ENTRIES);
        } else {
            LedgerEntry le = get(size() - 1);
            dateField.setValue(le.getDate());
            openingBalance.append("Opening balance as of ").append(le.getDate()).append(" is ")
                    .append(le.getOpeningBalance());
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
        boolean can = ledger.canMoveBackward();
        begin.setEnabled(can);
        backward.setEnabled(can);
        can = ledger.canMoveForward();
        end.setEnabled(can);
        forward.setEnabled(can);
    }

    protected List<LedgerEntry> getTail(Account account) {
        return null;
    }
}
