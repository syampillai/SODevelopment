package com.storedobject.iot;

import com.storedobject.core.GlobalProperty;
import com.storedobject.core.SOException;
import com.storedobject.core.StoredObject;
import com.storedobject.job.DaemonJob;
import com.storedobject.job.Schedule;

public class MQTTDataCollector extends DaemonJob {

    static MQTTDataCollector instance;
    MQTT mqtt;

    public MQTTDataCollector(Schedule schedule) {
        this(schedule, null);
    }

    public MQTTDataCollector(Schedule schedule, MQTT mqtt) {
        super(schedule);
        if(instance != null) {
            instance.shutdown();
        }
        this.mqtt = mqtt;
        instance = this;
    }

    @Override
    public void execute() throws Throwable {
        create();
        mqtt.collect(getTransactionManager());
    }

    private void create() throws Throwable {
        if (mqtt != null) {
            mqtt.removeAllListeners();
            mqtt.disconnect();
            return;
        }
        String name = GlobalProperty.get(getTransactionManager().getEntity(), "MQTT-CONNECTOR");
        if (name.isBlank()) {
            mqtt = StoredObject.list(MQTT.class).single(true);
        } else {
            mqtt = StoredObject.get(MQTT.class,
                    "lower(Name)='" + StoredObject.toCode(name).toLowerCase() + "'");
        }
        if (mqtt == null) {
            if (name.isBlank()) {
                throw new SOException("No MQTT connector configured!");
            }
            throw new SOException("MQTT connector not found - " + name);
        }
    }

    @Override
    public void shutdown() {
        if (mqtt != null) {
            mqtt.removeAllListeners();
            mqtt.disconnect();
            mqtt = null;
        }
    }

    public static void publish(Command command) throws Exception {
        if(instance == null || instance.mqtt == null) {
            throw new SOException("Data collector not running");
        }
        instance.mqtt.publish(command);
    }
}
