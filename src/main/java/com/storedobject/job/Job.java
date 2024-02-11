package com.storedobject.job;

import com.storedobject.core.*;

/**
 * A logic that extends this class is called a "Job". A Job can be scheduled to run (by invoking its
 * {@link #execute()} method) at some defined intervals in the background by creating entries in the
 * {@link Schedule} data class. An instance is created only once and at defined time-intervals (based on the entries in
 * the {@link Schedule}), the {@link #execute()} method is invoked. See also {@link DaemonJob}.
 *
 * @author Syam
 */
public abstract class Job {

	final Schedule schedule;

	/**
	 * Constructor.
	 *
	 * @param schedule Schedule defined for this Job.
	 */
	public Job(Schedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * Get the currently active {@link TransactionManager}. Always, a valid {@link TransactionManager} exists during
	 * the life-cycle of the Job.
	 *
	 * @return The currently active {@link TransactionManager}.
	 */
	public TransactionManager getTransactionManager() {
		return schedule.getTransactionManager();
	}

	/**
	 * Get the device associated with this Job. Sometimes, this will be useful if you want to generate a device-specific
	 * report.
	 *
	 * @return The current device.
	 */
	public Device getDevice() {
		return schedule.getDevice();
	}

	/**
	 * Execute the Job.
	 *
	 * @throws Throwable If an error is thrown from this method, execution is terminated and the details of the
	 * error will be logged.
	 */
	public abstract void execute() throws Throwable;

	/**
	 * This method is invoked when the Job is completed. It can be used to clean up the resources. (In the case of
	 * {@link DaemonJob}s, it will be invoked only when any error occurs. (The default implementation does nothing).
	 */
	public void clean() {
	}

	/**
	 * This method is invoked when the system is shutting down. It will be called even if the job is inactive at
	 * that time. (The default implementation does nothing).
	 */
	public void shutdown() {
	}

	/**
	 * Log something to the device.
	 *
	 * @param any Log anything.
	 */
	public void log(Object any) {
		getDevice().log(StringUtility.toString(any));
	}

	/**
	 * Send an alert to the configured persons. (When scheduling the Job, this can be configured).
	 *
	 * @param messageParameters Alert parameters.
	 */
	public void alert(Object... messageParameters) {
	}
}