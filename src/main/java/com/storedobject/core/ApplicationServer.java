package com.storedobject.core;

import java.util.List;
import java.util.Properties;

import com.storedobject.pdf.PDFImage;

public final class ApplicationServer {
	
	public ApplicationServer(Device device, String link) {
		this(device, link, true);
	}

	public ApplicationServer(Device device, String link, boolean downloadPackages) {
	}
	
	public ApplicationServer(Device device, String link, boolean downloadPackages, boolean checkPackages) {
	}
	
	public static boolean reloadRequired() {
		return false;
	}

	protected static void revalidateDevice(DeviceLayout layout) {
	}

	public Device getDevice() {
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

	public Object execute(String logicName, boolean execute) {
		return null;
	}
	
	public Object execute(Class<?> logicClass, boolean execute) {
		return null;
	}
	
	public boolean login(String user, String password) {
		return true;
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

	public static void log(Throwable e) {
	}

	public static void log(String message, Throwable e) {
	}

	public static void log(String message) {
	}

	public static void log(Device d, Throwable e) {
	}

	public static void log(Device d, String message, Throwable e) {
	}

	public static void log(Device d, String message) {
	}

	public TransactionManager getTransactionManager() {
		return null;
	}

	public String getDateFormat() {
		return null;
	}

	public void view(ContentProducer producer) {
	}

	public static String runMode() {
		return null;
	}

	public synchronized static void initialize(String propertiesFileName) {
	}

	public synchronized static void initialize(String propertiesFileName, Properties variables) {
	}

	public synchronized static void initialize(String propertiesFileName, boolean downloadPackages) {
	}

	public synchronized static void initialize(String propertiesFileName, boolean downloadPackages, boolean checkPackages) {
	}

	public synchronized static void initialize(String propertiesFileName, Properties variables, boolean downloadPackages) {
	}
	
	public synchronized static void initialize(String propertiesFileName, Properties variables, boolean downloadPackages, boolean checkPackages) {
	}

	public static String createLogicName(String deviceTag, Class<?> objectClass, String tag, boolean skipTools) {
		return null;
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
		return null;
	}

	public static String getIconName(String imageName) {
		return null;
	}
}