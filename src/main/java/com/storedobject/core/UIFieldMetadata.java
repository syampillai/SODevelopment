package com.storedobject.core;

import com.storedobject.core.annotation.Column;

import java.lang.reflect.Method;

public class UIFieldMetadata extends StoredObject implements Detail {

	private Column metadata;

	public UIFieldMetadata() {
		metadata = null;
	}

	public UIFieldMetadata(String fieldName, Method getMethod) {
	}

	public static void columns(Columns columns) {
	}

	public void setGetMethod(Method getMethod) {
	}

	public static String browseOrder() {
		return null;
	}

	public boolean isFieldOrderBuiltIn() {
		return false;
	}

	public void setFieldName(String fieldName) {
	}

	public String getFieldName() {
		return null;
	}

	public void setCaption(String caption) {
	}

	public String getCaption() {
		return null;
	}

	public void setFieldOrder(int fieldOrder) {
	}

	public int getFieldOrder() {
		return 0;
	}

	public void setType(int type) {
	}

	public int getType() {
		return 0;
	}

	public static String[] getTypeValues() {
		return null;
	}

	public static String getTypeValue(int value) {
		return null;
	}

	public String getTypeValue() {
		return null;
	}

	public void setParameters(String parameters) {
	}

	public String getParameters() {
		return null;
	}

	public void setEditability(boolean editability) {
	}

	public boolean getEditability() {
		return false;
	}

	public void setVisibility(boolean visibility) {
	}

	public boolean getVisibility() {
		return false;
	}

	public void setWidth(int width) {
	}

	public int getWidth() {
		return 0;
	}

	@Override
	public boolean isDetailOf(Class<? extends StoredObject> masterClass) {
		return false;
	}

	public int getParameterAsActions(int defaultValue, boolean developer) {
		return 0;
	}

	public Class<? extends StoredObject> getParameterAsClass(Class<? extends StoredObject> defaultClass) {
		return null;
	}

	public int getIntParameter(int defaultValue, int index) {
		return 0;
	}

	public String getParameter(int index) {
		return null;
	}

	public String getParameter(String defaultValue, int index) {
		return null;
	}
	
	public MeasurementUnit getUnit(Class<? extends Quantity> quantityClass) {
		return null;
	}

	public void setSetAllowed(boolean setAllowed) {
	}

	public boolean isSetAllowed() {
		return true;
	}

	public boolean isMultiline() {
		return false;
	}

	public int getColumnSpan() {
		return 1;
	}
	public boolean isAddAllowed() {
		return false;
	}

	public boolean isAny() {
		return false;
	}

	public boolean isTextSearch() {
		return false;
	}

	public boolean isMinutes() {
		return false;
	}

	public boolean isPopupText() {
		return false;
	}

	public boolean isCurrency() {
		return false;
	}

	public boolean isCountry() {
		return false;
	}

	public boolean isPhone() {
		return false;
	}

	public boolean isEmail() {
		return false;
	}

	public boolean isAddress() {
		return false;
	}

	public boolean isTimeZone() {
		return false;
	}

	public boolean isDays() {
		return false;
	}

	public boolean isSerial() {
		return false;
	}

	public boolean isRequired() {
		return false;
	}

	public boolean isCode() {
		return false;
	}

	public boolean isNegativeAllowed() {
		return false;
	}

	public String getStyle() {
		return null;
	}

	public boolean isStyle(String style) {
		return false;
	}
	
	public String getDisplayName() {
		return null;
	}

	public void setDisplayName(String displayName) {
	}

    public boolean isSecret() {
		return false;
    }

	public Column getMetadata() {
		return metadata;
	}

	public String getTabName() {
		return metadata == null ? null : metadata.tab();
	}
}