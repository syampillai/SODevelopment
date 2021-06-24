package com.storedobject.report;

import com.storedobject.common.MethodInvoker;
import com.storedobject.core.ClassAttribute;
import com.storedobject.core.StoredObject;

public class ReportColumn<T extends StoredObject> extends ObjectData<T> {

	public ReportColumn(ClassAttribute<T> ca, String name) {
		this(ca, name, null);
	}
	
	public ReportColumn(ClassAttribute<T> ca, String name, MethodInvoker methodInvoker) {
		super(ca, name, methodInvoker);
	}

	public void setValue(T object) {
	}

	public boolean valueExists() {
		return false;
	}

	public Object removeValue() {
		return null;
	}

	public int getWidth() {
		return 0;
	}
	
	public void setWidht(int width) {
	}
}
