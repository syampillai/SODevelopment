package com.storedobject.job;

/**
 * Daemon job is a special {@link Job} and its {@link #execute()} method will be invoked only once,
 * and it runs forever in the background. However, the {@link #execute()} method may be invoked again if an error
 * occurred during the previous run or the {@link #isActive()} method returns <code>false</code> when it is
 * due again.
 *
 * @author Syam
 */
public abstract class DaemonJob extends Job {

    /**
     * Constructor.
     *
     * @param schedule Schedule defined for this Job.
     */
    public DaemonJob(Schedule schedule) {
        super(schedule);
    }

    /**
     * Check if the Job is still active or not. (The default implementation always returns <code>true</code>).
     * If this method returns <code>false</code>, the Job Scheduler will invoke the {@link #execute()} method again
     * when it is due for the next run.
     *
     * @return True/false
     */
    public boolean isActive() {
        return true;
    }
}
