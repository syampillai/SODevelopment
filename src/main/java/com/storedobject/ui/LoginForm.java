package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.oauth.OAuth;
import com.storedobject.ui.tools.BiometricButton;
import com.storedobject.ui.tools.LoginNameField;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.shared.Registration;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a template-based login screen and its template can be defined in the {@link TextContent} named
 * "com.storedobject.ui.LoginForm". If this template exists, this will be used for rendering the login view
 * instead of the default one. Following id-values are used to map the necessary fields in the template:
 * <p>id = "layout" (Should be an HTML div tag. Represents the outermost layout in which the whole login
 * form is wrapped. This is optional.)</p>
 * <p>id = "login" (Should be a vaadin-combo-box tag. Represents user-name field. Not working due to a bug,
 * use "user" instead.)</p>
 * <p>id = "user" (Should be a vaadin-text-field tag. Represents user-name field.)</p>
 * <p>id = "remember" (Should be a vaadin-checkbox tag. Used to remember the user-name. This is optional.)</p>
 * <p>id = "password" (Should be a vaadin-password-field tag.)</p>
 * <p>id = "authCode" (Authenticator code. Should be a vaadin-custom-field tag. This is optional.)</p>
 * <p>id = "cram" (Should be a vaadin-custom-field tag. This is optional.)</p>
 * <p>id = "biometric" (Should be a so-auth tag. Used for showing biometric option. This is optional.)</p>
 * <p>id = "ok" ('OK' or 'Sign in' button. This should be a vaadin-button tag.)</p>
 * <p>id = "cancel" ('Cancel' button. This should be a vaadin-button tag.)</p>
 * <p>id = "forgot" ('Forgot Password' button. This should be a vaadin-button tag. This is optional.)</p>
 * <p>id = "forgotLink" ('Forgot Password' link. This should be an HTML a tag. This is optional.)</p>
 * <p>id = "terms"  (Should be a vaadin-checkbox tag. User needs to click this always. This is optional.)</p>
 * <p>id = "year" (Should be a span tag. Value of the current year will be set to this. This is optional.)</p>
 * <p>id = "viewX" (The value of X can be anything. Should be an HTML a tag with href attribute set to empty string.
 * Typically used for providing static content views. This is optional.)</p>
 * <p>Additional attributes can be defined for certain id values:</p>
 * <p>id = "terms" A "message" attribute can be specified for the message to show if it is not checked.</p>
 * <p>id = "viewX" A "file" attribute must be specified with the name of the file to view. A "caption" attribute may
 * be specified if you want to show the caption as something other than the file name.</p>
 *
 * @author Syam
 */
public class LoginForm extends TemplateView implements HomeView, FullScreen {

