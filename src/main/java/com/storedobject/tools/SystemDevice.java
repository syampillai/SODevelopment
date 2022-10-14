package com.storedobject.tools;

import com.storedobject.common.SOException;
import com.storedobject.common.annotation.Module;
import com.storedobject.core.*;

import java.util.Locale;
import java.util.function.Consumer;

public abstract class SystemDevice implements Device {

	protected ApplicationServer server;
	private String ipaddress = "Unknown";
	private DeviceLayout deviceLayout;
	protected final Login login;

	public SystemDevice(String link) {
		init(link);
		login = new Login(this);
	}

	void init(String link) {
		if(server == null) {
			new ApplicationServer(this, link);
		}
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
	public SystemEntity getDefaultEntity(SystemUser user) {
		return StoredObject.list(SystemEntity.class).findFirst();
	}

	@Override
	public boolean loggedIn(Login login) {
		return login == this.login;
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
		this.ipaddress = ipaddress;
	}

	@Override
	public String getIPAddress() {
		return ipaddress;
	}

	protected abstract String getIdentifierTag();

	@Override
	public String getIdentifier() {
		return getIdentifierTag() + " " + getMajorVersion() + "." + getMinorVersion();
	}

	protected abstract String getPackageName();

	@Override
	public String getDriverIdentifier() {
		Module m = JavaClass.getPackage(getPackageName()).getAnnotation(Module.class);
		return m == null ? "System" : (m.version() + "b" + m.build());
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
		if(deviceLayout == null) {
			deviceLayout = StoredObject.list(BrowserDeviceLayout.class, "Active").findFirst();
		}
		return deviceLayout;
	}

	@Override
	public void setDeviceLayout(DeviceLayout layout) {
	}

	@Override
	public void setLocale(Locale locale) {
	}

	@Override
	public void view(String caption, ContentProducer producer, Consumer<Long> informMe, boolean windowMode) {
	}

	@Override
	public void showNotification(String text) {
		showNotification(null, text);
	}

	@Override
	public void showNotification(String caption, String text) {
		notification(caption, text,false);
	}

	@Override
	public void showNotification(Throwable error) {
		showNotification(null, error);
	}

	@Override
	public void showNotification(String caption, Throwable error) {
		notification(caption, "\nError: " + StringUtility.toString(error), true);
	}

	private void notification(String caption, Object message, boolean error) {
		if(error) {
			notify(caption, message);
		}
		notify(caption, StringUtility.toString(message), error);
	}

	private void notify(String caption, Object message) {
		if((message instanceof Throwable throwable) && !(message instanceof SOException)) {
			ApplicationServer.log(this, caption, throwable);
		} else {
			ApplicationServer.log(this, caption + ":\n" + StringUtility.toString(message));
		}
	}

	protected void notify(String caption, String message, boolean error) {
		if(!error) {
			notify(caption, message);
		}
	}

	@Override
	public MessageViewer getMessageViewer() {
		return new MessageViewer() {
			@Override
			public TransactionManager getTransactionManager() {
				return null;
			}

			@Override
			public void alert(String message) {
			}

			@Override
			public void message(LoginMessage message) {
			}
		};
	}
}
