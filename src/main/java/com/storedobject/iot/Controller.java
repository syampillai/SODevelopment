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
        controller = this;
    }

    @Override
    public void execute() {
        load();
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
        controller.controls.clear();
        controller.load();
    }

    private void load() {
        List<ControlSchedule> css = StoredObject.list(ControlSchedule.class, "Active").toList();
        for(Site site: StoredObject.list(Site.class, "Active")) {
            for(Block block: StoredObject.list(Block.class, "Site=" + site.getId() + " AND Active", true)) {
                if(!block.getActive()) {
                    continue;
                }
                for(Unit unit: StoredObject.list(Unit.class, "Block=" + block.getId() + " AND Active",true)) {
                    for(ControlSchedule cs: css) {
                        if(unit.existsLink(cs) || block.existsLink(cs)) {
                            continue;
                        }
                        ValueDefinition<?> vd = cs.getControl();
                        int timeDiff = site.getTimeDifference();
                        long firstFire = DateUtility.startOfToday().getTime() + (cs.getSendAt() * 60000L);
                        if(firstFire < System.currentTimeMillis()) {
                            firstFire += ONE_DAY;
                        }
                        firstFire += timeDiff;
                        Timer timer = new Timer();
                        Control control = new Control(timer, unit.getId(), vd.getId(), cs.getDays(), cs.controlValue(),
                                timeDiff);
                        controls.add(control);
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                send(control);
                            }
                        }, new Date(firstFire), ONE_DAY);
                    }
                }
            }
        }
    }

    private void cancel() {
        controls.forEach(c -> c.timer.cancel());
        controls.clear();
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