    private static final Map<String, String> codeSVGs = new HashMap<>();
    static {
        codeSVGs.put("google",
                """
                <?xml version="1.0" ?>
                <svg id="Capa_1" style="enable-background:new 0 0 150 150;" version="1.1"\s
                    viewBox="0 0 150 150" xml:space="preserve" xmlns="http://www.w3.org/2000/svg"\s
                    xmlns:xlink="http://www.w3.org/1999/xlink">
                    <style type="text/css">
                        .st0{fill:#1A73E8;}
                        .st1{fill:#EA4335;}
                        .st2{fill:#4285F4;}
                        .st3{fill:#FBBC04;}
                        .st4{fill:#34A853;}
                        .st5{fill:#4CAF50;}
                        .st6{fill:#1E88E5;}
                        .st7{fill:#E53935;}
                        .st8{fill:#C62828;}
                        .st9{fill:#FBC02D;}
                        .st10{fill:#1565C0;}
                        .st11{fill:#2E7D32;}
                        .st12{fill:#F6B704;}
                        .st13{fill:#E54335;}
                        .st14{fill:#4280EF;}
                        .st15{fill:#34A353;}
                        .st16{clip-path:url(#SVGID_2_);}
                        .st17{fill:#188038;}
                        .st18{opacity:0.2;fill:#FFFFFF;enable-background:new    ;}
                        .st19{opacity:0.3;fill:#0D652D;enable-background:new    ;}
                        .st20{clip-path:url(#SVGID_4_);}
                        .st21{opacity:0.3;fill:url(#_45_shadow_1_);enable-background:new    ;}
                        .st22{clip-path:url(#SVGID_6_);}
                        .st23{fill:#FA7B17;}
                        .st24{opacity:0.3;fill:#174EA6;enable-background:new    ;}
                        .st25{opacity:0.3;fill:#A50E0E;enable-background:new    ;}
                        .st26{opacity:0.3;fill:#E37400;enable-background:new    ;}
                        .st27{fill:url(#Finish_mask_1_);}
                        .st28{fill:#FFFFFF;}
                        .st29{fill:#0C9D58;}
                        .st30{opacity:0.2;fill:#004D40;enable-background:new    ;}
                        .st31{opacity:0.2;fill:#3E2723;enable-background:new    ;}
                        .st32{fill:#FFC107;}
                        .st33{opacity:0.2;fill:#1A237E;enable-background:new    ;}
                        .st34{opacity:0.2;}
                        .st35{fill:#1A237E;}
                        .st36{fill:url(#SVGID_7_);}
                        .st37{fill:#FBBC05;}
                        .st38{clip-path:url(#SVGID_9_);fill:#E53935;}
                        .st39{clip-path:url(#SVGID_11_);fill:#FBC02D;}
                        .st40{clip-path:url(#SVGID_13_);fill:#E53935;}
                        .st41{clip-path:url(#SVGID_15_);fill:#FBC02D;}
                    </style>
                    <g>
                        <path class="st5" d="M121.1,57.9L99.1,74.3v35.8h15.4c3.6,0,6.6-2.9,6.6-6.6V57.9z"/>
                        <path class="st6" d="M28.9,57.9l21.9,16.5v35.8H35.5c-3.6,0-6.6-2.9-6.6-6.6V57.9z"/>
                        <polygon class="st7" points="99.1,46.9 75,65 50.9,46.9 50.9,74.3 75,92.4 99.1,74.3  "/>
                        <path class="st8" d="M28.9,49.3v8.6l21.9,16.5V46.9L44,41.8c-1.6-1.2-3.6-1.9-5.7-1.9l0,0C33.1,39.9,28.9,44.1,28.9,49.3z"/>
                        <path class="st9" d="M121.1,49.3v8.6L99.1,74.3V46.9l6.9-5.1c1.6-1.2,3.6-1.9,5.7-1.9l0,0C116.9,39.9,121.1,44.1,121.1,49.3z"/>
                       \s
                        <!-- Added Circle -->
                        <circle cx="75" cy="75" r="70" stroke="#0000FF" stroke-width="6" fill="none"/>
                    </g>
                </svg>
                """);
        codeSVGs.put("facebook",
                "<?xml version=\"1.0\" ?><!DOCTYPE svg  PUBLIC '-//W3C//DTD SVG 1.1//EN'  " +
                        "'http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd'><svg height=\"100%\" " +
                        "style=\"fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2;\" version=\"1.1\" " +
                        "viewBox=\"0 0 512 512\" width=\"100%\" xml:space=\"preserve\" " +
                        "xmlns=\"http://www.w3.org/2000/svg\" xmlns:serif=\"http://www.serif.com/\" " +
                        "xmlns:xlink=\"http://www.w3.org/1999/xlink\"><g><path d=\"M512,256c0,-141.385 -114.615," +
                        "-256 -256,-256c-141.385,0 -256,114.615 -256,256c0,127.777 93.616,233.685 216,252.89l0," +
                        "-178.89l-65,0l0,-74l65,0l0,-56.4c0,-64.16 38.219,-99.6 96.695,-99.6c28.009,0 57.305,5 57.305," +
                        "5l0,63l-32.281,0c-31.801,0 -41.719,19.733 -41.719,39.978l0,48.022l71,0l-11.35,74l-59.65,0l0," +
                        "178.89c122.385,-19.205 216,-125.113 216,-252.89Z\" style=\"fill:#1877f2;fill-rule:nonzero;\"/>" +
                        "<path d=\"M355.65,330l11.35,-74l-71,0l0,-48.022c0,-20.245 9.917,-39.978 41.719,-39.978l32.281," +
                        "0l0,-63c0,0 -29.297,-5 -57.305,-5c-58.476,0 -96.695,35.44 -96.695,99.6l0,56.4l-65,0l0,74l65," +
                        "0l0,178.89c13.033,2.045 26.392,3.11 40,3.11c13.608,0 26.966,-1.065 40,-3.11l0,-178.89l59.65,0Z\" " +
                        "style=\"fill:#fff;fill-rule:nonzero;\"/></g></svg>");
        codeSVGs.put("github",
                "<?xml version=\"1.0\" ?><!DOCTYPE svg  PUBLIC '-//W3C//DTD SVG 1.1//EN'  " +
                        "'http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd'><svg enable-background=\"new 0 0 512 512\" " +
                        "height=\"512px\" id=\"Layer_1\" version=\"1.1\" viewBox=\"0 0 512 512\" width=\"512px\" " +
                        "xml:space=\"preserve\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" +
                        "<g><path clip-rule=\"evenodd\" d=\"M296.133,354.174c49.885-5.891,102.942-24.029,102.942-110.192   " +
                        "c0-24.49-8.624-44.448-22.67-59.869c2.266-5.89,9.515-28.114-2.734-58.947c0,0-18.139-5.898-60.759,22.669   " +
                        "c-18.139-4.983-38.09-8.163-56.682-8.163c-19.053,0-39.011,3.18-56.697,8.163c-43.082-28.567-61.22-22.669-61.22-22.669   " +
                        "c-12.241,30.833-4.983,53.057-2.718,58.947c-14.061,15.42-22.677,35.379-22.677,59.869c0,86.163,53.057,104.301,102.942,110.192   " +
                        "c-6.344,5.452-12.241,15.873-14.507,30.387c-12.702,5.438-45.808,15.873-65.758-18.592c0,0-11.795-21.31-34.012-22.669   " +
                        "c0,0-22.224-0.453-1.813,13.592c0,0,14.96,6.812,24.943,32.653c0,0,13.6,43.089,76.179,29.48v38.543   " +
                        "c0,5.906-4.53,12.702-15.865,10.89C96.139,438.977,32.2,354.626,32.2,255.77c0-123.807,100.216-224.022,224.03-224.022   " +
                        "c123.347,0,224.023,100.216,223.57,224.022c0,98.856-63.946,182.754-152.828,212.688c-11.342,2.266-15.873-4.53-15.873-10.89   " +
                        "V395.45C311.1,374.577,304.288,360.985,296.133,354.174L296.133,354.174z M512,256.23C512,114.73,397.263,0,256.23,0   " +
                        "C114.73,0,0,114.73,0,256.23C0,397.263,114.73,512,256.23,512C397.263,512,512,397.263,512,256.23L512,256.23z\" " +
                        "fill=\"#0D2636\" fill-rule=\"evenodd\"/></g></svg>");
    }
    private final Animation[] animation = { Animation.SHAKE, Animation.FLASH };
    private int animationIndex = 0;

