package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an abstract unit within a system.
 * This class serves as a base for specific unit implementations, providing common functionality
 * and requiring extensions to supply specific attributes or behaviors.
 *
 * @author Syam
 */
public abstract class AbstractUnit extends Name {

    private boolean active;
    private Map<Id, HourlyConsumption> hourlyConsumption;
    private Map<Id, DailyConsumption> dailyConsumption;
    private Map<Id, WeeklyConsumption> weeklyConsumption;
    private Map<Id, MonthlyConsumption> monthlyConsumption;
    private Map<Id, YearlyConsumption> yearlyConsumption;

    /**
     * Default constructor for the AbstractUnit class.
     * This constructor initializes an instance of the AbstractUnit class or its subclasses.
     * The class acts as a base class providing foundational functionality for concrete unit implementations.
     */
    public AbstractUnit() {
    }

    /**
     * Configures column definitions for the given `Columns` object by adding metadata
     * about the "Active" column.
     *
     * @param columns the object where column definitions are added. This should support
     *                methods to add metadata, such as name and type, for database or data
     *                export structures.
     */
    public static void columns(Columns columns) {
        columns.add("Active", "boolean");
    }

    /**
     * Sets the active status of the unit.
     *
     * @param active a boolean value indicating whether the unit should be active (true) or inactive (false)
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Retrieves the active status of the unit.
     *
     * @return {@code true} if the unit is active; {@code false} otherwise.
     */
    @Column(order = 100000)
    public boolean getActive() {
        return active;
    }

    /**
     * Calculates the resource consumption for a specific unit over a defined time period.
     *
     * @param resource the resource identifier for which the consumption is being calculated
     * @param from the start timestamp of the time period (in milliseconds since epoch)
     * @param to the end timestamp of the time period (in milliseconds since epoch)
     * @return the consumption value as a Double, or null if unable to calculate
     */
    Double consumption(int resource, long from, long to) {
        return computeConsumption(resource, from, to);
    }

    /**
     * Computes the resource consumption for a specific unit within a given time interval.
     *
     * @param resource the resource identifier to calculate consumption for
     * @param from the start time (in milliseconds since epoch) of the period for which consumption is to be computed
     * @param to the end time (in milliseconds since epoch) of the period for which consumption is to be computed
     * @return the calculated consumption as a Double, or null if no ConsumptionCalculator is available for the resource
     */
    protected Double computeConsumption(int resource, long from, long to) {
        ConsumptionCalculator consumptionCalculator = getConsumptionCalculator(resource);
        return consumptionCalculator == null ? null : consumptionCalculator.compute(resource, unitId(), from, to);
    }

    /**
     * Retrieves the unique identifier for the unit.
     *
     * @return the unique identifier of type {@link Id} associated with the unit
     */
    Id unitId() {
        return getId();
    }

    /**
     * Retrieves the associated site of the current unit.
     * This method is abstract and must be implemented by subclasses.
     *
     * @return The site object associated with the current unit.
     */
    public abstract Site getSite();

    /**
     * Retrieves the unique identifier of the block associated with this unit.
     *
     * @return an {@code Id} object representing the block's unique identifier.
     */
    public abstract Id getBlockId();

    /**
     * Retrieves the specific consumption calculator for a given resource.
     * The consumption calculator is responsible for calculating resource usage
     * over a specific time period based on the provided resource identifier.
     *
     * @param resource the identifier of the resource for which the consumption calculator is requested
     * @return the {@code ConsumptionCalculator} for the specified resource,
     *         or {@code null} if no relevant calculator is available
     */
    protected ConsumptionCalculator getConsumptionCalculator(int resource) {
        return null;
    }

    /**
     * Determines whether the unit consumes a specified resource.
     *
     * @param resource The resource identifier to check for consumption.
     * @return {@code true} if the unit consumes the specified resource; {@code false} otherwise.
     */
    public boolean consumes(int resource) {
        return getConsumptionCalculator(resource) != null;
    }

    /**
     * Determines whether the unit consumes a specified resource.
     *
     * @param resource The resource to check for consumption.
     * @return {@code true} if the unit consumes the specified resource; {@code false} otherwise.
     */
    public boolean consumes(Resource resource) {
        return consumes(resource.getCode());
    }

    /**
     * Creates a DataPeriod object for the specified GMT date based on the site's timezone.
     * The method calculates the start and end time of the data period within the given year
     * and ensures the data period lies in the past relative to the current system time.
     *
     * @param dateGMT the GMT date for which the DataPeriod is determined
     * @return a DataPeriod object containing the site-adjusted date, the start time in milliseconds,
     *         and the end time in milliseconds; or null if the calculated period is in the future
     */
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

