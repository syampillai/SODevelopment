package com.storedobject.ui;

import com.storedobject.core.*;
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
            year.setText("" + DateUtility.getYear(DateUtility.today()));
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
            speak("Please enter a valid user name", false);
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
            case "cancel" -> new Button(null, (String) null, e -> getA().close());
            case "forgot" -> new Button(null, (String) null, e -> process(true));
            case "forgotLink" -> new AnchorButton("", e -> process(true));
            default -> super.createComponentForId(id);
        };
    }

    private Button createOK() {
        Button ok = new Button(null, (String) null, e -> process(false));
        ok.setDisableOnClick(true);
        return ok;
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
        public void abort() {
            super.abort();
            getApplication().close();
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