    @Id
    private Div layout;

    private LoginNameField loginField;

    @Id("user")
    private TextField userField;

    @Id("password")
    private PasswordField passwordField;

    @Id("authCode")
    private IntegerField authField;

    @Id("cram")
    private CRAMField cramField;

    @Id("biometric")
    private BiometricButton biometricButton;

    @Id
    private Button ok;

    @Id
    private Button cancel;

    @Id
    private Checkbox remember;

    @Id
    private Button forgot;

    @Id
    private AnchorButton forgotLink;

    @Id
    private Checkbox terms;

    @Id
    private Span year;

    @Id
    private SVGImage google;

    @Id
    private SVGImage facebook;

    @Id
    private SVGImage github;

    private SystemUser user;
    private boolean init;
    private final ExecutableView internal;
    private Application application;

    public LoginForm() {
        setCaption("Sign in");
        internal = this;
        init();
    }

    public LoginForm(String textHTMLContentName) {
        super(null, textHTMLContentName);
        setCaption("Sign in");
        internal = this;
        init();
    }

    LoginForm(@SuppressWarnings("unused") boolean dummy) {
        super("-", () -> "<div></div>");
        this.internal = new LF();
    }

    private void init() {
        application = Application.get();
    }

    @Override
    public void viewConstructed(View view) {
        super.viewConstructed(view);
        loginField = null;
        if(year != null) {
            year.setText(String.valueOf(DateUtility.getYear(DateUtility.today())));
        }
    }

