package com.storedobject.ui;

import com.storedobject.common.ArrayListSet;
import com.storedobject.common.Executable;
import com.storedobject.core.*;
import com.storedobject.ui.tools.BiometricButton;
import com.storedobject.ui.tools.LoginNameField;
import com.storedobject.ui.util.ApplicationFrame;
import com.storedobject.ui.util.*;
import com.storedobject.vaadin.ApplicationMenu;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.*;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.Lumo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Consumer;

public class Application extends com.storedobject.vaadin.Application implements Device, RunningLogic, RequiresApproval {

    private static final String VERSION = "18.0.3";
    private static final String COMPACT_STYLES =
            "--lumo-size-xl: 3rem;\n" +
            "--lumo-size-l: 2.5rem;\n" +
            "--lumo-size-m: 2rem;\n" +
            "--lumo-size-s: 1.75rem;\n" +
            "--lumo-size-xs: 1.5rem;\n" +
            "--lumo-font-size: 1rem;\n" +
            "--lumo-font-size-xxxl: 1.75rem;\n" +
            "--lumo-font-size-xxl: 1.375rem;\n" +
            "--lumo-font-size-xl: 1.125rem;\n" +
            "--lumo-font-size-l: 1rem;\n" +
            "--lumo-font-size-m: 0.875rem;\n" +
            "--lumo-font-size-s: 0.8125rem;\n" +
            "--lumo-font-size-xs: 0.75rem;\n" +
            "--lumo-font-size-xxs: 0.6875rem;\n" +
            "--lumo-line-height-m: 1.4;\n" +
            "--lumo-line-height-s: 1.2;\n" +
            "--lumo-line-height-xs: 1.1;\n" +
            "--lumo-space-xl: 1.875rem;\n" +
            "--lumo-space-l: 1.25rem;\n" +
            "--lumo-space-m: 0.625rem;\n" +
            "--lumo-space-s: 0.3125rem;\n" +
            "--lumo-space-xs: 0.1875rem;";
    private static final String DELETE_COMPACT_STYLES = "--lumo-size-xl\n" +
            "--lumo-size-l\n" +
            "--lumo-size-m\n" +
            "--lumo-size-s\n" +
            "--lumo-size-xs\n" +
            "--lumo-font-size\n" +
            "--lumo-font-size-xxxl\n" +
            "--lumo-font-size-xxl\n" +
            "--lumo-font-size-xl\n" +
            "--lumo-font-size-l\n" +
            "--lumo-font-size-m\n" +
            "--lumo-font-size-s\n" +
            "--lumo-font-size-xs\n" +
            "--lumo-font-size-xxs\n" +
            "--lumo-line-height-m\n" +
            "--lumo-line-height-s\n" +
            "--lumo-line-height-xs\n" +
            "--lumo-space-xl\n" +
            "--lumo-space-l\n" +
            "--lumo-space-m\n" +
            "--lumo-space-s\n" +
            "--lumo-space-xs\n";
    private ApplicationServer server;
    private BrowserDeviceLayout layout = null;
    private Logic runningLogic;
    private final ApplicationLayout mainLayout;
    private boolean singleLogicMode = false;
    private final Hashtable<Long, AbstractContentGenerator> dynamicContent = new Hashtable<>();
    private final Hashtable<Long, WeakReference<AbstractContentGenerator>> multiContent = new Hashtable<>();
    private ObjectViewer objectViewer;
    boolean biometricAvailable = false, biometricRegistered = false;
    Id biometricDeviceId = null;
    private boolean alertsVisible = false;
    private final AlertButton alertButton = new AlertButton(e -> alertClicked());
    private Login login;
    private boolean compactTheme = false;

    public Application() {
        this(new ApplicationFrame());
    }

    public Application(ApplicationLayout applicationLayout) {
        this(applicationLayout, ApplicationServer.getGlobalBooleanProperty("application.logic.single"));
    }

    public Application(ApplicationLayout applicationLayout, boolean singleLogicMode) {
        this.mainLayout = applicationLayout;
        setSingleLogicMode(singleLogicMode);
        login = new Login(this, getMessageViewer());
    }

