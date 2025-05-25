package com.storedobject.sms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.storedobject.core.Contact;
import com.storedobject.core.DateUtility;
import com.storedobject.core.LoginMessage;
import com.storedobject.core.Person;
import com.storedobject.core.StoredObject;
import com.storedobject.core.SystemUser;
import com.storedobject.core.Transaction;
import com.storedobject.job.Schedule;

public class SMSSimulator extends Server {

	private final Random random = new Random(System.currentTimeMillis());

    public SMSSimulator(Schedule schedule) {
        super(schedule);
    }

	@Override
	protected int sendMessage(SMSMessage message) {
    	Transaction transaction = null;
    	try {
    		transaction = getTransactionManager().createTransaction();
    		LoginMessage m = new LoginMessage();
    		m.setMessage("SMS Simulator: " + message.getMessage());
    		m.setActive(true);
    		m.setValidFrom(DateUtility.now());
    		m.setValidTo(DateUtility.endTime(DateUtility.addDay(DateUtility.today(), 30)));
    		m.setLoginAlert(false);
    		m.setDisableLogin(false);
    		m.setShowEveryTime(false);
    		m.save(transaction);
    		List<SystemUser> users = new ArrayList<>();
    		for(Contact c: StoredObject.list(Contact.class, "Value='" + message.getMobileNumber() + "'")) {
    			for(Person p: c.listMasters(Person.class)) {
    				for(SystemUser u: StoredObject.list(SystemUser.class, "Person=" + p.getId())) {
    					users.add(u);
    				}
    			}
    		}
    		for(SystemUser u: users) {
    			m.addLink(transaction, u);
    		}
    		transaction.commit();
    	} catch(Exception e) {
    		if(transaction != null) {
    			transaction.rollback();
    		}
			return 1;
    	}
        message.setMessageID(String.valueOf(random.nextLong()));
    	message.setDelivered(true);
		return 0;
    }

    @Override
    public String getProviderName() {
        return "SMS Simulator";
    }
}
