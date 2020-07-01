package com.storedobject.ui.account;

import com.storedobject.common.StringList;
import com.storedobject.core.Account;
import com.storedobject.core.LedgerEntry;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.ListGrid;

public class StatementView extends ListGrid<LedgerEntry> implements CloseableView {

    public StatementView() {
        super(LedgerEntry.class, StringList.create("Date", "Particulars", "Debit", "Credit", "Balance"));
    }

    public StatementView(Account account) {
        this();
    }
}