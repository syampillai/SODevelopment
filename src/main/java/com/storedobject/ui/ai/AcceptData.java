package com.storedobject.ui.ai;

import com.storedobject.core.JSONMap;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;

import java.util.concurrent.Semaphore;

public abstract class AcceptData extends DataForm {

    private final Semaphore semaphore = new Semaphore(1, true);
    private final Knowledge knowledge;
    protected final JSONMap map;
    private boolean dataCollected = false;

    public AcceptData(Knowledge knowledge, String caption) {
        this(knowledge, caption, null);
    }

    public AcceptData(Knowledge knowledge, String caption, JSONMap map) {
        super(caption);
        this.knowledge = knowledge;
        this.map = map == null ? new JSONMap() : map;
    }

    @SuppressWarnings("unchecked")
    public final Application getApplication() {
        return knowledge == null ? super.getApplication() : knowledge.application;
    }

    public final Knowledge getKnowledge() {
        return knowledge;
    }

    public final JSONMap getMap() {
        if(!dataCollected) {
            if(!executing()) {
                try {
                    knowledge.semaphore.acquire();
                } catch (InterruptedException e) {
                    err();
                    return map;
                }
                execute();
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    err();
                }
                knowledge.semaphore.release();
                semaphore.release();
            } else {
                try {
                    semaphore.acquire();
                    semaphore.release();
                } catch (InterruptedException e) {
                    err();
                }
            }
        }
        return map;
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        map.remove("error");
        if(dataCollected) {
            dataCollected = false;
        }
        knowledge.application.access(() -> super.execute(parent, doNotLock));
        try {
            semaphore.acquire();
        } catch (InterruptedException ignored) {
        }
    }

    private void err() {
        map.put("error", "Unable to provide - system busy");
    }

    @Override
    protected void cancel() {
        super.cancel();
        map.put("error", "Unable to provide - user cancelled the operation");
    }

    @Override
    public void clean() {
        dataCollected = true;
        semaphore.release();
        super.clean();
    }
}
