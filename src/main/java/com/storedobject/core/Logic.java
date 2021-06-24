package com.storedobject.core;

import com.storedobject.common.Executable;

public final class Logic extends StoredObject implements DisplayOrder {

	public Logic(String className, String title) {
	}

	public Logic() {
		this(null, null);
	}

	public static void columns(Columns columns) {
	}

	public int getApprovalCount() {
		return 0;
	}

	public void setApprovalCount() {
	}

	public String getClassName() {
		return null;
	}

	public void setClassName(String className) {
	}

	public String getTitle() {
		return null;
	}

	public void setTitle(String title) {
	}

	public void setIconImageName(String iconImageName) {
	}

	public String getIconImageName() {
		return null;
	}

	public void setDisplayOrder(int displayOrder) {
	}

	public int getDisplayOrder() {
		return 0;
	}

	public void setDevice(int device) {
	}

	public int getDevice() {
		return 0;
	}

	public static String[] getDeviceBitValues() {
		return null;
	}

	public static String getDeviceValue(int value) {
		return null;
	}

	public String getDeviceValue() {
		return null;
	}

	public void setMinimumDeviceHeight(int minimumDeviceHeight) {
	}

	public int getMinimumDeviceHeight() {
		return 0;
	}

	public void setMaximumDeviceHeight(int maximumDeviceHeight) {
	}

	public int getMaximumDeviceHeight() {
		return 0;
	}

	public void setMinimumDeviceWidth(int minimumDeviceWidth) {
	}

	public int getMinimumDeviceWidth() {
		return 0;
	}

	public void setMaximumDeviceWidth(int maximumDeviceWidth) {
	}

	public int getMaximumDeviceWidth() {
		return 0;
	}

	public Executable getExecutable() {
		return null;
	}

	public void setExecutable(Executable executable) {
	}

	public void setAutoExecute(boolean autoExecute) {
	}

	public boolean getSingleInstance() {
		return false;
	}

	public void setSingleInstance(boolean singleInstance) {
	}

	public boolean getAutoExecute() {
		return false;
	}

	public static Logic getRunningLogic(Device device) {
		return null;
	}

	public static String getRunningLogicTitle(Device device, String defaultTitle) {
		return null;
	}

	public boolean isApplicableTo(String deviceName) {
		return false;
	}

	public static Logic get(String title) {
		return null;
	}

	public static ObjectIterator<Logic> list(String title) {
		return null;
	}
}