package com.storedobject.job;

import java.util.ArrayList;

import com.storedobject.core.Device;
import com.storedobject.core.Id;
import com.storedobject.core.MessageTemplate;
import com.storedobject.core.Person;
import com.storedobject.core.SystemUser;
import com.storedobject.core.SystemUserGroup;
import com.storedobject.core.Transaction;
import com.storedobject.core.TransactionManager;

public abstract class Job {

	public Job(Schedule schedule) {
	}

	public TransactionManager getTransactionManager() {
		return null;
	}
	
	public Device getDevice() {
		return null;
	}

	public abstract void execute() throws Throwable;

	public boolean alert(Throwable error) {
		return false;
	}

	public boolean alert(String message) {
		return false;
	}
	
	public boolean alert(String message, int validityInDays) {
		return false;
	}

	public void alert(String message, Transaction transaction) throws Throwable {
	}

	public void alert(String message, int validityInDays, Transaction transaction) throws Throwable {
	}

	public boolean alert(SystemUser user, String message) {
		return false;
	}
	
	public boolean alert(SystemUser user, String message, int validityInDays) {
		return false;
	}

	public void alert(SystemUser user, String message, Transaction transaction) throws Throwable {
	}

	public void alert(SystemUser user, String message, int validityInDays, Transaction transaction) throws Throwable {
	}

	public boolean alert(SystemUserGroup group, String message) {
		return false;
	}
	
	public boolean alert(SystemUserGroup group, String message, int validityInDays) {
		return false;
	}

	public void alert(SystemUserGroup group, String message, Transaction transaction) throws Throwable {
	}

	public void alert(SystemUserGroup group, String message, int validityInDays, Transaction transaction) throws Throwable {
	}
	
	public Id alert(Person person, MessageTemplate messageTemplate, Object... messageParameters) {
		return null;
	}

	public ArrayList<Id> alert(Person person, String messageTemplateName, Object... messageParameters) {
		return null;
	}

	public void log(Object any) {
	}
	
	public void message(Object... messageParameters) {
	}
    
	public void message(Person person, Object... messageParameters) {
	}
}
