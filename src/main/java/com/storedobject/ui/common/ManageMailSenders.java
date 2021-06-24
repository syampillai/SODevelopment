package com.storedobject.ui.common;

import com.storedobject.core.SQLConnector;
import com.storedobject.core.StoredObject;
import com.storedobject.mail.GMailSender;
import com.storedobject.mail.MailSender;
import com.storedobject.ui.Application;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.vaadin.ActionForm;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public class ManageMailSenders extends ObjectEditor<MailSender> {

    private Button gmailSenders;
    private ManageGMailSenders gmsEditor = null;

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
                "UseTLS",
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
                    StoredObject.list(GMailSender.class, "lower(FromAddress)='" + gms.getFromAddress().toLowerCase() + "'").collectAll().
                            stream().filter(s -> !s.getId().equals(gms.getId()) && s.canSend()).findAny();
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
                gsenders = StoredObject.list(GMailSender.class, "lower(FromAddress)='" + sender.getFromAddress().toLowerCase() + "'").collectAll();
            }

            @Override
            protected void buildFields() {
                ELabel m = new ELabel();
                add(m);
                m.append("You are about to connect to GMail site to authenticate the following mail senders:", "blue");
                for(GMailSender gms: gsenders) {
                    m.newLine().append(gms.getName(), "red");
                }
                m.newLine().append("Once you are done with it, please check the authentication status of this sender again.", "blue").update();
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
}
