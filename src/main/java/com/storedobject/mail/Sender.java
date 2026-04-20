package com.storedobject.mail;

import com.storedobject.common.Email;
import com.storedobject.core.*;
import com.storedobject.core.annotation.Column;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

public abstract class Sender extends StoredObject implements Closeable {

	static final int BATCH_SIZE = 25;
	static final Set<Id> invalidGroups = new HashSet<>();
    private static final String[] statusValues = new String[] {
        "Active",
        "Inactive",
        "Error",
    };
    private String name, fromAddressName, fromAddress, replyToAddressName, replyToAddress, subject, body, bodyType, footer, footerType;
    private int status = 0;
	private Id senderGroupId;
    protected Session session;
    protected Transport transport;
    private SenderGroup group;

    public Sender() {
    }

    public static void columns(Columns columns) {
        columns.add("Name", "text");
        columns.add("FromAddressName", "text");
        columns.add("FromAddress", "text");
        columns.add("ReplyToAddressName", "text");
        columns.add("ReplyToAddress", "text");
        columns.add("Subject", "text");
        columns.add("Body", "text");
        columns.add("BodyType", "text");
        columns.add("Footer", "text");
        columns.add("FooterType", "text");
        columns.add("Status", "int");
		columns.add("SenderGroup", "Id");
    }

    public static void indices(Indices indices) {
        indices.add("lower(Name)", true);
    }

    @Override
	public String getUniqueCondition() {
        return "lower(Name)='" + getName().trim().toLowerCase().replace("'", "''") + "'";
    }
    
    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    public static String[] displayColumns() {
        return new String[] { "Name", };
    }

	public static String[] browseColumns() {
		return new String[] { "Name", "FromAddress", "ReplyToAddress", "Status", "SenderGroup.Name as Group" };
	}

	public void setName(String name) {
        this.name = name;
    }

    @Column(order = 100)
    public String getName() {
        return name;
    }

    public void setFromAddressName(String fromAddressName) {
        this.fromAddressName = fromAddressName;
    }

