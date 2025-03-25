package com.storedobject.iot;

import com.storedobject.core.Database;
import com.storedobject.core.Id;
import com.storedobject.core.StringUtility;
import com.storedobject.core.TransactionManager;
import com.storedobject.job.MessageGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

    private static AlertGenerator instance;
    private static long lastUpdateTime = System.currentTimeMillis();
    private static final long COMM_CHECK_INTERVAL = 15 * 60 * 1000L; // 15 Minutes
    private static TransactionManager tm;
    private static boolean commError = false;
    private record Alarm(long time, DataSet.DataStatus<?> ds, int alarm) {}
    private static final Map<Long, Alarm> alarms = new HashMap<>();
    private static final Map<Id, MessageGroup> messageGroups = new HashMap<>();

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
                            if(alarm == null || (System.currentTimeMillis() - alarm.time) > 3600000L
                                    || ds.alarm() != alarm.alarm) { // 1 hour expired or alarm state changed
                                alarms.put(ds.getId(), new Alarm(System.currentTimeMillis(), ds, ds.alarm()));
                                alert(ds);
                            }
                        } else if(alarm != null) { // Became normal
                            alarms.remove(ds.getId());
                            alert(ds);
                        }
                    });
                }
            }
            scanBranches(row);
        });
    }

    private static void alert(DataSet.DataStatus<?> ds) {
        MessageGroup mg = messageGroups.get(ds.unit.getBlockId());
        if(mg == null) {
            mg = ds.unit.getBlock().getMessageGroup();
            messageGroups.put(ds.unit.getBlockId(), mg);
        }
        try {
            mg.send(tm, ds.unit.getSite().getName(), ds.unit.getBlock().getName(),
                    ds.valueDefinition.getShortName() + " = " + val(ds));
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
            send(tm, "IoT Communication Restored", Database.get().name());
        }
    }

    private static void commCheck() {
        if((System.currentTimeMillis() - lastUpdateTime) <= COMM_CHECK_INTERVAL) {
            return;
        }
        lastUpdateTime = System.currentTimeMillis() + COMM_CHECK_INTERVAL; // This will delay the next duplicate message
        commError = true;
        send(tm, "IoT Communication Failure", Database.get().name());
    }

    private static void send(Object... parameters) {
        try {
            MessageGroup.send("IOT_ERROR_MONITOR", tm, parameters);
        } catch (Throwable e) {
            tm.log(e);
        }
    }
}
