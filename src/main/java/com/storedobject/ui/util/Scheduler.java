package com.storedobject.ui.util;

import com.storedobject.core.SystemLog;

import java.util.function.Function;

public class Scheduler implements Function<String, String> {

    private static Scheduler instance;
    private String by;

    private Scheduler() {
        instance = this;
    }

    public static Scheduler getInstance() {
        if(instance == null) {
            instance = new Scheduler();
        }
        return instance;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    private void kill(String who) {
        switch (com.storedobject.job.Scheduler.getStatusCode()) {
            case -1, 100, -100 -> { // Restarting or getting killed
                return;
            }
        }
        doneBy(who);
        Thread.startVirtualThread(com.storedobject.job.Scheduler::kill);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
    }

    private boolean isDead() {
        return com.storedobject.job.Scheduler.getStatusCode() == -1;
    }

    private String getStatus() {
        return com.storedobject.job.Scheduler.getStatusValue();
    }

    private void doneBy(String who) {
        if(who.equals(this.by)) {
            return;
        }
        this.by = who;
        SystemLog.log("Application-Management", who);
    }

    @Override
    public String apply(String by) {
        if(by == null) {
            return "";
        }
        if(isDead()) {
            doneBy(by);
            return null;
        }
        kill(by);
        String status = getStatus();
        if(isDead()) {
            return null; // Killed immediately
        }
        return "Job Scheduler Status: " + status + " (This may take a while) - Try again later";
    }
}
