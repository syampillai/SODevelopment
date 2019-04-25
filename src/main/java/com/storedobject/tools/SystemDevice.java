package com.storedobject.tools;

import java.util.Locale;

import com.storedobject.core.ApplicationServer;
import com.storedobject.core.ContentProducer;
import com.storedobject.core.Device;
import com.storedobject.core.DeviceLayout;
import com.storedobject.core.MessageViewer;

public abstract class SystemDevice implements Device {

	protected ApplicationServer server;

	public SystemDevice(String link) {
	}
	
	@Override
	public void setServer(ApplicationServer server) {
		this.server = server;
		getMessageViewer();
	}

	@Override
	public ApplicationServer getServer() {
		return server;
	}

	@Override
	public int getDeviceHeight() {
		return 0;
	}

	@Override
	public int getDeviceWidth() {
		return 0;
	}

	protected void setIPAddress(String ipaddress) {
	}

	@Override
	public String getIPAddress() {
		return null;
	}
	
	protected abstract String getIdentifierTag();

	@Override
	public String getIdentifier() {
		return null;
	}
	
	protected abstract String getPackageName();

	@Override
	public String getDriverIdentifier() {
		return null;
	}

	@Override
	public int getMajorVersion() {
		return 1;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public DeviceLayout getDeviceLayout() {
		return null;
	}

	@Override
	public void setDeviceLayout(DeviceLayout layout) {
	}

	@Override
	public void setLocale(Locale locale) {
	}

	@Override
	public void view(String caption, ContentProducer producer) {
	}

	@Override
	public void showNotification(String text) {
	}

	@Override
	public void showNotification(String caption, String text) {
	}

	@Override
	public void showNotification(Throwable error) {
	}

	@Override
	public void showNotification(String caption, Throwable error) {
	}

	@Override
	public Class<?> getDefaultLogicClass() {
		return null;
	}
	
	@Override
	public String getDevicePackageTag() {
		return null;
	}
	
	@Override
	public MessageViewer getMessageViewer() {
		return null;
	}
}