    private <T extends Consumption<?>> T getConsumption(Resource resource, Class<T> cClass, String condition) {
        return consumption(resource, cClass, condition).single(false);
    }

    private <T extends Consumption<?>> ObjectIterator<T> consumption(Resource resource, Class<T> cClass, String condition) {
        return list(cClass, condition + " AND Item=" + getId() + " AND Resource=" + resource.getId());
    }

    /**
     * Retrieves a list of yearly consumption data for the specified resource over a range of years.
     *
     * @param resource the resource for which the yearly consumption data is requested
     * @param yearFrom the starting year of the range (inclusive)
     * @param yearTo the ending year of the range (inclusive)
     * @return a list of {@code YearlyConsumption} objects representing the yearly consumption data for the specified resource
     */
    public final List<YearlyConsumption> listYearlyConsumption(Resource resource, int yearFrom, int yearTo) {
        return resource.listYearlyConsumption(getId(), yearFrom, yearTo);
    }

    /**
     * Retrieves a list of yearly consumption data for a given resource and site over a specified number of years.
     *
     * @param resource the resource for which the yearly consumption data is to be retrieved
     * @param site the site associated with the resource and its consumption data
     * @param periodCount the number of years for which the yearly consumption data will be listed
     * @return a list of YearlyConsumption objects representing the yearly consumption data over the specified period
     */
    public final List<YearlyConsumption> listYearlyConsumption(Resource resource, Site site, int periodCount) {
        --periodCount;
        int yearTo = DateUtility.getYear(site.date(new Date(System.currentTimeMillis())));
        return listYearlyConsumption(resource, yearTo - periodCount, yearTo);
    }

    /**
     * Retrieves the yearly consumption for a specified resource and year.
     * This method calculates the resource consumption aggregated over the specified year.
     *
     * @param resource the resource for which the yearly consumption is to be retrieved
     * @param year the year for which the consumption is to be calculated
     * @return the {@code YearlyConsumption} object representing the aggregated consumption for the specified resource and year
     */
    public final YearlyConsumption getYearlyConsumption(Resource resource, int year) {
        return getConsumption(resource, YearlyConsumption.class, "Year=" + year);
    }

    /**
     * Retrieves the yearly consumption for a specific resource based on the provided date.
     *
     * @param <D> A type that extends {@link java.util.Date}, representing the specific date used for determining the year.
     * @param resource The resource for which the yearly consumption is being retrieved.
     * @param date The date used to determine the year for retrieving the yearly consumption.
     * @return A {@link YearlyConsumption} object representing the resource's consumption for the determined year.
     */
    public final <D extends java.util.Date> YearlyConsumption getYearlyConsumption(Resource resource, D date) {
        return getYearlyConsumption(resource, DateUtility.getYear(date));
    }

    /**
     * Creates or retrieves a {@link YearlyConsumption} object for the specified resource and date.
     * If a {@link YearlyConsumption} for the given resource and year does not exist, a new one
     * is instantiated, initialized, and returned.
     *
     * @param resource the resource for which the yearly consumption is to be created or retrieved
     * @param date the date used to determine the year to associate with the consumption
     * @return the {@link YearlyConsumption} instance associated with the resource and specified year
     */
    private <D extends java.util.Date> YearlyConsumption createYearlyConsumption(Resource resource, D date) {
        int y = DateUtility.getYear(date);
        YearlyConsumption c = getYearlyConsumption(resource, y);
        if(c == null) {
            c = new YearlyConsumption();
            c.setItem(this);
            c.setResource(resource);
            c.setYear(y);
            c.makeVirtual();
        }
        return c;
    }

    /**
     * Retrieves a list of monthly consumption records for a specified resource and time range.
     *
     * @param resource the resource for which monthly consumption data is requested
     * @param year the year for the time range
     * @param monthFrom the starting month of the time range (inclusive)
     * @param monthTo the ending month of the time range (inclusive)
     * @return a list of {@code MonthlyConsumption} objects corresponding to the specified resource and time range
     */
    public final List<MonthlyConsumption> listMonthlyConsumption(Resource resource, int year, int monthFrom, int monthTo) {
        return resource.listMonthlyConsumption(getId(), year, monthFrom, monthTo);
    }

