package com.storedobject.ui.account;

import com.storedobject.common.StringList;
import com.storedobject.core.Id;
import com.storedobject.core.JournalVoucher;
import com.storedobject.core.Money;
import com.storedobject.ui.ELabel;
import com.storedobject.vaadin.Box;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.ListGrid;
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
        super(JournalVoucher.Entry.class, StringList.create("Account", "Particulars", "Debit", "Credit"));
        setCaption("Journal Voucher");
        new Box(count);
        setVouchers(vouchers);
    }

    @Override
    public Component createHeader() {
        ButtonLayout b = new ButtonLayout(new ELabel("Transaction:"), transaction);
        b.addFiller();
        b.add(new ELabel("Entries: "), count);
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
    public boolean isColumnSortable(String columnName) {
        return false;
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
                        transaction.append(", ");
                    }
                    pid = id;
                    transaction.append(id);
                    transaction.append(" dated ").append(jv.getDate());
                }
            }
            recalculateColumnWidths();
            count.append(size());
            setCaption("Voucher: " + vouchers.get(0).getTransactionId());
        }
        count.update();
        transaction.update();
    }

    public List<JournalVoucher> getVouchers() {
        return vouchers;
    }
}
