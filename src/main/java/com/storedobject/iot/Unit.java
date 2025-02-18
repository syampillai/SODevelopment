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

    public Unit() {
    }

    public static void columns(Columns columns) {
        columns.add("Block", "id");
        columns.add("Code", "int");
        columns.add("Ordinality", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Code,T_Family", true);
        indices.add("Block");
    }

    public static String[] links() {
        return new String[] {
                "Skip Control Schedules|com.storedobject.iot.ControlSchedule",
        };
    }

    @Override
    public void saved() throws Exception {
        super.saved();
        DataSet.scheduleRefresh();
    }

    public static Unit get(String name) {
        return StoredObjectUtility.get(Unit.class, "Name", name, true);
    }

    public static ObjectIterator<? extends Unit> list(String name) {
        return StoredObjectUtility.list(Unit.class, "Name", name, true);
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    public void setBlock(Id blockId) {
        this.blockId = blockId;
    }

    public void setBlock(BigDecimal idValue) {
        setBlock(new Id(idValue));
    }

    public void setBlock(Block block) {
        setBlock(block == null ? null : block.getId());
    }

    @Column(order = 200)
    public Id getBlockId() {
        return blockId;
    }

    public Block getBlock() {
        if(block == null) {
            block = getRelated(Block.class, blockId);
        }
        return block;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Column(order = 300, caption = "Unique Unit Code")
    public int getCode() {
        return code;
    }

    public void setOrdinality(int ordinality) {
        this.ordinality = ordinality;
    }

    @Column(order = 400)
    public int getOrdinality() {
        return ordinality;
    }

    public String getOrdinalityValue() {
        return getOrdinalityValue(ordinality);
    }

    public static String getOrdinalityValue(int ordinality) {
        return ordinalityValues[ordinality % ordinalityValues.length];
    }

    public static String[] getOrdinalityValues() {
        return ordinalityValues;
    }

    public int getLayoutStyle() {
        return getBlock().getLayoutStyle();
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        blockId = tm.checkType(this, blockId, Block.class, false);
        super.validateData(tm);
    }

    @Override
    public final Site getSite() {
        if(site == null) {
            site = getBlock().getSite();
        }
        return site;
    }

    public final Id getSiteId() {
        return site == null ? getBlock().getSiteId() : site.getId();
    }

    public final UnitType getType() {
        try {
            return UnitType.create(null, getClass());
        } catch (Exception e) {
            return null;
        }
    }

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

    public void recomputeStatistics(TransactionManager tm) throws Exception {
        tm.transact(t -> {
            for(Statistics statistics: list(Statistics.class, "Unit=" + getId(), true)) {
                statistics.delete(t);
            }
        });
        computeStatistics(tm);
    }

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
        List<Statistics> statisticsList = new ArrayList<>();
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
            for(Statistics c: statisticsList) {
                if(c.isVirtual()) {
                    c.makeNew();
                }
                c.save(t);
            }
        });
        return 1;
    }

    private <T extends Statistics> T getStatistics(String name, Class<T> cClass, String condition) {
        return statistics(name, cClass, condition).single(false);
    }

    private <T extends Statistics> List<T> listStatistics(String name, Class<T> cClass, String condition) {
        return statistics(name, cClass, condition).toList();
    }

    private <T extends Statistics> ObjectIterator<T> statistics(String name, Class<T> cClass, String condition) {
        return list(cClass, condition + " AND Unit=" + getId() + " AND Name='" + name + "'");
    }

    public List<YearlyStatistics> listYearlyStatistics(String name, int yearFrom, int yearTo) {
        String c;
        if(yearFrom == yearTo) {
            c = "Year=" + yearFrom;
        } else {
            c = "Year BETWEEN " + yearFrom + " AND " + yearTo;
        }
        return listStatistics(name, YearlyStatistics.class, c);
    }

    public YearlyStatistics getYearlyStatistics(String name, int year) {
        return getStatistics(name, YearlyStatistics.class, "Year=" + year);
    }

    public <D extends java.util.Date> YearlyStatistics getYearlyStatistics(String name, D date) {
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

    public List<MonthlyStatistics> listMonthlyStatistics(String name, int year, int monthFrom, int monthTo) {
        if(monthFrom == monthTo) {
            return listStatistics(name, MonthlyStatistics.class, "Year=" + year + " AND Month=" + monthFrom);
        }
        if(monthFrom < monthTo) {
            return listStatistics(name, MonthlyStatistics.class, "Year=" + year + " AND Month BETWEEN "
                    + monthFrom + " AND " + monthTo);
        }
        List<MonthlyStatistics> con = listMonthlyStatistics(name, year, monthFrom, 12);
        con.addAll(listMonthlyStatistics(name, year + 1, 1, monthTo));
        return con;
    }

    public MonthlyStatistics getMonthlyStatistics(String name, int year, int month) {
        return getStatistics(name, MonthlyStatistics.class, "Year=" + year + " AND Month=" + month);
    }

    public <D extends java.util.Date> MonthlyStatistics getMonthlyStatistics(String name, D date) {
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

    public List<WeeklyStatistics> listWeeklyStatistics(String name, int year, int weekFrom, int weekTo) {
        if(weekFrom == weekTo) {
            return listStatistics(name, WeeklyStatistics.class, "Year=" + year + " AND Week=" + weekFrom);
        }
        if(weekFrom < weekTo) {
            return listStatistics(name, WeeklyStatistics.class, "Year=" + year + " AND Week BETWEEN "
                    + weekFrom + " AND " + weekTo);
        }
        List<WeeklyStatistics> con = listWeeklyStatistics(name, year, weekFrom, 53);
        con.addAll(listWeeklyStatistics(name, year + 1, 1, weekTo));
        return con;
    }

    public WeeklyStatistics getWeeklyStatistics(String name, int year, int week) {
        return getStatistics(name, WeeklyStatistics.class, "Year=" + year + " AND Week=" + week);
    }

    public <D extends java.util.Date> WeeklyStatistics getWeeklyStatistics(String name, D date) {
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

    public List<HourlyStatistics> listHourlyStatistics(String name, int year, int hourFrom, int hourTo) {
        if(hourFrom == hourTo) {
            return listStatistics(name, HourlyStatistics.class, "Year=" + year + " AND Hour=" + hourFrom);
        }
        if(hourFrom < hourTo) {
            return listStatistics(name, HourlyStatistics.class, "Year=" + year + " AND Hour BETWEEN "
                    + hourFrom + " AND " + hourTo);
        }
        List<HourlyStatistics> con = listHourlyStatistics(name, year, hourFrom, 53);
        con.addAll(listHourlyStatistics(name, year + 1, 1, hourTo));
        return con;
    }

    public HourlyStatistics getHourlyStatistics(String name, int year, int hour) {
        return getStatistics(name, HourlyStatistics.class, "Year=" + year + " AND Hour=" + hour);
    }

    public <D extends java.util.Date> HourlyStatistics getHourlyStatistics(String name, D date) {
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

    public List<DailyStatistics> listDailyStatistics(String name, int year, int dayFrom, int dayTo) {
        if(dayFrom == dayTo) {
            return listStatistics(name, DailyStatistics.class, "Year=" + year + " AND Day=" + dayFrom);
        }
        if(dayFrom < dayTo) {
            return listStatistics(name, DailyStatistics.class, "Year=" + year + " AND Day BETWEEN "
                    + dayFrom + " AND " + dayTo);
        }
        List<DailyStatistics> con = listDailyStatistics(name, year, dayFrom, 366);
        con.addAll(listDailyStatistics(name, year + 1, 1, dayTo));
        return con;
    }

    public DailyStatistics getDailyStatistics(String name, int year, int day) {
        return getStatistics(name, DailyStatistics.class, "Year=" + year + " AND Day=" + day);
    }

    public <D extends java.util.Date> DailyStatistics getDailyStatistics(String name, D date) {
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

    public HourlyStatistics getHourlyStatistics(String name) {
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

    public DailyStatistics getDailyStatistics(String name) {
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

    public WeeklyStatistics getWeeklyStatistics(String name) {
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

    public MonthlyStatistics getMonthlyStatistics(String name) {
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

    public YearlyStatistics getYearlyStatistics(String name) {
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

    void resetStatistics() {
        hourlyStatistics = null;
        dailyStatistics = null;
        weeklyStatistics = null;
        monthlyStatistics = null;
        yearlyStatistics = null;
    }

    @Override
    public String toDisplay() {
        return super.toDisplay() + " (" + code + ")";
    }
}