    /**
     * Retrieves a list of monthly consumption data for the specified resource within the given date range.
     *
     * @param resource The resource for which the monthly consumption data is being retrieved.
     * @param yearFrom The starting year of the date range.
     * @param monthFrom The starting month of the date range.
     * @param yearTo The ending year of the date range.
     * @param monthTo The ending month of the date range.
     * @return A List of MonthlyConsumption objects representing the monthly consumption data for
     *         the specified resource within the provided date range. Returns an empty list if the
     *         date range is invalid.
     */
    public final List<MonthlyConsumption> listMonthlyConsumption(Resource resource, int yearFrom, int monthFrom,
                                                                 int yearTo, int monthTo) {
        if(yearFrom > yearTo) {
            return List.of();
        }
        if(yearFrom == yearTo) {
            return listMonthlyConsumption(resource, yearFrom, monthFrom, monthTo);
        }
        List<MonthlyConsumption> list = listMonthlyConsumption(resource, yearFrom, monthFrom, 12);
        while(yearFrom < yearTo) {
            ++yearFrom;
            list.addAll(listMonthlyConsumption(resource, yearFrom, 1, yearFrom == yearTo ? monthTo : 12));
        }
        return list;
    }

    /**
     * Retrieves a list of monthly consumption data for a specified resource and site
     * over a given number of periods (months).
     *
     * @param resource   the resource for which the consumption data is being retrieved
     * @param site       the site associated with the resource
     * @param periodCount the number of months for which the consumption data is required
     * @return a list of MonthlyConsumption objects representing the consumption data
     *         for the specified resource and site over the given period
     */
    public final List<MonthlyConsumption> listMonthlyConsumption(Resource resource, Site site, int periodCount) {
        PeriodCount p = PeriodCount.monthly(site, periodCount);
        return listMonthlyConsumption(resource, p.yearFrom, p.from, p.yearTo, p.to);
    }

    /**
     * Retrieves the monthly consumption for a specific resource in a given year and month.
     *
     * @param resource the resource for which the monthly consumption is being requested
     * @param year the year for which the consumption data is required
     * @param month the month for which the consumption data is required (1 for January, 12 for December)
     * @return a {@code MonthlyConsumption} object representing the consumption for the specified resource,
     *         year, and month, or {@code null} if no data is available
     */
    public final MonthlyConsumption getMonthlyConsumption(Resource resource, int year, int month) {
        return getConsumption(resource, MonthlyConsumption.class, "Year=" + year + " AND Month=" + month);
    }

    /**
     * Retrieves the monthly consumption for a given resource and date.
     *
     * @param resource the resource for which the monthly consumption is to be retrieved
     * @param date the date used to determine the year and month for the monthly consumption calculation
     * @param <D> a type that extends {@link java.util.Date}, representing the date parameter
     * @return the {@code MonthlyConsumption} object for the specified resource and date
     */
    public final <D extends java.util.Date> MonthlyConsumption getMonthlyConsumption(Resource resource, D date) {
        return getMonthlyConsumption(resource, DateUtility.getYear(date), DateUtility.getMonth(date));
    }

    private <D extends java.util.Date> MonthlyConsumption createMonthlyConsumption(Resource resource, D date) {
        int y = DateUtility.getYear(date), m = DateUtility.getMonth(date);
        MonthlyConsumption c = getMonthlyConsumption(resource, y, m);
        if(c == null) {
            c = new MonthlyConsumption();
            c.setItem(this);
            c.setResource(resource);
            c.setYear(y);
            c.setMonth(m);
            c.makeVirtual();
        }
        return c;
    }

    /**
     * Retrieves a list of weekly consumption data for a given resource and specified time range.
     *
     * @param resource the resource for which weekly consumption data is to be fetched
     * @param year the specific year corresponding to the time range
     * @param weekFrom the starting week number in the specified year
     * @param weekTo the ending week number in the specified year
     * @return a list of WeeklyConsumption objects representing the consumption data for the given resource
     *         within the specified year and week range
     */
    public final List<WeeklyConsumption> listWeeklyConsumption(Resource resource, int year, int weekFrom, int weekTo) {
        return resource.listWeeklyConsumption(getId(), year, weekFrom, weekTo);
    }

    /**
     * Retrieves the weekly consumption data for a given resource over a specified time period.
     *
     * @param resource the resource for which the weekly consumption needs to be calculated
     * @param yearFrom the starting year of the time range
     * @param weekFrom the starting week of the starting year
     * @param yearTo the ending year of the time range
     * @param weekTo the ending week of the ending year
     * @return a list of WeeklyConsumption objects representing the consumption data for the given time range
     */
    public final List<WeeklyConsumption> listWeeklyConsumption(Resource resource, int yearFrom, int weekFrom,
                                                               int yearTo, int weekTo) {
        if(yearFrom > yearTo) {
            return List.of();
        }
        if(yearFrom == yearTo) {
            return listWeeklyConsumption(resource, yearFrom, weekFrom, weekTo);
        }
        List<WeeklyConsumption> list = listWeeklyConsumption(resource, yearFrom, weekFrom, 52);
        while(yearFrom < yearTo) {
            ++yearFrom;
            list.addAll(listWeeklyConsumption(resource, yearFrom, 1, yearFrom == yearTo ? weekTo : 52));
        }
        return list;
    }

