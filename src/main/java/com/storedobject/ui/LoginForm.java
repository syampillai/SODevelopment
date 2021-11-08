package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.tools.BiometricButton;
import com.storedobject.ui.tools.LoginNameField;
import com.storedobject.ui.util.SOServlet;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.shared.Registration;

/**
 * This is a template-based login screen and its template can be defined in the {@link TextContent} named
 * "com.storedobject.ui.LoginForm". If this template exists, this will be used for rendering the login view
 * instead of the default one. Following id-values are used to map the necessary fields in the template:
 * <p>id = "layout" (Should be a div tag. Represents the outermost layout in which the whole login form is wrapped.
 * This is optional)</p>
 * <p>id = "login" (Should be a vaadin-combo-box tag. Represents user-name field)</p>
 * <p>id = "remember" (Should be a vaadin-checkbox tag. Used to remember the user-name. This is optional)</p>
 * <p>id = "password" (Should be a vaadin-password-field tag)</p>
 * <p>id = "cram" (Should be a vaadin-custom-field tag. This is optional)</p>
 * <p>id = "biometric" (Should be a so-auth tag. Used for showing biometric option. This is optional)</p>
 * <p>id = "ok" ('OK' or 'Sign in' button. This should be a vaadin-button tag)</p>
 * <p>id = "cancel" ('Cancel' button. This should be a vaadin-button tag)</p>
 * <p>id = "forgot" ('Forgot Password' button. This should be a vaadin-button tag)</p>
 *
 * @author Syam
 */
public class LoginForm extends TemplateView implements HomeView {

    private final Animation[] animation = { Animation.SHAKE, Animation.FLASH };
    private int animationIndex = 0;
    @Id
    private Component layout;

    @Id("login")
    private LoginNameField loginField;

    @Id("password")
    private PasswordField passwordField;

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

    private SystemUser user;
    private boolean init;
    private final ExecutableView internal;

    public LoginForm() {
        setCaption("Sign in");
        internal = this;
        Application.get().log("Accessed");
    }

    LoginForm(boolean dummy) {
        super("-", () -> "<div></div>");
        this.internal = new LF();
    }

    @Override
    public void abort() {
        if(internal == this) {
            super.abort();
        } else {
            internal.abort();
        }
        getApplication().close();
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
        String u = loginField == null ? null : loginField.getValue();
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
        internal.clearAlerts();
        Login login = getA().getLogin();
        setBioStatus();
        String u = user == null ?
                (loginField == null ? "" : loginField.getValue().trim().toLowerCase()) :
                user.getLogin();
        if(u.isBlank()) {
            speak("Please enter a valid user name", false);
            return;
        }
        try {
            boolean wrongCram = cramField != null && !cramField.verified();
            if(wrongCram || user == null || (forgot ? !login.forgotPassword(u) :
                    !login.login(u, passwordField == null ? new char[] {} :
                            passwordField.getValue().toCharArray(), true))) {
                if(login.canRetry()) {
                    speak("Please check the " + (wrongCram ? "captcha" : "password"), false);
                    if(wrongCram) {
                        cramField.shake();
                    } else {
                        shakePW();
                    }
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
            init = true;
            if(remember != null) {
                remember.setTabIndex(-1);
            }
            if(loginField != null) {
                loginField.addValueChangeListener(e -> setUser());
            }
        }
        super.execute();
    }

    @Override
    protected Component createComponentForId(String id) {
        if(loginField == null) {
            loginField = new LoginNameField();
        }
        if(remember == null) {
            remember = new Checkbox("Remember my login");
        }
        if(passwordField == null) {
            passwordField = new PasswordField("Password");
        }
        if("remember".equals(id)) {
            loginField.setRemember(remember);
        }
        if("password".equals(id)) {
            loginField.setPasswordField(passwordField);
        }
        return switch(id) {
            case "layout" -> new Div();
            case "login" -> loginField;
            case "remember" -> remember;
            case "password" -> passwordField;
            case "cram" -> new CRAMField();
            case "biometric" -> new BiometricButton(this::biometricLogin, getA().getLogin());
            case "ok" -> new Button("Sign-in", "ok", e -> process(false));
            case "cancel" -> new Button("Cancel", e -> getA().close());
            case "forgot" -> new Button("Forgot Password", VaadinIcon.QUESTION_CIRCLE_O, e -> process(true));
            default -> super.createComponentForId(id);
        };
    }

    static Runnable create() {
        return new LoginForm(false).internal;
    }

    private class LF extends DataForm implements HomeView {

        private Div imageHolder = new Div();
        private Registration registration;

        public LF() {
            super("Sign in", "Sign in", "Cancel");
            Application a = Application.get();
            a.log("Accessed");
            registration = a.addBrowserResizedListener((w, h) -> resized());
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
            loginField.setRemember(remember);
            setRequired(loginField, "Login can not be empty");
            addField(passwordField = (PasswordField) createComponentForId("password"));
            addField(cramField = (CRAMField) createComponentForId("cram"));
            add(remember);
            loginField.addValueChangeListener(e -> setUser());
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            buttonPanel.add(biometricButton = (BiometricButton) createComponentForId("biometric"));
            //buttonPanel.add(forgot = (Button) createComponentForId("forgot"));
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
            Application a = Application.get();
            a.getServer().doDeviceLayout();
            String background = a.getDeviceLayout().getLoginImageName();
            if(!background.isEmpty()) {
                TextContent tc = SOServlet.getTextContent(background);
                if(tc != null) {
                    c = new IFrame(tc);
                } else {
                    MediaFile mf = SOServlet.getImage(a.getDeviceLayout().getLoginImageName());
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
            c.getElement().getStyle().set("width", "100%").set("max-height", "30vh").set("object-fir", "fill");
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
}
