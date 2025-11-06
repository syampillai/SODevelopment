package com.storedobject.job;

import com.storedobject.core.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Scheduler job to compute stock history for all locations.
 *
 * @author Syam
 */
public class ComputeStockHistory extends Job {

    /**
     * Constructor.
     *
     * @param schedule Schedule defined for this Job.
     */
    public ComputeStockHistory(Schedule schedule) {
        super(schedule);
    }

    @Override
    public void execute() {
        execute(getTransactionManager(), DateUtility.endOfMonth(-1));
    }

    public static boolean execute(TransactionManager tm, Date date) {
        if(!date.before(DateUtility.today())) {
            return false;
        }
        if(StockHistoryDate.isComputed(date, Id.ZERO)) {
            return true;
        }
        boolean ok = compute(tm, date, QueryBuilder.from(InventoryStoreBin.class));
        ok = ok && compute(tm, date, QueryBuilder.from(InventoryFitmentPosition.class));
        ok = ok && compute(tm, date, QueryBuilder.from(InventoryCustodyLocation.class));
        ok = ok && compute(tm, date, QueryBuilder.from(InventoryVirtualLocation.class));
        if(ok) {
            StockHistoryDate d = new StockHistoryDate();
            d.setDate(date);
            d.setLocation(Id.ZERO);
            try {
                tm.transact(d::save);
            } catch (Exception e) {
                tm.log(e);
                return false;
            }
        }
        return ok;
    }

    private static boolean compute(TransactionManager tm, Date date, QueryBuilder<? extends InventoryLocation> locationQueryBuilder) {
        locationQueryBuilder.limit(100);
        List<InventoryLocation> locations = new ArrayList<>();
        locationQueryBuilder.list().map(InventoryLocation.class::cast).collectAll(locations);
        boolean ok = true;
        while (!locations.isEmpty()) {
            for (InventoryLocation location : locations) {
                ok = ok && compute(tm, date, location);
            }
            if(locations.size() < 100) {
                locations.clear();
                break;
            }
            locations.clear();
        }
        return ok;
    }

    private static boolean compute(TransactionManager tm, Date date, InventoryLocation location) {
        try {
            StockHistoryDate.compute(tm, date, location);
            return true;
        } catch (Exception e) {
            tm.log(e);
            return false;
        }
    }
}