    /**
     * Retrieves a list of weekly consumption data for the specified resource and site over a given number of weeks.
     *
     * @param resource The resource for which the consumption data is being requested.
     * @param site The site associated with the resource.
     * @param periodCount The number of weeks for which data should be retrieved, counting back from the current week.
     * @return A list of WeeklyConsumption objects representing the consumption data for the specified period.
     */
    public final List<WeeklyConsumption> listWeeklyConsumption(Resource resource, Site site, int periodCount) {
        PeriodCount p = PeriodCount.weekly(site, periodCount);
        return listWeeklyConsumption(resource, p.yearFrom, p.from, p.yearTo, p.to);
    }

    /**
     * Retrieves the weekly consumption for a specified resource, year, and week.
     *
     * @param resource the resource for which the weekly consumption is requested
     * @param year the year for which the weekly consumption is calculated
     * @param week the week of the specified year for which the consumption is calculated
     * @return the {@code WeeklyConsumption} object containing the data for the specified resource, year, and week
     */
    public final WeeklyConsumption getWeeklyConsumption(Resource resource, int year, int week) {
        return getConsumption(resource, WeeklyConsumption.class, "Year=" + year + " AND Week=" + week);
    }

    /**
     * Retrieves the weekly consumption data for a given resource based on a specific date.
     * Internally, this method calculates the year and week of the provided date and fetches
     * the corresponding weekly consumption information.
     *
     * @param resource the resource for which the weekly consumption data is to be retrieved
     * @param date     the date used to determine the year and week for weekly consumption retrieval;
     *                 must be a subclass of {@link java.util.Date}
     * @param <D>      a generic type extending {@link java.util.Date}, representing the date parameter type
     * @return the {@link WeeklyConsumption} object containing the resource's weekly consumption data
     */
    public final <D extends java.util.Date> WeeklyConsumption getWeeklyConsumption(Resource resource, D date) {
        return getWeeklyConsumption(resource, DateUtility.getYear(date), DateUtility.getWeekOfYear(date));
    }

    private <D extends java.util.Date> WeeklyConsumption createWeeklyConsumption(Resource resource, D date) {
        int y = DateUtility.getYear(date), w = DateUtility.getWeekOfYear(date);
        WeeklyConsumption c = getWeeklyConsumption(resource, y, w);
        if(c == null) {
            c = new WeeklyConsumption();
            c.setItem(this);
            c.setResource(resource);
            c.setYear(y);
            c.setWeek(w);
            c.makeVirtual();
        }
        return c;
    }

    /**
     * Retrieves a list of hourly consumption data for the specified resource within a given year and time interval.
     *
     * @param resource the resource for which hourly consumption data is requested
     * @param year the year during which the hourly consumption data should be retrieved
     * @param hourFrom the starting hour (inclusive) of the time interval within the year
     * @param hourTo the ending hour (inclusive) of the time interval within the year
     * @return a list of {@code HourlyConsumption} objects representing the consumption data for the specified resource and time range
     */
    public final List<HourlyConsumption> listHourlyConsumption(Resource resource, int year, int hourFrom, int hourTo) {
        return resource.listHourlyConsumption(getId(), year, hourFrom, hourTo);
    }

    /**
     * Retrieves a list of hourly consumption records for a specified resource between given years and hours.
     *
     * @param resource the resource for which hourly consumption data is to be retrieved
     * @param yearFrom the starting year of the range
     * @param hourFrom the starting hour of the range within the starting year
     * @param yearTo the ending year of the range
     * @param hourTo the ending hour of the range within the ending year
     * @return a list of hourly consumption records for the specified resource and range
     */
    public final List<HourlyConsumption> listHourlyConsumption(Resource resource, int yearFrom, int hourFrom,
                                                               int yearTo, int hourTo) {
        if(yearFrom > yearTo) {
            return List.of();
        }
        if(yearFrom == yearTo) {
            return listHourlyConsumption(resource, yearFrom, hourFrom, hourTo);
        }
        List<HourlyConsumption> list = listHourlyConsumption(resource, yearFrom, hourFrom, lastHour(yearFrom));
        while(yearFrom < yearTo) {
            ++yearFrom;
            list.addAll(listHourlyConsumption(resource, yearFrom, 1, yearFrom == yearTo ? hourTo : lastHour(yearFrom)));
        }
        return list;
    }

