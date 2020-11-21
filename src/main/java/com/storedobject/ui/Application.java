package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.util.AbstractContentGenerator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.progressbar.ProgressBar;

import java.util.Random;

public class Application extends com.storedobject.vaadin.Application implements Device, RunningLogic {

    public Application() {
        this(null);
    }

    public Application(ApplicationLayout applicationLayout) {
        this(applicationLayout, false);
    }

    public Application(ApplicationLayout applicationLayout, boolean singleLogicMode) {
    }

    @Override
    protected final ApplicationLayout createLayout() {
        return null;
    }

    public void setSingleLogicMode(boolean singleLogicMode) {
    }

    public void execute(Logic logic) {
    }

    public static Application get() {
        return (Application) com.storedobject.vaadin.Application.get();
    }

    public boolean isBiometricAvailable() {
        return false;
    }

    public boolean isBiometricRegistered() {
        return false;
    }

    public Id getBiometricDeviceId() {
        return null;
    }

    public TransactionManager getTransactionManager() {
        return null;
    }

    @Override
    public void setRunningLogic(Logic logic) {
    }

    @Override
    public Logic getRunningLogic() {
        return null;
    }

    public String getLogicTitle(String defaultTitle) {
        return null;
    }

    @Override
    public void setServer(ApplicationServer server) {
    }

    @Override
    public ApplicationServer getServer() {
        return null;
    }

    @Override
    public String getDevicePackageTag() {
        return null;
    }

    public static String getPackageTag() {
        return null;
    }

    public String getDateFormat() {
        return null;
    }

    @Override
    public MessageViewer getMessageViewer() {
        return null;
    }

    @Override
    public String getDeviceType() {
        return null;
    }

    public final String getVersion() {
        return null;
    }

    @Override
    public String getDriverIdentifier() {
        return null;
    }

    @Override
    public BrowserDeviceLayout getDeviceLayout() {
        return null;
    }

    @Override
    public void setDeviceLayout(DeviceLayout layout) {
    }

    public void loadStyles(String textContentName) {
    }

    public void logout() {
    }

    public boolean isMenuOpened() {
        return new Random().nextBoolean();
    }

    public void openMenu() {
    }

    public void closeMenu() {
    }

    public void view(MediaFile mediaFile) {
    }

    public void view(String caption, MediaFile mediaFile) {
    }

    public void view(String caption, Id objectId) {
    }

    public void view(Id objectId) {
    }

    public void view() {
    }

    public void view(String caption, StoredObject object) {
    }

    @Override
    public void view(String caption, ContentProducer producer) {
    }

    @Override
    public void download(ContentProducer producer) {
    }

    public String addResource(ContentProducer producer) {
        return null;
    }

    public Component getAlertButton() {
        return null;
    }

    @Override
    public void alert(LoginMessage message) {
    }

    @Override
    public void alert(String alert) {
    }

    @Override
    public void alert(String caption, String alert) {
    }

    public void alert(String caption, String alert, String icon) {
    }

    public StyledBuilder getAlert() {
        return null;
    }

    public StyledBuilder getAlert(Object alertHandler) {
        return null;
    }

    public StyledBuilder getAlert(Object alertHandler, Id reference) {
        return null;
    }

    public StyledBuilder getAlert(Id reference) {
        return null;
    }

    @Override
    public final void login() {
    }

    public BusyIndicator getProgressBar(boolean indeterminate) {
        return null;
    }

    public BusyIndicator getBusyIndicator() {
        return new BusyIndicator((this));
    }

    void drawMenu(com.storedobject.vaadin.Application application) {
    }

    public class BusyIndicator extends ProgressBar {

        protected BusyIndicator(Application application) {
        }

        public ELabel getLabel() {
            return null;
        }

        public void setCaption(String caption) {
        }

        @Override
        public void setVisible(boolean visible) {
            view();
        }

        @Override
        public void setValue(double value) {
        }

        public void close() {
        }
    }

    public void addContent(long fileId, AbstractContentGenerator content) {
    }

    public void addMultiContent(long fileId, AbstractContentGenerator content) {
    }
}
