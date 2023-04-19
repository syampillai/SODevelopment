package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.core.MessageTemplate;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocomplete;

import java.util.Random;

/**
 * Verify OTP.
 *
 * @author Syam
 */
public class VerifyOTP extends View implements CloseableView {

    private static final String OLD = "Old instance";
    private final Random random = new Random();
    private final Application a;
    private final Runnable cancelled, verified, errorWhileSending;
    private String templateName;
    private boolean oldInstance = false;
    private final boolean singleOTP;
    private int userTimeout = 180, senderTimeoutEmail = 10, senderTimeoutSMS = 10, resendTimeout = 120;
    private OTP phone, email;
    private volatile boolean closed = false;
    private final HorizontalLayout caption = new HorizontalLayout();
    private final VerticalLayout layout = new VerticalLayout();
    private String customTag;

    /**
     * Constructor.
     *
     * @param phone Mobile phone number. If null, SMS OTP will not be verified.
     * @param email Email. If null, email OTP will not be verified.
     * @param verified Runnable to be invoked if verified successfully. (Can not be <code>null</code>).
     * @param cancelled Runnable to be invoked if verification is cancelled by the user. (Can not be <code>null</code>).
     * @param errorWhileSending Runnable to be invoked if technical issue with sending SMS/emails. (Can not be
     *                          <code>null</code>).
     */
    public VerifyOTP(String phone, String email, Runnable verified, Runnable cancelled, Runnable errorWhileSending) {
        this(false, phone, email, verified, cancelled, errorWhileSending);
    }

