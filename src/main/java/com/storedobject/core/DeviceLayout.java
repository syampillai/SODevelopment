package com.storedobject.core;

import com.storedobject.core.annotation.Column;
import com.storedobject.pdf.PDFImage;

public abstract class DeviceLayout extends StoredObject {

    public DeviceLayout() {
    }

    public static void columns(Columns columns) {
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

    @Column(required = false)
    public int getMaximumDeviceWidth() {
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

    public void setName(String name) {
    }

    public String getName() {
    	return null;
    }

    public void setProductLogoName(String productLogoName) {
    }

    public String getProductLogoName() {
    	return null;
    }

    public void setCustomerLogoName(String customerLogoName) {
    }

    public String getCustomerLogoName() {
    	return null;
    }

    public void setActive(boolean active) {
    }

    public boolean getActive() {
        return false;
    }
    
    public int getMenuStyle(SystemUser su) {
    	return 0;
    }

    public void setMenuStyle(int menuStyle) {
    }

    public int getMenuStyle() {
        return 0;
    }

    public static String[] getMenuStyleValues() {
        return null;
    }

    public static String getMenuStyleValue(int value) {
    	return null;
    }

    public String getMenuStyleValue() {
        return null;
    }

    public int getMenuWidth() {
        return 0;
    }
    
    public int getMenuHeight() {
        return 0;
    }
    
    public int getMenuWidth(SystemUser su) {
        return 0;
    }
    
    public int getMenuHeight(SystemUser su) {
        return 0;
    }
    
    public int getMenuWidth(int menuStyle) {
        return 0;
    }
    
    public int getMenuHeight(int menuStyle) {
        return 0;
    }
    
    public void setImageMenuHeight(int imageMenuHeight) {
    }

    public int getImageMenuHeight() {
        return 0;
    }

    public void setTextMenuWidth(int textMenuWidth) {
    }

    public int getTextMenuWidth() {
        return 0;
    }

    public void setImageMenuWidth(int imageMenuWidth) {
    }

    public int getImageMenuWidth() {
        return 0;
    }
    
    public void setImageMenuScale(int imageMenuScale) {
    }

    public int getImageMenuScale() {
        return 0;
    }
    
    public int getScaledImageMenuWidth() {
    	return 0;
    }
    
    public int getScaledImageMenuHeight() {
    	return 0;
    }
    
    public void setTheme(int theme) {
    }

    public int getTheme() {
        return 0;
    }

    public static String[] getThemeValues() {
        return null;
    }

    public static String getThemeValue(int value) {
        return null;
    }

    public String getThemeValue() {
        return null;
    }

    public static String getThemeName(String baseName) {
    	return null;
    }
    
    public String getThemeName() {
        return null;
    }

    public static int identifyDevice(String name) {
    	return 0;
    }

    protected static DeviceLayout getLayout(Device device, int deviceId) {
    	return null;
    }

    public abstract int getSupportedDevices();

    public PDFImage getLogo(TransactionManager tm) {
    	return null;
    }

    public PDFImage getProductLogo() {
    	return null;
    }
    
    public void setHTMLColors() {
    }
    
    public static void setHTMLColors(String themeName) {
    }
}
