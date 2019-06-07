package com.storedobject.ui;

import com.storedobject.core.*;
import com.storedobject.ui.util.AbstractContentGenerator;
import com.storedobject.vaadin.ApplicationLayout;
import com.storedobject.vaadin.Icon;
import com.vaadin.flow.component.progressbar.ProgressBar;

public class Application extends com.storedobject.vaadin.Application implements Device, RunningLogic {

    @Override
    protected ApplicationLayout createLayout() {
        return null;
    }

    public static Application get() {
        return (Application) com.storedobject.vaadin.Application.get();
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

    @Override
    public void alert(LoginMessage message) {
    }

    @Override
    public void alert(String alert) {
    }

    @Override
    public void alert(String caption, String alert) {
    }

    public void alert(String alert, Icon icon) {
    }

    public void alert(String caption, String alert, Icon icon) {
    }

    @Override
    public final void login() {
    }

    public boolean login(String user, String password) {
        return true;
    }

    public BusyIndicator getProgressBar(boolean indeterminate) {
        return null;
    }

    public BusyIndicator getBusyIndicator() {
        return null;
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