    /**
     * Constructor.
     *
     * @param phone Mobile phone number. If null, SMS OTP will not be verified.
     * @param email Email. If null, email OTP will not be verified.
     * @param verified Runnable to be invoked if verified successfully. (Can not be <code>null</code>).
     * @param cancelled Runnable to be invoked if verification is cancelled by the user. (Can not be <code>null</code>).
     * @param errorWhileSending Runnable to be invoked if technical issue with sending SMS/emails. (Can not be
     *                          <code>null</code>).
     * @param singleOTP Whether to send the same OTP to both phone and email or not.
     */
    public VerifyOTP(boolean singleOTP, String phone, String email, Runnable verified, Runnable cancelled,
                     Runnable errorWhileSending) {
        super("Verify OTP");
        if(phone != null && phone.isBlank()) {
            phone = null;
        }
        if(email != null && email.isBlank()) {
            email = null;
        }
        this.singleOTP = singleOTP;
        a = Application.get();
        if(a.getSMSSender() == null) {
            throw new SORuntimeException("No SMS sender configured!");
        }
        if(a.getMailSender() == null) {
            throw new SORuntimeException("No email sender configured!");
        }
        if(verified == null || cancelled == null || errorWhileSending == null) {
            throw new SORuntimeException("Actions not specified!");
        }
        if(phone == null && email == null) {
            throw new SORuntimeException("Both mobile and email can't be empty!");
        }
        this.verified = verified;
        this.cancelled = cancelled;
        this.errorWhileSending = errorWhileSending;
        setComponent(layout);
        ELabel caption1 = new ELabel(getCaption(), "font-size:large;font-weight:900");
        ELabel caption2 = new ELabel("(One-Time Password)", "font-size:medium;font-weight:900");
        HorizontalLayout buttons = new HorizontalLayout();
        layout.add(caption1, caption2, caption);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, caption1, caption2, caption);
        if(phone == null) {
            this.email = new OTP(new MailSender(email), email);
        }
        if(email == null) {
            this.phone = new OTP(new SMSSender(phone), phone);
        }
        if(phone != null && email != null) {
            if(singleOTP) {
                this.phone = new OTP(new BothSender(phone, email), phone, email);
            } else {
                this.phone = new OTP(new SMSSender(phone), phone);
                this.email = new OTP(new MailSender(email), email);
            }
        }
        Button cancel = new Button("Cancel", e -> abort());
        cancel.getElement().getStyle().set("background", "var(--lumo-error-color-10pct)");
        buttons.add(new Button("Submit", "ok", e -> {}), cancel);
        layout.add(buttons);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, caption1, caption2, caption, buttons);
        setWindowMode(true);
    }

    @Override
    public void decorateComponent() {
        super.decorateComponent();
        Component c = getComponent();
        if(c instanceof Dialog d) {
            d.setMaxWidth("400px");
        }
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(oldInstance) {
            throw new SORuntimeException(OLD);
        }
        oldInstance = true;
        super.execute(parent, doNotLock);
        a.startPolling(this);
        if(phone != null) {
            phone.sendOTP();
        }
        if(email != null) {
            email.sendOTP();
        }
    }

    @Override
    public void clean() {
        super.clean();
        a.stopPolling(this);
    }

    @Override
    public void abort() {
        closeTimers();
        super.abort();
        cancelled.run();
    }

    @Override
    public void close() {
        closeTimers();
        super.close();
    }

    private void closeTimers() {
        closed = true;
        if(phone != null) {
            phone.abort();
        }
        if(email != null) {
            email.abort();
        }
    }

    /**
     * Set a custom tag that will be passed to the quick senders as an additional parameter.
     *
     * @param customTag Custom tag to be set.
     */
    public void setCustomTag(String customTag) {
        this.customTag = customTag;
    }

    /**
     * Set the template name to be used for constructing the OTP message. If no template name is set,
     * a default message is constructed, and it will look like:
     * <p>"Your OTP is ABC:12345678"</p>
     *
     * @param templateName Name of the template to use.
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    private String smsMessage() {
        return template(0);
    }

    private String emailMessage() {
        return template(1);
    }

    private String template(int type) {
        MessageTemplate mt = null;
        if(templateName != null && !templateName.isBlank()) {
            mt = MessageTemplate.list(templateName).find(t -> t.getDelivery() == type);
        }
        return template(mt);
    }

    private String template(MessageTemplate mt) {
        return (mt == null ? "Your OTP is <1>" : mt.getTemplate()).replace("<1>", "<OTP>");
    }

    /**
     * Set "resend" timeout.
     * @param resendTimeout Timeout in seconds.
     */
    public void setResendTimeout(int resendTimeout) {
        if(oldInstance) {
            throw new SORuntimeException(OLD);
        }
        this.resendTimeout = resendTimeout;
    }

    /**
     * Set "SMS sender" timeout.
     * @param senderTimeoutSMS Timeout in seconds.
     */
    public void setSenderTimeoutSMS(int senderTimeoutSMS) {
        if(oldInstance) {
            throw new SORuntimeException(OLD);
        }
        this.senderTimeoutSMS = senderTimeoutSMS;
    }

    /**
     * Set "Email sender" timeout.
     * @param senderTimeoutEmail Timeout in seconds.
     */
    public void setSenderTimeoutEmail(int senderTimeoutEmail) {
        if(oldInstance) {
            throw new SORuntimeException(OLD);
        }
        this.senderTimeoutEmail = senderTimeoutEmail;
    }

    /**
     * Set "user response" timeout.
     * @param userTimeout Timeout in seconds.
     */
    public void setUserTimeout(int userTimeout) {
        if(oldInstance) {
            throw new SORuntimeException(OLD);
        }
        this.userTimeout = userTimeout;
    }

    private class SMSSender implements Sender {

        private volatile int status = -1; // Not initiated
        private OTP otp;
        private final String sendTo;

        SMSSender(String sendTo) {
            this.sendTo = sendTo;
        }

        @Override
        public void run() {
            status = 0; // Initiated
            if(a.getSMSSender().send(sendTo, smsMessage(), otp.prefix + otp.otp, customTag)) {
                status = 1; // Send
            } else {
                status = 2; // Can't send - error
            }
            statusChanged();
        }

        @Override
        public void send(OTP otp) {
            this.otp = otp;
            new Thread(this).start();
        }

        @Override
        public OTP getOTP() {
            return otp;
        }

        @Override
        public int status() {
            return status;
        }
    }

    private class MailSender implements Sender {

        private volatile int status = -1; // Not initiated
        private OTP otp;
        private final String sendTo;

        MailSender(String sendTo) {
            this.sendTo = sendTo;
        }

        @Override
        public void run() {
            status = 0; // Initiated
            if(a.getMailSender().
                    send(sendTo, emailMessage(), null, null, otp.prefix + otp.otp, customTag)) {
                status = 1; // Send
            } else {
                status = 2; // Can't send - error
            }
            statusChanged();
        }

        @Override
        public void send(OTP otp) {
            this.otp = otp;
            new Thread(this).start();
        }

        @Override
        public OTP getOTP() {
            return otp;
        }

        @Override
        public int status() {
            return status;
        }
    }

    private class BothSender implements Sender {

        private volatile int status = -1; // Not initiated
        private final SMSSender smsSender;
        private final MailSender mailSender;
        private OTP otp;

        BothSender(String sendToPhone, String sendToEmail) {
            smsSender = new SMSSender(sendToPhone) {
                @Override
                public void statusChanged() {
                    smsStatus();
                }
            };
            mailSender = new MailSender(sendToEmail) {
                @Override
                public void statusChanged() {
                    emailStatus();
                }
            };
        }

        @Override
        public void run() {
            status = 0; // Initiated
            smsSender.send(otp);
            mailSender.send(otp);
        }

        @Override
        public void send(OTP otp) {
            this.otp = otp;
            new Thread(this).start();
        }

        @Override
        public OTP getOTP() {
            return otp;
        }

        private synchronized void smsStatus() {
            if(status == 1 || status == 2) {
                return;
            }
            if(smsSender.status() == 1) {
                status = 1;
            } else {
                status = mailSender.status();
            }
            if(status == 1 || status == 2) {
                otp.sendStatusChanged();
            }
        }

        private synchronized void emailStatus() {
            if(status == 1 || status == 2) {
                return;
            }
            if(mailSender.status() == 1) {
                status = 1;
            } else {
                status = smsSender.status();
            }
            if(status == 1 || status == 2) {
                otp.sendStatusChanged();
            }
        }

        @Override
        public int status() {
            return status;
        }
    }

    private interface Sender extends Runnable {
        void send(OTP otp);
        int status();
        OTP getOTP();
        default void statusChanged() {
            getOTP().sendStatusChanged();
        }
    }

    private class OTP {

        private final TimerComponent timer = new TimerComponent();
        private final Animation[] animation = { Animation.SHAKE, Animation.FLASH };
        private int animationIndex = 0;
        private final IntegerField otpField = new IntegerField();
        private final Button resend;
        private final ELabel prefixLabel = new ELabel(), done = new ELabel();
        private int otp;
        private String prefix;
        private boolean verified = false, expire = false;
        private final Sender sender;
        private int stage = 0; // 0:Initial, 1:Sending, 2:Sent, 3:Resending, 4:Resent

        private OTP(Sender sender, String... sentTo) {
            this.sender = sender;
            timer.addListener(e -> timedOut());
            timer.setSuffix(" seconds");
            StringBuilder s = new StringBuilder();
            for(String sTo: sentTo) {
                if(s.isEmpty()) {
                    s.append("OTP sent to ");
                } else {
                    s.append(" & ");
                }
                s.append(sTo, 0, 2).append("...");
                if(sTo.indexOf('.') >= 0) {
                    s.append(sTo.substring(sTo.lastIndexOf('.') + 1));
                } else {
                    s.append(sTo.substring(sTo.length() - 2));
                }
            }
            resend = new Button("Resend", VaadinIcon.PAPERPLANE_O, e -> resend());
            resend.setDisableOnClick(true);
            resend.setVisible(false);
            otpField.setAutocomplete(Autocomplete.OFF);
            otpField.setLength(6);
            otpField.setEmptyDisplay("");
            otpField.setWidth("6em");
            otpField.setEnabled(false);
            if(singleOTP) {
                caption.add(timer);
            } else {
                layout.add(timer);
            }
            ELabel label = new ELabel(s.toString());
            CompoundField cf;
            layout.add(label, cf = new CompoundField(prefixLabel, otpField, resend, done));
            if(singleOTP) {
                layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, label, cf);
            } else {
                layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, timer, label, cf);
            }
            otpField.addValueChangeListener(e -> verify());
        }

        void sendOTP() {
            otpField.setEnabled(false);
            prefix = genPrefix();
            otp = genOTP();
            prefixLabel.clearContent().append(prefix,"font-family:monospace").update();
            ++stage;
            sender.send(this);
            timer.setPrefix("Sending OTP in ");
            int time = sender instanceof BothSender ? Math.max(senderTimeoutSMS, senderTimeoutEmail) :
                    (sender instanceof SMSSender ? senderTimeoutSMS : senderTimeoutEmail);
            expire = false;
            timer.countDown(time);
        }

        private void resend() {
            expire = false;
            synchronized(random) {
                if(closed) {
                    return;
                }
                resend.setVisible(false);
                if(verified) {
                    return;
                }
                otpField.setValue(0);
                --stage;
                sendOTP();
            }
        }

        private boolean verify() {
            verified = otp == otpField.getValue();
            if(!verified) {
                shake();
                return false;
            }
            timer.abort();
            otpField.setEnabled(false);
            done.clearContent().append("Verified", Application.COLOR_INFO).update();
            synchronized(random) {
                if(closed) {
                    return true;
                }
                if(!singleOTP) {
                    if(phone != null && !phone.verified) {
                        return false;
                    }
                    if(email != null && !email.verified) {
                        return false;
                    }
                }
                closed = true;
                VerifyOTP.this.close();
                a.access(VerifyOTP.this.verified::run);
            }
            return true;
        }

        private void timedOut() {
            synchronized(random) {
                if(closed) {
                    return;
                }
                a.access(() -> {
                    if(expire) { // Expired
                        if(!verify()) { // Check one more time before aborting
                            VerifyOTP.this.abort();
                        }
                        return;
                    }
                    if(stage == 1 || stage == 3) { // Was sending (Unable to send!)
                        sendStatusChangedInt();
                        return;
                    }
                    if(stage == 2) { // Sent earlier, we can resend now
                        ++stage;
                        resend.setVisible(true);
                        timer.setPrefix("OTP will expire in ");
                        int time = userTimeout - resendTimeout;
                        if(time <= 5) {
                            time = 5;
                        }
                        expire = true;
                        timer.countDown(time);
                    }
                });
            }
        }

        private void shake() {
            animation[animationIndex % animation.length].animate(otpField);
            animationIndex++;
        }

        private String genPrefix() {
            char[] p = new char[4];
            synchronized(random) {
                for(int i = 0; i < 3; i++) {
                    p[i] = (char) ('A' + random.nextInt(26));
                }
            }
            p[3] = ':';
            String s = new String(p);
            return switch(s) {
                case "ASS", "FUK", "FUC", "PIG", "OTP", "TIT" -> genPrefix();
                default -> s;
            };
        }

        private int genOTP() {
            int o = 0;
            synchronized(random) {
                while(!(o > 100000 && o < 1000000)) {
                    o = random.nextInt();
                }
            }
            return o;
        }

        void abort() {
            otpField.setEnabled(false);
            timer.abort();
        }

        void sendStatusChanged() {
            a.access(() -> {
                synchronized(random) {
                    ++stage;
                    sendStatusChangedInt();
                }
            });
        }

        private void sendStatusChangedInt() {
            if(closed) {
                return;
            }
            if(sender.status() == 1) {
                if(stage == 1 || stage == 3) {
                    ++stage;
                }
                otpField.setEnabled(true);
                if(stage == 2) { // Sent for the first time
                    timer.setPrefix("OTP can be resent in ");
                    expire = false;
                    timer.countDown(resendTimeout);
                } else {
                    timer.setPrefix("OTP will expire in ");
                    expire = true;
                    timer.countDown(userTimeout);
                }
                return;
            }
            // Unable to send
            if(singleOTP) { // I am the only one, abort...
                VerifyOTP.this.abort();
                errorWhileSending.run();
                return;
            }
            if(phone != null && phone.sender.status() == 1) {
                return; // SMS is working
            }
            if(email != null && email.sender.status() == 1) {
                return; // Email is working
            }
            // Both SMS and Email are not working!
            VerifyOTP.this.abort();
            errorWhileSending.run();
        }
    }
}
