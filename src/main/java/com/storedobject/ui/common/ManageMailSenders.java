package com.storedobject.ui.common;

import com.storedobject.core.SQLConnector;
import com.storedobject.core.StoredObject;
import com.storedobject.mail.GMailSender;
import com.storedobject.mail.MailSender;
import com.storedobject.ui.*;
import com.storedobject.ui.Application;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public class ManageMailSenders extends ObjectEditor<MailSender> implements Transactional {

    private Button gmailSenders;
    private ManageGMailSenders gmsEditor = null;
    private final Button test = new Button("Test", e -> test());
    private Tester tester;

    public ManageMailSenders() {
        super(MailSender.class);
    }

    @Override
    public Stream<String> getFieldNames() {
        return Stream.of("Name",
                "SMTPServer",
                "Port",
                "FromAddress",
                "ReplyToAddress",
                "EncryptionType",
                "UserName",
                "Password",
                "Status",
                "SenderGroup");
    }

    @Override
    protected void createExtraButtons() {
        gmailSenders = new Button("Manage GMail Senders", "mail", this);
    }

    @Override
    protected void addExtraButtons() {
        // buttonPanel.add(gmailSenders);
        if(getObject() != null) {
            buttonPanel.add(test);
        }
    }

    @Override
    public void clicked(Component c) {
        if(c == gmailSenders) {
            if(gmsEditor == null) {
                gmsEditor = new ManageGMailSenders();
            }
            gmsEditor.execute();
            return;
        }
        super.clicked(c);
    }

    @Override
    public void clean() {
        if(gmsEditor != null) {
            ManageGMailSenders t = gmsEditor;
            gmsEditor = null;
            t.close();
        }
    }

    public class ManageGMailSenders extends ObjectEditor<GMailSender> {

        private Button mailSenders, setRefreshToken;

        public ManageGMailSenders() {
            super(GMailSender.class);
        }

        @Override
        public Stream<String> getFieldNames() {
            return Stream.of("Name",
                    "FromAddress",
                    "ReplyToAddress",
                    "ClientId",
                    "ClientSecret",
                    "ApplicationURI",
                    "Authenticated",
                    "Status",
                    "SenderGroup");
        }

        @Override
        protected void createExtraButtons() {
            setRefreshToken = new Button("Authenticate", "ok", this);
            mailSenders = new Button("Manage Mail Senders", "mail", this);
        }

        @Override
        protected void addExtraButtons() {
            if(getObject() != null) {
                buttonPanel.add(setRefreshToken);
            }
            buttonPanel.add(mailSenders);
        }

        @Override
        public void clicked(Component c) {
            if(c == mailSenders) {
                ManageMailSenders.this.execute();
                return;
            }
            if(c == setRefreshToken) {
                GMailSender gms = getObject();
                if(!gms.getRefreshToken().startsWith("State/")) {
                    Application a = getApplication();
                    ActionForm.execute("This sender was already authenticated, do you want to do it again?", () -> {
                        try {
                            authorize();
                        } catch (Exception e) {
                            a.access(() -> ManageGMailSenders.this.error(e));
                        }
                    });
                    return;
                }
                try {
                    authorize();
                } catch (Exception e) {
                    error(e);
                }
                return;
            }
            super.clicked(c);
        }

        public String getAuthenticated() {
            GMailSender gms = getObject();
            return gms == null ? "No" : (gms.getRefreshToken().startsWith("State/") ? "No" : "Yes");
        }

        @SuppressWarnings("RedundantThrows")
        private void authorize() throws Exception {
            GMailSender gms = getObject();
            Optional<GMailSender> another =
                    StoredObject.list(GMailSender.class, "lower(FromAddress)='"
                                    + gms.getFromAddress().toLowerCase() + "'").toList().stream()
                            .filter(s -> !s.getId().equals(gms.getId()) && s.canSend()).findAny();
            if(another.isPresent()) {
                GMailSender a = another.get();
                gms.setClientId(a.getClientId());
                gms.setClientSecret(a.getClientSecret());
                gms.setRefreshToken(a.getRefreshToken());
                gms.setApplicationURI(a.getApplicationURI());
                transact(gms::save);
                reload();
                return;
            }
            StringBuilder u = new StringBuilder();
            Random random = new Random();
            String state;
            int r, c;
            while(true) {
                for(int i = 0; i < 30; i++) {
                    r = random.nextInt(36);
                    if(r < 26) {
                        c = 'A' + r;
                    } else {
                        c = '0' + r - 26;
                    }
                    u.append((char)c);
                }
                state = u.toString();
                if(!StoredObject.exists(GMailSender.class, "RefreshToken='State/" + state + "'")) {
                    break;
                }
                u.delete(0, u.length());
            }
            gms.setRefreshToken("State/" + state);
            u = new StringBuilder("https://accounts.google.com/o/oauth2/auth?client_id=");
            u.append(URLEncoder.encode(gms.getClientId(), StandardCharsets.UTF_8));
            u.append("&redirect_uri=");
            u.append(URLEncoder.encode(gms.getApplicationURI() + "/" + SQLConnector.getDatabaseName() + "-scheduler", StandardCharsets.UTF_8));
            u.append("&response_type=code");
            u.append("&scope=").append(URLEncoder.encode("https://mail.google.com/", StandardCharsets.UTF_8));
            u.append("&login_hint=").append(URLEncoder.encode(gms.getFromAddress(), StandardCharsets.UTF_8));
            u.append("&approval_prompt=force");
            u.append("&access_type=offline");
            u.append("&state=").append(state);
            new Authenticate(u.toString(), gms).execute();
            ManageMailSenders.this.close();
        }

        private class Authenticate extends DataForm {

            private final String url;
            private final GMailSender sender;
            private final Collection<GMailSender> gsenders;

            public Authenticate(String url, GMailSender sender) {
                super("Authenticate GMail");
                this.url = url;
                this.sender = sender;
                gsenders = StoredObject.list(GMailSender.class, "lower(FromAddress)='"
                        + sender.getFromAddress().toLowerCase() + "'").toList();
            }

            @Override
            protected void buildFields() {
                ELabel m = new ELabel();
                add(m);
                m.append("You are about to connect to GMail site to authenticate the following mail senders:",
                        Application.COLOR_SUCCESS);
                for(GMailSender gms: gsenders) {
                    m.newLine().append(gms.getName(), Application.COLOR_ERROR);
                }
                m.newLine().append("Once you are done with it, please check the authentication status of this sender again.",
                        Application.COLOR_SUCCESS).update();
            }

            @Override
            protected boolean process() {
                gsenders.forEach(s ->
                        transact(t -> {
                            s.setRefreshToken(sender.getRefreshToken());
                            s.setClientId(sender.getClientId());
                            s.setClientSecret(sender.getClientSecret());
                            s.save(t);
                        }));
                UI.getCurrent().getPage().executeJs("window.open('" + url + "', '_blank', 'height=570,width=520,scrollbars=yes')");
                return true;
            }
        }
    }

    private void test() {
        if(tester == null) {
            tester = new Tester();
        }
        tester.execute();
    }

    private class Tester extends DataForm {

        private final EmailField to = new EmailField("To Address");
        private final TextField subject = new TextField("Subject");
        private final TextArea content = new TextArea("Content");

        public Tester() {
            super("Send Test Mail");
            addField(to, subject, content);
            setRequired(to);
            setRequired(subject);
            setRequired(content);
            subject.setValue("Test Mail");
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            content.setValue("Testing mail sender " + getObject().getName());
            super.execute(parent, doNotLock);
        }

        @Override
        protected boolean process() {
            try {
                getObject().sendTestMail(to.getValue(), subject.getValue(), content.getValue(), getApplication());
                message("Mail sent successfully");
            } catch(Exception e) {
                String m = e.getMessage();
                if(m != null) {
                    getApplication().log(e);
                    m += "<BR/>";
                } else {
                    m = "Error occurred while sending. ";
                }
                m += "Please check the System Log";
                error(m);
            }
            return true;
        }
    }
}