    /**
     * Retrieves a list of hourly consumption data for a specified resource at a given site over a defined period.
     *
     * @param resource the resource for which hourly consumption data is to be retrieved
     * @param site the site associated with the resource
     * @param periodCount the number of hours to include in the consumption data, counting backwards from the current time
     * @return a list of HourlyConsumption objects representing consumption data over the specified period
     */
    public final List<HourlyConsumption> listHourlyConsumption(Resource resource, Site site, int periodCount) {
        PeriodCount p = PeriodCount.hourly(site, periodCount);
        return listHourlyConsumption(resource, p.yearFrom, p.from, p.yearTo, p.to);
    }

    /**
     * Retrieves the hourly consumption for a specific resource, year, and hour.
     *
     * @param resource the resource for which the hourly consumption is retrieved
     * @param year the year for which the hourly consumption is calculated
     * @param hour the specific hour within the year for which the consumption is retrieved
     * @return the {@code HourlyConsumption} object containing the consumption data for the specified parameters
     */
    public final HourlyConsumption getHourlyConsumption(Resource resource, int year, int hour) {
        return getConsumption(resource, HourlyConsumption.class, "Year=" + year + " AND Hour=" + hour);
    }

    /**
     * Retrieves the hourly consumption for the specified resource and date.
     * The method calculates the corresponding year and hour for the given date
     * and fetches the associated hourly consumption.
     *
     * @param resource the {@link Resource} for which the hourly consumption is to be fetched
     * @param date the date, of type {@code D}, which is used to determine the year and hour
     * @param <D> a subclass of {@link java.util.Date} representing the date type
     * @return an instance of {@link HourlyConsumption} representing the hourly consumption for the specified resource and date
     */
    public final <D extends java.util.Date> HourlyConsumption getHourlyConsumption(Resource resource, D date) {
        return getHourlyConsumption(resource, DateUtility.getYear(date), DateUtility.getHourOfYear(date));
    }

    /**
     * Creates or retrieves an {@link HourlyConsumption} instance for the specified resource and date.
     * If no existing instance is found, a new virtual instance is created with the specified attributes.
     *
     * @param resource the resource for which the hourly consumption is to be retrieved or created
     * @param date the date representing the specific hour for which the consumption is to be tracked
     * @param <D> the type parameter extending {@link java.util.Date}, representing the date input type
     * @return the {@link HourlyConsumption} instance associated with the specified resource and hour
     */
    private <D extends java.util.Date> HourlyConsumption createHourlyConsumption(Resource resource, D date) {
        int y = DateUtility.getYear(date), h = DateUtility.getHourOfYear(date);
        HourlyConsumption c = getHourlyConsumption(resource, y, h);
        if(c == null) {
            c = new HourlyConsumption();
            c.setItem(this);
            c.setResource(resource);
            c.setYear(y);
            c.setHour(h);
            c.makeVirtual();
        }
        return c;
    }

    /**
     * Retrieves a list of daily consumption records for a specific resource within a given time range.
     *
     * @param resource the resource for which daily consumption records are being queried
     * @param year the year in which the daily consumption records reside
     * @param dayFrom the starting day (inclusive) of the time range, as a day-of-year value
     * @param dayTo the ending day (inclusive) of the time range, as a day-of-year value
     * @return a list of {@code DailyConsumption} objects representing the daily consumption for the specified resource and time range
     */
    public final List<DailyConsumption> listDailyConsumption(Resource resource, int year, int dayFrom, int dayTo) {
        return resource.listDailyConsumption(getId(), year, dayFrom, dayTo);
    }

    /**
     * Retrieves a list of daily consumption data for a specified resource over a range of dates.
     *
     * @param resource the resource for which the daily consumption is to be listed
     * @param yearFrom the starting year of the date range
     * @param dayFrom the starting day of the year in the date range
     * @param yearTo the ending year of the date range
     * @param dayTo the ending day of the year in the date range
     * @return a list of {@code DailyConsumption} objects representing the daily consumption data
     *         for the specified resource within the given date range
     */
    public final List<DailyConsumption> listDailyConsumption(Resource resource, int yearFrom, int dayFrom,
                                                             int yearTo, int dayTo) {
        if(yearFrom > yearTo) {
            return List.of();
        }
        if(yearFrom == yearTo) {
            return listDailyConsumption(resource, yearFrom, dayFrom, dayTo);
        }
        List<DailyConsumption> list = listDailyConsumption(resource, yearFrom, dayFrom, lastDay(yearFrom));
        while(yearFrom < yearTo) {
            ++yearFrom;
            list.addAll(listDailyConsumption(resource, yearFrom, 1, yearFrom == yearTo ? dayTo : lastDay(yearFrom)));
        }
        return list;
    }

