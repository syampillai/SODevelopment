package com.storedobject.ui.support;

import com.storedobject.core.*;
import com.storedobject.vaadin.View;

public class TicketingSystem extends SupportSystem {

    private SystemUser ticketManager;

    public TicketingSystem() {}

    public TicketingSystem(MemoType memoType) {
        super(memoType);
    }

    public TicketingSystem(String memoTypeShortName) {
        super(memoTypeShortName);
        int p;
        if(memoTypeShortName != null && (p = memoTypeShortName.indexOf('|')) > 0) {
            manager(memoTypeShortName.substring(p).trim());
        }
    }

    public TicketingSystem(MemoType memoType, boolean load) {
        super(memoType, load);
    }

    private void manager(String name) {
        if(ticketManager != null) return;
        if(name == null || name.isEmpty()) {
            name = "Ticket Manager";
        }
        SystemUserGroup group = SystemUserGroup.get(name);
        if(group == null) {
            error("Ticket Manager group '" + name + "' not defined!");
            return;
        }
        ticketManager = group.listUsers().findFirst();
        if(ticketManager == null) error("No users in group '" + name + "'");
    }

    @Override
    public void execute(View lock) {
        setCaption("Ticketing System");
        super.execute(lock);
    }

    @Override
    protected void memoCreated(Memo memo) {
        if(ticketManager == null) {
            manager(null);
            if(ticketManager == null) {
                return;
            }
        }
        MemoComment comment = memo.getLatestComment();
        if(comment == null || comment.getCommentCount() != 0) {
            error("Memo must have at least one comment!");
            return;
        }
        forwardMemo(comment, ticketManager);
    }
}
