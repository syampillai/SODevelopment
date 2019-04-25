package com.storedobject.mail;

import com.storedobject.core.Columns;
import com.storedobject.core.annotation.Column;

public class MailSender extends Sender {

    public MailSender() {
    }

    public static void columns(Columns columns) {
    }

    public void setSMTPServer(String smtpServer) {
    }

    public String getSMTPServer() {
        return null;
    }

    public void setPort(int port) {
    }

    @Column(required = false)
    public int getPort() {
        return 0;
    }

    public void setUseTLS(boolean useTLS) {
    }

    public boolean getUseTLS() {
        return false;
    }

    public void setUserName(String userName) {
    }

    @Column(required = false)
    public String getUserName() {
        return null;
    }

    public void setPassword(String password) {
    }

    @Column(required = false)
    public String getPassword() {
        return null;
    }
}
