package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * The HourlyConsumption class represents consumption data aggregated at an hourly level.
 * It extends the Consumption class and provides additional functionality specific to hourly consumption.
 * This class includes methods for managing and integrating hourly consumption data into different time-based aggregations.
 *
 * @author Syam
 */
public final class HourlyConsumption extends Consumption<PeriodType> {

    private int hour;

    /**
     * Default constructor for the HourlyConsumption class.
     * Initializes an instance representing the consumption data on an hourly basis.
     */
    public HourlyConsumption() {
    }

    /**
     * Configures the column definitions for the object.
     *
     * @param columns the object used to define and register column details for the entity, where
     *                "Hour" is added as a column with data type "int".
     */
    public static void columns(Columns columns) {
        columns.add("Hour", "int");
    }

    /**
     * Configures indices for the consumption data.
     *
     * @param indices the indices object used to specify the index configurations
     */
    public static void indices(Indices indices) {
        indices.add("Item,Resource,Year,Hour", true);
    }

    /**
     * Sets the hour value for this instance.
     *
     * @param hour the hour to be set, expected to be in the range of valid hour values (e.g., 0-23).
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * Retrieves the hour value associated with this instance.
     *
     * @return the hour as an integer
     */
    @Column(order = 300)
    public int getHour() {
        return hour;
    }

    /**
     * Retrieves the hourly period value representing the hour for this instance.
     *
     * @return the hour as an integer.
     */
    @Override
    public int getPeriod() {
        return hour;
    }

    /**
     * Computes the date and time corresponding to the specific hour for the consumption record.
     * The date is calculated as January 1st of the year retrieved from {@code getYear},
     * with the time adjusted by the given hour.
     *
     * @return the calculated {@link Date} instance.
     */
    private Date date() {
        return new Date(DateUtility.create(getYear(), 1, 1).getTime() + (hour - 1) * 3600000L);
    }

    /**
     * Provides the detailed representation of the period for the current hourly consumption.
     * The detail is formatted as a date and time string in "HH:mm" format.
     *
     * @return A string representing the period detail in "HH:mm" format.
     */
    @Override
    public String getPeriodDetail() {
        return DateUtility.formatWithTimeHHMM(date());
    }

    /**
     * Removes the consumption for the associated unit or unit items and updates related consumption records
     * for various time periods accordingly.
     *
     * @param tm The transaction manager used to execute the removal operations within a transactional context.
     * @throws Exception If any unexpected error occurs during the removal process, or if the removal is
     *                   not allowed due to state or contextual constraints.
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
        List<Consumption<?>> others = new ArrayList<>();
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
                    Consumption<?> c;
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
            for(Consumption<?> c: others) {
                c.addConsumption(consumption);
                c.save(t);
            }
        });
    }

    /**
     * Retrieves the previous HourlyConsumption record based on the current hour and year.
     * If the current hour is the first hour of the year, it calculates the last hour
     * of the previous year and returns the corresponding HourlyConsumption record.
     *
     * @return the previous HourlyConsumption instance.
     */
    @Override
    public HourlyConsumption previous() {
        int h = getHour() - 1;
        int y = getYear();
        if(h < 0) {
            h = DateUtility.getHourOfYear(new Date(DateUtility.create(y, 1, 1).getTime() - 60000L));
            --y;
        }
        return get(HourlyConsumption.class, "Year=" + y + " AND Hour=" + h + cond());
    }

    /**
     * Retrieves the next hourly consumption record. The next record is determined based on the
     * current hour and year. If the next hour within the same year exists, it is returned. If the
     * current hour is the last hour of the year, it fetches the first hour of the next year.
     *
     * @return the next {@link HourlyConsumption} instance if available; otherwise, returns null.
     */
    @Override
    public HourlyConsumption next() {
        int h = getHour() + 1;
        int y = getYear();
        HourlyConsumption hc = get(HourlyConsumption.class, "Year=" + y + " AND Hour=" + h + cond());
        if(hc != null) {
            return hc;
        }
        if(h <= 8748) {
            return null;
        }
        ++y;
        return get(HourlyConsumption.class, "Year=" + y + " AND Hour=1" + cond());
    }

    /**
     * Retrieves the type of period associated with this instance.
     *
     * @return the {@link PeriodType} representing the period type, which is {@code HOURLY} in this implementation.
     */
    @Override
    public PeriodType getPeriodType() {
        return PeriodType.HOURLY;
    }
}
