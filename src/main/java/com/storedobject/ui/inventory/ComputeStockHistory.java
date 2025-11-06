package com.storedobject.ui.inventory;

import com.storedobject.core.DateUtility;
import com.storedobject.core.TransactionManager;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabelField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.DateField;

import java.sql.Date;

public class ComputeStockHistory extends DataForm implements Transactional {

    private final DateField dateField = new DateField("Compute Stock History as of");

    public ComputeStockHistory() {
        super("Compute Stock History");
        ELabelField label = new ELabelField("This will compute the stock history for the selected date.");
        label.newLine().append("This could take a while!", Application.COLOR_ERROR)
                .append("This will be processed in the background.")
                .update();
        addField(label, dateField);
    }

    @Override
    protected boolean process() {
        Date date = dateField.getValue();
        if(!date.before(DateUtility.today())) {
            warning("Please select a date before today!");
            return false;
        }
        clearAlerts();
        close();
        TransactionManager tm = getTransactionManager();
        Thread.startVirtualThread(() -> {
            boolean ok = com.storedobject.job.ComputeStockHistory.execute(tm, date);
            String m = "Stock history computation " + (ok ? "completed" : "failed") + " for " + DateUtility.format(date);
            tm.getUser().getPerson().notify("STOCK-HISTORY-COMPUTED", tm, m);
        });
        message("Stock history computation started for " + DateUtility.format(date) + ". You will be notified when it is done.");
        return true;
    }
}
