package com.storedobject.sms;

import com.storedobject.job.Job;
import com.storedobject.job.Schedule;

public abstract class Server extends Job {

	public Server(Schedule schedule) {
		super(schedule);
	}

	@Override
	public final void execute() throws Exception {
	}

	public boolean canSend(SMSMessage messge) {
		return false;
	}
	
	public abstract String getProviderName();
	
	public abstract int credits() throws Exception;
	
	public abstract void send(SMSMessage message) throws Exception;
	
	public abstract void checkDelivery(SMSMessage message) throws Exception;
}