    /**
     * Retrieves a list of daily consumption records for a given resource within a specific time period.
     *
     * @param resource the resource for which the daily consumption data is to be retrieved
     * @param site the site where the resource is being consumed
     * @param periodCount the number of days for which daily consumption data should be fetched
     * @return a list of daily consumption records spanning the specified period
     */
    public final List<DailyConsumption> listDailyConsumption(Resource resource, Site site, int periodCount) {
        PeriodCount p = PeriodCount.daily(site, periodCount);
        return listDailyConsumption(resource, p.yearFrom, p.from, p.yearTo, p.to);
    }

    static int lastDay(int year) {
        return DateUtility.getDayOfYear(DateUtility.create(year, 12, 31));
    }

    static int lastWeek(int year) {
        return DateUtility.getWeekOfYear(DateUtility.create(year, 12, 31));
    }

    static int lastHour(int year) {
        Date date = DateUtility.create(year + 1, 1, 1); // Jan 1st next year
        date.setTime(date.getTime() - 10000); // Reduce 10 seconds to make it previous day around midnight
        return DateUtility.getWeekOfYear(date);
    }

    /**
     * Retrieves daily consumption data for a specific resource on a given day of a year.
     *
     * @param resource the resource for which the daily consumption is being retrieved
     * @param year the year during which the daily consumption is to be retrieved
     * @param day the day of the year (1-365 or 1-366 for leap years) for which the daily consumption is to be retrieved
     * @return a {@code DailyConsumption} object representing the consumption data for the specified resource
     *         on the given day, or {@code null} if no data is available
     */
    public final DailyConsumption getDailyConsumption(Resource resource, int year, int day) {
        return getConsumption(resource, DailyConsumption.class, "Year=" + year + " AND Day=" + day);
    }

    /**
     * Retrieves the daily consumption for a specific resource and date.
     * The method calculates the corresponding year and day of the year from the provided date
     * and fetches the daily consumption data accordingly.
     *
     * @param resource the resource for which the daily consumption is being retrieved
     * @param date the date for which the daily consumption is being retrieved; must extend {@link java.util.Date}
     * @param <D> the date type, which must be a subclass of {@link java.util.Date}
     * @return the {@link DailyConsumption} object representing the consumption data for the given resource and date
     */
    public final <D extends java.util.Date> DailyConsumption getDailyConsumption(Resource resource, D date) {
        return getDailyConsumption(resource, DateUtility.getYear(date), DateUtility.getDayOfYear(date));
    }

    /**
     * Creates or retrieves a {@link DailyConsumption} object for the specified resource and date.
     * If a matching existing daily consumption record does not exist, a new virtual record is created,
     * initialized with the given resource, year, and day of the year.
     *
     * @param <D> The type representing the date, which extends {@link java.util.Date}.
     * @param resource The resource for which the daily consumption should be obtained or created.
     * @param date The date for which the daily consumption is relevant.
     * @return The {@link DailyConsumption} object corresponding to the given resource and date.
     *         If no existing record is found, a new virtual record is returned.
     */
    private <D extends java.util.Date> DailyConsumption createDailyConsumption(Resource resource, D date) {
        int y = DateUtility.getYear(date), d = DateUtility.getDayOfYear(date);
        DailyConsumption c = getDailyConsumption(resource, y, d);
        if(c == null) {
            c = new DailyConsumption();
            c.setItem(this);
            c.setResource(resource);
            c.setYear(y);
            c.setDay(d);
            c.makeVirtual();
        }
        return c;
    }

    /**
     * Retrieves the hourly consumption for the specified resource.
     * If the hourly consumption for the resource does not exist, it either fetches
     * it based on the current timestamp adjusted for the site's timezone or creates
     * a new record for the resource.
     *
     * @param resource the resource for which the hourly consumption is being retrieved
     * @return the {@code HourlyConsumption} object representing the resource's consumption
     *         for the hour, either fetched or newly created
     */
    public final HourlyConsumption getHourlyConsumption(Resource resource) {
        Timestamp date = getSite().date(DateUtility.now());
        if(hourlyConsumption == null) {
            hourlyConsumption = new HashMap<>();
        }
        HourlyConsumption hs = hourlyConsumption.get(resource.getId());
        if(hs == null) {
            hs = getHourlyConsumption(resource, date);
            if(hs == null) {
                date = new Timestamp(date.getTime() - 3600000L);
                hs = createHourlyConsumption(resource, date);
            }
            hourlyConsumption.put(resource.getId(), hs);
        }
        return hs;
    }