    @Override
    protected void init(VaadinRequest request) {
        super.init(request);
        VaadinSession vs = VaadinSession.getCurrent();
        vs.addRequestHandler(new DownloadHandler());
        vs.setErrorHandler(e -> {
            Throwable error = e.getThrowable();
            if(error instanceof UploadException) {
                return;
            }
            log(error);
            getUI().access(() -> error("An error has occurred, please contact Technical Support!"));
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
    protected Login getLogin() {
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

    @Override
    public void attached() {
        super.attached();
        setCompactTheme(ApplicationServer.getGlobalBooleanProperty("application.theme.compact"));
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
                    s.set(line.substring(0, p).trim(), line.substring(p + 1).trim());
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

    public void execute(Logic logic) {
        if(singleLogicMode) {
            execute(logic, true);
        } else {
            logic.execute();
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
        String exit = layout == null ? "" : layout.getExitSite().trim();
        if(server == null) {
            return;
        }
        ApplicationServer as = server;
        server = null;
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
        return server.getTransactionManager();
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

    public String getLogicTitle(String defaultTitle) {
        return runningLogic == null ? defaultTitle : runningLogic.getTitle();
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
    public String getDevicePackageTag() {
        return getPackageTag();
    }

    public static String getPackageTag() {
        return "ui";
    }

    public String getDateFormat() {
        return server.getDateFormat();
    }

    @Override
    public MessageViewer getMessageViewer() {
        return null;
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
        view(null, mediaFile);
    }

    public void view(String caption, MediaFile mediaFile) {
        if(mediaFile == null) {
            return;
        }
        if(caption == null || caption.isEmpty()) {
            caption = "Media";
        }
        if(mediaFile.isImage()) {
            View.createCloseableView(new Image(mediaFile), caption).execute();
            return;
        }
        if(mediaFile.isAudio()) {
            View.createCloseableView(new Audio(mediaFile), caption).execute();
            return;
        }
        if(mediaFile.isVideo()) {
            View.createCloseableView(new Video(mediaFile), caption).execute();
            return;
        }
        if(mediaFile.getMimeType().equals(AbstractContentGenerator.PDF_CONTENT)) {
            View.createCloseableView(new PDFViewer("media/" + mediaFile.getFileName()), caption).execute();
        }
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

    @Override
    public void view(String caption, ContentProducer producer) {
        new ContentGenerator(this, producer, caption).kick();
    }

    @Override
    public void download(ContentProducer producer) {
        new ContentGenerator(this, producer).kick();
    }

    public String addResource(ContentProducer producer) {
        MultiContentGenerator mcg = new MultiContentGenerator(this, producer);
        return "so" + mcg.getId() + "." + producer.getFileExtension();
    }

    public Component getAlertButton() {
        return alertButton;
    }

    @Override
    public void alert(LoginMessage message) {
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
                new HomeHTMLView(this, home).execute();
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

    protected void executeLogin() {
        String loginLogic = ApplicationServer.getGlobalProperty("application.logic.login", "");
        if(loginLogic.isEmpty()) {
            new LoginForm().execute();
            return;
        }
        new Logic(loginLogic, "Login").execute();
    }

    private void loginDone() {
        SystemUser su = server.getTransactionManager().getUser();
        boolean checkPassword = !su.isAdmin();
        if(checkPassword && !"demo".equals(ApplicationServer.runMode())) {
            checkPassword = !"guest".equals(su.getLogin());
        }
        Login l = login;
        login = null;
        if(checkPassword) {
            boolean expired = su.isPasswordExpired();
            if(l.isWelcomePassword() || expired) {
                new ChangePassword(expired).execute();
                return;
            }
        }
        selectEntity();
    }

    private void startApp() {
        startPolling(this);
        BusyIndicator bi = getBusyIndicator();
        getUI().access(() -> {
            loggedin();
            bi.close();
            stopPolling(Application.this);
            mainLayout.initialized();
            if(!singleLogicMode) {
                setSingleLogicMode((getTransactionManager().getUser().getPreferences() & 2) == 2);
            }
        });
    }

    @Override
    protected void viewDetached(View view) {
        if(mainLayout != null) {
            mainLayout.viewDetached(view);
        }
        if(login != null && getActiveViewCount() == 0 && login.isLoggedIn()) {
            loginDone();
        }
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
        if(frameMenu instanceof com.storedobject.core.ApplicationMenu) {
            autos = as.populateMenu((com.storedobject.core.ApplicationMenu) frameMenu, null);
        } else {
            Application.Menu am;
            autos = as.populateMenu(am = new Application.Menu(mainLayout.getMenu()), null);
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
        //noinspection ResultOfMethodCallIgnored
        autos.forEach(as::execute);
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
        return a;
    }

    private Alert createAlert(Object alertHandler, Id reference) {
        if(alertHandler == null && reference == null) {
            return createAlert(null, "", null);
        }
        return new Alert("", a -> new AlertProcessor(null, a, alertHandler, reference).execute());
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

    private static class LoginMessageAlert extends Alert {

        private final LoginMessage message;

        private LoginMessageAlert(LoginMessage message) {
            super("", a -> new LoginMessageProcessor(message, a));
            this.message = message;
            String c = null;
            switch(message.getPriority()) {
                case 0: // Normal
                    c = "white";
                    break;
                case 1: // Low
                    c = "green";
                    break;
                case 2: // Medium
                    c = "orange";
                    break;
                case 3: // High
                    c = "red";
                    break;
            }
            appendHTML("<span style=\"color:").appendHTML(c).appendHTML("\">");
            appendHTML(message.getMessage().replaceAll("\n", "<BR/>")).appendHTML("</span>");
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
            delete = new ConfirmButton("Delete", VaadinIcon.MINUS_CIRCLE, e -> { alert.delete(); close(); });
            buttonPanel.add(ok, delete, cancel);
        }

        public void handleAlert(StoredObject reference) {
            alert.delete();
            close();
            if(alertHandler == null) {
                return;
            }
            if(alertHandler instanceof AlertHandler) {
                ((AlertHandler)alertHandler).handleAlert(reference);
            } else if(alertHandler instanceof Executable) {
                ((Executable)alertHandler).execute();
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
                if(alertHandler instanceof AlertHandler) {
                    String s = ((AlertHandler) alertHandler).getAlertCaption();
                    ok.setText(s == null ? "Process" : s);
                    s = ((AlertHandler) alertHandler).getAlertIcon();
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
            trackValueChange(priority);
            execute();
        }

        @Override
        protected void buildFields() {
            super.buildFields();
            addField(priority);
            priority.setValue(message.getPriority());
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            buttonPanel.removeAll();
            changePriority = new Button("Change Priority", "", this);
            buttonPanel.add(ok, changePriority, delete, cancel);
            changePriority.setEnabled(false);
        }

        @Override
        public void clicked(Component c) {
            if(c == changePriority) {
                message.setPriority(priority.getValue());
                boolean changed = transact(message::save);
                message.reload();
                priority.setValue(message.getPriority());
                changePriority.setEnabled(false);
                if(changed) {
                    alert.delete();
                    ((Application)getApplication()).registerAlert(alert);
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
        protected boolean process() {
            return false;
        }
    }

    private void selectEntity() {
        ArrayList<SystemEntity> entities = new ArrayList<>();
        SystemUser su = getTransactionManager().getUser();
        if(su.isSS()) {
            StoredObject.list(SystemEntity.class, null,"Id").collectAll(entities);
        } else {
            su.listLinks(SystemEntity.class, null, "Id").collectAll(entities);
        }
        if(entities.size() < 2) {
            setDate(server.getDate());
            startApp();
            return;
        }
        new EntitySelector(entities, su.getName()).execute();
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

    private static class LoginForm extends DataForm implements HomeView {

        private final Animation[] animation = { Animation.SHAKE, Animation.FLASH };
        private int animationIndex = 0;
        private LoginNameField loginField;
        private PasswordField passwordField;
        private CRAMField cramField;
        private BiometricButton biometricButton;
        private SystemUser user;
        private Div imageHolder = new Div();
        private Registration registration;

        public LoginForm() {
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
            Checkbox remember = new Checkbox("Remember my login name");
            remember.setTabIndex(-1);
            addField(loginField = new LoginNameField(remember));
            setRequired(loginField, "Login can not be empty");
            addField(passwordField = new PasswordField("Password"));
            addField(cramField = new CRAMField());
            add(remember);
            loginField.addValueChangeListener(e -> setUser());
        }

        @Override
        protected void buildButtons() {
            super.buildButtons();
            buttonPanel.add(biometricButton = new BiometricButton(this::biometricLogin, getA().getLogin()));
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
            return new Window(new WindowDecorator(this, speaker()), component);
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

        private void setUser() {
            String u = loginField.getValue();
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
            biometricButton.setUser(user);
        }

        private Application getA() {
            return getApplication();
        }

        private void shakePW() {
            animation[animationIndex % animation.length].animate(passwordField);
            animationIndex++;
        }

        @Override
        protected boolean process() {
            Login login = getA().getLogin();
            setBioStatus();
            String u = user == null ? loginField.getValue().trim().toLowerCase() : user.getLogin();
            try {
                boolean wrongCram = !cramField.verified();
                if(wrongCram || user == null || !login.login(u, passwordField.getValue().toCharArray(), true)) {
                    if(login.canRetry()) {
                        speak("Please check the " + (wrongCram ? "captcha" : "password"), false);
                        if(wrongCram) {
                            cramField.shake();
                        } else {
                            shakePW();
                        }
                        return false;
                    }
                    if(login.isBlocked()) {
                        cancel();
                        speak("System not available now. Please try later.", true);
                        return true;
                    }
                    cancel();
                    return true;
                }
                loginField.save();
            } catch(Throwable error) {
                if(!login.canRetry()) {
                    cancel();
                }
                error(error);
                return !login.canRetry();
            }
            return true;
        }

        private void setBioStatus() {
            getA().biometricRegistered = biometricButton.isAvailable() && biometricButton.isRegistered();
            getA().biometricAvailable = biometricButton.isAvailable();
            WebBiometric biometric = biometricButton.getBiometric();
            getA().biometricDeviceId = biometric == null ? null : biometric.getId();
        }

        private void biometricLogin(BiometricButton biometricButton) {
            if(biometricButton == this.biometricButton) {
                setBioStatus();
                loginField.save();
                close();
            } else {
                cancel();
            }
        }

        private void speak(String message, boolean warn) {
            speak(message);
            if(warn) {
                warning(message);
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
            selectEntity();
            return true;
        }
    }

    private static SpeakerButton speaker() {
        SpeakerButton sb = new SpeakerButton();
        return sb.withBox();
    }

    private class EntitySelector extends DataForm implements HomeView {

        private final ObjectComboField<SystemEntity> entities;

        public EntitySelector(List<SystemEntity> entities, String name) {
            super(name);
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
            startApp();
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
}
