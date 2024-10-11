package com.storedobject.iot;

import com.storedobject.core.Columns;
import com.storedobject.core.DateUtility;
import com.storedobject.core.Id;
import com.storedobject.core.Name;
import com.storedobject.core.annotation.Column;

import java.sql.Date;
import java.sql.Timestamp;

public abstract class AbstractUnit extends Name {

    private boolean active;

    public AbstractUnit() {
    }

    public static void columns(Columns columns) {
        columns.add("Active", "boolean");
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(order = 100000)
    public boolean getActive() {
        return active;
    }

    Double consumption(int resource, long from, long to) {
        return computeConsumption(resource, from, to);
    }

    protected Double computeConsumption(int resource, long from, long to) {
        ConsumptionCalculator consumptionCalculator = getConsumptionCalculator(resource);
        return consumptionCalculator == null ? null : consumptionCalculator.compute(resource, unitId(), from, to);
    }

    Id unitId() {
        return getId();
    }

    public abstract Site getSite();

    public abstract Id getBlockId();

    protected ConsumptionCalculator getConsumptionCalculator(int resource) {
        return null;
    }

    DataPeriod getDataPeriod(Date dateGMT) {
        Date siteDate = getSite().date(dateGMT);
        Timestamp d;
        d = DateUtility.startTime(DateUtility.startOfYear(dateGMT));
        long to = 3600000L;
        long from = d.getTime() + ((DateUtility.getHourOfYear(dateGMT) - 1) * to);
        to += from;
        if(to > System.currentTimeMillis()) {
            return null;
        }
        return new DataPeriod(siteDate, from, to);
    }

    record DataPeriod(Date siteDate, long from, long to) {
    }
}
