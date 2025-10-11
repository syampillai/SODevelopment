package com.storedobject.ui.support;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;

public class TicketingSystem extends SupportSystem {

    private static SystemUser ticketManager;

    public TicketingSystem() {}

    public TicketingSystem(MemoType memoType) {
        super(memoType);
    }

    public TicketingSystem(String memoTypeShortName) {
        super(memoTypeShortName);
    }

    public TicketingSystem(MemoType memoType, boolean load) {
        super(memoType, load);
    }

    @Override
    protected void checkMemoType(MemoType type) {
        super.checkMemoType(type);
        if(ticketManager != null) return;
        SystemUserGroup group = SystemUserGroup.get("Ticket Manager");
        if(group == null) throw new SORuntimeException("Ticket Manager group not defined!");
        ticketManager = group.listUsers().findFirst();
        if(ticketManager == null) throw new SORuntimeException("No users in Ticket Manager group!");
    }

    @Override
    protected boolean createNewMemo(Transaction transaction, MemoComment comment) throws Exception {
        comment.forwardMemo(transaction, comment.getComment(), ticketManager);
        return true;
    }
}
