package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public final class HourlyConsumption extends Consumption {

    private int hour;

    public HourlyConsumption() {
    }

    public static void columns(Columns columns) {
        columns.add("Hour", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Item,Resource,Year,Hour", true);
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @Column(order = 300)
    public int getHour() {
        return hour;
    }

    @Override
    public int getPeriod() {
        return hour;
    }

    private Date date() {
        return new Date(DateUtility.create(getYear(), 1, 1).getTime() + (hour - 1) * 3600000L);
    }

    @Override
    public String getPeriodDetail() {
        return DateUtility.formatWithTimeHHMM(date());
    }

    /**
     * Remove this consumption entry (maybe, it was wrongly computed or computed from erroneous data). related
     * daily, weekly, monthly and yearly entries will be updated accordingly.
     *
     * @param tm Transaction manager.
     * @throws Exception Raises exception if an error occurs.
     */
    public void remove(TransactionManager tm) throws Exception {
        AbstractUnit au = getItem();
        if(au instanceof Block) {
            throw new Invalid_State("Block consumption can't be directly removed");
        }
        if(au instanceof UnitItem ui && !ui.getIndependent()) {
            throw new Invalid_State("Unit item consumption that is not independent can't be directly removed");
        }
        Date date = date();
        Resource resource = getResource();
        Id itemId = getItemId();
        List<Consumption> others = new ArrayList<>();
        others.add(resource.createDailyConsumption(itemId, date));
        others.add(resource.createWeeklyConsumption(itemId, date));
        others.add(resource.createMonthlyConsumption(itemId, date));
        others.add(resource.createYearlyConsumption(itemId, date));
        Id blockId = au.getBlockId();
        others.add(resource.createHourlyConsumption(blockId, date));
        others.add(resource.createDailyConsumption(blockId, date));
        others.add(resource.createWeeklyConsumption(blockId, date));
        others.add(resource.createMonthlyConsumption(blockId, date));
        others.add(resource.createYearlyConsumption(blockId, date));
        others.removeIf(StoredObject::isVirtual);
        tm.transact(t -> {
            double consumption;
            if(au instanceof Unit) {
                for(UnitItem ui: list(UnitItem.class, "Unit=" + au.getId() + " AND NOT Independent",
                        true)) {
                    Consumption c;
                    c = resource.createHourlyConsumption(ui.getId(), date);
                    if(!c.isVirtual()) {
                        consumption = -c.getConsumption();
                        c.delete(t);
                        c = resource.createDailyConsumption(ui.getId(), date);
                        if(!c.isVirtual()) {
                            c.addConsumption(consumption);
                            c.save(t);
                        }
                        c = resource.createWeeklyConsumption(ui.getId(), date);
                        if(!c.isVirtual()) {
                            c.addConsumption(consumption);
                            c.save(t);
                        }
                        c = resource.createMonthlyConsumption(ui.getId(), date);
                        if(!c.isVirtual()) {
                            c.addConsumption(consumption);
                            c.save(t);
                        }
                        c = resource.createYearlyConsumption(ui.getId(), date);
                        if(!c.isVirtual()) {
                            c.addConsumption(consumption);
                            c.save(t);
                        }
                    }
                }
            }
            consumption = -getConsumption();
            delete(t);
            for(Consumption c: others) {
                c.addConsumption(consumption);
                c.save(t);
            }
        });
    }
}
