package com.storedobject.iot;

import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a unit with various attributes and methods for handling its statistics,
 * ordinality, block association, and other operations. A unit is a fundamental entity
 * that is associated with a site, block, and statistical data used for resource
 * consumption analysis and monitoring over different time periods.
 * <br/>
 * This class extends {@code AbstractUnit} and provides specific implementations and
 * utilities for managing unit-related tasks in a structured and efficient manner.
 */
public abstract class Unit extends AbstractUnit {

    private static final String[] ordinalityValues = new String[] {
            "1st",
            "2nd",
            "3rd",
            "4th",
            "5th",
            "6th",
            "7th",
            "8th",
            "9th",
            "10th",
            "11th",
            "12th",
            "13th",
            "14th",
            "15th",
            "16th",
            "17th",
            "18th",
            "19th",
            "20th",
            "21st",
            "22nd",
            "23rd",
            "24th",
    };
    private Id blockId;
    private Block block;
    private int code, ordinality;
    private Site site;
    private Map<String, HourlyStatistics> hourlyStatistics;
    private Map<String, DailyStatistics> dailyStatistics;
    private Map<String, WeeklyStatistics> weeklyStatistics;
    private Map<String, MonthlyStatistics> monthlyStatistics;
    private Map<String, YearlyStatistics> yearlyStatistics;

    /**
     * Default constructor for the Unit class.
     * Initializes a new instance of the Unit class.
     */
    public Unit() {
    }

    /**
     * Configures the specified {@code Columns} instance by adding predefined column definitions.
     *
     * @param columns the {@code Columns} object to be configured; it will have additional column definitions added,
     *                specifically "Block" with data type "id", "Code" with data type "int", and "Ordinality" with data type "int".
     */
    public static void columns(Columns columns) {
        columns.add("Block", "id");
        columns.add("Code", "int");
        columns.add("Ordinality", "int");
    }

    /**
     * Configures indices for the Unit instance.
     *
     * @param indices The Indices object to which index definitions are added.
     */
    public static void indices(Indices indices) {
        indices.add("Code,T_Family", true);
        indices.add("Block");
    }

    /**
     * Provides a list of links to related classes or resources in the system.
     *
     * @return An array of strings where each string represents a link with the format "Description|ClassPath".
     */
    public static String[] links() {
        return new String[] {
                "Skip Control Schedules|com.storedobject.iot.ControlSchedule",
        };
    }

    /**
     * Overridden method that is invoked when the object's state has been saved successfully.
     * This method calls the parent class's `saved()` method to ensure that any additional
     * saving logic from the superclass is executed. After the superclass logic is executed,
     * it triggers a refresh operation for the `DataSet` to ensure any associated data is updated
     * and synchronized if applicable.
     *
     * @throws Exception if an error occurs during the save or refresh process
     */
    @Override
    public void saved() throws Exception {
        super.saved();
        DataSet.refresh();
    }

    /**
     * Retrieves a Unit instance that matches the given name.
     *
     * @param name the name of the Unit to retrieve
     * @return the Unit instance corresponding to the given name, or null if no match is found
     */
    public static Unit get(String name) {
        return StoredObjectUtility.get(Unit.class, "Name", name, true);
    }

    /**
     * Lists the {@code Unit} objects based on the given name.
     *
     * @param name The name of the unit to list.
     * @return An iterator providing access to the list of {@code Unit} objects matching the given name.
     */
    public static ObjectIterator<? extends Unit> list(String name) {
        return StoredObjectUtility.list(Unit.class, "Name", name, true);
    }

    /**
     * Provides a hint about the type of object or structure used within this context.
     *
     * @return A constant value representing a hint, such as ObjectHint.SMALL_LIST.
     */
    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    /**
     * Sets the block identifier for this Unit.
     *
     * @param blockId the identifier of the block to associate with this Unit
     */
    public void setBlock(Id blockId) {
        this.blockId = blockId;
    }

    /**
     * Sets the block for the unit using the specified block identifier value.
     *
     * @param idValue The identifier value of the block as a BigDecimal.
     */
    public void setBlock(BigDecimal idValue) {
        setBlock(new Id(idValue));
    }

    /**
     * Sets the block associated with this unit.
     *
     * @param block The block to be set. If null, the block ID will be set to null; otherwise,
     *              the block's ID will be used to set the block.
     */
    public void setBlock(Block block) {
        setBlock(block == null ? null : block.getId());
    }

    /**
     * Retrieves the block ID associated with this unit.
     *
     * @return The block ID of the unit as an instance of {@code Id}.
     */
    @Column(order = 200)
    public Id getBlockId() {
        return blockId;
    }

    /**
     * Retrieves the associated {@link Block} for the current Unit. If the block is not already loaded,
     * it is fetched and initialized using the {@code getRelated()} method with the block ID.
     *
     * @return The associated Block instance. If the block is not set, it will attempt to retrieve it
     *         using the related block ID.
     */
    public Block getBlock() {
        if(block == null) {
            block = getRelated(Block.class, blockId);
        }
        return block;
    }

