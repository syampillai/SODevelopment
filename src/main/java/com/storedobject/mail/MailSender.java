package com.storedobject.mail;

import java.util.Properties;

import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

public class MailSender extends Sender {

    private static final String[] encryptionTypeValues = new String[] {
            "None", "STARTTLS", "SSL/TLS", "STARTTLS & SSL/TLS"
    };
    private String smtpServer;
    private int port;
    private String userName;
    private String password;
    private int encryptionType;

    public MailSender() {
    }

    public static void columns(Columns columns) {
        columns.add("SMTPServer", "text");
        columns.add("Port", "int");
        columns.add("EncryptionType", "int");
        columns.add("UserName", "text");
        columns.add("Password", "text");
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    public static String[] protectedColumns() {
        return new String[] { "Password", };
    }

    public static String[] links() {
        return new String[] {
        	"Errors|com.storedobject.mail.Error||",
        };
    }

    public void setSMTPServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    @Column(order = 1300)
    public String getSMTPServer() {
        return smtpServer;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Column(required = false, order = 1400)
    public int getPort() {
        return port;
    }

    public void setEncryptionType(int encryptionType) {
        this.encryptionType = encryptionType;
    }

    @Column(order = 1400)
    public int getEncryptionType() {
        return encryptionType;
    }

    public String  getEncryptionTypeValue() {
        return getEncryptionTypeValue(encryptionType);
    }

    public static String  getEncryptionTypeValue(int encryptionType) {
        return encryptionTypeValues[encryptionType % encryptionTypeValues.length];
    }

    public static String[] getEncryptionTypeValues() {
        return encryptionTypeValues;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(required = false, order = 1500)
    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(required = false, order = 1600, style = "(hidden)")
    public String getPassword() {
        return password;
    }

    @Override
	public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(smtpServer)) {
            throw new Invalid_Value("SMTP Server");
        }
        super.validateData(tm);
    }

    @Override
    protected void createTransport(Debugger debugger) throws MessagingException {
    	if(transport != null) {
    		return;
    	}
		Properties p = new Properties();
		p.put("mail.mime.charset", "UTF-8");
		p.put("mail.smtp.host", smtpServer);
		if(port > 0) {
			p.put("mail.smtp.port", String.valueOf(port));
		}
		final String un = StringUtility.isWhite(userName) ? getFromAddress() : userName, pw = password;
		Authenticator authenticator = null;
		if(!pw.isEmpty()) {
			authenticator = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(un, pw);
				}
			};
		}
        setEncryptionProperties(p, encryptionType);
		if(authenticator != null) {
			p.put("mail.smtp.auth", "true");
			session = Session.getInstance(p, authenticator);
		} else {
			session = Session.getInstance(p);
		}
        if(debugger != null) {
            debugger.debug(session, p);
        }
		transport = session.getTransport("smtp");
		if(authenticator != null) {
			if(port > 0) {
				transport.connect(smtpServer, port, un, pw);
			} else {
				transport.connect(smtpServer, un, pw);
			}
		} else {
			transport.connect();
		}
    }
}