    /**
     * Retrieves the daily consumption record for a specific resource. If no record exists for the current date,
     * it attempts to retrieve or create a new record associated with the resource and the current timestamp.
     *
     * @param resource the resource for which the daily consumption information is to be retrieved.
     * @return the {@code DailyConsumption} instance associated with the specified resource.
     */
    public final DailyConsumption getDailyConsumption(Resource resource) {
        Timestamp date = getSite().date(DateUtility.now());
        if(dailyConsumption == null) {
            dailyConsumption = new HashMap<>();
        }
        DailyConsumption ds = dailyConsumption.get(resource.getId());
        if(ds == null) {
            ds = getDailyConsumption(resource, date);
            if(ds == null) {
                date = new Timestamp(date.getTime() - 3600000L);
                ds = createDailyConsumption(resource, date);
            }
            dailyConsumption.put(resource.getId(), ds);
        }
        return ds;
    }

    /**
     * Retrieves the weekly consumption for a given resource. If the weekly consumption
     * data does not exist, it will compute or create a new instance of the weekly consumption
     * for the resource based on the current site date. The method ensures data is updated and
     * cached for future retrievals.
     *
     * @param resource the resource object for which the weekly consumption is to be retrieved
     * @return the {@link WeeklyConsumption} instance representing the weekly consumption
     *         for the specified resource
     */
    public final WeeklyConsumption getWeeklyConsumption(Resource resource) {
        Timestamp date = getSite().date(DateUtility.now());
        if(weeklyConsumption == null) {
            weeklyConsumption = new HashMap<>();
        }
        WeeklyConsumption ws = weeklyConsumption.get(resource.getId());
        if(ws == null) {
            ws = getWeeklyConsumption(resource, date);
            if(ws == null) {
                date = new Timestamp(date.getTime() - 168 * 3600000L); // 1 week = 168 hours
                ws = createWeeklyConsumption(resource, date);
            }
            weeklyConsumption.put(resource.getId(), ws);
        }
        return ws;
    }

    /**
     * Retrieves the monthly consumption data for the specified resource.
     * If no existing data is available, it attempts to generate new data for the current
     * or the previous month based on the site's time configuration and stores it for future use.
     *
     * @param resource the resource for which monthly consumption data is to be retrieved.
     * @return a {@code MonthlyConsumption} object representing the consumption details of the resource.
     */
    public final MonthlyConsumption getMonthlyConsumption(Resource resource) {
        Timestamp date = getSite().date(DateUtility.now());
        if(monthlyConsumption == null) {
            monthlyConsumption = new HashMap<>();
        }
        MonthlyConsumption ms = monthlyConsumption.get(resource.getId());
        if(ms == null) {
            ms = getMonthlyConsumption(resource, date);
            if(ms == null) {
                date = DateUtility.addMonth(date, -1);
                ms = createMonthlyConsumption(resource, date);
            }
            monthlyConsumption.put(resource.getId(), ms);
        }
        return ms;
    }

    /**
     * Retrieves the yearly consumption details for a specific resource. If the yearly
     * consumption is not already calculated or available, it attempts to initialize
     * and cache a new instance for the specified resource.
     *
     * @param resource the resource for which to retrieve the yearly consumption data
     * @return the {@link YearlyConsumption} object representing the consumption details of the resource for the relevant year
     */
    public final YearlyConsumption getYearlyConsumption(Resource resource) {
        Timestamp date = getSite().date(DateUtility.now());
        if(yearlyConsumption == null) {
            yearlyConsumption = new HashMap<>();
        }
        YearlyConsumption ys = yearlyConsumption.get(resource.getId());
        if(ys == null) {
            ys = getYearlyConsumption(resource, date);
            if(ys == null) {
                date = DateUtility.addYear(date, -1);
                ys = createYearlyConsumption(resource, date);
            }
            yearlyConsumption.put(resource.getId(), ys);
        }
        return ys;
    }

    /**
     * Resets the consumption data for the unit by clearing all stored consumption records.
     * <p></p>
     * This method sets the hourly, daily, weekly, monthly, and yearly consumption
     * values to {@code null}, effectively removing any tracked consumption data
     * for these time periods. It is used to clear out existing consumption data
     * and reset the tracking state for the unit.
     */
    void resetConsumption() {
        hourlyConsumption = null;
        dailyConsumption = null;
        weeklyConsumption = null;
        monthlyConsumption = null;
        yearlyConsumption = null;
    }

