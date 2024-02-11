package com.storedobject.job;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.Message;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StringUtility;
import com.storedobject.core.TransactionManager;
import com.storedobject.sms.SMSMessage;

import java.util.*;

/**
 * Message sender.
 * <p>Note: All message senders (SMS, Email, Telegram etc.) must extend this class.</p>
 *
 * @author Syam
 */
public abstract class MessageSender<T extends Message> extends DaemonJob {

    private static final Set<MessageSender<?>> senders = new HashSet<>();
    private final Class<T> messageClass;
    private final Object lock = new Object();
    private byte stage = 0;
    private Timer timer;

    /**
     * Constructor.
     *
     * @param schedule Schedule defined for this Job.
     * @param messageClass Message class.
     */
    public MessageSender(Schedule schedule, Class<T> messageClass) {
        super(schedule);
        this.messageClass = messageClass;
        if(senders.stream().anyMatch(s -> s.getClass() == getClass())) {
            throw new SORuntimeException("Duplicate message sender for " + StringUtility.makeLabel(messageClass));
        }
        senders.add(this);
    }

    /**
     * This method is invoked when this job is executed for the first time, and it will be invoked only once.
     * You may set up the sender here.
     */
    protected void initialize() {
    }

    /**
     * This method is invoked when the sender was stopped due to any error. Make sure that {@link #isActive()} method
     * returns <code>false</code> to indicate that the sender had stopped with some error condition. All recovery action
     * should happen here and the {@link #isActive()} method should return <code>true</code> after this. Otherwise,
     * no messages will be sent by invoking the {@link #sendMessage(Message)} method.
     */
    protected void recover() {
    }

    private void doRecover() {
        if (!isActive()) {
            synchronized (lock) {
                if(isActive()) {
                    return;
                }
                recover();
            }
        }
    }

    @Override
    public final void execute() throws Throwable {
        switch (stage) {
            case 0 -> {
                stage = 1;
                initialize();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        send();
                    }
                }, 0, 300000);
            }
            case 1 -> doRecover();
        }
    }

    /**
     * Kick all active "message senders" to indicate that some new messages are available for sending.
     */
    public static void kick() {
        for(MessageSender<?> ms: senders) {
            if(ms.isActive()) {
                new Thread(ms::send).start();
            }
        }
    }

    @Override
    public final void shutdown() {
        synchronized (lock) {
            if(stage == 2) {
                return;
            }
            stage = 2;
        }
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        terminate();
        senders.remove(this);
        super.shutdown();
    }

    /**
     * This method is invoked when the sender needs to be shut down. This will be invoked only once.
     */
    protected void terminate() {
    }

    private void send() {
        synchronized (lock) {
            while (true) {
                if(sendBatch()) {
                    continue;
                }
                break;
            }
        }
    }

    private boolean sendBatch() {
        if(stage != 1 || !isActive()) {
            return false;
        }
        int bs = getBatchSize();
        if(bs <= 0) {
            bs = 50;
        }
        List<T> messages = StoredObject.list(messageClass, "NOT Sent", "CreatedAt,Sent")
                .filter(this::canSend).limit(bs).toList();
        if(!messages.isEmpty() && monitorDeliveryCost()) {
            int balance = getCreditBalance();
            if(balance < 1) {
                return false;
            }
            List<T> sendList = new ArrayList<>();
            for(T m: messages) {
                balance -= getDeliveryCost(m);
                if(balance < 1) {
                    mg(StringUtility.makeLabel(messageClass) + " - " + "Not enough credit");
                    break;
                }
                sendList.add(m);
            }
            messages = sendList;
        }
        if(messages.isEmpty()) {
            return false;
        }
        return send(messages);
    }

    private boolean send(List<T> messages) {
        int error;
        for(T m: messages) {
            error = sendMessage(m);
            switch (error) {
                case 1, 2 -> m.setError(error);
                default -> m.sent(error);
            }
        }
        try {
            if(getTransactionManager().transact(t -> {
                for(T m: messages) {
                    m.save(t);
                }
            }) == 0) {
                return true;
            }
        } catch(Exception e) {
            mg(e);
        }
        return false;
    }

    /**
     * Check if we need to monitor delivery cost or not.
     * <p>Note: Applicable mostly to SMS messages.</p>
     *
     * @return True/false.
     */
    protected boolean monitorDeliveryCost() {
        return false;
    }

    /**
     * Get the current credit balance.
     * <p>Note: Applicable mostly to SMS messages.</p>
     *
     * @return Current credit balance.
     */
    protected int getCreditBalance() {
        return 0;
    }

    /**
     * Get the number of credits required to deliver this message.
     * <p>Note: Applicable mostly to SMS messages.</p>
     *
     * @param message Message to send.
     * @return Cost (in number of credits).
     */
    protected int getDeliveryCost(T message) {
        return 0;
    }

    /**
     * Send a message.
     *
     * @param message Message to send.
     * @return The error code to indicate the status. 0: Successful, 1: Retry later, 2: Insufficient balance. Anything
     * above 2 indicates an error condition that doesn't allow this message to be sent (for example: invalid mobile
     * number in case of an {@link SMSMessage}).
     */
    protected abstract int sendMessage(T message);

    /**
     * Batch size - How many messages to be read for sending in one batch?
     * @return Default is 50.
     */
    protected int getBatchSize() {
        return 50;
    }

    /**
     * Is it possible to send this message?
     *
     * @param message Message.
     * @return True/false.
     */
    protected boolean canSend(T message) {
        return true;
    }

    private void mg(Object any) {
        TransactionManager tm = getTransactionManager();
        tm.log(any);
        alert(any);
    }
}
