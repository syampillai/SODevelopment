package com.storedobject.job;

import com.storedobject.core.*;

public abstract class Job {

	private final Schedule schedule;

	public Job(Schedule schedule) {
		this.schedule = schedule;
	}

	public TransactionManager getTransactionManager() {
		return schedule.getDevice().getServer().getTransactionManager();
	}

	public Device getDevice() {
		return schedule.getDevice();
	}

	public abstract void execute() throws Throwable;

	public void clean() {
	}

	public void log(Object any) {
		getDevice().log(StringUtility.toString(any));
	}

	public void alert(Object... messageParameters) {
	}
}