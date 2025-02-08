package com.storedobject.iot;

import com.storedobject.common.ArrayListSet;
import com.storedobject.common.Sequencer;
import com.storedobject.common.StyledBuilder;
import com.storedobject.core.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class DataSet {

    private static final AtomicLong lastUpdate = new AtomicLong(0), lastPing = new AtomicLong(0);
    private static final int VALUE_COUNT = 6;
    private static final int REFRESH_RATE = 5;
    private static final Sequencer statusId = new Sequencer();
    private static final List<SiteData> sites = new ArrayList<>();
    private static final Set<Id> unitsUpdated = new HashSet<>();
    private static long time = 0;
    private static final Set<Consumer<Id>> consumers = new HashSet<>();
    private static final HashMap<String, Function<Object, String>> customFunctions = new HashMap<>();
    static {
        new Timer().schedule(new Refresher(), 3000, REFRESH_RATE * 1000L);
        refresh();
    }

    private DataSet() {
    }

    /**
     * Used internally by data acquisition logic to update the data-update time.
     *
     * @param time Time.
     */
    static void dataUpdated(Id unitId, long time) {
        if(lastUpdate.get() >= time) {
            return;
        }
        synchronized(unitsUpdated) {
            unitsUpdated.add(unitId);
        }
        lastUpdate.set(time);
        AlertGenerator.dataUpdated(time);
    }

    static void pingReceived(long time) {
        if(lastPing.get() < time) {
            lastPing.set(time);
        }
    }

    /**
     * Used internally by the statistics computation logic to inform about its status. This includes the resource
     * utilization too.
     */
    static void statisticsComputed(Site site) {
        resetStatistics(sites, site);
        informConsumers(true);
    }

    /**
     * Get the last time data was updated (GMT).
     *
     * @return Time.
     */
    public static long getTime() {
        return lastUpdate.get();
    }

    /**
     * Get the last time a ping was received.
     *
     * @return Ping-time.
     */
    public static long getPingTime() {
        return Math.max(lastPing.get(), lastUpdate.get());
    }

    public static int getValueCount() {
        return VALUE_COUNT;
    }

    public static List<SiteData> getSites() {
        return sites;
    }

    public static void refresh() {
        try {
            AlertGenerator.clearAlerts();
            sites.clear();
            customFunctions.clear();
            StoredObject.list(Site.class, "Active").forEach(SiteData::new);
        } catch(Throwable ignored) {
        }
    }

    static void scheduleRefresh() {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
            refresh();
            Controller.restart();
        }).start();
    }

    public static void register(Consumer<Id> consumer) {
        if(consumer != null) {
            Set<Id> blockIds = new HashSet<>();
            synchronized(consumers) {
                try {
                    visitUnits(u -> blockIds.add(u.getBlockId()));
                    for(Id id: blockIds) {
                        consumer.accept(id);
                    }
                    consumers.add(consumer);
                } catch(Throwable ignored) {
                }
            }
        }
    }

    public static void unregister(Consumer<Id> consumer) {
        if(consumer != null) {
            synchronized(consumers) {
                consumers.remove(consumer);
            }
        }
    }

    private static void resetStatistics(List<? extends AbstractData> rows, Site site) {
        synchronized(sites) {
            rows.stream().filter(r -> {
                if(r instanceof SiteData sd) {
                    return sd.site.getId().equals(site.getId());
                }
                return true;
            }).forEach(r -> {
                if(r instanceof UnitData ud) {
                    ud.unit.resetConsumption();
                    ud.unit.getBlock().resetConsumption();
                    ud.unit.resetStatistics();
                }
                resetStatistics(r.children(), site);
            });
        }
    }

    private static void markDirty(List<? extends AbstractData> rows) {
        synchronized(sites) {
            rows.forEach(r -> {
                r.cellStatusUpdated = false;
                markDirty(r.children());
            });
        }
    }

    private static class Refresher extends TimerTask {

        @Override
        public void run() {
            if(time >= lastUpdate.get()) {
                return;
            }
            time = lastUpdate.get();
            markDirty(sites);
            informConsumers(false);
        }
    }

    private static void informConsumers(boolean allBlocks) {
        Set<Id> blocksUpdated = new HashSet<>();
        synchronized(unitsUpdated) {
            if(allBlocks) {
                unitsUpdated.clear();
                visitUnits(u -> blocksUpdated.add(u.getBlockId()));
            } else {
                visitUnits(u -> {
                    if (unitsUpdated.contains(u.getId())) {
                        blocksUpdated.add(u.getBlockId());
                    }
                });
                unitsUpdated.clear();
            }
        }
        synchronized(consumers) {
            if(consumers.isEmpty()) {
                return;
            }
            Set<Consumer<Id>> rejected = new HashSet<>();
            for(Consumer<Id> consumer: consumers) {
                try {
                    for(Id id: blocksUpdated) {
                        consumer.accept(id);
                    }
                } catch(Throwable e) {
                    rejected.add(consumer);
                }
            }
            rejected.forEach(consumers::remove);
        }
    }

    public abstract static class DataStatus<V> {

        private final long id = statusId.next();
        final Unit unit;
        final ValueDefinition<V> valueDefinition;

        protected DataStatus(Unit unit, ValueDefinition<V> valueDefinition) {
            this.unit = unit;
            this.valueDefinition = valueDefinition;
        }

        /**
         * Retrieves the unit associated with the current data status.
         *
         * @return the unit of measurement as a {@link Unit} object.
         */
        public final Unit getUnit() {
            return unit;
        }

        /**
         * Retrieves the value definition associated with the current data status.
         *
         * @return the {@link ValueDefinition} object representing the value definition.
         */
        public ValueDefinition<V> getValueDefinition() {
            return valueDefinition;
        }

        /**
         * Sets the necessary values or configuration associated with the current data status.
         * This method is intended to be implemented by subclasses to define specific logic for setting
         * data or attributes related to the data status. (For internal use only).
         */
        abstract void set();

        /**
         * Retrieves the significance level associated with the current data status.
         *
         * @return an integer representing the significance value defined in the associated {@link ValueDefinition}.
         */
        public final int significance() {
            return valueDefinition.getSignificance();
        }

        /**
         * Get the alarm level associated with the current data status.
         * This method is intended to be implemented by subclasses to provide
         * the specific logic for handling alarm conditions.
         *
         * @return an integer representing the alarm level or code, which is
         *         defined and interpreted by the implementing class.
         */
        public abstract int alarm();

        /**
         * Determines whether the current data status condition should trigger an alert.
         * The alert condition depends on the alert flag from the value definition
         * and whether the alarm level is non-zero.
         *
         * @return true if the alert flag is enabled in the value definition and
         *         the alarm level is non-zero, otherwise false.
         */
        public final boolean alert() {
            return valueDefinition.getAlert() && alarm() != 0;
        }

        /**
         * Retrieves the status label associated with the current data status.
         * This method is intended to provide a human-readable representation
         * or description of the status, typically implemented by subclasses.
         *
         * @return a string representing the status label of the data status.
         */
        public abstract String statusLabel();

        /**
         * Retrieves the caption associated with the current data status.
         * The caption is determined based on the unit and value definition.
         *
         * @return a string representing the caption of the data status.
         */
        public final String caption() {
            return valueDefinition.getCaption(unit);
        }

        /**
         * Retrieves the label associated with the current data status.
         * The label is determined based on the unit and its corresponding value definition.
         *
         * @return a string representing the label of the data status.
         */
        public final String label() {
            return valueDefinition.getLabel(unit);
        }

        /**
         * Retrieves the tooltip information associated with the current data status.
         * The tooltip is derived based on the unit and its corresponding value definition.
         *
         * @return a string representing the tooltip of the data status.
         */
        public final String tooltip() {
            return valueDefinition.getTooltip(unit);
        }

        /**
         * Retrieves the string representation of the value associated with the current data status.
         * This method is intended to be implemented by subclasses to provide specific value formatting or logic.
         *
         * @return a string representing the value of the data status.
         */
        public abstract String value();

        /**
         * Retrieves the string representation of the value associated with the specified attribute
         * for the current data status.
         *
         * @param attribute the attribute for which the value needs to be retrieved
         * @return a string representing the value of the specified attribute
         */
        public String value(String attribute) {
            return value();
        }

        /**
         * Retrieves the string representation of the specified value, optionally including the unit.
         *
         * @param value the numerical value to be converted to a string representation
         * @param showUnit a boolean flag indicating whether the unit should be included in the output
         * @return a string representing the value, with or without the unit depending on the showUnit parameter
         */
        public String value(double value, boolean showUnit) {
            return "?";
        }

        /**
         * Retrieves the hourly statistics associated with the current data status.
         *
         * @return the {@link HourlyStatistics} object representing data specific to hourly intervals,
         *         obtained using the name from the value definition.
         */
        public HourlyStatistics hourlyStatistics() {
            return unit.getHourlyStatistics(valueDefinition.getName());
        }

        /**
         * Retrieves the daily statistics associated with the current data status.
         * The daily statistics object is obtained based on the name from the value definition.
         *
         * @return the {@link DailyStatistics} object representing daily statistics
         *         for the associated unit and value definition.
         */
        public DailyStatistics dailyStatistics() {
            return unit.getDailyStatistics(valueDefinition.getName());
        }

        /**
         * Retrieves the weekly statistics associated with the current data status.
         * This method fetches the {@link WeeklyStatistics} object using the name
         * from the associated value definition of the unit.
         *
         * @return the {@link WeeklyStatistics} object representing data specific
         *         to weekly intervals for the corresponding unit and value definition.
         */
        public WeeklyStatistics weeklyStatistics() {
            return unit.getWeeklyStatistics(valueDefinition.getName());
        }

        /**
         * Retrieves the monthly statistics associated with the current data status.
         * The statistics are determined based on the name from the value definition and
         * are specific to monthly intervals.
         *
         * @return the {@link MonthlyStatistics} object representing data for monthly
         *         intervals, obtained using the name from the value definition and
         *         associated logic in the unit.
         */
        public MonthlyStatistics monthlyStatistics() {
            return unit.getMonthlyStatistics(valueDefinition.getName());
        }

        /**
         * Retrieves the yearly statistics associated with the current data status.
         * The yearly statistics object is determined using the name from the associated value definition
         * and fetched through the unit's logic.
         *
         * @return the {@link YearlyStatistics} object representing data specific to yearly intervals
         *         for the corresponding unit and value definition.
         */
        public YearlyStatistics yearlyStatistics() {
            return unit.getYearlyStatistics(valueDefinition.getName());
        }

        /**
         * Retrieves the value associated with the current data status.
         *
         * @return the value of type V as defined by the subclass implementation.
         */
        public abstract V getValue();

        /**
         * Generates a formatted string that represents the current data status. The output
         * string combines the alarm level symbol, the associated caption, and the value
         * for the data status.
         * <br/>
         *     <pre>
         * The alarm level determines the symbol to be displayed:
         * - 3 -> ⇈
         * - 2 -> ↟
         * - 1 -> ↑
         * - -1 -> ↓
         * - -2 -> ↡
         * - -3 -> ⇊
         * - default -> "-"
         * </pre>
         * The caption is derived from the associated value definition and the unit.
         * The value is obtained from the implementation provided by the subclass.
         *
         * @return a string representation of the data status formatted as
         *         "[Alarm Symbol] [Caption]: [Value]".
         */
        public String display() {
            return switch (alarm()) {
                case 3 -> "⇈";
                case 2 -> "↟";
                case 1 -> "↑";
                case -1 -> "↓";
                case -2 -> "↡";
                case -3 -> "⇊";
                default -> "-";
            }
                    + " "
                    + caption()
                    + ": "
                    + value();
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DataStatus<?> that = (DataStatus<?>) o;
            return id == that.id;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id);
        }

        /**
         * Retrieves the unique identifier associated with the current data status.
         *
         * @return a long value representing the unique identifier.
         */
        public final long getId() {
            return id;
        }

        /**
         * Retrieves the ordinality of the current data status.
         * The ordinality is determined by the associated unit's ordinality value.
         *
         * @return an integer representing the ordinality as defined by the associated {@link Unit}.
         */
        public int ordinality() {
            return unit.getOrdinality();
        }
    }

    public static class LimitStatus extends DataStatus<Double> {

        double value;

        private LimitStatus(Unit unit, ValueLimit valueLimit) {
            super(unit, valueLimit);
        }

        /**
         * Updates the value field by retrieving the value associated with the current unit's ID.
         * This method fetches the value from the value definition based on the unit's identifier
         * and assigns it to the value field. Any exceptions that occur during this process are ignored.
         */
        @Override
        void set() {
            try {
                //noinspection DataFlowIssue
                value = valueDefinition.getValue(unit.getId());
            } catch (Throwable ignored) {
            }
        }

        /**
         * Retrieves the value definition associated with the current limit status.
         *
         * @return the {@link ValueLimit} object representing the value definition specific to limit status.
         */
        @Override
        public ValueLimit getValueDefinition() {
            return (ValueLimit) super.getValueDefinition();
        }

        /**
         * Retrieves the value associated with this instance.
         *
         * @return the value as a Double
         */
        @Override
        public Double getValue() {
            return value;
        }

        /**
         * Evaluates the current value against predefined limits and returns an alarm status based on the value range.
         * <p>
         * The method first checks whether the value is considered "unlimited". If so, it returns 0 to indicate no alarm.
         * Otherwise, it compares the current value to specific threshold values provided by {@link ValueLimit}, determining
         * the severity of the alarm as follows:
         * <pre>
         * - Returns 3 if the value exceeds or equals the highest or higher thresholds.
         * - Returns 1 if the value exceeds or equals the high threshold.
         * - Returns 0 if the value lies between the high and low thresholds.
         * - Returns -1 if the value is below the low threshold but above the lower threshold.
         * - Returns -2 if the value is below the lower threshold but above the lowest threshold.
         * - Returns -3 if the value is below the lowest threshold.
         *</pre>
         * @return An integer representing the alarm level:
         * <pre>
         *         0: No alarm for unlimited or normal range values,
         *         3: Severe high-level alarm,
         *         1: Low-level high alarm,
         *         -1: Low-level low alarm,
         *         -2: Moderate low-level alarm,
         *         -3: Critical low-level alarm.
         * </pre>
         */
        @Override
        public int alarm() {
            ValueLimit valueLimit = getValueDefinition();
            if(valueLimit.getUnlimited()) {
                return 0;
            }
            if (value >= valueLimit.getHighest()) {
                return 3;
            }
            if (value >= valueLimit.getHigher()) {
                return 3;
            }
            if (value >= valueLimit.getHigh()) {
                return 1;
            }
            if (value > valueLimit.getLow()) {
                return 0;
            }
            if (value > valueLimit.getLower()) {
                return -1;
            }
            if (value > valueLimit.getLowest()) {
                return -2;
            }
            return -3;
        }

        /**
         * Provides a textual representation of the current status based on the alarm level.
         *
         * @return A string representing the status label. Possible values are:
         *         "highest", "higher", "high", "normal", "low", "lower", or "lowest".
         */
        @Override
        public String statusLabel() {
            return switch (alarm()) {
                case 3 -> "highest";
                case 2 -> "higher";
                case 1 -> "high";
                case -1 -> "low";
                case -2 -> "lower";
                case -3 -> "lowest";
                default -> "normal";
            };
        }

        /**
         * Provides the current value as a formatted string. This method internally calls
         * the {@link #value(double, boolean)} method with the instance's default value and
         * a flag to indicate whether the unit should be shown.
         *
         * @return A string representation of the current value, potentially including the unit
         *         based on the internal configuration.
         */
        @Override
        public String value() {
            return value(value, true);
        }

        /**
         * Formats a given numeric value into a string representation. If the input value
         * is not a finite number (e.g., infinity, NaN, or extreme double limits), it returns "N/A".
         * Otherwise, the value is formatted based on the number of decimals defined in the
         * associated {@link ValueLimit} and optionally includes a unit suffix.
         *
         * @param value     The numeric value to be formatted.
         * @param showUnit  A boolean flag indicating whether the unit suffix should be included
         *                  in the formatted string.
         * @return A string representation of the value, formatted to the specified number of
         *         decimals and optionally including the unit suffix. Returns "N/A" for invalid values.
         */
        @Override
        public String value(double value, boolean showUnit) {
            if(value == Double.POSITIVE_INFINITY || value == Double.NEGATIVE_INFINITY || Double.isNaN(value)
                    || value == Double.MIN_VALUE || value == Double.MAX_VALUE) {
                return "N/A";
            }
            ValueLimit vd = getValueDefinition();
            String v = StringUtility.format(value, showUnit ? vd.getDecimals() : -1, false);
            if(showUnit) {
                v += vd.getUnitSuffix();
            }
            return v;
        }

        /**
         * Retrieves the formatted value of the specified attribute for the current status.
         * If the attribute is null or empty, it falls back to the default value method.
         * The method dynamically resolves and applies appropriate display logic for the attribute
         * if not available in the cached custom functions.
         *
         * @param attribute the name of the attribute whose value is to be retrieved. It can be null or empty,
         *                  in which case the default value is returned.
         * @return a string representing the formatted value of the specified attribute, or "None" if
         *         the corresponding value object is unavailable.
         */
        @Override
        public String value(String attribute) {
            if(attribute == null || attribute.isEmpty()) {
                return value();
            }
            ValueLimit vd = getValueDefinition();
            Object o = vd.getValueObject(unit.getId());
            if(o == null) {
                return "None";
            }
            Function<Object, String> func = customFunctions.get(attribute + "/" + getId());
            if(func == null) {
                StoredObjectUtility.MethodList methodList = StoredObjectUtility.createMethodList(o.getClass(), attribute);
                func = methodList.display(null);
                customFunctions.put(attribute + "/" + getId(), func);
            }
            return func.apply(o);
        }

        /**
         *
         */
        public String unit() {
            return getValueDefinition().getUnitSuffix();
        }
    }

    public static class AlarmStatus extends DataStatus<Boolean> {

        boolean value;

        private AlarmStatus(Unit unit, AlarmSwitch alarmSwitch) {
            super(unit, alarmSwitch);
            value = alarmSwitch.getAlarmWhen() == 0;
        }

        /**
         * Sets the value of the `value` field by retrieving it from the value definition
         * associated with the unit's ID. The method attempts to execute this operation
         * and suppresses any exceptions that occur during the process.
         */
        @Override
        void set() {
            try {
                //noinspection DataFlowIssue
                value = valueDefinition.getValue(unit.getId());
            } catch (Throwable ignored) {
            }
        }

        /**
         * Retrieves the associated {@link AlarmSwitch} value definition for the current data status.
         *
         * @return the {@link AlarmSwitch} object representing the value definition.
         */
        @Override
        public AlarmSwitch getValueDefinition() {
            return (AlarmSwitch) super.getValueDefinition();
        }

        /**
         * Returns the current boolean value of the alarm status.
         *
         * @return the current value of the alarm status, represented as a Boolean.
         */
        @Override
        public Boolean getValue() {
            return value;
        }

        /**
         * Determines the alarm status based on the current value and the defined alarm condition.
         *
         * @return an integer representing the alarm status:
         * <pre>
         *         - Returns 0 if the alarm condition is not met.
         *         - Returns -3 if the alarm should trigger but the condition is false.
         *         - Returns 3 if the alarm should trigger and the condition is true.
         * </pre>
         */
        @Override
        public int alarm() {
            if (getValueDefinition().getAlarmWhen() == 0) {
                return value ? 0 : -3;
            }
            return value ? 3 : 0;
        }

        /**
         * Returns the status label based on the alarm's state.
         *
         * @return "Off" if the alarm state is 0, otherwise "On".
         */
        @Override
        public String statusLabel() {
            return alarm() == 0 ? "Off" : "On";
        }

        /**
         * Returns the string representation of the alarm's current value.
         *
         * @return "On" if the alarm value is true, otherwise "Off".
         */
        @Override
        public String value() {
            return value ? "On" : "Off";
        }
    }

    public abstract static class AbstractData {

        private final long id = statusId.next();
        boolean cellStatusUpdated = false;
        final List<DataStatus<?>> statusList = new ArrayList<>();

        public abstract String getName();

        public abstract List<? extends AbstractData> children();

        public void prefix(DataStatus<?> cs, StyledBuilder html) {}

        public final DataStatus<?> getDataStatus(int index) {
            if (!cellStatusUpdated) {
                updateStatus();
            }
            return index < statusList.size() ? statusList.get(index) : null;
        }

        public final List<DataStatus<?>> getDataStatus() {
            if (!cellStatusUpdated) {
                updateStatus();
            }
            return statusList;
        }

        synchronized void updateStatus() {
            if (cellStatusUpdated) {
                return;
            }
            statusList.clear();
            for (AbstractData abstractData : children()) {
                abstractData.updateStatus();
                for (int i = 0; i < VALUE_COUNT; i++) {
                    DataStatus<?> cs = abstractData.getDataStatus(i);
                    if (cs == null) {
                        break;
                    }
                    statusList.add(cs);
                }
                sortStatus(statusList);
                while (statusList.size() > VALUE_COUNT) {
                    statusList.remove(statusList.size() - 1);
                }
            }
            cellStatusUpdated = true;
        }

        @Override
        public final boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AbstractData abstractData = (AbstractData) o;
            return id == abstractData.id;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id);
        }

        public final long getId() {
            return id;
        }
    }

    public static class SiteData extends AbstractData {

        private final Site site;
        private final List<UnitHeaderData> units = new ArrayList<>();

        private SiteData(Site site) {
            this.site = site;
            Map<String, UnitHeaderData> unitRows = new HashMap<>();
            StoredObject.list(UnitDefinition.class, null, "Significance DESC")
                    .forEach(
                            ud -> {
                                UnitHeaderData uhr = unitRows.get(ud.getUnitClassName());
                                if (uhr == null) {
                                    uhr = new UnitHeaderData(this.site.getId(), ud);
                                    if(!uhr.units.isEmpty()) {
                                        units.add(uhr);
                                        unitRows.put(ud.getUnitClassName(), uhr);
                                    }
                                }
                            });
            if(!units.isEmpty()) {
                sites.add(this);
            }
        }

        @Override
        public String getName() {
            return site.getName();
        }

        @Override
        public List<? extends AbstractData> children() {
            return units;
        }

        @Override
        public void prefix(DataStatus<?> cs, StyledBuilder html) {
            for (UnitHeaderData r : units) {
                if (r.paintPrefix(cs, html)) {
                    break;
                }
            }
        }

        public Site getSite() {
            return site;
        }
    }

    public static class UnitHeaderData extends AbstractData {

        private final String name;
        private final List<ValueLimit> limits = new ArrayList<>();
        private final List<AlarmSwitch> alarms = new ArrayList<>();
        private final List<UnitData> units = new ArrayList<>();

        private UnitHeaderData(Id siteId, UnitDefinition unitDefinition) {
            name = StringUtility.makeLabel(unitDefinition.getUnitClass());
            List<Class<Data>> iotClasses = new ArrayList<>();
            for (UnitDefinition ud:
                    StoredObject.list(UnitDefinition.class, "UnitType=" + unitDefinition.getUnitTypeId())) {
                add(ud);
                //noinspection unchecked
                iotClasses.add((Class<Data>) ud.getDataClass());
            }
            limits.sort(Comparator.comparingInt(ValueLimit::getSignificance));
            alarms.sort(Comparator.comparingInt(AlarmSwitch::getSignificance));
            StoredObject.list(unitDefinition.getUnitClass())
                    .filter(u -> u.getActive() && u.getBlock().getActive() && siteId.equals(u.getSiteId()))
                    .forEach(u -> units.add(new UnitData(this, u, iotClasses)));
            units.sort(Comparator.comparing(UnitData::getName));
        }

        private void add(UnitDefinition unitDefinition) {
            unitDefinition.listLinks(ValueLimit.class, "Active", "Significance DESC").collectAll(limits);
            unitDefinition.listLinks(AlarmSwitch.class, "Active", "Significance DESC").collectAll(alarms);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<? extends AbstractData> children() {
            return units;
        }

        @Override
        public void prefix(DataStatus<?> cs, StyledBuilder html) {
            paintPrefix(cs, html);
        }

        boolean paintPrefix(DataStatus<?> cs, StyledBuilder html) {
            UnitData ur = units.stream().filter(r -> r.statusList.contains(cs)).findAny().orElse(null);
            if (ur == null) {
                return false;
            }
            html.append(ur.name).newLine();
            return true;
        }
    }

    private static final List<? extends AbstractData> EMPTY = new ArrayList<>();

    public static class UnitData extends AbstractData {

        private final String name;
        private final Unit unit;
        private final List<Class<Data>> iotClasses;

        private UnitData(UnitHeaderData header, Unit unit, List<Class<Data>> iotClasses) {
            this.unit = unit;
            this.iotClasses = iotClasses;
            name = unit.toDisplay();
            header.limits.forEach(v -> statusList.add(new LimitStatus(unit, v)));
            header.alarms.forEach(
                    a -> {
                        AlarmStatus as = new AlarmStatus(unit, a);
                        int i = 0;
                        DataStatus<?> s;
                        for (; i < statusList.size(); i++) {
                            s = statusList.get(i);
                            if (s instanceof AlarmStatus) {
                                continue;
                            }
                            if (s.significance() < as.significance()) {
                                statusList.add(i, as);
                                i = -1;
                                break;
                            }
                        }
                        if (i > -1) {
                            statusList.add(as);
                        }
                    }
            );
        }

        public Unit getUnit() {
            return unit;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<? extends AbstractData> children() {
            return EMPTY;
        }

        @Override
        synchronized void updateStatus() {
            if (cellStatusUpdated) {
                return;
            }
            statusList.forEach(DataStatus::set);
            sortStatus(statusList);
            cellStatusUpdated = true;
        }
    }

    private static void sortStatus(List<DataStatus<?>> statusList) {
        statusList.sort(DataSet::compareTo);
    }

    private static int compareTo(DataStatus<?> s1, DataStatus<?> s2) {
        int a1 = Math.abs(s1.alarm()), a2 = Math.abs(s2.alarm());
        if (a1 == a2) {
            return 0;
        }
        return a2 - a1;
    }

    public static class DataValues {

        long startTime, endTime;
        private final List<? extends DataValue> values;

        public DataValues(List<? extends DataValue> values, long from, long to) {
            values.removeIf(dv -> dv.getDataStatus() == null || dv.getData() == null);
            values.forEach(DataValue::sanitize);
            values.removeIf(dv -> dv.getData() == null || dv.ioTClass == null);
            this.values = values;
            values.forEach(dv -> dv.dataValues = this);
            load(from, to);
        }

        public List<? extends DataValue> getDataValues() {
            return values;
        }

        public void load(long from, long to) {
            String condition = "CollectedAt BETWEEN " + from + " AND " + to, conditionStart = "CollectedAt < " + from;
            startTime = from;
            endTime = to;
            ArrayListSet<Class<Data>> classes = new ArrayListSet<>();
            values.forEach(dv -> {
                dv.values.clear();
                classes.add(dv.ioTClass);
            });
            ObjectIterator<Data> objects;
            while(!classes.isEmpty()) {
                Class<Data> ioTObjectClass = classes.remove(0);
                objects = StoredObject.list(ioTObjectClass, condition, "CollectedAt", true);
                try {
                    boolean found = false;
                    for(Data object : objects) {
                        found = true;
                        extractValue(object);
                    }
                    if(!found) {
                        Data object = StoredObject.list(ioTObjectClass, conditionStart, "CollectedAt DESC",
                                true).findFirst();
                        if(object != null) {
                            extractValue(object);
                        }
                    }
                } finally {
                    objects.close();
                }
            }
        }

        private void extractValue(Data object) {
            long time = object.getCollectedAt();
            values.stream().filter(dv -> dv.ioTClass == object.getClass()
                            && object.getUnitId().equals(((UnitData)dv.getData()).unit.getId()))
                    .forEach(dv -> {
                        try {
                            Method m = dv.dataStatus.valueDefinition.getValueMethodForGet();
                            if(dv.dataStatus instanceof LimitStatus) {
                                if(HasValue.class.isAssignableFrom(m.getReturnType())) {
                                    dv.add(time, ((HasValue) m.invoke(object)).getValue());
                                } else {
                                    dv.add(time, (Double) m.invoke(object));
                                }
                            } else {
                                dv.add(time, ((boolean) m.invoke(object)) ? 1 : 0);
                            }
                        } catch(Throwable ignored) {
                        }
                    });
        }

        public long getStartTime() {
            return startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public Stream<LocalDateTime> stream(int step) {
            return stream(step, 0);
        }

        public Stream<LocalDateTime> stream(int step, int timeOffsetInMillis) {
            return LongStream.iterate(startTime, t -> t <= endTime, t -> t + step)
                    .mapToObj(t -> LocalDateTime
                            .ofEpochSecond((t + timeOffsetInMillis) / 1000L, 0, ZoneOffset.UTC));
        }

        public void dispose() {
            values.forEach(dv -> {
                dv.values.clear();
                dv.dataValues = null;
            });
            values.clear();
        }
    }

    public static class DataValue {

        private final long id = statusId.next();
        private AbstractData data;
        final DataStatus<?> dataStatus;
        Class<Data> ioTClass;
        final List<ValueAndTime> values = new ArrayList<>();
        DataValues dataValues;

        public DataValue(AbstractData data, DataStatus<?> dataStatus) {
            this.data = data;
            this.dataStatus = dataStatus;
        }

        private void sanitize() {
            isMine(data);
        }

        private boolean isMine(AbstractData data) {
            if(data instanceof UnitData ud) {
                for (Class<Data> iotClass : ud.iotClasses) {
                    if(belongsTo(dataStatus.valueDefinition.getValueMethodForGet(), iotClass)) {
                        this.ioTClass = iotClass;
                        this.data = ud;
                        return true;
                    }
                }
            }
            for(AbstractData d: data.children()) {
                if(isMine(d)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean belongsTo(Method m, Class<?> klass) {
            if(m.getDeclaringClass() == klass) {
                return true;
            }
            klass = klass.getSuperclass();
            if(klass == null) {
                return false;
            }
            return belongsTo(m, klass);
        }

        public AbstractData getData() {
            return data;
        }

        public DataStatus<?> getDataStatus() {
            return dataStatus;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(!(o instanceof DataValue dataValue)) return false;
            return id == dataValue.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        void add(long time, double value) {
            values.add(new ValueAndTime(time, value));
        }

        private record ValueAndTime(long time, double value) {}

        public Stream<Double> stream(int step) {
            if(dataValues == null || values.isEmpty()) {
                return Stream.empty();
            }
            return Utility.stream(new VTIterator(step)).map(vt -> vt.value);
        }

        public Stream<Object[]> rawStream(int step) {
            if(dataValues == null || values.isEmpty()) {
                return Stream.empty();
            }
            AtomicLong time = new AtomicLong(dataValues.startTime);
            return Utility.stream(new VTIterator(step)).map(vt -> {
                Object[] v =  new Object[] { vt.time(), vt.value(), time.get() };
                time.set(time.get() + step);
                return v;
            });
        }

        public Stream<Object[]> rawStream() {
            if(dataValues == null || values.isEmpty()) {
                return Stream.empty();
            }
            return values.stream().map(vt -> new Object[] { vt.time(), vt.value() });
        }

        public int size() {
            return values.size();
        }

        private class VTIterator implements Iterator<ValueAndTime> {

            private int index = 0;
            private long time;
            private final long step;

            private VTIterator(long step) {
                this.step = step <= 0 ? (5 * 60000L) : step;
                this.time = dataValues.startTime;
            }

            @Override
            public boolean hasNext() {
                return time <= dataValues.endTime;
            }

            private ValueAndTime v() {
                time += step;
                return values.get(index);
            }

            private boolean tryNext() {
                long t = values.get(index).time;
                if(time == t) {
                    return false;
                }
                if(time > t) {
                    if(++index == values.size()) {
                        --index;
                        return false;
                    }
                    t = values.get(index).time;
                    if(time == t) {
                        return false;
                    }
                    if(time < t) {
                        --index;
                        return false;
                    }
                    return true;
                }
                return false;
            }

            @Override
            public ValueAndTime next() {
                if(time > dataValues.endTime) {
                    throw new NoSuchElementException();
                }
                //noinspection StatementWithEmptyBody
                while(tryNext());
                return v();
            }
        }
    }

    private static void visitUnits(Consumer<Unit> consumer) {
        synchronized(sites) {
            for(SiteData sd: sites) {
                for(UnitHeaderData uhd: sd.units) {
                    for(UnitData ud: uhd.units) {
                        consumer.accept(ud.unit);
                    }
                }
            }
        }
    }
}