    /**
     * Represents a time-based data period associated with a specific site date.
     * This record is used to define and encapsulate the start (from) and end (to)
     * timestamps of a data period in milliseconds, as well as a corresponding site-adjusted
     * date.
     * <p></p>
     * The `DataPeriod` can be utilized in operations requiring time intervals to
     * manage or process data within a specific period. Instances of this record may
     * also be used for calculations or validations against resource consumption
     * metrics.
     *
     * @param siteDate The site-adjusted date associated with this data period.
     * @param from     The start time of the data period, represented in milliseconds since epoch.
     * @param to       The end time of the data period, represented in milliseconds since epoch.
     *
     * @author Syam
     */
    record DataPeriod(Date siteDate, long from, long to) {
    }

    /**
     * Represents a count of a period defined by a starting year and month,
     * and an ending year and month.
     * A record that stores two time periods in terms of:
     * - Starting year and month.
     * - Ending year and month.
     * This class is immutable and provides built-in support for
     * component-based access methods.
     * Components:
     * - yearFrom: The starting year of the period.
     * - from: The starting month of the period.
     * - yearTo: The ending year of the period.
     * - to: The ending month of the period.
     *
     * @author Syam
     */
    record PeriodCount(int yearFrom, int from, int yearTo, int to) {

        /**
         * Creates a PeriodCount representing a range of months starting from a specific
         * number of months in the past up to the current month, based on the site's local date.
         *
         * @param site the site whose local date is used as the time reference for calculation
         * @param periodCount the number of months to include in the period, counting backwards from the current month
         * @return a PeriodCount object representing the calculated range of months and years
         */
        public static PeriodCount monthly(Site site, int periodCount) {
            --periodCount;
            Date siteDate = site.date(new Date(System.currentTimeMillis()));
            int yearTo = DateUtility.getYear(siteDate);
            int monthTo = DateUtility.getMonth(siteDate);
            int monthFrom = monthTo - periodCount;
            int yearFrom = yearTo;
            while (monthFrom < 1) {
                --yearFrom;
                monthFrom += 12;
            }
            return new PeriodCount(yearFrom, monthFrom, yearTo, monthTo);
        }

        /**
         * Computes the period information on a weekly basis going backwards from the current week.
         *
         * @param site the Site object representing the context of the computation
         * @param periodCount the number of weeks to count back to determine the starting week
         * @return a PeriodCount object that encapsulates the range of years and weeks from the calculated start week to the current week
         */
        public static PeriodCount weekly(Site site, int periodCount) {
            --periodCount;
            Date siteDate = site.date(new Date(System.currentTimeMillis()));
            int yearTo = DateUtility.getYear(siteDate);
            int weekTo = DateUtility.getWeekOfYear(siteDate);
            int weekFrom = weekTo - periodCount;
            int yearFrom = yearTo;
            while (weekFrom < 1) {
                --yearFrom;
                weekFrom += lastWeek(yearFrom);
            }
            return new PeriodCount(yearFrom, weekFrom, yearTo, weekTo);
        }

        /**
         * Computes a period based on the daily breakdown of the given period count
         * starting from the current date. Adjusts for year transitions when the period
         * count exceeds the number of days in the current year.
         *
         * @param site the site object providing contextual information such as the current date
         * @param periodCount the number of daily periods to calculate, where
         *                    the most recent day corresponds to a count of 1
         * @return a PeriodCount object representing the start and end dates of the calculated period
         */
        public static PeriodCount daily(Site site, int periodCount) {
            --periodCount;
            Date date = site.date(new Date(System.currentTimeMillis()));
            int yearTo = DateUtility.getYear(date);
            int dayTo = DateUtility.getDayOfYear(date);
            int dayFrom = dayTo - periodCount;
            int yearFrom = yearTo;
            while (dayFrom < 1) {
                --yearFrom;
                dayFrom += lastDay(yearFrom);
            }
            return new PeriodCount(yearFrom, dayFrom, yearTo, dayTo);
        }

        /**
         * Computes the start and end of an hourly period based on the given `Site` and period count.
         * The method calculates the period in hours starting from the current time
         * and adjusts appropriately for transitions between years.
         *
         * @param site the site information used for obtaining the current date and time
         * @param periodCount the number of hours to include in the period, starting from the current hour
         * @return a PeriodCount object representing the calculated period, containing the start year and hour, and end year and hour
         */
        public static PeriodCount hourly(Site site, int periodCount) {
            --periodCount;
            Date date = site.date(new Date(System.currentTimeMillis()));
            int yearTo = DateUtility.getYear(date);
            int hourTo = DateUtility.getHourOfYear(date);
            int hourFrom = hourTo - periodCount;
            int yearFrom = yearTo;
            while (hourFrom < 1) {
                --yearFrom;
                hourFrom += lastHour(yearFrom);
            }
            return new PeriodCount(yearFrom, hourFrom, yearTo, hourTo);
        }
    }
}