    @Override
    public void abort() {
        if(internal == this) {
            super.abort();
        } else {
            internal.abort();
        }
        application.close();
    }

    @Override
    public void close() {
        if(internal == this) {
            super.close();
        } else {
            internal.close();
        }
    }

    private void setUser() {
        String u = userField != null ? userField.getValue() : (loginField == null ? null : loginField.getValue());
        if(u == null) {
            return;
        }
        u = u.trim().toLowerCase();
        if(u.indexOf(':') > 0) {
            u = u.substring(0, u.indexOf(':'));
        }
        if(user != null && user.getLogin().equalsIgnoreCase(u)) {
            return;
        }
        if(StringUtility.isDigit(u)) {
            user = StoredObject.get(SystemUser.class, "Id=" + u);
        } else {
            user = StoredObject.get(SystemUser.class, "lower(Login)='" + u + "'");
        }
        if(biometricButton != null) {
            biometricButton.setUser(user);
        }
    }

    private Application getA() {
        return getApplication();
    }

    private void shakePW() {
        if(passwordField != null) {
            animation[animationIndex % animation.length].animate(passwordField);
            animationIndex++;
        }
    }

    private void process(boolean forgot) {
        if(terms != null && !terms.getValue()) {
            String m = terms.getElement().getAttribute("message");
            if(m == null || m.isBlank()) {
                m = "Please accept the terms & conditions";
            }
            speak(m, true);
            ok.setEnabled(true);
            return;
        }
        internal.clearAlerts();
        Login login = getA().getLogin();
        setBioStatus();
        String u;
        if(user == null) {
            if(userField != null) {
                u = userField.getValue().trim().toLowerCase();
            } else if(loginField != null) {
                u = loginField.getValue().trim().toLowerCase();
            } else {
                u = "";
            }
        } else {
            u = user.getLogin();
        }
        if(u.isBlank()) {
            speak("Please enter a valid username", false);
            ok.setEnabled(true);
            return;
        }
        try {
            boolean wrongCram = cramField != null && !cramField.verified();
            if(wrongCram || user == null || (forgot ? !login.forgotPassword(u) :
                    !login.login(u, passwordField == null ? new char[] {} :
                            passwordField.getValue().toCharArray(), authCode(),true))) {
                if(login.canRetry()) {
                    speak("Please check the " + (wrongCram ? "captcha" : "password"), false);
                    if(wrongCram) {
                        cramField.shake();
                    } else {
                        shakePW();
                    }
                    ok.setEnabled(true);
                    return;
                }
                if(login.isBlocked()) {
                    internal.abort();
                    speak("System not available now. Please try later.", true);
                    return;
                }
                internal.abort();
                return;
            }
            if(loginField != null) {
                loginField.save();
            }
        } catch(Throwable error) {
            if(!login.canRetry()) {
                abort();
            }
            internal.error(error);
            if(login.canRetry()) {
                ok.setEnabled(true);
                return;
            }
            abort();
        }
    }

    private void setBioStatus() {
        if(biometricButton != null) {
            getA().biometricRegistered = biometricButton.isAvailable() && biometricButton.isRegistered();
            getA().biometricAvailable = biometricButton.isAvailable();
            WebBiometric biometric = biometricButton.getBiometric();
            getA().biometricDeviceId = biometric == null ? null : biometric.getId();
        }
    }

