package com.storedobject.ui.account;

import com.storedobject.common.StringList;
import com.storedobject.core.JournalVoucher;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.ListGrid;

import java.util.ArrayList;
import java.util.List;

public class JournalVoucherView extends ListGrid<JournalVoucher.Entry> implements CloseableView {

    public JournalVoucherView() {
        this((List<JournalVoucher>)null);
    }

    public JournalVoucherView(JournalVoucher voucher) {
        this(voucher == null ? null : voucher.getVouchers());
    }

    public JournalVoucherView(List<JournalVoucher> vouchers) {
        super(JournalVoucher.Entry.class, StringList.create("Account", "Particulars", "Debit", "Credit"));
    }

    public void setVoucher(JournalVoucher voucher) {
    }

    public void setVouchers(List<JournalVoucher> vouchers) {
    }

    public List<JournalVoucher> getVouchers() {
        return new ArrayList<>();
    }
}