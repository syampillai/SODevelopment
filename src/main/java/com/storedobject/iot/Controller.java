package com.storedobject.iot;

import com.storedobject.core.DateUtility;
import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.job.DaemonJob;
import com.storedobject.job.Schedule;

import java.sql.Date;
import java.util.*;

public class Controller extends DaemonJob {

    private static final int ONE_DAY = 24 * 60 * 60000;
    private static Controller controller;
    private final List<Control> controls = new ArrayList<>();

    public Controller(Schedule schedule) {
        super(schedule);
        if(controller != null) {
            log("Controller is already running - clearing");
            controller.cancel();
        }
        controller = this;
    }

    @Override
    public void execute() {
        synchronized (controls) {
            load();
        }
    }

    @Override
    public void shutdown() {
        cancel();
        controller = null;
        super.shutdown();
    }

    static void restart() {
        if(controller == null) {
            return;
        }
        controller.cancel();
        synchronized (controller.controls) {
            controller.load();
        }
    }

    private void load() {
        String cv;
        Date at;
        List<ControlSchedule> css = StoredObject.list(ControlSchedule.class, "Active").toList();
        for(Site site: StoredObject.list(Site.class, "Active")) {
            for(Block block: StoredObject.list(Block.class, "Site=" + site.getId() + " AND Active")) {
                for(Unit unit: StoredObject.list(Unit.class, "Block=" + block.getId() + " AND Active",true)) {
                    for(ControlSchedule cs: css) {
                        if(unit.getOrdinality() != cs.getOrdinality() || unit.existsLink(cs) || block.existsLink(cs)) {
                            continue;
                        }
                        ValueDefinition<?> vd = cs.getControl();
                        if(vd == null) {
                            continue;
                        }
                        UnitDefinition ud = vd.getMaster(UnitDefinition.class);
                        if(ud == null) {
                            continue;
                        }
                        UnitType ut = ud.getUnitType();
                        if(ut == null || !ut.getUnitClassName().equals(unit.getClass().getName())) {
                            continue;
                        }
                        int timeDiff = site.getTimeDifference();
                        long firstFire = DateUtility.startOfToday().getTime() + (cs.getSendAt() * 60000L) - timeDiff;
                        if(firstFire < System.currentTimeMillis()) {
                            firstFire += ONE_DAY;
                        }
                        Timer timer = new Timer();
                        cv = cs.controlValue();
                        Control control = new Control(timer, unit.getId(), vd.getId(), cs.getDays(), cv, timeDiff);
                        controls.add(control);
                        at = new Date(firstFire);
                        log(vd.getName() + " = " + cv + " will be sent to " + unit.toDisplay() + " at "
                                + DateUtility.formatWithTimeHHMM(at) + " UTC ("
                                + DateUtility.formatWithTime(site.date(at)) + " " + site.getTimeZone() + ")");
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                send(control);
                            }
                        }, at, ONE_DAY);
                    }
                }
            }
        }
    }

    private void cancel() {
        synchronized (controls) {
            controls.forEach(c -> c.timer.cancel());
            controls.clear();
        }
        log("All controls cleared");
    }

    private void send(Control control) {
        long time = System.currentTimeMillis() + control.timeDiff;
        int day = 1 << (DateUtility.get(new Date(time), Calendar.DAY_OF_WEEK) - 1);
        if((control.days & day) == 0) {
            return;
        }
        Command command = new Command();
        command.setUnit(control.unitId);
        command.setCommand(control.valueDefinitionId);
        command.setCommandValue(control.value);
        if(MQTTDataCollector.instance != null && MQTTDataCollector.instance.mqtt != null) {
            try {
                MQTTDataCollector.publish(command);
            } catch (Exception e) {
                log(e);
            }
        }
    }

    private record Control(Timer timer, Id unitId, Id valueDefinitionId, int days, String value, int timeDiff) {}
}