    @Column(required = false, order = 200)
    public String getFromAddressName() {
        return fromAddressName;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

	@Column(order = 300)
    public String getFromAddress() {
        return fromAddress;
    }

    public void setReplyToAddressName(String replyToAddressName) {
        this.replyToAddressName = replyToAddressName;
    }

    @Column(required = false, order = 400)
    public String getReplyToAddressName() {
        return replyToAddressName;
    }

    public void setReplyToAddress(String replyToAddress) {
        this.replyToAddress = replyToAddress;
    }

    @Column(required = false, order = 500)
    public String getReplyToAddress() {
        return replyToAddress;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Column(required = false, order = 600)
    public String getSubject() {
        return subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Column(required = false, order = 700, style = "(large)")
    public String getBody() {
        return body;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }
    
    @Column(required = false, order = 800)
    public String getBodyType() {
        return bodyType;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    @Column(required = false, order = 900, style = "(large)")
    public String getFooter() {
        return footer;
    }

    public void setFooterType(String footerType) {
        this.footerType = footerType;
    }

    @Column(required = false, order = 1000)
    public String getFooterType() {
        return footerType;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Column(order = 1100)
    public int getStatus() {
        return status;
    }

    public static String[] getStatusValues() {
        return statusValues;
    }

    public static String getStatusValue(int value) {
        String[] s = getStatusValues();
        return s[value % s.length];
    }

    public String getStatusValue() {
        return getStatusValue(status);
    }
    
    public void setSenderGroup(Id senderGroupId) {
        this.senderGroupId = senderGroupId;
        group = null;
    }

    public void setSenderGroup(BigDecimal idValue) {
        setSenderGroup(new Id(idValue));
    }

    public void setSenderGroup(SenderGroup senderGroup) {
        setSenderGroup(senderGroup.getId());
    }

    @Column(order = 1200)
    public Id getSenderGroupId() {
        return senderGroupId;
    }

    public SenderGroup getSenderGroup() {
    	if(group != null) {
    		return group;
    	}
    	group = get(SenderGroup.class, senderGroupId);
    	return group;
    }

    @Override
	public void validateData(TransactionManager tm) throws Exception {
        if(StringUtility.isWhite(name)) {
            throw new Invalid_Value("Name");
        }
        Email.check(fromAddress);
        Email.check(replyToAddress, true);
        senderGroupId = tm.checkType(this, senderGroupId, SenderGroup.class, false);
        super.validateData(tm);
    }
    
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean canSend() {
    	return status == 0;
    }
    
    @Override
    public String toString() {
    	return name;
    }

	public void sendTestMail(String to, String subject, String content) throws Exception {
		sendTestMail(to, subject, content, null);
	}

	public void sendTestMail(String to, String subject, String content, Device device) throws Exception {
		Debugger debugger = device == null ? null : new Debugger();
		try {
			createTransport(device == null ? null : debugger);
			if(transport == null) {
				throw new Exception("Unable to create mail transport");
			}
			MimeMessage m = mimeMessage();
			m.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			m.setSubject(subject, "UTF-8");
			MimeMultipart mp = new MimeMultipart();
			MimeBodyPart bp = new MimeBodyPart();
			bp.setContent(content, Mail.PLAIN_TEXT);
			mp.addBodyPart(bp);
			m.setContent(mp);
			m.saveChanges();
			transport.sendMessage(m, m.getAllRecipients());
		} finally {
			if(debugger != null && !debugger.isEmpty()) {
				ApplicationServer.log(device, "Mail Sender Trace:\n" + debugger.getTrace());
			}
			closeInternal();
		}
	}

	private MimeMessage mimeMessage() throws MessagingException {
		MimeMessage m = new MimeMessage(session);
		InternetAddress a = new InternetAddress(getFromAddress());
		String t = getFromAddressName();
		if(t != null && !t.isEmpty()) {
			try {
				a.setPersonal(t);
			} catch (UnsupportedEncodingException ignored) {
			}
		}
		m.setFrom(a);
		return m;
	}
    
    protected abstract void createTransport(Debugger debugger) throws MessagingException;

	private static String type(String type) {
		if(type == null || type.isBlank()) return Mail.PLAIN_TEXT;
		type = type.trim().toLowerCase();
		if(!type.contains(";")) {
			return type + Mail.CHAR_SET_TAG;
		}
		return type;
	}

	private static boolean isText(String type) {
		return type == null || type.isBlank() || type.toLowerCase().startsWith("text/plain");
	}

	private static boolean isHtml(String type) {
		return type != null && type.trim().toLowerCase().startsWith("text/html");
	}

	private String[] body(String body, String bodyType) {
		if(StringUtility.isWhite(footer) || (!isText(footerType) && !isHtml(footerType))) {
			return new String[] { body, type(bodyType) };
		}
        if(isText(bodyType) && isText(footerType)) { // Adding footer to body directly only if both are text
			return new String[] { body + "\n\n-- \n" + footer, Mail.PLAIN_TEXT };
		}
		if(isHtml(bodyType)) {
			String b = body.toLowerCase();
			int p = b.indexOf("</body>");
			if(p >= 0) {
				if(isText(footerType)) {
					footer = "<p>" + footer + "</p>";
				}
				b = body.substring(0, p);
				b += "\n<hr>" + footer + body.substring(p);
				return new String[] { b, type(bodyType) };
			}
		}
		if(isText(bodyType)) {
			body = "<p>" + body.replace("\n", "</p><p>") + "</p><hr>" + footer;
			return new String[] { body + "\n\n-- \n" + footer, type(bodyType) };
		}
		return new String[] { body, type(bodyType) };
	}

	private record Image(String contentID, String mime, byte[] image) implements DataSource {

		@Override
		public InputStream getInputStream() {
			return new ByteArrayInputStream(image);
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			throw new IOException("Not supported");
		}

		@Override
		public String getContentType() {
			return mime;
		}

		@Override
		public String getName() {
			return null;
		}
	}

	// Extract base64 encoded images from an HTML body and replace with content IDs
	// Only process images with the 'src' attributes having 'data:' as the source.
	// It should be of the form "src:data:mime/type;base64,..."
	// Warning: For simplicity, white spaces and single quotes are not handled.
	private static Object[] extractImages(String body) {
		Random random = new Random();
		Map<String, Image> images = new HashMap<>();
		int p, q, from = 0;
		String cid, b, mime;
		byte[] image;
		while((p = body.indexOf("src=\"data:", from)) >= 0) {
			from = p + 1;
			b = body.substring(p + 10);
			q = b.indexOf('\"');
			if(q < 0) {
				continue;
			}
			b = b.substring(0, q);
			q = b.indexOf(";base64,");
			if(q < 0) {
				continue;
			}
			mime = b.substring(0, q);
			b = b.substring(q + 8);
			System.err.println("Mime: " + mime + "\nBase64: " + b);
			image = Base64.getMimeDecoder().decode(b);
			cid = "SOI" + System.currentTimeMillis() + random.nextInt();
			images.put(cid, new Image(cid, mime, image));
			body = body.replace("src=\"data:" + mime + ";base64," + b, "src=\"cid:" + cid);
		}
		return new Object[] { body, images };
	}

    private void sendMessage(Mail mail) throws MessagingException {
    	MimeMessage m = mimeMessage();
    	m.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getToAddress()));
    	String t = mail.getCCAddress();
    	if(!StringUtility.isWhite(t)) {
    		m.addRecipients(Message.RecipientType.CC, InternetAddress.parse(t));
    	}
    	t = mail.getReplyToAddress();
    	if(StringUtility.isWhite(t)) {
    		t = getReplyToAddress();
    	}
		InternetAddress a;
    	if(!StringUtility.isWhite(t)) {
    		a = new InternetAddress(t);
    		if(t.equals(getReplyToAddress())) {
    			t = getReplyToAddressName();
    			if(t != null && !t.isEmpty()) {
    				try {
    					a.setPersonal(t);
    				} catch (UnsupportedEncodingException ignored) {
    				}
    			}
    		}
    		m.setReplyTo(new InternetAddress[] { a });
    	}
    	t = mail.getSubject();
    	if(StringUtility.isWhite(t)) {
    		t = getSubject();
    	}
    	m.setSubject(t, "UTF-8");
		String[] b;
		t = mail.getMessage();
		if(StringUtility.isWhite(t)) {
			b = body(getBody(), getBodyType());
		} else {
			b = body(t, mail.getMessageType());
		}
		MimeMultipart mixed = new MimeMultipart("mixed"), related = new MimeMultipart("related");
    	MimeBodyPart bodyPart = new MimeBodyPart();
		Object[] o = extractImages(b[0]);
		bodyPart.setContent((String)o[0], b[1]);
		if(isHtml(b[1])) {
			bodyPart.setHeader("Content-Transfer-Encoding", "7bit");
		}
    	related.addBodyPart(bodyPart);
        //noinspection unchecked
        for(Map.Entry<String, Image> image: ((Map<String, Image>)o[1]).entrySet()) {
			bodyPart = new MimeBodyPart();
			bodyPart.setDataHandler(new DataHandler(image.getValue()));
			bodyPart.setContentID("<" + image.getKey() + ">");
			bodyPart.setDisposition(MimeBodyPart.INLINE);
			related.addBodyPart(bodyPart);
		}
		MimeBodyPart innerWrapper = new MimeBodyPart();
		innerWrapper.setContent(related);
		mixed.addBodyPart(innerWrapper);
    	for(Attachment ma: mail.listLinks(Attachment.class)) {
    		bodyPart = new MimeBodyPart();
			if(isHtml(ma.getContentType())) {
				bodyPart.setHeader("Content-Transfer-Encoding", "7bit");
			}
    		bodyPart.setDataHandler(new DataHandler(ma));
    		t = ma.getFileName();
        	if(!StringUtility.isWhite(t)) {
        		bodyPart.setFileName(t);
        	}
    		mixed.addBodyPart(bodyPart);
			bodyPart.setHeader("Content-ID", "<" + ma.getContentID() + ">");
    	}
    	m.setContent(mixed);
    	m.saveChanges();
    	transport.sendMessage(m, m.getAllRecipients());
    }

    public Error send(Mail mail) {
		mail.setSender(this);
    	try {
    		createTransport(null);
    		if(transport == null) {
    			throw new Exception("Unable to create mail transport");
    		}
    	} catch(Throwable e) {
    		if(transport != null) {
    			try {
    				transport.close();
    			} catch (MessagingException ignored) {
    			}
    		}
    		transport = null;
    		session = null;
    		setStatus(2);
    		return new Error(this, mail, e);
    	}
    	try {
			sendMessage(mail);
			mail.sent(0);
			return null;
		} catch (Throwable e) {
			mail.sent(1);
			return new Error(this, mail, e);
		}
    }

	@Override
	public void close() throws IOException {
		session = null;
		if(transport != null) {
			try {
				transport.close();
			} catch (MessagingException e) {
				throw new IOException(e);
			} finally {
				transport = null;
			}
		}
	}

	private void closeInternal() {
		try {
			close();
		} catch (IOException ignored) {
		}
	}

	static int sendMails(List<Mail> mails, TransactionManager tm) {
		return sendMailsInt(tm, ObjectIterator.create(mails), null);
	}

	public static int sendMails(TransactionManager tm) {
		return sendMails(-1, tm);
	}

	public static int sendMails(int count, TransactionManager tm) {
		List<Sender> senders = senders();
		if(senders.isEmpty()) {
			return -1;
		}
		ObjectIterator<Mail> mails = list(Mail.class, "NOT Sent AND Error=0", "CreatedAt");
		if(count > 0) {
			mails = mails.limit(count);
		}
		int c, sent = 0;
		while(mails.hasNext()) {
			if(senders.isEmpty()) {
				if(sent == 0) {
					sent = -1;
				}
				break;
			}
			c = sendMailsInt(tm, mails, senders);
			if(c >= 0) {
				sent += c;
				continue;
			}
			if(sent == 0) {
				sent = -1;
			}
			break;
		}
		mails.close();
		return sent;
	}

	private static List<Sender> senders() {
		return list(Sender.class, "Status=0", true).toList();
	}

	private static int sendMailsInt(TransactionManager tm, ObjectIterator<Mail> mails, List<Sender> senders) {
		if(senders == null) {
			senders = senders();
			if(senders.isEmpty()) {
				return -1;
			}
		}
		Transaction t = null;
		Sender sender;
		int senderIndex = -1, c = 0;
		try {
			t = tm.createTransaction();
			Error error;
			for(Mail mail: mails) {
				if(senders.isEmpty()) {
					try {
						t.commit();
					} catch(Throwable ignored) {
					}
					return c;
				}
				if(++senderIndex == senders.size()) {
					senderIndex = 0;
				}
				sender = matchSender(senderIndex, senders, mail);
				if(sender == null) {
					continue;
				}
				error = sender.send(mail);
				if(error != null) {
					error.save(t);
					if(sender.status == 2) {
						sender.closeInternal();
						sender.addLink(t, error);
						sender.save(t);
						senders.remove(sender);
					} else {
						mail.save(t);
						mail.addLink(t, error);
					}
				} else {
					mail.save(t);
					mail.removeAllLinks(t, Error.class);
				}
				++c;
				if(c == BATCH_SIZE) {
					break;
				}
			}
		} catch (Exception e) {
			tm.log(e);
			if(t != null) {
				t.rollback();
				t = null;
			}
		}
		try {
			if(t != null) {
				t.commit();
			}
		} catch(Exception e) {
			tm.log(e);
			return 0;
		} finally {
			senders.forEach(Sender::closeInternal);
		}
		return c;
	}

	private static Sender matchSender(int senderIndex, List<Sender> senders, Mail mail) {
		int i = senderIndex;
		Sender s;
		SenderGroup sgs, sgm = null;
		while(true) {
			s = senders.get(i);
			if(++i == senders.size()) {
				i = 0;
			}
			if(!s.canSend()) {
				if(i == senderIndex) {
					break;
				}
				continue;
			}
			if(s.getSenderGroupId().equals(mail.getSenderGroupId())) {
				return s;
			}
			if(sgm == null) {
				sgm = mail.getSenderGroup();
			}
			if(sgm.getAlert()) {
				sgs = s.getSenderGroup();
				if(sgs.getAlert()) {
					return s;
				}
			}
			if(i == senderIndex) {
				break;
			}
		}
		invalidGroups.add(mail.getSenderGroupId());
		return null;
	}

	protected static void setEncryptionProperties(Properties p, int encryptionType) {
		if(encryptionType == 0) {
			return;
		}
		p.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
		if(encryptionType == 1 || encryptionType == 3) {
			p.put("mail.smtp.starttls.enable", "true");
			p.put("mail.smtp.starttls.required", "false");
		}
		if(encryptionType == 2 || encryptionType == 3) {
			p.put("mail.smtp.ssl.enable", "true");
		}
	}

	protected static class Debugger extends PrintStream {

		private final StringBuilder trace;
		private Exception exception;

		Debugger() {
			this(new StringBuilder());
		}

		Debugger(StringBuilder sb) {
			this(new OutputStream() {
				@Override
				public void write(int b) {
					sb.append((char) b);
				}
			}, sb);
		}

		private Debugger(OutputStream out, StringBuilder trace) {
			super(out, true);
			this.trace = trace;
		}

		public StringBuilder getTrace() {
			return trace;
		}

		public Exception getException() {
			return exception;
		}

		public void setException(Exception exception) {
			this.exception = exception;
		}

		public boolean isEmpty() {
			return trace.isEmpty();
		}

		public void debug(Session session, Properties properties) {
			trace.append("Properties:\n");
			int count = 0;
			for(String key: properties.stringPropertyNames()) {
				++count;
				trace.append('(').append(count).append(") ").append(key).append(" = ")
						.append(properties.getProperty(key)).append("\n");
			}
			session.setDebugOut(this);
			session.setDebug(true);
		}
	}
}