    /**
     * Sets the unique code for the Unit.
     *
     * @param code the unique integer code that identifies the Unit
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Retrieves the unique unit code associated with the unit.
     *
     * @return the unique code of the unit
     */
    @Column(order = 300, caption = "Unique Unit Code")
    public int getCode() {
        return code;
    }

    /**
     * Sets the ordinality value for the unit.
     *
     * @param ordinality The ordinality value to set.
     */
    public void setOrdinality(int ordinality) {
        this.ordinality = ordinality;
    }

    /**
     * Gets the ordinality of this unit, which represents its order or sequence in some context.
     *
     * @return the ordinality of the unit as an integer
     */
    @Column(order = 400)
    public int getOrdinality() {
        return ordinality;
    }

    /**
     * Retrieves the descriptive string corresponding to the current object's ordinality value.
     *
     * @return The string representation of the ordinality value for this instance.
     */
    public String getOrdinalityValue() {
        return getOrdinalityValue(ordinality);
    }

    /**
     * Retrieves the ordinality value corresponding to the specified ordinality index.
     *
     * @param ordinality The ordinality index to determine the ordinality value.
     * @return A string representation of the ordinality value based on the provided index.
     */
    public static String getOrdinalityValue(int ordinality) {
        return ordinalityValues[ordinality % ordinalityValues.length];
    }

    /**
     * Retrieves an array of ordinality values associated with the Unit class.
     *
     * @return A string array containing the ordinality values.
     */
    public static String[] getOrdinalityValues() {
        return ordinalityValues;
    }

    /**
     * Retrieves the layout style identifier for this unit's associated block.
     *
     * @return An integer representing the layout style of the associated block.
     */
    public int getLayoutStyle() {
        return getBlock().getLayoutStyle();
    }

    /**
     * Validates the Unit's data by ensuring the {@code blockId} is associated with an instance of the {@code Block}
     * class and invoking additional data validation logic from the superclass.
     *
     * @param tm The {@code TransactionManager} used to perform validation operations.
     * @throws Exception If validation fails or an error occurs during the validation process.
     */
    @Override
    public void validateData(TransactionManager tm) throws Exception {
        blockId = tm.checkType(this, blockId, Block.class, false);
        super.validateData(tm);
    }

    /**
     * Retrieves the associated {@code Site} for this unit. If the site is not already set,
     * it initializes the {@code site} field by fetching the site from the associated block.
     *
     * @return The {@code Site} object associated with this unit. If the site is not set, it
     *         retrieves the site from the block and returns it.
     */
    @Override
    public final Site getSite() {
        if(site == null) {
            site = getBlock().getSite();
        }
        return site;
    }

    /**
     * Retrieves the identifier of the associated site.
     * If the site is defined for the current instance, its identifier is returned.
     * Otherwise, the site identifier is retrieved from the associated block.
     *
     * @return The identifier of the site as an {@code Id} object.
     */
    public final Id getSiteId() {
        return site == null ? getBlock().getSiteId() : site.getId();
    }

