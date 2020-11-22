package com.storedobject.core;

import com.storedobject.pdf.PDFImage;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class ApplicationServer {

    public ApplicationServer(Device device, String link) {
    }

    public void doDeviceLayout() {
    }

    protected static void revalidateDevice(DeviceLayout layout) {
    }

    public Date getDate() {
        return DateUtility.today();
    }

    public Device getDevice() {
        return null;
    }

    public static String getLogFile() {
        return null;
    }

    public static String getGlobalProperty(String key, String defaultValue) {
        return null;
    }

    public static String getGlobalProperty(String key) {
        return null;
    }

    public static String getPackageName() {
        return null;
    }

    public static String getPackageId() {
        return null;
    }

    public static String getApplicationName() {
        return null;
    }

    public static String getApplicationSite() {
        return null;
    }

    public static String getImageDirectory() {
        return null;
    }

    public static PDFImage getIconImage(String iconName) {
        return null;
    }

    public static PDFImage getImage(String imageName) {
        return null;
    }

    public static String guessClass(String name) {
        return null;
    }

    public final String getVersion() {
        return null;
    }

    public void showNotification(String text) {
    }

    public Object execute(Logic logic) {
        return null;
    }

    public Object execute(Logic logic, boolean execute) {
        return null;
    }

    public Object execute(String logicName) {
        return null;
    }

    public Object execute(String logicName, boolean execute) {
        return null;
    }

    public Object execute(Class<?> logicClass, boolean execute) {
        return null;
    }

    public boolean login(String user, char[] password, boolean lockIfFailed) {
        return true;
    }

    public boolean login(String user, char[] password) {
        return true;
    }

    public boolean login(WebBiometric biometric, String id, String type, String authenticatorData,
                         String clientDataJSON, String signature, String userHandle, boolean b) {
        return false;
    }

    public boolean isAdmin() {
        return false;
    }

    public boolean isDeveloper() {
        return false;
    }

    public void close(String message) {
    }

    public void close() {
    }

    public static void log(Object anything) {
        log(anything, null);
    }

    public static void log(Object anything, Throwable error) {
        log(null, anything, error);
    }

    public static void log(Device device, Object anything) {
        log(device, anything, null);
    }

    public static void log(Device device, Throwable error) {
        log(device, null, error);
    }

    public static void log(Device device, Object anything, Throwable error) {
    }

    public TransactionManager getTransactionManager() {
        return new TransactionManager(null, null);
    }

    public String getDateFormat() {
        return null;
    }

    public void view(ContentProducer producer) {
    }

    public static String runMode() {
        return "";
    }

    public synchronized static void initialize(String propertiesFileName) {
    }

    public synchronized static void initialize(String propertiesFileName, Properties variables) {
    }

    public static String createLogicName(String deviceTag, Class<?> objectClass, String tag) {
        return null;
    }

    public static String getCopyright() {
        return null;
    }

    public List<DisplayOrder> createLogicList(LogicGroup logicGroup) {
        return null;
    }

    public List<Logic> populateMenu(ApplicationMenu menu, SystemUser menuUser) {
        return new ArrayList<>();
    }
}