    private void biometricLogin(BiometricButton biometricButton) {
        if(biometricButton == this.biometricButton) {
            setBioStatus();
            if(loginField != null) {
                loginField.save();
            }
        } else {
            abort();
        }
    }

    private void speak(String message, boolean warn) {
        internal.speak(message);
        if(warn) {
            internal.warning(message);
        }
    }

    @Override
    public void execute() {
        if(!init) {
            build();
            init = true;
            if(remember != null) {
                remember.setTabIndex(-1);
            }
            if(userField != null) {
                userField.addValueChangeListener(e -> setUser());
            } else if(loginField != null) {
                loginField.addValueChangeListener(e -> setUser());
            }
        }
        super.execute();
        if(userField != null) {
            userField.focus();
        } else if(loginField != null) {
            loginField.focus();
        }
    }

    @Override
    public void decorateComponent() {
        getComponent().getElement()
                .setAttribute("style", "padding:0px;width:100vw;height:100vh;box-sizing:border-box");
    }

    @Override
    protected Component createComponentForId(String id) {
        if(id.startsWith("view")) {
            return new Anchor();
        }
        if(loginField == null) {
            loginField = new LoginNameField();
        }
        if(remember == null) {
            remember = new Checkbox();
            remember.setTabIndex(-1);
        }
        if(passwordField == null) {
            passwordField = new PasswordField();
        }
        if("remember".equals(id)) {
            loginField.setRemember(remember);
        }
        if("password".equals(id)) {
            loginField.setPasswordField(passwordField);
        }
        return switch(id) {
            case "user" -> new TextField();
            case "login" -> loginField;
            case "remember" -> remember;
            case "password" -> passwordField;
            case "authCode" -> new IntegerField();
            case "cram" -> new CRAMField();
            case "biometric" -> new BiometricButton(this::biometricLogin, getA().getLogin());
            case "ok" -> createOK();
            case "cancel" -> new Button(null, (String) null, e -> getA().close(5));
            case "forgot" -> new Button(null, (String) null, e -> process(true));
            case "forgotLink" -> new AnchorButton("", e -> process(true));
            case "google", "facebook", "github" -> {
                SVGImage image = new SVGImage(codeSVGs.get(id));
                image.getElement().setAttribute("title", "Use " + id + " account to sign in");
                image.getStyle().set("cursor", "pointer").set("margin-left", "10px");
                image.setWidth("48px");
                image.setHeight("48px");
                new Clickable<>(image, e -> oauthLogin(id));
                yield image;
            }
            default -> super.createComponentForId(id);
        };
    }

    private Button createOK() {
        Button ok = new Button(null, (String) null, e -> process(false));
        ok.setDisableOnClick(true);
        return ok;
    }

    private void oauthLogin(String id) {
        try {
            OAuth oa = SOServlet.getOAuth();
            if(oa != null) {
                String redirect = oa.getURL(id);
                if(redirect != null) {
                    Application.get().close(redirect, 101);
                } else {
                    warning("Unable to contact " + id);
                }
            }
        } catch (Exception e) {
            warning(e);
        }
    }

    private int authCode() {
        if(authField == null) {
            return -1;
        }
        int ac = authField.getValue();
        return ac <= 0 ? -1 : ac;
    }

    static Runnable create() {
        return new LoginForm(false).internal;
    }

    private class LF extends DataForm implements HomeView {

        private Div imageHolder = new Div();
        private Registration registration;
        private int closeReason = 4;

        public LF() {
            super("Sign in", "Sign in", "Cancel");
            application = Application.get();
            application.log("Accessed");
            registration = application.addBrowserResizedListener((w, h) -> resized());
        }

        @Override
        protected void formConstructed() {
            super.formConstructed();
            userField = null;
            terms = null;
        }

        @Override
        public String getMenuIconName() {
            return "sign-in";
        }

