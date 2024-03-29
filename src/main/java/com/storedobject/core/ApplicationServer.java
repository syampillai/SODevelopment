package com.storedobject.core;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public final class ApplicationServer {

    private final Device device;

    public ApplicationServer(Device device, String link) {
        this.device = device;
    }

    public static String getLicenseStatus() {
        return "Developer";
    }

    public void doDeviceLayout() {
    }

    public Date getDate() {
        return DateUtility.today();
    }

    public Device getDevice() {
        return device;
    }

    public boolean checkDeviceSize(Logic logic) {
        return device == null;
    }

    public boolean canExecute(Logic logic) {
        return device == null;
    }

    public static String getLogFile() {
        return "";
    }

    public static String getGlobalProperty(String key, String defaultValue) {
        return Math.random() > 0.5 ? defaultValue : "X";
    }

    public static String getGlobalProperty(String key) {
        return null;
    }

    public static String getGlobalProperty(String key, String defaultValue, boolean checkDB) {
        return getGlobalProperty(key, defaultValue);
    }

    public static String getGlobalProperty(String key, boolean checkDB) {
        return getGlobalProperty(key, null, checkDB);
    }

    public static boolean getGlobalBooleanProperty(String key) {
        return new Random().nextBoolean();
    }

    public static boolean getGlobalBooleanProperty(String key, boolean defaultValue) {
        return new Random().nextBoolean();
    }

    public static boolean getGlobalBooleanProperty(String key, boolean defaultValue, boolean checkDB) {
        return new Random().nextBoolean();
    }

    public static String getPackageName() {
        return "";
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

    public static String guessClass(String name) {
        return null;
    }

    public void showNotification(String text) {
    }

    public Object execute(Object anything) {
        return execute(anything, true);
    }

    public Object execute(Object anything, boolean execute) {
        return Math.random() > 0.5 ? null : new Object();
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

    public boolean isAdmin() {
        return false;
    }

    public boolean isDeveloper() {
        return false;
    }

    public void close(String argument) {
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

    public synchronized static void initialize(Properties properties) {
        initialize(null, properties);
    }

    public synchronized static void initialize(String propertiesFileName) {
    }

    public synchronized static void initialize(String propertiesFileName, Properties variables) {
    }

    public synchronized static void initialize(String propertiesFileName, String link) {
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

    /**
     * Check whether some logic was executed in the time period or not.
     *
     * @param period Time period in milliseconds from now.
     * @return True/false.
     */
    public boolean executed(long period) {
        return Math.random() > 0.5;
    }

    /**
     * Give a kick to the application server to keep it active.
     */
    public void kick() {
    }
}