package com.storedobject.sms;

import com.storedobject.core.Id;
import com.storedobject.job.MessageSender;
import com.storedobject.job.Schedule;

public abstract class Server extends MessageSender<SMSMessage> {

    private final Id providerId;

    public Server(Schedule schedule) {
        super(schedule, SMSMessage.class);
        Provider p = Provider.get(getProviderName());
        providerId = p == null ? null : p.getId();
    }

    @Override
    public boolean isActive() {
        return providerId != null;
    }

    public abstract String getProviderName();
}
