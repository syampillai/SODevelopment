package com.storedobject.ui;

import com.storedobject.common.ArrayListSet;
import com.storedobject.common.SORuntimeException;
import com.storedobject.core.*;
import com.storedobject.sms.QuickSender;
import com.storedobject.ui.util.ApplicationFrame;
import com.storedobject.ui.util.*;
import com.storedobject.vaadin.ApplicationMenu;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
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
import java.util.function.Consumer;

public class Application extends com.storedobject.vaadin.Application implements Device, RunningLogic, RequiresApproval {

    private static final String VERSION = "20.0.6";
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
    private final ApplicationLayout mainLayout;
    private boolean singleLogicMode = false, abortOnLogicSwitch = false;
    private final Hashtable<Long, AbstractContentGenerator> dynamicContent = new Hashtable<>();
    private final Hashtable<Long, WeakReference<AbstractContentGenerator>> multiContent = new Hashtable<>();
    private ObjectViewer objectViewer;
    boolean biometricAvailable = false, biometricRegistered = false;
    Id biometricDeviceId = null;
    private boolean alertsVisible = false;
    private final AlertButton alertButton = new AlertButton(e -> alertClicked());
    private Login login;
    private boolean compactTheme = false;
    private Runnable loginForm;

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
        this.mainLayout = applicationLayout;
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
                execute(() -> server.execute(logic), true);
            } else {
                if(getActiveViews().allMatch(v -> v instanceof InformationView || v.getComponent() instanceof PDFViewer)) {
                    server.execute(logic);
                }
                closeMenu();
            }
        } else {
            server.execute(logic);
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
    public void close() {
        if(server == null) {
            return;
        }
        closeTimer();
        ArrayList<View> views = new ArrayList<>();
        getActiveViews().forEach(views::add);
        int count = views.size() << 1;
        while(count-- > 0) {
            views.removeIf(v -> !v.executing());
            if(views.isEmpty()) {
                break;
            }
            View v = views.remove(0);
            if(v.executing()) {
                try {
                    v.abort();
                } catch(Throwable ignored) {
                }
            }
        }
        String exit = ApplicationServer.getGlobalProperty("application.exit.site", null);
        if(exit == null) {
            exit = layout == null ? "" : layout.getExitSite().trim();
        }
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
        super.close();
        if(ui == null) {
            return;
        }
        StringBuilder script = new StringBuilder("navigator.credentials.preventSilentAccess();");
        if(exit.isEmpty()) {
            script.append("window.close();");
        }
        if(isSpeakerOn()) {
            script.append("window.speechSynthesis.speak(new SpeechSynthesisUtterance('Goodbye'));");
        }
        ui.getPage().executeJs(script.toString());
        if(!exit.isEmpty()) {
            ui.getPage().setLocation(exit);
        }
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

    public final String getVersion() {
        return VERSION;
    }

    public final String getDisplayVersion() {
        return ApplicationServer.getGlobalProperty("application.version", VERSION);
    }

    @Override
    public String getDriverIdentifier() {
        return "V" + getVersion() + "-F" + com.vaadin.flow.server.Version.getFullVersion();
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
        view(mediaFile.getName(), mediaFile);
    }

    public void view(String caption, MediaFile mediaFile) {
        com.storedobject.ui.util.DocumentViewer.view(caption, mediaFile);
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
        v.view(caption, object);
    }

    private record CP(String caption, ContentProducer producer, Consumer<Long> timeTracker) {
    }

    private final List<CP> contentProducers = new ArrayList<>();

    @Override
    public void view(String caption, ContentProducer producer, Consumer<Long> timeTracker) {
        synchronized(contentProducers) {
            CP cp = new CP(caption, producer, timeTracker);
            contentProducers.add(cp);
            if(contentProducers.size() > 1) {
                startPolling(contentProducers);
                return;
            }
        }
        new ContentGenerator(this, producer, caption, this::remove, timeTracker).kick();
    }

    private void remove(AbstractContentGenerator acg) {
        if(acg instanceof ContentGenerator cg) {
            synchronized(contentProducers) {
                contentProducers.removeIf(cp -> cp.producer == cg.getProducer());
                if(contentProducers.isEmpty()) {
                    stopPolling(contentProducers);
                } else {
                    CP cp = contentProducers.get(0);
                    new ContentGenerator(this, cp.producer, cp.caption, this::remove, cp.timeTracker).kick();
                }
            }
        }
    }

    @Override
    public void download(ContentProducer producer, Consumer<Long> informMe) {
        new ContentGenerator(this, producer, null, this::remove, informMe).kick();
    }

    public String addResource(ContentProducer producer) {
        MultiContentGenerator mcg = new MultiContentGenerator(this, producer, this::remove, null);
        return "so" + mcg.getId() + "." + producer.getFileExtension();
    }

    public Component getAlertButton() {
        return alertButton;
    }

    @Override
    public void alert(LoginMessage message) {
        synchronized(messageViewer) {
            List<com.storedobject.vaadin.Alert> alerts = Application.getAlerts(alertButton);
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
                MediaFile background = SOServlet.getImage("homeview");
                if(background == null) {
                    background = SOServlet.getImage("background");
                }
                if(background != null) {
                    new ImageView(background).execute();
                }
            }
            home = SOServlet.getTextContent("homenotice.html");
            if(home != null) {
                new HomeNotice(home).execute();
                return;
            } else {
                MediaFile notice = SOServlet.getImage("homenotice");
                if(notice != null) {
                    new HomeNotice(notice).execute();
                    return;
                }
            }
        } catch (Throwable ignored) {
        }
        executeLogin();
    }

    private void executeLogin() {
        loginForm = createLogin();
        if(loginForm == null) {
            loginForm = createLoginInt();
        }
        if(loginForm != null) {
            loginForm.run();
        }
    }

    private Runnable createLoginInt() {
        String LOGIN_LOGIC = "application.logic.login";
        String loginLogic = ApplicationServer.getGlobalProperty(LOGIN_LOGIC, "");
        if(loginLogic.isEmpty()) {
            try {
                loginLogic = GlobalProperty.get(LOGIN_LOGIC);
            } catch(Throwable ignored) {
            }
        }
        if(loginLogic.isEmpty()) {
            if(SOServlet.getTextContent("com.storedobject.ui.LoginForm") == null) {
                loginForm = LoginForm.create();
            } else {
                loginForm = new LoginForm();
            }
        } else {
            Logic logic = new Logic(loginLogic, "Login");
            Object ex = server.execute(logic, false);
            if(ex instanceof Runnable) {
                loginForm = (Runnable) ex;
            }
        }
        return loginForm;
    }

    protected Runnable createLogin() {
        return null;
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
    }

    @Override
    public final boolean loggedin(Login login) {
        if(loginForm != null && this.login != null && login == this.login) {
            loginDone();
            return true;
        }
        return false;
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
                ELabel info = new ELabel("Please contact support with the following details:");
                info.newLine();
                information(info);
                new Viewer(new CenteredLayout(info), "Support", false).execute();
            }
        }
    }

    public void information(StyledBuilder appDetails) {
        appDetails.append("Version: ").append(getDriverIdentifier()).
                newLine().append("Device Size: ").append(getDeviceWidth()).append('x').append(getDeviceHeight()).
                newLine().append("Biometric Available: ").append(isBiometricAvailable()).
                newLine().append("Biometric Registered: ").append(isBiometricRegistered()).
                update();
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

        void handleAlert(StoredObject reference) {
            alert.delete();
            close();
            if(alertHandler == null) {
                return;
            }
            if(alertHandler instanceof AlertHandler ah) {
                ah.handleAlert(reference);
            } else if(alertHandler instanceof Runnable r) {
                r.run();
            }
        }

        void setAlertHandler(Object alertHandler) {
            if(alertHandler instanceof Class<?>) {
                alertHandler = ((Application)getApplication()).getServer().execute((Class<?>)alertHandler, false);
            }
            this.alertHandler = alertHandler;
            if(alertHandler == null) {
                ok.setVisible(false);
            } else {
                if(alertHandler instanceof AlertHandler aa) {
                    String s = aa.getAlertCaption();
                    ok.setText(s == null ? "Process" : s);
                    s = aa.getAlertIcon();
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
        private StoredObject referenceObject;

        AlertProcessor(String caption, Alert alert, Object alertHandler, Id reference) {
            super(caption, alert);
            if(alertHandler instanceof StoredObject) {
                referenceObject = (StoredObject) alertHandler;
                alertHandler = null;
            }
            this.reference = reference;
            if(alertHandler == null && !Id.isNull(reference)) {
                referenceObject = StoredObject.get(reference);
            }
            if(referenceObject != null && alertHandler == null) {
                alertHandler = ObjectEditor.create(referenceObject.getClass(), EditorAction.VIEW);
            }
            setAlertHandler(alertHandler);
        }

        @Override
        protected boolean process() {
            handleAlert(referenceObject == null ? StoredObject.get(reference) : referenceObject);
            return true;
        }
    }

    private static class LoginMessageProcessor extends AbstractAlertProcessor {

        private final LoginMessage message;
        private final ChoiceField priority = new ChoiceField("Priority", LoginMessage.getPriorityValues());
        private Button changePriority;

        private LoginMessageProcessor(LoginMessage message, Alert alert) {
            super(null, alert);
            this.message = message;
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
            return false;
        }
    }

    private void selectEntity(boolean welcomePassword, boolean passwordExpired) {
        ArrayList<SystemEntity> entities = new ArrayList<>();
        SystemUser su = getTransactionManager().getUser();
        if(su.isSS() || su.isAppAdmin() || su.isAdmin()) {
            StoredObject.list(SystemEntity.class, null,"Id").collectAll(entities);
        } else {
            su.listLinks(SystemEntity.class, null, "Id").collectAll(entities);
            if(entities.isEmpty()) {
                StoredObject.list(SystemEntity.class, null,"Id").collectAll(entities);
                if(entities.size() > 1) { // Multi
                    int count = entities.size();
                    // Remove fresher than mine
                    entities.removeIf(e -> e.getId().get().compareTo(su.getId().get()) > 0);
                    if(count == entities.size() || entities.isEmpty()) {
                        new NoEntityExit(Application.this);
                        return;
                    }
                }
            }
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

    private static class NoEntityExit extends ActionForm {

        public NoEntityExit(Application a) {
            super("Error",
                    "You do not have access to any configured organization.\nPlease contact Support!",
                    a::close, a::close);
            execute();
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            cancel.setVisible(false);
            ok.setText("Ok");
        }
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
                ApplicationMenuItem mi = a.createMenuItem(logic.getTitle(), logic.getIconImageName(), () -> a.execute(logic));
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
            selectEntity(false, false);
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
                error(e);
                Application.this.log("Content Generator - " + getTransactionManager().getUser().getLogin(), e);
            }
            return true;
        }
    }

    static class Notice extends DataForm implements HomeView {

        Component frame;

        Notice() {
            super("Notice");
            setButtonsAtTop(true);
        }

        @Override
        public Component getContent() {
            return frame;
        }

        @Override
        protected void buildFields() {
            add(frame);
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            ok.setVisible(false);
            cancel.asPrimary();
            cancel.setText("Close");
        }

        @Override
        protected HasComponents createLayout() {
            return new Div();
        }

        @Override
        protected boolean process() {
            return false;
        }

        @Override
        protected void cancel() {
            super.cancel();
            close();
            ((Application)getApplication()).executeLogin();
        }
    }

    private static class HomeNotice extends Notice {

        public HomeNotice(TextContent textContent) {
            frame = new IFrame(textContent);
        }

        public HomeNotice(MediaFile mediaFile) {
            frame = new Image(mediaFile);
        }
    }

    public void addContent(long fileId, AbstractContentGenerator content) {
        dynamicContent.put(fileId, content);
    }

    public void addMultiContent(long fileId, AbstractContentGenerator content) {
        multiContent.put(fileId, new WeakReference<>(content));
    }

    private com.storedobject.sms.QuickSender smsSender;
    private com.storedobject.mail.QuickSender mailSender;

    public com.storedobject.sms.QuickSender getSMSSender() {
        if(smsSender == null) {
            String sender = ApplicationServer.getGlobalProperty("application.quick.sms", "");
            if(!sender.isBlank()) {
                try {
                    smsSender = (QuickSender) JavaClassLoader.getLogic(sender).getDeclaredConstructor().newInstance();
                } catch(Throwable e) {
                    ApplicationServer.log(Application.get(), e);
                }
            }
        }
        return smsSender;
    }

    public com.storedobject.mail.QuickSender getMailSender() {
        if(mailSender == null) {
            String sender = ApplicationServer.getGlobalProperty("application.quick.mail", "");
            if(!sender.isBlank()) {
                try {
                    mailSender = (com.storedobject.mail.QuickSender) JavaClassLoader.getLogic(sender).
                            getDeclaredConstructor().newInstance();
                } catch(Throwable e) {
                    ApplicationServer.log(Application.get(), e);
                }
            }
        }
        return mailSender;
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
        this.timer = new Timer();
        long t;
        try {
            t = Long.parseLong(ApplicationServer.getGlobalProperty("application.refresh.timer", "600"));
        } catch(Throwable ignored) {
            t = 600;
        }
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
