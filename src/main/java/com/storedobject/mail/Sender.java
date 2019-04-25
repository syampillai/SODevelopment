package com.storedobject.mail;

import com.storedobject.core.Columns;
import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.core.TransactionManager;
import com.storedobject.core.annotation.Column;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;

public abstract class Sender extends StoredObject implements Closeable {

    public Sender() {
    }

    public static void columns(Columns columns) {
    }

    public void setName(String name) {
    }

    public String getName() {
        return null;
    }

    public void setFromAddressName(String fromAddressName) {
    }

    @Column(required = false)
    public String getFromAddressName() {
        return null;
    }

    public void setFromAddress(String fromAddress) {
    }

    public String getFromAddress() {
		return null;
    }

    public void setReplyToAddressName(String replyToAddressName) {
    }

    @Column(required = false)
    public String getReplyToAddressName() {
		return null;
    }

    public void setReplyToAddress(String replyToAddress) {
    }

    @Column(required = false)
    public String getReplyToAddress() {
		return null;
    }

    public void setSubject(String subject) {
    }

    @Column(required = false)
    public String getSubject() {
		return null;
    }

    public void setBody(String body) {
    }

    @Column(required = false, style = "(large)")
    public String getBody() {
		return null;
    }

    public void setBodyType(String bodyType) {
    }
    
    @Column(required = false)
    public String getBodyType() {
		return null;
    }

    public void setFooter(String footer) {
    }

    @Column(required = false, style = "(large)")
    public String getFooter() {
		return null;
    }

    public void setFooterType(String footerType) {
    }

    @Column(required = false)
    public String getFooterType() {
		return null;
    }

    public void setStatus(int status) {
    }

    public int getStatus() {
        return 0;
    }

    public static String[] getStatusValues() {
		return null;
    }

    public static String getStatusValue(int value) {
		return null;
    }

    public String getStatusValue() {
		return null;
    }
    
    public void setSenderGroup(Id senderGroupId) {
    }

    public void setSenderGroup(BigDecimal idValue) {
    }

    public void setSenderGroup(SenderGroup senderGroup) {
    }

    public Id getSenderGroupId() {
		return null;
    }

    public SenderGroup getSenderGroup() {
		return null;
    }

    public boolean canSend() {
    	return false;
    }
    
    public Error send(Mail mail) {
		return null;
    }

	@Override
	public void close() throws IOException {
	}

	public static int sendMails(TransactionManager tm) {
		return 0;
	}

	public static int sendMails(int count, TransactionManager tm) {
		return 0;
	}
}
