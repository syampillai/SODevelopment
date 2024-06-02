package com.storedobject.core;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.storedobject.core.annotation.Column;
import com.storedobject.core.annotation.SetNotAllowed;

public abstract class Message extends StoredObject {

    private String message;
    private String messageID;
    private Timestamp createdAt = DateUtility.now();
    private boolean sent;
    private Id sentToId = Id.ZERO;
    private Timestamp sentAt = DateUtility.now();
    protected int error; // 0: No error, 1: Retry delivery, 2: Insufficient balance
                         // Greater than 2: Error conditions (delivery not possible)

    public Message() {
    }
    
    public Message(String message) {
    	this.message = message;
    }

    public static void columns(Columns columns) {
        columns.add("Message", "text");
        columns.add("MessageID", "text");
        columns.add("CreatedAt", "timestamp");
        columns.add("Sent", "boolean");
        columns.add("SentAt", "timestamp");
        columns.add("Error", "int");
        columns.add("SentTo", "id");
    }

    public static void indices(Indices indices) {
        indices.add("SentTo,T_Family,CreatedAt");
    }

    public static String[] protectedColumns() {
        return new String[] { "MessageID", "SentTo" };
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(style = "(large)", order = 100)
    public String getMessage() {
        return message;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    @Column(required = false, order = 200)
    public String getMessageID() {
        return messageID;
    }

    public void setCreatedAt(Timestamp createdAt) {
        if(!loading()) {
            throw new Set_Not_Allowed("Created at");
        }
        this.createdAt = new Timestamp(createdAt.getTime());
    }

    @SetNotAllowed
    @Column(order = 300)
    public Timestamp getCreatedAt() {
        return new Timestamp(createdAt.getTime());
    }

    public void setSent(boolean sent) {
        if(!loading()) {
            throw new Set_Not_Allowed("Sent");
        }
        this.sent = sent;
    }

    @SetNotAllowed
    @Column(order = 400)
    public boolean getSent() {
        return sent;
    }

    public void setSentAt(Timestamp sentAt) {
        if(!loading()) {
            throw new Set_Not_Allowed("Sent at");
        }
        this.sentAt = new Timestamp(sentAt.getTime());
    }

    @SetNotAllowed
    @Column(order = 500)
    public Timestamp getSentAt() {
        return new Timestamp(sentAt.getTime());
    }

    /**
     * Set the error.
     * <pre>
     *     0: No error
     *     1: Retry delivery
     *     2: Insufficient balance
     *     Greater than 2: Error conditions (delivery not possible)
     * </pre>
     *
     * @param error Error value.
     */
    public void setError(int error) {
        this.error = error;
    }

    /**
     * Get the current error value. See {@link #setError(int)}.
     *
     * @return Error value.
     */
    @Column(order = 700)
    public int getError() {
        return error;
    }
    
    public void setSentTo(Id sentToId) {
        this.sentToId = sentToId;
    }

    public void setSentTo(BigDecimal idValue) {
        setSentTo(new Id(idValue));
    }

    public void setSentTo(StoredObject sentTo) {
        setSentTo(sentTo == null ? null : sentTo.getId());
    }

    @Column(required = false, style = "(any)")
    public Id getSentToId() {
        return sentToId;
    }

    public StoredObject getSentTo() {
        StoredObject so = get(sentToId);
        return so instanceof HasContacts ? so : null;
    }

    /**
     * Set is as sent with an associated error. (Use error = 0 for no error).
     *
     * @param error Error value to set.
     */
    public void sent(int error) {
    	this.error = error;
    	sent = true;
    	sentAt = DateUtility.now();
    }
    
    public boolean sent(TransactionControl tc, int error) {
    	sent(error);
    	return tc.save(this);
    }

    @Override
	public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(message)) {
            throw new Invalid_Value("Message");
        }
        if(error == 1) { // Try sending again
            sent = false;
        } else if(error > 2) { // Error greater than 2 means delivery not possible
            sent = true;
        }
        if(getReceiver() == null) {
            throw new Invalid_Value("Receiver");
        }
        super.validateData(tm);
    }

    public final HasContacts getReceiver() {
        return getSentTo() instanceof HasContacts hc ? hc : null;
    }
}