    /**
     * Retrieves the type of this unit as an instance of UnitType.
     * This method attempts to create and return a UnitType object
     * based on the class of this unit. If an exception occurs during
     * the creation process, it returns null.
     *
     * @return The UnitType instance representing the type of this unit,
     *         or null if the type cannot be determined.
     */
    public final UnitType getType() {
        try {
            return UnitType.create(null, getClass());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Calculates the total consumption of a specified resource for this unit
     * and all its associated active and non-independent unit items within a given period.
     *
     * @param resource the resource identifier for which consumption is calculated
     * @param from the start time (in milliseconds since epoch) of the period
     * @param to the end time (in milliseconds since epoch) of the period
     * @return the total consumption as a Double, which is the sum of this unit's
     *         consumption and the consumptions of associated unit items, or null if no consumption was computed
     */
    @Override
    Double consumption(int resource, long from, long to) {
        Double c = computeConsumption(resource, from, to);
        double value = 0;
        boolean foundAny = false;
        List<UnitItem> items = list(UnitItem.class, "Unit=" + getId() + " AND Active AND NOT Independent",
                true).toList();
        for(UnitItem item: items) {
            Double v = item.computeConsumption(resource, from, to);
            if(v == null) {
                continue;
            }
            foundAny = true;
            value += v;
        }
        if(foundAny) {
            return c == null ? value : (value + c);
        }
        return c;
    }

    /**
     * Recomputes all statistics for the current unit by first deleting the
     * existing statistics and then regenerating them.
     *
     * @param tm The {@link TransactionManager} instance used for performing the
     *           necessary database transactions.
     * @throws Exception If an error occurs during the recomputation process,
     *                   including database transaction failures or operations on
     *                   statistics.
     */
    public void recomputeStatistics(TransactionManager tm) throws Exception {
        tm.transact(t -> {
            for(Statistics<?> statistics: list(Statistics.class, "Unit=" + getId(), true)) {
                statistics.delete(t);
            }
        });
        computeStatistics(tm);
    }

    /**
     * Computes and updates statistics for the given transaction manager based on the unit type and data classes
     * associated with the unit.
     *
     * @param tm The transaction manager instance responsible for managing database transactions during
     *           the computation of statistics.
     * @throws Exception If any exception occurs during the computation or interaction with the data methods.
     */
    public void computeStatistics(TransactionManager tm) throws Exception {
        UnitType ut = UnitType.getFor(getClass().getName());
        if(ut == null) {
            return;
        }
        StringList names = StringList.create(ut.getStatistics());
        if(names.isEmpty()) {
            return;
        }
        List<Class<? extends Data>> dataClasses = new ArrayList<>();
        list(UnitDefinition.class, "UnitType=" + ut.getId()).forEach(ud -> {
            Class<? extends Data> dataClass = ud.getDataClass();
            if(dataClass != null) {
                dataClasses.add(dataClass);
            }
        });
        if(dataClasses.isEmpty()) {
            return;
        }
        Method m;
        for(String name: names) {
            for (Class<? extends Data> dataClass : dataClasses) {
                try {
                    m = dataClass.getMethod("get" + name);
                    if(Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers()) && (
                            m.getReturnType() == double.class || m.getReturnType() == long.class ||
                                    m.getReturnType() == int.class)) {
                        computeStatistics(tm, dataClass, name);
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }
        }
    }

    private void computeStatistics(TransactionManager tm, Class<? extends Data> dataClass, String name)
            throws Exception {
        int result = 1;
        while (result == 1) {
            result = statistics(tm, dataClass, name);
        }
    }

    private int statistics(TransactionManager tm, Class<? extends Data> dataClass, String name)
            throws Exception {
        ResultSet rs;
        int y, h;
        Date dateGMT;
        String condition = "Unit=" + getId() + " AND Name='" + name + "'";
        try (Query q = query(HourlyStatistics.class, "/Max(Year)", condition)) {
            rs = q.getResultSet();
            y = rs.getInt(1);
            if(rs.wasNull()) {
                y = -1;
            }
        }
        if(y == -1) { // Never computed
            long first;
            try (Query q = query(dataClass, "/Min(CollectedAt)", "Unit=" + getId())) {
                rs = q.getResultSet();
                first = rs.getLong(1);
                if(rs.wasNull()) {
                    return 0;
                }
                dateGMT = new Date(first);
            }
        } else {
            try (Query q = query(HourlyStatistics.class, "/Max(Hour)", condition + " AND Year=" + y)) {
                rs = q.getResultSet();
                h = rs.getInt(1);
                dateGMT = DateUtility.create(y, 1, 1); // Site date
                dateGMT = new Date(dateGMT.getTime() + (h * 3600000L)); // Hour offset + 1 hour
                dateGMT = getSite().dateGMT(dateGMT); // To GMT
            }
        }
        int result;
        while ((result = statistics(tm, dataClass, dateGMT, name)) == -2) { // Data gap?
            dateGMT = new Date(dateGMT.getTime() + 3600000L); // Look in the subsequent hour
        }
        return result;
    }

    private int statistics(TransactionManager tm, Class<? extends Data> dataClass, Date dateGMT, String name)
            throws Exception {
        DataPeriod dataPeriod = getDataPeriod(dateGMT);
        if(dataPeriod == null) {
            return -1;
        }
        Date siteDate = dataPeriod.siteDate();
        List<Statistics<?>> statisticsList = new ArrayList<>();
        HourlyStatistics hs = createHourlyStatistics(name, dataPeriod.siteDate());
        if(!hs.isVirtual()) {
            return -1;
        }
        Query query = query(dataClass, name, "Unit=" + getId() + " AND CollectedAt BETWEEN "
                + dataPeriod.from() + " AND " + dataPeriod.to());
        try(query) {
            for (ResultSet rs : query) {
                hs.add(rs.getDouble(1));
            }
        }
        if(hs.getCount() == 0) {
            return -2; // Data gap?
        }
        statisticsList.add(hs);
        DailyStatistics ds = createDailyStatistics(name, siteDate);
        ds.add(hs);
        WeeklyStatistics ws = createWeeklyStatistics(name, siteDate);
        ws.add(hs);
        MonthlyStatistics ms = createMonthlyStatistics(name, siteDate);
        ms.add(hs);
        YearlyStatistics ys = createYearlyStatistics(name, siteDate);
        ys.add(hs);
        statisticsList.add(hs);
        statisticsList.add(ds);
        statisticsList.add(ws);
        statisticsList.add(ms);
        statisticsList.add(ys);
        tm.transact(t -> {
            for(Statistics<?> c: statisticsList) {
                if(c.isVirtual()) {
                    c.makeNew();
                }
                c.save(t);
            }
        });
        return 1;
    }

    private <T extends Statistics<?>> T getStatistics(String name, Class<T> cClass, String condition, String orderBy) {
        return statistics(name, cClass, condition, orderBy).single(false);
    }

    private <T extends Statistics<?>> List<T> listStatistics(String name, Class<T> cClass, String condition, String orderBy) {
        return statistics(name, cClass, condition, orderBy).toList();
    }

    private <T extends Statistics<?>> ObjectIterator<T> statistics(String name, Class<T> cClass, String condition, String orderBy) {
        Id id = unitId4Statistics(name);
        if(Id.isNull(id)) {
            return ObjectIterator.create();
        }
        return list(cClass, condition + " AND Unit=" + id + " AND Name='" + name + "'", "Unit,Name,Year"
                + (orderBy == null ? "" : ("," + orderBy)));
    }

    Id unitId4Statistics(String name) {
        return getId();
    }

    /**
     * Retrieves a list of yearly statistics for a given name and within a specified year range.
     *
     * @param name the name for which statistics are being retrieved
     * @param yearFrom the starting year of the range
     * @param yearTo the ending year of the range
     * @return a list of YearlyStatistics objects within the specified year range for the given name
     */
    public final List<YearlyStatistics> listYearlyStatistics(String name, int yearFrom, int yearTo) {
        String c;
        if(yearFrom == yearTo) {
            c = "Year=" + yearFrom;
        } else {
            c = "Year BETWEEN " + yearFrom + " AND " + yearTo;
        }
        return listStatistics(name, YearlyStatistics.class, c, null);
    }

    /**
     * Retrieves a list of yearly statistics for a specified entity within a given time period.
     *
     * @param name the name of the entity for which yearly statistics are to be retrieved
     * @param site the site associated with the entity
     * @param periodCount the number of years for which statistics should be retrieved, counting backwards from the current year
     * @return a list of YearlyStatistics representing the yearly data for the specified entity and period
     */
    public final List<YearlyStatistics> listYearlyStatistics(String name, Site site, int periodCount) {
        --periodCount;
        int yearTo = DateUtility.getYear(site.date(new Date(System.currentTimeMillis())));
        return listYearlyStatistics(name, yearTo - periodCount, yearTo);
    }

    /**
     * Retrieves the yearly statistics for a given name and year.
     *
     * @param name the name identifier for which the statistics are to be retrieved
     * @param year the year for which the statistics are requested
     * @return an instance of YearlyStatistics containing the data for the specified name and year
     */
    public final YearlyStatistics getYearlyStatistics(String name, int year) {
        return getStatistics(name, YearlyStatistics.class, "Year=" + year, null);
    }

    /**
     * Retrieves the yearly statistics for the given name and date.
     *
     * @param name the name for which the yearly statistics are to be retrieved
     * @param date the date used to determine the year for the statistics
     * @return the yearly statistics corresponding to the given name and year derived from the date
     */
    public final <D extends java.util.Date> YearlyStatistics getYearlyStatistics(String name, D date) {
        return getYearlyStatistics(name, DateUtility.getYear(date));
    }

    private <D extends java.util.Date> YearlyStatistics createYearlyStatistics(String name, D date) {
        int y = DateUtility.getYear(date);
        YearlyStatistics c = getYearlyStatistics(name, y);
        if(c == null) {
            c = new YearlyStatistics();
            c.setUnit(this);
            c.setName(name);
            c.setYear(y);
            c.makeVirtual();
        }
        return c;
    }

    /**
     * Retrieves a list of monthly statistics based on the specified parameters.
     *
     * @param name the name of the entity for which statistics are retrieved
     * @param year the year for the data to be retrieved
     * @param monthFrom the starting month (inclusive) for the data range
     * @param monthTo the ending month (inclusive) for the data range
     * @return a list of MonthlyStatistics objects representing the retrieved data
     */
    public final List<MonthlyStatistics> listMonthlyStatistics(String name, int year, int monthFrom, int monthTo) {
        if(monthFrom == monthTo) {
            return listStatistics(name, MonthlyStatistics.class, "Year=" + year + " AND Month=" + monthFrom, "Month");
        }
        if(monthFrom < monthTo) {
            return listStatistics(name, MonthlyStatistics.class, "Year=" + year + " AND Month BETWEEN "
                    + monthFrom + " AND " + monthTo, "Month");
        }
        List<MonthlyStatistics> con = listMonthlyStatistics(name, year, monthFrom, 12);
        con.addAll(listMonthlyStatistics(name, year + 1, 1, monthTo));
        return con;
    }

    /**
     * Retrieves a list of monthly statistics for a given name within a specified date range.
     *
     * @param name the name for which statistics need to be retrieved
     * @param yearFrom the starting year of the desired date range
     * @param monthFrom the starting month of the desired date range
     * @param yearTo the ending year of the desired date range
     * @param monthTo the ending month of the desired date range
     * @return a list of MonthlyStatistics objects within the specified range;
     *         an empty list if the range is invalid or no statistics are available
     */
    public final List<MonthlyStatistics> listMonthlyStatistics(String name, int yearFrom, int monthFrom,
                                                               int yearTo, int monthTo) {
        if(yearFrom > yearTo) {
            return List.of();
        }
        if(monthFrom == monthTo) {
            return listMonthlyStatistics(name, yearFrom, monthFrom, monthFrom);
        }
        List<MonthlyStatistics> con = listMonthlyStatistics(name, yearFrom, monthFrom, 12);
        while(yearFrom < yearTo) {
            ++yearFrom;
            con.addAll(listMonthlyStatistics(name, yearFrom, 1, yearFrom == yearTo ? monthTo : 12));
        }
        return con;
    }

    /**
     * Retrieves a list of monthly statistics for a given site and time period.
     *
     * @param name the name or identifier for the entity whose statistics are being retrieved
     * @param site the site for which the statistics are to be generated
     * @param periodCount the number of months for which the statistics should be retrieved
     * @return a list of MonthlyStatistics objects containing the requested data
     */
    public final List<MonthlyStatistics> listMonthlyStatistics(String name, Site site, int periodCount) {
        PeriodCount p = PeriodCount.monthly(site, periodCount);
        return listMonthlyStatistics(name, p.yearFrom(), p.from(), p.yearTo(), p.to());
    }

    /**
     * Retrieves the monthly statistics for the specified name, year, and month.
     *
     * @param name the identifier for which the statistics are retrieved
     * @param year the year for which the statistics are retrieved
     * @param month the month for which the statistics are retrieved
     * @return an instance of MonthlyStatistics containing the relevant data for the specified parameters
     */
    public final MonthlyStatistics getMonthlyStatistics(String name, int year, int month) {
        return getStatistics(name, MonthlyStatistics.class, "Year=" + year + " AND Month=" + month, "Month");
    }

    /**
     * Retrieves the monthly statistics for the specified name and date.
     *
     * @param name the identifier for which the monthly statistics are to be retrieved
     * @param date the date object used to extract the month and year for the statistics
     * @return the monthly statistics based on the provided name and date
     */
    public final <D extends java.util.Date> MonthlyStatistics getMonthlyStatistics(String name, D date) {
        return getMonthlyStatistics(name, DateUtility.getYear(date), DateUtility.getMonth(date));
    }

    private <D extends java.util.Date> MonthlyStatistics createMonthlyStatistics(String name, D date) {
        int y = DateUtility.getYear(date), m = DateUtility.getMonth(date);
        MonthlyStatistics c = getMonthlyStatistics(name, y, m);
        if(c == null) {
            c = new MonthlyStatistics();
            c.setUnit(this);
            c.setName(name);
            c.setYear(y);
            c.setMonth(m);
            c.makeVirtual();
        }
        return c;
    }

    /**
     * Retrieves a list of weekly statistics for the specified name and year within the given week range.
     * The method supports querying statistics for a single week, a range of weeks within the same year,
     * or for weeks spanning across two consecutive years.
     *
     * @param name the name associated with the statistics being queried
     * @param year the starting year for the week range
     * @param weekFrom the starting week number (inclusive)
     * @param weekTo the ending week number (inclusive)
     * @return a list of weekly statistics for the specified parameters
     */
    public final List<WeeklyStatistics> listWeeklyStatistics(String name, int year, int weekFrom, int weekTo) {
        if(weekFrom == weekTo) {
            return listStatistics(name, WeeklyStatistics.class, "Year=" + year + " AND Week=" + weekFrom, "Week");
        }
        if(weekFrom < weekTo) {
            return listStatistics(name, WeeklyStatistics.class, "Year=" + year + " AND Week BETWEEN "
                    + weekFrom + " AND " + weekTo, "Week");
        }
        List<WeeklyStatistics> con = listWeeklyStatistics(name, year, weekFrom, 53);
        con.addAll(listWeeklyStatistics(name, year + 1, 1, weekTo));
        return con;
    }

    /**
     * Retrieves a list of weekly statistics for a given name, over a specified range of years and weeks.
     *
     * @param name the name for which the weekly statistics are to be retrieved
     * @param yearFrom the starting year of the range
     * @param weekFrom the starting week of the range within the starting year
     * @param yearTo the ending year of the range
     * @param weekTo the ending week of the range within the ending year
     * @return a list of WeeklyStatistics for the specified range, or an empty list if the range is invalid
     */
    public final List<WeeklyStatistics> listWeeklyStatistics(String name, int yearFrom, int weekFrom,
                                                             int yearTo, int weekTo) {
        if(yearFrom > yearTo) {
            return List.of();
        }
        if(yearFrom == yearTo) {
            return listWeeklyStatistics(name, yearFrom, weekFrom, weekTo);
        }
        List<WeeklyStatistics> list = listWeeklyStatistics(name, yearFrom, weekFrom, 52);
        while(yearFrom < yearTo) {
            ++yearFrom;
            list.addAll(listWeeklyStatistics(name, yearFrom, 1, yearFrom == yearTo ? weekTo : 52));
        }
        return list;
    }

    /**
     * Retrieves a list of weekly statistics based on the specified parameters.
     *
     * @param name         the name associated with the statistics to be retrieved
     * @param site         the site for which the statistics are to be generated
     * @param periodCount  the number of periods (weeks) to include in the statistics
     * @return a list of WeeklyStatistics that match the specified criteria
     */
    public final List<WeeklyStatistics> listWeeklyStatistics(String name, Site site, int periodCount) {
        PeriodCount p = PeriodCount.weekly(site, periodCount);
        return listWeeklyStatistics(name, p.yearFrom(), p.from(), p.yearTo(), p.to());
    }

    /**
     * Retrieves weekly statistics for a specific name, year, and week.
     *
     * @param name the identifier for which the weekly statistics are to be retrieved
     * @param year the year for the desired weekly statistics
     * @param week the week in the specified year for the desired statistics
     * @return an instance of WeeklyStatistics representing the data for the specified name, year, and week
     */
    public final WeeklyStatistics getWeeklyStatistics(String name, int year, int week) {
        return getStatistics(name, WeeklyStatistics.class, "Year=" + year + " AND Week=" + week, "Week");
    }

    /**
     * Retrieves weekly statistics for the specified name and date.
     *
     * @param name the name associated with the statistics to retrieve
     * @param date the date used to determine the year and week for the statistics
     * @param <D>  a type that extends java.util.Date
     * @return a WeeklyStatistics object containing the statistics for the specified name and date
     */
    public final <D extends java.util.Date> WeeklyStatistics getWeeklyStatistics(String name, D date) {
        return getWeeklyStatistics(name, DateUtility.getYear(date), DateUtility.getWeekOfYear(date));
    }

    private <D extends java.util.Date> WeeklyStatistics createWeeklyStatistics(String name, D date) {
        int y = DateUtility.getYear(date), w = DateUtility.getWeekOfYear(date);
        WeeklyStatistics c = getWeeklyStatistics(name, y, w);
        if(c == null) {
            c = new WeeklyStatistics();
            c.setUnit(this);
            c.setName(name);
            c.setYear(y);
            c.setWeek(w);
            c.makeVirtual();
        }
        return c;
    }

    /**
     * Retrieves a list of hourly statistics for a given name and year within the specified hour range.
     *
     * @param name the name for which hourly statistics are to be retrieved
     * @param year the year for which hourly statistics are to be retrieved
     * @param hourFrom the starting hour of the range
     * @param hourTo the ending hour of the range
     * @return a list of HourlyStatistics objects containing data for the specified range
     */
    public final List<HourlyStatistics> listHourlyStatistics(String name, int year, int hourFrom, int hourTo) {
        if(hourFrom == hourTo) {
            return listStatistics(name, HourlyStatistics.class, "Year=" + year + " AND Hour=" + hourFrom, "Hour");
        }
        if(hourFrom < hourTo) {
            return listStatistics(name, HourlyStatistics.class, "Year=" + year + " AND Hour BETWEEN "
                    + hourFrom + " AND " + hourTo, "Hour");
        }
        List<HourlyStatistics> con = listHourlyStatistics(name, year, hourFrom, 53);
        con.addAll(listHourlyStatistics(name, year + 1, 1, hourTo));
        return con;
    }

    /**
     * Retrieves hourly statistics for a given name across a specified time range.
     *
     * @param name the name for which hourly statistics are being retrieved
     * @param yearFrom the starting year of the range
     * @param hourFrom the starting hour of the range in the starting year
     * @param yearTo the ending year of the range
     * @param hourTo the ending hour of the range in the ending year
     * @return a list of hourly statistics for the specified name and time range, or an empty list if the range is invalid
     */
    public final List<HourlyStatistics> listHourlyStatistics(String name, int yearFrom, int hourFrom,
                                                             int yearTo, int hourTo) {
        if(yearFrom > yearTo) {
            return List.of();
        }
        if(yearFrom == yearTo) {
            return listHourlyStatistics(name, yearFrom, hourFrom, hourTo);
        }
        List<HourlyStatistics> list = listHourlyStatistics(name, yearFrom, hourFrom, lastHour(yearFrom));
        while(yearFrom < yearTo) {
            ++yearFrom;
            list.addAll(listHourlyStatistics(name, yearFrom, 1, yearFrom == yearTo ? hourTo : lastHour(yearFrom)));
        }
        return list;
    }

    /**
     * Retrieves a list of HourlyStatistics for a specified name, site, and period count.
     *
     * @param name the identifier for which hourly statistics are requested
     * @param site the site associated with the hourly statistics
     * @param periodCount the number of periods to retrieve statistics for
     * @return a list of HourlyStatistics objects representing the data for the specified parameters
     */
    public final List<HourlyStatistics> listHourlyStatistics(String name, Site site, int periodCount) {
        PeriodCount p = PeriodCount.hourly(site, periodCount);
        return listHourlyStatistics(name, p.yearFrom(), p.from(), p.yearTo(), p.to());
    }

    /**
     * Retrieves the hourly statistics for a specific name, year, and hour.
     *
     * @param name the name identifier for which the statistics are to be retrieved
     * @param year the year for which the statistics are to be retrieved
     * @param hour the specific hour of the day (0-23) for which the statistics are to be retrieved
     * @return an instance of HourlyStatistics containing the relevant data for the specified parameters
     */
    public final HourlyStatistics getHourlyStatistics(String name, int year, int hour) {
        return getStatistics(name, HourlyStatistics.class, "Year=" + year + " AND Hour=" + hour, "Hour");
    }

    /**
     * Retrieves the hourly statistics for the specified name and date.
     *
     * @param name the name for which the hourly statistics are to be retrieved
     * @param date the date object specifying the year and hour of the statistics
     * @param <D> the type of the date parameter, which must extend java.util.Date
     * @return an HourlyStatistics object containing the statistics for the given name and date
     */
    public final <D extends java.util.Date> HourlyStatistics getHourlyStatistics(String name, D date) {
        return getHourlyStatistics(name, DateUtility.getYear(date), DateUtility.getHourOfYear(date));
    }

    private <D extends java.util.Date> HourlyStatistics createHourlyStatistics(String name, D date) {
        int y = DateUtility.getYear(date), h = DateUtility.getHourOfYear(date);
        HourlyStatistics c = getHourlyStatistics(name, y, h);
        if(c == null) {
            c = new HourlyStatistics();
            c.setUnit(this);
            c.setName(name);
            c.setYear(y);
            c.setHour(h);
            c.makeVirtual();
        }
        return c;
    }

    /**
     * Retrieves a list of daily statistics filtered by the specified name, year, and day range.
     *
     * @param name the name identifier for the daily statistics
     * @param year the year to filter the statistics
     * @param dayFrom the starting day of the range (inclusive)
     * @param dayTo the ending day of the range (inclusive)
     * @return a list of DailyStatistics objects within the specified range
     */
    public final List<DailyStatistics> listDailyStatistics(String name, int year, int dayFrom, int dayTo) {
        if(dayFrom == dayTo) {
            return listStatistics(name, DailyStatistics.class, "Year=" + year + " AND Day=" + dayFrom, "Day");
        }
        if(dayFrom < dayTo) {
            return listStatistics(name, DailyStatistics.class, "Year=" + year + " AND Day BETWEEN "
                    + dayFrom + " AND " + dayTo, "Day");
        }
        List<DailyStatistics> con = listDailyStatistics(name, year, dayFrom, 366);
        con.addAll(listDailyStatistics(name, year + 1, 1, dayTo));
        return con;
    }

    /**
     * Retrieves a list of daily statistics within a specified date range.
     *
     * @param name the name of the entity for which the statistics are being retrieved
     * @param yearFrom the starting year of the range
     * @param dayFrom the starting day of the year in the range
     * @param yearTo the ending year of the range
     * @param dayTo the ending day of the year in the range
     * @return a list of DailyStatistics objects representing the statistics for each day
     *         within the specified date range; returns an empty list if the range is invalid
     */
    public final List<DailyStatistics> listDailyStatistics(String name, int yearFrom, int dayFrom, int yearTo, int dayTo) {
        if(yearFrom > yearTo) {
            return List.of();
        }
        if(yearFrom == yearTo) {
            return listDailyStatistics(name, yearFrom, dayFrom, dayTo);
        }
        List<DailyStatistics> list = listDailyStatistics(name, yearFrom, dayFrom, lastDay(yearFrom));
        while(yearFrom < yearTo) {
            ++yearFrom;
            list.addAll(listDailyStatistics(name, yearFrom, 1, yearFrom == yearTo ? dayTo : lastDay(yearFrom)));
        }
        return list;
    }

    /**
     * Retrieves a list of daily statistics for the specified parameters.
     *
     * @param name the name associated with the daily statistics
     * @param site the site for which the daily statistics are being retrieved
     * @param periodCount the number of daily periods to be considered
     * @return a list of DailyStatistics objects for the specified parameters
     */
    public final List<DailyStatistics> listDailyStatistics(String name, Site site, int periodCount) {
        PeriodCount p = PeriodCount.daily(site, periodCount);
        return listDailyStatistics(name, p.yearFrom(), p.from(), p.yearTo(), p.to());
    }

    /**
     * Retrieves daily statistics for a given name, year, and day.
     *
     * @param name the name associated with the statistics to retrieve
     * @param year the year for which the statistics are being retrieved
     * @param day the day (within the specified year) for which the statistics are being retrieved
     * @return the daily statistics corresponding to the specified parameters
     */
    public final DailyStatistics getDailyStatistics(String name, int year, int day) {
        return getStatistics(name, DailyStatistics.class, "Year=" + year + " AND Day=" + day, "Day");
    }

    /**
     * Retrieves the daily statistics for a given name and date.
     *
     * @param name the name associated with the daily statistics
     * @param date the date for which the daily statistics are requested
     * @param <D> the type of the date, which extends java.util.Date
     * @return the daily statistics corresponding to the provided name and date
     */
    public final <D extends java.util.Date> DailyStatistics getDailyStatistics(String name, D date) {
        return getDailyStatistics(name, DateUtility.getYear(date), DateUtility.getDayOfYear(date));
    }

    private <D extends java.util.Date> DailyStatistics createDailyStatistics(String name, D date) {
        int y = DateUtility.getYear(date), d = DateUtility.getDayOfYear(date);
        DailyStatistics c = getDailyStatistics(name, y, d);
        if(c == null) {
            c = new DailyStatistics();
            c.setUnit(this);
            c.setName(name);
            c.setYear(y);
            c.setDay(d);
            c.makeVirtual();
        }
        return c;
    }

    /**
     * Retrieves the hourly statistics for a given name. If the statistics are not yet available,
     * they are fetched or created for the corresponding time period and stored for future use.
     *
     * @param name the name for which the hourly statistics are to be retrieved
     * @return the HourlyStatistics object associated with the given name
     */
    public final HourlyStatistics getHourlyStatistics(String name) {
        Timestamp date = getSite().date(DateUtility.now());
        if(hourlyStatistics == null) {
            hourlyStatistics = new HashMap<>();
        }
        HourlyStatistics hs = hourlyStatistics.get(name);
        if(hs == null) {
            hs = getHourlyStatistics(name, date);
            if(hs == null) {
                date = new Timestamp(date.getTime() - 3600000L);
                hs = createHourlyStatistics(name, date);
            }
            hourlyStatistics.put(name, hs);
        }
        return hs;
    }

    /**
     * Retrieves the DailyStatistics for a given name. If the statistics are not
     * already available, it initializes and stores a new DailyStatistics instance
     * for the specified name and date.
     *
     * @param name the name for which the DailyStatistics are retrieved or created
     * @return the DailyStatistics instance corresponding to the given name
     */
    public final DailyStatistics getDailyStatistics(String name) {
        Timestamp date = getSite().date(DateUtility.now());
        if(dailyStatistics == null) {
            dailyStatistics = new HashMap<>();
        }
        DailyStatistics ds = dailyStatistics.get(name);
        if(ds == null) {
            ds = getDailyStatistics(name, date);
            if(ds == null) {
                date = new Timestamp(date.getTime() - 3600000L);
                ds = createDailyStatistics(name, date);
            }
            dailyStatistics.put(name, ds);
        }
        return ds;
    }

    /**
     * Retrieves the weekly statistics for the given name. If the statistics
     * are not already cached, they are either fetched or created for the
     * specified name and date.
     *
     * @param name the identifier for the statistics to be retrieved
     * @return the WeeklyStatistics object corresponding to the specified name
     */
    public final WeeklyStatistics getWeeklyStatistics(String name) {
        Timestamp date = getSite().date(DateUtility.now());
        if(weeklyStatistics == null) {
            weeklyStatistics = new HashMap<>();
        }
        WeeklyStatistics ws = weeklyStatistics.get(name);
        if(ws == null) {
            ws = getWeeklyStatistics(name, date);
            if(ws == null) {
                date = new Timestamp(date.getTime() - 168 * 3600000L); // 1 week = 168 hours
                ws = createWeeklyStatistics(name, date);
            }
            weeklyStatistics.put(name, ws);
        }
        return ws;
    }

    /**
     * Retrieves the monthly statistics for the given name. If no statistics are found,
     * it attempts to generate or retrieve statistics for the previous month.
     *
     * @param name the name for which the monthly statistics are required
     * @return the MonthlyStatistics object corresponding to the given name
     */
    public final MonthlyStatistics getMonthlyStatistics(String name) {
        Timestamp date = getSite().date(DateUtility.now());
        if(monthlyStatistics == null) {
            monthlyStatistics = new HashMap<>();
        }
        MonthlyStatistics ms = monthlyStatistics.get(name);
        if(ms == null) {
            ms = getMonthlyStatistics(name, date);
            if(ms == null) {
                date = DateUtility.addMonth(date, -1);
                ms = createMonthlyStatistics(name, date);
            }
            monthlyStatistics.put(name, ms);
        }
        return ms;
    }

    /**
     * Retrieves the yearly statistics for a given name. If the statistics do not exist,
     * it will attempt to create and cache the statistics for the specified name.
     *
     * @param name the name for which the yearly statistics are retrieved
     * @return the YearlyStatistics object associated with the given name
     */
    public final YearlyStatistics getYearlyStatistics(String name) {
        Timestamp date = getSite().date(DateUtility.now());
        if(yearlyStatistics == null) {
            yearlyStatistics = new HashMap<>();
        }
        YearlyStatistics ys = yearlyStatistics.get(name);
        if(ys == null) {
            ys = getYearlyStatistics(name, date);
            if(ys == null) {
                date = DateUtility.addYear(date, -1);
                ys = createYearlyStatistics(name, date);
            }
            yearlyStatistics.put(name, ys);
        }
        return ys;
    }

    final void resetStatistics() {
        hourlyStatistics = null;
        dailyStatistics = null;
        weeklyStatistics = null;
        monthlyStatistics = null;
        yearlyStatistics = null;
    }

    /**
     * Converts the object to a displayable string format, combining the super class's
     * display string with this instance's code value in parentheses.
     *
     * @return a formatted string combining the super class display string and the code value.
     */
    @Override
    public String toDisplay() {
        return super.toDisplay() + " (" + code + ")";
    }
}