        @Override
        protected void buildFields() {
            add(imageHolder);
            Checkbox remember = (Checkbox) createComponentForId("remember");
            remember.setTabIndex(-1);
            addField(loginField = (LoginNameField) createComponentForId("login"));
            loginField.setId("login");
            loginField.setLabel("Username");
            loginField.setRemember(remember);
            remember.setLabel("Remember my username");
            setRequired(loginField, "username can not be empty");
            addField(passwordField = (PasswordField) createComponentForId("password"));
            passwordField.setLabel("Password");
            addField(cramField = (CRAMField) createComponentForId("cram"));
            add(remember);
            loginField.addValueChangeListener(e -> setUser());
            if(terms != null) {
                terms.setLabel("Terms & Conditions");
            }
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            buttonPanel.add(biometricButton = (BiometricButton) createComponentForId("biometric"));
            for(String oauthProvider: SOServlet.getOAuthProviders()) {
                buttonPanel.add(social(oauthProvider));
            }
            ok.setText("Sign-in");
            ok.setIcon("ok");
            ok.setDisableOnClick(true);
            cancel.setText("Cancel");
            cancel.setIcon("cancel");
            forgot = (Button) createComponentForId("forgot");
            forgot.setText("Forgot Password");
            forgot.setIcon(VaadinIcon.QUESTION_CIRCLE_O);
            LoginForm.this.ok = ok;
            LoginForm.this.cancel = cancel;
        }

        private boolean checkKey(String name, String key) {
            return SOServlet.getOAuth() != null &&
                    ApplicationServer.getGlobalProperty("oauth." + name + "." + key) != null;
        }

        private Component social(String name) {
            return checkKey(name, "clientId") && checkKey(name, "clientSecret") ? createComponentForId(name)
                    : null;
        }

        @Override
        public int getMaximumContentWidth() {
            return 35;
        }

        private void resized() {
            if(imageHolder == null) {
                return;
            }
            Component c = null;
            application.getServer().doDeviceLayout();
            String background = application.getDeviceLayout().getLoginImageName();
            if(!background.isEmpty()) {
                TextContent tc = SOServlet.getTextContent(background);
                if(tc != null) {
                    c = new IFrame(tc);
                } else {
                    MediaFile mf = SOServlet.getImage(application.getDeviceLayout().getLoginImageName());
                    if(mf != null) {
                        c = new Image(mf);
                    }
                }
            }
            if(c != null) {
                sizeAndAdd(c);
            } else {
                remove(imageHolder);
            }
            imageHolder = null;
        }

        private void sizeAndAdd(Component c) {
            imageHolder.setWidthFull();
            imageHolder.setMaxHeight("30vh");
            c.getElement().getStyle().set("width", "100%").set("max-height", "30vh").set("object-fit", "fill");
            imageHolder.add(c);
        }

        @Override
        protected Window createWindow(Component component) {
            return new Window(new WindowDecorator(this, Application.speaker()), component);
        }

        @Override
        public void clean() {
            super.clean();
            if(registration != null) {
                registration.remove();
                registration = null;
            }
        }

        @Override
        protected void cancel() {
            closeReason = 5;
            super.cancel();
        }

        @Override
        public void abort() {
            super.abort();
            ((Application)getApplication()).close(closeReason);
        }

        @Override
        protected boolean process() {
            LoginForm.this.process(false);
            return false;
        }
    }

    private class Anchor extends AnchorButton {

        Anchor() {
            super("");
            addClickHandler(e -> view());
            setTabIndex(-1);
        }

        private void view() {
            String fileName = getElement().getAttribute("file");
            if(fileName == null || fileName.isBlank()) {
                warning("Nothing to view!");
                return;
            }
            MediaFile file = SOServlet.getMedia(fileName);
            if(file == null) {
                warning("File not found: " + fileName);
            } else {
                String caption = getElement().getAttribute("caption");
                if(caption == null || caption.isBlank()) {
                    caption = file.getName();
                }
                application.view(caption, file, true);
            }
        }
    }
}
