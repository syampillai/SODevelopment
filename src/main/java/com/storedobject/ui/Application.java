package com.storedobject.ui;

import com.storedobject.common.ArrayListSet;
import com.storedobject.common.Executable;
import com.storedobject.common.FilterProvider;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.sms.QuickSender;
import com.storedobject.ui.common.MemoSystem;
import com.storedobject.ui.util.ApplicationFrame;
import com.storedobject.ui.util.*;
import com.storedobject.vaadin.ApplicationMenu;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.*;
import com.vaadin.flow.theme.lumo.Lumo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Application extends com.storedobject.vaadin.Application implements Device, RunningLogic, RequiresApproval {

    public static final String COLOR_ERROR = "var(--so-error-color)", COLOR_SUCCESS = "var(--so-success-color)",
            COLOR_INFO = "var(--so-info-color)", COLOR_NORMAL = "var(--so-normal-color)";
    private static final String COMPACT_STYLES =
            """
                    --lumo-size-xl: 3rem;
                    --lumo-size-l: 2.5rem;
                    --lumo-size-m: 2rem;
                    --lumo-size-s: 1.75rem;
                    --lumo-size-xs: 1.5rem;
                    --lumo-font-size: 1rem;
                    --lumo-font-size-xxxl: 1.75rem;
                    --lumo-font-size-xxl: 1.375rem;
                    --lumo-font-size-xl: 1.125rem;
                    --lumo-font-size-l: 1rem;
                    --lumo-font-size-m: 0.875rem;
                    --lumo-font-size-s: 0.8125rem;
                    --lumo-font-size-xs: 0.75rem;
                    --lumo-font-size-xxs: 0.6875rem;
                    --lumo-line-height-m: 1.4;
                    --lumo-line-height-s: 1.2;
                    --lumo-line-height-xs: 1.1;
                    --lumo-space-xl: 1.875rem;
                    --lumo-space-l: 1.25rem;
                    --lumo-space-m: 0.625rem;
                    --lumo-space-s: 0.3125rem;
                    --lumo-space-xs: 0.1875rem;""";
    private static final String DELETE_COMPACT_STYLES = """
            --lumo-size-xl
            --lumo-size-l
            --lumo-size-m
            --lumo-size-s
            --lumo-size-xs
            --lumo-font-size
            --lumo-font-size-xxxl
            --lumo-font-size-xxl
            --lumo-font-size-xl
            --lumo-font-size-l
            --lumo-font-size-m
            --lumo-font-size-s
            --lumo-font-size-xs
            --lumo-font-size-xxs
            --lumo-line-height-m
            --lumo-line-height-s
            --lumo-line-height-xs
            --lumo-space-xl
            --lumo-space-l
            --lumo-space-m
            --lumo-space-s
            --lumo-space-xs
            """;
    private ApplicationServer server;
    private BrowserDeviceLayout layout = null;
    private Logic runningLogic;
    final ApplicationLayout mainLayout;
    private boolean singleLogicMode = false, abortOnLogicSwitch = false;
    private final Hashtable<Long, AbstractContentGenerator> dynamicContent = new Hashtable<>();
    private final Hashtable<Long, WeakReference<AbstractContentGenerator>> multiContent = new Hashtable<>();
    private final Set<Logic> closeMe = new HashSet<>();
    private ObjectViewer objectViewer;
    boolean biometricAvailable = false, biometricRegistered = false;
    Id biometricDeviceId = null;
    private boolean alertsVisible = false;
    private final AlertButton alertButton = new AlertButton(e -> alertClicked());
    private Login login;
    private boolean compactTheme = false;
    private Runnable loginForm;
    private IdentityCheck identityCheck;
    private final Notification waitMessage;
    private MemoSystem memoSystem;

    public Application() {
        this(new ApplicationFrame());
    }

    public Application(ApplicationLayout applicationLayout) {
        this(applicationLayout,
                ApplicationServer.getGlobalBooleanProperty("application.logic.single"),
                ApplicationServer.getGlobalBooleanProperty("application.logic.abortWhenSwitched")
        );
    }

    public Application(ApplicationLayout applicationLayout, boolean singleLogicMode) {
        this(applicationLayout, singleLogicMode, singleLogicMode);
    }

    public Application(ApplicationLayout applicationLayout, boolean singleLogicMode, boolean abortOnLogicSwitch) {
        waitMessage = new Notification("Please wait...");
        waitMessage.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        waitMessage.setDuration(Integer.MAX_VALUE);
        waitMessage.setPosition(Notification.Position.MIDDLE);
        this.mainLayout = applicationLayout;
        if(ApplicationServer.getGlobalBooleanProperty("application.allow.deprecated", false)) {
            HTMLText.setAllowTopLevelHTML();
        }
        String alertPos = ApplicationServer.getGlobalProperty("application.message.position", "");
        if(!alertPos.isBlank()) {
            alertPos = alertPos.trim().replace(' ', '_').toUpperCase().replace('-', '_');
            while(alertPos.contains("__")) {
                alertPos = alertPos.replace("__", "_");
            }
            try {
                Alert.setDefaultPosition(Notification.Position.valueOf(alertPos));
            } catch(Throwable ignored) {
            }
        }
        Button.setNoIcons(!ApplicationServer.getGlobalBooleanProperty("application.button.icon", true));
        setSingleLogicMode(singleLogicMode);
        setAbortOnLogicSwitch(abortOnLogicSwitch);
        login = new Login(this, getMessageViewer());
    }

    @Override
    protected void init(VaadinRequest request) {
        super.init(request);
        VaadinSession vs = VaadinSession.getCurrent();
        vs.addRequestHandler(new DownloadHandler());
        vs.setErrorHandler(e -> {
            Throwable error = e.getThrowable();
            Throwable cause = error;
            while(cause != null) {
                if(cause instanceof IOException || cause instanceof UploadException) {
                    break;
                }
                cause = cause.getCause();
            }
            if(cause != null) {
                return; // Ignore
            }
            log(error);
            UI ui = getUI();
            if(ui != null) {
                ui.access(() -> error("An error has occurred, please contact Technical Support!"));
            }
        });
    }

    @Override
    protected boolean init(String link) {
        new ApplicationServer(this, link);
        if(server == null) {
            showNotification("System not available, please try later.");
            server = null;
            return false;
        }
        return true;
    }

    @Override
    protected Consumer<UI> getUIConfigurator() {
        return ui -> ui.getLoadingIndicatorConfiguration().setApplyDefaultTheme(false);
    }

    /**
     * This return the {@link Login} instance associated with application. It will be available only till
     * logged in.
     *
     * @return Login instance when not logged in, otherwise null.
     */
    public Login getLogin() {
        return login;
    }

    /**
     * Set the application theme in compact mode.
     *
     * @param compactMode Whether to show compact mode or not.
     */
    public void setCompactTheme(boolean compactMode) {
        if(compactMode == compactTheme) {
            return;
        }
        this.compactTheme = compactMode;
        if(compactTheme) {
            loadStylesInt(COMPACT_STYLES);
        } else {
            compactThemeOff();
        }
    }

    /**
     * Is the application theme currently in compact mode?
     *
     * @return True/false.
     */
    public boolean isCompactTheme() {
        return compactTheme;
    }

    /**
     * Whether this application supports {@link CloseableView} or not. Applications may override the standard
     * menu system and the overridden menu system may not have support for displaying appropriate "close" icon for
     * closing the view. In such cases, this method should return <code>false</code>.
     *
     * @return True/false. Default is true.
     */
    public boolean supportsCloseableView() {
        return mainLayout == null || mainLayout.isMenuVisible();
    }

    @Override
    public void attached() {
        super.attached();
        setCompactTheme(ApplicationServer.getGlobalBooleanProperty("application.theme.compact", true));
        getUI().getElement().getStyle().set("--so-error-color", "red").set("--so-success-color", "blue")
                .set("--so-info-color", "green").set("--so-normal-color", "black");
        loadStyles(SOServlet.getTextContent("styles.css"));
    }

    public void loadStyles(String textContentName) {
        TextContent tc = SOServlet.getTextContent(textContentName);
        if(tc == null) {
            warning("Style file not found: " + textContentName);
        }
        loadStyles(tc);
    }

    private void loadStyles(TextContent tc) {
        if(tc == null) {
            return;
        }
        loadStylesInt(tc.getContent());
    }

    private void loadStylesInt(String styles) {
        UI ui = getUI();
        Style s = ui.getElement().getStyle();
        try(BufferedReader r = new BufferedReader(new StringReader(styles))) {
            r.lines().forEach(line -> {
                line = line.trim();
                if(line.endsWith(";")) {
                    line = line.substring(0, line.length() - 1);
                }
                int p = line.indexOf(':');
                if(p > 0) {
                    s.set(line.substring(0, p).trim(), MediaCSS.parse(line.substring(p + 1).trim()));
                }
            });
        } catch (IOException ignored) {
        } catch(Throwable error) {
            warning("Errors while setting custom styles!");
            error(error);
        }
    }

    private void compactThemeOff() {
        UI ui = getUI();
        Style s = ui.getElement().getStyle();
        try(BufferedReader r = new BufferedReader(new StringReader(Application.DELETE_COMPACT_STYLES))) {
            r.lines().forEach(line -> {
                line = line.trim();
                if(line.endsWith(";")) {
                    line = line.substring(0, line.length() - 1);
                }
                int p = line.indexOf(':');
                if(p >= 0) {
                    line = line.substring(0, p);
                }
                line = line.trim();
                if(!line.isEmpty()) {
                    s.remove(line);
                }
            });
        } catch (IOException ignored) {
        } catch(Throwable error) {
            warning("Errors while removing custom styles!");
            error(error);
        }
    }

    @Override
    protected final ApplicationLayout createLayout() {
        return mainLayout;
    }

    public final String getName() {
        return mainLayout + "";
    }

    @Override
    protected ApplicationEnvironment createEnvironment() {
        return new SOEnvironment();
    }

    public void setSingleLogicMode(boolean singleLogicMode) {
        this.singleLogicMode = singleLogicMode;
    }

    public void setAbortOnLogicSwitch(boolean abortOnLogicSwitch) {
        this.abortOnLogicSwitch = abortOnLogicSwitch;
    }

    public void execute(Logic logic) {
        if(singleLogicMode || abortOnLogicSwitch) {
            if(abortOnLogicSwitch) {
                execute(() -> executeMe(logic), true);
            } else {
                if(getActiveViews()
                        .allMatch(v -> v instanceof InformationView || v.getComponent() instanceof PDFViewer)) {
                    executeMe(logic);
                }
                closeMenu();
            }
        } else {
            executeMe(logic);
        }
    }

    private void executeMe(Logic logic) {
        if(MemoSystem.class.getName().equals(logic.getClassName())) {
            if(memoSystem == null) {
                memoSystem = new MemoSystem(false);
            }
            memoSystem.setCaption(logic.getTitle());
            memoSystem.executeAndLoad();
            return;
        }
        server.execute(logic);
        if(logic.getExecutable() != null) {
            synchronized(closeMe) {
                closeMe.add(logic);
            }
        }
    }

    public void logout() {
        TransactionManager tm = getTransactionManager();
        if(tm == null || (tm.getUser().getPreferences() & 2) != 2) {
            close();
            return;
        }
        new ActionForm("Do you really want to exit?", this::close).execute();
    }

    @Override
    public void detached() {
        if(ApplicationServer.getGlobalBooleanProperty("application.log.close", false)) {
            log(SORuntimeException.getTrace(new Exception("Application Detached")));
        }
        super.detached();
    }

    @Override
    public void close() {
        if(server == null) {
            return;
        }
        String exit = ApplicationServer.getGlobalProperty("application.exit.site", null, true);
        if(exit == null) {
            exit = layout == null ? "" : layout.getExitSite().trim();
        }
        close(exit);
    }

    public void close(final String exitSite) {
        if(server == null) {
            return;
        }
        contentProducers.clear();
        dynamicContent.clear();
        multiContent.clear();
        closeAllViews(true);
        ApplicationServer as = server;
        server = null;
        if(as == null) {
            return;
        }
        as.close();
        try {
            mainLayout.getComponent().getElement().removeFromParent();
        } catch(Throwable ignore) {
        }
        UI ui = getUI();
        try {
            super.close();
        } catch(Throwable ignored) {
        }
        if(ui == null) {
            return;
        }
        StringBuilder script = new StringBuilder("navigator.credentials.preventSilentAccess();");
        if(exitSite == null || exitSite.isEmpty()) {
            script.append("window.close();");
        }
        if(isSpeakerOn()) {
            script.append("window.speechSynthesis.speak(new SpeechSynthesisUtterance('Goodbye'));");
        }
        script.append("this.stopApplication();");
        if(exitSite != null && !exitSite.isEmpty()) {
            script.append("window.open('").append(exitSite).append("', '_self');");
        }
        ui.getPage().executeJs(script.toString());
    }

    /**
     * Exit the application after showing a message.
     *
     * @param message Message to show.
     * @param exitSite Exit to this site (If null is passed, it will exit to the configured site).
     */
    public void exit(String message, String exitSite) {
        InformationMessage m = new InformationMessage(message);
        if(exitSite == null) {
            m.setAction(this::close);
        } else {
            m.setAction(() -> close(exitSite));
        }
        access(m::execute);
    }

    /**
     * Close all current views by invoking {@link View#abort()} on each of them.
     *
     * @param forShutdown True should be passed if this is for a shutdown so that all internal timers will be
     *                    removed.
     */
    public void closeAllViews(boolean forShutdown) {
        if(forShutdown) {
            closeTimer();
        }
        List<Dialog> dialogs = new ArrayList<>();
        AtomicBoolean done = new AtomicBoolean(false);
        int round = 0;
        while(!done.get()) {
            done.set(true);
            getActiveViews().toList().forEach(v -> {
                try {
                    Component c = v.getComponent();
                    v.abort();
                    if(c instanceof Dialog d) {
                        dialogs.add(d);
                    }
                } catch(Exception ignored) {
                    done.set(false);
                }
            });
            if(++round > 10) {
                break;
            }
        }
        dialogs.forEach(Dialog::close);
    }

    public static Application get() {
        return (Application) com.storedobject.vaadin.Application.get();
    }

    public static Currency getDefaultCurrency() {
        Application a = get();
        if(a != null) {
            return a.getTransactionManager().getCurrency();
        }
        throw new SORuntimeException("Unable to determine default currency");
    }

    public static String getDefaultCountry() {
        Application a = get();
        if(a != null) {
            return a.getTransactionManager().getCountry();
        }
        throw new SORuntimeException("Unable to determine default country");
    }

    public boolean isBiometricAvailable() {
        return biometricAvailable;
    }

    public boolean isBiometricRegistered() {
        return biometricRegistered;
    }

    public Id getBiometricDeviceId() {
        return biometricDeviceId;
    }

    /**
     * For internal use only.
     */
    public void disableBiometric() {
        biometricRegistered = true;
    }

    public TransactionManager getTransactionManager() {
        return server == null ? null : server.getTransactionManager();
    }

    @Override
    public void log(Object anything) {
        Device.super.log(anything);
    }

    @Override
    public void log(Object anything, Throwable error) {
        Device.super.log(anything, error);
    }

    @Override
    public void setRunningLogic(Logic logic) {
        runningLogic = logic;
    }

    @Override
    public Logic getRunningLogic() {
        return runningLogic;
    }

    /**
     * Get the title of the currently running logic. Note: A call to this method will reset
     * it and you can not call it again to get it!
     *
     * @param defaultTitle Default title to be returned if there is no current logic.
     * @return Title of the logic or default title.
     */
    public String getLogicTitle(String defaultTitle) {
        Logic r = runningLogic;
        runningLogic = null;
        return r == null ? defaultTitle : r.getTitle();
    }

    /**
     * Get the caption of the currently running logic from the current {@link Application}.
     * Note: A call to this method will reset
     * it and you can not call it again to get it!
     *
     * @param defaultCaption Default caption to be returned if there is no current logic or application instance in the
     *                       current context..
     * @return Title of the logic or default caption.
     */
    public static String getLogicCaption(String defaultCaption) {
        Application a = get();
        return a == null ? defaultCaption : a.getLogicTitle(defaultCaption);
    }

    @Override
    public void setServer(ApplicationServer server) {
        if(this.server == null) {
            this.server = server;
        }
    }

    @Override
    public ApplicationServer getServer() {
        return server;
    }

    @Override
    public void setDate(Date date) {
        TransactionManager tm = getTransactionManager();
        DateField.setToday(tm == null ? date : tm.date(date));
    }

    @Override
    public void parse(Logic logic) throws SOException {
        LogicParser.parse(logic);
    }

    public String getDateFormat() {
        return server.getDateFormat();
    }

    @Override
    public final MessageViewer getMessageViewer() {
        return messageViewer;
    }

    @Override
    public String getDeviceType() {
        return "browser";
    }

    public final String getDisplayVersion() {
        return ApplicationServer.getGlobalProperty("application.version", getDriverIdentifier(), true);
    }

    @Override
    public String getDriverIdentifier() {
        return com.vaadin.flow.server.Version.getFullVersion();
    }

    @Override
    public BrowserDeviceLayout getDeviceLayout() {
        if(layout == null) {
            layout = new BrowserDeviceLayout();
        }
        return layout;
    }

    @Override
    public void setDeviceLayout(DeviceLayout layout) {
        if(layout instanceof BrowserDeviceLayout) {
            this.layout = (BrowserDeviceLayout) layout;
        } else {
            layout = getDeviceLayout();
        }
        if(layout.getTheme() == 1) {
            getUI().getElement().setAttribute("theme", Lumo.DARK);
        }
    }

    public void view(MediaFile mediaFile) {
        view(mediaFile, false);
    }

    public void view(String caption, MediaFile mediaFile) {
        view(caption, mediaFile, false);
    }

    public void view(MediaFile mediaFile, boolean windowMode) {
        view(mediaFile.getName(), mediaFile, windowMode);
    }

    public void view(String caption, MediaFile mediaFile, boolean windowMode) {
        com.storedobject.ui.util.DocumentViewer.view(caption, mediaFile, windowMode);
    }

    public void view(String caption, Id objectId) {
        view(caption, StoredObject.get(objectId));
    }

    public void view(Id objectId) {
        view(null, objectId);
    }

    public void view(StoredObject object) {
        view(null, object);
    }

    public void view(String caption, StoredObject object) {
        view(caption, object, null, null);
    }

    public void view(String caption, Id objectId, String actionName, Consumer<StoredObject> action) {
        view(caption, StoredObject.get(objectId), actionName, null);
    }

    public void view(Id objectId, String actionName, Consumer<StoredObject> action) {
        view(null, objectId, actionName, null);
    }

    public void view(StoredObject object, String actionName, Consumer<StoredObject> action) {
        view(null, object);
    }

    public void view(String caption, StoredObject object, String actionName, Consumer<StoredObject> action) {
        ObjectViewer v = null;
        if(objectViewer == null) {
            objectViewer = new ObjectViewer(this);
            v = objectViewer;
        } else {
            if(!objectViewer.executing()) {
                v = objectViewer;
            }
        }
        if(v == null) {
            v = new ObjectViewer(this);
        }
        v.view(caption, object, actionName, action);
    }

    private record CP(String caption, ContentProducer producer, Consumer<Long> timeTracker, boolean windowMode,
                      Component[] extraHeaderButtons) {
    }

    private final List<CP> contentProducers = new ArrayList<>();

    @Override
    public void view(String caption, ContentProducer producer, Consumer<Long> timeTracker, boolean windowMode) {
        view(caption, producer, timeTracker, windowMode, (Component[]) null);
    }

    public void view(String caption, ContentProducer producer, Consumer<Long> timeTracker, boolean windowMode,
                     Component... extraHeaderButtons) {
        if(server == null) {
            return;
        }
        synchronized(contentProducers) {
            CP cp = new CP(caption, producer, timeTracker, windowMode, extraHeaderButtons);
            contentProducers.add(cp);
            if(contentProducers.size() > 1) {
                startPolling(contentProducers);
                return;
            }
        }
        new ContentGenerator(this, producer, caption, this::remove, timeTracker, waitMessage::open,
                windowMode, extraHeaderButtons).kick();
    }

    public void closeWaitMessage() {
        waitMessage.close();
    }

    private void remove(AbstractContentGenerator acg) {
        waitMessage.close();
        if(acg instanceof ContentGenerator cg) {
            synchronized(contentProducers) {
                contentProducers.removeIf(cp -> cp.producer == cg.getProducer());
                if(contentProducers.isEmpty()) {
                    stopPolling(contentProducers);
                } else {
                    CP cp = contentProducers.get(0);
                    new ContentGenerator(this, cp.producer, cp.caption, this::remove, cp.timeTracker,
                            waitMessage::open, cp.windowMode, cp.extraHeaderButtons).kick();
                }
            }
        }
    }

    @Override
    public void download(ContentProducer producer, Consumer<Long> informMe) {
        new ContentGenerator(this, producer, true, null, this::remove, informMe,
                waitMessage::open, false).kick();
    }

    public String addResource(ContentProducer producer) {
        MultiContentGenerator mcg = new MultiContentGenerator(this, producer, this::remove, null,
                waitMessage::open);
        return "so" + mcg.getId() + "." + producer.getFileExtension();
    }

    public Component getAlertButton() {
        return alertButton;
    }

    @Override
    public void alert(LoginMessage message) {
        synchronized(messageViewer) {
            List<com.storedobject.vaadin.Alert> alerts = getAlerts(alertButton);
            if(alerts != null && alerts.stream().filter(a -> a instanceof LoginMessageAlert).
                    map(a -> (LoginMessageAlert)a).anyMatch(a -> a.message.getId().equals(message.getId()))) {
                return;
            }
        }
        createAlert(message).show();
    }

    @Override
    public void alert(String alert) {
        alert(null, alert, null);
    }

    @Override
    public void alert(String caption, String alert) {
        alert(caption, alert, null);
    }

    public void alert(String caption, String alert, String icon) {
        createAlert(caption, alert, icon).show();
    }

    public StyledBuilder getAlert() {
        return getAlert(null, null);
    }

    public StyledBuilder getAlert(Object alertHandler) {
        return getAlert(alertHandler, null);
    }

    public StyledBuilder getAlert(Object alertHandler, Id reference) {
        return createAlert(alertHandler, reference);
    }

    public StyledBuilder getAlert(Id reference) {
        return getAlert(null, reference);
    }

    @Override
    public void alertCountChanged(Object owner) {
        if(owner == alertButton) {
            synchronized (alertButton) {
                alertButton.setAlertCount(Application.getAlertCount(alertButton));
            }
        }
    }

    public void openMenu() {
        mainLayout.openMenu();
    }

    public void closeMenu() {
        mainLayout.closeMenu();
    }

    public boolean isMenuOpened() {
        return mainLayout.isMenuOpened();
    }

    @Override
    public final void login() {
        String s = ApplicationServer.getApplicationName();
        if(!"-".equals(s)) {
            setCaption(s + " " + getDisplayVersion());
        }
        try {
            TextContent home = SOServlet.getTextContent("homeview.html");
            if(home != null) {
                new HomeHTMLView(home).execute();
            } else {
                MediaFile background = SOServlet.getImage("homeview", "background");
                if(background != null) {
                    new ImageView(background).execute();
                }
            }
            MediaFile notice = SOServlet.getImage("homenotice");
            if(notice != null) {
                view(notice, true);
            }
        } catch (Throwable ignored) {
        }
        loginForm = paramLogin();
        if(loginForm != null) {
            log("Accessed");
            loginForm.run();
        } else {
            screenLogin();
        }
    }

    private Runnable paramLogin() {
        String autoToken = getQueryParameter("loginBlock");
        if(autoToken != null && !autoToken.isBlank()) {
            removeQueryParameter("loginBlock");
            String loginBlock = autoToken;
            return () -> {
                if(!login.login(loginBlock)) {
                    screenLogin();
                }
            };
        }
        autoToken = ApplicationServer.getGlobalProperty("application.autologin.token",
                null, false);
        if(autoToken != null && autoToken.equals(getQueryParameter("login"))) {
            removeQueryParameter("login");
            String autoLogin = ApplicationServer.getGlobalProperty("application.autologin.user",
                    null, false);
            if(autoLogin != null && !autoLogin.isBlank()) {
                String autoPassword = ApplicationServer.getGlobalProperty("application.autologin.password",
                        null, false);
                return () -> {
                    login.setType(4);
                    if(!login.login(autoLogin.trim(), autoPassword.toCharArray(), false)) {
                        screenLogin();
                    }
                };
            }
        }
        return null;
    }

    private void screenLogin() {
        loginForm = createLogin();
        if(loginForm == null) {
            loginForm = createLoginInt();
        }
        if(loginForm != null) {
            log("Accessed");
            loginForm.run();
        }
    }

    private Runnable createLoginInt() {
        String loginLogic = ApplicationServer.getGlobalProperty("application.logic.login",
                "", true);
        if(loginLogic.isEmpty()) {
            if(SOServlet.getTextContent("com.storedobject.ui.LoginForm") == null) {
                loginForm = LoginForm.create();
            } else {
                loginForm = new LoginForm();
            }
        } else {
            Logic logic = new Logic(loginLogic, "Login");
            if(server.execute(logic, false) instanceof Runnable ex) {
                loginForm = ex;
            }
        }
        return loginForm;
    }

    protected Runnable createLogin() {
        return null;
    }

    private boolean executePostLogin() {
        PostLogin postLogin = createPostLogin();
        if(postLogin == null) {
            postLogin = createPostLoginInt();
        }
        if(postLogin != null && !postLogin.canLogin(getTransactionManager())) {
            postLogin.informUser();
            return false;
        }
        return true;
    }

    private PostLogin createPostLoginInt() {
        String postLoginLogic = ApplicationServer.getGlobalProperty("application.logic.postLogin",
                "", true);
        if(postLoginLogic.isEmpty()) {
            return null;
        }
        Logic logic = new Logic(postLoginLogic, "Check");
        Object ex = server.execute(logic, false);
        if(ex instanceof PostLogin) {
            return (PostLogin) ex;
        }
        return null;
    }

    protected PostLogin createPostLogin() {
        return null;
    }

    private void startApp(boolean welcomePassword, boolean passwordExpired) {
        if(welcomePassword || passwordExpired) {
            new ChangePassword(passwordExpired).execute();
            return;
        }
        startPolling(this);
        BusyIndicator bi = getBusyIndicator();
        getUI().access(() -> {
            loggedin();
            LoginMessage.showMessages(getServer(), null);
            bi.close();
            stopPolling(Application.this);
            mainLayout.initialized();
            if(!singleLogicMode) {
                setSingleLogicMode((getTransactionManager().getUser().getPreferences() & 2) == 2);
            }
        });
        createTimer();
    }

    @Override
    protected void viewDetached(View view) {
        if(mainLayout != null) {
            mainLayout.viewDetached(view);
        }
        Object viewOwner = view.getCreatedBy();
        if(viewOwner instanceof Executable ex) {
            synchronized(closeMe) {
                Logic logic = closeMe.stream().filter(l -> l.getExecutable() == ex).findAny().orElse(null);
                if(logic != null) {
                    closeMe.remove(logic);
                    logic.setExecutable(null);
                }
            }
        }
    }

    @Override
    public final boolean loggedIn(Login login) {
        if(loginForm != null && this.login != null && login == this.login) {
            if(executePostLogin()) {
                loginDone();
            }
            return true;
        }
        return false;
    }

    private void loginDone() {
        Runnable r = loginForm;
        loginForm = null;
        if(r instanceof ExecutableView v) {
            v.close();
        }
        SystemUser su = server.getTransactionManager().getUser();
        boolean checkPassword = !su.isAdmin();
        if(checkPassword && !"demo".equals(ApplicationServer.runMode())) {
            checkPassword = !"guest".equals(su.getLogin());
        }
        Login l = login;
        login = null;
        boolean cp = checkPassword;
        selectEntity(cp && l.isWelcomePassword(), cp && su.isPasswordExpired());
    }

    @Override
    public final boolean forgotPassword(Login login) {
        if(loginForm != null && this.login != null && login == this.login) {
            forgotPassword();
            return true;
        }
        return false;
    }

    private void forgotPassword() {
        Runnable r = loginForm;
        loginForm = null;
        if(r instanceof ExecutableView v) {
            v.close();
        }
        login = null;
        identityCheck = null;
        try {
            identityCheck = (IdentityCheck) JavaClassLoader
                    .createInstanceFromProperty("application.login.password.forgot");
            if(identityCheck != null) {
                identityCheck.setUser(getTransactionManager().getUser());
                if(identityCheck instanceof Executable executable) {
                    executable.execute();
                } else if(identityCheck instanceof Runnable runnable) {
                    runnable.run();
                }
                closeMenu();
            }
        } catch(Throwable e) {
            log(e);
        }
        if(identityCheck == null) {
            close();
        }
    }

    public final void forgotPassword(IdentityCheck identityCheck) {
        if(identityCheck != this.identityCheck || identityCheck.getUser() != getTransactionManager().getUser()) {
            close();
            return;
        }
        String exitSite = identityCheck.getOTPExitSite();
        String errorMessage = identityCheck.getOTPErrorMessage();
        if(errorMessage == null) {
            errorMessage = "Unable to send OTP due to technical errors, please try later or contact support";
        }
        Runnable cancel, error;
        if(exitSite == null) {
            cancel = this::close;
        } else {
            cancel = () -> close(exitSite);
        }
        String finalErrorMessage = errorMessage;
        error = () -> exit(finalErrorMessage, exitSite);
        VerifyOTP verifyOTP = new VerifyOTP(identityCheck.isSingleOTP(), identityCheck.getMobile(),
                identityCheck.getEmail(),
                () -> new SetNewPassword(identityCheck.getPasswordChangeCaption()).execute(), cancel, error);
        String otpTemplate = identityCheck.getOTPTemplate();
        if(otpTemplate != null) {
            verifyOTP.setTemplateName(otpTemplate);
        }
        verifyOTP.setUserTimeout(identityCheck.getOTPTimeout());
        verifyOTP.setCustomTag(identityCheck.getOTPTag());
        verifyOTP.execute();
        closeMenu();
    }

    private class SetNewPassword extends com.storedobject.ui.tools.ChangePassword {

        private SetNewPassword(String caption) {
            super(Application.this.getTransactionManager().getUser(), caption);
            setAllowNameChange(identityCheck.allowNameChange());
        }

        @Override
        protected boolean process() {
            if(super.process()) {
                close();
                String m = null;
                if(isChanged()) {
                    identityCheck.passwordChangeSucceeded();
                    m = identityCheck.getSuccessMessage();
                }
                changed(m);
                return true;
            }
            return false;
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            super.execute(parent, doNotLock);
            closeMenu();
        }

        @Override
        public void abort() {
            close();
            identityCheck.passwordChangeFailed();
            changed(identityCheck.getFailureMessage());
        }

        private void changed(String m) {
            if(m == null) {
                if(isChanged()) {
                    m = "New password is set successfully. Please log in again to start using the application.";
                } else {
                    m = "Password not set! Please contact Technical Support for any help!!";
                }
            }
            String exitSite = identityCheck.getExitSite();
            Runnable close = exitSite == null ? Application.this::close : () -> Application.this.close(exitSite);
            InformationMessage message = new InformationMessage(new ELabel(m, isChanged() ? COLOR_SUCCESS : COLOR_ERROR),
                    close, "Close");
            message.setCloseable(false);
            message.execute();
            closeMenu();
        }
    }

    public static FilterProvider getUserVisibility(String action) {
        try {
            return (FilterProvider) createInstance("application.login.visibility."
                    + action, true, true);
        } catch(Throwable ignored) {
        }
        return () -> "false";
    }

    public BusyIndicator getProgressBar(boolean indeterminate) {
        ProgressStrip ps = new ProgressStrip(this);
        if(indeterminate) {
            ps.setIndeterminate(true);
        }
        getUI().access(() -> add(ps));
        return ps;
    }

    public BusyIndicator getBusyIndicator() {
        BusyIndicator pb = new BusyIndicator(this);
        pb.setIndeterminate(true);
        getUI().access(() -> add(pb));
        return pb;
    }

    private void add(BusyIndicator bi) {
        HasComponents bars = mainLayout.getProgressBarHolder();
        if(bars != null) {
            bars.add(bi.getLabel());
            bars.add(bi);
        }
    }

    void drawMenu(com.storedobject.vaadin.Application application) {
        ApplicationServer as = ((Application)application).getServer();
        Application a = (Application) application;
        List<Logic> autos;
        ApplicationMenu frameMenu = mainLayout.getMenu();
        int count = frameMenu.getMenuPane().getElement().getChildCount();
        if(frameMenu instanceof com.storedobject.core.ApplicationMenu am) {
            autos = as.populateMenu(am, null);
        } else {
            Application.Menu am;
            autos = as.populateMenu(am = new Application.Menu(frameMenu), null);
            ArrayList<Logic> allLogic = am.logicList;
            am.logicList = null;
            Component sm = mainLayout.getMenuSearcher();
            if(sm instanceof ComboBox) {
                @SuppressWarnings("unchecked") ComboBox<Logic> searchMenu = (ComboBox<Logic>) sm;
                searchMenu.setItems(allLogic);
                searchMenu.addValueChangeListener(e -> {
                    Logic v = e.getValue();
                    if(v != null) {
                        a.execute(v);
                        searchMenu.clear();
                    }
                });
            }
        }
        application.stopPolling(application);
        autos.forEach(as::execute);
        if(autos.isEmpty()) {
            count = frameMenu.getMenuPane().getElement().getChildCount() - count;
            if(count == 0) {
                ELabel info = new ELabel("Please contact support with the following details:", COLOR_ERROR);
                info.newLine();
                information(info);
                Viewer viewer = new Viewer(new CenteredLayout(info), "Support", false) {
                    @Override
                    public boolean isHomeView() {
                        return true;
                    }
                };
                viewer.execute();
            }
        }
    }

    public void information(StyledBuilder appDetails) {
        appDetails
                .append("Version: ").append(getDriverIdentifier())
                .newLine().append("Device Size: ").append(getDeviceWidth()).append('x').append(getDeviceHeight())
                .newLine().append("URL: ").append(getURL())
                .newLine().append("Biometric Available: ").append(isBiometricAvailable())
                .newLine().append("Biometric Registered: ").append(isBiometricRegistered())
                .newLine().append("License Status: ").append(ApplicationServer.getLicenseStatus())
                .update();
    }

    protected boolean canCreateMenu(Logic logic) {
        return true;
    }

    protected boolean canCreateMenu(LogicGroup logicGroup) {
        return true;
    }

    public class BusyIndicator extends ProgressBar {

        private final Application application;
        private ELabel label;
        private boolean active = false;

        protected BusyIndicator(Application application) {
            this.application = application;
        }

        public ELabel getLabel() {
            if(label == null) {
                label = new ELabel();
            }
            return label;
        }

        public void setCaption(String caption) {
            getLabel().setText(caption);
            label.update();
        }

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            super.onAttach(attachEvent);
            if(isVisible()) {
                activate();
            }
        }

        @Override
        protected void onDetach(DetachEvent detachEvent) {
            super.onDetach(detachEvent);
            deactivate();
        }

        @Override
        public void setVisible(boolean visible) {
            super.setVisible(visible);
            if(visible) {
                if(getParent().isPresent()) {
                    activate();
                }
            } else {
                deactivate();
            }
            getLabel().setVisible(visible);
        }

        @Override
        public void setValue(double value) {
            boolean close = value >= 0.9999;
            if(close) {
                value = 1;
            }
            super.setValue(value);
            if(close) {
                if(getParent().isPresent()) {
                    deactivate();
                    HasComponents holder = mainLayout.getProgressBarHolder();
                    if(holder != null) {
                        if(label != null) {
                            holder.remove(label);
                        }
                        holder.remove(this);
                    }
                }
            }
        }

        public void close() {
            setValue(1);
        }

        private void activate() {
            synchronized (application) {
                if(active) {
                    return;
                }
                application.setPollInterval(this, 1000);
                active = true;
            }
        }

        private void deactivate() {
            synchronized (application) {
                if(!active) {
                    return;
                }
                application.setPollInterval(this, -1);
                active = false;
            }
        }
    }

    private class ProgressStrip extends BusyIndicator {

        private ProgressStrip(Application application) {
            super(application);
        }

        @Override
        public void setCaption(String caption) {
            super.setCaption(caption);
        }
    }

    private void alertClicked() {
        List<com.storedobject.vaadin.Alert> alerts = Application.getAlerts(alertButton);
        if(alerts == null || alerts.isEmpty()) {
            return;
        }
        alertsVisible = !alertsVisible;
        if(alertsVisible) {
            alerts.forEach(com.storedobject.vaadin.Alert::show);
        } else {
            new ArrayList<>(alerts).forEach(com.storedobject.vaadin.Alert::close);
        }
    }

    private Alert createAlert(String caption, String alert, String icon) {
        Alert a = new Alert("");
        if(icon != null) {
            a.appendIcon(icon);
        }
        if(caption != null) {
            a.appendWithTag(caption, "h2");
        }
        if(alert != null) {
            a.append(alert);
        }
        Application.registerAlert(a, alertButton);
        a.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        return a;
    }

    private Alert createAlert(Object alertHandler, Id reference) {
        if(alertHandler == null && reference == null) {
            return createAlert(null, "", null);
        }
        Alert alert = new Alert("", a -> new AlertProcessor(null, a, alertHandler, reference).execute());
        alert.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        return alert;
    }

    private Alert createAlert(LoginMessage message) {
        Alert m = new LoginMessageAlert(message);
        registerAlert(m);
        return m;
    }

    private void registerAlert(Alert alert) {
        List<com.storedobject.vaadin.Alert> list = Application.getAlerts(alertButton);
        if(list == null) {
            Application.registerAlert(alert, alertButton);
            return;
        }
        Object item;
        for(int i = 0; i < list.size(); i++) {
            item = list.get(i);
            if(item instanceof Alert) {
                if(alert.getPriority() >= ((Alert) item).getPriority()) {
                    list.add(i, alert);
                    break;
                }
                continue;
            }
            Application.registerAlert(alert, alertButton);
        }
    }

    @Override
    public String getCountry() {
        TransactionManager tm = getTransactionManager();
        return tm == null ? "US" : tm.getUser().getLocaleCountry();
    }

    private static class LoginMessageAlert extends Alert {

        private final LoginMessage message;

        private LoginMessageAlert(LoginMessage message) {
            super(message.getMessage(), a -> new LoginMessageProcessor(message, a));
            this.message = message;
            priorityChanged(-1);
        }

        void priorityChanged(int oldP) {
            if(oldP >= 0) {
                removeThemeVariants(switch(oldP) {
                    case 3 -> NotificationVariant.LUMO_ERROR;
                    case 2 -> NotificationVariant.LUMO_CONTRAST;
                    default -> NotificationVariant.LUMO_SUCCESS;
                });
            }
            addThemeVariants(switch(message.getPriority()) {
                case 3 -> NotificationVariant.LUMO_ERROR;
                case 2 -> NotificationVariant.LUMO_CONTRAST;
                default -> NotificationVariant.LUMO_SUCCESS;
            });
        }

        @Override
        public int getPriority() {
            return message.getPriority();
        }
    }

    abstract static class AbstractAlertProcessor extends DataForm implements Transactional {

        final Alert alert;
        final StyledBuilder messageText = new ELabel();
        Object alertHandler;
        Button delete;

        AbstractAlertProcessor(String caption, Alert alert) {
            super(caption == null || caption.isEmpty() ? "Message" : caption, "Process", "Cancel");
            this.alert = alert;
            messageText.appendHTML(alert.getContent().getHTML()).update();
            add((Component)messageText);
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            buttonPanel.removeAll();
            delete = new ConfirmButton("Delete", VaadinIcon.MINUS_CIRCLE, e -> {
                if(delete()) {
                    alert.delete();
                    close();
                }
            });
            buttonPanel.add(ok, delete, cancel);
            ok.setVisible(false);
        }

        boolean delete() {
            return true;
        }

        void handleAlert(Id reference) {
            alert.delete();
            close();
            if(alertHandler == null) {
                return;
            }
            if(alertHandler instanceof AlertHandler ah) {
                if(reference != null) {
                    ah.handleAlert(reference);
                }
                return;
            }
            if(reference != null && alertHandler instanceof ObjectSetter<?> os) {
                os.setObject(reference);
            }
            if(alertHandler instanceof Runnable r) {
                r.run();
            }
        }

        void setAlertHandler(Object alertHandler) {
            getComponent();
            if(alertHandler instanceof StoredObject || alertHandler instanceof Id) {
                alertHandler = new ObjectViewer(Application.get());
            }
            if(alertHandler != null && !(alertHandler instanceof AlertHandler)) {
                alertHandler = ((Application)getApplication()).getServer().execute(alertHandler, false);
            }
            this.alertHandler = alertHandler;
            if(alertHandler == null) {
                ok.setVisible(false);

            } else {
                if(alertHandler instanceof AlertHandler ah) {
                    String s = ah.getAlertCaption();
                    ok.setText(s == null ? "Process" : s);
                    s = ah.getAlertIcon();
                    if(s == null) {
                        ok.setIcon(VaadinIcon.COG_O);
                    } else {
                        ok.setIcon(s);
                    }
                } else {
                    ok.setText("Process");
                    ok.setIcon(VaadinIcon.COG_O);
                }
                ok.setVisible(true);
            }
        }
    }

    private static class AlertProcessor extends AbstractAlertProcessor {

        private final Id reference;

        AlertProcessor(String caption, Alert alert, Object alertHandler, Id reference) {
            super(caption, alert);
            if(alertHandler instanceof StoredObject so) {
                reference = so.getId();
            }
            this.reference = reference;
            setAlertHandler(alertHandler == null ? reference : alertHandler);
        }

        @Override
        protected void formConstructed() {
            super.formConstructed();
        }

        @Override
        protected boolean process() {
            handleAlert(reference);
            return true;
        }
    }

    private static class LoginMessageProcessor extends AbstractAlertProcessor {

        private final LoginMessage message;
        private final ChoiceField priority = new ChoiceField("Priority", LoginMessage.getPriorityValues());
        private Button changePriority;
        private Id reference;

        private LoginMessageProcessor(LoginMessage message, Alert alert) {
            super(null, alert);
            this.message = message;
            Logic logic = message.getProcessorLogic();
            if(logic != null) {
                setAlertHandler(logic);
            }
            StoredObject ref = message.listGeneratedBy().findFirst();
            if(logic == null && ref != null) {
                if(ref instanceof Memo) {
                    MemoType memoType = ((Memo)ref).getType();
                    MemoSystem ms;
                    if(memoType.getSpecial()) {
                        ms = new MemoSystem(memoType, false);
                    } else {
                        ms = Application.get().memoSystem;
                        if(ms == null) {
                            ms = new MemoSystem(false);
                            Application.get().memoSystem = ms;
                        }
                    }
                    setAlertHandler(ms);
                } else {
                    setAlertHandler(ref);
                }
                reference = ref.getId();
            }
            execute();
        }

        @Override
        protected void buildFields() {
            super.buildFields();
            addField(priority);
            priority.setValue(message.getPriority());
            trackValueChange(priority);
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            buttonPanel.removeAll();
            changePriority = new Button("Change Priority", VaadinIcon.SORT, this);
            buttonPanel.add(ok, changePriority, delete, cancel);
            changePriority.setEnabled(false);
        }

        @Override
        public void clicked(Component c) {
            if(c == changePriority) {
                int oldP = message.getPriority();
                message.setPriority(priority.getValue());
                boolean changed = transact(message::save);
                message.reload();
                priority.setValue(message.getPriority());
                changePriority.setEnabled(false);
                if(changed) {
                    alert.delete();
                    ((Application)getApplication()).registerAlert(alert);
                    ((LoginMessageAlert)alert).priorityChanged(oldP);
                }
                return;
            }
            if(c == priority) {
                changePriority.setEnabled(priority.getValue() != message.getPriority());
                return;
            }
            super.clicked(c);
        }

        @Override
        boolean delete() {
            if(message.countLinks(Person.class) <= 1) {
                return transact(message::delete);
            }
            Id pid = getTransactionManager().getUser().getPersonId();
            return transact(t -> message.removeLink(t, pid));
        }

        @Override
        protected boolean process() {
            handleAlert(reference);
            return true;
        }
    }

    private void selectEntity(boolean welcomePassword, boolean passwordExpired) {
        SystemUser su = getTransactionManager().getUser();
        List<SystemEntity> entities;
        try {
            entities = su.listEntities();
        } catch(Throwable e) {
            new InformationMessage(e.getMessage(), this::close, "Close").execute();
            return;
        }
        if(entities.size() < 2) {
            if(entities.size() == 1) {
                getTransactionManager().setEntity(entities.get(0));
            }
            setDate(server.getDate());
            startApp(welcomePassword, passwordExpired);
            return;
        }
        new EntitySelector(entities, su.getName(), welcomePassword, passwordExpired).execute();
    }

    private static class Menu implements com.storedobject.core.ApplicationMenu {

        private final ApplicationMenu menu;
        private ArrayList<Logic> logicList = new ArrayListSet<>();

        private Menu(ApplicationMenu menu) {
            this.menu = menu;
        }

        @Override
        public void add(Logic logic) {
            Application a = Application.get();
            if(a.canCreateMenu(logic)) {
                menu.add(a.createMenuItem(logic.getTitle(), logic.getIconImageName(), () -> a.execute(logic)));
                logicList.add(logic);
            }
        }

        @Override
        public void add(LogicGroup logicGroup) {
        }

        @Override
        public com.storedobject.core.ApplicationMenu createGroupMenu(LogicGroup logicGroup) {
            return Application.get().canCreateMenu(logicGroup) ? new GroupMenu(logicGroup, null) : null;
        }

        private class GroupMenu implements com.storedobject.core.ApplicationMenu {

            private final GroupMenu parent;
            private final LogicGroup logicGroup;
            private ApplicationMenuItemGroup groupMenu;

            private GroupMenu(LogicGroup logicGroup, GroupMenu parent) {
                this.logicGroup = logicGroup;
                this.parent = parent;
            }

            private void createGroupMenu() {
                if(groupMenu != null) {
                    return;
                }
                if(parent != null) {
                    parent.createGroupMenu();
                }
                groupMenu = Application.get().createMenuItemGroup(logicGroup.getTitle());
                menu.add(groupMenu);
                if(parent != null) {
                    parent.groupMenu.add(groupMenu);
                }
            }

            @Override
            public void add(Logic logic) {
                Application a = Application.get();
                if(!a.canCreateMenu(logic)) {
                    return;
                }
                logicList.add(logic);
                if(groupMenu == null) {
                    createGroupMenu();
                }
                ApplicationMenuItem mi = a.createMenuItem(logic.getTitle(), logic.getIconImageName(),
                        () -> a.execute(logic));
                menu.add(mi);
                groupMenu.add(mi);
            }

            @Override
            public void add(LogicGroup logicGroup) {
            }

            @Override
            public com.storedobject.core.ApplicationMenu createGroupMenu(LogicGroup logicGroup) {
                return new GroupMenu(logicGroup, this);
            }
        }
    }

    private class ChangePassword extends com.storedobject.ui.tools.ChangePassword implements HomeView {

        public ChangePassword(boolean expired) {
            super(expired, true);
        }

        @Override
        protected Window createWindow(Component component) {
            return new Window(new WindowDecorator(this, speaker()), component);
        }

        @Override
        protected boolean process() {
            if(!super.process()) {
                return false;
            }
            close();
            if(isChanged()) {
                selectEntity(false, false);
            } else {
                new InformationMessage("Not Changed!",
                        "Unable to change your password and you need to log in with your previous password again.",
                        Application.this::close).execute();
            }
            return true;
        }
    }

    static SpeakerButton speaker() {
        SpeakerButton sb = new SpeakerButton();
        return sb.withBox();
    }

    private class EntitySelector extends DataForm implements HomeView {

        private final ObjectComboField<SystemEntity> entities;
        private final boolean welcomePassword, passwordExpired;

        public EntitySelector(List<SystemEntity> entities, String name, boolean welcomePassword, boolean passwordExpired) {
            super(name);
            this.welcomePassword = welcomePassword;
            this.passwordExpired = passwordExpired;
            this.entities = new ObjectComboField<>("Select Organization", SystemEntity.class, entities);
            this.entities.setValue(entities.get(0));
            addConstructedListener(o -> fConstructed());
        }

        private void fConstructed() {
            setColumns(1);
            ((HasSize)getContent()).setMinWidth((getDeviceWidth() / 3) + "px");
        }

        @Override
        protected Window createWindow(Component component) {
            return new Window(new WindowDecorator(this, speaker()), component);
        }

        @Override
        protected void buildFields() {
            addField(entities);
            setRequired(entities);
            cancel.setText("Sign out");
            ok.setText("Proceed");
        }

        @Override
        protected boolean process() {
            SystemEntity se = entities.getValue();
            close();
            getTransactionManager().setEntity(se);
            setDate(server.getDate());
            startApp(welcomePassword, passwordExpired);
            return true;
        }

        @Override
        public void abort() {
            super.abort();
            getApplication().close();
        }
    }

    private class DownloadHandler implements RequestHandler {

        private long multiClean = System.currentTimeMillis();

        @Override
        public boolean handleRequest(VaadinSession vaadinSession, VaadinRequest vaadinRequest, VaadinResponse vaadinResponse) {
            if(multiContent.size() > 0 && ((System.currentTimeMillis() - multiClean) / 1000) > 600) {
                boolean removed = true;
                while(removed) {
                    removed = false;
                    for(Long k: multiContent.keySet()) {
                        if(multiContent.get(k).get() == null) {
                            multiContent.remove(k);
                            removed = true;
                            break;
                        }
                    }
                }
                multiClean = System.currentTimeMillis();
            }
            String url = vaadinRequest.getPathInfo();
            if(!url.startsWith("/so")) {
                return false;
            }
            url = url.substring(3);
            boolean multi = url.startsWith("-");
            if(multi) {
                url = url.substring(1);
            }
            int ext = url.indexOf('.');
            if(ext > 0) {
                url = url.substring(0,  ext);
            }
            if(!StringUtility.isDigit(url)) {
                return false;
            }
            long key = Long.parseLong(url);
            AbstractContentGenerator generator;
            if(multi) {
                key = -key;
                WeakReference<AbstractContentGenerator> w = multiContent.get(key);
                if(w != null) {
                    generator = w.get();
                    if(generator == null) {
                        multiContent.remove(key);
                    }
                } else {
                    generator = null;
                }
            } else {
                generator = dynamicContent.remove(key);
            }
            if(generator == null) {
                return true;
            }
            try {
                generator.getContent().writeResponse(vaadinRequest, vaadinResponse);
            } catch(Exception e) {
                generator.abort();
                access(() -> error(e));
                Application.this.log("Content Generator - " + getTransactionManager().getUser().getLogin(), e);
            }
            return true;
        }
    }

    public void addContent(long fileId, AbstractContentGenerator content) {
        if(server != null) {
            dynamicContent.put(fileId, content);
        }
    }

    public void addMultiContent(long fileId, AbstractContentGenerator content) {
        if(server != null) {
            multiContent.put(fileId, new WeakReference<>(content));
        }
    }

    private com.storedobject.sms.QuickSender smsSender;
    private com.storedobject.mail.QuickSender mailSender;

    public com.storedobject.sms.QuickSender getSMSSender() {
        if(smsSender == null) {
            smsSender = (QuickSender) createInstance("application.quick.sms", true, false);
        }
        return smsSender;
    }

    public com.storedobject.mail.QuickSender getMailSender() {
        if(mailSender == null) {
            mailSender = (com.storedobject.mail.QuickSender)
                    createInstance("application.quick.mail", true, false);
        }
        return mailSender;
    }

    public static Object createInstance(String propertyName, boolean showError, boolean raiseError) {
        try {
            return JavaClassLoader.createInstanceFromProperty(propertyName);
        } catch(SOException e) {
            ApplicationServer.log(e);
            if(showError) {
                Application.warning(e);
            }
            if(raiseError) {
                throw new SORuntimeException(e);
            }
        }
        return null;
    }

    // Viewer for login messages and alerts.
    private final MV messageViewer = new MV(this);

    private static class MV implements MessageViewer {

        private final Application a;
        private Id minId = null;

        private MV(Application a) {
            this.a = a;
        }

        @Override
        public TransactionManager getTransactionManager() {
            return a.getTransactionManager();
        }

        @Override
        public void alert(String alert) {
            a.alert(alert);
        }

        @Override
        public void message(LoginMessage m) {
            if(minId == null || m.getId().get().compareTo(minId.get()) > 0) {
                minId = m.getId();
            }
            a.alert(m);
        }

        Id getMinId() {
            return minId;
        }
    }

    // Timer for alert messages.
    private Timer timer;

    private void createTimer() {
        long t;
        try {
            t = Long.parseLong(ApplicationServer.getGlobalProperty("application.refresh.timer",
                    "600", true));
            if(t == 0) {
                return;
            }
        } catch(Throwable ignored) {
            t = 600;
        }
        this.timer = new Timer();
        this.timer.schedule(new AppTimer(), 20000, t * 1000L);
    }

    private void closeTimer() {
        synchronized(messageViewer) {
            if(this.timer != null) {
                Timer t = this.timer;
                this.timer = null;
                t.cancel();
            }
        }
    }

    private class AppTimer extends TimerTask {

        @Override
        public void run() {
            synchronized(messageViewer) {
                access(this::task);
            }
        }

        private void task() {
            LoginMessage.showMessages(getServer(), messageViewer.getMinId());
        }
    }
}
