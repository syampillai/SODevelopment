package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.job.MessageGroup;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Alert Generator.
 * Data values are examined whenever new data values are received. Alerts are generated if the data received contain
 * outliers (a subsequent message is sent once the same data value becomes normal).
 * <p>A duplicate message is suppressed for 1 hour, so, the message may be repeated if the error situation persists.</p>
 * <p>Messages are sent to the message group configured in {@link Block#getMessageGroup()}.</p>
 * <p>Also, if there is no communication for a certain time (15 minutes) from the MQTT message handler, a special
 * alert is generated to the "IOT_ERROR_MONITOR" group.</p>
 *
 * @author Syam
 */
public class AlertGenerator {

    private static final long startedAt = System.currentTimeMillis();
    static final Map<String, Long> frequency = new ConcurrentHashMap<>();
    private static AlertGenerator instance;
    private static long lastUpdateTime = System.currentTimeMillis();
    private static final long COMM_CHECK_INTERVAL = 15 * 60 * 1000L; // 15 Minutes
    private static TransactionManager tm;
    private static boolean commError = false;
    private static final Map<Long, Alarm> alarms = new HashMap<>();
    private static final Map<Id, MessageGroup> messageGroups = new HashMap<>();
    private record Alarm(long time, DataSet.DataStatus<?> ds, int alarm, long occurredAt) {

        Alarm(long time, DataSet.DataStatus<?> ds, int alarm, Alarm parent) {
            this(time, ds, alarm, parent == null ? ds.getAlarmAt() : parent.occurredAt);
        }
    }

    private AlertGenerator() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                commCheck();
            }
        }, 60, COMM_CHECK_INTERVAL);
    }

    public static AlertGenerator get(TransactionManager tm) {
        if(tm == null) {
            return null;
        }
        if(instance == null) {
            AlertGenerator.tm = tm;
            //noinspection InstantiationOfUtilityClass
            instance = new AlertGenerator();
        }
        return AlertGenerator.tm == tm ? instance : null;
    }

    static void clearAlerts() {
        alarms.clear();
    }

    static void dataUpdated(long now) {
        lastUpdateTime = now;
        scanTree();
    }

    private synchronized static void scanTree() {
        DataSet.getSites().stream().filter(sd -> sd.getSite().getActive()).forEach(AlertGenerator::scanBranches);
    }

    private static void scanBranches(DataSet.AbstractData parent) {
        parent.children().forEach(row -> {
            if(row instanceof DataSet.UnitData ud) {
                if(ud.getUnit().getActive()) {
                    ud.getDataStatus().stream().filter(ds -> ds.getValueDefinition().getAlert()).forEach(ds -> {
                        Alarm alarm = alarms.get(ds.getId());
                        if(ds.alert()) {
                            long now = System.currentTimeMillis();
                            if(alarm == null || (now - alarm.time) > frequency(ds)
                                    || ds.alarm() != alarm.alarm) { // Last alert expired or alarm state changed
                                alarm = new Alarm(now, ds, ds.alarm(), alarm);
                                alarms.put(ds.getId(), alarm);
                                if((now - startedAt) > 300000L) { // More than 5 minutes have passed, we will start the alerts
                                    alert(alarm, false);
                                }
                            }
                        } else if(alarm != null) { // Became normal
                            alarms.remove(ds.getId());
                            alert(alarm, true);
                        }
                    });
                }
            }
            scanBranches(row);
        });
    }

    private static String siteTime(long time) {
        Timestamp ts = tm.date(new Timestamp(time));
        return DateUtility.formatWithTimeHHMM(ts);
    }

    private static void alert(Alarm alarm, boolean fixed) {
        DataSet.DataStatus<?> ds = alarm.ds;
        MessageGroup mg = messageGroups.get(ds.unit.getBlockId());
        if(mg == null) {
            mg = ds.unit.getBlock().getMessageGroup();
            messageGroups.put(ds.unit.getBlockId(), mg);
        }
        try {
            String v = val(ds);
            if(fixed) {
                v += " (Issue Fixed)";
            } else {
                String m = ds.getAlarmMessage();
                if(m != null && !m.isEmpty()) {
                    v += " (" + m + ")";
                }
            }
            String time = "Detected at " + siteTime(alarm.occurredAt);
            if(fixed) {
                time += ", Fixed at " + siteTime(ds.getAlarmAt());
            } else if((alarm.time - alarm.occurredAt) > 300000L) {
                time += ", Not yet fixed at " + siteTime(alarm.time);
            }
            mg.send(tm, ds.unit.getSite().getName(), ds.unit.getBlock().getName(),
                    ds.valueDefinition.getShortName() + " = " + v, time);
        } catch (Throwable e) {
            tm.log(e);
        }
    }

    private static String val(DataSet.DataStatus<?> ds) {
        if(ds instanceof DataSet.AlarmStatus as) {
            return as.value ? "On" : "Off";
        }
        return StringUtility.format(((DataSet.LimitStatus)ds).value, 2)
                + ((ValueLimit)ds.valueDefinition).getUnitSuffix() + " [" + switch (ds.alarm()) {
            case -3 -> "Lowest";
            case -2 -> "Lower";
            case -1 -> "Low";
            case 1 -> "High";
            case 2 -> "Higher";
            case 3 -> "highest";
            default -> "Normal";
        } + "]";
    }

    static void dataReceived(long now) {
        lastUpdateTime = now;
        if(commError) {
            commError = false;
            sendCommStatus("IoT Communication Restored", Database.get().name());
        }
    }

    private static void commCheck() {
        if((System.currentTimeMillis() - lastUpdateTime) <= COMM_CHECK_INTERVAL) {
            return;
        }
        lastUpdateTime = System.currentTimeMillis() + COMM_CHECK_INTERVAL; // This will delay the next duplicate message
        commError = true;
        sendCommStatus("IoT Communication Failure", Database.get().name());
    }

    private static void sendCommStatus(Object... parameters) {
        try {
            MessageGroup.send("IOT_ERROR_MONITOR", tm, parameters);
        } catch (Throwable e) {
            tm.log(e);
        }
    }

    private static long frequency(DataSet.DataStatus<?> ds) {
        int significance = ds.getValueDefinition().getSignificance();
        String key = tm.getEntity().getId() + "/" + significance;
        Long frequency = AlertGenerator.frequency.get(key);
        if(frequency == null) {
            AlertRepeatFrequency atf = AlertRepeatFrequency.get(significance, tm);
            frequency = atf.getFrequency() * 60 * 1000L;
            AlertGenerator.frequency.put(key, frequency);
        }
        return frequency;
    }
}
