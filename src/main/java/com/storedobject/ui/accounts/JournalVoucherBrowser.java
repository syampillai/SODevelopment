package com.storedobject.ui.accounts;

import com.storedobject.core.*;
import com.storedobject.ui.DatePeriodField;
import com.storedobject.ui.ObjectBrowser;
import com.storedobject.ui.ObjectField;
import com.storedobject.ui.ObjectSearchForm;

import java.util.function.Predicate;

public class JournalVoucherBrowser extends ObjectBrowser<JournalVoucher> {

    private JournalVoucherView viewer;

    public JournalVoucherBrowser() {
        super(JournalVoucher.class, EditorAction.NEW | EditorAction.VIEW | EditorAction.ALLOW_ANY
                | EditorAction.RELOAD | EditorAction.SEARCH, new Searcher());
    }

    @Override
    public void doView(JournalVoucher object) {
        if(object == null) {
            return;
        }
        if(viewer == null) {
            viewer = new JournalVoucherView();
        }
        viewer.setVoucher(object);
        viewer.execute();
    }

    static class Searcher extends ObjectSearchForm<JournalVoucher> {

        private final ObjectField<SystemEntity> systemEntity = new ObjectField<>("Organization", SystemEntity.class);
        private final DatePeriodField period = new DatePeriodField("Period", DatePeriod.create());

        Searcher() {
            super(JournalVoucher.class);
            add(systemEntity, period);
        }

        @Override
        public String getFilterText() {
            SystemEntity entity = systemEntity.getObject();
            return "SystemEntity=" + (entity == null ? 0 : entity.getId()) + " AND Date"
                    + period.getValue().getDBTimeCondition();
        }

        @Override
        public Predicate<JournalVoucher> getFilterPredicate() {
            DatePeriod p = period.getValue();
            SystemEntity entity = systemEntity.getObject();
            Id entityId = entity == null ? Id.ZERO : entity.getId();
            return jv -> jv.getSystemEntityId().equals(entityId) && p.inside(jv.getDate());
        }
    }
}
