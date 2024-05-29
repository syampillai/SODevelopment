package com.storedobject.ui.accounts;

import com.storedobject.common.StringList;
import com.storedobject.core.Id;
import com.storedobject.core.JournalVoucher;
import com.storedobject.core.Money;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;

import java.util.List;

public class JournalVoucherView extends ListGrid<JournalVoucher.Entry> implements CloseableView {

    private static final String NOT_LOADED = "Not loaded";
    private List<JournalVoucher> vouchers;
    private final ELabel transaction = new ELabel(), count = new ELabel();

    public JournalVoucherView() {
        this((List<JournalVoucher>)null);
    }

    public JournalVoucherView(JournalVoucher voucher) {
        this(voucher == null ? null : voucher.getVouchers());
    }

    public JournalVoucherView(List<JournalVoucher> vouchers) {
        super(JournalVoucher.Entry.class, StringList.create("Reference AS JV No.", "Account", "Particulars", "Debit", "Credit"));
        setCaption("Journal Voucher");
        new Box(count);
        setVouchers(vouchers);
    }

    @Override
    public Component createHeader() {
        ButtonLayout b = new ButtonLayout(new ELabel("Transaction:"), transaction);
        b.add(new ELabel(" Entries: "), count);
        b.addFiller();
        b.add(new Button("Exit", e -> close()));
        return b;
    }

    @Override
    public ColumnTextAlign getTextAlign(String columnName) {
        if("Debit".equals(columnName) || "Credit".equals(columnName)) {
            return ColumnTextAlign.END;
        }
        return super.getTextAlign(columnName);
    }

    @Override
    public int getRelativeColumnWidth(String columnName) {
        return switch (columnName) {
            case "Reference" -> 18;
            case "Account" -> 80;
            case "Particulars" -> 100;
            default -> 16;
        };
    }

    @Override
    public boolean isColumnSortable(String columnName) {
        return false;
    }

    public String getParticulars(JournalVoucher.Entry entry) {
        return StatementView.wrap(entry.getParticulars());
    }

    public String getDebit(JournalVoucher.Entry entry) {
        Money a = entry.getAmount();
        if(a.isDebit()) {
            return a.negate().toString(false);
        }
        return "";
    }

    public String getCredit(JournalVoucher.Entry entry) {
        Money a = entry.getAmount();
        if(a.isCredit()) {
            return a.toString(false);
        }
        return "";
    }

    public String getReference(JournalVoucher.Entry entry) {
        return entry.getVoucher().getReference();
    }

    public void setVoucher(JournalVoucher voucher) {
        setVouchers(voucher == null ? null : voucher.getVouchers());
    }

    public void setVouchers(List<JournalVoucher> vouchers) {
        if(vouchers != null && vouchers.equals(this.vouchers)) {
            return;
        }
        this.vouchers = vouchers;
        clear();
        count.clear();
        transaction.clear();
        if(vouchers == null || vouchers.isEmpty()) {
            count.append(NOT_LOADED);
            transaction.append(NOT_LOADED);
            setCaption("Voucher");
        } else {
            Id pid = null, id;
            for(JournalVoucher jv: vouchers) {
                jv.entries().forEach(this::add);
                id = jv.getTransactionId();
                if(pid == null || !pid.equals(id)) {
                    if(pid != null) {
                        transaction.append(", ", Application.COLOR_SUCCESS);
                    }
                    pid = id;
                    transaction.append(id, Application.COLOR_SUCCESS);
                    transaction.append(" dated ", Application.COLOR_SUCCESS)
                            .append(jv.getDate(), Application.COLOR_SUCCESS);
                }
            }
            count.append("" + size(), Application.COLOR_SUCCESS);
            setCaption("Voucher: " + vouchers.get(0).getReference());
        }
        count.update();
        transaction.update();
    }

    public List<JournalVoucher> getVouchers() {
        return vouchers;
    }
}
