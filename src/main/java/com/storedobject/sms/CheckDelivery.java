package com.storedobject.sms;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.Id;
import com.storedobject.core.ObjectIterator;
import com.storedobject.core.StoredObject;
import com.storedobject.core.TransactionControl;
import com.storedobject.job.Job;
import com.storedobject.job.Schedule;

public abstract class CheckDelivery extends Job {

    private final Id providerId;

    public CheckDelivery(Schedule schedule) {
        super(schedule);
        Provider p = Provider.get(getProviderName());
        providerId = p == null ? null : p.getId();
    }

    @Override
    public final void execute() throws Throwable {
        if(providerId == null) {
            throw new SORuntimeException("SMS Provider '" + getProviderName() + " not found!");
        }
        TransactionControl tc = new TransactionControl(getTransactionManager());
        ObjectIterator<SMSMessage> messages;
        messages = StoredObject.list(SMSMessage.class, "Sent AND NOT Delivered AND Error=0", "CreatedAt");
        try {
            for(SMSMessage m: messages) {
                checkDelivery(m);
                if(!m.getDelivered()) {
                    if(m.getError() == 0) { // Check later
                        continue;
                    }
                }
                if(m.save(tc) && tc.commit()) {
                    continue;
                }
                alert(tc.getError());
                return;
            }
        } finally {
            tc.rollback();
            messages.close();
        }
    }

    public abstract String getProviderName();

    public abstract void checkDelivery(SMSMessage message) throws Exception;
}
